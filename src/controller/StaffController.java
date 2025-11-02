package Assignment.src.controller;

import Assignment.src.model.*;
import Assignment.src.constant.InternshipStatus;
import Assignment.src.constant.ApplicationStatus;
import Assignment.src.view.StaffView;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class StaffController {
    private final StaffView view;
    private final Staff staff;
    private final Scanner sc;
    
    public StaffController(Staff staff, StaffView view) {
        this.staff = staff;
        this.view = view;
        this.sc = new Scanner(System.in);
    }
    
    public void run() {
        do {
            view.displayMenu();
            int choice = sc.nextInt();
            sc.nextLine();
            
            try {
                switch (choice) {
                    case 1:
                        approveRejectCompanyRep();
                        break;
                    case 2:
                        approveRejectInternship();
                        break;
                    case 3:
                        approveRejectWithdrawal();
                        break;
                    case 4:
                        filterData();
                        break;
                    case 5:
                        generateReport();
                        break;
                    case 0:
                        logout();
                        break;
                    default:
                        System.out.println("Invalid choice!");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        } while (staff.isLoggedIn());
    }
    
    private void approveRejectCompanyRep() {
        List<CompanyRepresentative> pendingReps = getPendingCompanyReps();
        if (pendingReps.isEmpty()) {
            System.out.println("No pending company representatives.");
            return;
        }
        
        view.showCompanyRepList(pendingReps);
        String repID = view.getCompanyRepID();
        boolean approve = view.getApprovalDecision();
        
        User rep = UserRegistry.getInstance().findById(repID);
        if (rep == null || !(rep instanceof CompanyRepresentative)) {
            System.out.println("Company representative not found.");
            return;
        }
        
        CompanyRepresentative compRep = (CompanyRepresentative) rep;
        compRep.setApproved(approve);
        
        // Update CSV file
        UserRegistry.getInstance().updateCompanyRepStatusInCsv(compRep);
        
        System.out.println("Company representative " + (approve ? "approved" : "rejected") + " successfully.");
    }
    
    private void approveRejectInternship() {
        List<Internship> pendingInternships = getPendingInternships();
        if (pendingInternships.isEmpty()) {
            System.out.println("No pending internships.");
            return;
        }
        
        view.displayInternshipList(pendingInternships);
        int internID = view.getInternshipID();
        boolean approve = view.getApprovalDecision();
        
        updateInternshipApproval(internID, approve);
        System.out.println("Internship " + (approve ? "approved" : "rejected") + " successfully.");
    }
    
    private void approveRejectWithdrawal() {
        List<Application> withdrawalRequests = getWithdrawalRequests();
        if (withdrawalRequests.isEmpty()) {
            System.out.println("No withdrawal requests pending.");
            return;
        }
        
        view.displayApplications(withdrawalRequests);
        int appID = view.getApplicationID();
        boolean approve = view.getApprovalDecision();
        
        Application app = findApplicationByID(appID);
        if (app == null) {
            System.out.println("Application not found.");
            return;
        }
        
        if (app.getStatus() != ApplicationStatus.WITHDRAWAL_REQUESTED) {
            System.out.println("Application is not in WITHDRAWAL_REQUESTED status.");
            return;
        }
        
        try {
            if (approve) {
                app.approveWithdrawal();
                System.out.println("Withdrawal approved successfully.");
            } else {
                app.rejectWithdrawal();
                System.out.println("Withdrawal rejected. Application restored to previous status.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    private void filterData() {
        String criteria = view.getFilterCriteria();
        System.out.println("Filtering data with criteria: " + criteria);
        // Implementation depends on requirements
    }
    
    private void generateReport() {
        String criteria = view.getFilterCriteria();
        Report report = new Report(criteria);
        view.displayReport(report);
    }
    
    private void logout() {
        staff.setLoggedIn(false);
    }
    
    public List<Internship> getPendingInternships() {
        return Internship.getAllInternships().stream()
                .filter(i -> i.getStatus() == InternshipStatus.PENDING)
                .collect(Collectors.toList());
    }
    
    public List<CompanyRepresentative> getPendingCompanyReps() {
        return UserRegistry.getInstance().getAllCompanyReps().stream()
                .filter(rep -> !rep.isApproved())
                .collect(Collectors.toList());
    }
    
    public void updateInternshipApproval(int internID, boolean approve) {
        Internship intern = Internship.findWithID(internID);
        if (intern == null) {
            throw new IllegalArgumentException("Internship not found.");
        }
        if (approve) {
            intern.setStatus(InternshipStatus.APPROVED);
        } else {
            intern.setStatus(InternshipStatus.REJECTED);
        }
    }
    
    private List<Application> getWithdrawalRequests() {
        // Get all applications from all internships that are in WITHDRAWAL_REQUESTED status
        List<Application> allApps = new ArrayList<>();
        for (Internship intern : Internship.getAllInternships()) {
            allApps.addAll(intern.getApplications());
        }
        return allApps.stream()
                .filter(app -> app.getStatus() == ApplicationStatus.WITHDRAWAL_REQUESTED)
                .collect(Collectors.toList());
    }
    
    private Application findApplicationByID(int appID) {
        for (Internship intern : Internship.getAllInternships()) {
            for (Application app : intern.getApplications()) {
                if (app.getId() == appID) {
                    return app;
                }
            }
        }
        return null;
    }
}
