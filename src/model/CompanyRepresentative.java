package model;

import constant.StaffApprovalStatus;
import utils.validation.ValidationHelper;

/**
 * Represents a company representative who can create and manage internship opportunities.
 * Company representatives must be approved by staff before they can post internships.
 * Each representative can create up to MAX_INTERNSHIPS opportunities.
 * 
 * @author NTU SC2002 Group
 * @version 1.0
 * @since 2025-11-16
 */
public class CompanyRepresentative extends User {
    /** Maximum number of internships a company representative can create */
    public static final int MAX_INTERNSHIPS = 5;
    
    /** Name of the company */
    private String companyName;
    /** Department within the company */
    private String department;
    /** Job position/title of the representative */
    private String position;
    /** Current approval status from staff */
    private StaffApprovalStatus approvalStatus;

    /**
     * Constructs a new CompanyRepresentative with the specified details.
     * 
     * @param userID unique company representative identifier
     * @param name representative's display name
     * @param password authentication password
     * @param email representative's email address
     * @param companyName name of the company
     * @param department department within the company
     * @param position job position or title
     * @param statusFromCsv approval status string from CSV ("Approved", "Rejected", or "Pending")
     */
    public CompanyRepresentative(String userID, String name, String password, String email,
                                 String companyName, String department, String position, String statusFromCsv) {
        super(userID, name, password, email);
        this.companyName = companyName;
        this.department = department;
        this.position = position;
        this.approvalStatus = parseStatusFromCsv(statusFromCsv);
    }
    
    /**
     * Parses approval status from CSV string format.
     * Converts to uppercase to match enum names directly.
     * @param status the status string from CSV (case-insensitive)
     * @return the corresponding StaffApprovalStatus enum value
     */
    private StaffApprovalStatus parseStatusFromCsv(String status) {
        if (status == null || status.trim().isEmpty()) {
            return StaffApprovalStatus.PENDING;
        }
        try {
            return StaffApprovalStatus.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return StaffApprovalStatus.PENDING;
        }
    }
    
    /**
     * Gets the company name.
     * @return the company name
     */
    public String getCompanyName() {
        return companyName;
    }

    /**
     * Gets the department.
     * @return the department
     */
    public String getDepartment() {
        return department;
    }

    /**
     * Gets the job position.
     * @return the position
     */
    public String getPosition() {
        return position;
    }

    /**
     * Sets the company name.
     * @param companyName the new company name (must not be empty)
     * @throws IllegalArgumentException if company name is empty or null
     */
    public void setCompanyName(String companyName) {
        ValidationHelper.validateNotEmpty(companyName, "Company name");
        this.companyName = companyName;
    }
    
    /**
     * Sets the department.
     * @param department the new department (must not be empty)
     * @throws IllegalArgumentException if department is empty or null
     */
    public void setDepartment(String department) {
        ValidationHelper.validateNotEmpty(department, "Department");
        this.department = department;
    }
    
    /**
     * Sets the job position.
     * @param position the new position (must not be empty)
     * @throws IllegalArgumentException if position is empty or null
     */
    public void setPosition(String position) {
        ValidationHelper.validateNotEmpty(position, "Position");
        this.position = position;
    }

    /**
     * Checks if the company representative is approved.
     * @return true if approved, false otherwise
     */
    public boolean isApproved() {
        return approvalStatus == StaffApprovalStatus.APPROVED;
    }
    
    /**
     * Gets the current approval status.
     * @return the approval status
     */
    public StaffApprovalStatus getApprovalStatus() {
        return approvalStatus;
    }

    /**
     * Sets the approval status.
     * @param status the new approval status
     */
    public void setApprovalStatus(StaffApprovalStatus status) {
        this.approvalStatus = status;
    }


    /**
     * Gets the user type identifier.
     * @return "CompanyRepresentative"
     */
    @Override
    public String getUserType() {
        return "CompanyRepresentative";
    }
    
    /**
     * Gets the CSV filename for persisting company representative data.
     * @return the CSV file path for company representatives
     */
    @Override
    public String getCsvFilename() {
        return "sample_file/sample_company_representative_list.csv";
    }
    
    /**
     * Creates the dashboard strategy for company representative users.
     * Uses factory method pattern to eliminate instanceof checks.
     * 
     * @return CompanyRepDashboardStrategy for this company representative
     */
    @Override
    public view.interfaces.DashboardStrategy createDashboardStrategy() {
        return new view.CompanyRepDashboardStrategy(this);
    }
}
