package constant;

/**
 * Enumeration of staff approval statuses.
 * Used primarily for company representative account approval.
 * 
 * @author NTU SC2002 Group
 * @version 1.0
 * @since 2025-11-16
 */
public enum StaffApprovalStatus {
    /**
     * Awaiting staff approval.
     * Initial state for company representative accounts upon registration.
     * Account cannot login or access system until approved.
     */
    PENDING,
    
    /**
     * Approved by staff.
     * Company representative can now login and create internships.
     * Final positive state allowing full system access.
     */
    APPROVED,
    
    /**
     * Rejected by staff.
     * Company representative account denied access.
     * Cannot login or create internships.
     * Would require new registration to try again.
     */
    REJECTED;
    
    /**
     * Converts enum to CSV string format.
     * @return "Approved", "Rejected", or "Pending"
     */
    public String toCsvString() {
        return this.name().charAt(0) + this.name().substring(1).toLowerCase();
    }
    
    @Override
    public String toString() {
        return this == PENDING ? "Pending Approval" : toCsvString();
    }
}

