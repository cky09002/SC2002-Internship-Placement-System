package Assignment.src.utils;

import Assignment.src.constant.UserType;

public class ValidationHelper {
    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@(.+)$";
    private static final String STUDENT_ID_PATTERN = "^U\\d{6,7}[A-Z]$";
    private static final String STAFF_ID_PATTERN = "^[a-z]+\\d+$";
    
    public static void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty.");
        }
        if (!email.trim().matches(EMAIL_PATTERN)) {
            throw new IllegalArgumentException("Invalid email format.");
        }
    }
    
    public static void validateNotEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty.");
        }
    }
    
    public static void validateRange(int value, int min, int max, String fieldName) {
        if (value < min || value > max) {
            throw new IllegalArgumentException(fieldName + " must be between " + min + " and " + max + ".");
        }
    }
    
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

