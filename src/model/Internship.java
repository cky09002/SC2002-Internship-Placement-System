package model;

import constant.InternshipStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Internship {

    private static final List<Internship> internships = new ArrayList<>();

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
        this.title = title;
        this.description = description;
        this.level = level;
        this.preferredMajor = major;
        this.openDate = open;
        this.closeDate = close;
        this.companyName = company;
        this.creator = creator;
        this.numSlots = slots;
        this.filledSlots = 0;
        this.visible = false; // hidden until approved
        this.status = InternshipStatus.PENDING;
        this.applications = new ArrayList<>();
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
    }

    public void toggleVisibility() {
        if (status == InternshipStatus.APPROVED)
            visible = !visible;
    }

    public boolean isOpen() {
        LocalDate now = LocalDate.now();
        return isVisible() && !now.isBefore(openDate) && !now.isAfter(closeDate)
                && status == InternshipStatus.APPROVED;
    }

    public void addApplication(Application app) {
        applications.add(app);
        updateFilledSlots();
    }

    public void confirmPlacement() {
        if (filledSlots < numSlots) {
            filledSlots++;
            updateFilledSlots();
        }
    }

    private void updateFilledSlots() {
        if (filledSlots >= numSlots) status = InternshipStatus.FILLED;
    }
}