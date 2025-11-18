package controller;

import model.*;
import utils.validation.ValidationHelper;
import utils.formatter.UserFormatter;
import utils.csv.UserCsvHandler;
import utils.csv.UserCsvHandlerInterface;
import controller.interfaces.*;
import constant.StaffApprovalStatus;

import java.util.List;

/**
 * Controller for handling user authentication and registration operations.
 * Manages login, password changes, and company representative registration.
 * Follows MVC pattern by separating business logic from presentation.
 * 
 * Follows Dependency Inversion Principle by depending on UserCsvHandlerInterface abstraction.
 * Implements LoginControllerInterface to allow clients to depend on abstraction.
 * 
 * @author NTU SC2002 Group
 * @version 1.0
 * @since 2025-11-16
 */
public class LoginController implements LoginControllerInterface {
    /** The user registry containing all registered users */
    private final UserRegistry userRegistry;
    /** CSV handler for user persistence operations */
    private final UserCsvHandlerInterface userCsvHandler;
    
    /**
     * Constructs a LoginController with the specified user registry and CSV handler.
     * Follows Dependency Inversion Principle - depends on abstraction, not concretion.
     * 
     * @param userRegistry The registry managing all user accounts
     * @param userCsvHandler The CSV handler interface for user persistence operations
     */
    public LoginController(UserRegistry userRegistry, UserCsvHandlerInterface userCsvHandler) {
        this.userRegistry = userRegistry;
        this.userCsvHandler = userCsvHandler;
    }
    
    /**
     * Constructs a LoginController with the specified user registry.
     * Uses singleton UserCsvHandler for backward compatibility.
     * 
     * @param userRegistry The registry managing all user accounts
     */
    public LoginController(UserRegistry userRegistry) {
        this(userRegistry, UserCsvHandler.getInstance());
    }
    
    /**
     * Get formatted profile string for a user.
     * @param user the user whose profile to format
     * @return formatted profile string
     */
    public String getFormattedProfile(User user) {
        return UserFormatter.formatProfile(user);
    }
    
    /**
     * Authenticate user and get profile - returns profile string for MVC compliance
     * @param userID User ID
     * @param password Password
     * @return Formatted profile string
     */
    public String authenticateAndGetProfile(String userID, String password) {
        User user = authenticate(userID, password);
        return getFormattedProfile(user);
    }
    
    /**
     * Authenticate user - returns User object (used internally and by ViewFactory).
     * 
     * @param userID the user's unique identifier
     * @param password the user's password
     * @return authenticated User object
     * @throws IllegalArgumentException if credentials are invalid or account not approved
     */
    @Override
    public User authenticate(String userID, String password) {
        User user = userRegistry.findById(userID);
        if (user == null) throw new IllegalArgumentException("Invalid user ID. Please try again.");
        if (!user.verifyPassword(password)) throw new IllegalArgumentException("Incorrect password. Please try again.");
        
        if (user instanceof CompanyRepresentative && !((CompanyRepresentative) user).isApproved()) {
            throw new IllegalArgumentException("Your account is pending approval. Please wait for Career Center Staff approval.");
        }
        user.setLoggedIn(true);
        return user;
    }
    
    /**
     * Changes a user's password after verifying the old password.
     * @param userID the user's unique identifier
     * @param oldPassword the current password for verification
     * @param newPassword the new password to set
     * @return true if password changed successfully
     * @throws IllegalArgumentException if user not found or old password incorrect
     */
    @Override
    public boolean changePassword(String userID, String oldPassword, String newPassword) {
        User user = userRegistry.findById(userID);
        if (user == null) throw new IllegalArgumentException("Invalid user ID.");
        user.changePassword(oldPassword, newPassword);
        userCsvHandler.savePasswordChangeToCsv(user);
        return true;
    }

    /**
     * Registers a new company representative account.
     * @param email the representative's email (used as user ID)
     * @param name the representative's name
     * @param password the account password
     * @param companyName the company name
     * @param department the department within the company
     * @param position the representative's job position
     * @return true if registration successful
     * @throws IllegalArgumentException if email invalid or already exists
     */
    @Override
    public boolean registerCompanyRepresentative(String email, String name, String password, 
                                                 String companyName, String department, String position) {
        ValidationHelper.validateEmail(email);
        if (userRegistry.findById(email) != null) {
            throw new IllegalArgumentException("An account with this email already exists. Please login instead.");
        }
        
        CompanyRepresentative compRep = new CompanyRepresentative(
            email, name, password, email, companyName, department, position, "Pending");
        
        if (userRegistry.register(compRep)) {
            userCsvHandler.saveCompanyRepToCsv(compRep);
            return true;
        }
        throw new IllegalArgumentException("Registration failed. Please try again.");
    }
    
    /**
     * Check if user exists by ID.
     * @param userID the user ID to check
     * @return true if user exists, false otherwise
     */
    public boolean userExists(String userID) {
        return userRegistry.findById(userID) != null;
    }
    
    /**
     * Find user by ID - reusable method for other controllers.
     * @param userID the user ID to search for
     * @return the User object if found, null otherwise
     */
    public User findUserById(String userID) {
        return userRegistry.findById(userID);
    }
    
    /**
     * Get all company representatives - for StaffController.
     * @return list of all company representatives
     */
    @Override
    public List<CompanyRepresentative> getAllCompanyReps() {
        return userRegistry.getAllCompanyReps();
    }
    
    /**
     * Approve/reject company representative - for StaffController.
     * @param repID the company representative ID
     * @param approve true to approve, false to reject
     */
    @Override
    public void approveRejectCompanyRep(String repID, boolean approve) {
        User rep = userRegistry.findById(repID);
        if (rep == null || !(rep instanceof CompanyRepresentative)) {
            throw new IllegalArgumentException("Company representative not found.");
        }
        
        CompanyRepresentative compRep = (CompanyRepresentative) rep;
        compRep.setApprovalStatus(approve ? StaffApprovalStatus.APPROVED : StaffApprovalStatus.REJECTED);
        userCsvHandler.updateCompanyRepStatusInCsv(compRep);
    }
}

