package view;

import controller.StaffController;
import model.Staff;
import view.interfaces.DashboardStrategy;

/**
 * Concrete strategy for staff dashboard.
 * Encapsulates staff-specific dashboard behavior.
 * 
 * @author NTU SC2002 Group
 * @version 1.0
 * @since 2025-11-18
 */
public class StaffDashboardStrategy implements DashboardStrategy {
    /** Controller managing staff-specific operations */
    private final StaffController controller;
    /** View displaying staff dashboard */
    private final StaffView view;
    
    /**
     * Constructs a StaffDashboardStrategy for the given staff member.
     * 
     * @param staff The authenticated staff user
     */
    public StaffDashboardStrategy(Staff staff) {
        this.controller = new StaffController(staff);
        this.view = new StaffView(controller);
    }
    
    @Override
    public void runDashboard() {
        view.run();
    }
    
    @Override
    public boolean isLoggedIn() {
        return controller.isLoggedIn();
    }
}
