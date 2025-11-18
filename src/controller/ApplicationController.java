package controller;

import model.*;
import constant.ApplicationStatus;
import utils.csv.*;
import controller.interfaces.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller class that handles all Application-related logic.
 * Controllers should use this class instead of accessing Application model directly.
 * 
 * Follows Dependency Inversion Principle by depending on CsvHandler interface abstraction
 * and accepting InternshipControllerInterface as dependency instead of concrete class.
 * Implements ApplicationControllerInterface to allow clients to depend on abstraction.
 * 
 * @author NTU SC2002 Group
 * @version 1.0
 * @since 2025-11-16
 */
public class ApplicationController implements ApplicationControllerInterface {
    /** CSV handler for persistence operations */
    private final CsvHandler<Application> csvHandler;
    /** Internship reader for reading internship data (ISP - only needs read operations) */
    private final InternshipReader internshipReader;
    /** Internship writer for saving internship data when applications affect internships */
    private final InternshipWriter internshipWriter;
    
    /**
     * Constructs an ApplicationController with the specified dependencies.
     * Follows Dependency Inversion Principle - depends on abstractions, not concretions.
     * Follows Interface Segregation Principle - depends on InternshipReader and InternshipWriter separately.
     * 
     * @param csvHandler The CSV handler for persistence operations
     * @param internshipReader The internship reader interface for reading internship data
     * @param internshipWriter The internship writer interface for saving internship data
     */
    public ApplicationController(CsvHandler<Application> csvHandler, InternshipReader internshipReader, InternshipWriter internshipWriter) {
        this.csvHandler = csvHandler;
        this.internshipReader = internshipReader;
        this.internshipWriter = internshipWriter;
    }
    
    /**
     * Constructs an ApplicationController with InternshipControllerInterface (for backward compatibility).
     * Extracts reader and writer from the full controller interface.
     * 
     * @param csvHandler The CSV handler for persistence operations
     * @param internshipController The full internship controller interface
     */
    public ApplicationController(CsvHandler<Application> csvHandler, InternshipControllerInterface internshipController) {
        this(csvHandler, internshipController, internshipController);
    }
    
    /**
     * Default constructor for backward compatibility.
     * Uses singleton instances.
     */
    public ApplicationController() {
        InternshipController controller = new InternshipController();
        this.csvHandler = ApplicationCsvHandler.getInstance();
        this.internshipReader = controller;
        this.internshipWriter = controller;
    }
    
    /**
     * Creates an application for a student to a specific internship.
     * Students can only select internships that are visible to them (already filtered by major, level, and visibility).
     * 
     * @param internshipID The ID of the internship to apply to
     * @param student The student submitting the application
     * @param internshipControllerParam Controller to retrieve internship details
     * @return The created Application object
     * @throws IllegalArgumentException if internship not found or student ineligible
     */
    @Override
    public Application createApplication(int internshipID, Student student, InternshipControllerInterface internshipControllerParam) {
        loadApplicationsFromCsv(internshipControllerParam);
        
        Internship internship = internshipReader.findInternship(internshipID);
        Application app = student.submitApplication(internship);
        csvHandler.saveToCsv(app);
        return app;
    }
    
    /**
     * Retrieves all applications for a specific internship.
     * Overloaded method - can use InternshipController or find directly.
     * 
     * @param internshipID The ID of the internship
     * @param internshipController Controller to retrieve internship details
     * @return List of applications for the internship
     * @throws IllegalArgumentException if internship not found
     */
    @Override
    public List<Application> getApplicationsForInternship(int internshipID, InternshipControllerInterface internshipController) {
        Internship internship = internshipReader.findInternship(internshipID);
        return internship.getApplications();
    }
    
    /**
     * Retrieves all applications for an internship (overloaded without controller parameter).
     * Creates a temporary InternshipController internally for MVC compliance.
     * NOTE: Prefer using the overload with InternshipController parameter for better performance.
     * 
     * @param internshipID The ID of the internship
     * @return List of applications for the internship
     * @throws IllegalArgumentException if internship not found
     */
    public List<Application> getApplicationsForInternship(int internshipID) {
        internshipReader.loadInternshipsFromCsv();
        loadApplicationsFromCsv((InternshipControllerInterface) internshipReader);
        return getApplicationsForInternship(internshipID, (InternshipControllerInterface) internshipReader);
    }
    
