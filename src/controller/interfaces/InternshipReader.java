package controller.interfaces;

import model.*;
import constant.InternshipStatus;
import utils.filter.FilterSettings;

import java.util.List;

/**
 * Interface for reading internship data.
 * Follows Interface Segregation Principle - clients that only need to read internships
 * depend on this minimal interface, not the full InternshipControllerInterface.
 * 
 * @author NTU SC2002 Group
 * @version 1.0
 * @since 2025-11-16
 */
public interface InternshipReader {
    /**
     * Finds an internship by its ID.
     * 
     * @param internshipID The ID of the internship to find
     * @return The found Internship object
     * @throws IllegalArgumentException if internship not found
     */
    Internship findInternship(int internshipID);
    
    /**
     * Loads all internships from CSV file.
     */
    void loadInternshipsFromCsv();
    
    /**
     * Gets the status of an internship.
     * 
     * @param internshipID The ID of the internship
     * @return The InternshipStatus
     * @throws IllegalArgumentException if internship not found
     */
    InternshipStatus getInternshipStatus(int internshipID);
    
    /**
     * Retrieves all internships visible to a student.
     * 
     * @param student The student for visibility filtering
     * @param filterSettings Filter settings to apply
     * @return List of visible internships
     */
    List<Internship> getVisibleInternshipsForStudent(Student student, FilterSettings filterSettings);
    
    /**
     * Retrieves all internships with filtering.
     * 
     * @param filterSettings Filter settings to apply
     * @return List of filtered internships
     */
    List<Internship> getAllInternships(FilterSettings filterSettings);
    
    /**
     * Retrieves all internships without filtering.
     * 
     * @return List of all internships
     */
    List<Internship> getAllInternships();
    
    /**
     * Retrieves internships created by a company representative.
     * 
     * @param creator The company representative
     * @param filterSettings Filter settings to apply
     * @return List of internships created by the representative
     */
    List<Internship> getInternshipsByCreator(CompanyRepresentative creator, FilterSettings filterSettings);
    
    /**
     * Retrieves internships created by a company representative (overloaded without filters).
     * 
     * @param creator The company representative
     * @return List of internships created by the representative
     */
    List<Internship> getInternshipsByCreator(CompanyRepresentative creator);
    
    /**
     * Retrieves all approved internships created by a company representative.
     * 
     * @param creator The company representative
     * @return List of approved internships
     */
    List<Internship> getApprovedInternshipsByCreator(CompanyRepresentative creator);
    
    /**
     * Retrieves all internships pending staff approval.
     * 
     * @return List of internships with PENDING status
     */
    List<Internship> getPendingInternships();
}

