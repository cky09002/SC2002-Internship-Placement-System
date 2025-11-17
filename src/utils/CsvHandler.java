package Assignment.src.utils;

/**
 * Base interface for CSV handlers to ensure consistency.
 * Promotes polymorphism and code reuse across CSV handlers.
 */
public interface CsvHandler {
    /**
     * Escape commas and quotes in CSV strings.
     * Shared utility method for all CSV handlers.
     */
    static String escapeCSV(String s) {
        if (s == null) return "";
        // For simplicity, just wrap in quotes if contains comma or newline
        if (s.contains(",") || s.contains("\n")) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }

    void loadFromCsv();
    void saveToCsv(Object obj);
    String formatCsvLine(Object obj);
}
