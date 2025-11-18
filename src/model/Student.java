package model;

import constant.ApplicationStatus;
import utils.validation.ValidationHelper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a student user who can apply for internships.
 * Students can submit up to MAX_APPLICATIONS applications, view their applications,
 * accept placement offers, and withdraw applications.
 * 
 * @author NTU SC2002 Group
 * @version 1.0
 * @since 2025-11-16
 */
public class Student extends User {
    /** Maximum number of active applications a student can have */
    public static final int MAX_APPLICATIONS = 3;

    /** Current year of study (1-5) */
    private int yearOfStudy;
    /** Academic major/program */
    private String major;
    /** List of applications submitted by this student */
    private final List<Application> applications = new ArrayList<>(MAX_APPLICATIONS);

    /**
     * Constructs a new Student with the specified details.
     * 
     * @param userID unique student identifier
     * @param name student's display name
     * @param password authentication password
     * @param email student's email address
     * @param yearOfStudy current year of study (1-5)
     * @param major academic major or program
     */
    public Student(String userID, String name, String password, String email, int yearOfStudy, String major) {
        super(userID, name, password, email);
        this.yearOfStudy = yearOfStudy;
        this.major = major;
    }

    /**
     * Gets all applications submitted by this student.
     * @return list of applications
     */
    public List<Application> getApplications() { return applications; }

    /**
     * Finds an application by its ID.
     * @param applicationID the ID of the application to find
     * @return the matching application
     * @throws IllegalArgumentException if no application with the given ID exists
     */
    public Application findApplicationWithID(int applicationID) {
        return applications.stream()
                .filter((a) -> a.getId() == applicationID)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Couldn't find application with ID " + applicationID + "."));
    }

    /**
     * Submits a new application for an internship.
     * Enforces the maximum application limit (excluding withdrawn applications).
     * Prevents duplicate applications to the same internship.
     * Prevents new applications if student already has an accepted placement.
     * 
     * @param internship the internship to apply for
     * @return the newly created application
     * @throws IllegalArgumentException if maximum applications reached, already applied, or already has accepted placement
     */
    public Application submitApplication(Internship internship) {
        // Check if student has already accepted a placement
        boolean hasAcceptedPlacement = applications.stream()
                .anyMatch(a -> a.getStatus() == ApplicationStatus.ACCEPTED);
        
        if (hasAcceptedPlacement) {
            throw new IllegalArgumentException("You have already accepted a placement and cannot apply to other internships.");
        }
        
        // Check if student has already applied to this internship
        boolean alreadyApplied = applications.stream()
                .anyMatch(a -> a.getInternship().getID() == internship.getID());
        
        if (alreadyApplied) {
            throw new IllegalArgumentException("You have already applied to this internship.");
        }
        
        // enforce per-student application limit (exclude only WITHDRAWN applications)
        long activeApplications = applications.stream()
                .filter(a -> a.getStatus() != ApplicationStatus.WITHDRAWN)
                .count();
        
        if (activeApplications >= MAX_APPLICATIONS) {
            throw new IllegalArgumentException("Maximum of " + MAX_APPLICATIONS + " applications allowed.");
        }

        // create the application object and pass it to associated internship
        Application app = new Application(
                internship,
                this,
                LocalDateTime.now()
        );
        internship.addApplication(app);

        // also store a reference locally
        applications.add(app);
        return app;
    }

    /**
     * Accepts a successful application and confirms placement.
     * This withdraws all other applications and updates the internship's filled slots.
     * 
     * @param app the application to accept (must be SUCCESSFUL status)
     * @throws IllegalArgumentException if application is not in SUCCESSFUL status
     */
    public void acceptApplication(Application app) {
        // check that chosen application is successful
        if (app.getStatus() != ApplicationStatus.SUCCESSFUL) {
            throw new IllegalArgumentException("Application is " + app.getStatus() + ", needs to be " + ApplicationStatus.SUCCESSFUL + " to be accepted.");
        }

        // Update status to ACCEPTED when student confirms placement
        app.setStatus(ApplicationStatus.ACCEPTED);
        // Withdraw all other applications
        applications.stream()
                .filter((a) -> a != app)
                .forEach((a) -> a.setStatus(ApplicationStatus.WITHDRAWN));
        // Update filled slots in internship
        app.getInternship().confirmPlacement();
    }

    /**
     * Withdraws an application with a specified reason.
     * @param app the application to withdraw
     * @param reason the reason for withdrawal
     */
    public void withdrawApplication(Application app, String reason) {
        app.requestWithdrawal(reason);
    }
    
    /**
     * Withdraws an application without specifying a reason.
     * @param app the application to withdraw
     */
    public void withdrawApplication(Application app) {
        app.requestWithdrawal((String) null);
    }

    /**
     * Gets the user type identifier.
     * @return "Student"
     */
    @Override
    public String getUserType() {
        return "Student";
    }
    
    /**
     * Gets the CSV filename for persisting student data.
     * @return the CSV file path for students
     */
    @Override
    public String getCsvFilename() {
        return "sample_file/sample_student_list.csv";
    }
    
    /**
     * Creates the dashboard strategy for student users.
     * Uses factory method pattern to eliminate instanceof checks.
     * 
     * @return StudentDashboardStrategy for this student
     */
    @Override
    public view.DashboardStrategy createDashboardStrategy() {
        return new view.StudentDashboardStrategy(this);
    }
    
    /**
     * Gets the student's current year of study.
     * @return year of study (1-5)
     */
    public int getYearOfStudy() { return yearOfStudy; }
    
    /**
     * Gets the student's academic major.
     * @return the major
     */
    public String getMajor() { return major; }

    /**
     * Sets the student's year of study.
     * @param yearOfStudy the year of study (must be 1-5)
     * @throws IllegalArgumentException if year is out of range
     */
    public void setYearOfStudy(int yearOfStudy) {
        ValidationHelper.validateRange(yearOfStudy, 1, 5, "Year of study");
        this.yearOfStudy = yearOfStudy;
    }
    
    /**
     * Sets the student's academic major.
     * @param major the new major (must not be empty)
     * @throws IllegalArgumentException if major is empty or null
     */
    public void setMajor(String major) {
        ValidationHelper.validateNotEmpty(major, "Major");
        this.major = major;
    }
}