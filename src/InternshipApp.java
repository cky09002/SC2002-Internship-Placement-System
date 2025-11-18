import view.*;
import controller.*;
import model.*;
import constant.*;
import utils.factory.UserFactory;
import utils.formatter.ViewFormatter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Main application controller for the Internship Placement System.
 * Handles user authentication, CSV data loading, and menu navigation.
 * Implements the main application loop and coordinates between views and controllers.
 * 
 * @author NTU SC2002 Group
 * @version 1.0
 * @since 2025-11-16
 */
public class InternshipApp {
    /** View component for login functionality */
    private LoginView loginView;
    
    /** Scanner for user input */
    private Scanner sc;
    
    /**
     * Constructs a new InternshipApp instance.
     * Initializes the login view, scanner, and loads all data from CSV files.
     * Creates a LoginController with the UserRegistry singleton instance.
     * Loads data in order: Users → Internships → Applications (due to dependencies).
     */
    public InternshipApp() {
        LoginController loginController = new LoginController(UserRegistry.getInstance());
        this.loginView = new LoginView(loginController);
        this.sc = new Scanner(System.in);
        loadUsers();
        loadInternships();
        loadApplications();
    }
    
    /**
     * Loads all users from CSV files.
     * Reads student, staff, and company representative data from their respective CSV files.
     * Displays loading progress and total count of successfully loaded users.
     */
    private void loadUsers() {
        showLoadingDots("Loading users from CSV files");
        int total = loadUserType("sample_file/sample_student_list.csv", UserType.STUDENT, 6, "Students") +
                    loadUserType("sample_file/sample_staff_list.csv", UserType.STAFF, 6, "Staff") +
                    loadUserType("sample_file/sample_company_representative_list.csv", UserType.COMPANY_REPRESENTATIVE, 8, "Company Representatives");
        System.out.println("\n═══ " + total + " users loaded successfully ═══\n");
    }
    
    /**
     * Loads users of a specific type from a CSV file.
     * Parses each line, validates the column count, and registers users in the UserRegistry.
     * 
     * @param file Path to the CSV file containing user data
     * @param type Type of user to load (STUDENT, STAFF, or COMPANY_REPRESENTATIVE)
     * @param minCols Minimum number of columns required for valid data
     * @param label Display label for user type in console output
     * @return Number of users successfully loaded and registered
     */
    private int loadUserType(String file, UserType type, int minCols, String label) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine();
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
    
    /**
     * Displays a loading animation with dots.
     * Shows the provided message followed by three animated dots with brief pauses.
     * 
     * @param message The loading message to display
     */
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
    
    /**
     * Runs the main application loop.
     * Displays the welcome banner and main menu, then processes user input.
     * Continues until the user selects exit option.
     * Handles input validation and error recovery.
     */
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
    
    /**
     * Handles user login process.
     * Displays login dialog and creates appropriate view based on user type upon successful authentication.
     */
    private void handleLogin() {
        printSection("USER LOGIN");
        String userID = loginView.showLoginDialog();
        if (userID != null) {
            User user = UserRegistry.getInstance().findById(userID);
            if (user != null) {
                ViewFactory.createAndRunView(user);
            }
        }
    }
    
    /**
     * Handles password change process.
     * Displays password change dialog for user to update their credentials.
     */
    private void handleChangePassword() {
        printSection("CHANGE PASSWORD");
        loginView.showChangePasswordDialog();
    }
    
    /**
     * Handles company representative registration process.
     * Displays registration dialog for new company representatives to create accounts.
     */
    private void handleRegistration() {
        printSection("COMPANY REPRESENTATIVE REGISTRATION");
        loginView.showRegistrationDialog();
    }
    
    /**
     * Prints the welcome banner with application title.
     * Displays a formatted header with the system name and organization.
     */
    private void printWelcomeBanner() {
        String border = "═".repeat(120);
        System.out.println(border);
        System.out.println(ViewFormatter.centerText("INTERNSHIP PLACEMENT SYSTEM", 120));
        System.out.println(ViewFormatter.centerText("NTU Career Center", 120));
        System.out.println(border);
    }
    
    /**
     * Prints the main menu options.
     * Displays all available actions: Login, Change Password, Register, and Exit.
     */
    private void printMainMenu() {
        String border = "═".repeat(120);
        System.out.println(border);
        System.out.println("  MAIN MENU");
        System.out.println(border);
        System.out.println("  1. Login\n  2. Change Password\n  3. Register as Company Representative\n  4. Exit System");
        System.out.print(border + "\n  Enter your choice: ");
    }
    
    /**
     * Prints a section header with the specified title.
     * 
     * @param title The title text to display in the header
     */
    private void printSection(String title) {
        String border = "─".repeat(120);
        ViewFormatter.displayHeader(title, border, 120);
    }
    
    /**
     * Prints an error message in a formatted style.
     * 
     * @param message The error message to display
     */
    private void printError(String message) {
        System.out.println("\n⚠  ERROR: " + message);
    }
    
    /**
     * Prints a goodbye message when user exits the system.
     * Displays a farewell message with decorative borders.
     */
    private void printGoodbye() {
        String border = "═".repeat(120);
        System.out.println("\n" + border + "\n    Thank you for using the Internship Placement System!\n                      Have a great day!\n" + border + "\n");
    }
    
    /**
     * Loads all internships from CSV file.
     * Must be called after users are loaded (internships reference company representatives).
     */
    private void loadInternships() {
        loadDataFromCsv(
            "Loading internships from CSV",
            () -> {
                model.Internship.clearAll();
                utils.csv.InternshipCsvHandler.getInstance().loadFromCsv();
                return model.Internship.getAllInternships().size();
            },
            "Internships"
        );
    }
    
    /**
     * Loads all applications from CSV file.
     * Must be called after both users and internships are loaded (applications reference both).
     */
    private void loadApplications() {
        loadDataFromCsv(
            "Loading applications from CSV",
            () -> {
                model.Application.clearAll();
                utils.csv.ApplicationCsvHandler.getInstance().loadFromCsv();
                return model.Application.getAllApplications().size();
            },
            "Applications"
        );
    }
    
    /**
     * Generic method to load data from CSV with consistent error handling and display.
     * 
     * @param loadingMessage Message to display while loading
     * @param loader Runnable that clears data, loads from CSV, and returns count
     * @param dataLabel Label for the data type (for success message)
     */
    private void loadDataFromCsv(String loadingMessage, java.util.function.Supplier<Integer> loader, String dataLabel) {
        showLoadingDots(loadingMessage);
        try {
            int count = loader.get();
            System.out.println("✓ " + dataLabel + ": " + count + " loaded");
        } catch (Exception e) {
            System.out.println("✗ Error loading " + dataLabel.toLowerCase() + ": " + e.getMessage());
        }
    }
    
    /**
     * Parses a CSV line into an array of strings.
     * Handles quoted fields containing commas correctly.
     * 
     * @param line The CSV line to parse
     * @return Array of strings representing the fields in the CSV line
     */
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

