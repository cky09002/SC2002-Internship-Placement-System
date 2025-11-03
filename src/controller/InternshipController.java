package Assignment.src.controller;

import Assignment.src.model.*;
import Assignment.src.constant.InternshipStatus;
import Assignment.src.utils.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller class that handles all Internship-related logic.
 * Controllers should use this class instead of accessing Internship model directly.
 */
public class InternshipController {
    
    /**
     * Get all internships visible to a student based on their profile.
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
     * Overloaded method for backward compatibility (no filters)
     */
    public List<Internship> getVisibleInternshipsForStudent(Student student) {
        FilterSettings defaultSettings = new FilterSettings();
        return getVisibleInternshipsForStudent(student, defaultSettings);
    }
    
    /**
     * Get all internships created by a company representative
     * Supports filtering and sorting
     */
    public List<Internship> getInternshipsByCreator(CompanyRepresentative creator, FilterSettings filterSettings) {
        List<Internship> internships = Internship.getAllInternships().stream()
                .filter(i -> i.getCreator().equals(creator))
                .collect(Collectors.toList());
        @SuppressWarnings("unchecked")
        List<Internship> result = (List<Internship>) (List<?>) InternshipFilter.applyFilters(internships, filterSettings);
        return result;
    }
    
    /**
     * Overloaded method for backward compatibility
     */
    public List<Internship> getInternshipsByCreator(CompanyRepresentative creator) {
        FilterSettings defaultSettings = new FilterSettings();
        return getInternshipsByCreator(creator, defaultSettings);
    }
    
    /**
     * Get all approved internships created by a company representative
     */
    public List<Internship> getApprovedInternshipsByCreator(CompanyRepresentative creator) {
        return Internship.getAllInternships().stream()
                .filter(i -> i.getCreator().equals(creator))
                .filter(i -> i.getStatus() == InternshipStatus.APPROVED)
                .collect(Collectors.toList());
    }
    
    /**
     * Get all pending internships
     */
    public List<Internship> getPendingInternships() {
        return Internship.getAllInternships().stream()
                .filter(i -> i.getStatus() == InternshipStatus.PENDING)
                .collect(Collectors.toList());
    }
    
    
    /**
     * Create a new internship
     * Validates data and enforces maximum limit per company representative
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
        InternshipCsvHandler.saveToCsv(internship);
        return internship;
    }

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
    
    public Internship findInternship(int internshipID) {
        Internship internship = Internship.findWithID(internshipID);
        if (internship == null) throw new IllegalArgumentException("Internship not found!");
        return internship;
    }
    
    public void updateInternship(int internshipID, String title, String description, String level, 
                                 String major, LocalDate openDate, LocalDate closeDate, Integer slots) {
        Internship i = findInternship(internshipID);
        i.updateDetails(title != null ? title : i.getTitle(), description != null ? description : i.getDescription(),
            level != null ? level : i.getLevel(), major != null ? major : i.getPreferredMajor(),
            openDate != null ? openDate : i.getOpenDate(), closeDate != null ? closeDate : i.getCloseDate(),
            slots != null ? slots : i.getNumSlots());
    }
    
    public void deleteInternship(int internshipID) { findInternship(internshipID).delete(); }
    public void toggleInternshipVisibility(int internshipID) { findInternship(internshipID).toggleVisibility(); }
    public void updateInternshipApproval(int internshipID, boolean approve) {
        findInternship(internshipID).setStatus(approve ? InternshipStatus.APPROVED : InternshipStatus.REJECTED);
    }
    public boolean isInternshipOwnedBy(int internshipID, CompanyRepresentative companyRep) {
        return findInternship(internshipID).isOwnedBy(companyRep);
    }
    public boolean canEditInternship(int internshipID) { return findInternship(internshipID).canEdit(); }
    public InternshipStatus getInternshipStatus(int internshipID) { return findInternship(internshipID).getStatus(); }
    
    /**
     * Get all internships (for staff/admin views with filtering)
     */
    public List<Internship> getAllInternships(FilterSettings filterSettings) {
        List<Internship> all = Internship.getAllInternships();
        @SuppressWarnings("unchecked")
        List<Internship> result = (List<Internship>) (List<?>) InternshipFilter.applyFilters(all, filterSettings);
        return result;
    }
    
    /**
     * Get all internships (no filters)
     */
    public List<Internship> getAllInternships() {
        return Internship.getAllInternships();
    }
}

