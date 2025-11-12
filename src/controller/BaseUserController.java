package Assignment.src.controller;

import Assignment.src.model.*;
import Assignment.src.constant.*;
import Assignment.src.utils.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Base abstract class for user controllers that provides common functionality.
 * All user controllers (StudentController, StaffController, CompanyRepresentativeController)
 * share login/logout functionality and common controller instances.
 * Filter settings persist across menu page navigation.
 */
public abstract class BaseUserController {
    protected final User user;
    public final InternshipController internshipController;
    protected final ApplicationController applicationController;
    protected final LoginController loginController; // For profile operations
    
    // Reusable component: Filter settings persist across menu pages
    protected final FilterSettings filterSettings;
    
    protected BaseUserController(User user) {
        this.user = user;
        this.internshipController = new InternshipController();
        this.applicationController = new ApplicationController();
        this.loginController = new LoginController(UserRegistry.getInstance());
        this.filterSettings = new FilterSettings(); // Persist filter state
    }
    
    /**
     * Check if the user is currently logged in - delegates to model
     * @return true if logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return user.isLoggedIn();
    }
    
    /**
     * Log out the user - delegates to model
     */
    public void logout() {
        user.logout();
    }
    
    /**
     * Utility method to convert a stream of objects to a list of strings using toString()
     * Reduces code duplication across controllers
     */
    protected <T> List<String> toStringList(Stream<T> stream) {
        return stream.map(Object::toString).collect(Collectors.toList());
    }
    
    /**
     * Utility method to convert a list of objects to a list of strings using toString()
     */
    protected <T> List<String> toStringList(List<T> list) {
        return toStringList(list.stream());
    }
    
    /**
     * Get filter settings - reusable component for all controllers
     */
    public FilterSettings getFilterSettings() {
        return filterSettings;
    }
    
    // Reusable validation methods - only for CompanyRepresentativeController
    protected void validateOwnership(int internshipID, CompanyRepresentative companyRep,
                                    InternshipController internshipController, String action) {
        if (!internshipController.isInternshipOwnedBy(internshipID, companyRep)) {
            throw new IllegalArgumentException("You don't have permission to " + action + "!");
        }
    }
    
    protected void validateOwnershipAndEditability(int internshipID, CompanyRepresentative companyRep,
                                                   InternshipController internshipController, String operation) {
        validateOwnership(internshipID, companyRep, internshipController, operation + " this internship");
        if (!internshipController.canEditInternship(internshipID)) {
            throw new IllegalArgumentException("Cannot " + operation + " approved internship!");
        }
    }
    
    protected void validateApprovedStatus(int internshipID, InternshipController internshipController) {
        if (internshipController.getInternshipStatus(internshipID) != InternshipStatus.APPROVED) {
            throw new IllegalArgumentException("Operation requires internship to be APPROVED!");
        }
    }
    
    
    /**
     * Get formatted profile for current user - reusable across all controllers
     */
    public String getProfile() {
        return UserFormatter.formatProfile(user);
    }

    public abstract List<String> listInternships();
    public abstract List<Internship> getInternships();
    public abstract void editProfile(String... fields);


}

