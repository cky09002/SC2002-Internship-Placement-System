package model;

import constant.ApplicationStatus;
import exceptions.IDNotFoundException;
import exceptions.TooManyApplicationsException;
import exceptions.WrongApplicationStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Student extends User {
    public static final int MAX_APPLICATIONS = 3;

    private static final List<Student> allStudents = new ArrayList<>();

    private int yearOfStudy;
    private String major;
    private final List<Application> applications = new ArrayList<>(MAX_APPLICATIONS);

    public Student(String userID, String name, String password, String email, int yearOfStudy, String major) {
        super(userID, name, password, email);
        this.yearOfStudy = yearOfStudy;
        this.major = major;
    }

    public int getYearOfStudy() { return yearOfStudy; }
    public void setYearOfStudy(int yearOfStudy) { this.yearOfStudy = yearOfStudy; }
    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }
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
        applications.stream()
                .filter((a) -> a != app)
                .forEach((a) -> a.setStatus(ApplicationStatus.WITHDRAWN));
    }

    public void withdrawApplication(Application app) {
        // TODO: needs a way to mark an application for withdrawal
        app.requestWithdrawal();
    }
}