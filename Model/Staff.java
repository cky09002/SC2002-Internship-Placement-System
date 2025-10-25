package Assignment;

public class Staff extends User {
    private String staffDepartment;

    public Staff(String userID, String name, String password,String email, String staffDepartment) {
        super(userID, name, password,email);
        this.staffDepartment = staffDepartment;
    }

    // Getter for staffDepartment
    public String getStaffDepartment() {
        return staffDepartment;
    }

    // Setter for staffDepartment
    public void setStaffDepartment(String staffDepartment) {
        this.staffDepartment = staffDepartment;
    }

    @Override
    public void displayProfile() {
        System.out.println("Staff: " + getUserID() + " | " + getName() + " | Dept: " + staffDepartment);
    }

    @Override
    public String getUserType() {
        return "Staff";
    }

    // Staff class methods
    public void authoriseCompanyRepAcc(CompanyRepresentative rep) {
        rep.setApproved(true);
    }

    public void rejectCompanyRepAcc(CompanyRepresentative rep) {
        rep.setApproved(false);
    }

    public void approveListing(Internship internListing) {
        internListing.updateStatus(InternshipStatus.APPROVED);
    }

    public void rejectListing(Internship internListing) {
        internListing.updateStatus(InternshipStatus.REJECTED);
    }

    public void approveStudentWithdrawal(Application app) {
        app.updateStatus(ApplicationStatus.WITHDRAWN);
    }

    public void rejectStudentWithdrawal(Application app) {
        app.updateStatus(ApplicationStatus.REJECTED);
    }

    public Report generateReport(String criteria) {
        // placeholder: report generation logic will aggregate and filter data
        return new Report(criteria);
    }
}
