package controller;

import utils.filter.FilterSettings;

/**
 * Interface defining common operations for all user controllers.
 * Follows Interface Segregation Principle - minimal interface with only essential methods.
 * Enforces consistency across Student, Staff, and CompanyRepresentative controllers.
 */
public interface UserController {
    /**
     * Checks if the user is currently logged in.
     * @return true if user is logged in, false otherwise
     */
    boolean isLoggedIn();
    
    /**
     * Logs out the current user.
     */
    void logout();
    
    /**
     * Gets the formatted profile text for the current user.
     * @return Formatted profile string
     */
    String getProfile();
    
    /**
     * Gets the filter settings instance for this controller.
     * @return The FilterSettings object
     */
    FilterSettings getFilterSettings();
    
    /**
     * Gets detailed information about a specific internship.
     * Authorization rules vary by user type.
     * @param internshipID The ID of the internship
     * @return Formatted internship details
     * @throws IllegalArgumentException if user doesn't have permission
     */
    String getInternshipDetails(int internshipID);
}
