package Assignment.src.view;

import Assignment.src.controller.*;
import Assignment.src.model.*;

/**
 * Factory class for creating views based on user type.
 * MVC Note: This class is in the view package but is called from InternshipApp (application layer).
 * It can access models because it's part of the view creation logic.
 * Controllers are created here to keep view classes clean of model dependencies.
 */
public class ViewFactory {
    
    /**
     * Create and run the appropriate view based on user type
     * @param user The authenticated user (obtained from LoginController)
     */
    public static void createAndRunView(User user) {
        if (user instanceof Student) {
            StudentController controller = new StudentController((Student) user);
            StudentView view = new StudentView(controller);
            view.run();
        } else if (user instanceof Staff) {
            StaffController controller = new StaffController((Staff) user);
            StaffView view = new StaffView(controller);
            view.run();
        } else if (user instanceof CompanyRepresentative) {
            CompanyRepresentativeController controller = 
                new CompanyRepresentativeController((CompanyRepresentative) user);
            CompanyRepresentativeView view = 
                new CompanyRepresentativeView(controller);
            view.run();
        }
    }
}

