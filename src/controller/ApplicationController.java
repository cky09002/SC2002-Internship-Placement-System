package Assignment.src.controller;

import Assignment.src.model.Internship;
import Assignment.src.model.Application;
import Assignment.src.model.Student;
import Assignment.src.constant.ApplicationStatus;
import Assignment.src.utils.ApplicationFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller class that handles all Application-related logic.
 * Controllers should use this class instead of accessing Application model directly.
 */
public class ApplicationController {
    
    /**
     * Create an application for a student
     * Students can only select internships that are visible to them (already filtered by major, level, and visibility)
     */
    public Application createApplication(int internshipID, Student student, InternshipController internshipController) {
        Internship internship = internshipController.findInternship(internshipID);
        return student.submitApplication(internship);
    }
    
    /**
     * Get all applications for an internship
     * Overloaded - can use InternshipController or find directly
     */
    public List<Application> getApplicationsForInternship(int internshipID, InternshipController internshipController) {
        Internship internship = internshipController.findInternship(internshipID);
        return internship.getApplications();
    }
    
    /**
     * Get all applications for an internship (overloaded) - should use InternshipController
     * NOTE: This overload requires InternshipController for proper MVC separation.
     * Consider using the overloaded version with InternshipController parameter instead.
     */
    public List<Application> getApplicationsForInternship(int internshipID) {
        // For proper MVC, we need InternshipController, so delegate to the other overload
        // by creating a temporary controller instance (slight inefficiency but maintains MVC)
        InternshipController internshipController = new InternshipController();
        // Ensure fresh data by reloading internships
        internshipController.getAllInternships();
        // Reload applications after internships (applications depend on internships being loaded)
        Application.getAllApplications(); // This will reload applications and link them to internships
        return getApplicationsForInternship(internshipID, internshipController);
    }
    
    /**
     * Check if a student has applied for a specific internship
     * Used to allow students to view internships they've applied for even if visibility is off
     */
    public boolean hasStudentAppliedForInternship(Student student, int internshipID) {
        List<Application> studentApplications = getApplicationsForStudent(student);
        return studentApplications.stream()
                .anyMatch(app -> app.getInternship().getID() == internshipID);
    }
    
    /**
     * Get all applications for a student
     * Students can view all their applications, including those with WITHDRAWAL_REQUESTED status
     * Always reloads from CSV to ensure fresh data
     */
    public List<Application> getApplicationsForStudent(Student student) {
        // Always reload applications from CSV to ensure fresh data
        Application.getAllApplications();
        return student.getApplications();
    }
    
    /**
     * Find application by ID from a student's applications
     */
    public Application findApplicationByID(int applicationID, Student student) {
        return student.findApplicationWithID(applicationID);
    }
    
    /**
     * Find application by ID from all internships - uses InternshipController for proper MVC
     * Overloaded method - can find from all internships or from student's applications
     */
    public Application findApplicationByID(int appID) {
        return getAllApplications(new InternshipController()).stream()
                .filter(app -> app.getId() == appID)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Application not found!"));
    }
    
    /**
     * Get all withdrawal requests - loads applications from CSV first to ensure fresh data
     */
    public List<Application> getWithdrawalRequests() {
        // Load all applications from CSV first (this ensures internships are also loaded)
        List<Application> allApps = Application.getAllApplications();
        return allApps.stream()
                .filter(app -> app.getStatus() == ApplicationStatus.WITHDRAWAL_REQUESTED)
                .collect(Collectors.toList());
    }
    
    /**
     * Get all applications from all internships - uses InternshipController for proper MVC separation
     * Used by findApplicationByID
     */
    private List<Application> getAllApplications(InternshipController internshipController) {
        // Load all applications from CSV first (this ensures internships are also loaded)
        return Application.getAllApplications();
    }
    
    /**
     * Withdraw an application
     */
    public void withdrawApplication(int applicationID, Student student, String reason) {
        Application app = findApplicationByID(applicationID, student);
        if (reason != null && !reason.trim().isEmpty()) {
            student.withdrawApplication(app, reason);
        } else {
            student.withdrawApplication(app);
        }
    }
    
    /**
     * Accept an application
     */
    public void acceptApplication(int applicationID, Student student) {
        Application app = findApplicationByID(applicationID, student);
        student.acceptApplication(app);
    }
    
    /**
     * Approve or reject withdrawal request - consolidated method using polymorphism
     * @param applicationID Application ID
     * @param approve true to approve, false to reject
     */
    public void processWithdrawal(int applicationID, boolean approve) {
        Application app = findApplicationByID(applicationID);
        if (app.getStatus() != ApplicationStatus.WITHDRAWAL_REQUESTED) {
            throw new IllegalArgumentException("Application is not in WITHDRAWAL_REQUESTED status.");
        }
        if (approve) {
            app.approveWithdrawal();
        } else {
            app.rejectWithdrawal();
        }
    }
    
    /**
     * Approve withdrawal request - delegates to processWithdrawal for consistency
     */
    public void approveWithdrawal(int applicationID) {
        processWithdrawal(applicationID, true);
    }
    
