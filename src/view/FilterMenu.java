package Assignment.src.view;

import Assignment.src.utils.*;
import Assignment.src.constant.*;
import Assignment.src.constant.MenuConstants;
import Assignment.src.constant.FilterConstants;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Reusable component for filter menu UI.
 * Promotes code reuse across different views.
 */
public class FilterMenu {
    private static final Scanner sc = new Scanner(System.in);
    
    /**
     * Show filter menu and allow user to set filter options
     * @param filterSettings Filter settings to modify
     * @param getAllInternships Supplier function to get all internships (for MVC compliance - goes through controller)
     * @param detailGetter Function to get internship details (for preview)
     * Uses raw Supplier to avoid importing model classes in view layer while maintaining type compatibility
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void showFilterMenu(FilterSettings filterSettings, Supplier getAllInternships, 
                                     java.util.function.Function<Integer, String> detailGetter) {
        while (true) {
            showFilterPreview(filterSettings, getAllInternships, detailGetter);
            
            String filterBorder = "═".repeat(120);
            ViewFormatter.displaySmallHeader("FILTER MENU", filterBorder, 120);
            System.out.println("  Current Filters: " + filterSettings.getFilterSummary());
            
            Map<Integer, MenuOption> options = createFilterMenuOptions(filterSettings, getAllInternships);
            int[] choices = showFilterMenuDialog(options);
            
            if (choices.length == 0) continue;
            
            for (int choice : choices) {
                if (choice == MenuConstants.MENU_CHOICE_CANCEL) return;
                if (choice == MenuConstants.MENU_CHOICE_PREVIEW) { showFullPreview(filterSettings, getAllInternships, detailGetter); continue; }
            }
            
            boolean anyApplied = false;
            boolean multipleSelected = choices.length > 1;
            
            for (int i = 0; i < choices.length; i++) {
                MenuOption selected = options.get(choices[i]);
                if (selected != null && selected.getOnSelCallback() != null) {
                    if (multipleSelected) setSkipEnterPrompt(true);
                    selected.getOnSelCallback().run();
                    anyApplied = true;
                    if (multipleSelected && i == choices.length - 1) setSkipEnterPrompt(false);
                }
            }
            
            if (anyApplied) {
                if (multipleSelected) {
                    System.out.println("\n  ✓ Applied " + choices.length + " filter option(s)");
                    System.out.println("  Updated Filters: " + filterSettings.getFilterSummary());
                    waitForEnter();
                }
                // For single selections, filter methods already show prompt, so skip duplicate
                continue;
            }
            
            if (!anyApplied && choices.length > 0) {
                showError("Invalid choice(s)! Valid options: " + options.keySet());
            }
        }
    }
    
    // Thread-local flag to skip Enter prompts during batch operations
    private static final ThreadLocal<Boolean> skipEnterPrompt = ThreadLocal.withInitial(() -> false);
    
    private static void setSkipEnterPrompt(boolean skip) {
        skipEnterPrompt.set(skip);
    }
    
    private static boolean shouldSkipEnterPrompt() {
        return skipEnterPrompt.get();
    }
    
    /**
     * Create filter menu options using MenuOption pattern
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static Map<Integer, MenuOption> createFilterMenuOptions(FilterSettings filterSettings, Supplier getAllInternships) {
        Map<Integer, MenuOption> options = new HashMap<>();
        options.put(1, new MenuOption("Filter by Status", () -> 
            filterStatus(filterSettings, InternshipFilter.getStatusOptions())));
        options.put(2, new MenuOption("Filter by Major", () -> {
            List<?> currentInternships = (List<?>) getAllInternships.get();
            filterFromList(filterSettings, "Major", "All (clear filter)", 
                          InternshipFilter.getAvailableMajors(currentInternships),
                          filterSettings::setMajorFilter, null);
        }));
        options.put(3, new MenuOption("Filter by Level", () -> 
            filterEnum(filterSettings, "Level", InternshipFilter.getLevelOptions(), null, 1, null)));
        options.put(4, new MenuOption("Filter by Opening Date", () -> filterByOpeningDate(filterSettings)));
        options.put(5, new MenuOption("Filter by Closing Date", () -> filterByClosingDate(filterSettings)));
        options.put(6, new MenuOption("Filter by Company", () -> {
            List<?> currentInternships = (List<?>) getAllInternships.get();
            filterFromList(filterSettings, "Company", "All (clear filter)", 
                          InternshipFilter.getAvailableCompanies(currentInternships),
                          filterSettings::setCompanyFilter, null);
        }));
        options.put(7, new MenuOption("Filter by Keyword", () -> filterByKeyword(filterSettings)));
        options.put(8, new MenuOption("Set Sort Order", () -> setSortOrder(filterSettings)));
        options.put(9, new MenuOption("Reset All Filters", () -> {
            filterSettings.reset();
            showSuccess("All filters reset");
            waitForEnter();
        }));
        options.put(MenuConstants.MENU_CHOICE_PREVIEW, new MenuOption("View Full Results Preview", () -> {})); // Handled in showFilterMenu
        options.put(MenuConstants.MENU_CHOICE_CANCEL, new MenuOption("Back to menu", () -> {})); // Handler will check for 0 and return
        return options;
    }
    
    private static int[] showFilterMenuDialog(Map<Integer, MenuOption> options) {
        options.entrySet().stream()
            .sorted((a, b) -> a.getKey() == MenuConstants.MENU_CHOICE_CANCEL ? 1 : (b.getKey() == MenuConstants.MENU_CHOICE_CANCEL ? -1 : Integer.compare(a.getKey(), b.getKey())))
            .forEach(entry -> System.out.println("  " + entry.getKey() + ". " + entry.getValue().getDesc()));
        System.out.println(BaseView.BORDER);
        System.out.print("  Enter your choice(s) (comma-separated for multiple): ");
        String input = sc.nextLine().trim();
        if (input.isEmpty()) { showError("Invalid input!"); return new int[0]; }
        int maxOption = options.keySet().stream().mapToInt(Integer::intValue).max().orElse(0);
        int[] choices = java.util.Arrays.stream(input.split(","))
            .map(String::trim).filter(s -> !s.isEmpty())
            .mapToInt(s -> { try { int v = Integer.parseInt(s); return (v >= 0 && v <= maxOption) ? v : -1; } catch (Exception e) { return -1; } })
            .filter(v -> v >= 0).toArray();
        if (choices.length == 0) { showError("Invalid input! Valid range: 0-" + maxOption); return new int[0]; }
        List<Integer> valid = java.util.Arrays.stream(choices).filter(options::containsKey).boxed().collect(java.util.stream.Collectors.toList());
        if (valid.isEmpty()) { showError("Invalid choice(s)!"); return new int[0]; }
        return valid.stream().mapToInt(i -> i).toArray();
    }
    
    private static void showError(String message) {
        System.out.println("  ✗ " + message);
        if (!shouldSkipEnterPrompt()) waitForEnter();
    }
    
    private static void showRangeError(int maxValue) { showError("Invalid! Range: 0-" + maxValue); }
    
    private static LocalDate parseDate(String input) {
        try { return LocalDate.parse(input.trim()); }
        catch (DateTimeParseException e) { return null; }
    }
    
    private static int displayMenuAndGetChoice(String title, String[] options, String prompt, int maxValue) {
        System.out.println("\n  " + title);
        for (int i = 0; i < options.length; i++) System.out.printf("  %d. %s%n", i + 1, options[i]);
        System.out.print("  " + prompt);
        try { return Integer.parseInt(sc.nextLine().trim()); } catch (Exception e) { return -1; }
    }
    
    private static int displayListMenuAndGetChoice(String title, String clearOption, List<String> items, String prompt) {
        System.out.println("\n  " + title + ":\n  " + MenuConstants.MENU_CHOICE_CANCEL + ". " + clearOption);
        for (int i = 0; i < items.size(); i++) System.out.printf("  %d. %s%n", i + 1, items.get(i));
        System.out.print("  " + prompt);
        try { return Integer.parseInt(sc.nextLine().trim()); } catch (Exception e) { return -1; }
    }
    
    private static void showSuccess(String message) {
        if (!shouldSkipEnterPrompt()) System.out.println("  ✓ " + message);
    }
    
    private static void filterByDate(FilterSettings settings, String name, java.util.function.Consumer<LocalDate> setter) {
        System.out.println("\n  Filter by " + name);
        System.out.print("  Enter " + (name.contains("Opening") ? "min" : "max") + " date (YYYY-MM-DD) or 'clear': ");
        String input = sc.nextLine().trim();
        if (input.equalsIgnoreCase(FilterConstants.CLEAR_COMMAND)) {
            setter.accept(null);
            showSuccess("Cleared");
        } else {
            LocalDate date = parseDate(input);
            if (date != null) {
                setter.accept(date);
                showSuccess("Set to: " + (name.contains("Opening") ? "≥" : "≤") + " " + date);
            } else showError("Invalid format! Use YYYY-MM-DD");
        }
        waitForEnter();
    }
    
    private static void filterStatus(FilterSettings settings, String[] options) {
        int choice = displayMenuAndGetChoice("Filter by Status", options, "Select (0=clear): ", options.length);
        if (choice == MenuConstants.INVALID_CHOICE) { showError("Invalid input!"); return; }
        if (choice == MenuConstants.MENU_CHOICE_CANCEL) {
            settings.setStatusFilter(null);
            settings.setStatusFilterStr(null);
            showSuccess("Cleared");
        } else if (choice > MenuConstants.MENU_CHOICE_CANCEL && choice <= options.length) {
            String selected = options[choice - 1];
            if (selected.equalsIgnoreCase("All")) {
                settings.setStatusFilter(null);
                settings.setStatusFilterStr(null);
                showSuccess("Cleared");
            } else if (selected.equalsIgnoreCase("Available") || selected.equalsIgnoreCase("Filled")) {
                settings.setStatusFilterStr(selected.toUpperCase());
                showSuccess("Set to: " + selected);
            } else {
                try {
                    InternshipStatus status = InternshipStatus.valueOf(selected.toUpperCase());
                    settings.setStatusFilter(status);
                    showSuccess("Set to: " + status);
                } catch (IllegalArgumentException e) {
                    showError("Invalid status!");
                }
            }
        } else showRangeError(options.length);
        waitForEnter();
    }
    
    private static void filterEnum(FilterSettings settings, String name, String[] options,
                                   Consumer<InternshipStatus> statusSetter, Integer offset,
                                   InternshipStatus[] statusValues) {
        int choice = displayMenuAndGetChoice("Filter by " + name, options, "Select (0=clear): ", options.length);
        if (choice == MenuConstants.INVALID_CHOICE) { showError("Invalid input!"); return; }
        if (choice == MenuConstants.MENU_CHOICE_CANCEL) {
            if (statusSetter != null) statusSetter.accept(null); else settings.setLevelFilter(null);
            showSuccess("Cleared");
        } else if (statusSetter != null && statusValues != null) {
            if (choice == MenuConstants.MENU_CHOICE_CLEAR) { statusSetter.accept(null); showSuccess("Cleared"); }
            else if (choice > MenuConstants.MENU_CHOICE_CLEAR && choice <= statusValues.length + 1) {
                statusSetter.accept(statusValues[choice - 2]);
                showSuccess("Set to: " + statusValues[choice - 2]);
            } else showRangeError(statusValues.length + 1);
        } else if (offset != null) {
            if (choice == MenuConstants.MENU_CHOICE_CLEAR && options.length > 0 && options[0].equalsIgnoreCase(FilterConstants.ALL_OPTION)) {
                settings.setLevelFilter(null);
                showSuccess("Cleared");
            } else if (choice > MenuConstants.MENU_CHOICE_CANCEL && choice <= options.length) {
                settings.setLevelFilter(options[choice - 1]);
                showSuccess("Set to: " + options[choice - 1]);
            } else showRangeError(options.length);
        } else showError("Invalid choice!");
        waitForEnter();
    }
    
    private static void filterFromList(FilterSettings settings, String name, String clearOption,
                                      List<String> items, Consumer<String> setter, Runnable clearAction) {
        int choice = displayListMenuAndGetChoice("Filter by " + name, clearOption, items, "Select (0=clear): ");
        if (choice == MenuConstants.INVALID_CHOICE) { showRangeError(items.size()); return; }
        if (choice == MenuConstants.MENU_CHOICE_CANCEL) {
            if (setter != null) setter.accept(null); else if (clearAction != null) clearAction.run();
            showSuccess("Cleared");
        } else if (choice > 0 && choice <= items.size()) {
            setter.accept(items.get(choice - 1));
            showSuccess("Set to: " + items.get(choice - 1));
        } else showRangeError(items.size());
        waitForEnter();
    }
    
    private static void filterByOpeningDate(FilterSettings settings) { filterByDate(settings, "Opening Date", settings::setOpeningDateFilter); }
    private static void filterByClosingDate(FilterSettings settings) { filterByDate(settings, "Closing Date", settings::setClosingDateFilter); }
    
    private static void filterByKeyword(FilterSettings settings) {
        System.out.println("\n  Filter by Keyword:");
        System.out.print("  Enter keyword or 'clear': ");
        String input = sc.nextLine().trim();
        if (input.equalsIgnoreCase(FilterConstants.CLEAR_COMMAND)) {
            settings.setKeywordFilter(null);
            showSuccess("Cleared");
        } else if (!input.isEmpty()) {
            settings.setKeywordFilter(input);
            showSuccess("Set to: " + input);
        } else showError("Invalid! Keyword required.");
        waitForEnter();
    }
    
    private static void setSortOrder(FilterSettings settings) {
        String[] orders = {"ALPHABETICAL", "ID", "CLOSING_DATE", "OPENING_DATE", "COMPANY", "LEVEL"};
        String[] labels = {"Alphabetical", "ID", "Closing Date", "Opening Date", "Company", "Level"};
        int choice = displayMenuAndGetChoice("Sort Order", labels, "Select: ", orders.length);
        if (choice < 1 || choice > orders.length) { showRangeError(orders.length); return; }
        settings.setSortOrder(orders[choice - 1]);
        showSuccess("Set to: " + labels[choice - 1]);
        waitForEnter();
    }
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void showFilterPreview(FilterSettings filterSettings, Supplier getAllInternships,
                                         java.util.function.Function<Integer, String> detailGetter) {
        List<?> filtered = (List<?>) InternshipFilter.applyFilters((java.util.List<?>) getAllInternships.get(), filterSettings);
        String[] headers = InternshipFormatter.HEADERS_BASIC;
        TableView.displayPreviewTable(filtered, "FILTERED RESULTS PREVIEW",
            "No internships match the current filters.\n  Try adjusting your filter criteria.",
            (obj, num) -> InternshipFormatter.formatTableRowFromObject(obj, num, false), headers, MenuConstants.MENU_CHOICE_CLEAR + 4);
    }
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void showFullPreview(FilterSettings filterSettings, Supplier getAllInternships,
                                       java.util.function.Function<Integer, String> detailGetter) {
        List<?> filtered = (List<?>) InternshipFilter.applyFilters((java.util.List<?>) getAllInternships.get(), filterSettings);
        String[] headers = InternshipFormatter.HEADERS_BASIC;
        TableView.displayPaginatedTable(filtered, "FULL RESULTS PREVIEW\n  Filters: " + filterSettings.getFilterSummary(),
            "No internships match the current filters.", detailGetter,
            (obj, num) -> InternshipFormatter.formatTableRowFromObject(obj, num, false),
            obj -> { try { return (Integer) obj.getClass().getMethod("getID").invoke(obj); } catch (Exception e) { return -1; } },
            headers, null, null, null, null, null, sc, BaseView.BORDER, BaseView.WIDTH);
    }
    
    private static void waitForEnter() {
        if (!shouldSkipEnterPrompt()) {
            ViewFormatter.waitForEnter(sc);
        }
    }
}