package com.sc2002.assignment.view;

import com.sc2002.assignment.model.Application;
import com.sc2002.assignment.model.CompanyRepresentative;
import com.sc2002.assignment.model.Internship;
import com.sc2002.assignment.model.Report;

import java.util.List;
import java.util.Scanner;

public class StaffView {
    private Scanner sc = new Scanner(System.in);

    public void displayMenu() {
        System.out.println();
        System.out.println("─".repeat(60));
        System.out.println("  CAREER CENTER STAFF DASHBOARD");
        System.out.println("─".repeat(60));
        System.out.println("  1. Approve/Reject Company Representative");
        System.out.println("  2. Approve/Reject Internship Listing");
        System.out.println("  3. Approve/Reject Student Withdrawal");
        System.out.println("  4. Filter Data");
        System.out.println("  5. Generate Report");
        System.out.println("  0. Logout");
        System.out.println("─".repeat(60));
        System.out.print("  Enter your choice: ");
    }
    
    public int getApplicationID() {
        System.out.print("  Enter application ID: ");
        int id = sc.nextInt();
        sc.nextLine();
        return id;
    }

    public String getFilterCriteria() {
        System.out.print("  Enter filter criteria: ");
        return sc.nextLine();
    }
    
    public int getInternshipID() {
        System.out.print("  Enter internship ID: ");
        int id = sc.nextInt();
        sc.nextLine();
        return id;
    }
    
    public String getCompanyRepID() {
        System.out.print("  Enter company representative ID: ");
        return sc.nextLine();
    }
    
    public boolean getApprovalDecision() {
        System.out.print("  Approve? (y/n): ");
        String input = sc.nextLine().trim().toLowerCase();
        return input.equals("y") || input.equals("yes");
    }

    public void displayInternshipList(List<Internship> list) {
        System.out.println("\n─".repeat(60));
        System.out.println("  INTERNSHIP LIST");
        System.out.println("─".repeat(60));
        if (list.isEmpty()) {
            System.out.println("  No internships found.");
        } else {
            for (Internship i : list) {
                System.out.println(i.toString());
            }
        }
    }

    public void displayApplications(List<Application> list) {
        System.out.println("\n─".repeat(60));
        System.out.println("  APPLICATIONS");
        System.out.println("─".repeat(60));
        if (list.isEmpty()) {
            System.out.println("  No applications found.");
        } else {
            for (Application app : list) {
                System.out.println(app.toString());
            }
        }
    }

    public void showCompanyRepList(List<CompanyRepresentative> list) {
        System.out.println("\n─".repeat(60));
        System.out.println("  COMPANY REPRESENTATIVES");
        System.out.println("─".repeat(60));
        if (list.isEmpty()) {
            System.out.println("  No company representatives found.");
        } else {
            for (CompanyRepresentative rep : list) {
                System.out.println(rep.toString());
            }
        }
    }

    public void displayReport(Report report) {
        System.out.println("\n═".repeat(60));
        System.out.println("  GENERATED REPORT");
        System.out.println("═".repeat(60));
        report.generateSummary();
    }
}
