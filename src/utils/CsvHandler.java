package Assignment.src.utils;

/**
 * Generic CSV handler interface.
 * Defines required operations for loading and saving model objects.
 */
public interface CsvHandler<T> {

    /**
     * Load all model objects from the CSV file.
     */
    void loadFromCsv();

    /**
     * Save or update a model object in the CSV file.
     */
    void saveToCsv(T obj);

    /**
     * Escape helper shared across implementations.
     */
    static String escapeCSV(String s) {
        if (s == null) return "";
        if (s.contains(",") || s.contains("\n")) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }
}

