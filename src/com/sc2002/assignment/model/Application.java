package com.sc2002.assignment.model;

import com.sc2002.assignment.constant.ApplicationStatus;
import com.sc2002.assignment.utils.ApplicationCsvHandler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Application {
    private static int nextID = 500000;
    private static final List<Application> allApplications = new ArrayList<>();

    private final int id;
    private final Internship internship;
    private final Student applicant;
    private final LocalDateTime dateApplied;
    private ApplicationStatus status;
    private ApplicationStatus previousStatus; // Track previous status for withdrawal rejection

    public Application(Internship internship, Student applicant, LocalDateTime dateApplied) {
        this(nextID++, internship, applicant, dateApplied, ApplicationStatus.PENDING, null);
        allApplications.add(this);
    }
    
    public static List<Application> getAllApplications() {
        return new ArrayList<>(allApplications);
    }

    public int getId() { return id; }
    public ApplicationStatus getStatus() { return status; }
    public Student getApplicant() { return applicant; }
    public Internship getInternship() { return internship; }
    public LocalDateTime getDateApplied() { return dateApplied; }

    public void setStatus(ApplicationStatus status) { 
        this.status = status;
        // Save to CSV
        ApplicationCsvHandler.saveToCsv(this);
    }

    public void requestWithdrawal() {
        // Students can request withdrawal for PENDING, SUCCESSFUL, or CONFIRMED applications
        if (status == ApplicationStatus.PENDING || status == ApplicationStatus.SUCCESSFUL 
            || status == ApplicationStatus.CONFIRMED) {
            this.previousStatus = this.status; // Store the previous status
            this.status = ApplicationStatus.WITHDRAWAL_REQUESTED;
            // Save to CSV
            ApplicationCsvHandler.saveToCsv(this);
        } else {
            throw new IllegalStateException("Cannot request withdrawal for application with status: " + status);
        }
    }
    
    public void approveWithdrawal() {
        if (status == ApplicationStatus.WITHDRAWAL_REQUESTED) {
            this.status = ApplicationStatus.WITHDRAWN;
            
            // If the previous status was CONFIRMED, we need to update the internship's filled slots
            if (previousStatus == ApplicationStatus.CONFIRMED) {
                internship.confirmPlacement(); // This will recount and update filledSlots
            }
            
            this.previousStatus = null;
            // Save to CSV
            ApplicationCsvHandler.saveToCsv(this);
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
                // Save to CSV
                ApplicationCsvHandler.saveToCsv(this);
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
                                   ApplicationStatus status, ApplicationStatus previousStatus) {
        return new Application(id, internship, student, dateApplied, status, previousStatus);
    }
    
    private Application(int id, Internship internship, Student student, LocalDateTime dateApplied,
                       ApplicationStatus status, ApplicationStatus previousStatus) {
        this.id = id;
        this.internship = internship;
        this.applicant = student;
        this.dateApplied = dateApplied;
        this.status = status;
        this.previousStatus = previousStatus;
    }
    
    // Public getter for CSV handler
    public ApplicationStatus getPreviousStatus() {
        return previousStatus;
    }
}
