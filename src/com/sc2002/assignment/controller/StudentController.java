package com.sc2002.assignment.controller;

import com.sc2002.assignment.exceptions.IDNotFoundException;
import com.sc2002.assignment.exceptions.WrongApplicationStatusException;
import com.sc2002.assignment.model.Application;
import com.sc2002.assignment.model.Internship;
import com.sc2002.assignment.model.Student;
import com.sc2002.assignment.view.MenuOption;
import com.sc2002.assignment.view.StudentView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentController {
    private final StudentView view;
    private final Student student;

    // preset menu option mappings
    private final Map<String, MenuOption> mainMenuOptions;

    public StudentController(Student student, StudentView view) {
        this.student = student;
        this.view = view;
        this.mainMenuOptions = initializeMenuOptions();
    }
    
    private Map<String, MenuOption> initializeMenuOptions() {
        Map<String, MenuOption> options = new HashMap<>();
        options.put("l", new MenuOption("list available internships", this::listInternships));
        options.put("c", new MenuOption("create a new application", this::createApplication));
        options.put("v", new MenuOption("com.sc2002.assignment.view my applications", this::viewApplications));
        options.put("w", new MenuOption("request to withdraw an application", this::withdrawApplication));
        options.put("a", new MenuOption("accept a successful application", this::acceptApplication));
        options.put("q", new MenuOption("log out", this::logout));
        return options;
    }

    public void run() {
        do {
            try {
                // show main menu
                MenuOption menuSel = view.showMenuDialog(mainMenuOptions);

                // choose what to do next using preset main menu options
                if (menuSel != null && menuSel.getOnSelCallback() != null) {
                    menuSel.getOnSelCallback().run();
                } else {
                    System.out.println("Invalid menu option!");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        } while (student.isLoggedIn());
    }

    public void logout() {
        student.logout();
    }

    public void listInternships() {
        List<Internship> eligibleInternships = Internship.getAllInternships().stream()
                .filter(i -> i.isVisibleToStudent(student))
                .collect(java.util.stream.Collectors.toList());
        view.showInternships(eligibleInternships);
    }

    public void createApplication() {
        try {
            int internshipID = view.showApplyDialog();
            Internship its = Internship.findWithID(internshipID);
            if (its == null) {
                System.out.println("Internship not found!");
                return;
            }
            Application app = student.submitApplication(its);
            view.showApplyMsg(app);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void withdrawApplication() {
        try {
            int applicationID = view.showWithdrawDialog();
            Application app = student.findApplicationWithID(applicationID);
            student.withdrawApplication(app);
            System.out.println("Withdrawal request submitted successfully! It will be reviewed by Career Center Staff.");
        } catch (IDNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void acceptApplication() {
        try {
            int applicationID = view.showAcceptDialog();
            Application app = student.findApplicationWithID(applicationID);
            student.acceptApplication(app);
            System.out.println("Application accepted successfully!");
        } catch (WrongApplicationStatusException | IDNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    public void viewApplications() {
        view.viewMyApplications(student.getApplications());
    }
}
