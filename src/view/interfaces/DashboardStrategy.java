package view.interfaces;

/**
 * Strategy interface for different user dashboard behaviors.
 * Follows Strategy Pattern and Open/Closed Principle - new user types can be added
 * without modifying existing code.
 * 
 * This interface defines the contract for all user-specific dashboard implementations.
 * Each user type (Student, Staff, Company Representative) has its own strategy.
 * 
 * @author NTU SC2002 Group
 * @version 1.0
 * @since 2025-11-18
 */
public interface DashboardStrategy {
    /**
     * Runs the dashboard loop for the specific user type.
     * This method encapsulates all user-specific dashboard behavior.
     */
    void runDashboard();
    
    /**
     * Checks if the user is still logged in.
     * @return true if user is logged in, false otherwise
     */
    boolean isLoggedIn();
}
