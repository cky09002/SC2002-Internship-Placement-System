package Assignment.src.utils;

import Assignment.src.model.Internship;
import Assignment.src.utils.FilterSettings;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;
import Assignment.src.view.TableView; // Import TableView for table display

/**
 * Default implementation for internship formatting.
 * Provides flexible formatting options for displaying internship details.
 * Can be extended for different formatting styles.
 */
public class InternshipFormatter implements Formatter {
    
    // Table headers aligned with CSV structure: InternshipID,Title,Level,Major,Company,NumSlots,FilledSlots,Status,Visible
    // Display headers match CSV columns (excluding Description, OpenDate, CloseDate, CreatorID from table view)
    public static final String[] HEADERS_BASIC = new String[]{"#", "ID", "Title", "Level", "Major", "Company", "Slots"};
    public static final String[] HEADERS_WITH_STATUS = new String[]{"#", "ID", "Title", "Level", "Major", "Company", "Slots", "Status", "Visible"};
    
    private boolean showVisible;
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
        if (showVisible) {
            sb.append(String.format("  Status: %s%n", i.getDisplayStatus()));
            sb.append(String.format("  Visible: %s%n", i.isVisiblePrivate() ? "Yes" : "No"));
        }
        sb.append("═".repeat(80));
        return sb.toString();
    }
    
    public static String formatAsRow(Internship i, boolean showStatus) {
        if (showStatus) {
            return String.format("ID: %d | Title: %s | Company: %s | Level: %s | Major: %s | Status: %s | Visible: %s | Slots: %d/%d",
                    i.getID(), i.getTitle(), i.getCompanyName(), i.getLevel(), i.getPreferredMajor(),
                    i.getStatus(), i.isVisiblePrivate(), i.getFilledSlots(), i.getNumSlots());
        }
        return String.format("ID: %d | Title: %s | Company: %s | Level: %s | Major: %s | Slots: %d/%d",
                i.getID(), i.getTitle(), i.getCompanyName(), i.getLevel(), i.getPreferredMajor(),
                i.getFilledSlots(), i.getNumSlots());
    }
    
    public static String formatDetails(Internship i, String header, boolean showVisible) {
        return new InternshipFormatter(header, showVisible).format(i);
    }
    
    /**
     * Format internship as table row (MVC compliant).
     */
    public static String[] formatTableRowFromObject(Object internshipObject, int displayNumber) {
        return formatTableRowFromObject(internshipObject, displayNumber, false);
    }
    
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
                       // Order matches CSV: InternshipID,Title,Level,Major,Company,NumSlots/FilledSlots,Status,Visible
                       return new String[]{
                           String.valueOf(displayNumber),
                           String.valueOf(id),
                           title,
                           level,
                           major != null ? major : "N/A",
                           company,
                           filledSlots + "/" + numSlots,
                           displayStatus,
                           isVisible ? "Yes" : "No"
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
    
    public static boolean displayPaginatedTable(List<?> internships, String header, String emptyMsg,
                                               Function<Integer, String> detailGetter,
                                               FilterSettings filterSettings,
                                               java.util.function.Consumer<Integer> onAction,
                                               String actionPrompt,
                                               Scanner sc, String border, int width) {
        return displayPaginatedTable(internships, header, emptyMsg, detailGetter, filterSettings,
            onAction, actionPrompt, false, sc, border, width);
    }
    
    public static boolean displayPaginatedTable(List<?> internships, String header, String emptyMsg,
                                               Function<Integer, String> detailGetter,
                                               FilterSettings filterSettings,
                                               java.util.function.Consumer<Integer> onAction,
                                               String actionPrompt,
                                               boolean showStatus,
                                               Scanner sc, String border, int width) {
        return displayPaginatedTable(internships, header, emptyMsg, detailGetter,
            filterSettings, onAction, actionPrompt, null, null, showStatus, sc, border, width);
    }
    
    public static boolean displayPaginatedTable(List<?> internships, String header, String emptyMsg,
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


