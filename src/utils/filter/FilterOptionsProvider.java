package utils.filter;

/**
 * Interface for providing filter options based on user type.
 * Follows Interface Segregation Principle - only provides filter-related options.
 * Controllers implement this to provide user-type-specific filter options.
 * 
 * @author NTU SC2002 Group
 * @version 1.0
 * @since 2025-11-18
 */
public interface FilterOptionsProvider {
    /**
     * Gets the available status filter options for this user type.
     * @return Array of status filter option strings
     */
    String[] getStatusFilterOptions();
}

