package Assignment.src.model;

import Assignment.src.constant.StaffApprovalStatus;
import Assignment.src.utils.ValidationHelper;

public class CompanyRepresentative extends User {
    public static final int MAX_INTERNSHIPS = 5; // Maximum internships per company representative
    
    private String companyName;
    private String department;
    private String position;
    private StaffApprovalStatus approvalStatus;

    public CompanyRepresentative(String userID, String name, String password, String email,
                                 String companyName, String department, String position, String statusFromCsv) {
        super(userID, name, password, email);
        this.companyName = companyName;
        this.department = department;
        this.position = position;
        this.approvalStatus = parseStatusFromCsv(statusFromCsv);
    }
    
    private StaffApprovalStatus parseStatusFromCsv(String status) {
        if (status == null) return StaffApprovalStatus.PENDING;
        if (status.equalsIgnoreCase("Approved")) return StaffApprovalStatus.APPROVED;
        if (status.equalsIgnoreCase("Rejected")) return StaffApprovalStatus.REJECTED;
        if (status.equalsIgnoreCase("Pending")) return StaffApprovalStatus.PENDING;
        return StaffApprovalStatus.PENDING;
    }
    
    public String getCompanyName() {
        return companyName;
    }

    public String getDepartment() {
        return department;
    }

    public String getPosition() {
        return position;
    }

    public void setCompanyName(String companyName) {
        ValidationHelper.validateNotEmpty(companyName, "Company name");
        this.companyName = companyName;
    }
    
    public void setDepartment(String department) {
        ValidationHelper.validateNotEmpty(department, "Department");
        this.department = department;
    }
    
    public void setPosition(String position) {
        ValidationHelper.validateNotEmpty(position, "Position");
        this.position = position;
    }

    public boolean isApproved() {
        return approvalStatus == StaffApprovalStatus.APPROVED;
    }
    
    public StaffApprovalStatus getApprovalStatus() {
        return approvalStatus;
    }

    public String getApprovalStatusDescription() {
        switch(approvalStatus) {
            case APPROVED: return "Approved";
            case REJECTED: return "Rejected";
            default: return "Pending Approval";
        }
    }
    
    public void setApprovalStatus(StaffApprovalStatus status) {
        this.approvalStatus = status;
    }
    
    // Legacy method for backwards compatibility
    public void setApproved(boolean approved) {
        this.approvalStatus = approved ? StaffApprovalStatus.APPROVED : StaffApprovalStatus.PENDING;
    }
    
    public String getStatusStringForCsv() {
        switch(approvalStatus) {
            case APPROVED: return "Approved";
            case REJECTED: return "Rejected";
            default: return "Pending";
        }
    }


    @Override
    public String getUserType() {
        return "CompanyRepresentative";
    }

    @Override
    public String toString() {
        return String.format("ID: %s | Name: %s | Company: %s | Department: %s | Position: %s | Email: %s | Status: %s",
                getUserID(), getName(), companyName, department, position, getEmail(), getApprovalStatusDescription());
    }
}