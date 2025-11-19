package view;

import view.interfaces.DashboardStrategy;

/**
 * Context class for dashboard strategy pattern.
 * Delegates dashboard execution to the appropriate strategy based on user type.
 * Follows Strategy Pattern - decouples dashboard behavior from user type checking.
 * 
 * @author NTU SC2002 Group
 * @version 1.0
 * @since 2025-11-18
 */
public class DashboardContext {
    /** The dashboard strategy to execute */
    private final DashboardStrategy strategy;
    
    /**
     * Constructs a DashboardContext with the specified strategy.
     * 
     * @param strategy The dashboard strategy for the specific user type
     */
    public DashboardContext(DashboardStrategy strategy) {
        this.strategy = strategy;
    }
    
    /**
     * Executes the dashboard using the configured strategy.
     */
    public void executeDashboard() {
        strategy.runDashboard();
    }
}
