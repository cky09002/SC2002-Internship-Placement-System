package view;

import model.*;
import view.interfaces.DashboardStrategy;

/**
 * Factory class for creating dashboard strategies based on user type.
 * Follows Factory Pattern and SOLID principles:
 * - Single Responsibility: Only creates strategies
 * - Open/Closed: New user types can be added by creating new strategy classes
 * - Dependency Inversion: Returns DashboardStrategy interface, not concrete classes
 * 
 * This refactored design eliminates if-else chains and makes the system extensible.
 * 
 * @author NTU SC2002 Group
 * @version 1.0
 * @since 2025-11-18
 */
public class ViewFactory {
    
    /**
     * Private constructor to prevent instantiation.
     * This is a utility class with only static methods.
     */
    private ViewFactory() {
        throw new AssertionError("Utility class should not be instantiated");
    }
    
    /**
     * Creates the appropriate dashboard strategy based on user type.
     * Uses factory method pattern on User class to eliminate instanceof checks.
     * Follows Open/Closed Principle - new user types can be added without modifying this method.
     * 
     * @param user The authenticated user (obtained from LoginController)
     * @return DashboardStrategy for the user type
     */
    public static DashboardStrategy createDashboardStrategy(User user) {
        return user.createDashboardStrategy();
    }
    
    /**
     * Creates and runs the appropriate dashboard based on user type.
     * This is a convenience method that combines strategy creation and execution.
     * 
     * @param user The authenticated user (obtained from LoginController)
     */
    public static void createAndRunView(User user) {
        DashboardStrategy strategy = createDashboardStrategy(user);
        DashboardContext context = new DashboardContext(strategy);
        context.executeDashboard();
    }
}

