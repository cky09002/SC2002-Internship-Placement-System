package view;

import controller.StudentController;
import constant.MenuConstants;
import java.util.HashMap;
import java.util.Map;

/**
 * View class for student dashboard and operations.
 * Handles all UI interactions for student users including viewing/applying to internships,
 * managing applications, and profile management.
 * Extends BaseView for common functionality and follows MVC architecture.
 * 
 * @author NTU SC2002 Group
 * @version 1.0
 * @since 2025-11-16
 */
public class StudentView extends BaseView {
    /** The controller managing student-specific business logic */
    private StudentController controller;

    /**
     * Constructs a new StudentView with the specified controller.
     * 
     * @param controller The student controller handling business logic
     */
    public StudentView(StudentController controller) {
        super(controller::isLoggedIn);
        this.controller = controller;
    }
    
    /**
     * Displays the initial view showing available internships for the student.
     * Shows internships filtered by student's major, level eligibility, and filter settings.
     * Provides option to apply to internships directly from the list.
     */
    @Override
    protected void showInitialInternshipsView() {
        showInternshipsLoop("AVAILABLE INTERNSHIPS", controller::getInternships,
            "No internships available.", controller.getFilterSettings(),
            controller::getInternshipDetails, this::handleApplyToInternship, "apply");
    }
    
    /**
     * Handles the application to a specific internship.
     * Creates an application through the controller and displays success/error messages.
     * 
     * @param internshipID The ID of the internship to apply to
     */
    private void handleApplyToInternship(int internshipID) {
        handleAction(() -> {
            String msg = controller.createApplication(internshipID);
            if (msg != null) {
                System.out.println(msg);
            } else {
                System.out.println("Application submitted successfully!");
            }
        });
    }

    /**
     * Initializes the menu options for the student dashboard.
     * Creates a map of menu choices to their corresponding actions including:
     * profile management, internship browsing/filtering, application management.
     * 
     * @return Map of menu option numbers to MenuOption objects
     */
    @Override
    protected Map<Integer, MenuOption> initializeMenuOptions() {
        Map<Integer, MenuOption> options = new HashMap<>();
        options.put(1, new MenuOption("view my profile", this::handleViewProfile));
        options.put(2, new MenuOption("edit my profile", this::handleEditProfile));
        options.put(3, new MenuOption("list available internships", this::handleListInternships));
        options.put(5, new MenuOption("filter internships", () -> handleFilterInternships(controller.getFilterSettings(), () -> controller.internshipController.getAllInternships())));
               options.put(5, new MenuOption("create a new application", () -> handleActionWithMessage(() -> controller.createApplication(promptInt("  Enter internship ID to apply: ")))));
               options.put(6, new MenuOption("view my applications", this::handleViewApplications));
               options.put(7, new MenuOption("request to withdraw an application", () -> handleActionWithID("  Enter application ID: ", id -> {
                   String reason = promptString("  Enter reason for withdrawal (or press Enter to skip): ");
                   controller.withdrawApplication(id, reason != null && reason.trim().isEmpty() ? null : reason);
                   System.out.println("Withdrawal request submitted successfully! It will be reviewed by Career Center Staff.");
               })));
               options.put(8, new MenuOption("accept a successful application", () -> handleActionWithID("  Enter application ID: ", id -> {
                   controller.acceptApplication(id);
                   System.out.println("Application accepted successfully!");
               })));
        options.put(0, new MenuOption("log out", controller::logout));
        return options;
    }

    /**
     * Gets the title for the student dashboard.
     * 
     * @return The dashboard title string
     */
    @Override
    protected String getDashboardTitle() {
        return "STUDENT DASHBOARD";
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
     * Retrieves the formatted profile text for the current student.
     * 
     * @return Formatted string containing student profile information
     */
    @Override
    protected String getProfileText() {
        return controller.getProfile();
    }
    
    /**
     * Handles the profile editing workflow for students.
     * Allows editing of name, email, year of study, and major fields.
     * Prompts user to select fields to edit and collects new values.
     */
    @Override
    protected void editProfileFields() {
        String[] menu = {"Name", "Email", "Year of Study", "Major"};
        String[] prompts = {"Name", "Email", "Year of Study (1-5)", "Major"};
        int[] indices = displayMenuAndGetSelection(menu, "  Enter field numbers to edit (comma-separated, e.g., 1,3): ");
        if (checkEditCancelled(indices)) return;
        
        String[] fields = collectFieldValues(indices, prompts, idx -> idx == MenuConstants.FIELD_INDEX_YEAR_OF_STUDY); // Year of Study is integer
        
        String name = fields[0];
        String email = fields[1];
        Integer yearOfStudy = fields[2] != null ? parseInt(fields[2], 0) : null;
        String major = fields[3];
        
        controller.editProfile(name, email, yearOfStudy, major);
        System.out.println("\nProfile updated successfully!");
    }

    /**
     * Handles the display of paginated internship list.
     * Shows available internships with option to view details and apply.
     * Uses loop to reload data after actions.
     */
    private void handleListInternships() {
        showInternshipsLoop("AVAILABLE INTERNSHIPS", controller::getInternships,
            "No internships available.", controller.getFilterSettings(),
            controller::getInternshipDetails, this::handleApplyToInternship, "Apply to internship");
    }
    


    /**
     * Displays all applications submitted by the student.
     * Uses showApplicationsLoop for automatic refresh after accept/withdraw actions.
     * Supports interactive actions for accepting offers and withdrawing applications.
     */
    private void handleViewApplications() {
        // Create action maps for C (confirm placement/accept) and W (withdraw)
        java.util.Map<String, java.util.function.Consumer<Integer>> actions = new java.util.HashMap<>();
        java.util.Map<String, String> actionPrompts = new java.util.HashMap<>();
        
        actions.put(MenuConstants.TABLE_CMD_CONFIRM, this::handleAcceptApplicationAction);
        actionPrompts.put(MenuConstants.TABLE_CMD_CONFIRM, "confirm placement");
        
        actions.put(MenuConstants.TABLE_CMD_WITHDRAW, this::handleWithdrawApplicationAction);
        actionPrompts.put(MenuConstants.TABLE_CMD_WITHDRAW, "withdraw application");
        
        // Use reusable showApplicationsLoop - automatically refreshes after actions
        showApplicationsLoop("MY APPLICATIONS",
            controller::viewApplications,
            "No applications found.",
            controller::getApplicationDetails,
            actions, actionPrompts);
    }
    
    /**
     * Handles the withdrawal action for a specific application.
     * Prompts for withdrawal reason and submits request to controller.
     * 
     * @param applicationID The ID of the application to withdraw
     */
    private void handleWithdrawApplicationAction(int applicationID) {
        String reason = promptString("  Enter reason for withdrawal (or press Enter to skip): ");
        if (reason != null && reason.trim().isEmpty()) {
            reason = null;
        }
        controller.withdrawApplication(applicationID, reason);
        System.out.println("Withdrawal request submitted successfully! It will be reviewed by Career Center Staff.");
    }
    
    /**
     * Handles the acceptance action for a successful application.
     * Confirms placement and notifies the student of success.
     * 
     * @param applicationID The ID of the application to accept
     */
    private void handleAcceptApplicationAction(int applicationID) {
        controller.acceptApplication(applicationID);
        System.out.println("Application accepted successfully!");
    }

}
