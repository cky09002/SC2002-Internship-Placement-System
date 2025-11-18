package view;

import controller.StudentController;
import model.Student;

/**
 * Concrete strategy for student dashboard.
 * Encapsulates student-specific dashboard behavior.
 * 
 * @author NTU SC2002 Group
 * @version 1.0
 * @since 2025-11-18
 */
public class StudentDashboardStrategy implements DashboardStrategy {
    /** Controller managing student-specific operations */
    private final StudentController controller;
    /** View displaying student dashboard */
    private final StudentView view;
    
    /**
     * Constructs a StudentDashboardStrategy for the given student.
     * 
     * @param student The authenticated student user
     */
    public StudentDashboardStrategy(Student student) {
        this.controller = new StudentController(student);
        this.view = new StudentView(controller);
    }
    
    @Override
    public void runDashboard() {
        view.run();
    }
    
    @Override
    public boolean isLoggedIn() {
        return controller.isLoggedIn();
    }
}
