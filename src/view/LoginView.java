package Assignment.src.view;

import Assignment.src.controller.*;
import java.util.Scanner;

/**
 * View for login, registration, and password change functionality.
 * Handles all UI logic and delegates business logic to LoginController.
 * MVC compliant: No model imports, returns user ID string instead of User object.
 */
public class LoginView {
    private static final Scanner sc = new Scanner(System.in);
    private LoginController controller;
    
    public LoginView(LoginController controller) {
        this.controller = controller;
    }
    
    /**
     * Display login dialog and handle login flow
     * @return User ID string if login successful, null otherwise
     */
    public String showLoginDialog() {
        System.out.println("\n=== Login ===");
        System.out.print("Enter user ID: ");
        String userID = sc.nextLine().trim();
        
        System.out.print("Enter password: ");
        String password = sc.nextLine();
        
        try {
            String profile = controller.authenticateAndGetProfile(userID, password);
            System.out.println("\nLogin successful!");
            System.out.println(profile);
            return userID;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Display change password dialog
     */
    public void showChangePasswordDialog() {
        System.out.println("\n=== Change Password ===");
        System.out.print("Enter user ID: ");
        String userID = sc.nextLine().trim();
        
        System.out.print("Enter current password: ");
        String oldPassword = sc.nextLine();
        
        System.out.print("Enter new password: ");
        String newPassword = sc.nextLine();
        
        try {
            controller.changePassword(userID, oldPassword, newPassword);
            System.out.println("Password changed successfully! You can now login with your new password.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    /**
     * Display registration dialog for company representatives
     */
    public void showRegistrationDialog() {
        System.out.println("\n=== Company Representative Registration ===");
        
        System.out.print("Enter your email (will be your user ID): ");
        String email = sc.nextLine().trim();
        
        System.out.print("Enter your name: ");
        String name = sc.nextLine().trim();
        
        System.out.print("Enter password: ");
        String password = sc.nextLine();
        
        System.out.print("Enter company name: ");
        String companyName = sc.nextLine().trim();
        
        System.out.print("Enter department: ");
        String department = sc.nextLine().trim();
        
        System.out.print("Enter position: ");
        String position = sc.nextLine().trim();
        
        try {
            controller.registerCompanyRepresentative(email, name, password, companyName, department, position);
            System.out.println("\nRegistration successful!");
            System.out.println("Your account is pending approval from Career Center Staff.");
            System.out.println("You will be able to login once your account has been approved.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}

