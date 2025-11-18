package controller;

import model.*;
import constant.*;
import utils.filter.FilterSettings;
import utils.formatter.UserFormatter;
import controller.interfaces.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Base abstract class for user controllers providing common functionality.
 * Implements UserController interface (Interface Segregation Principle).
 * All user controllers share login/logout, filter settings, and common controller instances.
 * 
 * Follows Dependency Inversion Principle by accepting interface dependencies via constructor injection.
 * 
 * @author NTU SC2002 Group
 * @version 1.0
 * @since 2025-11-16
 */
public abstract class BaseUserController implements UserController {
    /** The user associated with this controller */
    protected final User user;
    /** Controller for managing internship operations */
    public final InternshipControllerInterface internshipController;
    /** Controller for managing application operations */
    protected final ApplicationControllerInterface applicationController;
    /** Controller for managing login and authentication operations */
    protected final LoginControllerInterface loginController;
    /** Filter settings for data filtering operations */
    protected final FilterSettings filterSettings;
    
    /**
     * Constructs a BaseUserController with the specified user and dependencies.
     * Follows Dependency Inversion Principle - dependencies are interfaces, not concrete classes.
     * 
     * @param user The user associated with this controller
     * @param internshipController Interface for managing internship operations
     * @param applicationController Interface for managing application operations
     * @param loginController Interface for managing login and authentication operations
     */
    protected BaseUserController(User user, InternshipControllerInterface internshipController, 
                                 ApplicationControllerInterface applicationController, LoginControllerInterface loginController) {
        this.user = user;
        this.internshipController = internshipController;
        this.applicationController = applicationController;
        this.loginController = loginController;
        this.filterSettings = new FilterSettings();
    }
    
    /**
     * Constructs a BaseUserController for the specified user.
     * Uses default controller instances for backward compatibility.
     * 
     * @param user The user associated with this controller
     */
    protected BaseUserController(User user) {
        this(user, 
             new InternshipController(), 
             new ApplicationController(), 
             new LoginController(UserRegistry.getInstance()));
    }
    
    public boolean isLoggedIn() { return user.isLoggedIn(); }
    public void logout() { user.logout(); }
    public FilterSettings getFilterSettings() { return filterSettings; }
    public String getProfile() { return UserFormatter.formatProfile(user); }
    
    /**
     * Converts a stream of objects to strings using toString().
     * 
     * @param <T> The type of objects in the stream
     * @param stream The stream to convert
     * @return List of string representations
     */
    protected <T> List<String> toStringList(Stream<T> stream) {
        return stream.map(Object::toString).collect(Collectors.toList());
    }
    
    /**
     * Converts a list of objects to strings using toString().
     * 
     * @param <T> The type of objects in the list
     * @param list The list to convert
     * @return List of string representations
     */
    protected <T> List<String> toStringList(List<T> list) {
        return toStringList(list.stream());
    }
    
    /**
     * Validates that the company representative owns the specified internship.
     * 
     * @param internshipID The ID of the internship to validate
     * @param companyRep The company representative to check ownership for
     * @param controller The internship controller to use for validation
     * @param action The action being attempted (for error message)
     * @throws IllegalArgumentException if the rep does not own the internship
     */
    protected void validateOwnership(int internshipID, CompanyRepresentative companyRep,
                                    InternshipValidator validator, String action) {
        if (!validator.isInternshipOwnedBy(internshipID, companyRep)) {
            throw new IllegalArgumentException("You don't have permission to " + action + "!");
        }
    }
    
    /**
     * Validates both ownership and editability of an internship.
     * 
     * @param internshipID The ID of the internship to validate
     * @param companyRep The company representative to check ownership for
     * @param controller The internship controller to use for validation
     * @param operation The operation being attempted (for error message)
     * @throws IllegalArgumentException if validation fails
     */
    protected void validateOwnershipAndEditability(int internshipID, CompanyRepresentative companyRep,
                                                   InternshipValidator validator, String operation) {
        validateOwnership(internshipID, companyRep, validator, operation + " this internship");
        if (!validator.canEditInternship(internshipID)) {
            throw new IllegalArgumentException("Cannot " + operation + " approved or filled internship!");
        }
    }
    
    /**
     * Validates that an internship is in APPROVED status.
     * 
     * @param internshipID The ID of the internship to validate
     * @param controller The internship controller to use for validation
     * @throws IllegalArgumentException if the internship is not approved
     */
    protected void validateApprovedStatus(int internshipID, InternshipValidator validator) {
        if (validator.getInternshipStatus(internshipID) != InternshipStatus.APPROVED) {
            throw new IllegalArgumentException("Operation requires internship to be APPROVED!");
        }
    }
    
    /**
     * Gets detailed information about a specific internship.
     * Must be implemented by subclasses as authorization rules vary by user type.
     * 
     * @param internshipID The ID of the internship
     * @return Formatted internship details
     * @throws IllegalArgumentException if user doesn't have permission or internship not found
     */
    public abstract String getInternshipDetails(int internshipID);

}

