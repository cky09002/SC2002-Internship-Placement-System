package model;

import java.util.HashMap;
import java.util.Map;

/**
 * Central database for all users in the system.
 * Uses the Singleton pattern - only one instance exists throughout the application.
 * Manages in-memory user storage and retrieval.
 * 
 * Note: CSV persistence is handled by UserCsvHandler in utils/csv/ (MVC compliance).
 * 
 * @author NTU SC2002 Group
 * @version 1.0
 * @since 2025-11-16
 */
public class UserRegistry {
    /** Singleton instance of UserRegistry */
    private static UserRegistry instance;
    /** Map of user IDs to User objects */
    private final Map<String,User> users = new HashMap<>();
    
    /** Private constructor to enforce singleton pattern */
    private UserRegistry() {}
    
    /**
     * Get the single instance of UserRegistry.
     * Called from anywhere in the app that needs to access user data.
     * @return the singleton UserRegistry instance
     */
    public static UserRegistry getInstance() {
        if (instance == null) {
            instance = new UserRegistry();
        }
        return instance;
    }

    /**
     * Add a new user to the registry.
     * Called from InternshipApp when loading users from CSV files.
     * @param user the user to register
     * @return true if registration successful, false otherwise
     */
    public boolean register(User user){
        if(user == null || user.getUserID() == null) return false;
        if (users.containsKey(user.getUserID())) return false;
        users.put(user.getUserID(),user);
        return true;
    }

    /**
     * Find a user by their ID.
     * Called from LoginController during login and password changes.
     * @param userID the user ID to search for
     * @return the User object if found, null otherwise
     */
    public User findById(String userID){
        if (userID == null) return null;
        return users.get(userID);
    }
    
    /**
     * Get all users in the system.
     * Used for clearing application references when reloading from CSV.
     * @return collection of all users
     */
    public java.util.Collection<User> getAllUsers() {
        return new java.util.ArrayList<>(users.values());
    }
    
    /**
     * Get all company representatives in the system.
     * Called from StaffController to show the list of company reps for approval/rejection.
     * @return list of all company representatives
     */
    public java.util.List<CompanyRepresentative> getAllCompanyReps() {
        return users.values().stream()
                .filter(u -> u instanceof CompanyRepresentative)
                .map(u -> (CompanyRepresentative) u)
                .collect(java.util.stream.Collectors.toList());
    }
    
}