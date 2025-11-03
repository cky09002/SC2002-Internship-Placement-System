package Assignment.src.view;

import Assignment.src.controller.CompanyRepresentativeController;
import Assignment.src.constant.MenuConstants;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompanyRepresentativeView extends BaseView {
    private CompanyRepresentativeController controller;

    public CompanyRepresentativeView(CompanyRepresentativeController controller) {
        super(controller::isLoggedIn);
        this.controller = controller;
    }
    
    @Override
    protected String getInternshipDetailsForFilter(int internshipID) {
        return controller.getInternshipDetails(internshipID);
    }
    
    @Override
    protected void showInitialInternshipsView() {
        java.util.Map<String, java.util.function.Consumer<Integer>> actions = new java.util.HashMap<>();
        java.util.Map<String, String> actionPrompts = new java.util.HashMap<>();
        actions.put(MenuConstants.TABLE_CMD_TOGGLE, internshipID -> {
            try { controller.toggleVisibility(internshipID); System.out.println("Visibility toggled successfully!"); }
            catch (Exception e) { System.out.println("Error: " + e.getMessage()); }
        });
        actionPrompts.put(MenuConstants.TABLE_CMD_TOGGLE, "toggle visibility");
        
        showInternshipsLoop("MY INTERNSHIPS", controller::getInternships,
            "No internships found.", controller.getFilterSettings(),
            controller::getInternshipDetails, this::handleViewApplicationsForInternship, "view applications", true,
            actions, actionPrompts);
    }
    
    private void handleViewApplicationsForInternship(int internshipID) {
        // View applications for this internship - nested interface (stays in loop until user presses 0)
        while (true) {
            java.util.List<?> applications = controller.getApplicationsForInternship(internshipID);
            if (applications.isEmpty()) {
                displayCenteredHeader("APPLICATIONS FOR INTERNSHIP #" + internshipID);
                System.out.println("  No applications found for this internship.");
                System.out.print(BORDER);
                System.out.println("  Press Enter to go back...");
                sc.nextLine();
                return; // Go back to internships table
            }
            
            // Create action maps for A (approve application) and C (confirm placement)
            java.util.Map<String, java.util.function.Consumer<Integer>> actions = new java.util.HashMap<>();
            java.util.Map<String, String> actionPrompts = new java.util.HashMap<>();
            
            actions.put(MenuConstants.TABLE_CMD_APPROVE, applicationID -> {
                try {
                    controller.confirmPlacement(internshipID, applicationID);
                    System.out.println("Application marked as successful! Student can now accept it.");
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }
                // Stay in the applications view loop - table will refresh after action
            });
            actionPrompts.put(MenuConstants.TABLE_CMD_APPROVE, "approve application");
            
            actions.put(MenuConstants.TABLE_CMD_CONFIRM, applicationID -> {
                try {
                    controller.confirmPlacement(internshipID, applicationID);
                    System.out.println("Placement confirmed! Application marked as accepted.");
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }
                // Stay in the applications view loop - table will refresh after action
            });
            actionPrompts.put(MenuConstants.TABLE_CMD_CONFIRM, "confirm placement");
            
            // Display paginated table of applications with detail view and confirm placement capability
            // Returns false when user presses 0 to go back to internships table
            boolean goBack = displayPaginatedApplicationTable("APPLICATIONS FOR INTERNSHIP #" + internshipID, 
                applications,
                "No applications found for this internship.", 
                applicationID -> controller.getApplicationDetails(internshipID, applicationID),
                actions, actionPrompts);
            
            if (goBack) {
                // User pressed 0 - go back to internships table
                return;
            }
            // Otherwise continue loop (refresh applications and show table again)
        }
    }

    @Override
    protected Map<Integer, MenuOption> initializeMenuOptions() {
        Map<Integer, MenuOption> options = new HashMap<>();
        options.put(1, new MenuOption("view my profile", this::handleViewProfile));
        options.put(2, new MenuOption("edit my profile", this::handleEditProfile));
        options.put(3, new MenuOption("list my internships", this::handleListMyInternships));
        options.put(4, new MenuOption("filter internships", this::handleFilterInternships));
        options.put(5, new MenuOption("create a new internship", this::handleCreateInternship));
        options.put(6, new MenuOption("edit an internship", this::handleEditInternship));
        options.put(7, new MenuOption("delete an internship", this::handleDeleteInternship));
        options.put(8, new MenuOption("toggle internship visibility", this::handleToggleVisibility));
        options.put(9, new MenuOption("view applications for an internship", this::handleViewApplications));
        options.put(10, new MenuOption("confirm student placement", this::handleConfirmPlacement));
        options.put(0, new MenuOption("log out", controller::logout));
        return options;
    }

    @Override
    protected String getDashboardTitle() {
        return "COMPANY REPRESENTATIVE DASHBOARD";
    }

    @Override
    protected String getProfileText() {
        return controller.getProfile();
    }
    
    @Override
    protected void editProfileFields() {
        String[] menu = {"Name", "Email", "Company Name", "Department", "Position"};
        String[] prompts = {"Name", "Email", "Company Name", "Department", "Position"};
        int[] indices = displayMenuAndGetSelection(menu, "  Enter field numbers to edit (comma-separated, e.g., 1,3,5): ");
        if (checkEditCancelled(indices)) return;
        
        String[] fields = collectFieldValues(indices, prompts, idx -> false); // No integer fields
        
        String name = fields[0];
        String email = fields[1];
        String companyName = fields[2];
        String department = fields[3];
        String position = fields[4];
        
        controller.editProfile(name, email, companyName, department, position);
        System.out.println("\nProfile updated successfully!");
    }

    private void handleListMyInternships() {
        showInitialInternshipsView();
    }
    
    private void handleFilterInternships() {
        handleFilterMenu(controller.getFilterSettings(), controller.getInternshipsForFilter(),
            () -> controller.getInternshipsForFilter());
    }

    private void handleCreateInternship() {
        handleAction(() -> {
            displayCenteredHeader("CREATE NEW INTERNSHIP");
            String title = promptString("Title: ");
            String description = promptString("Description: ");
            System.out.println("\n  Select Level:\n  1. Basic\n  2. Intermediate\n  3. Advanced\n  " + MenuConstants.MENU_CHOICE_CANCEL + ". Cancel");
            System.out.print(BORDER);
            System.out.print("  Enter your choice: ");
            int choice = sc.nextInt();
            sc.nextLine();
            String level = switch (choice) { 
                case 1 -> "Basic"; 
                case 2 -> "Intermediate"; 
                case 3 -> "Advanced"; 
                default -> null; 
            };
            if (level == null) { System.out.println("  Creation cancelled."); return; }
            String major = promptString("Preferred Major: ");
            LocalDate openDate = parseDate(promptString("Open Date (YYYY-MM-DD): "));
            LocalDate closeDate = parseDate(promptString("Close Date (YYYY-MM-DD): "));
            int slots = promptInt("Number of slots (max 10): ");
            String msg = controller.createInternship(title, description, level, major, openDate, closeDate, slots);
            if (msg != null) System.out.println(msg);
        });
        waitForEnter();
    }

    private void handleEditInternship() {
        handleAction(() -> {
            int id = promptInt("  Enter internship ID: ");
            System.out.println(controller.getInternshipDetails(id));
            String[] menu = {"Title", "Description", "Level", "Preferred Major", "Open Date", "Close Date", "Number of Slots"};
            String[] prompts = {"Title", "Description", null, "Preferred Major", "Open Date (YYYY-MM-DD)", "Close Date (YYYY-MM-DD)", "Number of Slots (max 10)"};
            int[] indices = displayMenuAndGetSelection(menu, "  Enter field numbers to edit (comma-separated, e.g., 1,3,5): ");
            if (checkEditCancelled(indices)) return;
            String[] fields = new String[7];
            for (int idx : indices) {
                if (idx == MenuConstants.FIELD_INDEX_LEVEL) {
                    System.out.println("\n  Select Level:\n  1. Basic\n  2. Intermediate\n  3. Advanced\n  " + MenuConstants.MENU_CHOICE_CANCEL + ". Cancel");
                    System.out.print(BORDER + "\n  Enter your choice: ");
                    int choice = sc.nextInt();
                    sc.nextLine();
                    fields[MenuConstants.FIELD_INDEX_LEVEL] = switch (choice) { 
                        case 1 -> "Basic"; 
                        case 2 -> "Intermediate"; 
                        case 3 -> "Advanced"; 
                        default -> null; 
                    };
                    if (fields[MenuConstants.FIELD_INDEX_LEVEL] == null) { System.out.println("  Edit cancelled."); return; }
                } else if (idx == MenuConstants.FIELD_INDEX_SLOTS) {
                    System.out.print("  Enter new " + prompts[idx] + ": ");
                    fields[MenuConstants.FIELD_INDEX_SLOTS] = String.valueOf(sc.nextInt());
                    sc.nextLine();
                } else if (prompts[idx] != null) {
                    System.out.print("  Enter new " + prompts[idx] + ": ");
                    fields[idx] = sc.nextLine().trim();
                }
            }
            controller.editInternship(id, fields[0], fields[1], fields[MenuConstants.FIELD_INDEX_LEVEL], fields[3],
                fields[4] != null ? parseDate(fields[4]) : null,
                fields[5] != null ? parseDate(fields[5]) : null,
                fields[MenuConstants.FIELD_INDEX_SLOTS] != null ? parseInt(fields[MenuConstants.FIELD_INDEX_SLOTS], 0) : null);
            System.out.println("\nInternship updated successfully!");
        });
        waitForEnter();
    }

    private void handleDeleteInternship() {
        handleActionWithID("  Enter internship ID: ", controller::deleteInternship,
            "Internship deleted successfully!");
    }

    private void handleToggleVisibility() {
        List<String> items = controller.getApprovedInternships();
        handleListWithAction("MY INTERNSHIPS", items, "No approved internships found!",
            "  Enter internship ID: ", controller::toggleVisibility, "Visibility toggled successfully.");
    }

    private void handleViewApplications() {
        // First show list of internships for selection
        List<String> internships = controller.listMyInternships();
        if (internships.isEmpty()) {
            System.out.println("No internships found.");
            waitForEnter();
            return;
        }
        
        displayCenteredHeader("MY INTERNSHIPS");
        int idx = displayNumberedListAndSelect(internships, "  Enter internship number to view applications (0 to go back to menu): ");
        
        if (idx >= 0) {
            int internshipID = extractIDFromString(internships.get(idx));
            if (internshipID == MenuConstants.INVALID_ID) {
                System.out.println("  Error: Could not find internship ID.");
                waitForEnter();
                return;
            }
            
            // Show applications for selected internship with detail view capability
            handleAction(() -> {
                List<String> applications = controller.viewApplications(internshipID);
                displayListWithDetails("APPLICATIONS FOR INTERNSHIP #" + internshipID, applications,
                    "No applications found.", 
                    appID -> controller.getApplicationDetails(internshipID, appID));
            });
            waitForEnter();
        } else {
            // User selected 0 to go back
            waitForEnter();
        }
    }

    private void handleConfirmPlacement() {
        handleActionWithTwoIDs("  Enter internship ID: ", "Enter application ID: ",
            controller::confirmPlacement, "Application marked as successful! Student can now accept it.");
    }
}
