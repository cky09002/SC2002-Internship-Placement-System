package Assignment.src.model;

import Assignment.src.utils.ValidationHelper;

public class Staff extends User {
    private String staffDepartment;

    public Staff(String userID, String name, String password, String email, String staffDepartment) {
        super(userID, name, password, email);
        this.staffDepartment = staffDepartment;
    }

    public String getStaffDepartment() {
        return staffDepartment;
    }

    public void setStaffDepartment(String staffDepartment) {
        ValidationHelper.validateNotEmpty(staffDepartment, "Department");
        this.staffDepartment = staffDepartment;
    }

    @Override
    public String getUserType() {
        return "Staff";
    }
}