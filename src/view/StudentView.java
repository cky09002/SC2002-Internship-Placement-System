package Assignment.src.view;

import Assignment.src.controller.StudentController;
import Assignment.src.constant.MenuConstants;
import java.util.HashMap;
import java.util.Map;

public class StudentView extends BaseView {
    private StudentController controller;

    public StudentView(StudentController controller) {
        super(controller::isLoggedIn);
        this.controller = controller;
    }
    
    @Override
    protected void showInitialInternshipsView() {
        showInternshipsLoop("AVAILABLE INTERNSHIPS", controller::getInternships,
            "No internships available.", controller.getFilterSettings(),
            controller::getInternshipDetails, this::handleApplyToInternship, "Apply to internship");
    }
    
    
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

    @Override
    protected Map<Integer, MenuOption> initializeMenuOptions() {
        Map<Integer, MenuOption> options = new HashMap<>();
        options.put(1, new MenuOption("view my profile", this::handleViewProfile));
        options.put(2, new MenuOption("edit my profile", this::handleEditProfile));
        options.put(3, new MenuOption("list available internships", this::handleListInternships));
        options.put(4, new MenuOption("filter internships", this::handleFilterInternships));
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

    @Override
    protected String getDashboardTitle() {
        return "STUDENT DASHBOARD";
    }
    
    @Override
    protected String getInternshipDetailsForFilter(int internshipID) {
        return controller.getInternshipDetails(internshipID);
    }

    @Override
    protected String getProfileText() {
        return controller.getProfile();
    }
    
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

    private void handleListInternships() {
        displayPaginatedInternshipTable("AVAILABLE INTERNSHIPS", controller.getInternships(),
            "No internships available.", controller::getInternshipDetails,
            controller.getFilterSettings(), this::handleApplyToInternship, "Apply to internship");
    }
    
    private void handleFilterInternships() {
        handleFilterMenu(controller.getFilterSettings(), controller.internshipController.getAllInternships(),
            () -> controller.internshipController.getAllInternships());
    }

    private void handleViewApplications() {
        java.util.List<?> applications = controller.viewApplications();
        if (applications.isEmpty()) {
            displayList("MY APPLICATIONS", java.util.Collections.emptyList(), "No applications found.");
            waitForEnter();
            return;
        }
        
        // Create action maps for C (confirm placement/accept) and W (withdraw)
        java.util.Map<String, java.util.function.Consumer<Integer>> actions = new java.util.HashMap<>();
        java.util.Map<String, String> actionPrompts = new java.util.HashMap<>();
        
        actions.put(MenuConstants.TABLE_CMD_CONFIRM, this::handleAcceptApplicationAction);
        actionPrompts.put(MenuConstants.TABLE_CMD_CONFIRM, "confirm placement");
        
        actions.put(MenuConstants.TABLE_CMD_WITHDRAW, this::handleWithdrawApplicationAction);
        actionPrompts.put(MenuConstants.TABLE_CMD_WITHDRAW, "withdraw application");
        
        displayPaginatedApplicationTable("MY APPLICATIONS", applications,
            "No applications found.", controller::getApplicationDetails, actions, actionPrompts);
    }
    
    private void handleWithdrawApplicationAction(int applicationID) {
        String reason = promptString("  Enter reason for withdrawal (or press Enter to skip): ");
        if (reason != null && reason.trim().isEmpty()) {
            reason = null;
        }
        controller.withdrawApplication(applicationID, reason);
        System.out.println("Withdrawal request submitted successfully! It will be reviewed by Career Center Staff.");
    }
    
    private void handleAcceptApplicationAction(int applicationID) {
        controller.acceptApplication(applicationID);
        System.out.println("Application accepted successfully!");
    }

}
