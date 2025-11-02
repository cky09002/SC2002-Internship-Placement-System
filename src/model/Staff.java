package Assignment.src.model;

public class Staff extends User {
    private String staffDepartment;

    public Staff(String userID, String name, String password,String email, String staffDepartment) {
        super(userID, name, password,email);
        this.staffDepartment = staffDepartment;
    }

    public String getStaffDepartment() {
        return staffDepartment;
    }

    @Override
    public void displayProfile() {
        System.out.println("Staff: " + getUserID() + " | " + getName() + " | Dept: " + staffDepartment);
    }

    @Override
    public String getUserType() {
        return "Staff";
    }
}