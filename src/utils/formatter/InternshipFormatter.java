package utils.formatter;

import model.Internship;
import utils.filter.FilterSettings;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;
import view.TableView; // Import TableView for table display

/**
 * Default implementation for internship formatting.
 * Provides flexible formatting options for displaying internship details.
 * Can be extended for different formatting styles.
 */
public class InternshipFormatter implements Formatter {
    
    // Table headers aligned with CSV structure: InternshipID,Title,Level,Major,Company,NumSlots,FilledSlots,Status,Visible
    // Display headers match CSV columns (excluding Description, OpenDate, CloseDate, CreatorID from table view)
    /** Basic table headers for internship display */
    public static final String[] HEADERS_BASIC = new String[]{"#", "ID", "Title", "Level", "Major", "Company", "Slots"};
    /** Table headers with status and visibility columns */
    public static final String[] HEADERS_WITH_STATUS = new String[]{"#", "ID", "Title", "Level", "Major", "Company", "Slots", "Staff Approval Status", "Visible", "Unsettled Applications"};
    
    /** Whether to show visibility status */
    private boolean showVisible;
    /** Optional header text for display */
    private String header;
    
    /**
     * Create a new InternshipFormatter
     * @param header Optional header text (null for no header)
     * @param showVisible Whether to show visibility status
     */
    public InternshipFormatter(String header, boolean showVisible) {
        this.header = header;
        this.showVisible = showVisible;
    }
    
    @Override
    public String format(Object object) {
        if (!(object instanceof Internship)) throw new IllegalArgumentException("Object must be an Internship");
        Internship i = (Internship) object;
        StringBuilder sb = new StringBuilder("\n").append("═".repeat(80)).append("\n");
        if (header != null && !header.isEmpty()) sb.append("  ").append(header).append("\n").append("═".repeat(80)).append("\n");
        sb.append(String.format("  ID: %d%n", i.getID()));
        sb.append(String.format("  Title: %s%n", i.getTitle()));
        sb.append(String.format("  Description: %s%n", i.getDescription()));
        sb.append(String.format("  Company: %s%n", i.getCompanyName()));
        sb.append(String.format("  Level: %s%n", i.getLevel()));
        sb.append(String.format("  Preferred Major: %s%n", i.getPreferredMajor()));
        sb.append(String.format("  Open Date: %s%n", i.getOpenDate()));
        sb.append(String.format("  Close Date: %s%n", i.getCloseDate()));
        sb.append(String.format("  Number of Slots: %d%n", i.getNumSlots()));
        sb.append(String.format("  Filled Slots: %d%n", i.getFilledSlots()));
        sb.append(String.format("  Available Slots: %d%n", i.getNumSlots() - i.getFilledSlots()));
        sb.append(String.format("  Status: %s%n", i.isFilled() ? "Filled" : "Available"));
        if (showVisible) {
            sb.append(String.format("  Staff Approval Status: %s%n", i.getStatus()));
            sb.append(String.format("  Visible: %s%n", i.getVisibleFlag() ? "Yes" : "No"));
        }
        sb.append("═".repeat(80));
        return sb.toString();
    }
    
    /**
     * Formats an internship as a single-line row string.
     * @param i the internship to format
     * @param showStatus whether to include status and visibility
     * @return formatted row string
     */
    public static String formatAsRow(Internship i, boolean showStatus) {
        if (showStatus) {
            long pendingCount = i.getPendingApplicationsCount();
            String pendingInfo = pendingCount > 0 ? " | Pending: " + pendingCount : "";
            return String.format("ID: %d | Title: %s | Company: %s | Level: %s | Major: %s | Status: %s | Visible: %s | Slots: %d/%d%s",
                    i.getID(), i.getTitle(), i.getCompanyName(), i.getLevel(), i.getPreferredMajor(),
                    i.getStatus(), i.getVisibleFlag(), i.getFilledSlots(), i.getNumSlots(), pendingInfo);
        }
        return String.format("ID: %d | Title: %s | Company: %s | Level: %s | Major: %s | Slots: %d/%d",
                i.getID(), i.getTitle(), i.getCompanyName(), i.getLevel(), i.getPreferredMajor(),
                i.getFilledSlots(), i.getNumSlots());
    }
    
