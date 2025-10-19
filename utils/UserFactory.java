package Assignment;

public final class UserFactory {
    private UserFactory() {}

    // default password for imported User
    private static final String DEFAULT_PASSWORD = "password";

    /**
     * Parse csv columns into the appropriate User subclass.
     * Adjust indices if your CSV columns differ from the assumed layout.
     *
     * Expected columns (examples):
     * - CompanyRep CSV: CompanyRepID,Name,CompanyName,Department,Position,Email,Status
     * - Student CSV:   StudentID,Name,YearOfStudy,Major,Email
     * - Staff CSV:     StaffID,Name,Department,Email
     */
    public static User fromCsv(UserType type, String[] cols) {
        switch (type) {
            case STUDENT:
                // safe access and simple parsing
                String sid = safe(cols,0);
                String sname = safe(cols,1);
                int year = parseIntSafe(safe(cols,2), 1);
                String major = safe(cols,3);
                String email = safe(cols,4);
                return new Student(sid, sname, DEFAULT_PASSWORD,email, year, major);

            case STAFF:
                String staffId = safe(cols,0);
                String staffName = safe(cols,1);
                String staffDept = safe(cols,3);
                String staff_email = safe(cols,4);
                return new Staff(staffId, staffName, DEFAULT_PASSWORD, staff_email, staffDept);

            case COMPANY_REPRESENTATIVE:
                // CompanyRepID should be company email (per your rule) — use cols[0].
                String crepId = safe(cols,0);
                String crepName = safe(cols,1);
                String company = safe(cols,2);
                String dept = safe(cols,3);
                String position = safe(cols,4);
                String comp_email = safe(cols,5);
                String status = safe(cols,6);
                return new CompanyRepresentative(crepId, crepName, DEFAULT_PASSWORD, comp_email,company, dept, position,status);

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