package controller;

import model.*;
import utils.formatter.ApplicationFormatter;
import utils.formatter.InternshipFormatter;

import java.util.List;

/**
 * Controller for student-specific operations.
 * Handles internship browsing, application submission, and application management.
 * 
 * @author NTU SC2002 Group
 * @version 1.0
 * @since 2025-11-16
 */
public class StudentController extends BaseUserController {
    /** The student user associated with this controller */
    private final Student student;

    /**
     * Constructs a StudentController for the specified student.
     * 
     * @param student The student user
     */
    public StudentController(Student student) {
        super(student);
        this.student = student;
    }

    /**
     * Gets filtered internships visible to student, sorted by ID.
     * 
     * @return List of internships visible to the student, sorted by ID
     */
    public List<Internship> getInternships() {
        return internshipController.getVisibleInternshipsForStudent(student, filterSettings).stream()
                .sorted((i1, i2) -> Integer.compare(i1.getID(), i2.getID()))
                .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * Lists internships as formatted strings (no status column).
     * 
     * @return List of formatted internship strings
     */
    public List<String> listInternships() {
        return getInternships().stream()
                .map(i -> InternshipFormatter.formatAsRow(i, false))
                .collect(java.util.stream.Collectors.toList());
    }
    

    /**
     * Creates an application for the specified internship.
     * 
     * @param internshipID The ID of the internship to apply for
     * @return Formatted success message with application details, or null if failed
     */
    public String createApplication(int internshipID) {
        Application app = applicationController.createApplication(internshipID, student, internshipController);
        if (app == null) {
            return null;
        }
        return ApplicationFormatter.formatDetails(app, "✓ APPLICATION SUBMITTED SUCCESSFULLY", true);
    }

    /**
     * Requests withdrawal of an application.
     * 
     * @param applicationID The ID of the application to withdraw
     * @param reason Optional reason for withdrawal
     */
    public void withdrawApplication(int applicationID, String reason) {
        applicationController.withdrawApplication(applicationID, student, reason);
    }

    /**
     * Accepts a successful application.
     * Student confirms they will take the placement.
     * 
     * @param applicationID The ID of the application to accept
     */
    public void acceptApplication(int applicationID) {
        applicationController.acceptApplication(applicationID, student);
    }
    
    /**
     * Rejects a placement offer.
     * Student can reject SUCCESSFUL or ACCEPTED applications.
     * Sets status to UNSUCCESSFUL.
     * 
     * @param applicationID The ID of the application to reject
     */
    public void rejectPlacement(int applicationID) {
        applicationController.rejectPlacement(applicationID, student);
    }
    
    /**
     * Views all applications submitted by student, sorted by ID.
     * 
     * @return List of applications submitted by the student, sorted by ID
     */
    public List<Application> viewApplications() {
        return applicationController.getApplicationsForStudent(student).stream()
                .sorted((a1, a2) -> Integer.compare(a1.getId(), a2.getId()))
                .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * Gets detailed information about a specific application.
     * 
     * @param applicationID The ID of the application to view
     * @return Formatted application details
     */
    public String getApplicationDetails(int applicationID) {
        Application application = applicationController.findApplicationByID(applicationID, student);
        return ApplicationFormatter.formatDetails(application, "APPLICATION DETAILS", true);
    }
    
    /**
     * Gets detailed information about a specific internship.
     * Students can view internships they've applied for, even if visibility is turned off.
     * Listings only show internships matching their profile.
     * 
     * @param internshipID The ID of the internship to view
     * @return Formatted internship details
     */
    public String getInternshipDetails(int internshipID) {
        Internship internship = internshipController.findInternship(internshipID);
        
        // Use canStudentViewDetails which allows viewing applied internships
        if (!internship.canStudentViewDetails(student)) {
            throw new IllegalArgumentException("You don't have permission to view this internship!");
        }
        
        return InternshipFormatter.formatDetails(internship, "INTERNSHIP DETAILS", false);
    }
    
    
    /**
     * Edits student profile fields (null values skipped, in-memory only).
     * 
     * @param name New name (null to skip)
     * @param email New email (null to skip)
     * @param yearOfStudy New year of study (null to skip)
     * @param major New major (null to skip)
     */
    public void editProfile(String name, String email, Integer yearOfStudy, String major) {
        if (name != null && !name.trim().isEmpty()) student.setName(name);
        if (email != null && !email.trim().isEmpty()) student.setEmail(email);
        if (yearOfStudy != null) student.setYearOfStudy(yearOfStudy);
        if (major != null && !major.trim().isEmpty()) student.setMajor(major);
    }
}