    /**
     * Checks if a student has applied for a specific internship.
     * Used to allow students to view internships they've applied for even if visibility is off.
     * 
     * @param student The student to check
     * @param internshipID The ID of the internship
     * @return true if student has applied, false otherwise
     */
    public boolean hasStudentAppliedForInternship(Student student, int internshipID) {
        List<Application> studentApplications = getApplicationsForStudent(student);
        return studentApplications.stream()
                .anyMatch(app -> app.getInternship().getID() == internshipID);
    }
    
    /**
     * Retrieves all applications submitted by a student.
     * Students can view all their applications, including those with WITHDRAWAL_REQUESTED status.
     * Always reloads from CSV to ensure fresh data.
     * 
     * @param student The student whose applications to retrieve
     * @return List of all applications submitted by the student
     */
    @Override
    public List<Application> getApplicationsForStudent(Student student) {
        loadApplicationsFromCsv((InternshipControllerInterface) internshipReader);
        return student.getApplications();
    }
    
    /**
     * Finds an application by ID from a student's applications.
     * 
     * @param applicationID The ID of the application to find
     * @param student The student whose applications to search
     * @return The found Application object
     * @throws IllegalArgumentException if application not found
     */
    @Override
    public Application findApplicationByID(int applicationID, Student student) {
        return student.findApplicationWithID(applicationID);
    }
    
    /**
     * Finds an application by ID from all internships.
     * Uses InternshipController for proper MVC separation.
     * Overloaded method - searches across all applications system-wide.
     * 
     * @param appID The ID of the application to find
     * @return The found Application object
     * @throws IllegalArgumentException if application not found
     */
    @Override
    public Application findApplicationByID(int appID) {
        return getAllApplications((InternshipControllerInterface) internshipReader).stream()
                .filter(app -> app.getId() == appID)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Application not found!"));
    }
    
    /**
     * Retrieves all withdrawal requests pending staff approval.
     * Loads applications from CSV first to ensure fresh data.
     * 
     * @return List of applications with WITHDRAWAL_REQUESTED status
     */
    @Override
    public List<Application> getWithdrawalRequests() {
        loadApplicationsFromCsv((InternshipControllerInterface) internshipReader);
        return Application.getAllApplications().stream()
                .filter(app -> app.getStatus() == ApplicationStatus.WITHDRAWAL_REQUESTED)
                .collect(Collectors.toList());
    }
    
    /**
     * Retrieves all applications from all internships.
     * Uses InternshipController for proper MVC separation.
     * Private helper method used by findApplicationByID.
     * 
     * @param internshipControllerParam Controller instance to ensure internships are loaded
     * @return List of all applications system-wide
     */
    private List<Application> getAllApplications(InternshipControllerInterface internshipControllerParam) {
        loadApplicationsFromCsv(internshipControllerParam);
        return Application.getAllApplications();
    }
    
    /**
     * Submits a withdrawal request for an application.
     * 
     * @param applicationID The ID of the application to withdraw
     * @param student The student requesting withdrawal
     * @param reason Optional reason for withdrawal (can be null)
     * @throws IllegalArgumentException if application not found or ineligible for withdrawal
     */
    @Override
    public void withdrawApplication(int applicationID, Student student, String reason) {
        Application app = findApplicationByID(applicationID, student);
        if (reason != null && !reason.trim().isEmpty()) {
            student.withdrawApplication(app, reason);
        } else {
            student.withdrawApplication(app);
        }
        csvHandler.saveToCsv(app);
    }
    
    /**
     * Accepts a successful application, confirming the student's placement.
     * 
     * @param applicationID The ID of the application to accept
     * @param student The student accepting the application
     * @throws IllegalArgumentException if application not found or not in SUCCESSFUL status
     */
    @Override
    public void acceptApplication(int applicationID, Student student) {
        Application app = findApplicationByID(applicationID, student);
        student.acceptApplication(app);
        csvHandler.saveToCsv(app);
        internshipWriter.saveInternship(app.getInternship());
    }
    
