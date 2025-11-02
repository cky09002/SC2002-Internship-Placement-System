package Assignment.src.model;

import Assignment.src.constant.ApplicationStatus;
import Assignment.src.exceptions.IDNotFoundException;
import Assignment.src.exceptions.WrongApplicationStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Student extends User {
    public static final int MAX_APPLICATIONS = 3;

    private int yearOfStudy;
    private String major;
    private final List<Application> applications = new ArrayList<>(MAX_APPLICATIONS);

    public Student(String userID, String name, String password, String email, int yearOfStudy, String major) {
        super(userID, name, password, email);
        this.yearOfStudy = yearOfStudy;
        this.major = major;
    }

    public int getYearOfStudy() { return yearOfStudy; }
    public String getMajor() { return major; }
    public List<Application> getApplications() { return applications; }

    public void logout() {
        setLoggedIn(false);
    }

    public Application findApplicationWithID(int applicationID) {
        return applications.stream()
                .filter((a) -> a.getId() == applicationID)
                .findFirst()
                .orElseThrow(() -> new IDNotFoundException("application", applicationID));
    }

    private void validateApplication(Internship internship) {
        // check student has not accepted any offer yet
        if (applications.stream().anyMatch(app -> app.getStatus() == ApplicationStatus.CONFIRMED)) {
            throw new IllegalStateException("You have already accepted an internship offer.");
        }

        // check student has not already applied
        if (applications.stream().anyMatch(app -> app.getApplicant() == this)) {
            throw new IllegalStateException("You have already applied for this internship.");
        }

        // check per-student application limit
        // UNSUCCESSFUL and WITHDRAWN applications don't count towards the limit
        long numActiveApps = applications.stream()
                .filter(app -> {
                    ApplicationStatus status = app.getStatus();
                    return status != ApplicationStatus.UNSUCCESSFUL &&
                            status != ApplicationStatus.WITHDRAWN;
                })
                .count();
        if (numActiveApps >= MAX_APPLICATIONS) {
            throw new IllegalStateException(String.format(
                    "Application limit reached. Max allowed: %d",
                    MAX_APPLICATIONS));
        }

        // Check eligibility
        if (!internship.isEligibleForStudent(this)) {
            throw new IllegalStateException(
                    "You are not eligible for this internship. Check your major, year of study, or visibility status.");
        }
    }

    public Application submitApplication(Internship internship) {
        validateApplication(internship);

        // create the application object and pass it to associated internship
        Application app = new Application(
                internship,
                this,
                LocalDateTime.now()
        );
        internship.addApplication(app);

        // also store a reference locally
        applications.add(app);
        return app;
    }

    public void acceptApplication(Application app) {
        // check that chosen application is successful
        if (app.getStatus() != ApplicationStatus.SUCCESSFUL) {
            throw new WrongApplicationStatusException(app.getStatus());
        }

        app.setStatus(ApplicationStatus.CONFIRMED);
        // Withdraw all other applications
        applications.stream()
                .filter((a) -> a != app)
                .forEach((a) -> a.setStatus(ApplicationStatus.WITHDRAWN));
        // Update filled slots in internship
        app.getInternship().confirmPlacement();
    }

    public void withdrawApplication(Application app) {
        app.requestWithdrawal();
    }

    @Override
    public void displayProfile() {
        System.out.println("Student: " + getUserID() + 
                          " | Name: " + getName() + 
                          " | Year: " + yearOfStudy + 
                          " | Major: " + major + 
                          " | Email: " + getEmail());
    }

    @Override
    public String getUserType() {
        return "Student";
    }
}