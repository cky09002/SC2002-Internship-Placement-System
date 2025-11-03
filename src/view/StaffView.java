package Assignment.src.view;

import Assignment.src.controller.StaffController;
import java.util.HashMap;
import java.util.Map;

public class StaffView extends BaseView {
    private StaffController controller;

    public StaffView(StaffController controller) {
        super(controller::isLoggedIn);
        this.controller = controller;
    }
    
    @Override
    protected String getInternshipDetailsForFilter(int internshipID) {
        return controller.getInternshipDetails(internshipID);
    }
    
    @Override
    protected void showInitialInternshipsView() {
        showInternshipsLoop("INTERNSHIP OPPORTUNITIES",
            () -> controller.internshipController.getAllInternships(controller.getFilterSettings()),
            "No internships available.", controller.getFilterSettings(),
            controller::getInternshipDetails, null, null, true);
    }

    @Override
    protected Map<Integer, MenuOption> initializeMenuOptions() {
        Map<Integer, MenuOption> options = new HashMap<>();
        options.put(1, new MenuOption("view my profile", this::handleViewProfile));
        options.put(2, new MenuOption("edit my profile", this::handleEditProfile));
        options.put(3, new MenuOption("approve/reject company representative", this::handleApproveRejectCompanyRep));
        options.put(4, new MenuOption("approve/reject internship listing", this::handleApproveRejectInternship));
        options.put(5, new MenuOption("approve/reject student withdrawal", this::handleApproveRejectWithdrawal));
        options.put(6, new MenuOption("filter internships", this::handleFilterInternships));
        options.put(7, new MenuOption("list internship opportunities", this::handleListInternshipOpportunities));
        options.put(0, new MenuOption("log out", controller::logout));
        return options;
    }

    @Override
    protected String getDashboardTitle() {
        return "CAREER CENTER STAFF DASHBOARD";
    }

    @Override
    protected String getProfileText() {
        return controller.getProfile();
    }
    
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

    private void handleFilterInternships() {
        handleFilterMenu(controller.getFilterSettings(), controller.internshipController.getAllInternships(),
            () -> controller.internshipController.getAllInternships());
    }
    
    private void handleListInternshipOpportunities() {
        displayPaginatedInternshipTable("INTERNSHIP OPPORTUNITIES",
            controller.internshipController.getAllInternships(controller.getFilterSettings()),
            "No internships available.", controller::getInternshipDetails,
            controller.getFilterSettings(), null, null, true);
    }
}
