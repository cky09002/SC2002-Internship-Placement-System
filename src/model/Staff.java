package model;

import utils.validation.ValidationHelper;

/**
 * Represents a staff member who manages the internship system.
 * Staff can approve/reject company representatives, approve internships,
 * and handle application withdrawal requests.
 * 
 * @author NTU SC2002 Group
 * @version 1.0
 * @since 2025-11-16
 */
public class Staff extends User {
    /** Department or organizational unit of the staff member */
    private String staffDepartment;

    /**
     * Constructs a new Staff member with the specified details.
     * 
     * @param userID unique staff identifier
     * @param name staff member's display name
     * @param password authentication password
     * @param email staff member's email address
     * @param staffDepartment department or unit
     */
    public Staff(String userID, String name, String password, String email, String staffDepartment) {
        super(userID, name, password, email);
        this.staffDepartment = staffDepartment;
    }

    /**
     * Gets the staff member's department.
     * @return the department
     */
    public String getStaffDepartment() {
        return staffDepartment;
    }

    /**
     * Sets the staff member's department.
     * @param staffDepartment the new department (must not be empty)
     * @throws IllegalArgumentException if department is empty or null
     */
    public void setStaffDepartment(String staffDepartment) {
        ValidationHelper.validateNotEmpty(staffDepartment, "Department");
        this.staffDepartment = staffDepartment;
    }

    /**
     * Gets the user type identifier.
     * @return "Staff"
     */
    @Override
    public String getUserType() {
        return "Staff";
    }
    
    /**
     * Gets the CSV filename for persisting staff data.
     * @return the CSV file path for staff
     */
    @Override
    public String getCsvFilename() {
        return "sample_file/sample_staff_list.csv";
    }
    
    /**
     * Creates the dashboard strategy for staff users.
     * Uses factory method pattern to eliminate instanceof checks.
     * 
     * @return StaffDashboardStrategy for this staff member
     */
    @Override
    public view.interfaces.DashboardStrategy createDashboardStrategy() {
        return new view.StaffDashboardStrategy(this);
    }
}