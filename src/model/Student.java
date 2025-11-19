package model;

import utils.validation.ValidationHelper;

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
    public view.interfaces.DashboardStrategy createDashboardStrategy() {
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