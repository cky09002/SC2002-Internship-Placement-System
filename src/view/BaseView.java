package view;

import utils.filter.*;
import utils.formatter.*;
import utils.formatter.ApplicationFormatter;
import constant.MenuConstants;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Base class for all views - concise implementation.
 */
public abstract class BaseView {
    /** Scanner for user input */
    protected static final Scanner sc = new Scanner(System.in);
    /** Border decoration for UI */
    protected static final String BORDER = "═".repeat(120);
    /** Display width for formatting */
    protected static final int WIDTH = 120;
    
    /** Lambda to check if user is still logged in */
    protected Supplier<Boolean> loginStatusChecker;
    
    /**
     * Constructs a BaseView with login status checker.
     * @param loginStatusChecker function to check login status
     */
    public BaseView(Supplier<Boolean> loginStatusChecker) {
        this.loginStatusChecker = loginStatusChecker;
    }
    
    /**
     * Main loop that displays the menu and handles user actions.
     */
    public void run() {
        Map<Integer, MenuOption> options = initializeMenuOptions();
        do {
            try {
                MenuOption selected = showMenuDialog(options);
                if (selected != null && selected.getOnSelCallback() != null) {
                    selected.getOnSelCallback().run();
                } else {
                    System.out.println("Invalid menu option!");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        } while (loginStatusChecker.get());
    }
    
    /**
     * Displays the initial view of internships for the user.
     */
    protected abstract void showInitialInternshipsView();
    
    /**
     * Initializes menu options specific to the user type.
     * @return map of menu option numbers to MenuOption objects
     */
    protected abstract Map<Integer, MenuOption> initializeMenuOptions();
    
    /**
     * Gets the dashboard title for the user type.
     * @return the dashboard title string
     */
    protected abstract String getDashboardTitle();
    
    /**
     * Gets internship details formatted for filter display.
     * @param internshipID the internship ID
     * @return formatted internship details
     */
    protected abstract String getInternshipDetailsForFilter(int internshipID);
    
    /**
     * Displays a centered header with border for section titles.
     * 
     * @param title The title text to display
     */
    protected void displayCenteredHeader(String title) {
        ViewFormatter.displayHeader(title, BORDER, WIDTH);
    }
    
    /**
     * Displays the main menu dialog and gets user selection.
     * 
     * @param options Map of menu options to display
     * @return Selected MenuOption or null if invalid
     */
    protected MenuOption showMenuDialog(Map<Integer, MenuOption> options) {
        displayCenteredHeader(getDashboardTitle());
        new TreeMap<>(options).forEach((k, v) -> System.out.printf("  %d. %s%n", k, v.getDesc()));
        System.out.print(BORDER + "\n  Enter your choice: ");
        int choice = sc.nextInt();
        sc.nextLine();
        return options.get(choice);
    }
    
    /** Waits for user to press Enter before continuing. */
    protected void waitForEnter() { ViewFormatter.waitForEnter(sc); }
    /**
     * Extracts an integer ID from a formatted string.
     * 
     * @param str The formatted string containing an ID
     * @return The extracted ID or -1 if not found
     */
    protected int extractIDFromString(String str) { return ViewFormatter.extractIDFromString(str); }
    /**
     * Extracts a string ID from a formatted string.
     * 
     * @param str The formatted string containing an ID
     * @return The extracted ID string or null if not found
     */
    protected String extractStringIDFromString(String str) { return ViewFormatter.extractStringIDFromString(str); }
    /**
     * Displays a numbered list and prompts for selection.
     * 
     * @param items The list of items to display
     * @param prompt The prompt message for selection
     * @return The zero-based index of selected item, or -1 if cancelled
     */
    protected int displayNumberedListAndSelect(List<String> items, String prompt) {
        System.out.println();
        for (int i = 0; i < items.size(); i++) System.out.printf("  [%d] %s%n", i + 1, items.get(i));
        System.out.println(BORDER);
        System.out.print(prompt);
        int choice = sc.nextInt();
        sc.nextLine();
        return (choice > 0 && choice <= items.size()) ? choice - 1 : -1;
    }
    
    /**
     * Displays a simple list with header.
     * 
     * @param header The header text
     * @param items The list of items to display
     * @param emptyMsg Message to display if list is empty
     */
    protected void displayList(String header, List<String> items, String emptyMsg) {
        displayCenteredHeader(header);
        if (items.isEmpty()) System.out.println("  " + emptyMsg);
        else items.forEach(System.out::println);
    }
    
    /**
     * Prompts user for an integer input.
     * 
     * @param prompt The prompt message
     * @return The integer value entered by user
     */
    protected int promptInt(String prompt) { System.out.print(prompt); int v = sc.nextInt(); sc.nextLine(); return v; }
    /**
     * Prompts user for a string input.
     * 
     * @param prompt The prompt message
     * @return The trimmed string value entered by user
     */
    protected String promptString(String prompt) { System.out.print(prompt); return sc.nextLine().trim(); }
    
    /**
     * Executes an action with error handling and optional success message.
     * 
     * @param action The action to execute
     * @param successMsg Success message to display, or null for none
     */
    protected void handleAction(Runnable action, String successMsg) {
        try { action.run(); if (successMsg != null) System.out.println(successMsg); }
        catch (Exception e) { System.out.println("Error: " + e.getMessage()); }
    }
    
    /**
     * Executes an action with error handling (no success message).
     * 
     * @param action The action to execute
     */
    protected void handleAction(Runnable action) { handleAction(action, null); }
    
    /**
     * Displays a list with option to view detailed information for selected item.
     * 
     * @param header The header text
     * @param items The list of items to display
     * @param emptyMsg Message to display if list is empty
     * @param detailGetter Function to retrieve details by ID
     */
    protected void displayListWithDetails(String header, List<String> items, String emptyMsg, Function<Integer, String> detailGetter) {
        if (items.isEmpty()) { displayList(header, items, emptyMsg); waitForEnter(); return; }
        displayCenteredHeader(header);
        for (int i = 0; i < items.size(); i++) System.out.printf("  [%d] %s%n", i + 1, items.get(i));
        System.out.println(BORDER);
        System.out.print("  Enter item number to view details (" + MenuConstants.MENU_CHOICE_CANCEL + " to go back to menu): ");
        int choice = sc.nextInt();
        sc.nextLine();
        int idx = (choice > 0 && choice <= items.size()) ? choice - 1 : -1;
        if (idx >= 0) {
            int id = extractIDFromString(items.get(idx));
            if (id == MenuConstants.INVALID_ID) { System.out.println("  Error: Could not find ID."); waitForEnter(); return; }
            try { System.out.println(detailGetter.apply(id)); waitForEnter(); }
            catch (Exception e) { System.out.println("  Error: " + e.getMessage()); waitForEnter(); }
        }
    }
    
    /**
     * Displays the filter menu for internship filtering.
     * 
     * @param filterSettings The current filter settings
     * @param internships The list of internships to filter
     * @param getAllInternships Supplier function to get all internships
     */
    protected void handleFilterMenu(FilterSettings filterSettings, List<?> internships, Supplier<List<?>> getAllInternships) {
        FilterMenu.showFilterMenu(filterSettings, getAllInternships, this::getInternshipDetailsForFilter);
    }
    
    /**
     * Generic data display loop that automatically refreshes after actions.
     * 
     * @param <T> Type marker for data
     * @param header The table header
     * @param dataSupplier Supplier function to get fresh data
     * @param displayFunction Function that takes (dataSupplier, header) and returns exit code
     * @param filterHandler Optional handler for filter menu (null if no filtering)
     */
    protected <T> void showDataLoop(String header, Supplier<List<?>> dataSupplier,
                                   java.util.function.BiFunction<Supplier<List<?>>, String, Boolean> displayFunction,
                                   Runnable filterHandler) {
        while (true) {
            boolean result = displayFunction.apply(dataSupplier, header);
            if (!result) break;
        }
    }
    
    /**
     * Displays internships in a loop with filtering capability (with status column).
     * 
     * @param header The table header
     * @param getInternships Supplier function to get internships
     * @param emptyMsg Message when no internships found
     * @param filterSettings Current filter settings
     * @param detailGetter Function to get internship details
     * @param onAction Action callback for single action
     * @param actionPrompt Prompt for the action
     * @param showStatus Whether to show status column
     */
    protected void showInternshipsLoop(String header, Supplier<List<?>> getInternships, String emptyMsg,
                                      FilterSettings filterSettings, Function<Integer, String> detailGetter,
                                      java.util.function.Consumer<Integer> onAction, String actionPrompt, boolean showStatus) {
        showInternshipsLoop(header, getInternships, emptyMsg, filterSettings, detailGetter, onAction, actionPrompt, showStatus, null, null);
    }
    
    /**
     * Displays internships in a loop with filtering and multiple actions capability.
     * Uses generic showDataLoop for automatic refresh.
     * 
     * @param header The table header
     * @param getInternships Supplier function to get internships
     * @param emptyMsg Message when no internships found
     * @param filterSettings Current filter settings
     * @param detailGetter Function to get internship details
     * @param onAction Action callback for single action (can be null)
     * @param actionPrompt Prompt for the action (can be null)
     * @param showStatus Whether to show status column
     * @param actions Map of action codes to callbacks (can be null)
     * @param actionPrompts Map of action codes to prompts (can be null)
     */
    protected void showInternshipsLoop(String header, Supplier<List<?>> getInternships, String emptyMsg,
                                      FilterSettings filterSettings, Function<Integer, String> detailGetter,
                                      java.util.function.Consumer<Integer> onAction, String actionPrompt, boolean showStatus,
                                      java.util.Map<String, java.util.function.Consumer<Integer>> actions,
                                      java.util.Map<String, String> actionPrompts) {
        showDataLoop(header, getInternships,
            (supplier, hdr) -> displayPaginatedInternshipTableWithRefresh(supplier, hdr, emptyMsg,
                detailGetter, filterSettings, onAction, actionPrompt, actions, actionPrompts, showStatus),
            filterSettings != null ? () -> handleFilterMenu(filterSettings, getInternships.get(), getInternships) : null);
    }
    
    /**
     * Displays paginated internship table with auto-refresh after actions.
     * 
     * @param dataSupplier Supplier to get fresh internship data
     * @param header The table header
     * @param emptyMsg Message when no internships found
     * @param detailGetter Function to get internship details
     * @param filterSettings Current filter settings
     * @param onAction Action callback for single action (can be null)
     * @param actionPrompt Prompt for the action (can be null)
     * @param actions Map of action codes to callbacks (can be null)
     * @param actionPrompts Map of action codes to prompts (can be null)
     * @param showStatus Whether to show status column
     * @return false if user exits, true otherwise
     */
    protected boolean displayPaginatedInternshipTableWithRefresh(Supplier<List<?>> dataSupplier, String header, String emptyMsg,
                                                                Function<Integer, String> detailGetter, FilterSettings filterSettings,
                                                                java.util.function.Consumer<Integer> onAction, String actionPrompt,
                                                                java.util.Map<String, java.util.function.Consumer<Integer>> actions,
                                                                java.util.Map<String, String> actionPrompts, boolean showStatus) {
        while (true) {
            List<?> data = dataSupplier.get();
            int result = InternshipFormatter.displayPaginatedTable(data, header, emptyMsg,
                detailGetter, filterSettings, onAction, actionPrompt, actions, actionPrompts, showStatus, sc, BORDER, WIDTH);
            
            if (result == 0) return false; // User exited
            
            if (result == 2 && filterSettings != null) {
                // Only show filter menu when user explicitly pressed 'F'
                handleFilterMenu(filterSettings, data, dataSupplier);
            }
            // If result == 1, action was executed, just continue loop to refresh
        }
    }
    
    /**
     * Displays internships in a loop with filtering capability (without status column).
     * 
     * @param header The table header
     * @param getInternships Supplier function to get internships
     * @param emptyMsg Message when no internships found
     * @param filterSettings Current filter settings
     * @param detailGetter Function to get internship details
     * @param onAction Action callback for single action
     * @param actionPrompt Prompt for the action
     */
    protected void showInternshipsLoop(String header, Supplier<List<?>> getInternships, String emptyMsg,
                                      FilterSettings filterSettings, Function<Integer, String> detailGetter,
                                      java.util.function.Consumer<Integer> onAction, String actionPrompt) {
        showInternshipsLoop(header, getInternships, emptyMsg, filterSettings, detailGetter, onAction, actionPrompt, false);
    }
    
    /**
     * Displays applications in a loop with automatic refresh after actions.
     * Reusable method for application views - no filtering support for applications.
     * 
     * @param header The table header
     * @param getApplications Supplier function to get applications
     * @param emptyMsg Message when no applications found
     * @param detailGetter Function to get application details
     */
    protected void showApplicationsLoop(String header, Supplier<List<?>> getApplications, String emptyMsg,
                                       Function<Integer, String> detailGetter) {
        showApplicationsLoop(header, getApplications, emptyMsg, detailGetter, null, null);
    }
    
    /**
     * Displays applications in a loop with multiple actions and automatic refresh.
     * 
     * @param header The table header
     * @param getApplications Supplier function to get applications
     * @param emptyMsg Message when no applications found
     * @param detailGetter Function to get application details
     * @param actions Map of action codes to callbacks (can be null)
     * @param actionPrompts Map of action codes to prompts (can be null)
     */
    protected void showApplicationsLoop(String header, Supplier<List<?>> getApplications, String emptyMsg,
                                       Function<Integer, String> detailGetter,
                                       java.util.Map<String, java.util.function.Consumer<Integer>> actions,
                                       java.util.Map<String, String> actionPrompts) {
        showDataLoop(header, getApplications,
            (supplier, hdr) -> displayPaginatedApplicationTableWithRefresh(supplier, hdr, emptyMsg, detailGetter, actions, actionPrompts),
            null);
    }
    
    /**
     * Parses a date string with fallback value.
     * 
     * @param dateStr The date string in YYYY-MM-DD format
     * @param fallback Fallback date if parsing fails
     * @return Parsed LocalDate or fallback
     */
    protected LocalDate parseDate(String dateStr, LocalDate fallback) {
        try { return LocalDate.parse(dateStr.trim()); }
        catch (Exception e) { return fallback; }
    }
    /**
     * Parses a date string with current date as fallback.
     * 
     * @param dateStr The date string in YYYY-MM-DD format
     * @return Parsed LocalDate or current date if parsing fails
     */
    protected LocalDate parseDate(String dateStr) { return parseDate(dateStr, LocalDate.now()); }
    /**
     * Parses an integer string with fallback value.
     * 
     * @param intStr The integer string to parse
     * @param fallback Fallback value if parsing fails
     * @return Parsed integer or fallback
     */
    protected int parseInt(String intStr, int fallback) {
        int result = ViewFormatter.parseInt(intStr, Integer.MAX_VALUE);
        return result <= Integer.MAX_VALUE ? result : fallback;
    }
    /**
     * Parses comma-separated indices from user input.
     * 
     * @param input The comma-separated input string
     * @param maxIndex Maximum valid index
     * @return Array of valid zero-based indices
     */
    protected int[] parseCommaSeparatedIndices(String input, int maxIndex) {
        if (input == null || input.trim().isEmpty()) return new int[0];
        return java.util.Arrays.stream(input.split(","))
            .map(String::trim).filter(s -> !s.isEmpty())
            .mapToInt(s -> parseInt(s, -1) - 1)
            .filter(idx -> idx >= 0 && idx < maxIndex).toArray();
    }
    
    /**
     * Handles an action requiring an ID with success message.
     * 
     * @param prompt The prompt for ID input
     * @param action The action to execute with the ID
     * @param successMsg Success message to display
     */
    protected void handleActionWithID(String prompt, java.util.function.Consumer<Integer> action, String successMsg) {
        handleAction(() -> { action.accept(promptInt(prompt)); if (successMsg != null) System.out.println(successMsg); });
        waitForEnter();
    }
    
    /**
     * Handles an action requiring an ID (no success message).
     * 
     * @param prompt The prompt for ID input
     * @param action The action to execute with the ID
     */
    protected void handleActionWithID(String prompt, java.util.function.Consumer<Integer> action) {
        handleActionWithID(prompt, action, null);
    }
    
    /**
     * Handles an action requiring two IDs.
     * 
     * @param prompt1 The prompt for first ID input
     * @param prompt2 The prompt for second ID input
     * @param action The action to execute with both IDs
     * @param successMsg Success message to display
     */
    protected void handleActionWithTwoIDs(String prompt1, String prompt2, 
                                         java.util.function.BiConsumer<Integer, Integer> action, String successMsg) {
        handleAction(() -> {
            System.out.print(prompt1);
            int id1 = sc.nextInt();
            sc.nextLine();
            System.out.print(prompt2);
            int id2 = sc.nextInt();
            sc.nextLine();
            action.accept(id1, id2);
            if (successMsg != null) System.out.println(successMsg);
        });
        waitForEnter();
    }
    
    /**
     * Handles an action that returns a message to display.
     * 
     * @param action The action that supplies a message
     */
    protected void handleActionWithMessage(java.util.function.Supplier<String> action) {
        handleAction(() -> { String msg = action.get(); if (msg != null) System.out.println(msg); });
        waitForEnter();
    }
    
    /**
     * Handles approve/reject workflow with string IDs.
     * 
     * @param header The header text
     * @param emptyMsg Message when no items found
     * @param prompt Selection prompt
     * @param itemsSupplier Supplier function for items
     * @param idExtractor Function to extract ID from item
     * @param approvalAction Action to execute with ID and approval decision
     * @return true if action was taken, false if cancelled
     */
    protected boolean handleApproveRejectPattern(String header, String emptyMsg, String prompt,
                                                java.util.function.Supplier<List<String>> itemsSupplier,
                                                java.util.function.Function<Integer, String> idExtractor,
                                                java.util.function.BiConsumer<String, Boolean> approvalAction) {
        List<String> items = itemsSupplier.get();
        if (items.isEmpty()) { System.out.println(emptyMsg); waitForEnter(); return false; }
        displayCenteredHeader(header);
        int idx = displayNumberedListAndSelect(items, prompt);
        if (idx < 0) { waitForEnter(); return false; }
        String id = idExtractor.apply(idx);
        if (id == null || id.isEmpty() || id.equals(String.valueOf(MenuConstants.INVALID_ID))) { System.out.println("  Error: Could not find ID."); waitForEnter(); return false; }
        handleAction(() -> {
            System.out.print("  Approve? (y/n): ");
            boolean approve = sc.nextLine().trim().toLowerCase().matches("y|yes");
            approvalAction.accept(id, approve);
        });
        waitForEnter();
        return true;
    }
    
    /**
     * Handles approve/reject workflow with integer IDs.
     * 
     * @param header The header text
     * @param emptyMsg Message when no items found
     * @param prompt Selection prompt
     * @param itemsSupplier Supplier function for items
     * @param approvalAction Action to execute with ID and approval decision
     * @return true if action was taken, false if cancelled
     */
    protected boolean handleApproveRejectPatternInt(String header, String emptyMsg, String prompt,
                                                     java.util.function.Supplier<List<String>> itemsSupplier,
                                                     java.util.function.BiConsumer<Integer, Boolean> approvalAction) {
        return handleApproveRejectPattern(header, emptyMsg, prompt, itemsSupplier,
            idx -> { int id = extractIDFromString(itemsSupplier.get().get(idx)); return id == MenuConstants.INVALID_ID ? null : String.valueOf(id); },
            (idStr, approve) -> approvalAction.accept(Integer.parseInt(idStr), approve));
    }
    
    /**
     * Displays a list with an action that can be performed on items.
     * 
     * @param header The header text
     * @param items The list of items
     * @param emptyMsg Message when list is empty
     * @param prompt The prompt for action
     * @param action The action to execute
     * @param successMsg Success message to display
     */
    protected void handleListWithAction(String header, List<String> items, String emptyMsg,
                                       String prompt, java.util.function.Consumer<Integer> action, String successMsg) {
        displayList(header, items, emptyMsg);
        if (!items.isEmpty()) handleAction(() -> { action.accept(promptInt(prompt)); System.out.println(successMsg); });
        waitForEnter();
    }
    
    /**
     * Displays a menu for field selection and gets user's comma-separated choices.
     * 
     * @param menu Array of menu items
     * @param prompt The prompt for selection
     * @return Array of zero-based indices of selected items
     */
    protected         int[] displayMenuAndGetSelection(String[] menu, String prompt) {
        displayCenteredHeader("SELECT FIELDS TO EDIT");
        for (int i = 0; i < menu.length; i++) System.out.println("  " + (i+1) + ". " + menu[i]);
        System.out.print("  " + MenuConstants.MENU_CHOICE_CANCEL + ". Cancel (go back to menu)\n" + BORDER);
        String input = promptString(prompt);
        return input.equals(String.valueOf(MenuConstants.MENU_CHOICE_CANCEL)) ? new int[0] : parseCommaSeparatedIndices(input, menu.length);
    }
    
    /**
     * Checks if edit operation was cancelled.
     * 
     * @param indices Array of selected indices
     * @return true if cancelled (empty array), false otherwise
     */
    protected boolean checkEditCancelled(int[] indices) {
        if (indices.length == 0) { System.out.println("  Edit cancelled."); return true; }
        return false;
    }
    
    /**
     * Collects new values for selected fields from user input.
     * 
     * @param indices Array of field indices to collect
     * @param prompts Array of prompts for each field
     * @param isIntField Function to determine if field is integer type
     * @return Array of collected field values (null for unselected fields)
     */
    protected String[] collectFieldValues(int[] indices, String[] prompts, java.util.function.Function<Integer, Boolean> isIntField) {
        String[] fields = new String[prompts.length];
        for (int idx : indices) {
            System.out.print("  Enter new " + prompts[idx] + ": ");
            fields[idx] = isIntField.apply(idx) ? String.valueOf(sc.nextInt()) : sc.nextLine().trim();
            if (isIntField.apply(idx)) sc.nextLine();
        }
        return fields;
    }
    
    /**
     * Handles viewing the user's profile.
     * Displays profile information and waits for user to continue.
     */
    protected void handleViewProfile() {
        try { displayCenteredHeader("MY PROFILE"); System.out.println(getProfileText()); }
        catch (Exception e) { System.out.println("Error: " + e.getMessage()); }
        waitForEnter();
    }
    
    /**
     * Displays the filter menu for internships.
     * Reusable method across all views that have filter functionality.
     * 
     * @param filterSettings The filter settings object to use
     * @param getAllInternships Supplier to get all internships list
     */
    protected void handleFilterInternships(FilterSettings filterSettings, 
                                         Supplier<List<?>> getAllInternships) {
        handleFilterMenu(filterSettings, getAllInternships.get(), getAllInternships);
    }
    
    /**
     * Handles editing the user's profile.
     * Displays current profile and prompts for field edits.
     */
    protected void handleEditProfile() {
        try { displayCenteredHeader("MY PROFILE"); System.out.println(getProfileText() + "\n"); editProfileFields(); }
        catch (Exception e) { System.out.println("Error: " + e.getMessage()); }
        waitForEnter();
    }
    
    /**
     * Gets the formatted profile text for the current user.
     * Must be implemented by subclasses to provide user-specific profile information.
     * @return formatted profile text
     */
    protected String getProfileText() {
        throw new UnsupportedOperationException("Subclass must implement getProfileText()");
    }
    
    /**
     * Handles editing of profile fields.
     * Must be implemented by subclasses to handle user-specific field editing.
     */
    protected void editProfileFields() {
        throw new UnsupportedOperationException("Subclass must implement editProfileFields()");
    }
    
    /**
     * Displays a paginated internship table (without status column).
     * 
     * @param header The table header
     * @param internships List of internships to display
     * @param emptyMsg Message when no internships found
     * @param detailGetter Function to get internship details
     * @param filterSettings Current filter settings
     * @param onAction Action callback for single action
     * @param actionPrompt Prompt for the action
     * @return true if filter requested, false otherwise
     */
    protected boolean displayPaginatedInternshipTable(String header, List<?> internships, String emptyMsg,
                                                    Function<Integer, String> detailGetter, FilterSettings filterSettings,
                                                    java.util.function.Consumer<Integer> onAction, String actionPrompt) {
        int result = InternshipFormatter.displayPaginatedTable(internships, header, emptyMsg, detailGetter,
            filterSettings, onAction, actionPrompt, false, sc, BORDER, WIDTH);
        return result == 2; // Return true only if filter requested
    }
    
    /**
     * Displays a paginated internship table with optional status column.
     * 
     * @param header The table header
     * @param internships List of internships to display
     * @param emptyMsg Message when no internships found
     * @param detailGetter Function to get internship details
     * @param filterSettings Current filter settings
     * @param onAction Action callback for single action
     * @param actionPrompt Prompt for the action
     * @param showStatus Whether to show status column
     * @return true if filter requested, false otherwise
     */
    protected boolean displayPaginatedInternshipTable(String header, List<?> internships, String emptyMsg,
                                                    Function<Integer, String> detailGetter, FilterSettings filterSettings,
                                                    java.util.function.Consumer<Integer> onAction, String actionPrompt, boolean showStatus) {
        int result = InternshipFormatter.displayPaginatedTable(internships, header, emptyMsg, detailGetter,
            filterSettings, onAction, actionPrompt, showStatus, sc, BORDER, WIDTH);
        return result == 2; // Return true only if filter requested
    }
    
    /**
     * Displays a paginated application table.
     * 
     * @param header The table header
     * @param applications List of applications to display
     * @param emptyMsg Message when no applications found
     * @param detailGetter Function to get application details
     * @return true if user wants to go back, false to continue
     */
    protected boolean displayPaginatedApplicationTable(String header, List<?> applications, String emptyMsg,
                                                      Function<Integer, String> detailGetter) {
        int result = ApplicationFormatter.displayPaginatedTable(applications, header, emptyMsg, detailGetter, sc, BORDER, WIDTH);
        return result == 0; // Return true if user exited
    }
    
    /**
     * Displays a paginated application table with multiple actions.
     * 
     * @param header The table header
     * @param applications List of applications to display
     * @param emptyMsg Message when no applications found
     * @param detailGetter Function to get application details
     * @param actions Map of action codes to callbacks
     * @param actionPrompts Map of action codes to prompts
     * @return true if user wants to go back, false to continue
     */
    protected boolean displayPaginatedApplicationTable(String header, List<?> applications, String emptyMsg,
                                                      Function<Integer, String> detailGetter,
                                                      java.util.Map<String, java.util.function.Consumer<Integer>> actions,
                                                      java.util.Map<String, String> actionPrompts) {
        int result = ApplicationFormatter.displayPaginatedTable(applications, header, emptyMsg, detailGetter,
            actions, actionPrompts, sc, BORDER, WIDTH);
        return result == 0; // Return true if user exited
    }
    
    /**
     * Displays paginated application table with auto-refresh after actions.
     * Uses internal loop to re-fetch data after each action execution.
     * 
     * @param dataSupplier Supplier to get fresh application data
     * @param header The table header
     * @param emptyMsg Message when no applications found
     * @param detailGetter Function to get application details
     * @param actions Map of action codes to callbacks
     * @param actionPrompts Map of action codes to prompts
     * @return false (always returns false since applications don't support filtering)
     */
    protected boolean displayPaginatedApplicationTableWithRefresh(Supplier<List<?>> dataSupplier, String header, String emptyMsg,
                                                                 Function<Integer, String> detailGetter,
                                                                 java.util.Map<String, java.util.function.Consumer<Integer>> actions,
                                                                 java.util.Map<String, String> actionPrompts) {
        while (true) {
            List<?> data = dataSupplier.get();
            int result = ApplicationFormatter.displayPaginatedTable(data, header, emptyMsg, detailGetter,
                actions, actionPrompts, sc, BORDER, WIDTH);
            if (result == 0) return false; // User exited
            // If result == 1, action was executed, continue loop to refresh
        }
    }
}

