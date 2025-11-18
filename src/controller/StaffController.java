package controller;

import model.*;
import utils.formatter.ApplicationFormatter;
import utils.formatter.InternshipFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for staff-specific operations.
 * Handles approval/rejection of company representatives, internships, and withdrawal requests.
 * Provides access to all system internships with filtering capabilities.
 * 
 * @author NTU SC2002 Group
 * @version 1.0
 * @since 2025-11-16
 */
public class StaffController extends BaseUserController {
    /** The staff user associated with this controller */
    private final Staff staff;
    
    /**
     * Constructs a StaffController for the specified staff member.
     * 
     * @param staff The staff user
     */
    public StaffController(Staff staff) {
        super(staff);
        this.staff = staff;
        // BaseUserController already has loginController, accessible via protected field
    }
    
    /**
     * Approves or rejects a company representative account.
     * 
     * @param repID The ID of the company representative
     * @param approve true to approve, false to reject
     */
    public void approveRejectCompanyRep(String repID, boolean approve) {
        // Use LoginController from BaseUserController (MVC compliant)
        loginController.approveRejectCompanyRep(repID, approve);
    }
    
    /**
     * Approves or rejects an internship listing.
     * 
     * @param internID The ID of the internship
     * @param approve true to approve, false to reject
     */
    public void updateInternshipApproval(int internID, boolean approve) {
        internshipController.updateInternshipApproval(internID, approve);
    }
    
    /**
     * Approves or rejects student withdrawal request.
     * 
     * @param appID The ID of the application
     * @param approve true to approve withdrawal, false to reject
     */
    public void approveRejectWithdrawal(int appID, boolean approve) {
        if (approve) applicationController.approveWithdrawal(appID);
        else applicationController.rejectWithdrawal(appID);
    }
    
    
    /**
     * Gets all pending internships awaiting approval.
     * 
     * @return List of formatted internship strings
     */
    public List<String> getPendingInternships() {
        List<Internship> internships = internshipController.getPendingInternships();
        return toStringList(internships);
    }
    
    /**
     * Gets pending company representatives awaiting approval.
     * 
     * @return List of formatted company representative strings with PENDING status
     */
    public List<String> getPendingCompanyReps() {
        return loginController.getAllCompanyReps().stream()
                .filter(rep -> rep.getApprovalStatus() == constant.StaffApprovalStatus.PENDING)
                .map(rep -> utils.formatter.UserFormatter.formatProfile(rep))
                .collect(Collectors.toList());
    }
    
    /**
     * Gets all pending withdrawal requests.
     * 
     * @return List of formatted application strings with WITHDRAWAL_REQUESTED status
     */
    public List<String> getWithdrawalRequests() {
        List<Application> requests = applicationController.getWithdrawalRequests();
        return requests.stream()
                .sorted((a1, a2) -> Integer.compare(a1.getId(), a2.getId()))
                .map(app -> ApplicationFormatter.formatAsRow(app))
                .collect(Collectors.toList());
    }
    
    /**
     * Lists all internship opportunities with applied filters.
     * Staff can view all internships in the system.
     * Uses FilterSettings to filter by Status, Major, Level, Closing Date, etc.
     * 
     * @return List of formatted internship strings
     */
    public List<String> listInternshipOpportunities() {
        // Use filter settings that persist across menu pages
        List<Internship> internships = internshipController.getAllInternships(filterSettings);
        // Format as simple rows for numbered list display
        return internships.stream()
                .map(i -> InternshipFormatter.formatAsRow(i, true))
                .collect(Collectors.toList());
    }
    
    
    /**
     * Gets detailed information about a specific internship.
     * Staff can view all fields including status and visibility.
     * 
     * @param internshipID The ID of the internship to view
     * @return Formatted internship details
     */
    public String getInternshipDetails(int internshipID) {
        Internship internship = internshipController.findInternship(internshipID);
        // Format with visible status shown (staff can see all fields)
        return InternshipFormatter.formatDetails(internship, "INTERNSHIP DETAILS", true);
    }
    
    /**
     * Edits staff profile fields (null values skipped, in-memory only).
     * 
     * @param name New name (null to skip)
     * @param email New email (null to skip)
     * @param department New department (null to skip)
     */
    public void editProfile(String name, String email, String department) {
        if (name != null && !name.trim().isEmpty()) staff.setName(name);
        if (email != null && !email.trim().isEmpty()) staff.setEmail(email);
        if (department != null && !department.trim().isEmpty()) staff.setStaffDepartment(department);
    }
}
