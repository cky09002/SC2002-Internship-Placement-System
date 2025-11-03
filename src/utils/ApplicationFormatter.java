package Assignment.src.utils;

import Assignment.src.model.Application;
import Assignment.src.model.Internship;
import Assignment.src.model.Student;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;
import Assignment.src.view.TableView; // Import TableView for table display

/**
 * Default implementation for application formatting.
 * Provides flexible formatting options for displaying application details.
 * Can be extended for different formatting styles.
 */
public class ApplicationFormatter implements Formatter {
    
    // Table headers aligned with CSV structure: ApplicationID,StudentID,Status,DateApplied
    // Additional derived columns: Student (name from StudentID), Internship, Company (from internship)
    public static final String[] HEADERS = new String[]{"#", "ID", "Student ID", "Student", "Internship", "Company", "Status", "Date"};
    private boolean showInternshipDetails;
    private String header;
    
    /**
     * Create a new ApplicationFormatter
     * @param header Optional header text (null for no header)
     * @param showInternshipDetails Whether to show detailed internship information
     */
    public ApplicationFormatter(String header, boolean showInternshipDetails) {
        this.header = header;
        this.showInternshipDetails = showInternshipDetails;
    }
    
    @Override
    public String format(Object object) {
        if (!(object instanceof Application)) {
            throw new IllegalArgumentException("Object must be an Application");
        }
        
        Application application = (Application) object;
        StringBuilder details = new StringBuilder();
        details.append("\n").append("═".repeat(80)).append("\n");
        
        if (header != null && !header.isEmpty()) {
            details.append("  ").append(header).append("\n");
            details.append("═".repeat(80)).append("\n");
        }
        
        if (showInternshipDetails) {
            details.append("\n  Internship Details:\n");
            Internship internship = application.getInternship();
            details.append(String.format("    ID: %d%n", internship.getID()));
            details.append(String.format("    Title: %s%n", internship.getTitle()));
            details.append(String.format("    Description: %s%n", internship.getDescription()));
            details.append(String.format("    Level: %s%n", internship.getLevel()));
            details.append(String.format("    Preferred Major: %s%n", internship.getPreferredMajor()));
            details.append(String.format("    Company: %s%n", internship.getCompanyName()));
            details.append(String.format("    Open Date: %s%n", internship.getOpenDate()));
            details.append(String.format("    Close Date: %s%n", internship.getCloseDate()));
            details.append(String.format("    Slots: %d/%d%n", internship.getFilledSlots(), internship.getNumSlots()));
            details.append(String.format("    Status: %s%n", internship.getStatus()));
            details.append(String.format("    Visible: %s%n", internship.isVisiblePrivate() ? "Yes" : "No"));
        }
        
        details.append("\n  Applicant Details:\n");
        Student applicant = application.getApplicant();
        details.append(String.format("    Name: %s%n", applicant.getName()));
        details.append(String.format("    Student ID: %s%n", applicant.getUserID()));
        details.append(String.format("    Email: %s%n", applicant.getEmail()));
        details.append(String.format("    Year of Study: %d%n", applicant.getYearOfStudy()));
        details.append(String.format("    Major: %s%n", applicant.getMajor()));
        
        details.append("\n  Application Details:\n");
        details.append(String.format("    Application ID: %d%n", application.getId()));
        details.append(String.format("    Date Applied: %s%n", application.getDateApplied()));
        details.append(String.format("    Status: %s%n", application.getStatus()));
        
        // Show previous status if applicable
        if (application.getPreviousStatus() != null) {
            details.append(String.format("    Previous Status: %s%n", application.getPreviousStatus()));
        }
        
        // Show withdrawal reason if applicable
        if (application.getWithdrawalReason() != null && !application.getWithdrawalReason().isEmpty()) {
            details.append(String.format("    Withdrawal Reason: %s%n", application.getWithdrawalReason()));
        }
        
        details.append("═".repeat(80));
        return details.toString();
    }
    
    /**
     * Static utility method for formatting application as a single row for list display.
     * Polymorphism via overloading - same name as InternshipFormatter.formatAsRow().
     * @param application The application to format
     * @return Formatted row string
     */
    public static String formatAsRow(Application application) {
        String reasonText = "";
        if (application.getWithdrawalReason() != null && !application.getWithdrawalReason().isEmpty()) {
            reasonText = " | Reason: " + application.getWithdrawalReason();
        }
        return String.format("ID: %d | Student: %s (%s) | Internship: %s (ID: %d) | Company: %s | Status: %s | Date: %s%s",
                application.getId(),
                application.getApplicant().getName(),
                application.getApplicant().getUserID(),
                application.getInternship().getTitle(),
                application.getInternship().getID(),
                application.getInternship().getCompanyName(),
                application.getStatus(),
                application.getDateApplied().toLocalDate(),
                reasonText);
    }
    
