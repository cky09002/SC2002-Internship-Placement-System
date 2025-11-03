package Assignment.src.model;

import Assignment.src.constant.ApplicationStatus;
import Assignment.src.utils.ValidationHelper;

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

    public List<Application> getApplications() { return applications; }

    public Application findApplicationWithID(int applicationID) {
        return applications.stream()
                .filter((a) -> a.getId() == applicationID)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Couldn't find application with ID " + applicationID + "."));
    }

    public Application submitApplication(Internship internship) {
        // enforce per-student application limit (exclude only WITHDRAWN applications)
        long activeApplications = applications.stream()
                .filter(a -> a.getStatus() != ApplicationStatus.WITHDRAWN)
                .count();
        
        if (activeApplications >= MAX_APPLICATIONS) {
            throw new IllegalArgumentException("Maximum of " + MAX_APPLICATIONS + " applications allowed.");
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
            throw new IllegalArgumentException("Application is " + app.getStatus() + ", needs to be " + ApplicationStatus.SUCCESSFUL + " to be accepted.");
        }

        // Update status to ACCEPTED when student confirms placement
        app.setStatus(ApplicationStatus.ACCEPTED);
        // Withdraw all other applications
        applications.stream()
                .filter((a) -> a != app)
                .forEach((a) -> a.setStatus(ApplicationStatus.WITHDRAWN));
        // Update filled slots in internship
        app.getInternship().confirmPlacement();
    }

    public void withdrawApplication(Application app, String reason) {
        app.requestWithdrawal(reason);
    }
    
    /**
     * Withdraw application without reason (overloaded)
     */
    public void withdrawApplication(Application app) {
        app.requestWithdrawal((String) null);
    }

    @Override
    public String getUserType() {
        return "Student";
    }
    
    public int getYearOfStudy() { return yearOfStudy; }
    public String getMajor() { return major; }

    public void setYearOfStudy(int yearOfStudy) {
        ValidationHelper.validateRange(yearOfStudy, 1, 5, "Year of study");
        this.yearOfStudy = yearOfStudy;
    }
    
    public void setMajor(String major) {
        ValidationHelper.validateNotEmpty(major, "Major");
        this.major = major;
    }
}