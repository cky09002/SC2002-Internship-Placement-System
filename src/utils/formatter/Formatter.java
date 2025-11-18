package utils.formatter;

/**
 * Interface for reusable formatting components.
 * Promotes polymorphism and reduces code duplication across controllers.
 * 
 * Implementations should provide specific formatting logic for different object types.
 */
public interface Formatter {
    /**
     * Format an object to string representation
     * @param object Object to format
     * @return Formatted string
     */
    String format(Object object);
}

