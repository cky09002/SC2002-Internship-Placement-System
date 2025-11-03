package Assignment.src.utils;

import Assignment.src.model.Internship;
import Assignment.src.constant.InternshipStatus;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Reusable component for filtering and sorting internships.
 * Implements Strategy pattern for different filter criteria.
 * Default sorting is alphabetical by company name.
 */
public class InternshipFilter {
    
    /**
     * Filter and sort internships based on filter settings
     * Uses List<?> for MVC compliance (no model imports)
     * @param internships List of internships to filter
     * @param settings Filter settings to apply
     * @return Filtered and sorted list
     */
    @SuppressWarnings("unchecked")
    public static List<?> applyFilters(List<?> internships, FilterSettings settings) {
        List<Internship> typedInternships = (List<Internship>) internships;
        Stream<Internship> stream = typedInternships.stream();
        
        // Apply filters
        if (settings.getStatusFilterStr() != null && 
            (settings.getStatusFilterStr().equalsIgnoreCase("AVAILABLE") || 
             settings.getStatusFilterStr().equalsIgnoreCase("FILLED"))) {
            stream = applyStatusFilterByString(stream, settings.getStatusFilterStr());
        } else {
            stream = applyStatusFilter(stream, settings.getStatusFilter());
        }
        stream = applyMajorFilter(stream, settings.getMajorFilter());
        stream = applyLevelFilter(stream, settings.getLevelFilter());
        stream = applyOpeningDateFilter(stream, settings.getOpeningDateFilter());
        stream = applyClosingDateFilter(stream, settings.getClosingDateFilter());
        stream = applyCompanyFilter(stream, settings.getCompanyFilter());
        stream = applyKeywordFilter(stream, settings.getKeywordFilter());
        
        // Convert to list for sorting
        List<Internship> filtered = stream.collect(Collectors.toList());
        
        // Apply sorting (default: alphabetical by company)
        return (List<?>) applySorting(filtered, settings.getSortOrder());
    }
    
    /**
     * Filter by status
     * Special handling for "VACANT" - shows APPROVED internships with available slots
     */
    private static Stream<Internship> applyStatusFilter(Stream<Internship> stream, InternshipStatus status) {
        if (status == null) return stream;
        return stream.filter(i -> i.getStatus() == status);
    }
    
    /**
     * Filter by status string (supports "Available" and "Filled" as user-facing filters)
     */
    private static Stream<Internship> applyStatusFilterByString(Stream<Internship> stream, String statusStr) {
        if (statusStr == null || statusStr.isEmpty()) return stream;
        if (statusStr.equalsIgnoreCase("AVAILABLE")) {
            return stream.filter(i -> !i.isFilled());
        } else if (statusStr.equalsIgnoreCase("FILLED")) {
            return stream.filter(Internship::isFilled);
        }
        // Try to match enum value (for backward compatibility)
        try {
            InternshipStatus status = InternshipStatus.valueOf(statusStr.toUpperCase());
            return applyStatusFilter(stream, status);
        } catch (IllegalArgumentException e) {
            return stream;
        }
    }
    
    /**
     * Filter by preferred major
     */
    private static Stream<Internship> applyMajorFilter(Stream<Internship> stream, String major) {
        if (major == null || major.trim().isEmpty()) return stream;
        return stream.filter(i -> {
            String preferredMajor = i.getPreferredMajor();
            return preferredMajor != null && preferredMajor.equalsIgnoreCase(major);
        });
    }
    
    /**
     * Filter by internship level
     */
    private static Stream<Internship> applyLevelFilter(Stream<Internship> stream, String level) {
        if (level == null || level.trim().isEmpty()) return stream;
        return stream.filter(i -> {
            String internshipLevel = i.getLevel();
            return internshipLevel != null && internshipLevel.equalsIgnoreCase(level);
        });
    }
    
