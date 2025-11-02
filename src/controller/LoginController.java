package Assignment.src.controller;

import Assignment.src.model.User;
import Assignment.src.model.CompanyRepresentative;
import Assignment.src.model.UserRegistry;

import java.util.Scanner;

public class LoginController {
    private final UserRegistry userRegistry;
    private final Scanner sc;
    
    public LoginController(UserRegistry userRegistry) {
        this.userRegistry = userRegistry;
        this.sc = new Scanner(System.in);
    }
    
    public User login() {
        System.out.println("\n=== Login ===");
        System.out.print("Enter user ID: ");
        String userID = sc.nextLine().trim();
        
        System.out.print("Enter password: ");
        String password = sc.nextLine();
        
        User user = userRegistry.findById(userID);
        
        if (user == null) {
            System.out.println("Invalid user ID. Please try again.");
            return null;
        }
        
        if (!user.verifyPassword(password)) {
            System.out.println("Incorrect password. Please try again.");
            return null;
        }
        
        // Check if company rep is approved
        if (user instanceof Assignment.src.model.CompanyRepresentative) {
            Assignment.src.model.CompanyRepresentative compRep = 
                (Assignment.src.model.CompanyRepresentative) user;
            if (!compRep.isApproved()) {
                System.out.println("Your account is pending approval. Please wait for Career Center Staff approval.");
                return null;
            }
        }
        
        user.setLoggedIn(true);
        System.out.println("\nLogin successful!");
        user.displayProfile();
        return user;
    }
    
    public void changePasswordAtLoginPage() {
        System.out.println("\n=== Change Password ===");
        System.out.print("Enter user ID: ");
        String userID = sc.nextLine().trim();
        
        User user = userRegistry.findById(userID);
        if (user == null) {
            System.out.println("Invalid user ID.");
            return;
        }
        
        System.out.print("Enter current password: ");
        String oldPassword = sc.nextLine();
        
        System.out.print("Enter new password: ");
        String newPassword = sc.nextLine();
        
        try {
            user.changePassword(oldPassword, newPassword);
            System.out.println("Password changed successfully! You can now login with your new password.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    public void registerCompanyRepresentative() {
        System.out.println("\n=== Company Representative Registration ===");
        
        System.out.print("Enter your email (will be your user ID): ");
        String email = sc.nextLine().trim();
        
        // Check if email already exists
        if (userRegistry.findById(email) != null) {
            System.out.println("An account with this email already exists. Please login instead.");
            return;
        }
        
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
        
        // Create new company representative (not approved by default)
        CompanyRepresentative compRep = new CompanyRepresentative(
            email, name, password, email, companyName, department, position, "Pending"
        );
        
        if (userRegistry.register(compRep)) {
            // Save to CSV file for persistence
            userRegistry.saveCompanyRepToCsv(compRep);
            System.out.println("\nRegistration successful!");
            System.out.println("Your account is pending approval from Career Center Staff.");
            System.out.println("You will be able to login once your account has been approved.");
        } else {
            System.out.println("Registration failed. Please try again.");
        }
    }
}

