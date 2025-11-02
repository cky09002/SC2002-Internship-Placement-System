package Assignment.src.view;

import Assignment.src.model.Internship;
import Assignment.src.model.Application;
import Assignment.src.model.CompanyRepresentative;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class CompanyRepresentativeView {
    public static final Scanner sc = new Scanner(System.in);

    public MenuOption showMenuDialog(Map<String, MenuOption> dialogOptionMap) {
        System.out.println("=== Company Representative Menu ===");

        for (Map.Entry<String, MenuOption> entry : dialogOptionMap.entrySet()) {
            System.out.printf("%s : %s%n", entry.getKey(), entry.getValue().getDesc());
        }
        System.out.println("\nChoose a menu option:");

        String sel = sc.next();
        return dialogOptionMap.get(sel);
    }

    public void showInternships(List<Internship> internships) {
        System.out.println("=== My Internships ===");
        if (internships.isEmpty()) {
            System.out.println("No internships found.");
        } else {
            for (Internship i : internships) {
                System.out.println(i);
            }
        }
    }

    public void showCreateInternshipDialog() {
        System.out.println("=== Create New Internship ===");
    }

    public Internship getInternshipData(CompanyRepresentative creator) {
        sc.nextLine();
        System.out.print("Title: ");
        String title = sc.nextLine();
        
        System.out.print("Description: ");
        String description = sc.nextLine();
        
        System.out.print("Level (Basic/Intermediate/Advanced): ");
        String level = sc.nextLine();
        
        System.out.print("Preferred Major: ");
        String major = sc.nextLine();
        
        System.out.print("Open Date (YYYY-MM-DD): ");
        String openDateStr = sc.nextLine();
        LocalDate openDate = parseDate(openDateStr);
        
        System.out.print("Close Date (YYYY-MM-DD): ");
        String closeDateStr = sc.nextLine();
        LocalDate closeDate = parseDate(closeDateStr);
        
        System.out.print("Number of slots: ");
        int slots = sc.nextInt();
        sc.nextLine();

        return new Internship(title, description, level, major, 
                             openDate, closeDate, creator.getCompanyName(), 
                             creator, slots);
    }

    public void showInternshipCreatedMsg(Internship internship) {
        System.out.println("--- Internship created successfully ---");
        System.out.printf("ID: %d%n", internship.getID());
        System.out.printf("Title: %s%n", internship.getTitle());
        System.out.println("Note: Internship is pending approval.");
    }

    public int showSelectInternshipDialog() {
        System.out.println("=== Select Internship ===");
        System.out.print("Enter internship ID: ");
        return sc.nextInt();
    }

    public void showApplications(List<Application> applications) {
        System.out.println("=== Applications ===");
        if (applications.isEmpty()) {
            System.out.println("No applications found.");
        } else {
            for (Application app : applications) {
                System.out.println(app);
            }
        }
    }

    public int showSelectApplicationDialog() {
        System.out.println("=== Select Application ===");
        System.out.print("Enter application ID: ");
        return sc.nextInt();
    }

    private LocalDate parseDate(String dateStr) {
        try {
            return LocalDate.parse(dateStr);
        } catch (Exception e) {
            System.out.println("Invalid date format, using today's date.");
            return LocalDate.now();
        }
    }
}

