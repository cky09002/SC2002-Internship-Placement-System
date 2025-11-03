package Assignment.src.controller;

import Assignment.src.model.*;
import Assignment.src.utils.ValidationHelper;
import Assignment.src.utils.UserFormatter;

import java.util.List;

public class LoginController {
    private final UserRegistry userRegistry;
    
    public LoginController(UserRegistry userRegistry) {
        this.userRegistry = userRegistry;
    }
    
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
     * Authenticate user - returns User object (used internally and by ViewFactory)
     */
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
    
    public boolean changePassword(String userID, String oldPassword, String newPassword) {
        User user = userRegistry.findById(userID);
        if (user == null) throw new IllegalArgumentException("Invalid user ID.");
        user.changePassword(oldPassword, newPassword);
        return true;
    }

    public boolean registerCompanyRepresentative(String email, String name, String password, 
                                                 String companyName, String department, String position) {
        ValidationHelper.validateEmail(email);
        if (userRegistry.findById(email) != null) {
            throw new IllegalArgumentException("An account with this email already exists. Please login instead.");
        }
        
        CompanyRepresentative compRep = new CompanyRepresentative(
            email, name, password, email, companyName, department, position, "Pending");
        
        if (userRegistry.register(compRep)) {
            userRegistry.saveCompanyRepToCsv(compRep);
            return true;
        }
        throw new IllegalArgumentException("Registration failed. Please try again.");
    }
    
    /**
     * Find user by ID - reusable method for other controllers
     */
    public User findUserById(String userID) {
        return userRegistry.findById(userID);
    }
    
    /**
     * Get all company representatives - for StaffController
     */
    public List<CompanyRepresentative> getAllCompanyReps() {
        return userRegistry.getAllCompanyReps();
    }
    
    /**
     * Approve/reject company representative - for StaffController
     */
    public void approveRejectCompanyRep(String repID, boolean approve) {
        User rep = userRegistry.findById(repID);
        if (rep == null || !(rep instanceof CompanyRepresentative)) {
            throw new IllegalArgumentException("Company representative not found.");
        }
        
        CompanyRepresentative compRep = (CompanyRepresentative) rep;
        compRep.setApproved(approve);
        userRegistry.updateCompanyRepStatusInCsv(compRep);
    }
}

