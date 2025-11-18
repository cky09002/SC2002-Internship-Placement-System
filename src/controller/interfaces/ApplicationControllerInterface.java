package controller.interfaces;

import model.*;

import java.util.List;

/**
 * Interface for application-related operations.
 * Follows Dependency Inversion Principle - clients depend on this abstraction, not concrete implementation.
 * 
 * @author NTU SC2002 Group
 * @version 1.0
 * @since 2025-11-16
 */
public interface ApplicationControllerInterface {
    /**
     * Creates an application for a student to a specific internship.
     * 
     * @param internshipID The ID of the internship to apply to
     * @param student The student submitting the application
     * @param internshipController Controller to retrieve internship details
     * @return The created Application object
     * @throws IllegalArgumentException if internship not found or student ineligible
     */
    Application createApplication(int internshipID, Student student, controller.interfaces.InternshipControllerInterface internshipController);
    
    /**
     * Retrieves all applications for a specific internship.
     * 
     * @param internshipID The ID of the internship
     * @param internshipController Controller to retrieve internship details
     * @return List of applications for the internship
     * @throws IllegalArgumentException if internship not found
     */
    List<Application> getApplicationsForInternship(int internshipID, controller.interfaces.InternshipControllerInterface internshipController);
    
    /**
     * Retrieves all applications submitted by a student.
     * 
     * @param student The student whose applications to retrieve
     * @return List of all applications submitted by the student
     */
    List<Application> getApplicationsForStudent(Student student);
    
    /**
     * Finds an application by ID from a student's applications.
     * 
     * @param applicationID The ID of the application to find
     * @param student The student whose applications to search
     * @return The found Application object
     * @throws IllegalArgumentException if application not found
     */
    Application findApplicationByID(int applicationID, Student student);
    
    /**
     * Finds an application by ID from all internships.
     * 
     * @param appID The ID of the application to find
     * @return The found Application object
     * @throws IllegalArgumentException if application not found
     */
    Application findApplicationByID(int appID);
    
    /**
     * Submits a withdrawal request for an application.
     * 
     * @param applicationID The ID of the application to withdraw
     * @param student The student requesting withdrawal
     * @param reason Optional reason for withdrawal (can be null)
     * @throws IllegalArgumentException if application not found or ineligible for withdrawal
     */
    void withdrawApplication(int applicationID, Student student, String reason);
    
    /**
     * Accepts a successful application, confirming the student's placement.
     * 
     * @param applicationID The ID of the application to accept
     * @param student The student accepting the application
     * @throws IllegalArgumentException if application not found or not in SUCCESSFUL status
     */
    void acceptApplication(int applicationID, Student student);
    
    /**
     * Approves a withdrawal request.
     * 
     * @param applicationID The ID of the application
     * @throws IllegalArgumentException if application not in WITHDRAWAL_REQUESTED status
     */
    void approveWithdrawal(int applicationID);
    
    /**
     * Rejects a withdrawal request.
     * 
     * @param applicationID The ID of the application
     * @throws IllegalArgumentException if application not in WITHDRAWAL_REQUESTED status
     */
    void rejectWithdrawal(int applicationID);
    
    /**
     * Confirms student placement for an internship.
     * 
     * @param internshipID The ID of the internship
     * @param applicationID The ID of the application
     * @param internshipController Controller to retrieve internship details
     * @throws IllegalArgumentException if application not found or not in PENDING/SUCCESSFUL status
     */
    void confirmPlacement(int internshipID, int applicationID, controller.interfaces.InternshipControllerInterface internshipController);
    
    /**
     * Confirms student placement (overloaded without internshipController).
     * 
     * @param applicationID The ID of the application
     * @throws IllegalArgumentException if application not found or not in PENDING/SUCCESSFUL status
     */
    void confirmPlacement(int applicationID);
    
    /**
     * Rejects a pending application.
     * 
     * @param internshipID The ID of the internship
     * @param applicationID The ID of the application
     * @param internshipController Controller to retrieve internship details
     * @throws IllegalArgumentException if application not found or not in PENDING status
     */
    void rejectApplication(int internshipID, int applicationID, controller.interfaces.InternshipControllerInterface internshipController);
    
    /**
     * Rejects a pending application (overloaded without internshipController).
     * 
     * @param applicationID The ID of the application
     * @throws IllegalArgumentException if application not found or not in PENDING status
     */
    void rejectApplication(int applicationID);
    
    /**
     * Rejects a placement offer.
     * 
     * @param applicationID The ID of the application
     * @param student The student rejecting the placement
     * @throws IllegalArgumentException if application not found or not in SUCCESSFUL/ACCEPTED status
     */
    void rejectPlacement(int applicationID, Student student);
    
    /**
     * Loads all applications from CSV file.
     * 
     * @param internshipController Controller to ensure internships are loaded
     */
    void loadApplicationsFromCsv(controller.interfaces.InternshipControllerInterface internshipController);
    
    /**
     * Retrieves all withdrawal requests pending staff approval.
     * 
     * @return List of applications with WITHDRAWAL_REQUESTED status
     */
    List<Application> getWithdrawalRequests();
}

