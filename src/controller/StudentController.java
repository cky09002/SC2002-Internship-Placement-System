package controller;

import model.Application;
import model.Internship;
import model.Student;
import view.MenuOption;
import view.StudentView;

import java.util.Map;

public class StudentController {
    private final StudentView view;
    private final Student student;

    // preset menu option mappings
    private final Map<String, MenuOption> mainMenuOptions = Map.of(
            "l", new MenuOption(
                    "list available internships",
                    this::listInternships
            ),
            "c", new MenuOption(
                    "create a new application",
                    this::createApplication
            ),
            "w", new MenuOption(
                    "request to withdraw an application",
                    this::withdrawApplication
            ),
            "a", new MenuOption(
                    "accept a successful application",
                    this::acceptApplication
            ),
            "q", new MenuOption(
                    "log out",
                    this::logout
            )
    );

    public StudentController(Student student, StudentView view) {
        this.student = student;
        this.view = view;
    }

    public void run() {
        do {
            // show main menu
            MenuOption menuSel = view.showMenuDialog(mainMenuOptions);

            // choose what to do next using preset main menu options
            menuSel.getOnSelCallback().run();
        } while (student.isLoggedIn());
    }

    public void logout() {
        student.logout();
    }

    public void listInternships() {
        view.showInternships();
    }

    public void createApplication() {
        int internshipID = view.showApplyDialog();
        Internship its = Internship.findWithID(internshipID);
        Application app = student.submitApplication(its);
        view.showApplyMsg(app);
    }

    public void withdrawApplication() {
        int applicationID = view.showWithdrawDialog();
        Application app = student.findApplicationWithID(applicationID);
        student.withdrawApplication(app);
    }

    public void acceptApplication() {
        int applicationID = view.showAcceptDialog();
        Application app = student.findApplicationWithID(applicationID);
        student.acceptApplication(app);
    }
}
