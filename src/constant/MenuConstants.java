package Assignment.src.constant;

/**
 * Constants for menu choices and field indices.
 */
public final class MenuConstants {
    private MenuConstants() {}
    
    // Menu choice constants
    public static final int MENU_CHOICE_CANCEL = 0;
    public static final int MENU_CHOICE_CLEAR = 1;
    public static final int MENU_CHOICE_PREVIEW = 10;
    public static final int INVALID_CHOICE = -1;
    
    // Field index constants
    public static final int FIELD_INDEX_LEVEL = 2;
    public static final int FIELD_INDEX_SLOTS = 6;
    public static final int FIELD_INDEX_YEAR_OF_STUDY = 2;
    
    // Invalid ID constant
    public static final int INVALID_ID = -1;
    
    // Table header length constants
    public static final int HEADERS_LENGTH_BASIC = 7;
    public static final int HEADERS_LENGTH_WITH_STATUS = 9;
    public static final int HEADERS_LENGTH_APPLICATION = 8;
    
    // Table navigation constants
    public static final String TABLE_CMD_BACK = "0";
    public static final String TABLE_CMD_FILTER = "F";
    public static final String TABLE_CMD_NEXT = "N";
    public static final String TABLE_CMD_PREV = "P";
    public static final String TABLE_CMD_DETAIL = "D";
    // Table action constants
    public static final String TABLE_CMD_APPROVE = "A";  // For company rep: approve application / confirm placement
    public static final String TABLE_CMD_CONFIRM = "C";  // For student: confirm placement / accept application
    public static final String TABLE_CMD_REJECT = "R";  // For company rep: reject application, For student: reject placement
    public static final String TABLE_CMD_WITHDRAW = "W";
    public static final String TABLE_CMD_TOGGLE = "T";  // For company rep: toggle visibility
}
 