    /**
     * Static utility method for formatting application details with header
     * @param application The application to format
     * @param header Optional header text (null for no header)
     * @param showInternshipDetails Whether to show detailed internship information
     * @return Formatted details string
     */
    public static String formatDetails(Application application, String header, boolean showInternshipDetails) {
        ApplicationFormatter formatter = new ApplicationFormatter(header, showInternshipDetails);
        return formatter.format(application);
    }
    
    /**
     * Display paginated table for applications (polymorphism via overloading - same name as InternshipFormatter).
     * @param applications List of applications
     * @param header Table header
     * @param emptyMsg Empty message
     * @param detailGetter Function to get details
     * @param sc Scanner
     * @param border Border string
     * @param width Width
     * @return true if go back requested
     */
    public static boolean displayPaginatedTable(List<?> applications, String header, String emptyMsg,
                                               Function<Integer, String> detailGetter,
                                               Scanner sc, String border, int width) {
        return TableView.displayPaginatedTable(applications, header, emptyMsg, detailGetter,
            (obj, num) -> formatTableRowFromObject(obj, num),
            obj -> { try { return (Integer) obj.getClass().getMethod("getId").invoke(obj); } catch (Exception e) { return -1; } },
            HEADERS, null, null, null, null, null, sc, border, width);
    }
    
    /**
     * Format application as a table row using reflection (MVC compliant).
     * Returns array of cell values for TableView.
     * @param applicationObject The application object to format
     * @param displayNumber The row number to display (1-based)
     * @return Array of cell values
     */
    public static String[] formatTableRowFromObject(Object applicationObject, int displayNumber) {
        try {
            int id = (Integer) applicationObject.getClass().getMethod("getId").invoke(applicationObject);
            Object applicant = applicationObject.getClass().getMethod("getApplicant").invoke(applicationObject);
            String studentName = (String) applicant.getClass().getMethod("getName").invoke(applicant);
            String studentID = (String) applicant.getClass().getMethod("getUserID").invoke(applicant);
            Object internship = applicationObject.getClass().getMethod("getInternship").invoke(applicationObject);
            String title = (String) internship.getClass().getMethod("getTitle").invoke(internship);
            String company = (String) internship.getClass().getMethod("getCompanyName").invoke(internship);
            String status = applicationObject.getClass().getMethod("getStatus").invoke(applicationObject).toString();
            Object dateApplied = applicationObject.getClass().getMethod("getDateApplied").invoke(applicationObject);
            String date = dateApplied.getClass().getMethod("toLocalDate").invoke(dateApplied).toString();
            
            // Order matches CSV: ApplicationID,StudentID,Status,DateApplied + derived columns
            return new String[]{
                String.valueOf(displayNumber),
                String.valueOf(id),
                studentID != null ? studentID : "N/A",
                studentName != null ? studentName : "N/A",
                title != null ? title : "N/A",
                company != null ? company : "N/A",
                status,
                date
            };
        } catch (Exception e) {
            return new String[]{"Error", "Error", "Error formatting row", "", "", "", "", ""};
        }
    }
    
    /**
     * Display paginated table for applications with actions support.
     * @param applications List of applications
     * @param header Table header
     * @param emptyMsg Empty message
     * @param detailGetter Function to get details
     * @param actions Map of action codes to action callbacks (e.g., 'A' -> accept, 'W' -> withdraw)
     * @param actionPrompts Map of action codes to action prompts (e.g., 'A' -> "accept", 'W' -> "withdraw")
     * @param sc Scanner
     * @param border Border string
     * @param width Width
     * @return true if go back requested
     */
    public static boolean displayPaginatedTable(List<?> applications, String header, String emptyMsg,
                                               Function<Integer, String> detailGetter,
                                               java.util.Map<String, java.util.function.Consumer<Integer>> actions,
                                               java.util.Map<String, String> actionPrompts,
                                               Scanner sc, String border, int width) {
        return TableView.displayPaginatedTable(applications, header, emptyMsg, detailGetter,
            (obj, num) -> formatTableRowFromObject(obj, num),
            obj -> { try { return (Integer) obj.getClass().getMethod("getId").invoke(obj); } catch (Exception e) { return -1; } },
            HEADERS, null, null, null, actions, actionPrompts, sc, border, width);
    }
    
}

