package com.sc2002.assignment.view;

import com.sc2002.assignment.model.Application;
import com.sc2002.assignment.model.Internship;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class StudentView {
    public static final Scanner sc = new Scanner(System.in);

    public MenuOption showMenuDialog(Map<String, MenuOption> dialogOptionMap) {
        System.out.println();
        System.out.println("─".repeat(60));
        System.out.println("  STUDENT DASHBOARD");
        System.out.println("─".repeat(60));

        for (Map.Entry<String, MenuOption> entry : dialogOptionMap.entrySet()) {
            System.out.printf("  %s. %s%n", entry.getKey(), entry.getValue().getDesc());
        }
        System.out.println("─".repeat(60));
        System.out.print("  Enter your choice: ");

        String sel = sc.next();
        return dialogOptionMap.get(sel);
    }

    public void showInternships(List<Internship> internships) {
        System.out.println("\n─".repeat(60));
        System.out.println("  AVAILABLE INTERNSHIPS");
        System.out.println("─".repeat(60));
        if (internships.isEmpty()) {
            System.out.println("  No internships available.");
        } else {
            for (Internship i : internships) {
                System.out.println(i);
            }
        }
    }
    
    public void viewMyApplications(List<Application> applications) {
        System.out.println("\n─".repeat(60));
        System.out.println("  MY APPLICATIONS");
        System.out.println("─".repeat(60));
        if (applications.isEmpty()) {
            System.out.println("  No applications found.");
        } else {
            for (Application app : applications) {
                System.out.println(app);
            }
        }
    }

    public int showApplyDialog() {
        System.out.println("\n─".repeat(60));
        System.out.println("  SUBMIT APPLICATION");
        System.out.println("─".repeat(60));
        System.out.print("  Enter internship ID to apply: ");
        return sc.nextInt();
    }

    public int showWithdrawDialog() {
        System.out.println("\n─".repeat(60));
        System.out.println("  REQUEST WITHDRAWAL");
        System.out.println("─".repeat(60));
        System.out.print("  Enter application ID to withdraw: ");
        return sc.nextInt();
    }

    public int showAcceptDialog() {
        System.out.println("\n─".repeat(60));
        System.out.println("  ACCEPT OFFER");
        System.out.println("─".repeat(60));
        System.out.print("  Enter application ID to accept: ");
        return sc.nextInt();
    }

    public void showApplyMsg(Application submittedData) {
        System.out.println("\n" + "═".repeat(60));
        System.out.println("  ✓ APPLICATION SUBMITTED SUCCESSFULLY");
        System.out.println("═".repeat(60));
        System.out.println("\n  Internship Details:");
        System.out.printf("    ID: %d%n", submittedData.getInternship().getID());
        System.out.printf("    Title: %s%n", submittedData.getInternship().getTitle());
        System.out.printf("    Description: %s%n", submittedData.getInternship().getDescription());
        System.out.println("\n  Application Details:");
        System.out.printf("    ID: %d%n", submittedData.getId());
        System.out.printf("    Applicant: %s%n", submittedData.getApplicant().getName());
        System.out.printf("    Date Applied: %s%n", submittedData.getDateApplied());
        System.out.printf("    Status: %s%n", submittedData.getStatus());
        System.out.println("═".repeat(60));
    }
}
