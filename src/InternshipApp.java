package Assignment.src;

import Assignment.src.view.*;
import Assignment.src.controller.*;
import Assignment.src.model.*;
import Assignment.src.constant.*;
import Assignment.src.utils.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class InternshipApp {
    private LoginView loginView;
    private Scanner sc;
    
    public InternshipApp() {
        // Application layer can access models - create LoginController with UserRegistry
        LoginController loginController = new LoginController(UserRegistry.getInstance());
        this.loginView = new LoginView(loginController);
        this.sc = new Scanner(System.in);
        loadUsers();
    }
    
    private void loadUsers() {
        showLoadingDots("Loading users from CSV files");
        int total = loadUserType("sample_file/sample_student_list.csv", UserType.STUDENT, 6, "Students") +
                    loadUserType("sample_file/sample_staff_list.csv", UserType.STAFF, 6, "Staff") +
                    loadUserType("sample_file/sample_company_representative_list.csv", UserType.COMPANY_REPRESENTATIVE, 8, "Company Representatives");
        System.out.println("\n═══ " + total + " users loaded successfully ═══\n");
    }
    
    private int loadUserType(String file, UserType type, int minCols, String label) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine(); // skip header
            int count = reader.lines()
                .filter(line -> !line.trim().isEmpty())
                .map(this::parseCSVLine)
                .filter(cols -> cols.length >= minCols)
                .mapToInt(cols -> UserRegistry.getInstance().register(UserFactory.fromCsv(type, cols)) ? 1 : 0)
                .sum();
            System.out.println("✓ " + label + ": " + count + " loaded");
            return count;
        } catch (IOException e) {
            System.out.println("✗ Error loading " + label.toLowerCase() + ": " + e.getMessage());
            return 0;
        }
    }
    
    private void showLoadingDots(String message) {
        System.out.print(message);
        try {
            for (int i = 0; i < 3; i++) {
                Thread.sleep(200);
                System.out.print(".");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println();
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
                
                switch (choice) {
                    case 1 -> handleLogin();
                    case 2 -> handleChangePassword();
                    case 3 -> handleRegistration();
                    case 4 -> { printGoodbye(); return; }
                    default -> printError("Invalid choice! Please select a number between 1-4.");
                }
            } catch (Exception e) {
                printError("Error: " + e.getMessage());
                if (sc.hasNextLine()) sc.nextLine();
            }
        }
    }
    
    private void handleLogin() {
        printSection("USER LOGIN");
        String userID = loginView.showLoginDialog();
        if (userID != null) {
            // Get user from registry (application layer can access models)
            User user = UserRegistry.getInstance().findById(userID);
            if (user != null) {
                ViewFactory.createAndRunView(user);
            }
        }
    }
    
    private void handleChangePassword() {
        printSection("CHANGE PASSWORD");
        loginView.showChangePasswordDialog();
    }
    
    private void handleRegistration() {
        printSection("COMPANY REPRESENTATIVE REGISTRATION");
        loginView.showRegistrationDialog();
    }
    
    private void printWelcomeBanner() {
        String border = "═".repeat(120);
        System.out.println(border);
        System.out.println(ViewFormatter.centerText("INTERNSHIP PLACEMENT SYSTEM", 120));
        System.out.println(ViewFormatter.centerText("NTU Career Center", 120));
        System.out.println(border);
    }
    
    private void printMainMenu() {
        String border = "═".repeat(120);
        System.out.println(border);
        System.out.println("  MAIN MENU");
        System.out.println(border);
        System.out.println("  1. Login\n  2. Change Password\n  3. Register as Company Representative\n  4. Exit System");
        System.out.print(border + "\n  Enter your choice: ");
    }
    
    private void printSection(String title) {
        String border = "─".repeat(120);
        ViewFormatter.displayHeader(title, border, 120);
    }
    
    private void printError(String message) {
        System.out.println("\n⚠  ERROR: " + message);
    }
    
    private void printGoodbye() {
        String border = "═".repeat(120);
        System.out.println("\n" + border + "\n    Thank you for using the Internship Placement System!\n                      Have a great day!\n" + border + "\n");
    }
    
    private String[] parseCSVLine(String line) {
        List<String> cols = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder current = new StringBuilder();
        for (char c : line.toCharArray()) {
            if (c == '"') inQuotes = !inQuotes;
            else if (c == ',' && !inQuotes) {
                cols.add(current.toString().trim());
                current = new StringBuilder();
            } else current.append(c);
        }
        cols.add(current.toString().trim());
        return cols.toArray(new String[0]);
    }
}

