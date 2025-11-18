package model;

import constant.ApplicationStatus;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a student application for an internship.
 * Manages application status and withdrawal requests.
 * 
 * Note: CSV persistence is handled by controllers, not models (MVC compliance).
 * 
 * @author NTU SC2002 Group
 * @version 1.0
 * @since 2025-11-16
 */
public class Application {
    /** Next available application ID (auto-incrementing) */
    private static int nextID = 500000;
    
    /** Static list of all applications in the system */
    private static final List<Application> allApplications = new ArrayList<>();

    /**
     * Get all applications from in-memory list.
     * Note: Loading from CSV is handled by controllers/repositories (MVC compliance).
     * @return list of all applications
     */
    public static List<Application> getAllApplications() {
        return new ArrayList<>(allApplications);
    }
    
    /**
     * Clears all applications from memory.
     * Used by CSV handlers when reloading data.
     */
    public static void clearAll() {
        allApplications.clear();
        // Clear applications from students
        UserRegistry.getInstance().getAllUsers().stream()
            .filter(u -> u instanceof Student)
            .map(u -> (Student) u)
            .forEach(student -> student.getApplications().clear());
    }

    /** Unique application ID */
    private final int id;
    
    /** The internship this application is for */
    private final Internship internship;
    
    /** The student who submitted this application */
    private final Student applicant;
    
    /** Date and time when application was submitted */
    private final LocalDateTime dateApplied;
    
    /** Current status of the application */
    private ApplicationStatus status;
    
    /** Previous status (tracked for withdrawal rejection) */
    private ApplicationStatus previousStatus;
    
    /** Reason provided for withdrawal request */
    private String withdrawalReason;

    /**
     * Constructs a new Application with the specified details.
     * Automatically assigns a unique ID and adds to the global application list.
     * Note: CSV persistence is handled by controllers (MVC compliance).
     * 
     * @param internship The internship being applied for
     * @param applicant The student submitting the application
     * @param dateApplied The date and time of application submission
     */
    public Application(Internship internship, Student applicant, LocalDateTime dateApplied) {
        this(nextID++, internship, applicant, dateApplied, ApplicationStatus.PENDING, null, null);
        allApplications.add(this);
    }

    /**
     * Gets the unique application ID.
     * @return the unique application ID
     */
    public int getId() { return id; }
    
    /**
     * Gets the current application status.
     * @return the current status
     */
    public ApplicationStatus getStatus() { return status; }
    
    /**
     * Gets the student who submitted this application.
     * @return the student who applied
     */
    public Student getApplicant() { return applicant; }
    
    /**
     * Gets the internship that was applied for.
     * @return the internship applied for
     */
    public Internship getInternship() { return internship; }
    
    /**
     * Gets the date and time when the application was submitted.
     * @return the date and time of application
     */
    public LocalDateTime getDateApplied() { return dateApplied; }

    /**
     * Sets the status of this application.
     * Note: CSV persistence is handled by controllers (MVC compliance).
     * 
     * @param status The new status to set
     */
    public void setStatus(ApplicationStatus status) { 
        this.status = status;
    }

    /**
     * Requests withdrawal of this application.
     * Students can request withdrawal for PENDING, SUCCESSFUL, or ACCEPTED applications.
     * Stores the previous status for potential rejection of withdrawal.
     * Note: CSV persistence is handled by controllers (MVC compliance).
     * 
     * @param reason Optional reason for withdrawal
     * @throws IllegalStateException if application status does not allow withdrawal
     */
    public void requestWithdrawal(String reason) {
        // Students can request withdrawal for PENDING, SUCCESSFUL, or ACCEPTED applications
        if (status == ApplicationStatus.PENDING || status == ApplicationStatus.SUCCESSFUL || status == ApplicationStatus.ACCEPTED) {
            this.previousStatus = this.status; // Store the previous status
            this.status = ApplicationStatus.WITHDRAWAL_REQUESTED;
            this.withdrawalReason = reason != null ? reason : ""; // Store withdrawal reason (optional)
        } else {
            throw new IllegalStateException("Cannot request withdrawal for application with status: " + status);
        }
    }
    
    /**
     * Requests withdrawal without providing a reason.
     * Delegates to requestWithdrawal(String) with null reason.
     */
    public void requestWithdrawal() {
        requestWithdrawal(null);
    }
    
