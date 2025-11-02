package com.sc2002.assignment.model;


import com.sc2002.assignment.constant.ApplicationStatus;
import com.sc2002.assignment.constant.InternshipStatus;
import com.sc2002.assignment.utils.InternshipCsvHandler;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Internship {

    private static final List<Internship> internships = new ArrayList<>();
    private static int nextID = 100000;

    public static List<Internship> getAllInternships() {
        return new ArrayList<>(internships);
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
    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLevel() {
        return level;
    }

    public String getPreferredMajor() {
        return preferredMajor;
    }
    
    public LocalDate getOpenDate() {
        return openDate;
    }
    
    public LocalDate getCloseDate() {
        return closeDate;
    }

    public String getCompanyName() {
        return companyName;
    }

    public CompanyRepresentative getCreator() {
        return creator;
    }

    public InternshipStatus getStatus() {
        return status;
    }

    public boolean isVisible() {
        return visible && status == InternshipStatus.APPROVED;
    }
    
    public boolean isVisibleToStudent(Student student) {
        if (!isVisible()) return false;
        
        // Check major match
        if (!preferredMajor.equalsIgnoreCase(student.getMajor())) {
            return false;
        }
        
        // Check level eligibility based on year of study
        int year = student.getYearOfStudy();
        if (level.equalsIgnoreCase("Intermediate") && year < 2) {
            return false;
        }
        if (level.equalsIgnoreCase("Advanced") && year < 3) {
            return false;
        }
        
        // Check if internship is open (within date range)
        LocalDate now = LocalDate.now();
        return !now.isBefore(openDate) && !now.isAfter(closeDate);
    }
    
    public boolean isEligibleForStudent(Student student) {
        return isVisibleToStudent(student);
    }
    
    public void toggleVisibility() {
        if (status == InternshipStatus.APPROVED) {
            visible = !visible;
            // Save to CSV
            InternshipCsvHandler.saveToCsv(this);
        }
    }
    
    public boolean canEdit() {
        return status == InternshipStatus.PENDING;
    }

    public int getNumSlots() {
        return numSlots;
    }

    public int getFilledSlots() {
        return filledSlots;
    }

    public ArrayList<Application> getApplications() {
        return applications;
    }

    // Status and visibility control
    public void setStatus(InternshipStatus status) {
        this.status = status;
        if (status == InternshipStatus.APPROVED) visible = true;
        if (status == InternshipStatus.REJECTED) visible = false;
        // Save to CSV
        InternshipCsvHandler.saveToCsv(this);
    }

    public void addApplication(Application app) {
        applications.add(app);
        updateFilledSlots();
    }

    private void updateFilledSlots() {
        long confirmedCount = applications.stream()
                .filter(app -> app.getStatus() == ApplicationStatus.CONFIRMED)
                .count();
        filledSlots = (int) confirmedCount;
        if (filledSlots >= numSlots) {
            status = InternshipStatus.FILLED;
        }
    }

    public int getID() {
        return id;
    }

    public static Internship findWithID(int id) {
        return internships.stream()
                .filter(i -> i.id == id)
                .findFirst()
                .orElse(null);
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
        // Save to CSV
        InternshipCsvHandler.saveToCsv(this);
    }
    
    public void delete() {
        if (!canEdit()) {
            throw new IllegalStateException("Cannot delete approved internship.");
        }
        internships.remove(this);
        // TODO: Handle CSV deletion if needed
    }
    
    public void confirmPlacement() {
        updateFilledSlots();
        // Save to CSV
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
        return String.format("ID: %d | Title: %s | Company: %s | Level: %s | Major: %s | Status: %s | Visible: %s | Slots: %d/%d",
                id, title, companyName, level, preferredMajor, status, isVisible(), filledSlots, numSlots);
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