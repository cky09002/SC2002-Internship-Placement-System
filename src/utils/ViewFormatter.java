package Assignment.src.utils;

import java.util.Scanner;

/**
 * Reusable component for all view display operations.
 * Consolidates common display methods used across views and formatters.
 */
public class ViewFormatter {
    
    /** Display large centered header (for main title) - bigger visual presence */
    public static void displayLargeHeader(String title, String border, int width) {
        // Make title appear bigger - centered with no extra newlines before
        int padding = (width - title.length()) / 2;
        String centered = " ".repeat(Math.max(0, padding)) + title;
        System.out.println(border + "\n" + centered + "\n" + border);
    }
    
    /** Display centered header (for menus/dashboards) - smaller than title */
    public static void displayHeader(String title, String border, int width) {
        System.out.println(border + "\n  " + title + "\n" + border);
    }
    
    /** Display small header (for filters) - smallest */
    public static void displaySmallHeader(String title, String border, int width) {
        System.out.println(border + "\n  " + title + "\n" + border);
    }
    
    /** Display filter footer (smallest) */
    public static void displayFilterFooter(FilterSettings filterSettings) {
        if (filterSettings != null) {
            System.out.println("  " + "═".repeat(118));
            System.out.println("  FILTERS: " + filterSettings.getFilterSummary());
            System.out.println("  " + "═".repeat(118));
        }
    }
    
    /** Wait for enter */
    public static void waitForEnter(Scanner sc) {
        System.out.print("\n  Press Enter to continue...");
        sc.nextLine();
    }
    
    /** Parse integer with fallback */
    public static int parseInt(String s, int max) {
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return max + 1;
        }
    }
    
    /** Extract ID from formatted string */
    public static int extractIDFromString(String str) {
        String idStr = extractIDString(str);
        if (idStr == null || idStr.isEmpty()) return -1;
        try {
            return Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    /** Extract string ID from formatted string */
    public static String extractStringIDFromString(String str) {
        return extractIDString(str);
    }
    
    /** Common ID extraction logic */
    private static String extractIDString(String str) {
        int start = str.indexOf("ID: ");
        if (start == -1) return null;
        int end = str.indexOf(" |", start);
        return (end == -1 ? str.substring(start + 4) : str.substring(start + 4, end)).trim();
    }
    
    /** Center text within width */
    public static String centerText(String text, int width) {
        int padding = (width - text.length()) / 2;
        return " ".repeat(Math.max(0, padding)) + text;
    }
}

