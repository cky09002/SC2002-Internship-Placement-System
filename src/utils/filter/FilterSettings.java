package utils.filter;

import constant.InternshipStatus;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Reusable component to store filter settings for internship listings.
 * Settings persist across menu page navigation.
 * Maintains state for status, major, level, date ranges, company, keyword filters, and sort order.
 * 
 * @author NTU SC2002 Group
 * @version 1.0
 * @since 2025-11-16
 */
public class FilterSettings {
    /** Status filter (enum value) */
    private InternshipStatus statusFilter;
    
    /** Status filter string (for special filters like "AVAILABLE" or "FILLED") */
    private String statusFilterStr;
    
    /** Major filter */
    private String majorFilter;
    
    /** Level filter (Basic, Intermediate, Advanced) */
    private String levelFilter;
    
    /** Minimum opening date filter */
    private LocalDate openingDateFilter;
    
    /** Maximum closing date filter */
    private LocalDate closingDateFilter;
    
    /** Company name filter */
    private String companyFilter;
    
    /** Keyword search filter */
    private String keywordFilter;
    
    /** Sort order (default: ALPHABETICAL by company) */
    private String sortOrder = "ALPHABETICAL";
    
    /**
     * Constructs a FilterSettings with default values.
     * Initializes with no filters and alphabetical sort order.
     */
    public FilterSettings() {
        // Default: no filters, alphabetical order by company
        reset();
    }
    
    /**
     * Resets all filters to default values.
     * Clears all filter criteria and restores alphabetical sort order.
     */
    public void reset() {
        this.statusFilter = null;
        this.majorFilter = null;
        this.levelFilter = null;
        this.openingDateFilter = null;
        this.closingDateFilter = null;
        this.companyFilter = null;
        this.keywordFilter = null;
        this.sortOrder = "ALPHABETICAL";
    }
    
    // Getters
    /**
     * Gets the status filter enum value.
     * @return the status filter (enum)
     */
    public InternshipStatus getStatusFilter() { return statusFilter; }
    
    /**
     * Gets the status filter as string.
     * @return the status filter string
     */
    public String getStatusFilterStr() { return statusFilterStr; }
    
    /**
     * Gets the major filter.
     * @return the major filter
     */
    public String getMajorFilter() { return majorFilter; }
    
    /**
     * Gets the level filter.
     * @return the level filter
     */
    public String getLevelFilter() { return levelFilter; }
    
    /**
     * Gets the opening date filter.
     * @return the opening date filter
     */
    public LocalDate getOpeningDateFilter() { return openingDateFilter; }
    
    /**
     * Gets the closing date filter.
     * @return the closing date filter
     */
    public LocalDate getClosingDateFilter() { return closingDateFilter; }
    
    /**
     * Gets the company filter.
     * @return the company filter
     */
    public String getCompanyFilter() { return companyFilter; }
    
    /**
     * Gets the keyword filter.
     * @return the keyword filter
     */
    public String getKeywordFilter() { return keywordFilter; }
    
    /**
     * Gets the sort order.
     * @return the sort order
     */
    public String getSortOrder() { return sortOrder; }
    
    // Setters
    /**
     * Sets the status filter using enum value.
     * 
     * @param status The status to filter by (null to clear)
     */
    public void setStatusFilter(InternshipStatus status) { 
        this.statusFilter = status;
        this.statusFilterStr = status != null ? status.toString() : null;
    }
    
    /**
     * Sets the status filter using string value.
     * Supports special filters like "AVAILABLE" or "FILLED".
     * 
     * @param statusStr The status string to filter by (null to clear)
     */
    public void setStatusFilterStr(String statusStr) {
        this.statusFilterStr = statusStr;
        // Status filter string is for user-facing filters like "Available" or "Filled"
        // Don't set statusFilter enum for these - they're handled specially in filter logic
        if (statusStr != null && !statusStr.equalsIgnoreCase("AVAILABLE") && !statusStr.equalsIgnoreCase("FILLED")) {
            try {
                this.statusFilter = InternshipStatus.valueOf(statusStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                this.statusFilter = null;
            }
        } else {
            this.statusFilter = null;
        }
    }
    
    /**
     * Sets the major filter.
     * 
     * @param major The major to filter by (null to clear)
     */
    public void setMajorFilter(String major) { 
        this.majorFilter = major; 
    }
    
    /**
     * Sets the level filter.
     * 
     * @param level The level to filter by (null to clear)
     */
    public void setLevelFilter(String level) { 
        this.levelFilter = level; 
    }
    
    /**
     * Sets the opening date filter (minimum date).
     * 
     * @param date The minimum opening date (null to clear)
     */
    public void setOpeningDateFilter(LocalDate date) {
        this.openingDateFilter = date;
    }
    
    /**
     * Sets the closing date filter (maximum date).
     * 
     * @param date The maximum closing date (null to clear)
     */
    public void setClosingDateFilter(LocalDate date) { 
        this.closingDateFilter = date; 
    }
    
    /**
     * Sets the company filter.
     * 
     * @param company The company to filter by (null to clear)
     */
    public void setCompanyFilter(String company) {
        this.companyFilter = company;
    }
    
    /**
     * Sets the keyword filter.
     * Empty strings are treated as null.
     * 
     * @param keyword The keyword to filter by (null to clear)
     */
    public void setKeywordFilter(String keyword) {
        this.keywordFilter = keyword != null && keyword.trim().isEmpty() ? null : keyword;
    }
    
    /**
     * Sets the sort order.
     * 
     * @param order The sort order (e.g., "ALPHABETICAL", "ID", "CLOSING_DATE")
     */
    public void setSortOrder(String order) { 
        this.sortOrder = order; 
    }
    
    /**
     * Checks if any filters are currently active.
     * 
     * @return true if at least one filter is set, false otherwise
     */
    public boolean hasActiveFilters() {
        return (statusFilter != null || (statusFilterStr != null && !statusFilterStr.isEmpty())) || majorFilter != null || 
               levelFilter != null || openingDateFilter != null ||
               closingDateFilter != null || companyFilter != null ||
               (keywordFilter != null && !keywordFilter.trim().isEmpty()) ||
               !sortOrder.equals("ALPHABETICAL");
    }
    
    /**
     * Gets a human-readable summary of active filters.
     * 
     * @return Summary string describing all active filters
     */
    public String getFilterSummary() {
        if (!hasActiveFilters()) {
            return "No filters applied (Alphabetical order)";
        }
        
        List<String> active = new ArrayList<>();
        if (statusFilter != null) active.add("Status: " + statusFilter);
        else if (statusFilterStr != null && !statusFilterStr.isEmpty()) active.add("Status: " + statusFilterStr);
        if (majorFilter != null) active.add("Major: " + majorFilter);
        if (levelFilter != null) active.add("Level: " + levelFilter);
        if (openingDateFilter != null) active.add("Opening Date: >= " + openingDateFilter);
        if (closingDateFilter != null) active.add("Closing Date: <= " + closingDateFilter);
        if (companyFilter != null) active.add("Company: " + companyFilter);
        if (keywordFilter != null && !keywordFilter.trim().isEmpty()) active.add("Keyword: " + keywordFilter);
        if (!sortOrder.equals("ALPHABETICAL")) active.add("Sort: " + sortOrder);
        
        return String.join(", ", active);
    }
}