    /**
     * Approves a withdrawal request.
     * 
     * @param applicationID The ID of the application
     * @throws IllegalArgumentException if application not in WITHDRAWAL_REQUESTED status
     */
    @Override
    public void approveWithdrawal(int applicationID) {
        Application app = findApplicationByID(applicationID);
        if (app.getStatus() != ApplicationStatus.WITHDRAWAL_REQUESTED) {
            throw new IllegalArgumentException("Application is not in WITHDRAWAL_REQUESTED status.");
        }
        app.approveWithdrawal();
        csvHandler.saveToCsv(app);
        internshipWriter.saveInternship(app.getInternship());
    }
    
    /**
     * Rejects a withdrawal request.
     * Restores application to previous status and updates internship slots if necessary.
     * 
     * @param applicationID The ID of the application
     * @throws IllegalArgumentException if application not in WITHDRAWAL_REQUESTED status
     */
    @Override
    public void rejectWithdrawal(int applicationID) {
        Application app = findApplicationByID(applicationID);
        if (app.getStatus() != ApplicationStatus.WITHDRAWAL_REQUESTED) {
            throw new IllegalArgumentException("Application is not in WITHDRAWAL_REQUESTED status.");
        }
        ApplicationStatus previousStatus = app.getPreviousStatus();
        app.rejectWithdrawal();
        csvHandler.saveToCsv(app);
        
        // If restoring to SUCCESSFUL or ACCEPTED, update internship slots
        if (previousStatus == ApplicationStatus.SUCCESSFUL || previousStatus == ApplicationStatus.ACCEPTED) {
            app.getInternship().confirmPlacement();
            internshipWriter.saveInternship(app.getInternship());
        }
    }
    
    /**
     * Confirms student placement for an internship.
     * Handles both approving (PENDING → SUCCESSFUL) and confirming placement (SUCCESSFUL → ACCEPTED).
     * Overloaded method - uses internshipID to find application.
     * 
     * @param internshipID The ID of the internship
     * @param applicationID The ID of the application
     * @param internshipController Controller to retrieve internship details
     * @throws IllegalArgumentException if application not found or not in PENDING/SUCCESSFUL status
     */
    @Override
    public void confirmPlacement(int internshipID, int applicationID, InternshipControllerInterface internshipController) {
        loadApplicationsFromCsv(internshipController);
        
        Internship internship = internshipController.findInternship(internshipID);
        Application app = internship.getApplications().stream()
                .filter(a -> a.getId() == applicationID)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Application not found!"));
        
        processPlacementConfirmation(app, internship);
    }
    
    /**
     * Confirms student placement (overloaded without internshipController).
     * Finds application by ID directly and updates status.
     * 
     * @param applicationID The ID of the application
     * @throws IllegalArgumentException if application not found or not in PENDING/SUCCESSFUL status
     */
    @Override
    public void confirmPlacement(int applicationID) {
        Application app = findApplicationByID(applicationID);
        processPlacementConfirmation(app, app.getInternship());
    }
    
    /**
     * Processes the placement confirmation logic.
     * Handles both approving (PENDING → SUCCESSFUL) and confirming placement (SUCCESSFUL → ACCEPTED).
     * Extracted to eliminate code duplication.
     * 
     * @param app The application to process
     * @param internship The internship associated with the application
     * @throws IllegalArgumentException if application not in PENDING/SUCCESSFUL status
     */
    private void processPlacementConfirmation(Application app, Internship internship) {
        if (app.getStatus() == ApplicationStatus.PENDING) {
            app.setStatus(ApplicationStatus.SUCCESSFUL);
            csvHandler.saveToCsv(app);
        } else if (app.getStatus() == ApplicationStatus.SUCCESSFUL) {
            app.setStatus(ApplicationStatus.ACCEPTED);
            withdrawOtherApplications(app);
            internship.confirmPlacement();
            csvHandler.saveToCsv(app);
            internshipWriter.saveInternship(internship);
        } else {
            throw new IllegalArgumentException("Application is " + app.getStatus() + ", can only confirm placement for PENDING or SUCCESSFUL applications.");
        }
    }
    