    /**
     * Reject withdrawal request - delegates to processWithdrawal for consistency
     */
    public void rejectWithdrawal(int applicationID) {
        processWithdrawal(applicationID, false);
    }
    
    /**
     * Confirm placement - handles both approving (PENDING → SUCCESSFUL) and confirming placement (SUCCESSFUL → ACCEPTED)
     * Overloaded - can find by internshipID+applicationID or just applicationID
     */
    public void confirmPlacement(int internshipID, int applicationID, InternshipController internshipController) {
        Internship internship = internshipController.findInternship(internshipID);
        Application app = internship.getApplications().stream()
                .filter(a -> a.getId() == applicationID)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Application not found!"));
        
        if (app.getStatus() == ApplicationStatus.PENDING) {
            // Approve application - mark as SUCCESSFUL
            app.setStatus(ApplicationStatus.SUCCESSFUL);
        } else if (app.getStatus() == ApplicationStatus.SUCCESSFUL) {
            // Confirm placement - mark as ACCEPTED
            app.setStatus(ApplicationStatus.ACCEPTED);
            // Withdraw all other applications from the same student
            app.getApplicant().getApplications().stream()
                    .filter(a -> a != app && a.getStatus() != ApplicationStatus.WITHDRAWN)
                    .forEach(a -> a.setStatus(ApplicationStatus.WITHDRAWN));
            // Update filled slots in internship
            internship.confirmPlacement();
        } else {
            throw new IllegalArgumentException("Application is " + app.getStatus() + ", can only confirm placement for PENDING or SUCCESSFUL applications.");
        }
    }
    
    /**
     * Confirm placement (overloaded) - finds application by ID directly
     */
    public void confirmPlacement(int applicationID) {
        Application app = findApplicationByID(applicationID);
        
        if (app.getStatus() == ApplicationStatus.PENDING) {
            // Approve application - mark as SUCCESSFUL
            app.setStatus(ApplicationStatus.SUCCESSFUL);
        } else if (app.getStatus() == ApplicationStatus.SUCCESSFUL) {
            // Confirm placement - mark as ACCEPTED
            app.setStatus(ApplicationStatus.ACCEPTED);
            // Withdraw all other applications from the same student
            app.getApplicant().getApplications().stream()
                    .filter(a -> a != app && a.getStatus() != ApplicationStatus.WITHDRAWN)
                    .forEach(a -> a.setStatus(ApplicationStatus.WITHDRAWN));
            // Update filled slots in internship
            app.getInternship().confirmPlacement();
        } else {
            throw new IllegalArgumentException("Application is " + app.getStatus() + ", can only confirm placement for PENDING or SUCCESSFUL applications.");
        }
    }
    
    /**
     * Reject application - Company Representative can reject PENDING applications
     * Sets status to UNSUCCESSFUL
     * @param internshipID Internship ID
     * @param applicationID Application ID
     * @param internshipController InternshipController instance
     */
    public void rejectApplication(int internshipID, int applicationID, InternshipController internshipController) {
        Internship internship = internshipController.findInternship(internshipID);
        Application app = internship.getApplications().stream()
                .filter(a -> a.getId() == applicationID)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Application not found!"));
        
        if (app.getStatus() != ApplicationStatus.PENDING) {
            throw new IllegalArgumentException("Application is " + app.getStatus() + ", can only reject PENDING applications.");
        }
        
        // Reject application - mark as UNSUCCESSFUL
        app.setStatus(ApplicationStatus.UNSUCCESSFUL);
    }
    
    /**
     * Reject application (overloaded) - finds application by ID directly
     */
    public void rejectApplication(int applicationID) {
        Application app = findApplicationByID(applicationID);
        
        if (app.getStatus() != ApplicationStatus.PENDING) {
            throw new IllegalArgumentException("Application is " + app.getStatus() + ", can only reject PENDING applications.");
        }
        
        // Reject application - mark as UNSUCCESSFUL
        app.setStatus(ApplicationStatus.UNSUCCESSFUL);
    }
    
    /**
     * Reject placement - Student can reject SUCCESSFUL or ACCEPTED applications
     * Sets status to UNSUCCESSFUL
     * @param applicationID Application ID
     * @param student Student rejecting the placement
     */
    public void rejectPlacement(int applicationID, Student student) {
        Application app = findApplicationByID(applicationID, student);
        
        if (app.getStatus() != ApplicationStatus.SUCCESSFUL && app.getStatus() != ApplicationStatus.ACCEPTED) {
            throw new IllegalArgumentException("Application is " + app.getStatus() + ", can only reject placement for SUCCESSFUL or ACCEPTED applications.");
        }
        
        // Store original status before changing
        boolean wasAccepted = app.getStatus() == ApplicationStatus.ACCEPTED;
        
        // Reject placement - mark as UNSUCCESSFUL
        app.setStatus(ApplicationStatus.UNSUCCESSFUL);
        
        // If it was ACCEPTED, need to update internship filled slots
        if (wasAccepted) {
            app.getInternship().confirmPlacement(); // This will recount and update filledSlots
        }
    }
    
}

