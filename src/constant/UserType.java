package constant;

/**
 * Enumeration of user types in the system.
 * Used for user authentication and role-based access control.
 * 
 * @author NTU SC2002 Group
 * @version 1.0
 * @since 2025-11-16
 */
public enum UserType {
    /**
     * Student user role.
     * Can view available internships, submit applications,
     * accept offers, and request withdrawals.
     * Permissions limited to own profile and applications.
     */
    STUDENT,
    
    /**
     * Career center staff role.
     * Can approve/reject company representatives, internship listings,
     * and withdrawal requests.
     * Has oversight of entire system but cannot create internships.
     */
    STAFF,
    
    /**
     * Company representative role.
     * Can create and manage internship listings,
     * view and approve applications, confirm placements.
     * Requires staff approval before accessing system.
     * Limited to internships created by their account.
     */
    COMPANY_REPRESENTATIVE
}