package controller;

import model.*;
import constant.InternshipStatus;
import utils.filter.*;
import utils.csv.*;
import utils.validation.ValidationHelper;
import controller.interfaces.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller class that handles all Internship-related logic.
 * Controllers should use this class instead of accessing Internship model directly.
 * 
 * Follows Dependency Inversion Principle by depending on CsvHandler interface abstraction.
 * Implements InternshipControllerInterface to allow clients to depend on abstraction.
 * 
 * @author NTU SC2002 Group
 * @version 1.0
 * @since 2025-11-16
 */
public class InternshipController implements InternshipControllerInterface {
    /** CSV handler for persistence operations */
    private final CsvHandler<Internship> csvHandler;
    
    /**
     * Constructs an InternshipController with the specified CSV handler.
     * Follows Dependency Inversion Principle - depends on abstraction, not concretion.
     * 
     * @param csvHandler The CSV handler for persistence operations
     */
    public InternshipController(CsvHandler<Internship> csvHandler) {
        this.csvHandler = csvHandler;
    }
    
    /**
     * Default constructor for backward compatibility.
     * Uses singleton InternshipCsvHandler instance.
     */
    public InternshipController() {
        this(InternshipCsvHandler.getInstance());
    }
    
    /**
     * Retrieves all internships visible to a student based on their profile.
     * Filters internships based on:
     * 1. Visibility toggle (must be visible AND approved)
     * 2. Major match (student's major must match internship's preferred major)
     * 3. Level eligibility (year of study):
     *    - Basic: Any year
     *    - Intermediate: Year 3 or higher
     *    - Advanced: Year 3 or higher
     * 4. Date range (must be within open and close dates)
     * 5. User-applied filters (Status, Major, Level, Closing Date)
     * Default sort: alphabetical by title
     * 
     * @param student The student for visibility filtering
     * @param filterSettings Additional filter settings to apply
     * @return List of visible internships matching all criteria
     */
    public List<Internship> getVisibleInternshipsForStudent(Student student, FilterSettings filterSettings) {
        List<Internship> visible = Internship.getAllInternships().stream()
                .filter(i -> i.isVisibleToStudent(student))
                .collect(Collectors.toList());
        @SuppressWarnings("unchecked")
        List<Internship> result = (List<Internship>) (List<?>) InternshipFilter.applyFilters(visible, filterSettings);
        return result;
    }
    
    /**
     * Retrieves all visible internships for a student (overloaded without filters).
     * Uses default filter settings.
     * 
     * @param student The student for visibility filtering
     * @return List of visible internships
     */
    public List<Internship> getVisibleInternshipsForStudent(Student student) {
        FilterSettings defaultSettings = new FilterSettings();
        return getVisibleInternshipsForStudent(student, defaultSettings);
    }
    
    /**
     * Retrieves all internships created by a company representative.
     * Supports filtering and sorting through filter settings.
     * 
     * @param creator The company representative who created the internships
     * @param filterSettings Filter settings to apply
     * @return List of internships created by the representative
     */
    public List<Internship> getInternshipsByCreator(CompanyRepresentative creator, FilterSettings filterSettings) {
        model.Application.getAllApplications();
        List<Internship> internships = model.Internship.getInternshipsList().stream()
                .filter(i -> i.getCreator().equals(creator))
                .collect(Collectors.toList());
        @SuppressWarnings("unchecked")
        List<Internship> result = (List<Internship>) (List<?>) InternshipFilter.applyFilters(internships, filterSettings);
        return result;
    }
    
    /**
     * Retrieves internships by creator (overloaded without filters).
     * Uses default filter settings.
     * 
     * @param creator The company representative who created the internships
     * @return List of internships created by the representative
     */
    public List<Internship> getInternshipsByCreator(CompanyRepresentative creator) {
        FilterSettings defaultSettings = new FilterSettings();
        return getInternshipsByCreator(creator, defaultSettings);
    }
    
    /**
     * Retrieves all approved internships created by a company representative.
     * Only includes internships with APPROVED status.
     * 
     * @param creator The company representative who created the internships
     * @return List of approved internships
     */
    public List<Internship> getApprovedInternshipsByCreator(CompanyRepresentative creator) {
        return Internship.getAllInternships().stream()
                .filter(i -> i.getCreator().equals(creator))
                .filter(i -> i.getStatus() == InternshipStatus.APPROVED)
                .collect(Collectors.toList());
    }
    
