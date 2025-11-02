package Assignment.src;

import Assignment.src.controller.*;
import Assignment.src.model.*;
import Assignment.src.view.*;
import Assignment.src.constant.UserType;
import Assignment.src.utils.UserFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class InternshipApp {
    private UserRegistry userRegistry;
    private LoginController loginController;
    private Scanner sc;
    
    public InternshipApp() {
        this.userRegistry = UserRegistry.getInstance();
        this.loginController = new LoginController(userRegistry);
        this.sc = new Scanner(System.in);
        
        // Load users from CSV files
        loadUsers();
        // Load internships and applications from CSV files
        loadInternshipsAndApplications();
    }
    
    private void loadUsers() {
        System.out.print("Loading users from CSV files");
        for (int i = 0; i < 3; i++) {
            try {
                Thread.sleep(200);
                System.out.print(".");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println();
        
        int totalUsers = 0;
        
        // Load students
        try {
            BufferedReader reader = new BufferedReader(new FileReader("sample_file/sample_student_list.csv"));
            String line = reader.readLine(); // skip header
            int count = 0;
            while ((line = reader.readLine()) != null) {
                String[] cols = parseCSVLine(line);
                if (cols.length >= 6) {
                    Student student = (Student) UserFactory.fromCsv(UserType.STUDENT, cols);
                    if (userRegistry.register(student)) {
                        count++;
                    }
                }
            }
            reader.close();
            totalUsers += count;
            System.out.println("✓ Students: " + count + " loaded");
        } catch (IOException e) {
            System.out.println("✗ Error loading students: " + e.getMessage());
        }
        
        // Load staff
        try {
            BufferedReader reader = new BufferedReader(new FileReader("sample_file/sample_staff_list.csv"));
            String line = reader.readLine(); // skip header
            int count = 0;
            while ((line = reader.readLine()) != null) {
                String[] cols = parseCSVLine(line);
                if (cols.length >= 6) {
                    Staff staff = (Staff) UserFactory.fromCsv(UserType.STAFF, cols);
                    if (userRegistry.register(staff)) {
                        count++;
                    }
                }
            }
            reader.close();
            totalUsers += count;
            System.out.println("✓ Staff: " + count + " loaded");
        } catch (IOException e) {
            System.out.println("✗ Error loading staff: " + e.getMessage());
        }
        
        // Load company representatives
        try {
            BufferedReader reader = new BufferedReader(new FileReader("sample_file/sample_company_representative_list.csv"));
            String line = reader.readLine(); // skip header
            int count = 0;
            while ((line = reader.readLine()) != null && !line.trim().isEmpty()) {
                String[] cols = parseCSVLine(line);
                if (cols.length >= 8) {
                    CompanyRepresentative compRep = (CompanyRepresentative) UserFactory.fromCsv(UserType.COMPANY_REPRESENTATIVE, cols);
                    if (userRegistry.register(compRep)) {
                        count++;
                    }
                }
            }
            reader.close();
            totalUsers += count;
            System.out.println("✓ Company Representatives: " + count + " loaded");
        } catch (IOException e) {
            System.out.println("✗ Error loading company representatives: " + e.getMessage());
        }
        
        System.out.println("\n═══ " + totalUsers + " users loaded successfully ═══\n");
    }
    
    private void loadInternshipsAndApplications() {
        System.out.print("Loading internships and applications from CSV files");
        for (int i = 0; i < 3; i++) {
            try {
                Thread.sleep(200);
                System.out.print(".");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println();
        
        // Load internships first (applications depend on them)
        Assignment.src.utils.InternshipCsvHandler.loadFromCsv();
        int internshipCount = Assignment.src.model.Internship.getAllInternships().size();
        System.out.println("✓ Internships: " + internshipCount + " loaded");
        
        // Load applications
        Assignment.src.utils.ApplicationCsvHandler.loadFromCsv();
        int applicationCount = Assignment.src.model.Application.getAllApplications().size();
        System.out.println("✓ Applications: " + applicationCount + " loaded");
        
        System.out.println("\n═══ Data loaded successfully ═══\n");
    }
    
    public void run() {
        printWelcomeBanner();
        
        while (true) {
            try {
                printMainMenu();
                
                if (!sc.hasNextInt()) {
                    printError("Invalid input! Please enter a number.");
                    sc.nextLine();
                    continue;
                }
                
                int choice = sc.nextInt();
                sc.nextLine();
                
                if (choice == 1) {
                    printSection("USER LOGIN");
                    User user = loginController.login();
                    if (user != null) {
                        routeToController(user);
                    }
                } else if (choice == 2) {
                    printSection("CHANGE PASSWORD");
                    loginController.changePasswordAtLoginPage();
                } else if (choice == 3) {
                    printSection("COMPANY REPRESENTATIVE REGISTRATION");
                    loginController.registerCompanyRepresentative();
                } else if (choice == 4) {
                    printGoodbye();
                    break;
                } else {
                    printError("Invalid choice! Please select a number between 1-4.");
                }
            } catch (Exception e) {
                printError("Error: " + e.getMessage());
                System.out.println("Please try again.");
                if (sc.hasNextLine()) {
                    sc.nextLine();
                }
            }
        }
    }
    
    private void printWelcomeBanner() {
        String line = "═".repeat(60);
        String title = "INTERNSHIP PLACEMENT SYSTEM";
        String subtitle = "NTU Career Center";
        
        System.out.println("\n" + line);
        // Center the title
        int titlePadding = (60 - title.length()) / 2;
        System.out.println("  " + " ".repeat(titlePadding) + title + " ".repeat(60 - title.length() - titlePadding - 2));
        // Center the subtitle
        int subtitlePadding = (60 - subtitle.length()) / 2;
        System.out.println("  " + " ".repeat(subtitlePadding) + subtitle + " ".repeat(60 - subtitle.length() - subtitlePadding - 2));
        System.out.println(line);
    }
    
    private void printMainMenu() {
        System.out.println("\n" + "─".repeat(60));
        System.out.println("  MAIN MENU");
        System.out.println("─".repeat(60));
        System.out.println("  1. Login");
        System.out.println("  2. Change Password");
        System.out.println("  3. Register as Company Representative");
        System.out.println("  4. Exit System");
        System.out.println("─".repeat(60));
        System.out.print("  Enter your choice: ");
    }
    
    private void printSection(String title) {
        System.out.println("\n" + "─".repeat(60));
        System.out.println("  " + title);
        System.out.println("─".repeat(60));
    }
    
    private void printError(String message) {
        System.out.println("\n⚠  ERROR: " + message);
    }
    
    private void printGoodbye() {
        System.out.println("\n" + "═".repeat(60));
        System.out.println("    Thank you for using the Internship Placement System!");
        System.out.println("                      Have a great day!");
        System.out.println("═".repeat(60) + "\n");
    }
    
    private void routeToController(User user) {
        if (user instanceof Student) {
            StudentController controller = new StudentController((Student) user, new StudentView());
            controller.run();
        } else if (user instanceof Staff) {
            StaffController controller = new StaffController((Staff) user, new StaffView());
            controller.run();
        } else if (user instanceof CompanyRepresentative) {
            CompanyRepresentativeController controller = 
                new CompanyRepresentativeController((CompanyRepresentative) user, new CompanyRepresentativeView());
            controller.run();
        }
    }
    
    private String[] parseCSVLine(String line) {
        List<String> cols = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder current = new StringBuilder();
        
        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                cols.add(current.toString().trim());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        cols.add(current.toString().trim());
        return cols.toArray(new String[0]);
    }
}

