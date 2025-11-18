package utils.csv;

import model.*;

/**
 * Interface for user CSV persistence operations.
 * Follows Dependency Inversion Principle - clients depend on this abstraction, not concrete implementation.
 * Follows Interface Segregation Principle - focused interface with only user-related CSV operations.
 * 
 * @author NTU SC2002 Group
 * @version 1.0
 * @since 2025-11-16
 */
public interface UserCsvHandlerInterface {
    /**
     * Save a newly registered company representative to the CSV file.
     * 
     * @param compRep the company representative to save
     * @throws RuntimeException if file I/O fails
     */
    void saveCompanyRepToCsv(CompanyRepresentative compRep);
    
    /**
     * Update a company rep's approval status in the CSV file.
     * 
     * @param compRep the company representative to update
     * @throws RuntimeException if file I/O fails
     */
    void updateCompanyRepStatusInCsv(CompanyRepresentative compRep);
    
    /**
     * Save password changes to the appropriate CSV file based on user type.
     * 
     * @param user the user whose password changed
     * @throws RuntimeException if file I/O fails
     */
    void savePasswordChangeToCsv(User user);
}

