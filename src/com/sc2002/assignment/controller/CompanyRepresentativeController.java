package com.sc2002.assignment.controller;

import com.sc2002.assignment.constant.ApplicationStatus;
import com.sc2002.assignment.constant.InternshipStatus;
import com.sc2002.assignment.model.Application;
import com.sc2002.assignment.model.CompanyRepresentative;
import com.sc2002.assignment.model.Internship;
import com.sc2002.assignment.utils.InternshipCsvHandler;
import com.sc2002.assignment.view.CompanyRepresentativeView;
import com.sc2002.assignment.view.MenuOption;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CompanyRepresentativeController {
    private final CompanyRepresentativeView view;
    private final CompanyRepresentative companyRep;

    private final Map<String, MenuOption> mainMenuOptions = Map.of(
            "l", new MenuOption(
                    "list my internships",
                    this::listMyInternships
            ),
            "c", new MenuOption(
                    "create a new internship",
                    this::createInternship
            ),
            "e", new MenuOption(
                    "edit an internship",
                    this::editInternship
            ),
            "d", new MenuOption(
                    "delete an internship",
                    this::deleteInternship
            ),
            "t", new MenuOption(
                    "toggle internship visibility",
                    this::toggleVisibility
            ),
            "v", new MenuOption(
                    "com.sc2002.assignment.view applications for an internship",
                    this::viewApplications
            ),
            "p", new MenuOption(
                    "confirm student placement",
                    this::confirmPlacement
            ),
            "q", new MenuOption(
                    "log out",
                    this::logout
            )
    );

    public CompanyRepresentativeController(CompanyRepresentative companyRep, CompanyRepresentativeView view) {
        this.companyRep = companyRep;
        this.view = view;
    }

    public void run() {
        do {
            try {
                MenuOption menuSel = view.showMenuDialog(mainMenuOptions);

                if (menuSel != null && menuSel.getOnSelCallback() != null) {
                    menuSel.getOnSelCallback().run();
                } else {
                    System.out.println("Invalid menu option!");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        } while (companyRep.isLoggedIn());
    }

    public void logout() {
        companyRep.logout();
    }

    public void listMyInternships() {
        List<Internship> myInternships = Internship.getAllInternships().stream()
                .filter(i -> i.getCreator().equals(companyRep))
                .collect(Collectors.toList());
        view.showInternships(myInternships);
    }

    public void createInternship() {
        try {
            view.showCreateInternshipDialog();
            Internship internship = view.getInternshipData(companyRep);
            if (internship != null) {
                // Save to CSV
                InternshipCsvHandler.saveToCsv(internship);
                view.showInternshipCreatedMsg(internship);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void viewApplications() {
        int internshipID = view.showSelectInternshipDialog();
        Internship internship = Internship.findWithID(internshipID);
        if (internship == null) {
            System.out.println("Internship not found!");
            return;
        }
        if (!internship.isOwnedBy(companyRep)) {
            System.out.println("You don't have permission to com.sc2002.assignment.view applications for this internship!");
            return;
        }
        view.showApplications(internship.getApplications());
    }
    
    public void editInternship() {
        try {
            int internshipID = view.showSelectInternshipDialog();
            Internship internship = Internship.findWithID(internshipID);
            if (internship == null) {
                System.out.println("Internship not found!");
                return;
            }
            if (!internship.isOwnedBy(companyRep)) {
                System.out.println("You don't have permission to edit this internship!");
                return;
            }
            if (!internship.canEdit()) {
                System.out.println("Cannot edit approved internship!");
                return;
            }
            
            Internship updatedData = view.getInternshipData(companyRep);
            internship.updateDetails(updatedData.getTitle(), updatedData.getDescription(), 
                                    updatedData.getLevel(), updatedData.getPreferredMajor(),
                                    updatedData.getOpenDate(), updatedData.getCloseDate(), 
                                    updatedData.getNumSlots());
            System.out.println("Internship updated successfully!");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    public void deleteInternship() {
        try {
            int internshipID = view.showSelectInternshipDialog();
            Internship internship = Internship.findWithID(internshipID);
            if (internship == null) {
                System.out.println("Internship not found!");
                return;
            }
            if (!internship.isOwnedBy(companyRep)) {
                System.out.println("You don't have permission to delete this internship!");
                return;
            }
            if (!internship.canEdit()) {
                System.out.println("Cannot delete approved internship!");
                return;
            }
            
            internship.delete();
            System.out.println("Internship deleted successfully!");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    public void toggleVisibility() {
        try {
            // First, show all approved internships created by this company rep
            List<Internship> myInternships = Internship.getAllInternships().stream()
                    .filter(i -> i.getCreator().equals(companyRep))
                    .filter(i -> i.getStatus() == InternshipStatus.APPROVED)
                    .collect(Collectors.toList());
            
            if (myInternships.isEmpty()) {
                System.out.println("No approved internships found!");
                return;
            }
            
            view.showInternships(myInternships);
            
            int internshipID = view.showSelectInternshipDialog();
            Internship internship = Internship.findWithID(internshipID);
            if (internship == null) {
                System.out.println("Internship not found!");
                return;
            }
            if (!internship.isOwnedBy(companyRep)) {
                System.out.println("You don't have permission to toggle visibility for this internship!");
                return;
            }
            if (internship.getStatus() != InternshipStatus.APPROVED) {
                System.out.println("Cannot toggle visibility for unapproved internship!");
                return;
            }
            
            internship.toggleVisibility();
            System.out.println("Visibility toggled. Internship is now " + 
                              (internship.isVisible() ? "visible" : "hidden") + " to students.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    public void confirmPlacement() {
        int internshipID = view.showSelectInternshipDialog();
        Internship internship = Internship.findWithID(internshipID);
        if (internship == null) {
            System.out.println("Internship not found!");
            return;
        }
        if (!internship.isOwnedBy(companyRep)) {
            System.out.println("You don't have permission to confirm placement for this internship!");
            return;
        }
        
        int applicationID = view.showSelectApplicationDialog();
        Application app = internship.getApplications().stream()
                .filter(a -> a.getId() == applicationID)
                .findFirst()
                .orElse(null);
        
        if (app == null) {
            System.out.println("Application not found!");
            return;
        }
        
        app.setStatus(ApplicationStatus.SUCCESSFUL);
        System.out.println("Application marked as successful! Student can now accept it.");
    }
}

