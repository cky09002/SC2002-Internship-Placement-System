package utils.formatter;

import constant.MenuConstants;
import java.util.List;

/**
 * Table formatter utility for consistent table layouts.
 * Provides formatting methods for tables with fixed column widths.
 * No display logic - only formatting operations.
 * 
 * @author NTU SC2002 Group
 * @version 1.0
 * @since 2025-11-16
 */
public class TableFormatter {
    
    /**
     * Default constructor for TableFormatter.
     */
    public TableFormatter() {
        // Default constructor
    }
    
    // Fixed column width constants - shared across all tables
    /** Width for row number column */
    public static final int WIDTH_NUMBER = 4;
    
    /** Width for ID column */
    public static final int WIDTH_ID = 6;
    
    /** Width for title column */
    public static final int WIDTH_TITLE = 25;
    
    /** Width for company column */
    public static final int WIDTH_COMPANY = 18;
    
    /** Width for level column */
    public static final int WIDTH_LEVEL = 12;
    
    /** Width for major column */
    public static final int WIDTH_MAJOR = 20;
    
    /** Width for slots column */
    public static final int WIDTH_SLOTS = 10;
    
    /** Width for status column (internships) - Staff Approval Status */
    public static final int WIDTH_STATUS = 22;
    
    /** Width for visible column */
    public static final int WIDTH_VISIBLE = 10;
    
    /** Width for unsettled applications column */
    public static final int WIDTH_PENDING = 22;
    
    // Fixed column width constants for application tables
    /** Width for status column (applications - wider than internship status) */
    public static final int WIDTH_APP_STATUS = 18;
    
    /** Width for student ID column */
    public static final int WIDTH_APP_STUDENT_ID = 12;
    
    /** Width for student name column */
    public static final int WIDTH_APP_STUDENT = 25;
    
    /** Width for date column */
    public static final int WIDTH_APP_DATE = 12;
    
    /**
     * Gets fixed column widths for internship table without status/visible columns.
     * 
     * @return Array of column widths
     */
    public static int[] getInternshipColumnWidths() {
        return new int[]{WIDTH_NUMBER, WIDTH_ID, WIDTH_TITLE, WIDTH_COMPANY, WIDTH_LEVEL, WIDTH_MAJOR, WIDTH_SLOTS};
    }
    
    /**
     * Gets fixed column widths for internship table with status/visible/pending columns.
     * 
     * @return Array of column widths
     */
    public static int[] getInternshipColumnWidthsWithStatus() {
        return new int[]{WIDTH_NUMBER, WIDTH_ID, WIDTH_TITLE, WIDTH_COMPANY, WIDTH_LEVEL, WIDTH_MAJOR, WIDTH_SLOTS, WIDTH_STATUS, WIDTH_VISIBLE, WIDTH_PENDING};
    }
    
    /**
     * Gets fixed column widths for application table.
     * Order: #, ID, Student ID, Student, Internship, Company, Status, Date.
     * 
     * @return Array of column widths
     */
    public static int[] getApplicationColumnWidths() {
        // Order matches headers: #, ID, Student ID, Student, Internship, Company, Status, Date
        return new int[]{WIDTH_NUMBER, WIDTH_ID, WIDTH_APP_STUDENT_ID, WIDTH_APP_STUDENT, 
                         WIDTH_TITLE, WIDTH_COMPANY, WIDTH_APP_STATUS, WIDTH_APP_DATE};
    }
    
    /**
     * Get fixed column widths based on headers (for custom tables).
     * Returns predefined widths if headers match known patterns, otherwise calculates.
     * @param headers the table headers
     * @return array of column widths
     */
    public static int[] getColumnWidths(String[] headers) {
        // Check if headers match internship table pattern
        if (headers != null && headers.length > 0) {
            String firstHeader = headers[0].trim();
            if (firstHeader.equals("#")) {
                if (headers.length == MenuConstants.HEADERS_LENGTH_BASIC) {
                    // Internship table without status: #, ID, Title, Company, Level, Major, Slots
                    return getInternshipColumnWidths();
                } else if (headers.length == MenuConstants.HEADERS_LENGTH_WITH_STATUS) {
                    // Internship table with status: #, ID, Title, Company, Level, Major, Slots, Status, Visible
                    return getInternshipColumnWidthsWithStatus();
                }
            } else if (firstHeader.equals("#") && headers.length == MenuConstants.HEADERS_LENGTH_APPLICATION) {
                // Application table: #, ID, Student ID, Student, Internship, Company, Status, Date
                if (headers[2] != null && headers[2].contains("Student ID")) {
                    return getApplicationColumnWidths();
                }
            }
        }
        
        // Fallback: calculate widths (for custom tables)
        return calculateColumnWidths(headers, null);
    }
    
