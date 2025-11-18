package controller;

import model.*;
import utils.formatter.InternshipFormatter;
import utils.formatter.ApplicationFormatter;
import controller.interfaces.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for company representative-specific operations.
 * Handles internship creation, editing, deletion, visibility toggling,
 * and application management (view, approve, reject, confirm placement).
 * 
 * @author NTU SC2002 Group
 * @version 1.0
 * @since 2025-11-16
 */
public class CompanyRepresentativeController extends BaseUserController {
    /** The company representative user associated with this controller */
    private final CompanyRepresentative companyRep;

    /**
     * Constructs a CompanyRepresentativeController for the specified company representative.
     * 
     * @param companyRep The company representative user
     */
    public CompanyRepresentativeController(CompanyRepresentative companyRep) {
        super(companyRep);
        this.companyRep = companyRep;
    }

    /**
     * Lists internships created by this rep (with status/visibility).
     * 
     * @return List of formatted internship strings with status information
     */
    public List<String> listMyInternships() {
        internshipController.loadInternshipsFromCsv();
        return internshipController.getInternshipsByCreator(companyRep, filterSettings).stream()
                .map(i -> InternshipFormatter.formatAsRow(i, true))
                .collect(Collectors.toList());
    }
    
    /**
     * Creates a new internship with the specified details.
     * 
     * @param title Internship title
     * @param description Internship description
     * @param level Difficulty level
     * @param major Preferred major
     * @param openDate Opening date
     * @param closeDate Closing date
     * @param slots Number of slots
     * @return Formatted success message with internship details
     */
    public String createInternship(String title, String description, String level, String major,
                                  LocalDate openDate, LocalDate closeDate, int slots) {
        Internship created = internshipController.createInternship(title, description, level, major, 
                                                                   openDate, closeDate, companyRep, slots);
        return InternshipFormatter.formatDetails(created, "✓ INTERNSHIP CREATED SUCCESSFULLY", true);
    }

    /**
     * Views all applications for a specific internship.
     * Only the owner can view applications.
     * 
     * @param internshipID The ID of the internship
     * @return List of formatted application strings
     */
    public List<String> viewApplications(int internshipID) {
        validateOwnership(internshipID, companyRep, (InternshipValidator) internshipController, "view applications for this internship");
        List<Application> applications = applicationController.getApplicationsForInternship(internshipID, internshipController);
        return applications.stream()
                .sorted((a1, a2) -> Integer.compare(a1.getId(), a2.getId()))
                .map(app -> ApplicationFormatter.formatAsRow(app))
                .collect(Collectors.toList());
    }
    
    /**
     * Gets applications for an internship as Application objects.
     * Used for nested paginated view. Only the owner can access.
     * 
     * @param internshipID The ID of the internship
     * @return List of Application objects sorted by date (descending)
     */
    public List<Application> getApplicationsForInternship(int internshipID) {
        validateOwnership(internshipID, companyRep, (InternshipValidator) internshipController, "view applications for this internship");
        // Ensure applications are loaded and linked to internships (reload from CSV)
        applicationController.loadApplicationsFromCsv(internshipController);
        List<Application> applications = applicationController.getApplicationsForInternship(internshipID, internshipController);
        return applications.stream()
                .sorted((a1, a2) -> a2.getDateApplied().toLocalDate().compareTo(a1.getDateApplied().toLocalDate())) // Descending by date
                .collect(Collectors.toList());
    }
    
    /**
     * Gets detailed information about a specific application.
     * Only the internship owner can view application details.
     * 
     * @param internshipID The ID of the internship
     * @param applicationID The ID of the application
     * @return Formatted application details
     */
    public String getApplicationDetails(int internshipID, int applicationID) {
        validateOwnership(internshipID, companyRep, (InternshipValidator) internshipController, "view application for this internship");
        Application application = applicationController.getApplicationsForInternship(internshipID, internshipController)
                .stream()
                .filter(app -> app.getId() == applicationID)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Application not found!"));
        return ApplicationFormatter.formatDetails(application, "APPLICATION DETAILS", true);
    }
    
    /**
     * Gets detailed information about a specific internship.
     * Only the owner can view internship details.
     * 
     * @param internshipID The ID of the internship
     * @return Formatted internship details
     */
    public String getInternshipDetails(int internshipID) {
        validateOwnership(internshipID, companyRep, (InternshipValidator) internshipController, "view this internship");
        return InternshipFormatter.formatDetails(
            internshipController.findInternship(internshipID), "CURRENT INTERNSHIP DETAILS", true);
    }
    
    /**
     * Validates if the user has permission to edit an internship.
     * Checks ownership and editability (PENDING or REJECTED status only).
     * 
     * @param internshipID The ID of the internship
     * @throws IllegalArgumentException if validation fails
     */
    public void validateEditPermission(int internshipID) {
        validateOwnershipAndEditability(internshipID, companyRep, (InternshipValidator) internshipController, "edit");
    }
    
