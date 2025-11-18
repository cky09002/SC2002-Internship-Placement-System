package view;

import utils.filter.*;
import utils.formatter.*;
import constant.MenuConstants;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Table view component - handles table display and pagination.
 * Uses TableFormatter to format table data.
 */
public class TableView {
    
    /**
     * Default constructor for TableView.
     */
    public TableView() {
        // Default constructor
    }
    
    /**
     * Displays a paginated table with headers, navigation, and interactive actions.
     * Supports detail viewing, filtering, and custom actions on table items.
     * Main method with all parameters - other overloads delegate to this.
     * 
     * @param items List of items to display in the table
     * @param header Table header title
     * @param emptyMsg Message to display when list is empty
     * @param detailGetter Function to retrieve detailed view by ID
     * @param rowFormatter Function to format each row: (item, displayNumber) -&gt; String[]
     * @param getID Function to extract ID from item object
     * @param tableHeaders Array of column header labels
     * @param filterSettings Current filter settings (null if filtering not supported)
     * @param onAction Single action callback (deprecated - use actions map instead)
     * @param actionPrompt Single action prompt (deprecated - use actionPrompts map instead)
     * @param actions Map of action codes to callbacks (e.g., "A" -&gt; approve, "W" -&gt; withdraw)
     * @param actionPrompts Map of action codes to display prompts (e.g., "A" -&gt; "approve")
     * @param sc Scanner for reading user input
     * @param border Border string for visual separation
     * @param width Display width for formatting
     * @return 0=exit, 1=action executed, 2=filter requested
     */
    public static int displayPaginatedTable(List<?> items, String header, String emptyMsg,
                                               Function<Integer, String> detailGetter,
                                               BiFunction<Object, Integer, String[]> rowFormatter,
                                               Function<Object, Integer> getID,
                                               String[] tableHeaders,
                                               FilterSettings filterSettings,
                                               java.util.function.Consumer<Integer> onAction,
                                               String actionPrompt,
                                               java.util.Map<String, java.util.function.Consumer<Integer>> actions,
                                               java.util.Map<String, String> actionPrompts,
                                               Scanner sc, String border, int width) {
        if (items.isEmpty()) {
            ViewFormatter.displayHeader(header, border, width);
            System.out.println("  " + emptyMsg);
            ViewFormatter.displayFilterFooter(filterSettings);
            System.out.println("\n  Press " + (filterSettings != null ? "'" + MenuConstants.TABLE_CMD_FILTER + "' to modify filters, or " : "") + "'" + MenuConstants.TABLE_CMD_BACK + "' to go back to menu.");
            System.out.print("  Enter your choice: ");
            String input = sc.nextLine().trim().toUpperCase();
            if (filterSettings != null && input.equals(MenuConstants.TABLE_CMD_FILTER)) return 2;
            return 0;
        }
        
        int itemsPerPage = 10;
        int totalPages = (int) Math.ceil((double) items.size() / itemsPerPage);
        int currentPage = 1;
        
        while (true) {
            System.out.println("\n".repeat(2));
            ViewFormatter.displayHeader(header, border, width);
            
            // Display table
            if (tableHeaders != null && tableHeaders.length > 0) {
                System.out.println();
                List<String[]> tableRows = new ArrayList<>();
                int startIndex = (currentPage - 1) * itemsPerPage;
                int endIndex = Math.min(startIndex + itemsPerPage, items.size());
                int displayNumber = startIndex + 1;
                
                for (int i = startIndex; i < endIndex; i++) {
                    tableRows.add(rowFormatter.apply(items.get(i), displayNumber++));
                }
                
                System.out.print(TableFormatter.formatTable(tableHeaders, tableRows));
            } else {
                System.out.println();
                int startIndex = (currentPage - 1) * itemsPerPage;
                int endIndex = Math.min(startIndex + itemsPerPage, items.size());
                int displayNumber = startIndex + 1;
                for (int i = startIndex; i < endIndex; i++) {
                    String[] row = rowFormatter.apply(items.get(i), displayNumber++);
                    System.out.println("  " + String.join(" | ", row));
                }
            }
            
            // Pagination info
            System.out.println();
            String itemType = filterSettings != null ? "internships" : "applications";
            System.out.printf("  Page %d of %d (%d total %s)%n", currentPage, totalPages, items.size(), itemType);
            ViewFormatter.displayFilterFooter(filterSettings);
            
            // Navigation options
            System.out.println("\n" + border);
            System.out.println("  Options:");
            System.out.println("  - Enter number (1-" + items.size() + ") to " + (tableHeaders != null ? "select" : "view details"));
            // Display multiple actions if provided
            if (actions != null && !actions.isEmpty()) {
                for (java.util.Map.Entry<String, String> entry : actionPrompts.entrySet()) {
                    System.out.println("  - Enter '" + entry.getKey() + "' + number to " + entry.getValue().toLowerCase());
                }
            } else if (onAction != null && actionPrompt != null) {
                // Fallback to single action for backward compatibility
                System.out.println("  - Enter '" + MenuConstants.TABLE_CMD_APPROVE + "' + number to " + actionPrompt.toLowerCase());
            }
            if (tableHeaders != null) {
                System.out.println("  - Enter '" + MenuConstants.TABLE_CMD_DETAIL + "' + number to view details");
            }
            if (totalPages > 1) {
                System.out.println("  - Enter '" + MenuConstants.TABLE_CMD_NEXT + "' for next page, '" + MenuConstants.TABLE_CMD_PREV + "' for previous page");
            }
            if (filterSettings != null) {
                System.out.println("  - Enter '" + MenuConstants.TABLE_CMD_FILTER + "' to modify filters");
            }
            System.out.println("  - Enter '" + MenuConstants.TABLE_CMD_BACK + "' to go back to menu");
            System.out.println(border);
            System.out.print("  Enter your choice: ");
            
            String input = sc.nextLine().trim().toUpperCase();
            
            if (input.equals(MenuConstants.TABLE_CMD_BACK)) return 0;
            if (filterSettings != null && input.equals(MenuConstants.TABLE_CMD_FILTER)) {
                return 2; // Return 2 to signal filter menu request (distinct from action execution)
            }
            if (input.equals(MenuConstants.TABLE_CMD_NEXT) && currentPage < totalPages) currentPage++;
            else if (input.equals(MenuConstants.TABLE_CMD_PREV) && currentPage > 1) currentPage--;
            else if (tableHeaders != null && input.startsWith(MenuConstants.TABLE_CMD_DETAIL) && input.length() > 1) {
                int index = ViewFormatter.parseInt(input.substring(1), items.size()) - 1;
                if (index >= 0 && index < items.size()) {
                    Integer selectedID = getID.apply(items.get(index));
                    System.out.println(detailGetter.apply(selectedID));
                    
                    // Show options menu after viewing details
                    System.out.println("\n" + border);
                    System.out.println("  Options:");
                    if (onAction != null && actionPrompt != null) {
                        System.out.println("  - Press Enter to " + actionPrompt);
                    }
                    if (actions != null && !actions.isEmpty()) {
                        for (java.util.Map.Entry<String, String> entry : actionPrompts.entrySet()) {
                            System.out.println("  - Enter '" + entry.getKey() + "' to " + entry.getValue());
                        }
                    }
                    System.out.println("  - Enter '" + MenuConstants.TABLE_CMD_BACK + "' to go back to table");
                    System.out.println(border);
                    System.out.print("  Enter your choice: ");
                    
                    String choice = sc.nextLine().trim().toUpperCase();
                    if (!choice.equals(MenuConstants.TABLE_CMD_BACK)) {
                        if (choice.isEmpty() && onAction != null) {
                            // Enter pressed - execute onAction
                            try {
                                onAction.accept(selectedID);
                            } catch (Exception e) {
                                System.out.println("  Error: " + e.getMessage());
                                ViewFormatter.waitForEnter(sc);
                            }
                        } else if (actions != null && actions.containsKey(choice)) {
                            // Execute the selected action
                            try {
                                actions.get(choice).accept(selectedID);
                            } catch (Exception e) {
                                System.out.println("  Error: " + e.getMessage());
                                ViewFormatter.waitForEnter(sc);
                            }
                        }
                    }
                } else {
                    System.out.println("  Invalid number!");
                    ViewFormatter.waitForEnter(sc);
                }
            } else if (actions != null && !actions.isEmpty() && input.length() > 1) {
                // Check for multiple actions
                String actionCode = input.substring(0, 1);
                java.util.function.Consumer<Integer> action = actions.get(actionCode);
                if (action != null) {
                    int index = ViewFormatter.parseInt(input.substring(1), items.size()) - 1;
                    if (index >= 0 && index < items.size()) {
                        try {
                            action.accept(getID.apply(items.get(index)));
                            return 1; // Return 1 to signal action executed (needs refresh, but not filter menu)
                        } catch (Exception e) {
                            System.out.println("  Error: " + e.getMessage());
                            ViewFormatter.waitForEnter(sc);
                        }
                    } else {
                        System.out.println("  Invalid number!");
                        ViewFormatter.waitForEnter(sc);
                    }
                } else {
                    // Try to parse as direct number for view details
                    int index = ViewFormatter.parseInt(input, items.size()) - 1;
                    if (index >= 0 && index < items.size()) {
                        System.out.println(detailGetter.apply(getID.apply(items.get(index))));
                        // Show back option menu
                        System.out.println("\n" + border);
                        System.out.println("  Options:");
                        System.out.println("  - Enter '" + MenuConstants.TABLE_CMD_BACK + "' to go back");
                        System.out.println(border);
                        System.out.print("  Enter your choice: ");
                        String backInput = sc.nextLine().trim().toUpperCase();
                        if (!backInput.equals(MenuConstants.TABLE_CMD_BACK)) {
                            // If not back, just continue (re-display table)
                        }
                    } else {
                        System.out.println("  Invalid choice!");
                        ViewFormatter.waitForEnter(sc);
                    }
                }
            } else if (onAction != null && input.startsWith(MenuConstants.TABLE_CMD_APPROVE) && input.length() > 1) {
                // Fallback to single action for backward compatibility (using A for approve)
                int index = ViewFormatter.parseInt(input.substring(1), items.size()) - 1;
                if (index >= 0 && index < items.size()) {
                    try {
                        onAction.accept(getID.apply(items.get(index)));
                        return 1; // Return 1 to signal action executed
                    } catch (Exception e) {
                        System.out.println("  Error: " + e.getMessage());
                        ViewFormatter.waitForEnter(sc);
                    }
                } else {
                    System.out.println("  Invalid number!");
                    ViewFormatter.waitForEnter(sc);
                }
            } else {
                int index = ViewFormatter.parseInt(input, items.size()) - 1;
                if (index >= 0 && index < items.size()) {
                    System.out.println(detailGetter.apply(getID.apply(items.get(index))));
                    ViewFormatter.waitForEnter(sc);
                } else {
                    System.out.println("  Invalid choice!");
                    ViewFormatter.waitForEnter(sc);
                }
            }
        }
    }
    
