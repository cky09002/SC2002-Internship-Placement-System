package Assignment.src.utils;

import Assignment.src.constant.MenuConstants;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Table formatter utility - formats table data only.
 * No display logic - just formatting.
 * Uses fixed column widths for consistent layouts.
 */
public class TableFormatter {
    
    // Fixed column width constants - shared across all tables
    public static final int WIDTH_NUMBER = 4;      // # (used in both internship and application tables)
    public static final int WIDTH_ID = 6;          // ID (used in both tables)
    public static final int WIDTH_TITLE = 25;      // Title / Internship title (shared)
    public static final int WIDTH_COMPANY = 18;    // Company (shared)
    public static final int WIDTH_LEVEL = 12;      // Level
    public static final int WIDTH_MAJOR = 20;      // Major
    public static final int WIDTH_SLOTS = 10;      // Slots
    public static final int WIDTH_STATUS = 15;     // Status (for internships)
    public static final int WIDTH_VISIBLE = 10;    // Visible
    
    // Fixed column width constants for application tables (unique columns)
    // Order matches CSV: ApplicationID,StudentID,Status,DateApplied
    public static final int WIDTH_APP_STATUS = 18;  // Status (for applications - wider than internship status)
    public static final int WIDTH_APP_STUDENT_ID = 12; // Student ID (from CSV)
    public static final int WIDTH_APP_STUDENT = 25; // Student name (derived from StudentID)
    public static final int WIDTH_APP_DATE = 12;    // Date
    
    /**
     * Get fixed column widths for internship table without status/visible.
     */
    public static int[] getInternshipColumnWidths() {
        return new int[]{WIDTH_NUMBER, WIDTH_ID, WIDTH_TITLE, WIDTH_COMPANY, WIDTH_LEVEL, WIDTH_MAJOR, WIDTH_SLOTS};
    }
    
    /**
     * Get fixed column widths for internship table with status/visible.
     */
    public static int[] getInternshipColumnWidthsWithStatus() {
        return new int[]{WIDTH_NUMBER, WIDTH_ID, WIDTH_TITLE, WIDTH_COMPANY, WIDTH_LEVEL, WIDTH_MAJOR, WIDTH_SLOTS, WIDTH_STATUS, WIDTH_VISIBLE};
    }
    
    /**
     * Get fixed column widths for application table (multi-column format).
     * Uses shared constants: WIDTH_NUMBER, WIDTH_ID, WIDTH_TITLE, WIDTH_COMPANY
     */
    public static int[] getApplicationColumnWidths() {
        // Order matches headers: #, ID, Student ID, Student, Internship, Company, Status, Date
        return new int[]{WIDTH_NUMBER, WIDTH_ID, WIDTH_APP_STUDENT_ID, WIDTH_APP_STUDENT, 
                         WIDTH_TITLE, WIDTH_COMPANY, WIDTH_APP_STATUS, WIDTH_APP_DATE};
    }
    
    /**
     * Get fixed column widths based on headers (for custom tables).
     * Returns predefined widths if headers match known patterns, otherwise calculates.
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
     * Format a table with headers and rows using fixed column widths.
     * @param headers Column headers
     * @param rows List of data rows (each row is an array of strings)
     * @return Formatted table string
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
     * Format a single row.
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
     * Draw border with dashes.
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
     * Adjust fixed column widths based on actual data (but maintain minimum fixed widths).
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
     * Calculate optimal column widths (fallback for custom tables).
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
     * Truncate cell content if too long.
     */
    private static String truncateCell(String cell, int maxWidth) {
        if (cell.length() <= maxWidth) return cell;
        if (maxWidth <= 3) return cell.substring(0, maxWidth);
        return cell.substring(0, maxWidth - 3) + "...";
    }
}

