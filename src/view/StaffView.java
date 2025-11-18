package view;

import controller.StaffController;
import java.util.HashMap;
import java.util.Map;

/**
 * View class for career center staff dashboard and operations.
 * Handles all UI interactions for staff users including approving/rejecting
 * company representatives, internship listings, and withdrawal requests.
 * Extends BaseView for common functionality and follows MVC architecture.
 * 
 * @author NTU SC2002 Group
 * @version 1.0
 * @since 2025-11-16
 */
public class StaffView extends BaseView {
    /** The controller managing staff-specific business logic */
    private StaffController controller;

    /**
     * Constructs a new StaffView with the specified controller.
     * 
     * @param controller The staff controller handling business logic
     */
    public StaffView(StaffController controller) {
        super(controller::isLoggedIn);
        this.controller = controller;
    }
    
    /**
     * Retrieves detailed information for a specific internship.
     * Used by the filter menu for previewing internships.
     * 
     * @param internshipID The ID of the internship
     * @return Formatted string containing internship details
     */
    @Override
    protected String getInternshipDetailsForFilter(int internshipID) {
        return controller.getInternshipDetails(internshipID);
    }
    
    /**
     * Displays the initial view showing all internship opportunities.
     * Shows all internships in the system with status information.
     * Staff can view all internships regardless of status or visibility.
     */
    @Override
    protected void showInitialInternshipsView() {
        showInternshipsLoop("INTERNSHIP OPPORTUNITIES",
            () -> controller.internshipController.getAllInternships(controller.getFilterSettings()),
            "No internships available.", controller.getFilterSettings(),
            controller::getInternshipDetails, null, null, true);
    }

    /**
     * Initializes the menu options for the staff dashboard.
     * Creates a map of menu choices to their corresponding actions including:
     * profile management, approval workflows, and internship filtering.
     * 
     * @return Map of menu option numbers to MenuOption objects
     */
    @Override
    protected Map<Integer, MenuOption> initializeMenuOptions() {
        Map<Integer, MenuOption> options = new HashMap<>();
        options.put(1, new MenuOption("view my profile", this::handleViewProfile));
        options.put(2, new MenuOption("edit my profile", this::handleEditProfile));
        options.put(3, new MenuOption("approve/reject company representative", this::handleApproveRejectCompanyRep));
        options.put(4, new MenuOption("filter internships", () -> handleFilterInternships(controller.getFilterSettings(), () -> controller.internshipController.getAllInternships())));
        options.put(5, new MenuOption("approve/reject student withdrawal", this::handleApproveRejectWithdrawal));
        options.put(6, new MenuOption("approve/reject internship listing", this::handleApproveRejectInternship));
        options.put(7, new MenuOption("list internship opportunities", this::handleListInternshipOpportunities));
        options.put(0, new MenuOption("log out", controller::logout));
        return options;
    }

    /**
     * Gets the title for the staff dashboard.
     * 
     * @return The dashboard title string
     */
    @Override
    protected String getDashboardTitle() {
        return "CAREER CENTER STAFF DASHBOARD";
    }

    /**
     * Retrieves the formatted profile text for the current staff member.
     * 
     * @return Formatted string containing staff profile information
     */
    @Override
    protected String getProfileText() {
        return controller.getProfile();
    }
    
    /**
     * Handles the profile editing workflow for staff members.
     * Allows editing of name, email, and department fields.
     * Prompts user to select fields to edit and collects new values.
     */
    @Override
    protected void editProfileFields() {
        String[] menu = {"Name", "Email", "Department"};
        String[] prompts = {"Name", "Email", "Department"};
        int[] indices = displayMenuAndGetSelection(menu, "  Enter field numbers to edit (comma-separated, e.g., 1,3): ");
        if (checkEditCancelled(indices)) return;
        
        String[] fields = collectFieldValues(indices, prompts, idx -> false); // No integer fields
        
        String name = fields[0];
        String email = fields[1];
        String department = fields[2];
        
        controller.editProfile(name, email, department);
        System.out.println("\nProfile updated successfully!");
    }

    /**
     * Handles the approval/rejection workflow for pending company representatives.
     * Displays list of pending representatives and allows staff to approve or reject them.
     */
    private void handleApproveRejectCompanyRep() {
        handleApproveRejectPattern("COMPANY REPRESENTATIVES", 
            "No pending company representatives.", 
            "  Enter company representative number to approve/reject (0 to go back to menu): ",
            controller::getPendingCompanyReps,
            idx -> extractStringIDFromString(controller.getPendingCompanyReps().get(idx)),
            (repID, approve) -> {
                controller.approveRejectCompanyRep(repID, approve);
                System.out.println("Company representative " + (approve ? "approved" : "rejected") + " successfully.");
            });
    }

    /**
     * Handles the approval/rejection workflow for pending internship listings.
     * Displays list of pending internships and allows staff to approve or reject them.
     */
    private void handleApproveRejectInternship() {
        handleApproveRejectPatternInt("INTERNSHIP LIST",
            "No pending internships.",
            "  Enter internship number to approve/reject (0 to go back to menu): ",
            controller::getPendingInternships,
            (internshipID, approve) -> {
                controller.updateInternshipApproval(internshipID, approve);
                System.out.println("Internship " + (approve ? "approved" : "rejected") + " successfully.");
            });
    }

    /**
     * Handles the approval/rejection workflow for student withdrawal requests.
     * Displays list of pending withdrawal requests and allows staff to approve or reject them.
     * Approved withdrawals change application status to WITHDRAWN.
     * Rejected withdrawals restore the application to its previous status.
     */
    private void handleApproveRejectWithdrawal() {
        handleApproveRejectPatternInt("WITHDRAWAL REQUESTS",
            "No withdrawal requests pending.",
            "  Enter application number to approve/reject (0 to go back to menu): ",
            controller::getWithdrawalRequests,
            (applicationID, approve) -> {
                controller.approveRejectWithdrawal(applicationID, approve);
                System.out.println(approve ? "Withdrawal approved successfully." : 
                    "Withdrawal rejected. Application restored to previous status.");
            });
    }


    
    /**
     * Displays paginated list of all internship opportunities.
     * Shows all internships with status information for staff overview.
     */
    private void handleListInternshipOpportunities() {
        displayPaginatedInternshipTable("INTERNSHIP OPPORTUNITIES",
            controller.internshipController.getAllInternships(controller.getFilterSettings()),
            "No internships available.", controller::getInternshipDetails,
            controller.getFilterSettings(), null, null, true);
    }
}