    /**
     * Display simple table preview (first N items) - uses small header for filters.
     * @param items list of items to display
     * @param header table header text
     * @param emptyMsg message to show when no results
     * @param rowFormatter function to format each row
     * @param tableHeaders table column headers
     * @param maxPreviewItems maximum number of items to show
     */
    public static void displayPreviewTable(List<?> items, String header, String emptyMsg,
                                          BiFunction<Object, Integer, String[]> rowFormatter,
                                          String[] tableHeaders, int maxPreviewItems) {
        String filterBorder = "═".repeat(120);
        ViewFormatter.displaySmallHeader(header, filterBorder, 120);
        int previewCount = Math.min(maxPreviewItems, items.size());
        System.out.printf("  Showing %d of %d internship(s)%n", previewCount, items.size());
        System.out.println("  " + "═".repeat(118));
        
        if (items.isEmpty()) {
            System.out.println("  " + emptyMsg.replace("\n", "\n  "));
        } else {
            List<String[]> tableRows = new ArrayList<>();
            for (int i = 0; i < previewCount; i++) {
                tableRows.add(rowFormatter.apply(items.get(i), i + 1));
            }
            System.out.print(TableFormatter.formatTable(tableHeaders, tableRows));
            if (items.size() > maxPreviewItems) {
                System.out.println("  ... and " + (items.size() - maxPreviewItems) + " more");
            }
        }
    }
}