    /**
     * Formats internship details with full information.
     * @param i the internship to format
     * @param header optional header text
     * @param showVisible whether to show visibility information
     * @return formatted details string
     */
    public static String formatDetails(Internship i, String header, boolean showVisible) {
        return new InternshipFormatter(header, showVisible).format(i);
    }
    
    /**
     * Format internship as table row (MVC compliant).
     * @param internshipObject the internship object to format
     * @param displayNumber the display number for the row
     * @return formatted array of strings for table display
     */
    public static String[] formatTableRowFromObject(Object internshipObject, int displayNumber) {
        return formatTableRowFromObject(internshipObject, displayNumber, false);
    }
    
    /**
     * Format internship as table row with optional status column (MVC compliant).
     * @param internshipObject the internship object to format
     * @param displayNumber the display number for the row
     * @param showStatus whether to include status and visibility columns
     * @return formatted array of strings for table display
     */
    public static String[] formatTableRowFromObject(Object internshipObject, int displayNumber, boolean showStatus) {
        try {
            int id = (Integer) internshipObject.getClass().getMethod("getID").invoke(internshipObject);
            String title = (String) internshipObject.getClass().getMethod("getTitle").invoke(internshipObject);
            String company = (String) internshipObject.getClass().getMethod("getCompanyName").invoke(internshipObject);
            String level = (String) internshipObject.getClass().getMethod("getLevel").invoke(internshipObject);
            String major = (String) internshipObject.getClass().getMethod("getPreferredMajor").invoke(internshipObject);
            int filledSlots = (Integer) internshipObject.getClass().getMethod("getFilledSlots").invoke(internshipObject);
            int numSlots = (Integer) internshipObject.getClass().getMethod("getNumSlots").invoke(internshipObject);
            
                   if (showStatus) {
                       Boolean isVisible = (Boolean) internshipObject.getClass().getMethod("isVisible").invoke(internshipObject);
                       String displayStatus = getDisplayStatus(internshipObject, filledSlots, numSlots);
                       Long pendingCount = (Long) internshipObject.getClass().getMethod("getPendingApplicationsCount").invoke(internshipObject);
                       // Order matches CSV: InternshipID,Title,Level,Major,Company,NumSlots/FilledSlots,Status,Visible,Pending
                       return new String[]{
                           String.valueOf(displayNumber),
                           String.valueOf(id),
                           title,
                           level,
                           major != null ? major : "N/A",
                           company,
                           filledSlots + "/" + numSlots,
                           displayStatus,
                           isVisible ? "Yes" : "No",
                           String.valueOf(pendingCount)
                       };
                   } else {
                       // Order matches CSV: InternshipID,Title,Level,Major,Company,NumSlots/FilledSlots
                       return new String[]{
                           String.valueOf(displayNumber),
                           String.valueOf(id),
                           title,
                           level,
                           major != null ? major : "N/A",
                           company,
                           filledSlots + "/" + numSlots
                       };
                   }
        } catch (Exception e) {
            return showStatus ? 
                new String[]{"Error", "Error", "Error formatting row", "", "", "", "", "", ""} :
                new String[]{"Error", "Error", "Error formatting row", "", "", "", ""};
        }
    }
    
    /**
     * Convert internal internship status to user-facing status display using reflection
     * @param internshipObj the internship object to query
     * @param filledSlots number of filled slots
     * @param numSlots total number of slots
     * @return display status string
     */
    private static String getDisplayStatus(Object internshipObj, int filledSlots, int numSlots) {
        try {
            return (String) internshipObj.getClass().getMethod("getDisplayStatus").invoke(internshipObj);
        } catch (Exception e) {
            // Fallback for other statuses
            try {
                Object status = internshipObj.getClass().getMethod("getStatus").invoke(internshipObj);
                return status.toString();
            } catch (Exception ex) {
                return "Unknown";
            }
        }
    }
    
