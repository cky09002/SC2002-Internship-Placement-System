package model;

import constant.*;
import utils.formatter.InternshipFormatter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an internship opportunity in the system.
 * Manages internship details, applications, visibility, and status.
 * Provides methods for validation and filtering.
 * 
 * Note: CSV persistence is handled by controllers, not models (MVC compliance).
 * 
 * @author NTU SC2002 Group
 * @version 1.0
 * @since 2025-11-16
 */
public class Internship {
    /** Static list of all internships in the system */
    private static final List<Internship> internships = new ArrayList<>();
    
    /** Next available internship ID (auto-incrementing) */
    private static int nextID = 100000;

    /**
     * Get all internships from in-memory list.
     * Note: Loading from CSV is handled by controllers/repositories (MVC compliance).
     * @return list of all internships
     */
    public static List<Internship> getAllInternships() {
        return new ArrayList<>(internships);
    }
    
    /**
     * Clears all internships from memory.
     * Used by CSV handlers when reloading data.
     */
    public static void clearAll() {
        for (Internship internship : new ArrayList<>(internships)) {
            internship.getApplications().clear();
        }
        internships.clear();
    }

    /** Unique internship ID */
    private final int id;
    
    /** Internship title */
    private String title;
    
    /** Detailed description of the internship */
    private String description;
    
    /** Difficulty level: Basic, Intermediate, or Advanced */
    private String level;
    
    /** Preferred major for applicants */
    private String preferredMajor;
    
    /** Opening date for applications */
    private LocalDate openDate;
    
    /** Closing date for applications */
    private LocalDate closeDate;
    
    /** Name of the company offering this internship */
    private String companyName;
    
    /** Company representative who created this internship */
    private CompanyRepresentative creator;
    
    /** Visibility flag (toggled by company representative) */
    private boolean visible;
    
    /** Total number of available slots */
    private int numSlots;
    
    /** Number of filled/confirmed slots */
    private int filledSlots;
    
    /** Current status of the internship */
    private InternshipStatus status;
    
    /** List of applications submitted for this internship */
    private ArrayList<Application> applications;

    /**
     * Constructs a new Internship with the specified details.
     * Automatically assigns a unique ID and adds to the global internship list.
     * 
     * @param title The internship title
     * @param description Detailed description
     * @param level Difficulty level (Basic, Intermediate, Advanced)
     * @param major Preferred major
     * @param open Opening date for applications
     * @param close Closing date for applications
     * @param company Company name
     * @param creator Company representative who created this
     * @param slots Number of available slots
     */
    public Internship(String title, String description, String level, String major,
                      LocalDate open, LocalDate close, String company,
                      CompanyRepresentative creator, int slots) {
        this(nextID++, title, description, level, major, open, close, company, creator, slots,
             false, InternshipStatus.PENDING, 0);
        internships.add(this);
    }

    // Getters
    /**
     * Gets the internship title.
     * @return the internship title
     */
    public String getTitle() { return title; }
    
    /**
     * Gets the internship description.
     * @return the internship description
     */
    public String getDescription() { return description; }
    
    /**
     * Gets the difficulty level.
     * @return the difficulty level
     */
    public String getLevel() { return level; }
    
    /**
     * Gets the preferred major for this internship.
     * @return the preferred major
     */
    public String getPreferredMajor() { return preferredMajor; }
    
    /**
     * Gets the application opening date.
     * @return the opening date
     */
    public LocalDate getOpenDate() { return openDate; }
    
    /**
     * Gets the application closing date.
     * @return the closing date
     */
    public LocalDate getCloseDate() { return closeDate; }
    
    /**
     * Gets the company name.
     * @return the company name
     */
    public String getCompanyName() { return companyName; }
    
    /**
     * Gets the company representative who created this internship.
     * @return the company representative who created this
     */
    public CompanyRepresentative getCreator() { return creator; }
    
