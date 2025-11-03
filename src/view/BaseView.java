package Assignment.src.view;

import Assignment.src.utils.*;
import Assignment.src.constant.MenuConstants;
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
    protected static final Scanner sc = new Scanner(System.in);
    protected static final String BORDER = "═".repeat(120);
    protected static final int WIDTH = 120;
    
    protected Supplier<Boolean> loginStatusChecker;
    
    public BaseView(Supplier<Boolean> loginStatusChecker) {
        this.loginStatusChecker = loginStatusChecker;
    }
    
    public void run() {
        Map<Integer, MenuOption> options = initializeMenuOptions();
        do {
            try {
                showInitialInternshipsView();
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
    
    protected abstract void showInitialInternshipsView();
    protected abstract Map<Integer, MenuOption> initializeMenuOptions();
    protected abstract String getDashboardTitle();
    
    protected void displayCenteredHeader(String title) {
        ViewFormatter.displayHeader(title, BORDER, WIDTH);
    }
    
    protected MenuOption showMenuDialog(Map<Integer, MenuOption> options) {
        displayCenteredHeader(getDashboardTitle());
        new TreeMap<>(options).forEach((k, v) -> System.out.printf("  %d. %s%n", k, v.getDesc()));
        System.out.print(BORDER + "\n  Enter your choice: ");
        int choice = sc.nextInt();
        sc.nextLine();
        return options.get(choice);
    }
    
    protected void waitForEnter() { ViewFormatter.waitForEnter(sc); }
    protected int extractIDFromString(String str) { return ViewFormatter.extractIDFromString(str); }
    protected String extractStringIDFromString(String str) { return ViewFormatter.extractStringIDFromString(str); }
    protected int displayNumberedListAndSelect(List<String> items, String prompt) {
        System.out.println();
        for (int i = 0; i < items.size(); i++) System.out.printf("  [%d] %s%n", i + 1, items.get(i));
        System.out.println(BORDER);
        System.out.print(prompt);
        int choice = sc.nextInt();
        sc.nextLine();
        return (choice > 0 && choice <= items.size()) ? choice - 1 : -1;
    }
    
    protected void displayList(String header, List<String> items, String emptyMsg) {
        displayCenteredHeader(header);
        if (items.isEmpty()) System.out.println("  " + emptyMsg);
        else items.forEach(System.out::println);
    }
    
    protected int promptInt(String prompt) { System.out.print(prompt); int v = sc.nextInt(); sc.nextLine(); return v; }
    protected String promptString(String prompt) { System.out.print(prompt); return sc.nextLine().trim(); }
    
    protected void handleAction(Runnable action, String successMsg) {
        try { action.run(); if (successMsg != null) System.out.println(successMsg); }
        catch (Exception e) { System.out.println("Error: " + e.getMessage()); }
    }
    
    protected void handleAction(Runnable action) { handleAction(action, null); }
    
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
    
    protected void handleFilterMenu(FilterSettings filterSettings, List<?> internships, Supplier<List<?>> getAllInternships) {
        FilterMenu.showFilterMenu(filterSettings, getAllInternships, this::getInternshipDetailsForFilter);
    }
    
    protected String getInternshipDetailsForFilter(int internshipID) {
        return "Internship ID: " + internshipID + " (Details not available in this view)";
    }
    
    protected void showInternshipsLoop(String header, Supplier<List<?>> getInternships, String emptyMsg,
                                      FilterSettings filterSettings, Function<Integer, String> detailGetter,
                                      java.util.function.Consumer<Integer> onAction, String actionPrompt, boolean showStatus) {
        showInternshipsLoop(header, getInternships, emptyMsg, filterSettings, detailGetter, onAction, actionPrompt, showStatus, null, null);
    }
    
    protected void showInternshipsLoop(String header, Supplier<List<?>> getInternships, String emptyMsg,
                                      FilterSettings filterSettings, Function<Integer, String> detailGetter,
                                      java.util.function.Consumer<Integer> onAction, String actionPrompt, boolean showStatus,
                                      java.util.Map<String, java.util.function.Consumer<Integer>> actions,
                                      java.util.Map<String, String> actionPrompts) {
        while (true) {
            List<?> internships = getInternships.get();
            boolean shouldFilter = InternshipFormatter.displayPaginatedTable(internships, header, emptyMsg,
                detailGetter, filterSettings, onAction, actionPrompt, actions, actionPrompts, showStatus, sc, BORDER, WIDTH);
            if (!shouldFilter) break;
            FilterMenu.showFilterMenu(filterSettings, getInternships, detailGetter);
        }
    }
    
    protected void showInternshipsLoop(String header, Supplier<List<?>> getInternships, String emptyMsg,
                                      FilterSettings filterSettings, Function<Integer, String> detailGetter,
                                      java.util.function.Consumer<Integer> onAction, String actionPrompt) {
        showInternshipsLoop(header, getInternships, emptyMsg, filterSettings, detailGetter, onAction, actionPrompt, false);
    }
    
    protected LocalDate parseDate(String dateStr, LocalDate fallback) {
        try { return LocalDate.parse(dateStr.trim()); }
        catch (Exception e) { return fallback; }
    }
    protected LocalDate parseDate(String dateStr) { return parseDate(dateStr, LocalDate.now()); }
    protected int parseInt(String intStr, int fallback) {
        int result = ViewFormatter.parseInt(intStr, Integer.MAX_VALUE);
        return result <= Integer.MAX_VALUE ? result : fallback;
    }
    protected int[] parseCommaSeparatedIndices(String input, int maxIndex) {
        if (input == null || input.trim().isEmpty()) return new int[0];
        return java.util.Arrays.stream(input.split(","))
            .map(String::trim).filter(s -> !s.isEmpty())
            .mapToInt(s -> parseInt(s, -1) - 1)
            .filter(idx -> idx >= 0 && idx < maxIndex).toArray();
    }
    
    protected void handleActionWithID(String prompt, java.util.function.Consumer<Integer> action, String successMsg) {
        handleAction(() -> { action.accept(promptInt(prompt)); if (successMsg != null) System.out.println(successMsg); });
        waitForEnter();
    }
    
    protected void handleActionWithID(String prompt, java.util.function.Consumer<Integer> action) {
        handleActionWithID(prompt, action, null);
    }
    
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
    
    protected void handleActionWithMessage(java.util.function.Supplier<String> action) {
        handleAction(() -> { String msg = action.get(); if (msg != null) System.out.println(msg); });
        waitForEnter();
    }
    
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
    
    protected boolean handleApproveRejectPatternInt(String header, String emptyMsg, String prompt,
                                                     java.util.function.Supplier<List<String>> itemsSupplier,
                                                     java.util.function.BiConsumer<Integer, Boolean> approvalAction) {
        return handleApproveRejectPattern(header, emptyMsg, prompt, itemsSupplier,
            idx -> { int id = extractIDFromString(itemsSupplier.get().get(idx)); return id == MenuConstants.INVALID_ID ? null : String.valueOf(id); },
            (idStr, approve) -> approvalAction.accept(Integer.parseInt(idStr), approve));
    }
    
    protected void handleListWithAction(String header, List<String> items, String emptyMsg,
                                       String prompt, java.util.function.Consumer<Integer> action, String successMsg) {
        displayList(header, items, emptyMsg);
        if (!items.isEmpty()) handleAction(() -> { action.accept(promptInt(prompt)); System.out.println(successMsg); });
        waitForEnter();
    }
    
    protected         int[] displayMenuAndGetSelection(String[] menu, String prompt) {
        displayCenteredHeader("SELECT FIELDS TO EDIT");
        for (int i = 0; i < menu.length; i++) System.out.println("  " + (i+1) + ". " + menu[i]);
        System.out.print("  " + MenuConstants.MENU_CHOICE_CANCEL + ". Cancel (go back to menu)\n" + BORDER);
        String input = promptString(prompt);
        return input.equals(String.valueOf(MenuConstants.MENU_CHOICE_CANCEL)) ? new int[0] : parseCommaSeparatedIndices(input, menu.length);
    }
    
    protected boolean checkEditCancelled(int[] indices) {
        if (indices.length == 0) { System.out.println("  Edit cancelled."); return true; }
        return false;
    }
    
    protected String[] collectFieldValues(int[] indices, String[] prompts, java.util.function.Function<Integer, Boolean> isIntField) {
        String[] fields = new String[prompts.length];
        for (int idx : indices) {
            System.out.print("  Enter new " + prompts[idx] + ": ");
            fields[idx] = isIntField.apply(idx) ? String.valueOf(sc.nextInt()) : sc.nextLine().trim();
            if (isIntField.apply(idx)) sc.nextLine();
        }
        return fields;
    }
    
    protected void handleViewProfile() {
        try { displayCenteredHeader("MY PROFILE"); System.out.println(getProfileText()); }
        catch (Exception e) { System.out.println("Error: " + e.getMessage()); }
        waitForEnter();
    }
    
    protected void handleEditProfile() {
        try { displayCenteredHeader("MY PROFILE"); System.out.println(getProfileText() + "\n"); editProfileFields(); }
        catch (Exception e) { System.out.println("Error: " + e.getMessage()); }
        waitForEnter();
    }
    
    protected String getProfileText() {
        throw new UnsupportedOperationException("Subclass must implement getProfileText()");
    }
    
    protected void editProfileFields() {
        throw new UnsupportedOperationException("Subclass must implement editProfileFields()");
    }
    
    protected boolean displayPaginatedInternshipTable(String header, List<?> internships, String emptyMsg,
                                                    Function<Integer, String> detailGetter, FilterSettings filterSettings,
                                                    java.util.function.Consumer<Integer> onAction, String actionPrompt) {
        return InternshipFormatter.displayPaginatedTable(internships, header, emptyMsg, detailGetter,
            filterSettings, onAction, actionPrompt, false, sc, BORDER, WIDTH);
    }
    
    protected boolean displayPaginatedInternshipTable(String header, List<?> internships, String emptyMsg,
                                                    Function<Integer, String> detailGetter, FilterSettings filterSettings,
                                                    java.util.function.Consumer<Integer> onAction, String actionPrompt, boolean showStatus) {
        return InternshipFormatter.displayPaginatedTable(internships, header, emptyMsg, detailGetter,
            filterSettings, onAction, actionPrompt, showStatus, sc, BORDER, WIDTH);
    }
    
    protected boolean displayPaginatedApplicationTable(String header, List<?> applications, String emptyMsg,
                                                      Function<Integer, String> detailGetter) {
        return ApplicationFormatter.displayPaginatedTable(applications, header, emptyMsg, detailGetter, sc, BORDER, WIDTH);
    }
    
    protected boolean displayPaginatedApplicationTable(String header, List<?> applications, String emptyMsg,
                                                      Function<Integer, String> detailGetter,
                                                      java.util.Map<String, java.util.function.Consumer<Integer>> actions,
                                                      java.util.Map<String, String> actionPrompts) {
        return ApplicationFormatter.displayPaginatedTable(applications, header, emptyMsg, detailGetter,
            actions, actionPrompts, sc, BORDER, WIDTH);
    }
}