    /**
     * Gets the reason provided for withdrawal request.
     * 
     * @return The withdrawal reason, or null if not applicable
     */
    public String getWithdrawalReason() {
        return withdrawalReason;
    }
    
    /**
     * Approves a withdrawal request.
     * Sets status to WITHDRAWN and updates internship filled slots if necessary.
     * Note: CSV persistence is handled by controllers (MVC compliance).
     * 
     * @throws IllegalStateException if application is not in WITHDRAWAL_REQUESTED status
     */
    public void approveWithdrawal() {
        if (status == ApplicationStatus.WITHDRAWAL_REQUESTED) {
            this.status = ApplicationStatus.WITHDRAWN;
            
            // If the previous status was SUCCESSFUL or ACCEPTED, we need to update the internship's filled slots
            if (previousStatus == ApplicationStatus.SUCCESSFUL || previousStatus == ApplicationStatus.ACCEPTED) {
                internship.confirmPlacement(); // This will recount and update filledSlots
            }
            
            this.previousStatus = null;
            this.withdrawalReason = null; // Clear withdrawal reason after approval
        } else {
            throw new IllegalStateException("Application is not in WITHDRAWAL_REQUESTED status.");
        }
    }
    
    /**
     * Rejects a withdrawal request.
     * Restores the application to its previous status.
     * Note: CSV persistence is handled by controllers (MVC compliance).
     * 
     * @throws IllegalStateException if application is not in WITHDRAWAL_REQUESTED status
     */
    public void rejectWithdrawal() {
        if (status == ApplicationStatus.WITHDRAWAL_REQUESTED) {
            // Restore to previous status
            if (previousStatus != null) {
                this.status = previousStatus;
                this.previousStatus = null;
                this.withdrawalReason = null; // Clear withdrawal reason after rejection
            }
        }
    }

    /**
     * Returns a string representation of this application.
     * 
     * @return Formatted string with application details
     */
    @Override
    public String toString() {
        return String.format("ID: %d | Internship: %s | Applicant: %s (%s) | Status: %s | Date: %s",
                id, internship.getTitle(), applicant.getName(), applicant.getUserID(), status, dateApplied);
    }
    
    // Public helper methods for CSV handler
    /**
     * Gets the internal list of all applications.
     * Used by CSV handler for persistence operations.
     * 
     * @return The list of all applications
     */
    public static List<Application> getAllApplicationsList() {
        return allApplications;
    }
    
    /**
     * Gets the next available ID.
     * 
     * @return The next ID to be assigned
     */
    public static int getNextID() {
        return nextID;
    }
    
    /**
     * Sets the next ID value.
     * Used by CSV handler when loading from file.
     * 
     * @param id The new next ID value
     */
    public static void setNextID(int id) {
        nextID = id;
    }
    
    // Public factory method for CSV loading
    /**
     * Factory method for creating applications from CSV data.
     * Used by CSV handler to reconstruct applications with all fields.
     * 
     * @param id Application ID
     * @param internship The internship
     * @param student The student
     * @param dateApplied Application date
     * @param status Current status
     * @param previousStatus Previous status (for withdrawal tracking)
     * @param withdrawalReason Withdrawal reason
     * @return A new Application instance
     */
    public static Application createForCsv(int id, Internship internship, Student student, LocalDateTime dateApplied,
                                   ApplicationStatus status, ApplicationStatus previousStatus, String withdrawalReason) {
        return new Application(id, internship, student, dateApplied, status, previousStatus, withdrawalReason);
    }
    
    /**
     * Private constructor for CSV loading.
     * Allows setting all fields including ID, status, and withdrawal information.
     * 
     * @param id Application ID
     * @param internship The internship
     * @param student The student
     * @param dateApplied Application date
     * @param status Current status
     * @param previousStatus Previous status
     * @param withdrawalReason Withdrawal reason
     */
    private Application(int id, Internship internship, Student student, LocalDateTime dateApplied,
                       ApplicationStatus status, ApplicationStatus previousStatus, String withdrawalReason) {
        this.id = id;
        this.internship = internship;
        this.applicant = student;
        this.dateApplied = dateApplied;
        this.status = status;
        this.previousStatus = previousStatus;
        this.withdrawalReason = withdrawalReason;
    }
    
    // Public getter for CSV handler
    /**
     * Gets the previous status value.
     * Used by CSV handler for persistence.
     * 
     * @return The previous status, or null if not applicable
     */
    public ApplicationStatus getPreviousStatus() {
        return previousStatus;
    }
}