    /**
     * Gets the current internship status.
     * @return the current status
     */
    public InternshipStatus getStatus() { return status; }
    
    /**
     * Checks if internship is visible to users.
     * An internship is visible only if both visibility flag is true AND status is APPROVED.
     * 
     * @return true if visible and approved, false otherwise
     */
    public boolean isVisible() { return visible && status == InternshipStatus.APPROVED; }
    
    /**
     * Check if internship should be visible in listings for a student.
     * Students should NOT see internships with:
     * - Filled status (all slots taken)
     * - Wrong major (doesn't match student's major)
     * - Wrong level (not eligible for student's year of study)
     * - Visibility off (unless they've applied)
     * 
     * Applied internships are still visible in listings only if they meet profile requirements.
     * @param student the student to check visibility for
     * @return true if visible to the student, false otherwise
     */
    public boolean isVisibleToStudent(Student student) {
        // Students cannot view filled internships
        if (isFilled()) return false;
        
        // Must be visible (unless student has applied - handled separately for viewing details)
        if (!isVisible()) {
            // If visibility is off, check if student has applied (for detail viewing permission)
            // But for listings, we still require visibility to be on
            return false;
        }
        
        // Check major match - must match student's major
        if (preferredMajor == null || student.getMajor() == null || 
            !preferredMajor.equalsIgnoreCase(student.getMajor())) {
            return false;
        }
        
        // Check level eligibility based on year of study
        int year = student.getYearOfStudy();
        if (level != null) {
            // Intermediate and Advanced require Year 3 or higher
            // Basic: Any year
            if (level.equalsIgnoreCase("Intermediate") && year < 3) {
                return false;
            }
            if (level.equalsIgnoreCase("Advanced") && year < 3) {
                return false;
            }
        }
        
        // Check if internship is open (within date range)
        LocalDate now = LocalDate.now();
        return !now.isBefore(openDate) && !now.isAfter(closeDate);
    }
    
    /**
     * Check if a student can view details of this internship.
     * Allows viewing if:
     * 1. Internship is visible to student (normal rules), OR
     * 2. Student has applied for it (can view even if visibility is off).
     * @param student the student to check
     * @return true if student can view details, false otherwise
     */
    public boolean canStudentViewDetails(Student student) {
        // Can view if normally visible
        if (isVisibleToStudent(student)) {
            return true;
        }
        // Can view if student has applied (even if visibility is off)
        return hasStudentApplied(student);
    }
    
    /**
     * Check if a student has applied for this internship.
     * Used to allow students to view internships they've applied for even if visibility is off.
     * @param student the student to check
     * @return true if student has applied, false otherwise
     */
    public boolean hasStudentApplied(Student student) {
        if (applications == null) return false;
        return applications.stream()
                .anyMatch(app -> app.getApplicant().equals(student));
    }
    
    /**
     * Checks if a student is eligible to apply for this internship.
     * Delegates to isVisibleToStudent for eligibility checks.
     * 
     * @param student The student to check eligibility for
     * @return true if eligible, false otherwise
     */
    public boolean isEligibleForStudent(Student student) {
        return isVisibleToStudent(student);
    }
    
    /**
     * Toggles the visibility of this internship.
     * Can only toggle if status is APPROVED.
     * Note: CSV persistence is handled by controllers (MVC compliance).
     * @return true if now visible, false if now invisible
     */
    public boolean toggleVisibility() {
        if (status == InternshipStatus.APPROVED) {
            visible = !visible;
        }
        return visible;
    }
    
    /**
     * Check if internship can be edited.
     * Only PENDING and REJECTED internships can be edited.
     * 
     * @return true if status is PENDING or REJECTED, false otherwise
     */
    public boolean canEdit() {
        return status == InternshipStatus.PENDING || status == InternshipStatus.REJECTED;
    }
    
