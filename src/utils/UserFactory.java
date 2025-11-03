package Assignment.src.utils;

import Assignment.src.model.*;
import Assignment.src.constant.UserType;

public final class UserFactory {
    private UserFactory() {}
    private static final String DEFAULT_PASSWORD = "password";

    public static User fromCsv(UserType type, String[] cols) {
        switch (type) {
            case STUDENT:
                String sid = safe(cols,0);
                ValidationHelper.validateUserID(sid, UserType.STUDENT);
                String sname = safe(cols,1);
                String major = safe(cols,2);
                int year = parseIntSafe(safe(cols,3), 1);
                String email = safe(cols,4);
                String password = safe(cols,5);
                if (password.isEmpty()) password = DEFAULT_PASSWORD;
                return new Student(sid, sname, password, email, year, major);

            case STAFF:
                String staffId = safe(cols,0);
                ValidationHelper.validateUserID(staffId, UserType.STAFF);
                String staffName = safe(cols,1);
                String staffDept = safe(cols,3);
                String staff_email = safe(cols,4);
                String staffPassword = safe(cols,5);
                if (staffPassword.isEmpty()) staffPassword = DEFAULT_PASSWORD;
                return new Staff(staffId, staffName, staffPassword, staff_email, staffDept);

            case COMPANY_REPRESENTATIVE:
                String crepId = safe(cols,0);
                ValidationHelper.validateUserID(crepId, UserType.COMPANY_REPRESENTATIVE);
                String crepName = safe(cols,1);
                String company = safe(cols,2);
                String dept = safe(cols,3);
                String position = safe(cols,4);
                String comp_email = safe(cols,5);
                String compPassword = safe(cols,6);
                String status = safe(cols,7);
                if (compPassword.isEmpty()) compPassword = DEFAULT_PASSWORD;
                return new CompanyRepresentative(crepId, crepName, compPassword, comp_email, company, dept, position, status);

            default:
                throw new IllegalArgumentException("Unsupported user type: " + type);
        }
    }

    private static String safe(String[] arr, int idx) {
        if (arr == null || idx >= arr.length) return "";
        return arr[idx].trim();
    }

    private static int parseIntSafe(String s, int fallback) {
        try { return Integer.parseInt(s); } catch (Exception e) { return fallback; }
    }
}