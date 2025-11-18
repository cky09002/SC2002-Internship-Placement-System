package utils.validation;

import constant.UserType;

/**
 * Utility class for input validation.
 * Provides validation methods for email, user IDs, and common input constraints.
 * 
 * @author NTU SC2002 Group
 * @version 1.0
 * @since 2025-11-16
 */
public class ValidationHelper {
    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private ValidationHelper() {
        // Utility class
    }
    
    /** Regular expression pattern for email validation */
    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@(.+)$";
    
    /** Regular expression pattern for student ID validation (e.g., U2310001A) */
    private static final String STUDENT_ID_PATTERN = "^U\\d{6,7}[A-Z]$";
    
    /** Regular expression pattern for staff ID validation (e.g., sng001) */
    private static final String STAFF_ID_PATTERN = "^[a-z]+\\d+$";
    
    /**
     * Validates an email address format.
     * 
     * @param email The email to validate
     * @throws IllegalArgumentException if email is invalid
     */
    public static void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty.");
        }
        if (!email.trim().matches(EMAIL_PATTERN)) {
            throw new IllegalArgumentException("Invalid email format.");
        }
    }
    
    /**
     * Validates that a string value is not empty.
     * 
     * @param value The value to validate
     * @param fieldName The name of the field (for error message)
     * @throws IllegalArgumentException if value is null or empty
     */
    public static void validateNotEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty.");
        }
    }
    
    /**
     * Validates that an integer value is within a specified range.
     * 
     * @param value The value to validate
     * @param min Minimum allowed value (inclusive)
     * @param max Maximum allowed value (inclusive)
     * @param fieldName The name of the field (for error message)
     * @throws IllegalArgumentException if value is out of range
     */
    public static void validateRange(int value, int min, int max, String fieldName) {
        if (value < min || value > max) {
            throw new IllegalArgumentException(fieldName + " must be between " + min + " and " + max + ".");
        }
    }
    
    /**
     * Validates a user ID based on user type.
     * Each user type has specific format requirements.
     * 
     * @param userID The user ID to validate
     * @param userType The type of user
     * @throws IllegalArgumentException if user ID format is invalid
     */
    public static void validateUserID(String userID, UserType userType) {
        validateNotEmpty(userID, "User ID");
        String trimmed = userID.trim();
        
        switch (userType) {
            case STUDENT:
                if (!trimmed.matches(STUDENT_ID_PATTERN)) {
                    throw new IllegalArgumentException("Invalid Student ID format. Must be U followed by 6-7 digits and ending with a letter (e.g., U2310001A).");
                }
                break;
            case STAFF:
                if (!trimmed.matches(STAFF_ID_PATTERN)) {
                    throw new IllegalArgumentException("Invalid Staff ID format. Must be lowercase letters followed by digits (e.g., sng001).");
                }
                break;
            case COMPANY_REPRESENTATIVE:
                validateEmail(trimmed);
                break;
            default:
                throw new IllegalArgumentException("Unknown user type.");
        }
    }
}