    /**
     * Check if internship is filled (all slots taken)
     * @return true if all slots are filled, false otherwise
     */
    public boolean isFilled() {
        return status == InternshipStatus.FILLED || (status == InternshipStatus.APPROVED && filledSlots >= numSlots);
    }

    /**
     * Gets the total number of slots.
     * @return the total number of slots
     */
    public int getNumSlots() { return numSlots; }
    
    /**
     * Gets the number of filled slots.
     * @return the number of filled slots
     */
    public int getFilledSlots() { return filledSlots; }
    
    /**
     * Gets the list of applications for this internship.
     * @return the list of applications
     */
    public ArrayList<Application> getApplications() { return applications; }
    
    /**
     * Gets the count of pending applications for this internship.
     * @return the number of applications with PENDING status
     */
    public long getPendingApplicationsCount() {
        return applications.stream()
                .filter(app -> app.getStatus() == constant.ApplicationStatus.PENDING)
                .count();
    }
    
    /**
     * Gets the unique internship ID.
     * @return the unique internship ID
     */
    public int getID() { return id; }

    // Status and visibility control
    /**
     * Sets the status of this internship.
     * Automatically updates visibility based on status.
     * Note: CSV persistence is handled by controllers (MVC compliance).
     * 
     * @param status The new status to set
     */
    public void setStatus(InternshipStatus status) {
        this.status = status;
        if (status == InternshipStatus.APPROVED) visible = true;
        if (status == InternshipStatus.REJECTED) visible = false;
    }

    /**
     * Adds an application to this internship.
     * Automatically updates filled slots count.
     * 
     * @param app The application to add
     */
    public void addApplication(Application app) {
        applications.add(app);
        updateFilledSlots();
    }

    /**
     * Updates the filled slots count based on successful/accepted applications.
     * Automatically sets status to FILLED if all slots are taken.
     */
    private void updateFilledSlots() {
        // Count both SUCCESSFUL and ACCEPTED applications as filled slots
        long successfulCount = applications.stream()
                .filter(app -> app.getStatus() == ApplicationStatus.SUCCESSFUL || app.getStatus() == ApplicationStatus.ACCEPTED)
                .count();
        filledSlots = (int) successfulCount;
        if (filledSlots >= numSlots) {
            status = InternshipStatus.FILLED;
        }
    }

    /**
     * Finds an internship by its ID.
     * 
     * @param id The internship ID to search for
     * @return The internship with the given ID, or null if not found
     */
    public static Internship findWithID(int id) {
        return internships.stream().filter(i -> i.id == id).findFirst().orElse(null);
    }

    /**
     * Updates the details of this internship.
     * Can only update if status is PENDING.
     * Note: CSV persistence is handled by controllers (MVC compliance).
     * 
     * @param title New title
     * @param description New description
     * @param level New level
     * @param major New preferred major
     * @param openDate New opening date
     * @param closeDate New closing date
     * @param slots New number of slots
     * @throws IllegalStateException if internship is already approved
     */
    public void updateDetails(String title, String description, String level, String major, 
                             LocalDate openDate, LocalDate closeDate, int slots) {
        if (!canEdit()) {
            throw new IllegalStateException("Cannot edit approved internship.");
        }
        this.title = title;
        this.description = description;
        this.level = level;
        this.preferredMajor = major;
        this.openDate = openDate;
        this.closeDate = closeDate;
        this.numSlots = slots;
    }
    
    /**
     * Deletes this internship from the system.
     * Can only delete if status is PENDING or REJECTED.
     * Marks all related applications as WITHDRAWN.
     * Note: CSV persistence is handled by controllers (MVC compliance).
     * 
     * @throws IllegalStateException if internship is approved or filled
     */
    public void delete() {
        if (!canEdit()) {
            throw new IllegalStateException("Cannot delete approved or filled internship. Only PENDING or REJECTED internships can be deleted.");
        }
        // Mark all applications for this internship as WITHDRAWN
        for (Application app : new java.util.ArrayList<>(applications)) {
            app.setStatus(constant.ApplicationStatus.WITHDRAWN);
        }
        internships.remove(this);
    }
    
