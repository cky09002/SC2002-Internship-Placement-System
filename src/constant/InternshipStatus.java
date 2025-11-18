package constant;

/**
 * Enumeration of possible internship statuses.
 * Represents the approval lifecycle and availability status.
 * 
 * @author NTU SC2002 Group
 * @version 1.0
 * @since 2025-11-16
 */
public enum InternshipStatus {
  /**
   * Internship awaiting staff approval.
   * Initial state when company representative creates a new internship.
   * Staff can approve or reject from this state.
   * Not visible to students until approved.
   */
  PENDING,
  
  /**
   * Internship approved by staff and potentially visible to students.
   * Company representative can toggle visibility on/off.
   * Students can view and apply if visibility is on and they meet criteria.
   * Main operational state for active internships.
   */
  APPROVED,
  
  /**
   * Internship rejected by staff.
   * Cannot be made visible or accept applications.
   * Terminal state - requires creating new internship to post again.
   */
  REJECTED,
  
  /**
   * All internship slots have been filled.
   * Automatically set when number of accepted applications reaches slot limit.
   * No longer visible to students in "Available" filter.
   * Used by filters, formatters, and visibility checks.
   */
  FILLED
}
