package controller.interfaces;

import model.*;

/**
 * Interface for validating internship operations.
 * Follows Interface Segregation Principle - clients that only need validation
 * depend on this minimal interface.
 * 
 * @author NTU SC2002 Group
 * @version 1.0
 * @since 2025-11-16
 */
public interface InternshipValidator {
    /**
     * Checks if an internship is owned by a company representative.
     * 
     * @param internshipID The ID of the internship
     * @param companyRep The company representative to check
     * @return true if owned by the representative, false otherwise
     * @throws IllegalArgumentException if internship not found
     */
    boolean isInternshipOwnedBy(int internshipID, CompanyRepresentative companyRep);
    
    /**
     * Checks if an internship can be edited.
     * 
     * @param internshipID The ID of the internship
     * @return true if editable, false otherwise
     * @throws IllegalArgumentException if internship not found
     */
    boolean canEditInternship(int internshipID);
    
    /**
     * Gets the status of an internship.
     * 
     * @param internshipID The ID of the internship
     * @return The InternshipStatus
     * @throws IllegalArgumentException if internship not found
     */
    constant.InternshipStatus getInternshipStatus(int internshipID);
}