    /**
     * Confirms a placement, updating filled slots.
     * Note: CSV persistence is handled by controllers (MVC compliance).
     */
    public void confirmPlacement() {
        updateFilledSlots();
    }
    
    /**
     * Checks if this internship is owned by the given company representative.
     * 
     * @param rep The company representative to check
     * @return true if the given rep is the creator, false otherwise
     */
    public boolean isOwnedBy(CompanyRepresentative rep) {
        return creator.equals(rep);
    }
    
    /**
     * Checks if the given company representative can view details of this internship.
     * 
     * @param rep The company representative to check
     * @return true if the rep owns this internship, false otherwise
     */
    public boolean canViewDetails(CompanyRepresentative rep) {
        return isOwnedBy(rep);
    }

    @Override
    public String toString() {
        // Use formatter for consistent formatting
        return InternshipFormatter.formatAsRow(this, true);
    }
    
    // Public helper methods for CSV handler
    /**
     * Gets the internal list of internships.
     * Used by CSV handler for persistence operations.
     * 
     * @return The list of all internships
     */
    public static List<Internship> getInternshipsList() {
        return internships;
    }
    
    /**
     * Gets the next available ID.
     * 
     * @return The next ID to be assigned
     */
    public static int getNextID() {
        return nextID;
    }
    
    /**
     * Sets the next ID value.
     * Used by CSV handler when loading from file.
     * 
     * @param id The new next ID value
     */
    public static void setNextID(int id) {
        nextID = id;
    }
    
    // Public factory method for CSV loading
    /**
     * Factory method for creating internships from CSV data.
     * Used by CSV handler to reconstruct internships with all fields.
     * 
     * @param id Internship ID
     * @param title Title
     * @param description Description
     * @param level Level
     * @param major Preferred major
     * @param openDate Opening date
     * @param closeDate Closing date
     * @param company Company name
     * @param creator Creator
     * @param numSlots Number of slots
     * @param visible Visibility flag
     * @param status Status
     * @param filledSlots Filled slots count
     * @return A new Internship instance
     */
    public static Internship createForCsv(int id, String title, String description, String level, String major,
                                   LocalDate openDate, LocalDate closeDate, String company,
                                   CompanyRepresentative creator, int numSlots, boolean visible,
                                   InternshipStatus status, int filledSlots) {
        return new Internship(id, title, description, level, major, openDate, closeDate, company, creator, numSlots,
                             visible, status, filledSlots);
    }
    
    /**
     * Private constructor for CSV loading.
     * Allows setting all fields including ID, visibility, status, and filled slots.
     * 
     * @param id Internship ID
     * @param title Title
     * @param description Description
     * @param level Level
     * @param major Preferred major
     * @param open Opening date
     * @param close Closing date
     * @param company Company name
     * @param creator Creator
     * @param slots Number of slots
     * @param visible Visibility flag
     * @param status Status
     * @param filledSlots Filled slots count
     */
    private Internship(int id, String title, String description, String level, String major,
                      LocalDate open, LocalDate close, String company, CompanyRepresentative creator,
                      int slots, boolean visible, InternshipStatus status, int filledSlots) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.level = level;
        this.preferredMajor = major;
        this.openDate = open;
        this.closeDate = close;
        this.companyName = company;
        this.creator = creator;
        this.numSlots = slots;
        this.visible = visible;
        this.status = status;
        this.filledSlots = filledSlots;
        this.applications = new ArrayList<>();
    }
    
    // Public getter for CSV handler
    /**
     * Gets the raw visibility flag value (ignores approval status).
     * Used for CSV persistence and admin displays.
     * For student visibility logic, use {@link #isVisible()} instead.
     * 
     * @return The visibility flag
     */
    public boolean getVisibleFlag() {
        return visible;
    }
}

