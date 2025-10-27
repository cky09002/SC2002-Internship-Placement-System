package View;

import model.*;
import java.util.List;
import java.util.Scanner;

public class CareerCentreStaffView {

    private Scanner sc = new Scanner(System.in);

    public void displayMenu() {
        System.out.println("=== Career Centre Staff Menu ===");
        System.out.println("1. Approve/Reject Company Representative");
        System.out.println("2. Approve/Reject Internship Listing");
        System.out.println("3. Approve/Reject Student Withdrawal");
        System.out.println("4. Filter Data");
        System.out.println("5. Generate Report");
        System.out.println("0. Logout");
        System.out.print("Enter your choice: ");
    }

    public String getFilterCriteria() {
        System.out.print("Enter filter criteria: ");
        return sc.nextLine();
    }

    // Display Lists
    public void displayInternshipList(List<Internship> list) {
        System.out.println("\n--- Internship List ---");
        for (Internship i : list)
            System.out.println(i.toString());
    }

    public void displayApplications(List<Application> list) {
        System.out.println("\n--- Applications ---");
        for (Application app : list)
            System.out.println(app.toString());
    }

    public void showCompanyRepList(List<CompanyRepresentative> list) {
        System.out.println("\n--- Company Representatives ---");
        for (CompanyRepresentative rep : list)
            System.out.println(rep.toString());
    }

    // Display Report
    public void displayReport(Report report) {
        System.out.println("\n=== Generated Report ===");
        report.generateSummary();
    }
}
