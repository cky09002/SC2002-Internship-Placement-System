package Assignment.src.utils;

import Assignment.src.constant.InternshipStatus;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Reusable component to store filter settings for internship listings.
 * Settings persist across menu page navigation.
 * Uses class fields to maintain state.
 */
public class FilterSettings {
    private InternshipStatus statusFilter;
    private String statusFilterStr; // For special filters like "VACANT"
    private String majorFilter;
    private String levelFilter;
    private LocalDate openingDateFilter; // Min opening date
    private LocalDate closingDateFilter; // Max closing date
    private String companyFilter;
    private String keywordFilter;
    private String sortOrder = "ALPHABETICAL"; // Default alphabetical order by company
    
    public FilterSettings() {
        // Default: no filters, alphabetical order by company
        reset();
    }
    
    /**
     * Reset all filters to default values
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
    public InternshipStatus getStatusFilter() { return statusFilter; }
    public String getStatusFilterStr() { return statusFilterStr; }
    public String getMajorFilter() { return majorFilter; }
    public String getLevelFilter() { return levelFilter; }
    public LocalDate getOpeningDateFilter() { return openingDateFilter; }
    public LocalDate getClosingDateFilter() { return closingDateFilter; }
    public String getCompanyFilter() { return companyFilter; }
    public String getKeywordFilter() { return keywordFilter; }
    public String getSortOrder() { return sortOrder; }
    
    // Setters
    public void setStatusFilter(InternshipStatus status) { 
        this.statusFilter = status;
        this.statusFilterStr = status != null ? status.toString() : null;
    }
    
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
    
    public void setMajorFilter(String major) { 
        this.majorFilter = major; 
    }
    
    public void setLevelFilter(String level) { 
        this.levelFilter = level; 
    }
    
    public void setOpeningDateFilter(LocalDate date) {
        this.openingDateFilter = date;
    }
    
    public void setClosingDateFilter(LocalDate date) { 
        this.closingDateFilter = date; 
    }
    
    public void setCompanyFilter(String company) {
        this.companyFilter = company;
    }
    
    public void setKeywordFilter(String keyword) {
        this.keywordFilter = keyword != null && keyword.trim().isEmpty() ? null : keyword;
    }
    
    public void setSortOrder(String order) { 
        this.sortOrder = order; 
    }
    
    /**
     * Check if any filters are active
     */
    public boolean hasActiveFilters() {
        return (statusFilter != null || (statusFilterStr != null && !statusFilterStr.isEmpty())) || majorFilter != null || 
               levelFilter != null || openingDateFilter != null ||
               closingDateFilter != null || companyFilter != null ||
               (keywordFilter != null && !keywordFilter.trim().isEmpty()) ||
               !sortOrder.equals("ALPHABETICAL");
    }
    
    /**
     * Get filter summary for display
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