    /**
     * Formats a table with headers and rows using fixed column widths.
     * 
     * @param headers Column headers
     * @param rows List of data rows (each row is an array of strings)
     * @return Formatted table string with borders
     */
    public static String formatTable(String[] headers, List<String[]> rows) {
        if (headers == null || headers.length == 0) return "";
        
        // Use fixed column widths based on headers, or calculate if custom
        int[] columnWidths = getColumnWidths(headers);
        // Adjust widths if rows exist (to ensure they fit, but prefer fixed widths)
        if (rows != null && !rows.isEmpty()) {
            adjustColumnWidthsForData(headers, rows, columnWidths);
        }
        
        StringBuilder sb = new StringBuilder();
        
        // Header row
        sb.append(formatRow(headers, columnWidths));
        sb.append("  ").append(drawBorder(columnWidths)).append("\n");
        
        // Data rows
        if (rows != null && !rows.isEmpty()) {
            for (String[] row : rows) {
                sb.append(formatRow(row, columnWidths));
            }
        }
        
        // Bottom border
        sb.append("  ").append(drawBorder(columnWidths)).append("\n");
        
        return sb.toString();
    }
    
    /**
     * Formats a single table row with proper column widths and alignment.
     * Pads each cell to its column width and adds spacing between columns.
     * 
     * @param row The row data as array of strings
     * @param columnWidths The column widths to use for each cell
     * @return Formatted row string with newline
     */
    private static String formatRow(String[] row, int[] columnWidths) {
        StringBuilder sb = new StringBuilder("  ");
        for (int i = 0; i < columnWidths.length; i++) {
            String cell = (i < row.length && row[i] != null) ? row[i] : "";
            cell = truncateCell(cell, columnWidths[i]);
            sb.append(String.format("%-" + columnWidths[i] + "s", cell));
            if (i < columnWidths.length - 1) sb.append("  ");
        }
        sb.append("\n");
        return sb.toString();
    }
    
    /**
     * Draws a horizontal border line using dashes.
     * Calculates total width based on column widths plus spacing.
     * 
     * @param columnWidths Array of column widths
     * @return Border string of repeated dashes
     */
    private static String drawBorder(int[] columnWidths) {
        int totalWidth = 0;
        for (int width : columnWidths) {
            totalWidth += width + 2;
        }
        totalWidth -= 2;
        return "-".repeat(totalWidth);
    }
    
    /**
     * Adjusts fixed column widths based on actual data.
     * Maintains minimum fixed widths but expands if data is longer.
     * Caps maximum width at 80 characters to prevent excessive expansion.
     * Modifies columnWidths array in place.
     * 
     * @param headers The column headers
     * @param rows The data rows
     * @param columnWidths The column widths array to adjust (modified in place)
     */
    private static void adjustColumnWidthsForData(String[] headers, List<String[]> rows, int[] columnWidths) {
        for (String[] row : rows) {
            for (int i = 0; i < columnWidths.length && i < row.length; i++) {
                if (row[i] != null) {
                    // Only increase width if data is longer than fixed width, but cap at reasonable max
                    int maxCellWidth = Math.min(row[i].length(), 80);
                    columnWidths[i] = Math.max(columnWidths[i], maxCellWidth);
                }
            }
        }
        // Ensure header fits
        for (int i = 0; i < headers.length && i < columnWidths.length; i++) {
            if (headers[i] != null) {
                columnWidths[i] = Math.max(columnWidths[i], headers[i].length());
            }
        }
    }
    
    /**
     * Calculates optimal column widths for custom tables.
     * Fallback method when predefined fixed widths are not available.
     * Ensures minimum widths for first two columns (# and ID).
     * 
     * @param headers The column headers
     * @param rows The data rows (can be null for header-only calculation)
     * @return Array of calculated column widths
     */
    private static int[] calculateColumnWidths(String[] headers, List<String[]> rows) {
        int[] widths = new int[headers.length];
        for (int i = 0; i < headers.length; i++) {
            widths[i] = headers[i] != null ? headers[i].length() : 0;
        }
        if (rows != null) {
            for (String[] row : rows) {
                for (int i = 0; i < widths.length && i < row.length; i++) {
                    if (row[i] != null) {
                        widths[i] = Math.max(widths[i], Math.min(row[i].length(), 80));
                    }
                }
            }
        }
        if (widths.length > 0) widths[0] = Math.max(widths[0], 4);
        if (widths.length > 1) widths[1] = Math.max(widths[1], 6);
        return widths;
    }
    
    /**
     * Truncates cell content if it exceeds maximum width.
     * Adds "..." ellipsis suffix to indicate truncation.
     * 
     * @param cell The cell content to truncate
     * @param maxWidth The maximum width allowed
     * @return Truncated cell content with ellipsis if needed, original content otherwise
     */
    private static String truncateCell(String cell, int maxWidth) {
        if (cell.length() <= maxWidth) return cell;
        if (maxWidth <= 3) return cell.substring(0, maxWidth);
        return cell.substring(0, maxWidth - 3) + "...";
    }
}

