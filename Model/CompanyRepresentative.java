package Assignment;

public class CompanyRepresentative extends User {
    private String companyName;
    private String department;
    private String position;
    private boolean isApproved; // Declared here

    // Constructor - adjusted based on your UserFactory and the CSV parsing
    public CompanyRepresentative(String userID, String name, String password,String email,
                                 String companyName, String department, String position, String statusFromCsv) {
        super(userID, name, password,email); // Assuming User constructor only takes ID, Name, Password
        this.companyName = companyName;
        this.department = department;
        this.position = position;
        // The 'isApproved' flag is derived from the 'statusFromCsv'
        this.isApproved = "Approved".equalsIgnoreCase(statusFromCsv);
        // We don't need to store the 'statusFromCsv' string if 'isApproved' is the functional flag
    }

    // --- Getters ---
    public String getCompanyName() {
        return companyName;
    }

    public String getDepartment() {
        return department;
    }

    public String getPosition() {
        return position;
    }

    // Getter for approval status
    public boolean isApproved() {
        return isApproved;
    }

    // You might also want a getter for the descriptive status
    public String getApprovalStatusDescription() {
        return isApproved ? "Approved" : "Pending Approval"; // Or "Rejected" if you handle it
    }


    // --- Setters ---
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    // Setter for the approval status (likely used by CareerCenterStaff)
    public void setApproved(boolean approved) {
        isApproved = approved;
    }

    @Override
    public void displayProfile() {
        System.out.println("CompanyRep: " + getUserID() +
                           " | Name: " + getName() +
                           " | Company: " + companyName +
                           " | Dept: " + department +
                           " | Position: " + position +
                           " | Email: " + getEmail() +
                           " | Status: " + getApprovalStatusDescription()); // Use the descriptive getter
    }

    @Override
    public String getUserType() {
        return "CompanyRepresentative";
    }
}