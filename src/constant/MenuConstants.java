package constant;

/**
 * Constants for menu choices, field indices, and table navigation.
 * Centralizes all UI command strings and index values.
 * 
 * @author NTU SC2002 Group
 * @version 1.0
 * @since 2025-11-16
 */
public final class MenuConstants {
    /** Private constructor to prevent instantiation */
    private MenuConstants() {}
    
    // Menu choice constants
    /** Menu choice to cancel/go back */
    public static final int MENU_CHOICE_CANCEL = 0;
    
    /** Menu choice to clear a selection */
    public static final int MENU_CHOICE_CLEAR = 1;
    
    /** Menu choice for preview option */
    public static final int MENU_CHOICE_PREVIEW = 10;
    
    /** Value indicating an invalid menu choice */
    public static final int INVALID_CHOICE = -1;
    
    // Field index constants
    /** Index for level field in edit operations */
    public static final int FIELD_INDEX_LEVEL = 2;
    
    /** Index for slots field in edit operations */
    public static final int FIELD_INDEX_SLOTS = 6;
    
    /** Index for year of study field in edit operations */
    public static final int FIELD_INDEX_YEAR_OF_STUDY = 2;
    
    // Invalid ID constant
    /** Value indicating an invalid ID */
    public static final int INVALID_ID = -1;
    
    // Table header length constants
    /** Header count for basic internship table */
    public static final int HEADERS_LENGTH_BASIC = 7;
    
    /** Header count for internship table with status/visibility/pending */
    public static final int HEADERS_LENGTH_WITH_STATUS = 10;
    
    /** Header count for application table */
    public static final int HEADERS_LENGTH_APPLICATION = 8;
    
    // Table navigation constants
    /** Command to go back from table view */
    public static final String TABLE_CMD_BACK = "0";
    
    /** Command to open filter menu */
    public static final String TABLE_CMD_FILTER = "F";
    
    /** Command for next page */
    public static final String TABLE_CMD_NEXT = "N";
    
    /** Command for previous page */
    public static final String TABLE_CMD_PREV = "P";
    
    /** Command to view details */
    public static final String TABLE_CMD_DETAIL = "D";
    
    // Table action constants
    /** Command to approve (company rep: approve application/confirm placement) */
    public static final String TABLE_CMD_APPROVE = "A";
    
    /** Command to confirm (student: confirm placement/accept application) */
    public static final String TABLE_CMD_CONFIRM = "C";
    
    /** Command to reject (company rep: reject application; student: reject placement) */
    public static final String TABLE_CMD_REJECT = "R";
    
    /** Command to withdraw application */
    public static final String TABLE_CMD_WITHDRAW = "W";
    
    /** Command to toggle visibility (company rep) */
    public static final String TABLE_CMD_TOGGLE = "T";
    
    /** Command to edit internship (company rep) */
    public static final String TABLE_CMD_EDIT = "E";
    
    /** Command to delete internship (company rep) */
    public static final String TABLE_CMD_DELETE = "X";
}
 