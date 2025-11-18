package controller.interfaces;

import model.*;

import java.util.List;

/**
 * Interface for authentication and user management operations.
 * Follows Dependency Inversion Principle - clients depend on this abstraction, not concrete implementation.
 * 
 * @author NTU SC2002 Group
 * @version 1.0
 * @since 2025-11-16
 */
public interface LoginControllerInterface {
    /**
     * Authenticates a user with the provided credentials.
     * 
     * @param userID The user ID
     * @param password The password
     * @return The authenticated User object
     * @throws IllegalArgumentException if authentication fails
     */
    User authenticate(String userID, String password);
    
    /**
     * Changes a user's password.
     * 
     * @param userID The user ID
     * @param oldPassword The current password
     * @param newPassword The new password
     * @return true if password changed successfully
     * @throws IllegalArgumentException if user not found or old password incorrect
     */
    boolean changePassword(String userID, String oldPassword, String newPassword);
    
    /**
     * Registers a new company representative account.
     * 
     * @param email The representative's email (used as user ID)
     * @param name The representative's name
     * @param password The password
     * @param companyName The company name
     * @param department The department
     * @param position The position
     * @return true if registration successful
     * @throws IllegalArgumentException if registration fails
     */
    boolean registerCompanyRepresentative(String email, String name, String password, 
                                          String companyName, String department, String position);
    
    /**
     * Approves or rejects a company representative account.
     * 
     * @param repID The ID of the company representative
     * @param approve true to approve, false to reject
     * @throws IllegalArgumentException if company representative not found
     */
    void approveRejectCompanyRep(String repID, boolean approve);
    
    /**
     * Gets all company representatives in the system.
     * 
     * @return List of all company representatives
     */
    List<CompanyRepresentative> getAllCompanyReps();
}

