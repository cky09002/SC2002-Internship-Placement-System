package Assignment.src.model;

import Assignment.src.constant.ApplicationStatus;
import Assignment.src.exceptions.IDNotFoundException;
import Assignment.src.exceptions.TooManyApplicationsException;
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

    public Application submitApplication(Internship internship) {
        // enforce per-student application limit
        if (applications.size() >= MAX_APPLICATIONS) {
            throw new TooManyApplicationsException(MAX_APPLICATIONS);
        }
        
        // Check eligibility
        if (!internship.isEligibleForStudent(this)) {
            throw new IllegalStateException("You are not eligible for this internship. Check your major, year of study, or visibility status.");
        }

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