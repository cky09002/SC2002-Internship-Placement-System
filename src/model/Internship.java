package Assignment.src.model;

import Assignment.src.constant.*;
import Assignment.src.utils.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Internship {
    private static final List<Internship> internships = new ArrayList<>();
    private static int nextID = 100000;

    /**
     * Get all internships - always reloads from CSV to ensure fresh data
     */
    public static List<Internship> getAllInternships() {
        // Always reload from CSV - no caching
        loadFromCsv();
        return new ArrayList<>(internships);
    }
    
    /**
     * Load internships from CSV file - clears existing data first
     */
    private static void loadFromCsv() {
        // Clear existing internships and their applications
        for (Internship internship : new ArrayList<>(internships)) {
            internship.getApplications().clear();
        }
        internships.clear();
        InternshipCsvHandler.loadFromCsv();
    }

    private final int id;
    private String title;
    private String description;
    private String level; // Basic, Intermediate, Advanced
    private String preferredMajor;
    private LocalDate openDate;
    private LocalDate closeDate;
    private String companyName;
    private CompanyRepresentative creator; // who created it
    private boolean visible; // toggled by company rep
    private int numSlots; // total slots
    private int filledSlots;//how many slots confirmed
    private InternshipStatus status; // enum
    private ArrayList<Application> applications;

    public Internship(String title, String description, String level, String major,
                      LocalDate open, LocalDate close, String company,
                      CompanyRepresentative creator, int slots) {
        this(nextID++, title, description, level, major, open, close, company, creator, slots,
             false, InternshipStatus.PENDING, 0);
        internships.add(this);
    }

    // Getters
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getLevel() { return level; }
    public String getPreferredMajor() { return preferredMajor; }
    public LocalDate getOpenDate() { return openDate; }
    public LocalDate getCloseDate() { return closeDate; }
    public String getCompanyName() { return companyName; }
    public CompanyRepresentative getCreator() { return creator; }
    public InternshipStatus getStatus() { return status; }
    public boolean isVisible() { return visible && status == InternshipStatus.APPROVED; }
    
    /**
     * Check if internship should be visible in listings for a student.
     * Students should NOT see internships with:
     * - Filled status (all slots taken)
     * - Wrong major (doesn't match student's major)
     * - Wrong level (not eligible for student's year of study)
     * - Visibility off (unless they've applied)
     * 
     * Applied internships are still visible in listings only if they meet profile requirements
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
     * 2. Student has applied for it (can view even if visibility is off)
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
     * Check if a student has applied for this internship
     * Used to allow students to view internships they've applied for even if visibility is off
     */
    public boolean hasStudentApplied(Student student) {
        if (applications == null) return false;
        return applications.stream()
                .anyMatch(app -> app.getApplicant().equals(student));
    }
    
    public boolean isEligibleForStudent(Student student) {
        return isVisibleToStudent(student);
    }
    
    public void toggleVisibility() {
        if (status == InternshipStatus.APPROVED) {
            visible = !visible;
            InternshipCsvHandler.saveToCsv(this);
        }
    }
    
    public boolean canEdit() {
        return status == InternshipStatus.PENDING;
    }
    
    /**
     * Check if internship is filled (all slots taken)
     */
    public boolean isFilled() {
        return status == InternshipStatus.FILLED || (status == InternshipStatus.APPROVED && filledSlots >= numSlots);
    }
    
    /**
     * Get display status: "Filled" or "Available"
     */
    public String getDisplayStatus() {
        return isFilled() ? "Filled" : "Available";
    }

    public int getNumSlots() { return numSlots; }
    public int getFilledSlots() { return filledSlots; }
    public ArrayList<Application> getApplications() { return applications; }
    public int getID() { return id; }

    // Status and visibility control
    public void setStatus(InternshipStatus status) {
        this.status = status;
        if (status == InternshipStatus.APPROVED) visible = true;
        if (status == InternshipStatus.REJECTED) visible = false;
        InternshipCsvHandler.saveToCsv(this);
    }

    public void addApplication(Application app) {
        applications.add(app);
        updateFilledSlots();
    }

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

    public static Internship findWithID(int id) {
        return internships.stream().filter(i -> i.id == id).findFirst().orElse(null);
    }

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
        InternshipCsvHandler.saveToCsv(this);
    }
    
    public void delete() {
        if (!canEdit()) {
            throw new IllegalStateException("Cannot delete approved internship.");
        }
        internships.remove(this);
        // CSV deletion handled by InternshipCsvHandler if needed
    }
    
    public void confirmPlacement() {
        updateFilledSlots();
        InternshipCsvHandler.saveToCsv(this);
    }
    
    public boolean isOwnedBy(CompanyRepresentative rep) {
        return creator.equals(rep);
    }
    
    public boolean canViewDetails(CompanyRepresentative rep) {
        return isOwnedBy(rep);
    }

    @Override
    public String toString() {
        // Use formatter for consistent formatting
        return InternshipFormatter.formatAsRow(this, true);
    }
    
    // Public helper methods for CSV handler
    public static List<Internship> getInternshipsList() {
        return internships;
    }
    
    public static int getNextID() {
        return nextID;
    }
    
    public static void setNextID(int id) {
        nextID = id;
    }
    
    // Public factory method for CSV loading
    public static Internship createForCsv(int id, String title, String description, String level, String major,
                                   LocalDate openDate, LocalDate closeDate, String company,
                                   CompanyRepresentative creator, int numSlots, boolean visible,
                                   InternshipStatus status, int filledSlots) {
        return new Internship(id, title, description, level, major, openDate, closeDate, company, creator, numSlots,
                             visible, status, filledSlots);
    }
    
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
    public boolean isVisiblePrivate() {
        return visible;
    }
}