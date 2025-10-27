package Controller;

import Model.*;
import java.util.List;

public class StaffController {
    private List<Internship> internshipList;
    private List<Application> applicationList;
    private List<CompanyRepresentative> companyRepList;

    public CareerCentre(List<Internship> internships, List<Application> applications,
                        List<CompanyRepresentative> companyReps) {
        this.internshipList = internships;
        this.applicationList = applications;
        this.companyRepList = companyReps;
    }

    // Staff controller methods
    public void handleUserInput(String action) {
        // logic to route user action to correct method
        // e.g., switch(action)
    }

    public void updateCompRepApprovalStatus(String repID) {
        for (CompanyRepresentative rep : companyRepList) {
            if (rep.getID().equals(repID)) {
                rep.setApproved(true);
                break;
            }
        }
    }

    public void updateInternshipApproval(String internID) {
        for (Internship intern : internshipList) {
            if (intern.getID().equals(internID)) {
                intern.updateStatus(InternshipStatus.APPROVED);
                break;
            }
        }
    }

    public void updateWithdrawalRequest(String appID) {
        for (Application app : applicationList) {
            if (app.getApplicationID().equals(appID)) {
                app.updateStatus(ApplicationStatus.WITHDRAWN);
                break;
            }
        }
    }

    public Report filterData(String criteria) {
        // placeholder filtering logic
        // use criteria to generate a filtered Report
        return new Report(criteria);
    }

    public Report generateReport(String criteria) {
        // generate a detailed report based on current internship/application data
        Report report = new Report(criteria);
        report.generateSummary();
        return report;
    }
}
