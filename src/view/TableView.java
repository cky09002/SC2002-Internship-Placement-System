package Assignment.src.view;

import Assignment.src.utils.*;
import Assignment.src.constant.MenuConstants;
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
     * Display paginated table with headers and navigation.
     * @param items List of items to display
     * @param header Table header
     * @param emptyMsg Empty message
     * @param detailGetter Function to get details by ID
     * @param rowFormatter Function to format each row: (item, displayNumber) -> String[]
     * @param getID Function to extract ID from item
     * @param tableHeaders Table column headers
     * @param filterSettings Filter settings (can be null)
     * @param onAction Action callback (can be null) - single action with 'A' prefix
     * @param actionPrompt Action prompt (can be null)1
     * 
     * @param actions Map of action codes to action callbacks (e.g., 'A' -> accept, 'W' -> withdraw)
     * @param actionPrompts Map of action codes to action prompts (e.g., 'A' -> "accept", 'W' -> "withdraw")
     * @param sc Scanner
     * @param border Border string
     * @param width Width
     * @return true if filter requested, false otherwise
     */
    @SuppressWarnings("unchecked")
    public static boolean displayPaginatedTable(List<?> items, String header, String emptyMsg,
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
            if (filterSettings != null && input.equals(MenuConstants.TABLE_CMD_FILTER)) return true;
            return false;
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
            
            if (input.equals(MenuConstants.TABLE_CMD_BACK)) return false;
            if (filterSettings != null && input.equals(MenuConstants.TABLE_CMD_FILTER)) return true;
            if (input.equals(MenuConstants.TABLE_CMD_NEXT) && currentPage < totalPages) currentPage++;
            else if (input.equals(MenuConstants.TABLE_CMD_PREV) && currentPage > 1) currentPage--;
            else if (tableHeaders != null && input.startsWith(MenuConstants.TABLE_CMD_DETAIL) && input.length() > 1) {
                int index = ViewFormatter.parseInt(input.substring(1), items.size()) - 1;
                if (index >= 0 && index < items.size()) {
                    System.out.println(detailGetter.apply(getID.apply(items.get(index))));
                    // Show back option menu
                    System.out.println("\n" + border);
                    System.out.println("  Options:");
                    System.out.println("  - Enter '" + MenuConstants.TABLE_CMD_BACK + "' to go back to table");
                    System.out.println(border);
                    System.out.print("  Enter your choice: ");
                    String backInput = sc.nextLine().trim().toUpperCase();
                    if (!backInput.equals(MenuConstants.TABLE_CMD_BACK)) {
                        // If not back, just continue (re-display table)
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
                            // Action completed - continue to refresh table (no extra prompt)
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
                            // Action completed - continue to refresh table (no extra prompt)
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
                    // Show back option menu
                    System.out.println("\n" + border);
                    System.out.println("  Options:");
                    System.out.println("  - Enter '" + MenuConstants.TABLE_CMD_BACK + "' to go back to table");
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
        }
    }
    
    /**
     * Display simple table preview (first N items) - uses small header for filters.
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

