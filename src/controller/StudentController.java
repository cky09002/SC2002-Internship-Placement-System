package Assignment.src.controller;

import Assignment.src.model.Application;
import Assignment.src.model.Internship;
import Assignment.src.model.Student;
import Assignment.src.utils.*;

import java.util.List;

public class StudentController extends BaseUserController {
    private final Student student;

    public StudentController(Student student) {
        super(student);
        this.student = student;
    }

    @Override
    public List<String> listInternships() {
        // Use filter settings that persist across menu pages
        List<Internship> internships = getInternships();
        // Format internships for students (no Status or Visible shown)
        return internships.stream()
                .map(i -> InternshipFormatter.formatAsRow(i, false))
                .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * Get filtered internships visible to student
     */
    @Override
    public List<Internship> getInternships() {
        return internshipController.getVisibleInternshipsForStudent(student, filterSettings);
    }
    

    public String createApplication(int internshipID) {
        Application app = applicationController.createApplication(internshipID, student, internshipController);
        if (app == null) {
            return null;
        }
        return ApplicationFormatter.formatDetails(app, "✓ APPLICATION SUBMITTED SUCCESSFULLY", true);
    }

    public void withdrawApplication(int applicationID, String reason) {
        applicationController.withdrawApplication(applicationID, student, reason);
    }

    public void acceptApplication(int applicationID) {
        applicationController.acceptApplication(applicationID, student);
    }
    
    /**
     * Reject placement - Student can reject SUCCESSFUL or ACCEPTED applications
     * Sets status to UNSUCCESSFUL
     */
    public void rejectPlacement(int applicationID) {
        applicationController.rejectPlacement(applicationID, student);
    }
    
    public List<Application> viewApplications() {
        List<Application> applications = applicationController.getApplicationsForStudent(student);
        return applications.stream()
                .sorted((a1, a2) -> Integer.compare(a1.getId(), a2.getId()))
                .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * Get formatted application list as strings (for backward compatibility)
     */
    public List<String> viewApplicationsAsStrings() {
        return viewApplications().stream()
                .map(app -> ApplicationFormatter.formatAsRow(app))
                .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * Get detailed application information for viewing
     */
    public String getApplicationDetails(int applicationID) {
        Application application = applicationController.findApplicationByID(applicationID, student);
        return ApplicationFormatter.formatDetails(application, "APPLICATION DETAILS", true);
    }
    
    /**
     * Get detailed internship information for viewing
     * Students can view internships they've applied for, even if visibility is turned off
     * But listings should only show internships that match their profile
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
     * Edit student profile fields
     */
    @Override
    public void editProfile(String name, String email, Integer yearOfStudy, String major) {
        if (name != null && !name.trim().isEmpty()) {
            student.setName(name);
        }
        if (email != null && !email.trim().isEmpty()) {
            student.setEmail(email);
        }
        if (yearOfStudy != null) {
            student.setYearOfStudy(yearOfStudy);
        }
        if (major != null && !major.trim().isEmpty()) {
            student.setMajor(major);
        }
        // Note: Profile changes are in-memory only, not persisted to CSV
        // CSV updates would require UserRegistry.saveUserToCsv() method
    }
}
