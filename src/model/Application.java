package Assignment.src.model;

import Assignment.src.constant.ApplicationStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Application {
    private static int nextID = 500000;
    private static final List<Application> allApplications = new ArrayList<>();
    private static boolean loaded = false;

    /**
     * Get all applications - loads from CSV lazily if not already loaded
     * Also reloads if internships have been reloaded (clears applications)
     */
    public static List<Application> getAllApplications() {
        loadFromCsv(); // Always reload to ensure fresh data after internships reload
        return new ArrayList<>(allApplications);
    }
    
    /**
     * Load applications from CSV file - always reloads to ensure fresh data
     */
    private static void loadFromCsv() {
        // Clear existing applications from all tracking lists
        allApplications.clear();
        // Clear applications from students first
        Assignment.src.model.UserRegistry.getInstance().getAllUsers().stream()
            .filter(u -> u instanceof Assignment.src.model.Student)
            .map(u -> (Assignment.src.model.Student) u)
            .forEach(student -> student.getApplications().clear());
        // Ensure internships are loaded first (applications depend on them, and Internship.loadFromCsv() clears internship applications)
        Assignment.src.model.Internship.getAllInternships();
        // Now load applications from CSV (this will link them to internships and students)
        Assignment.src.utils.ApplicationCsvHandler.loadFromCsv();
    }

    private final int id;
    private final Internship internship;
    private final Student applicant;
    private final LocalDateTime dateApplied;
    private ApplicationStatus status;
    private ApplicationStatus previousStatus; // Track previous status for withdrawal rejection
    private String withdrawalReason; // Reason for withdrawal request

    public Application(Internship internship, Student applicant, LocalDateTime dateApplied) {
        this(nextID++, internship, applicant, dateApplied, ApplicationStatus.PENDING, null, null);
        allApplications.add(this);
    }

    public int getId() { return id; }
    public ApplicationStatus getStatus() { return status; }
    public Student getApplicant() { return applicant; }
    public Internship getInternship() { return internship; }
    public LocalDateTime getDateApplied() { return dateApplied; }

    public void setStatus(ApplicationStatus status) { 
        this.status = status;
        // Save to CSV
        Assignment.src.utils.ApplicationCsvHandler.saveToCsv(this);
    }

    public void requestWithdrawal(String reason) {
        // Students can request withdrawal for PENDING, SUCCESSFUL, or ACCEPTED applications
        if (status == ApplicationStatus.PENDING || status == ApplicationStatus.SUCCESSFUL || status == ApplicationStatus.ACCEPTED) {
            this.previousStatus = this.status; // Store the previous status
            this.status = ApplicationStatus.WITHDRAWAL_REQUESTED;
            this.withdrawalReason = reason != null ? reason : ""; // Store withdrawal reason (optional)
            // Save to CSV
            Assignment.src.utils.ApplicationCsvHandler.saveToCsv(this);
        } else {
            throw new IllegalStateException("Cannot request withdrawal for application with status: " + status);
        }
    }
    
    /**
     * Request withdrawal without reason (overloaded)
     */
    public void requestWithdrawal() {
        requestWithdrawal(null);
    }
    
    public String getWithdrawalReason() {
        return withdrawalReason;
    }
    
    public void approveWithdrawal() {
        if (status == ApplicationStatus.WITHDRAWAL_REQUESTED) {
            this.status = ApplicationStatus.WITHDRAWN;
            
            // If the previous status was SUCCESSFUL or ACCEPTED, we need to update the internship's filled slots
            if (previousStatus == ApplicationStatus.SUCCESSFUL || previousStatus == ApplicationStatus.ACCEPTED) {
                internship.confirmPlacement(); // This will recount and update filledSlots
            }
            
            this.previousStatus = null;
            this.withdrawalReason = null; // Clear withdrawal reason after approval
            // Save to CSV
            Assignment.src.utils.ApplicationCsvHandler.saveToCsv(this);
        } else {
            throw new IllegalStateException("Application is not in WITHDRAWAL_REQUESTED status.");
        }
    }
    
    public void rejectWithdrawal() {
        if (status == ApplicationStatus.WITHDRAWAL_REQUESTED) {
            // Restore to previous status
            if (previousStatus != null) {
                this.status = previousStatus;
                this.previousStatus = null;
                this.withdrawalReason = null; // Clear withdrawal reason after rejection
                // Save to CSV
                Assignment.src.utils.ApplicationCsvHandler.saveToCsv(this);
            }
        }
    }

    @Override
    public String toString() {
        return String.format("ID: %d | Internship: %s | Applicant: %s (%s) | Status: %s | Date: %s",
                id, internship.getTitle(), applicant.getName(), applicant.getUserID(), status, dateApplied);
    }
    
    // Public helper methods for CSV handler
    public static List<Application> getAllApplicationsList() {
        return allApplications;
    }
    
    public static int getNextID() {
        return nextID;
    }
    
    public static void setNextID(int id) {
        nextID = id;
    }
    
    // Public factory method for CSV loading
    public static Application createForCsv(int id, Internship internship, Student student, LocalDateTime dateApplied,
                                   ApplicationStatus status, ApplicationStatus previousStatus, String withdrawalReason) {
        return new Application(id, internship, student, dateApplied, status, previousStatus, withdrawalReason);
    }
    
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
    public ApplicationStatus getPreviousStatus() {
        return previousStatus;
    }
}
