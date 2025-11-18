package utils.formatter;

import utils.filter.FilterSettings;
import java.util.Scanner;

/**
 * Reusable component for all view display operations.
 * Consolidates common display methods used across views and formatters.
 * Provides header formatting, input parsing, and ID extraction utilities.
 * 
 * @author NTU SC2002 Group
 * @version 1.0
 * @since 2025-11-16
 */
public class ViewFormatter {
    
    /**
     * Private constructor to prevent instantiation.
     * This is a utility class with only static methods.
     */
    private ViewFormatter() {
        throw new AssertionError("Utility class should not be instantiated");
    }
    
    /**
     * Displays a large centered header for main titles.
     * Provides biggest visual presence with centered text.
     * 
     * @param title The title text to display
     * @param border The border string
     * @param width The width for centering
     */
    public static void displayLargeHeader(String title, String border, int width) {
        // Make title appear bigger - centered with no extra newlines before
        int padding = (width - title.length()) / 2;
        String centered = " ".repeat(Math.max(0, padding)) + title;
        System.out.println(border + "\n" + centered + "\n" + border);
    }
    
    /**
     * Displays a centered header for menus and dashboards.
     * Smaller than large header, indented with 2 spaces.
     * 
     * @param title The title text to display
     * @param border The border string
     * @param width The width for layout
     */
    public static void displayHeader(String title, String border, int width) {
        System.out.println(border + "\n  " + title + "\n" + border);
    }
    
    /**
     * Displays a small header for filters and sub-sections.
     * Smallest header size, indented with 2 spaces.
     * 
     * @param title The title text to display
     * @param border The border string
     * @param width The width for layout
     */
    public static void displaySmallHeader(String title, String border, int width) {
        System.out.println(border + "\n  " + title + "\n" + border);
    }
    
    /**
     * Displays a filter footer showing active filters.
     * 
     * @param filterSettings The filter settings to display (null for no footer)
     */
    public static void displayFilterFooter(FilterSettings filterSettings) {
        if (filterSettings != null) {
            System.out.println("  " + "═".repeat(118));
            System.out.println("  FILTERS: " + filterSettings.getFilterSummary());
            System.out.println("  " + "═".repeat(118));
        }
    }
    
    /**
     * Waits for user to press Enter before continuing.
     * 
     * @param sc The Scanner to read input from
     */
    public static void waitForEnter(Scanner sc) {
        System.out.print("\n  Press Enter to continue...");
        sc.nextLine();
    }
    
    /**
     * Parses an integer with a fallback value.
     * Returns max + 1 if parsing fails (indicates invalid input).
     * 
     * @param s The string to parse
     * @param max The maximum valid value
     * @return Parsed integer or max + 1 if invalid
     */
    public static int parseInt(String s, int max) {
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return max + 1;
        }
    }
    
    /**
     * Extracts an integer ID from a formatted string.
     * Looks for pattern "ID: &lt;number&gt;".
     * 
     * @param str The formatted string
     * @return The extracted ID, or -1 if not found or invalid
     */
    public static int extractIDFromString(String str) {
        String idStr = extractIDString(str);
        if (idStr == null || idStr.isEmpty()) return -1;
        try {
            return Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    /**
     * Extracts a string ID from a formatted string.
     * Looks for pattern "ID: &lt;id&gt;".
     * 
     * @param str The formatted string
     * @return The extracted ID string, or null if not found
     */
    public static String extractStringIDFromString(String str) {
        return extractIDString(str);
    }
    
    /**
     * Common ID extraction logic shared by integer and string ID extractors.
     * Extracts text between "ID: " and next " |" delimiter or end of string.
     * Used internally by extractIDFromString and extractStringIDFromString.
     * 
     * @param str The string to extract ID from
     * @return The extracted ID string, or null if "ID: " pattern not found
     */
    private static String extractIDString(String str) {
        int start = str.indexOf("ID: ");
        if (start == -1) return null;
        int end = str.indexOf(" |", start);
        return (end == -1 ? str.substring(start + 4) : str.substring(start + 4, end)).trim();
    }
    
    /**
     * Centers text within a specified width by adding leading spaces.
     * Calculates padding needed to center text and adds appropriate spacing.
     * 
     * @param text The text to center
     * @param width The total width for centering
     * @return Centered text with leading spaces
     */
    public static String centerText(String text, int width) {
        int padding = (width - text.length()) / 2;
        return " ".repeat(Math.max(0, padding)) + text;
    }
}

