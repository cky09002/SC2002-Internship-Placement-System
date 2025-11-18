package view;

import controller.CompanyRepresentativeController;
import model.CompanyRepresentative;

/**
 * Concrete strategy for company representative dashboard.
 * Encapsulates company representative-specific dashboard behavior.
 * 
 * @author NTU SC2002 Group
 * @version 1.0
 * @since 2025-11-18
 */
public class CompanyRepDashboardStrategy implements DashboardStrategy {
    /** Controller managing company representative-specific operations */
    private final CompanyRepresentativeController controller;
    /** View displaying company representative dashboard */
    private final CompanyRepresentativeView view;
    
    /**
     * Constructs a CompanyRepDashboardStrategy for the given company representative.
     * 
     * @param companyRep The authenticated company representative user
     */
    public CompanyRepDashboardStrategy(CompanyRepresentative companyRep) {
        this.controller = new CompanyRepresentativeController(companyRep);
        this.view = new CompanyRepresentativeView(controller);
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
