package utils.csv;

/**
 * Interface for CSV handlers that support deletion operations.
 * Follows Interface Segregation Principle - only handlers that need deletion implement this.
 * 
 * @param <T> The type of object this handler processes
 * @author NTU SC2002 Group
 * @version 1.0
 * @since 2025-11-16
 */
public interface CsvDeletable<T> {
    /**
     * Delete an object from CSV file by its ID.
     * 
     * @param id The ID of the object to delete
     * @throws RuntimeException if file I/O fails
     */
    void deleteFromCsv(int id);
}