    /**
     * Filter by opening date (on or after specified date)
     */
    private static Stream<Internship> applyOpeningDateFilter(Stream<Internship> stream, LocalDate minDate) {
        if (minDate == null) return stream;
        return stream.filter(i -> !i.getOpenDate().isBefore(minDate));
    }
    
    /**
     * Filter by closing date (on or before specified date)
     */
    private static Stream<Internship> applyClosingDateFilter(Stream<Internship> stream, LocalDate maxDate) {
        if (maxDate == null) return stream;
        return stream.filter(i -> !i.getCloseDate().isAfter(maxDate));
    }
    
    /**
     * Filter by company name (exact match)
     */
    private static Stream<Internship> applyCompanyFilter(Stream<Internship> stream, String company) {
        if (company == null || company.trim().isEmpty()) return stream;
        return stream.filter(i -> {
            String companyName = i.getCompanyName();
            return companyName != null && companyName.equalsIgnoreCase(company);
        });
    }
    
    /**
     * Filter by keyword (searches in title, description, and company name)
     */
    private static Stream<Internship> applyKeywordFilter(Stream<Internship> stream, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) return stream;
        String lowerKeyword = keyword.toLowerCase().trim();
        return stream.filter(i -> {
            String title = i.getTitle() != null ? i.getTitle().toLowerCase() : "";
            String description = i.getDescription() != null ? i.getDescription().toLowerCase() : "";
            String companyName = i.getCompanyName() != null ? i.getCompanyName().toLowerCase() : "";
            return title.contains(lowerKeyword) || 
                   description.contains(lowerKeyword) || 
                   companyName.contains(lowerKeyword);
        });
    }
    
    /**
     * Apply sorting based on sort order
     * Default is alphabetical by ID
     */
    private static List<Internship> applySorting(List<Internship> internships, String sortOrder) {
        Comparator<Internship> comparator;
        
        switch (sortOrder.toUpperCase()) {
            case "ID":
                comparator = Comparator.comparing(Internship::getID);
                break;
            case "CLOSING_DATE":
                comparator = Comparator.comparing(Internship::getCloseDate);
                break;
            case "OPENING_DATE":
                comparator = Comparator.comparing(Internship::getOpenDate);
                break;
            case "COMPANY":
                comparator = Comparator.comparing(Internship::getCompanyName)
                                       .thenComparing(Internship::getTitle);
                break;
            case "LEVEL":
                comparator = Comparator.comparing(Internship::getLevel)
                                       .thenComparing(Internship::getTitle);
                break;
            case "ALPHABETICAL":
            default:
                // Default: alphabetical by company name
                comparator = Comparator.comparing(Internship::getCompanyName)
                                       .thenComparing(Internship::getTitle);
                break;
        }
        
        return internships.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }
    
    /**
     * Get available filter options for status
     * Shows "Available" and "Filled" for students/company reps
     * Shows PENDING, APPROVED, REJECTED for staff approve/reject workflow
     */
    public static String[] getStatusOptions() {
        return new String[]{"All", "Available", "Filled", "PENDING", "APPROVED", "REJECTED"};
    }
    
    /**
     * Get available filter options for level
     */
    public static String[] getLevelOptions() {
        return new String[]{"All", "Basic", "Intermediate", "Advanced"};
    }
    
    /**
     * Get unique majors from internships list (using reflection for MVC compliance)
     */
    @SuppressWarnings("unchecked")
    public static List<String> getAvailableMajors(List<?> internships) {
        return internships.stream()
                .map(internship -> {
                    try {
                        return (String) internship.getClass().getMethod("getPreferredMajor").invoke(internship);
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(major -> major != null && !major.trim().isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
    
    /**
     * Get unique company names from internships list (using reflection for MVC compliance)
     */
    @SuppressWarnings("unchecked")
    public static List<String> getAvailableCompanies(List<?> internships) {
        return internships.stream()
                .map(internship -> {
                    try {
                        return (String) internship.getClass().getMethod("getCompanyName").invoke(internship);
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(company -> company != null && !company.trim().isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
}

