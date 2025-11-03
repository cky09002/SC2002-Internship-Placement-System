package Assignment.src.controller;

import Assignment.src.model.*;
import Assignment.src.constant.ApplicationStatus;
import Assignment.src.controller.LoginController;
import Assignment.src.utils.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StaffController extends BaseUserController {
    private final Staff staff;
    
    public StaffController(Staff staff) {
        super(staff);
        this.staff = staff;
        // BaseUserController already has loginController, accessible via protected field
    }
    
    public void approveRejectCompanyRep(String repID, boolean approve) {
        // Use LoginController from BaseUserController (MVC compliant)
        loginController.approveRejectCompanyRep(repID, approve);
    }
    
    public void updateInternshipApproval(int internID, boolean approve) {
        internshipController.updateInternshipApproval(internID, approve);
    }
    
    public void approveRejectWithdrawal(int appID, boolean approve) {
        // Use consolidated processWithdrawal method instead of if/else
        applicationController.processWithdrawal(appID, approve);
    }
    
    
    public List<String> getPendingInternships() {
        List<Internship> internships = internshipController.getPendingInternships();
        return toStringList(internships);
    }
    
    public List<String> getPendingCompanyReps() {
        // Use LoginController instead of accessing UserRegistry directly (MVC compliant)
        List<CompanyRepresentative> reps = loginController.getAllCompanyReps().stream()
                .filter(rep -> !rep.isApproved())
                .collect(java.util.stream.Collectors.toList());
        return toStringList(reps);
    }
    
    public List<String> getWithdrawalRequests() {
        List<Application> requests = applicationController.getWithdrawalRequests();
        return requests.stream()
                .sorted((a1, a2) -> Integer.compare(a1.getId(), a2.getId()))
                .map(app -> ApplicationFormatter.formatAsRow(app))
                .collect(Collectors.toList());
    }
    
    /**
     * List all internship opportunities - staff can view all internships with filters
     * Uses FilterSettings to filter by Status, Preferred Major, Level, Closing Date, etc.
     * Returns simple row format for initial display, detailed view available on selection
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
     * Get detailed internship information for viewing
     */
    public String getInternshipDetails(int internshipID) {
        Internship internship = internshipController.findInternship(internshipID);
        // Format with visible status shown (staff can see all fields)
        return InternshipFormatter.formatDetails(internship, "INTERNSHIP DETAILS", true);
    }
    
    /**
     * Edit staff profile fields
     */
    public void editProfile(String name, String email, String department) {
        if (name != null && !name.trim().isEmpty()) {
            staff.setName(name);
        }
        if (email != null && !email.trim().isEmpty()) {
            staff.setEmail(email);
        }
        if (department != null && !department.trim().isEmpty()) {
            staff.setStaffDepartment(department);
        }
        // Note: Profile changes are in-memory only, not persisted to CSV
        // CSV updates would require UserRegistry.saveUserToCsv() method
    }
}
