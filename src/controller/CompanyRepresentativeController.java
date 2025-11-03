package Assignment.src.controller;

import Assignment.src.model.Internship;
import Assignment.src.model.CompanyRepresentative;
import Assignment.src.model.Application;
import Assignment.src.constant.InternshipStatus;
import Assignment.src.utils.InternshipFormatter;
import Assignment.src.utils.ApplicationFormatter;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class CompanyRepresentativeController extends BaseUserController {
    private final CompanyRepresentative companyRep;

    public CompanyRepresentativeController(CompanyRepresentative companyRep) {
        super(companyRep);
        this.companyRep = companyRep;
    }

    public List<String> listMyInternships() {
        List<Internship> internships = internshipController.getInternshipsByCreator(companyRep, filterSettings);
        return internships.stream()
                .map(i -> InternshipFormatter.formatAsRow(i, true))
                .collect(Collectors.toList());
    }
    
    public String createInternship(String title, String description, String level, String major,
                                  LocalDate openDate, LocalDate closeDate, int slots) {
        Internship created = internshipController.createInternship(title, description, level, major, 
                                                                   openDate, closeDate, companyRep, slots);
        return InternshipFormatter.formatDetails(created, "✓ INTERNSHIP CREATED SUCCESSFULLY", true);
    }

    public List<String> viewApplications(int internshipID) {
        validateOwnership(internshipID, companyRep, internshipController, "view applications for this internship");
        List<Application> applications = applicationController.getApplicationsForInternship(internshipID);
        return applications.stream()
                .sorted((a1, a2) -> Integer.compare(a1.getId(), a2.getId()))
                .map(app -> ApplicationFormatter.formatAsRow(app))
                .collect(Collectors.toList());
    }
    
    /**
     * Get applications for internship as Application objects (for nested paginated view)
     */
    public List<Application> getApplicationsForInternship(int internshipID) {
        validateOwnership(internshipID, companyRep, internshipController, "view applications for this internship");
        // Ensure applications are loaded and linked to internships (reload from CSV)
        Application.getAllApplications(); // This reloads applications and links them to internships
        List<Application> applications = applicationController.getApplicationsForInternship(internshipID, internshipController);
        return applications.stream()
                .sorted((a1, a2) -> a2.getDateApplied().toLocalDate().compareTo(a1.getDateApplied().toLocalDate())) // Descending by date
                .collect(Collectors.toList());
    }
    
    /**
     * Get detailed application information for viewing
     */
    public String getApplicationDetails(int internshipID, int applicationID) {
        validateOwnership(internshipID, companyRep, internshipController, "view application for this internship");
        Application application = applicationController.getApplicationsForInternship(internshipID)
                .stream()
                .filter(app -> app.getId() == applicationID)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Application not found!"));
        return ApplicationFormatter.formatDetails(application, "APPLICATION DETAILS", true);
    }
    
    public String getInternshipDetails(int internshipID) {
        validateOwnership(internshipID, companyRep, internshipController, "view this internship");
        return InternshipFormatter.formatDetails(
            internshipController.findInternship(internshipID), "CURRENT INTERNSHIP DETAILS", true);
    }
    
    public void editInternship(int internshipID, String title, String description, String level, 
                               String major, LocalDate openDate, LocalDate closeDate, Integer slots) {
        validateOwnershipAndEditability(internshipID, companyRep, internshipController, "edit");
        internshipController.updateInternship(internshipID, title, description, level, major, openDate, closeDate, slots);
    }
    
    public void deleteInternship(int internshipID) {
        validateOwnershipAndEditability(internshipID, companyRep, internshipController, "delete");
        internshipController.deleteInternship(internshipID);
    }
    
    public List<String> getApprovedInternships() {
        List<Internship> internships = internshipController.getApprovedInternshipsByCreator(companyRep);
        return toStringList(internships);
    }
    
    public void toggleVisibility(int internshipID) {
        validateOwnership(internshipID, companyRep, internshipController, "toggle visibility for this internship");
        validateApprovedStatus(internshipID, internshipController);
        internshipController.toggleInternshipVisibility(internshipID);
    }
    
    public void confirmPlacement(int internshipID, int applicationID) {
        validateOwnership(internshipID, companyRep, internshipController, "confirm placement for this internship");
        applicationController.confirmPlacement(internshipID, applicationID, internshipController);
    }
    
    /**
     * Reject application - Company Representative can reject PENDING applications
     * Sets status to UNSUCCESSFUL
     */
    public void rejectApplication(int internshipID, int applicationID) {
        validateOwnership(internshipID, companyRep, internshipController, "reject application for this internship");
        applicationController.rejectApplication(internshipID, applicationID, internshipController);
    }
    
    /**
     * Get internships for filter menu - avoids exposing model to view
     */
    public List<Internship> getInternshipsForFilter() {
        return internshipController.getInternshipsByCreator(companyRep, filterSettings);
    }
    
    /**
     * Get filtered internships created by this company representative
     * Default sort: ID (for company view)
     */
    public List<Internship> getInternships() {
        return internshipController.getInternshipsByCreator(companyRep, filterSettings);
    }
    
    /**
     * Edit company representative profile fields
     * Note: Status is admin-controlled, not editable by user
     */
    public void editProfile(String name, String email, String companyName, String department, String position) {
        if (name != null && !name.trim().isEmpty()) {
            companyRep.setName(name);
        }
        if (email != null && !email.trim().isEmpty()) {
            companyRep.setEmail(email);
        }
        if (companyName != null && !companyName.trim().isEmpty()) {
            companyRep.setCompanyName(companyName);
        }
        if (department != null && !department.trim().isEmpty()) {
            companyRep.setDepartment(department);
        }
        if (position != null && !position.trim().isEmpty()) {
            companyRep.setPosition(position);
        }
        // Note: Profile changes are in-memory only, not persisted to CSV
        // CSV updates would require UserRegistry.updateCompanyRepInCsv() method
    }
}
