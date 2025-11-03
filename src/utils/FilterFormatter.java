package Assignment.src.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Default implementation for filter settings formatting.
 * Provides structured display of active filters and sorting options.
 * Can be extended for different filter display styles.
 */
public class FilterFormatter implements Formatter {
    private boolean showActiveOnly;
    private String header;
    
    /**
     * Create a new FilterFormatter
     * @param header Optional header text (null for no header)
     * @param showActiveOnly Whether to show only active filters (or all filter states)
     */
    public FilterFormatter(String header, boolean showActiveOnly) {
        this.header = header;
        this.showActiveOnly = showActiveOnly;
    }
    
    @Override
    public String format(Object object) {
        if (!(object instanceof FilterSettings)) {
            throw new IllegalArgumentException("Object must be a FilterSettings");
        }
        
        FilterSettings settings = (FilterSettings) object;
        StringBuilder details = new StringBuilder();
        details.append("\n").append("═".repeat(80)).append("\n");
        
        if (header != null && !header.isEmpty()) {
            details.append("  ").append(header).append("\n");
            details.append("═".repeat(80)).append("\n");
        }
        
        details.append("\n  Current Filter Settings:\n");
        
        List<String> activeFilters = new ArrayList<>();
        
        if (settings.getStatusFilter() != null) {
            activeFilters.add(String.format("    Status: %s", settings.getStatusFilter()));
        } else if (!showActiveOnly) {
            details.append("    Status: All\n");
        }
        
        if (settings.getMajorFilter() != null) {
            activeFilters.add(String.format("    Major: %s", settings.getMajorFilter()));
        } else if (!showActiveOnly) {
            details.append("    Major: All\n");
        }
        
        if (settings.getLevelFilter() != null) {
            activeFilters.add(String.format("    Level: %s", settings.getLevelFilter()));
        } else if (!showActiveOnly) {
            details.append("    Level: All\n");
        }
        
        if (settings.getClosingDateFilter() != null) {
            activeFilters.add(String.format("    Closing Date: ≤ %s", settings.getClosingDateFilter()));
        } else if (!showActiveOnly) {
            details.append("    Closing Date: No filter\n");
        }
        
        details.append(String.format("    Sort Order: %s%n", settings.getSortOrder()));
        
        if (showActiveOnly) {
            if (activeFilters.isEmpty()) {
                details.append("    No active filters\n");
            } else {
                for (String filter : activeFilters) {
                    details.append(filter).append("\n");
                }
            }
        } else {
            for (String filter : activeFilters) {
                details.append(filter).append("\n");
            }
        }
        
        details.append("\n  Summary: ").append(settings.getFilterSummary()).append("\n");
        details.append("═".repeat(80));
        return details.toString();
    }
}

