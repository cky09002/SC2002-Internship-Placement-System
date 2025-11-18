package controller.interfaces;

import model.*;

import java.time.LocalDate;

/**
 * Interface for writing/modifying internship data.
 * Follows Interface Segregation Principle - clients that only need to modify internships
 * depend on this minimal interface, not the full InternshipControllerInterface.
 * 
 * @author NTU SC2002 Group
 * @version 1.0
 * @since 2025-11-16
 */
public interface InternshipWriter {
    /**
     * Creates a new internship listing.
     * 
     * @param title The internship title
     * @param description The internship description
     * @param level The difficulty level
     * @param major The preferred major
     * @param openDate The opening date
     * @param closeDate The closing date
     * @param creator The company representative creating the internship
     * @param slots The number of available slots
     * @return The created Internship object
     * @throws IllegalArgumentException if validation fails
     */
    Internship createInternship(String title, String description, String level, String major,
                               LocalDate openDate, LocalDate closeDate, 
                               CompanyRepresentative creator, int slots);
    
    /**
     * Updates an existing internship's details.
     * 
     * @param internshipID The ID of the internship to update
     * @param title New title (null to keep existing)
     * @param description New description (null to keep existing)
     * @param level New level (null to keep existing)
     * @param major New preferred major (null to keep existing)
     * @param openDate New opening date (null to keep existing)
     * @param closeDate New closing date (null to keep existing)
     * @param slots New slot count (null to keep existing)
     * @throws IllegalArgumentException if internship not found
     */
    void updateInternship(int internshipID, String title, String description, String level, 
                         String major, LocalDate openDate, LocalDate closeDate, Integer slots);
    
    /**
     * Deletes an internship.
     * 
     * @param internshipID The ID of the internship to delete
     * @throws IllegalArgumentException if internship not found or cannot be deleted
     */
    void deleteInternship(int internshipID);
    
    /**
     * Toggles the visibility of an internship.
     * 
     * @param internshipID The ID of the internship
     * @return true if now visible, false if now invisible
     * @throws IllegalArgumentException if internship not found
     */
    boolean toggleInternshipVisibility(int internshipID);
    
    /**
     * Updates internship approval status.
     * 
     * @param internshipID The ID of the internship
     * @param approve true to approve, false to reject
     * @throws IllegalArgumentException if internship not found
     */
    void updateInternshipApproval(int internshipID, boolean approve);
    
    /**
     * Saves an internship to CSV.
     * 
     * @param internship The internship to save
     */
    void saveInternship(Internship internship);
}