    /**
     * Retrieves all internships pending staff approval.
     * 
     * @return List of internships with PENDING status
     */
    public List<Internship> getPendingInternships() {
        return Internship.getAllInternships().stream()
                .filter(i -> i.getStatus() == InternshipStatus.PENDING)
                .collect(Collectors.toList());
    }
    
    
    /**
     * Creates a new internship listing.
     * Validates data and enforces maximum limit per company representative.
     * 
     * @param title The internship title
     * @param description The internship description
     * @param level The difficulty level (Basic/Intermediate/Advanced)
     * @param major The preferred major for applicants
     * @param openDate The opening date for applications
     * @param closeDate The closing date for applications
     * @param creator The company representative creating the internship
     * @param slots The number of available slots (1-10)
     * @return The created Internship object
     * @throws IllegalArgumentException if validation fails or max limit exceeded
     */
    public Internship createInternship(String title, String description, String level, String major,
                                       LocalDate openDate, LocalDate closeDate, CompanyRepresentative creator, int slots) {
        // Validate company representative is approved
        if (!creator.isApproved()) {
            throw new IllegalArgumentException("Company representative must be approved to create internships.");
        }
        
        // Validate maximum limit per company representative
        long existingCount = getInternshipsByCreator(creator).size();
        if (existingCount >= CompanyRepresentative.MAX_INTERNSHIPS) {
            throw new IllegalArgumentException("Maximum of " + CompanyRepresentative.MAX_INTERNSHIPS + 
                " internships allowed per company representative.");
        }
        
        // Validate input data
        validateInternshipData(title, description, level, major, openDate, closeDate, slots);
        
        Internship internship = new Internship(
            title, 
            description, 
            level, 
            major, 
            openDate, 
            closeDate, 
            creator.getCompanyName(), 
            creator, 
            slots
        );
        csvHandler.saveToCsv(internship);
        return internship;
    }

    /**
     * Validates internship data before creation or update.
     * 
     * @param title The internship title
     * @param description The internship description
     * @param level The difficulty level
     * @param major The preferred major
     * @param openDate The opening date
     * @param closeDate The closing date
     * @param slots The number of slots
     * @throws IllegalArgumentException if any validation fails
     */
    private void validateInternshipData(String title, String description, String level, String major,
                                       LocalDate openDate, LocalDate closeDate, int slots) {
        ValidationHelper.validateNotEmpty(title, "Title");
        ValidationHelper.validateNotEmpty(description, "Description");
        ValidationHelper.validateNotEmpty(level, "Level");
        ValidationHelper.validateNotEmpty(major, "Major");
        
        String levelUpper = level.trim();
        if (!levelUpper.equalsIgnoreCase("Basic") && !levelUpper.equalsIgnoreCase("Intermediate") 
            && !levelUpper.equalsIgnoreCase("Advanced")) {
            throw new IllegalArgumentException("Level must be Basic, Intermediate, or Advanced.");
        }
        
        if (openDate == null || closeDate == null) throw new IllegalArgumentException("Dates cannot be null.");
        if (closeDate.isBefore(openDate)) throw new IllegalArgumentException("Close date must be after open date.");
        
        ValidationHelper.validateRange(slots, 1, 10, "Number of slots");
    }
    
    /**
     * Finds an internship by its ID.
     * 
     * @param internshipID The ID of the internship to find
     * @return The found Internship object
     * @throws IllegalArgumentException if internship not found
     */
    public Internship findInternship(int internshipID) {
        Internship internship = Internship.findWithID(internshipID);
        if (internship == null) throw new IllegalArgumentException("Internship not found!");
        return internship;
    }
    
