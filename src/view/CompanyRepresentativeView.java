package view;

import controller.CompanyRepresentativeController;
import constant.MenuConstants;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * View class for company representative dashboard and operations.
 * Handles all UI interactions for company representative users including managing
 * internship listings, viewing/approving applications, and confirming placements.
 * Extends BaseView for common functionality and follows MVC architecture.
 * 
 * @author NTU SC2002 Group
 * @version 1.0
 * @since 2025-11-16
 */
public class CompanyRepresentativeView extends BaseView {
    /** The controller managing company representative-specific business logic */
    private CompanyRepresentativeController controller;

    /**
     * Constructs a new CompanyRepresentativeView with the specified controller.
     * 
     * @param controller The company representative controller handling business logic
     */
    public CompanyRepresentativeView(CompanyRepresentativeController controller) {
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
     * Displays the initial view showing internships created by this company representative.
     * Shows internships with options to view applications and toggle visibility.
     * Provides interactive actions for managing internship listings.
     */
    @Override
    protected void showInitialInternshipsView() {
        java.util.Map<String, java.util.function.Consumer<Integer>> actions = new java.util.HashMap<>();
        java.util.Map<String, String> actionPrompts = new java.util.HashMap<>();
        actions.put(MenuConstants.TABLE_CMD_TOGGLE, internshipID -> {
            try { 
                boolean isVisible = controller.toggleVisibility(internshipID);
                System.out.println("Visibility set to " + (isVisible ? "visible" : "invisible") + "!");
            }
            catch (Exception e) { System.out.println("Error: " + e.getMessage()); }
        });
        actionPrompts.put(MenuConstants.TABLE_CMD_TOGGLE, "toggle visibility");
        
        actions.put(MenuConstants.TABLE_CMD_EDIT, internshipID -> {
            try {
                handleEditInternshipById(internshipID);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        });
        actionPrompts.put(MenuConstants.TABLE_CMD_EDIT, "edit internship");
        
        actions.put(MenuConstants.TABLE_CMD_DELETE, internshipID -> {
            try {
                System.out.print("\n  Are you sure you want to delete this internship? All related applications will be withdrawn. (Y/N): ");
                String confirm = sc.nextLine().trim().toUpperCase();
                if (confirm.equals("Y")) {
                    controller.deleteInternship(internshipID);
                    System.out.println("Internship deleted successfully!\n");
                } else {
                    System.out.println("Delete cancelled.");
                }
            } catch (IllegalStateException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.out.println("Error: Internship not found.");
            }
        });
        actionPrompts.put(MenuConstants.TABLE_CMD_DELETE, "delete internship");
        
        showInternshipsLoop("MY INTERNSHIPS", controller::getInternships,
            "No internships found.", controller.getFilterSettings(),
            controller::getInternshipDetails, this::handleViewApplicationsForInternship, "view applications", true,
            actions, actionPrompts);
    }
    
    /**
     * Handles viewing and managing applications for a specific internship.
     * Displays paginated list of applications with options to approve or confirm placement.
     * Uses showApplicationsLoop for automatic refresh after actions.
     * 
     * @param internshipID The ID of the internship to view applications for
     */
    private void handleViewApplicationsForInternship(int internshipID) {
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
        });
        actionPrompts.put(MenuConstants.TABLE_CMD_APPROVE, "approve application");
        
        actions.put(MenuConstants.TABLE_CMD_CONFIRM, applicationID -> {
            try {
                controller.confirmPlacement(internshipID, applicationID);
                System.out.println("Placement confirmed! Application marked as accepted.");
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        });
        actionPrompts.put(MenuConstants.TABLE_CMD_CONFIRM, "confirm placement");
        
        // Use reusable showApplicationsLoop - automatically refreshes after actions
        showApplicationsLoop("APPLICATIONS FOR INTERNSHIP #" + internshipID,
            () -> controller.getApplicationsForInternship(internshipID),
            "No applications found for this internship.",
            applicationID -> controller.getApplicationDetails(internshipID, applicationID),
            actions, actionPrompts);
    }

    /**
     * Initializes the menu options for the company representative dashboard.
     * Creates a map of menu choices to their corresponding actions including:
     * profile management, internship CRUD operations, application management.
     * 
     * @return Map of menu option numbers to MenuOption objects
     */
    @Override
    protected Map<Integer, MenuOption> initializeMenuOptions() {
        Map<Integer, MenuOption> options = new HashMap<>();
        options.put(1, new MenuOption("view my profile", this::handleViewProfile));
        options.put(2, new MenuOption("edit my profile", this::handleEditProfile));
        options.put(3, new MenuOption("list my internships", this::handleListMyInternships));
        options.put(4, new MenuOption("create a new internship", this::handleCreateInternship));
        options.put(5, new MenuOption("edit an internship", this::handleEditInternship));
        options.put(6, new MenuOption("delete an internship", this::handleDeleteInternship));
        options.put(7, new MenuOption("toggle internship visibility", this::handleToggleVisibility));
        options.put(8, new MenuOption("view applications for an internship", this::handleViewApplications));
        options.put(9, new MenuOption("confirm student placement", this::handleConfirmPlacement));
        options.put(0, new MenuOption("log out", controller::logout));
        return options;
    }

    /**
     * Gets the title for the company representative dashboard.
     * 
     * @return The dashboard title string
     */
    @Override
    protected String getDashboardTitle() {
        return "COMPANY REPRESENTATIVE DASHBOARD";
    }

    /**
     * Retrieves the formatted profile text for the current company representative.
     * 
     * @return Formatted string containing company representative profile information
     */
    @Override
    protected String getProfileText() {
        return controller.getProfile();
    }
    
    /**
     * Handles the profile editing workflow for company representatives.
     * Allows editing of name, email, company name, department, and position fields.
     * Prompts user to select fields to edit and collects new values.
     */
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

    /**
     * Displays the list of internships created by this company representative.
     * Delegates to showInitialInternshipsView for consistent display.
     */
    private void handleListMyInternships() {
        showInitialInternshipsView();
    }
    


    /**
     * Handles the creation of a new internship listing.
     * Prompts for all required fields including title, description, level, major,
     * dates, and number of slots. Validates input and creates internship through controller.
     */
    private void handleCreateInternship() {
        handleAction(() -> {
            displayCenteredHeader("CREATE NEW INTERNSHIP");
            String title = promptString("Title: ");
            String description = promptString("Description: ");
            
            // Level selection with validation loop
            String level = null;
            while (level == null) {
                System.out.println("\n  Select Level:\n  1. Basic\n  2. Intermediate\n  3. Advanced\n  " + MenuConstants.MENU_CHOICE_CANCEL + ". Cancel");
                System.out.print(BORDER);
                System.out.print("  Enter your choice: ");
                
                if (!sc.hasNextInt()) {
                    sc.nextLine(); // Clear invalid input
                    System.out.println("  Invalid input! Please enter a number (1-3 or 0 to cancel).");
                    continue;
                }
                
                int choice = sc.nextInt();
                sc.nextLine();
                level = switch (choice) { 
                    case 1 -> "Basic"; 
                    case 2 -> "Intermediate"; 
                    case 3 -> "Advanced"; 
                    case 0 -> { System.out.println("  Creation cancelled."); yield "CANCEL"; }
                    default -> { System.out.println("  Invalid choice! Please select 1-3 or 0 to cancel."); yield null; }
                };
            }
            if (level.equals("CANCEL")) return;
            
            String major = promptString("Preferred Major: ");
            LocalDate openDate = parseDate(promptString("Open Date (YYYY-MM-DD): "));
            LocalDate closeDate = parseDate(promptString("Close Date (YYYY-MM-DD): "));
            int slots = promptInt("Number of slots (max 10): ");
            String msg = controller.createInternship(title, description, level, major, openDate, closeDate, slots);
            if (msg != null) System.out.println(msg);
        });
        waitForEnter();
    }

    /**
     * Handles editing an existing internship listing.
     * Displays current details and allows selective field editing.
     * Validates changes and updates internship through controller.
     */
    private void handleEditInternship() {
        handleAction(() -> {
            int id = promptInt("  Enter internship ID: ");
            handleEditInternshipById(id);
        });
        waitForEnter();
    }
    
    /**
     * Handles editing an internship by ID (used from table actions).
     * @param id The internship ID to edit
     */
    private void handleEditInternshipById(int id) {
        try {
            // Validate ownership and editability (will throw exception if cannot edit)
            controller.validateEditPermission(id);
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
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Handles deletion of an internship listing.
     * Prompts for internship ID and deletes through controller.
     */
    private void handleDeleteInternship() {
        handleActionWithID("  Enter internship ID: ", controller::deleteInternship,
            "Internship deleted successfully!");
    }

    /**
     * Handles toggling visibility of an approved internship.
     * Shows list of approved internships and allows visibility toggle.
     */
    private void handleToggleVisibility() {
        List<String> items = controller.getApprovedInternships();
        handleListWithAction("MY INTERNSHIPS", items, "No approved internships found!",
            "  Enter internship ID: ", controller::toggleVisibility, "Visibility toggled successfully.");
    }

    /**
     * Handles viewing applications for a selected internship.
     * First displays list of internships for selection, then shows applications
     * using showApplicationsLoop for auto-refresh after actions.
     */
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
            
            // Delegate to handleViewApplicationsForInternship for consistent behavior
            handleViewApplicationsForInternship(internshipID);
        } else {
            // User selected 0 to go back
            waitForEnter();
        }
    }

    /**
     * Handles confirming a student placement for an internship.
     * Prompts for internship ID and application ID, then confirms through controller.
     * Marks application as successful so student can accept it.
     */
    private void handleConfirmPlacement() {
        handleActionWithTwoIDs("  Enter internship ID: ", "Enter application ID: ",
            controller::confirmPlacement, "Application marked as successful! Student can now accept it.");
    }
    
    /**
     * Gets the filter options provider from the controller.
     * @return The controller as FilterOptionsProvider
     */
    @Override
    protected utils.filter.FilterOptionsProvider getFilterOptionsProvider() {
        return controller;
    }
}