    /**
     * Edits an existing internship.
     * Only pending internships can be edited, and only by the owner.
     * 
     * @param internshipID The ID of the internship to edit
     * @param title New title (null to skip)
     * @param description New description (null to skip)
     * @param level New level (null to skip)
     * @param major New preferred major (null to skip)
     * @param openDate New opening date (null to skip)
     * @param closeDate New closing date (null to skip)
     * @param slots New number of slots (null to skip)
     */
    public void editInternship(int internshipID, String title, String description, String level, 
                               String major, LocalDate openDate, LocalDate closeDate, Integer slots) {
        validateOwnershipAndEditability(internshipID, companyRep, (InternshipValidator) internshipController, "edit");
        internshipController.updateInternship(internshipID, title, description, level, major, openDate, closeDate, slots);
    }
    
    /**
     * Deletes an internship.
     * Only pending internships can be deleted, and only by the owner.
     * 
     * @param internshipID The ID of the internship to delete
     */
    public void deleteInternship(int internshipID) {
        validateOwnershipAndEditability(internshipID, companyRep, (InternshipValidator) internshipController, "delete");
        internshipController.deleteInternship(internshipID);
    }
    
    /**
     * Gets all approved internships created by this company representative.
     * 
     * @return List of formatted internship strings
     */
    public List<String> getApprovedInternships() {
        List<Internship> internships = internshipController.getApprovedInternshipsByCreator(companyRep);
        return toStringList(internships);
    }
    
    /**
     * Toggles the visibility of an internship.
     * Only approved internships can have visibility toggled, and only by the owner.
     * 
     * @param internshipID The ID of the internship
     * @return true if now visible, false if now invisible
     */
    public boolean toggleVisibility(int internshipID) {
        validateOwnership(internshipID, companyRep, (InternshipValidator) internshipController, "toggle visibility for this internship");
        validateApprovedStatus(internshipID, (InternshipValidator) internshipController);
        return internshipController.toggleInternshipVisibility(internshipID);
    }
    
    /**
     * Confirms a student placement.
     * Approves PENDING applications or confirms SUCCESSFUL applications.
     * Only the internship owner can confirm placements.
     * 
     * @param internshipID The ID of the internship
     * @param applicationID The ID of the application
     */
    public void confirmPlacement(int internshipID, int applicationID) {
        validateOwnership(internshipID, companyRep, (InternshipValidator) internshipController, "confirm placement for this internship");
        applicationController.confirmPlacement(internshipID, applicationID, internshipController);
    }
    
    /**
     * Rejects an application.
     * Company representatives can reject PENDING applications.
     * Sets status to UNSUCCESSFUL.
     * 
     * @param internshipID The ID of the internship
     * @param applicationID The ID of the application
     */
    public void rejectApplication(int internshipID, int applicationID) {
        validateOwnership(internshipID, companyRep, (InternshipValidator) internshipController, "reject application for this internship");
        applicationController.rejectApplication(internshipID, applicationID, internshipController);
    }
    
    /**
     * Gets internships for filter menu.
     * Avoids exposing model to view layer.
     * 
     * @return List of Internship objects
     */
    public List<Internship> getInternshipsForFilter() {
        return internshipController.getInternshipsByCreator(companyRep, filterSettings);
    }
    
    /**
     * Gets filtered internships created by this company representative.
     * Sorted by Status (PENDING → APPROVED → REJECTED → FILLED), then by ID.
     * Reloads data from CSV to ensure fresh data.
     * 
     * @return List of Internship objects sorted by status and ID
     */
    public List<Internship> getInternships() {
        internshipController.loadInternshipsFromCsv();
        return internshipController.getInternshipsByCreator(companyRep, filterSettings).stream()
                .sorted((i1, i2) -> {
                    int statusCompare = i1.getStatus().compareTo(i2.getStatus());
                    return statusCompare != 0 ? statusCompare : Integer.compare(i1.getID(), i2.getID());
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Edits company rep profile fields (null values skipped, in-memory only).
     * 
     * @param name New name (null to skip)
     * @param email New email (null to skip)
     * @param companyName New company name (null to skip)
     * @param department New department (null to skip)
     * @param position New position (null to skip)
     */
    public void editProfile(String name, String email, String companyName, String department, String position) {
        if (name != null && !name.trim().isEmpty()) companyRep.setName(name);
        if (email != null && !email.trim().isEmpty()) companyRep.setEmail(email);
        if (companyName != null && !companyName.trim().isEmpty()) companyRep.setCompanyName(companyName);
        if (department != null && !department.trim().isEmpty()) companyRep.setDepartment(department);
        if (position != null && !position.trim().isEmpty()) companyRep.setPosition(position);
    }
}
