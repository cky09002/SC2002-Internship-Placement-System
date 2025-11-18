package constant;

/**
 * Enumeration of possible application statuses.
 * Represents the lifecycle of a student application from submission to final outcome.
 * 
 * @author NTU SC2002 Group
 * @version 1.0
 * @since 2025-11-16
 */
public enum ApplicationStatus {
    /**
     * Application submitted and awaiting company review.
     * Initial state when student applies to an internship.
     * Company representative can approve or reject from this state.
     */
    PENDING,
    
    /**
     * Application approved by company, awaiting student acceptance.
     * Company representative has marked application as successful.
     * Student can accept or decline the offer.
     */
    SUCCESSFUL,
    
    /**
     * Student has confirmed and accepted the successful application.
     * Final positive state - placement is confirmed.
     * All other applications from same student are automatically withdrawn.
     */
    ACCEPTED,
    
    /**
     * Application rejected or declined.
     * Can result from company rejection or student declining an offer.
     * Terminal state - no further actions possible.
     */
    UNSUCCESSFUL,
    
    /**
     * Student has requested to withdraw the application.
     * Requires staff approval before final withdrawal.
     * Staff can approve (moves to WITHDRAWN) or reject (restores previous status).
     */
    WITHDRAWAL_REQUESTED,
    
    /**
     * Application has been withdrawn.
     * Can result from approved withdrawal request or automatic withdrawal
     * when student accepts another internship.
     * Terminal state - no further actions possible.
     */
    WITHDRAWN
}