    /**
     * Updates an existing internship's details.
     * Null parameters retain existing values.
     * 
     * @param internshipID The ID of the internship to update
     * @param title New title (null to keep existing)
     * @param description New description (null to keep existing)
     * @param level New level (null to keep existing)
     * @param major New preferred major (null to keep existing)
     * @param openDate New opening date (null to keep existing)
     * @param closeDate New closing date (null to keep existing)
     * @param slots New slot count (null to keep existing)
     * @throws IllegalArgumentException if internship not found
     */
    public void updateInternship(int internshipID, String title, String description, String level, 
                                 String major, LocalDate openDate, LocalDate closeDate, Integer slots) {
        Internship i = findInternship(internshipID);
        // If editing a REJECTED internship, reset to PENDING for resubmission
        if (i.getStatus() == InternshipStatus.REJECTED) {
            i.setStatus(InternshipStatus.PENDING);
            csvHandler.saveToCsv(i);
        }
        i.updateDetails(title != null ? title : i.getTitle(), description != null ? description : i.getDescription(),
            level != null ? level : i.getLevel(), major != null ? major : i.getPreferredMajor(),
            openDate != null ? openDate : i.getOpenDate(), closeDate != null ? closeDate : i.getCloseDate(),
            slots != null ? slots : i.getNumSlots());
        csvHandler.saveToCsv(i);
    }
    
    /**
     * Deletes an internship.
     * 
     * @param internshipID The ID of the internship to delete
     * @throws IllegalArgumentException if internship not found or cannot be deleted
     */
    public void deleteInternship(int internshipID) {
        Internship internship = findInternship(internshipID);
        internship.delete();
        // deleteFromCsv is only available if handler implements CsvDeletable
        if (csvHandler instanceof CsvDeletable) {
            ((CsvDeletable<?>) csvHandler).deleteFromCsv(internshipID);
        }
    }
    /**
     * Toggles the visibility of an internship.
     * 
     * @param internshipID The ID of the internship
     * @return true if now visible, false if now invisible
     * @throws IllegalArgumentException if internship not found
     */
    public boolean toggleInternshipVisibility(int internshipID) {
        Internship internship = findInternship(internshipID);
        boolean result = internship.toggleVisibility();
        csvHandler.saveToCsv(internship);
        return result;
    }
    /**
     * Updates internship approval status.
     * 
     * @param internshipID The ID of the internship
     * @param approve true to approve, false to reject
     * @throws IllegalArgumentException if internship not found
     */
    public void updateInternshipApproval(int internshipID, boolean approve) {
        Internship internship = findInternship(internshipID);
        internship.setStatus(approve ? InternshipStatus.APPROVED : InternshipStatus.REJECTED);
        csvHandler.saveToCsv(internship);
    }
    
    /**
     * Loads all internships from CSV file.
     * Clears existing data and reloads from disk.
     */
    public void loadInternshipsFromCsv() {
        Internship.clearAll();
        csvHandler.loadFromCsv();
    }
    
    /**
     * Saves an internship to CSV.
     * Helper method for other controllers that need to save internships.
     * 
     * @param internship The internship to save
     */
    public void saveInternship(Internship internship) {
        csvHandler.saveToCsv(internship);
    }
    /**
     * Checks if an internship is owned by a company representative.
     * 
     * @param internshipID The ID of the internship
     * @param companyRep The company representative to check
     * @return true if owned by the representative, false otherwise
     * @throws IllegalArgumentException if internship not found
     */
    public boolean isInternshipOwnedBy(int internshipID, CompanyRepresentative companyRep) {
        return findInternship(internshipID).isOwnedBy(companyRep);
    }
    /**
     * Checks if an internship can be edited.
     * 
     * @param internshipID The ID of the internship
     * @return true if editable, false otherwise
     * @throws IllegalArgumentException if internship not found
     */
    public boolean canEditInternship(int internshipID) { return findInternship(internshipID).canEdit(); }
    /**
     * Gets the status of an internship.
     * 
     * @param internshipID The ID of the internship
     * @return The InternshipStatus
     * @throws IllegalArgumentException if internship not found
     */
    public InternshipStatus getInternshipStatus(int internshipID) { return findInternship(internshipID).getStatus(); }
    
    /**
     * Retrieves all internships with filtering (for staff/admin views).
     * 
     * @param filterSettings Filter settings to apply
     * @return List of filtered internships
     */
    public List<Internship> getAllInternships(FilterSettings filterSettings) {
        List<Internship> all = Internship.getAllInternships();
        @SuppressWarnings("unchecked")
        List<Internship> result = (List<Internship>) (List<?>) InternshipFilter.applyFilters(all, filterSettings);
        return result;
    }
    
    /**
     * Retrieves all internships without filtering.
     * 
     * @return List of all internships in the system
     */
    public List<Internship> getAllInternships() {
        return Internship.getAllInternships();
    }
}


