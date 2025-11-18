package utils.csv;

/**
 * Interface for converting objects to CSV format.
 * Implementations provide object-to-CSV serialization and deserialization logic.
 * 
 * @param <T> The type of object this handler processes
 */
public interface CsvHandler<T> {
    
    /**
     * Load data from CSV file into memory.
     * Each implementation loads its specific object type.
     */
    void loadFromCsv();
    
    /**
     * Save an object to CSV file.
     * @param obj the object to save
     */
    void saveToCsv(T obj);
    
    /**
     * Convert an object to CSV line format.
     * @param object the object to convert
     * @return CSV formatted string
     */
    String formatCsvLine(T object);
    
    /**
     * Escape commas and quotes in CSV strings.
     * Default helper method for all CSV handlers.
     * @param s the string to escape
     * @return the escaped string
     */
    default String escapeCSV(String s) {
        if (s == null) return "";
        // Wrap in quotes if contains comma or newline
        if (s.contains(",") || s.contains("\n")) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }
}
