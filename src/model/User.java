package model;

import utils.validation.ValidationHelper;

/**
 * Abstract base class representing a user in the Internship Placement System.
 * This class provides common functionality for all user types including authentication,
 * profile management, and session handling.
 * 
 * @author NTU SC2002 Group
 * @version 1.0
 * @since 2025-11-16
 */
public abstract class User {
    /** Unique identifier for the user */
    private String userID;
    /** Display name of the user */
    private String name;
    /** Encrypted password for authentication */
    private String password;
    /** Email address of the user */
    private String email;
    /** Current login status */
    private boolean loggedIn = false;

    /**
     * Constructs a new User with the specified credentials.
     * 
     * @param userID unique identifier for the user
     * @param name display name of the user
     * @param password authentication password
     * @param email email address
     */
    public User(String userID, String name, String password, String email){
        this.userID = userID;
        this.name = name;
        this.password = password;
        this.email = email;
    }

    /**
     * Gets the unique user identifier.
     * @return the user ID
     */
    public String getUserID() { return userID; }
    
    /**
     * Gets the user's display name.
     * @return the name
     */
    public String getName() { return name; }
    
    /**
     * Gets the user's email address.
     * @return the email
     */
    public String getEmail(){ return email; }
    
    /**
     * Gets the user's password (protected access).
     * @return the password
     */
    protected String getPassword() { return password; }
    
    /**
     * Gets the user's password for persistence operations.
     * Used by CSV handlers to save password data.
     * @return the password
     */
    public String getPasswordForPersistence() { return password; }
    
    /**
     * Checks if the user is currently logged in.
     * @return true if logged in, false otherwise
     */
    public boolean isLoggedIn() { return loggedIn; }
    
    /**
     * Sets the user's display name.
     * @param name the new name (must not be empty)
     * @throws IllegalArgumentException if name is empty or null
     */
    public void setName(String name) {
        ValidationHelper.validateNotEmpty(name, "Name");
        this.name = name;
    }
    
    /**
     * Sets the user's email address.
     * @param email the new email (must be valid format)
     * @throws IllegalArgumentException if email format is invalid
     */
    public void setEmail(String email) {
        ValidationHelper.validateEmail(email);
        this.email = email;
    }

    /**
     * Sets the user's login status.
     * @param loggedIn true to mark as logged in, false otherwise
     */
    public void setLoggedIn(boolean loggedIn) { this.loggedIn = loggedIn; }
    
    /**
     * Verifies if the provided password matches the user's password.
     * @param pw the password to verify
     * @return true if password matches, false otherwise
     */
    public boolean verifyPassword(String pw) { return pw != null && pw.equals(this.password); }
    
    /**
     * Logs out the user by setting their login status to false.
     */
    public void logout() {
        setLoggedIn(false);
    }
    
    /**
     * Changes the user's password after verifying the old password.
     * @param oldPassword the current password for verification
     * @param newPassword the new password to set
     * @throws IllegalArgumentException if old password is incorrect or new password is empty
     */
    public void changePassword(String oldPassword, String newPassword) {
        if (!verifyPassword(oldPassword)) {
            throw new IllegalArgumentException("Incorrect old password.");
        }
        ValidationHelper.validateNotEmpty(newPassword, "New password");
        this.password = newPassword;
    }

    /**
     * Gets the user type as a string.
     * Must be implemented by subclasses to identify their specific user type.
     * @return the user type (e.g., "Student", "Staff", "CompanyRepresentative")
     */
    public abstract String getUserType();
    
    /**
     * Gets the CSV filename for persisting this user type.
     * Uses polymorphism to eliminate instanceof checks.
     * Must be implemented by subclasses to return their specific CSV file path.
     * 
     * @return the CSV file path for this user type
     */
    public abstract String getCsvFilename();
    
    /**
     * Creates the appropriate dashboard strategy for this user type.
     * Uses factory method pattern to eliminate instanceof checks in ViewFactory.
     * Each subclass knows how to create its own dashboard strategy.
     * 
     * @return DashboardStrategy for this user type
     */
    public abstract view.DashboardStrategy createDashboardStrategy();
}
