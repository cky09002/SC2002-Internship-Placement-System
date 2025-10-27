package model;

import constant.ApplicationStatus;

import java.time.LocalDateTime;

public class Application {
    private static int nextID = 500000;

    private final int id;
    private Internship internship;
    private Student applicant;
    private LocalDateTime dateApplied;
    private ApplicationStatus status;

    public Application(Internship internship, Student applicant, LocalDateTime dateApplied) {
        this.id = nextID++;
        this.internship = internship;
        this.applicant = applicant;
        this.dateApplied = dateApplied;
    }

    public int getId() { return id; }
    public ApplicationStatus getStatus() { return status; }
    public Student getApplicant() { return applicant; }
    public Internship getInternship() { return internship; }
}