    /**
     * Withdraws all other applications from the same student when a placement is confirmed.
     * 
     * @param app The application that was accepted
     */
    private void withdrawOtherApplications(Application app) {
        app.getApplicant().getApplications().stream()
                .filter(a -> a != app && a.getStatus() != ApplicationStatus.WITHDRAWN)
                .forEach(a -> {
                    a.setStatus(ApplicationStatus.WITHDRAWN);
                    csvHandler.saveToCsv(a);
                });
    }
    
    /**
     * Rejects a pending application.
     * Company Representatives can reject PENDING applications, setting status to UNSUCCESSFUL.
     * 
     * @param internshipID The ID of the internship
     * @param applicationID The ID of the application
     * @param internshipController Controller to retrieve internship details
     * @throws IllegalArgumentException if application not found or not in PENDING status
     */
    @Override
    public void rejectApplication(int internshipID, int applicationID, InternshipControllerInterface internshipController) {
        Internship internship = internshipReader.findInternship(internshipID);
        Application app = internship.getApplications().stream()
                .filter(a -> a.getId() == applicationID)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Application not found!"));
        
        if (app.getStatus() != ApplicationStatus.PENDING) {
            throw new IllegalArgumentException("Application is " + app.getStatus() + ", can only reject PENDING applications.");
        }
        
        // Reject application - mark as UNSUCCESSFUL
        app.setStatus(ApplicationStatus.UNSUCCESSFUL);
        csvHandler.saveToCsv(app);
    }
    
    /**
     * Rejects a pending application (overloaded without internshipController).
     * Finds application by ID directly and sets status to UNSUCCESSFUL.
     * 
     * @param applicationID The ID of the application
     * @throws IllegalArgumentException if application not found or not in PENDING status
     */
    @Override
    public void rejectApplication(int applicationID) {
        Application app = findApplicationByID(applicationID);
        
        if (app.getStatus() != ApplicationStatus.PENDING) {
            throw new IllegalArgumentException("Application is " + app.getStatus() + ", can only reject PENDING applications.");
        }
        app.setStatus(ApplicationStatus.UNSUCCESSFUL);
        csvHandler.saveToCsv(app);
    }
    
    /**
     * Rejects a placement offer.
     * Students can reject SUCCESSFUL or ACCEPTED applications, setting status to UNSUCCESSFUL.
     * Updates internship filled slots if application was previously ACCEPTED.
     * 
     * @param applicationID The ID of the application
     * @param student The student rejecting the placement
     * @throws IllegalArgumentException if application not found or not in SUCCESSFUL/ACCEPTED status
     */
    @Override
    public void rejectPlacement(int applicationID, Student student) {
        Application app = findApplicationByID(applicationID, student);
        
        if (app.getStatus() != ApplicationStatus.SUCCESSFUL && app.getStatus() != ApplicationStatus.ACCEPTED) {
            throw new IllegalArgumentException("Application is " + app.getStatus() + ", can only reject placement for SUCCESSFUL or ACCEPTED applications.");
        }
        boolean wasAccepted = app.getStatus() == ApplicationStatus.ACCEPTED;
        app.setStatus(ApplicationStatus.UNSUCCESSFUL);
        csvHandler.saveToCsv(app);
        if (wasAccepted) {
            app.getInternship().confirmPlacement();
            internshipWriter.saveInternship(app.getInternship());
        }
    }
    
    /**
     * Loads all applications from CSV file.
     * Clears existing data and reloads from disk.
     * Ensures internships are loaded first (applications depend on them).
     * 
     * @param internshipControllerParam Controller to ensure internships are loaded
     */
    @Override
    public void loadApplicationsFromCsv(InternshipControllerInterface internshipControllerParam) {
        internshipControllerParam.loadInternshipsFromCsv();
        Application.clearAll();
        csvHandler.loadFromCsv();
    }
    
}