    /**
     * Display paginated table of internships without status column.
     * @param internships list of internships to display
     * @param header table header text
     * @param emptyMsg message to show when no results
     * @param detailGetter function to get detail string from internship ID
     * @param filterSettings filter settings for the table
     * @param onAction action to perform on selected item
     * @param actionPrompt prompt text for action
     * @param sc scanner for user input
     * @param border border string for display
     * @param width display width
     * @return true if user wants to return to previous menu
     */
    public static int displayPaginatedTable(List<?> internships, String header, String emptyMsg,
                                               Function<Integer, String> detailGetter,
                                               FilterSettings filterSettings,
                                               java.util.function.Consumer<Integer> onAction,
                                               String actionPrompt,
                                               Scanner sc, String border, int width) {
        return displayPaginatedTable(internships, header, emptyMsg, detailGetter, filterSettings,
            onAction, actionPrompt, false, sc, border, width);
    }
    
    /**
     * Display paginated table of internships with optional status column.
     * @param internships list of internships to display
     * @param header table header text
     * @param emptyMsg message to show when no results
     * @param detailGetter function to get detail string from internship ID
     * @param filterSettings filter settings for the table
     * @param onAction action to perform on selected item
     * @param actionPrompt prompt text for action
     * @param showStatus whether to show status and visibility columns
     * @param sc scanner for user input
     * @param border border string for display
     * @param width display width
     * @return 0=exit, 1=action executed, 2=filter requested
     */
    public static int displayPaginatedTable(List<?> internships, String header, String emptyMsg,
                                               Function<Integer, String> detailGetter,
                                               FilterSettings filterSettings,
                                               java.util.function.Consumer<Integer> onAction,
                                               String actionPrompt,
                                               boolean showStatus,
                                               Scanner sc, String border, int width) {
        return displayPaginatedTable(internships, header, emptyMsg, detailGetter,
            filterSettings, onAction, actionPrompt, null, null, showStatus, sc, border, width);
    }
    
    /**
     * Display paginated table of internships with multiple actions and optional status column (full version).
     * @param internships list of internships to display
     * @param header table header text
     * @param emptyMsg message to show when no results
     * @param detailGetter function to get detail string from internship ID
     * @param filterSettings filter settings for the table
     * @param onAction primary action to perform on selected item
     * @param actionPrompt prompt text for primary action
     * @param actions map of action keys to action consumers
     * @param actionPrompts map of action keys to prompt strings
     * @param showStatus whether to show status and visibility columns
     * @param sc scanner for user input
     * @param border border string for display
     * @param width display width
     * @return 0=exit, 1=action executed, 2=filter requested
     */
    public static int displayPaginatedTable(List<?> internships, String header, String emptyMsg,
                                               Function<Integer, String> detailGetter,
                                               FilterSettings filterSettings,
                                               java.util.function.Consumer<Integer> onAction,
                                               String actionPrompt,
                                               java.util.Map<String, java.util.function.Consumer<Integer>> actions,
                                               java.util.Map<String, String> actionPrompts,
                                               boolean showStatus,
                                               Scanner sc, String border, int width) {
        String[] headers = showStatus ? HEADERS_WITH_STATUS : HEADERS_BASIC;
        return TableView.displayPaginatedTable(internships, header, emptyMsg, detailGetter,
            (obj, num) -> formatTableRowFromObject(obj, num, showStatus),
            obj -> { try { return (Integer) obj.getClass().getMethod("getID").invoke(obj); } catch (Exception e) { return -1; } },
            headers,
            filterSettings, onAction, actionPrompt, actions, actionPrompts, sc, border, width);
    }
}


