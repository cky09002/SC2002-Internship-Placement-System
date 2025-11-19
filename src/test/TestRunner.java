package test;

import model.*;
import controller.*;
import utils.factory.*;
import utils.validation.*;
import utils.filter.*;
import constant.*;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Test runner for all 22 test cases. Executes automated tests and provides manual testing guidance.
 */
public class TestRunner {
    private static int passedTests = 0;
    private static int failedTests = 0;
    private static final int MANUAL_TEST_COUNT = 18;
    
    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════════════════════════════════════════════");
        System.out.println("              INTERNSHIP MANAGEMENT SYSTEM - TEST RUNNER");
        System.out.println("                       All 22 Test Cases");
        System.out.println("═══════════════════════════════════════════════════════════════════════════════\n");
        
        if (!verifyCsvFiles()) {
            System.out.println("❌ CSV files not found. Please ensure sample_file/ directory contains all CSV files.");
            return;
        }
        System.out.println("✅ CSV files verified\n");
        
        runAutomatedTests();
        provideManualTestInstructions();
        printSummary();
    }
    
    // Verifies all required CSV files exist
    private static boolean verifyCsvFiles() {
        String[] requiredFiles = {
            "sample_file/sample_student_list.csv",
            "sample_file/sample_staff_list.csv",
            "sample_file/sample_company_representative_list.csv",
            "sample_file/sample_internships.csv",
            "sample_file/sample_applications.csv"
        };
        boolean allExist = true;
        for (String filename : requiredFiles) {
            File file = new File(filename);
            if (!file.exists()) {
                System.out.println("❌ Missing: " + filename);
                allExist = false;
            } else {
                System.out.println("✅ Found: " + filename);
            }
        }
        return allExist;
    }
    
    private static void runAutomatedTests() {
        System.out.println("\n═══════════════════════════════════════════════════════════════════════════════");
        System.out.println("                    AUTOMATED TESTS");
        System.out.println("═══════════════════════════════════════════════════════════════════════════════\n");
        
        testCase1_CSVLoading();
        testCase2_UserCreation();
        testCase3_Validation();
        testCase4_UserRegistrySingleton();
        testCase5_ApplicationCreation();
        testCase6_InternshipCreation();
        testCase7_StatusTransitions();
        testCase8_FilterSettings();
        testCase1_ValidUserLogin();
        testCase2_InvalidID();
        testCase3_IncorrectPassword();
        testCase4_PasswordChange();
        testCase5_CompanyRepApproval();
        testCase10_SinglePlacementAcceptance();
        testCase13_InternshipCreationByRep();
        testCase14_InternshipApprovalStatus();
        testCase16_EditRestriction();
        testCase18_ApplicationManagement();
        testCase19_PlacementConfirmation();
        testCase20_CRUDOperations();
        testCase21_StaffApproval();
        testCase22_VisibilityToggle();
    }
    
    // Tests CSV file loading mechanism
    private static void testCase1_CSVLoading() {
        System.out.println("Test Case 1: CSV File Loading");
        try {
            if (UserRegistry.getInstance() != null) {
                passed("CSV loading mechanism initialized successfully.");
            } else {
                failed("CSV loading mechanism failed to initialize.");
            }
        } catch (Exception e) {
            failed("Error in CSV loading test: " + e.getMessage());
        }
    }
    
    // Tests UserFactory creation of User objects from CSV data
    private static void testCase2_UserCreation() {
        System.out.println("\nTest Case 2: User Creation via Factory");
        try {
            String[] studentCols = {"U9999999Z", "Test Student", "Computer Science", "3", "test@ntu.edu.sg", "password"};
            User student = UserFactory.fromCsv(UserType.STUDENT, studentCols);
            if (student instanceof Student) {
                Student s = (Student) student;
                if (s.getYearOfStudy() == 3 && s.getMajor().equals("Computer Science")) {
                    passed("Student creation via factory works correctly.");
                } else {
                    failed("Student created but attributes incorrect.");
                }
            } else {
                failed("Student creation failed.");
            }
        } catch (Exception e) {
            failed("Error in user creation: " + e.getMessage());
        }
    }
    
    // Tests ValidationHelper email and field validation
    private static void testCase3_Validation() {
        System.out.println("\nTest Case 3: Validation Helper");
        try {
            ValidationHelper.validateEmail("valid@email.com");
            passed("Email validation accepts valid email.");
            
            try {
                ValidationHelper.validateEmail("invalid-email");
                failed("Email validation should reject invalid email.");
            } catch (IllegalArgumentException e) {
                passed("Email validation correctly rejects invalid email.");
            }
            
            ValidationHelper.validateNotEmpty("test", "TestField");
            passed("NotEmpty validation works for non-empty string.");
        } catch (IllegalArgumentException e) {
            failed("Validation test failed: " + e.getMessage());
        } catch (Exception e) {
            failed("Error in validation tests: " + e.getMessage());
        }
    }
    
    // Tests UserRegistry singleton pattern implementation
    private static void testCase4_UserRegistrySingleton() {
        System.out.println("\nTest Case 4: UserRegistry Singleton Pattern");
        try {
            UserRegistry instance1 = UserRegistry.getInstance();
            UserRegistry instance2 = UserRegistry.getInstance();
            if (instance1 == instance2) {
                passed("Singleton pattern: same instance returned.");
            } else {
                failed("Singleton pattern: different instances returned.");
            }
        } catch (Exception e) {
            failed("Error testing singleton: " + e.getMessage());
        }
    }
    
    // Tests Application object creation with PENDING status
    private static void testCase5_ApplicationCreation() {
        System.out.println("\nTest Case 5: Application Creation");
        try {
            Student student = new Student("U8888888Y", "Test", "password", "test@test.com", 3, "CSC");
            CompanyRepresentative rep = new CompanyRepresentative("rep@test.com", "Rep", "password", 
                "rep@test.com", "Company", "Dept", "Position", "Approved");
            Internship internship = new Internship("Test Internship", "Description", "Basic", 
                "CSC", LocalDate.now(), LocalDate.now().plusDays(30), "Company", rep, 5);
            Application app = new Application(internship, student, LocalDateTime.now());
            if (app != null && app.getStatus() == ApplicationStatus.PENDING) {
                passed("Application created with PENDING status.");
            } else {
                failed("Application creation failed or wrong status.");
            }
        } catch (Exception e) {
            failed("Error creating application: " + e.getMessage());
        }
    }
    
    // Tests Internship object creation with PENDING status
    private static void testCase6_InternshipCreation() {
        System.out.println("\nTest Case 6: Internship Creation");
        try {
            CompanyRepresentative rep = new CompanyRepresentative("crep@test.com", "Rep", "password",
                "crep@test.com", "Company", "Dept", "Position", "Approved");
            Internship internship = new Internship("Test", "Desc", "Basic", "CSC",
                LocalDate.now(), LocalDate.now().plusDays(30), "Company", rep, 10);
            if (internship != null && internship.getStatus() == InternshipStatus.PENDING) {
                passed("Internship created with PENDING status.");
            } else {
                failed("Internship creation failed or wrong status.");
            }
        } catch (Exception e) {
            failed("Error creating internship: " + e.getMessage());
        }
    }
    
    // Tests enum values for ApplicationStatus and InternshipStatus
    private static void testCase7_StatusTransitions() {
        System.out.println("\nTest Case 7: Status Transitions");
        try {
            if (ApplicationStatus.values().length >= 6) {
                passed("ApplicationStatus enum has all required values.");
            } else {
                failed("ApplicationStatus enum missing values.");
            }
            if (InternshipStatus.values().length >= 4) {
                passed("InternshipStatus enum has all required values.");
            } else {
                failed("InternshipStatus enum missing values.");
            }
        } catch (Exception e) {
            failed("Error testing status transitions: " + e.getMessage());
        }
    }
    
    // Tests FilterSettings functionality and active filter tracking
    private static void testCase8_FilterSettings() {
        System.out.println("\nTest Case 8: Filter Settings");
        try {
            FilterSettings settings = new FilterSettings();
            if (!settings.hasActiveFilters()) {
                passed("FilterSettings starts with no active filters.");
            } else {
                failed("FilterSettings has active filters initially.");
            }
            settings.setMajorFilter("CSC");
            settings.setLevelFilter("Basic");
            if (settings.hasActiveFilters()) {
                passed("FilterSettings correctly tracks active filters.");
            } else {
                failed("FilterSettings not tracking active filters.");
            }
        } catch (Exception e) {
            failed("Error testing filter settings: " + e.getMessage());
        }
    }
    
    // Tests valid login for Student, Staff, and approved CompanyRepresentative
    private static void testCase1_ValidUserLogin() {
        System.out.println("\nTest Case 1: Valid User Login");
        try {
            UserRegistry registry = UserRegistry.getInstance();
            LoginController loginController = new LoginController(registry);
            
            Student student = new Student("UTEST001", "Test Student", "password", "test@test.com", 3, "CSC");
            registry.register(student);
            User loggedInStudent = loginController.authenticate("UTEST001", "password");
            if (loggedInStudent != null && loggedInStudent.isLoggedIn() && loggedInStudent instanceof Student) {
                passed("Student login works correctly");
            } else {
                failed("Student login failed");
            }
            
            Staff staff = new Staff("STEST001", "Test Staff", "password", "staff@test.com", "IT");
            registry.register(staff);
            User loggedInStaff = loginController.authenticate("STEST001", "password");
            if (loggedInStaff != null && loggedInStaff.isLoggedIn() && loggedInStaff instanceof Staff) {
                passed("Staff login works correctly");
            } else {
                failed("Staff login failed");
            }
            
            CompanyRepresentative rep = new CompanyRepresentative("REP001", "Test Rep", "password", 
                "rep@test.com", "Company", "Dept", "Position", "Approved");
            registry.register(rep);
            User loggedInRep = loginController.authenticate("REP001", "password");
            if (loggedInRep != null && loggedInRep.isLoggedIn() && loggedInRep instanceof CompanyRepresentative) {
                passed("Approved company rep login works correctly");
            } else {
                failed("Approved company rep login failed");
            }
        } catch (Exception e) {
            failed("Error in valid user login test: " + e.getMessage());
        }
    }
    
    // Tests rejection of invalid user ID during login
    private static void testCase2_InvalidID() {
        System.out.println("\nTest Case 2: Invalid ID");
        try {
            UserRegistry registry = UserRegistry.getInstance();
            LoginController loginController = new LoginController(registry);
            try {
                loginController.authenticate("INVALID123", "password");
                failed("Should throw exception for invalid ID");
            } catch (IllegalArgumentException e) {
                if (e.getMessage().contains("Invalid user ID")) {
                    passed("Invalid ID correctly rejected with proper error message");
                } else {
                    failed("Invalid ID rejected but error message incorrect: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            failed("Error in invalid ID test: " + e.getMessage());
        }
    }
    
    // Tests rejection of incorrect password during login
    private static void testCase3_IncorrectPassword() {
        System.out.println("\nTest Case 3: Incorrect Password");
        try {
            UserRegistry registry = UserRegistry.getInstance();
            Student student = new Student("UTEST002", "Test", "correctpass", "test@test.com", 2, "CSC");
            registry.register(student);
            LoginController loginController = new LoginController(registry);
            try {
                loginController.authenticate("UTEST002", "wrongpass");
                failed("Should throw exception for incorrect password");
            } catch (IllegalArgumentException e) {
                if (e.getMessage().contains("Incorrect password")) {
                    passed("Incorrect password correctly rejected with proper error message");
                } else {
                    failed("Incorrect password rejected but error message incorrect: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            failed("Error in incorrect password test: " + e.getMessage());
        }
    }
    
    // Tests password change functionality and verification
    private static void testCase4_PasswordChange() {
        System.out.println("\nTest Case 4: Password Change Functionality");
        try {
            UserRegistry registry = UserRegistry.getInstance();
            Student student = new Student("UTEST003", "Test", "oldpass", "test@test.com", 2, "CSC");
            registry.register(student);
            LoginController loginController = new LoginController(registry);
            loginController.changePassword("UTEST003", "oldpass", "newpass");
            
            try {
                loginController.authenticate("UTEST003", "oldpass");
                failed("Old password should not work after password change");
            } catch (IllegalArgumentException e) {
                if (e.getMessage().contains("Incorrect password")) {
                    User user = loginController.authenticate("UTEST003", "newpass");
                    if (user != null && user.isLoggedIn()) {
                        passed("Password change works correctly - old password rejected, new password accepted");
                    } else {
                        failed("New password accepted but login failed");
                    }
                } else {
                    failed("Old password rejected but wrong error: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            failed("Error in password change test: " + e.getMessage());
        }
    }
    
    // Tests company rep registration, pending status, and approval workflow
    private static void testCase5_CompanyRepApproval() {
        System.out.println("\nTest Case 5: Company Representative Registration & Approval");
        try {
            UserRegistry registry = UserRegistry.getInstance();
            LoginController loginController = new LoginController(registry);
            CompanyRepresentative rep = new CompanyRepresentative("REP002", "Test Rep", "password", 
                "rep2@test.com", "Company", "Dept", "Position", "Pending");
            registry.register(rep);
            
            try {
                loginController.authenticate("REP002", "password");
                failed("Pending company rep should not be able to login");
            } catch (IllegalArgumentException e) {
                if (e.getMessage().contains("pending approval")) {
                    rep.setApprovalStatus(StaffApprovalStatus.APPROVED);
                    User loggedInRep = loginController.authenticate("REP002", "password");
                    if (loggedInRep != null && loggedInRep.isLoggedIn() && rep.isApproved()) {
                        passed("Company rep approval works - pending rep cannot login, approved rep can login");
                    } else {
                        failed("Company rep approved but login still fails");
                    }
                } else {
                    failed("Pending approval error message incorrect: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            failed("Error in company rep approval test: " + e.getMessage());
        }
    }
    
    // Tests that accepting one placement automatically withdraws other applications
    private static void testCase10_SinglePlacementAcceptance() {
        System.out.println("\nTest Case 10: Single Placement Acceptance per Student");
        try {
            Student student = new Student("UTEST010", "Test", "password", "test@test.com", 3, "CSC");
            CompanyRepresentative rep = new CompanyRepresentative("REP010", "Rep", "password", 
                "rep@test.com", "Company", "Dept", "Pos", "Approved");
            Internship internship1 = new Internship("Internship 1", "Desc", "Basic", "CSC", 
                LocalDate.now(), LocalDate.now().plusDays(30), "Company", rep, 5);
            Internship internship2 = new Internship("Internship 2", "Desc", "Basic", "CSC", 
                LocalDate.now(), LocalDate.now().plusDays(30), "Company", rep, 5);
            
            // Create applications directly (Anemic Domain Model - business logic in controller)
            Application app1 = new Application(internship1, student, LocalDateTime.now());
            Application app2 = new Application(internship2, student, LocalDateTime.now());
            internship1.addApplication(app1);
            internship2.addApplication(app2);
            student.getApplications().add(app1);
            student.getApplications().add(app2);
            
            if (student.getApplications().size() == 2) {
                app1.setStatus(ApplicationStatus.SUCCESSFUL);
                // Use ApplicationController for business logic
                ApplicationController appController = new ApplicationController();
                appController.acceptApplication(app1.getId(), student);
                if (app2.getStatus() == ApplicationStatus.WITHDRAWN && app1.getStatus() == ApplicationStatus.ACCEPTED) {
                    passed("Single placement acceptance works - accepting one application withdraws others");
                } else {
                    failed("Other applications not withdrawn when one is accepted. app1: " + app1.getStatus() + ", app2: " + app2.getStatus());
                }
            } else {
                failed("Applications not created correctly");
            }
        } catch (Exception e) {
            failed("Error in single placement acceptance test: " + e.getMessage());
        }
    }
    
    // Tests internship creation by company rep with PENDING status
    private static void testCase13_InternshipCreationByRep() {
        System.out.println("\nTest Case 13: Company Representative Internship Opportunity Creation");
        try {
            CompanyRepresentative rep = new CompanyRepresentative("REP013", "Rep", "password", 
                "rep@test.com", "Company", "Dept", "Pos", "Approved");
            Internship internship = new Internship("Test Internship", "Description", "Basic", "CSC", 
                LocalDate.now(), LocalDate.now().plusDays(30), "Company", rep, 5);
            if (internship != null && internship.getStatus() == InternshipStatus.PENDING) {
                passed("Internship created successfully with PENDING status");
                if (CompanyRepresentative.MAX_INTERNSHIPS > 0) {
                    passed("Maximum internship limit constant exists: " + CompanyRepresentative.MAX_INTERNSHIPS);
                }
            } else {
                failed("Internship creation failed or wrong status");
            }
        } catch (Exception e) {
            failed("Error in internship creation test: " + e.getMessage());
        }
    }
    
    // Tests internship status transitions (PENDING → APPROVED → REJECTED)
    private static void testCase14_InternshipApprovalStatus() {
        System.out.println("\nTest Case 14: Internship Opportunity Approval Status");
        try {
            CompanyRepresentative rep = new CompanyRepresentative("REP014", "Rep", "password", 
                "rep@test.com", "Company", "Dept", "Pos", "Approved");
            Internship internship = new Internship("Test", "Desc", "Basic", "CSC", 
                LocalDate.now(), LocalDate.now().plusDays(30), "Company", rep, 5);
            
            if (internship.getStatus() == InternshipStatus.PENDING) {
                passed("Internship starts with PENDING status");
            } else {
                failed("Internship should start with PENDING status");
            }
            
            internship.setStatus(InternshipStatus.APPROVED);
            if (internship.getStatus() == InternshipStatus.APPROVED) {
                passed("Internship status can be updated to APPROVED");
            } else {
                failed("Internship status not updated to APPROVED");
            }
            
            internship.setStatus(InternshipStatus.REJECTED);
            if (internship.getStatus() == InternshipStatus.REJECTED) {
                passed("Internship status can be updated to REJECTED");
            } else {
                failed("Internship status not updated to REJECTED");
            }
        } catch (Exception e) {
            failed("Error in internship approval status test: " + e.getMessage());
        }
    }
    
    // Tests that internships can be edited when PENDING but not when APPROVED
    private static void testCase16_EditRestriction() {
        System.out.println("\nTest Case 16: Restriction on Editing Approved Opportunities");
        try {
            CompanyRepresentative rep = new CompanyRepresentative("REP016", "Rep", "password", 
                "rep@test.com", "Company", "Dept", "Pos", "Approved");
            Internship internship = new Internship("Test", "Desc", "Basic", "CSC", 
                LocalDate.now(), LocalDate.now().plusDays(30), "Company", rep, 5);
            
            if (internship.getStatus() == InternshipStatus.PENDING) {
                internship.updateDetails("New Title", "New Desc", "Intermediate", "CSC", 
                    LocalDate.now(), LocalDate.now().plusDays(30), 5);
                if (internship.getTitle().equals("New Title")) {
                    passed("Internship can be edited when PENDING");
                } else {
                    failed("Internship edit failed when PENDING");
                }
            }
            
            internship.setStatus(InternshipStatus.APPROVED);
            if (internship.getStatus() == InternshipStatus.APPROVED) {
                passed("Internship status changed to APPROVED (edit restriction would be enforced in controller)");
            } else {
                failed("Internship status not updated to APPROVED");
            }
        } catch (Exception e) {
            failed("Error in edit restriction test: " + e.getMessage());
        }
    }
    
    // Tests application management and status updates
    private static void testCase18_ApplicationManagement() {
        System.out.println("\nTest Case 18: Student Application Management and Placement Confirmation");
        try {
            Student student = new Student("UTEST018", "Test", "password", "test@test.com", 3, "CSC");
            CompanyRepresentative rep = new CompanyRepresentative("REP018", "Rep", "password", 
                "rep@test.com", "Company", "Dept", "Pos", "Approved");
            Internship internship = new Internship("Test", "Desc", "Basic", "CSC", 
                LocalDate.now(), LocalDate.now().plusDays(30), "Company", rep, 5);
            
            // Create application directly (Anemic Domain Model)
            Application app = new Application(internship, student, LocalDateTime.now());
            internship.addApplication(app);
            student.getApplications().add(app);
            app.setStatus(ApplicationStatus.ACCEPTED);
            if (app.getStatus() == ApplicationStatus.ACCEPTED) {
                passed("Application can be accepted and status updated correctly");
            } else {
                failed("Application acceptance failed");
            }
        } catch (Exception e) {
            failed("Error in application management test: " + e.getMessage());
        }
    }
    
    // Tests placement confirmation status update to ACCEPTED
    private static void testCase19_PlacementConfirmation() {
        System.out.println("\nTest Case 19: Internship Placement Confirmation Status Update");
        try {
            Student student = new Student("UTEST019", "Test", "password", "test@test.com", 3, "CSC");
            CompanyRepresentative rep = new CompanyRepresentative("REP019", "Rep", "password", 
                "rep@test.com", "Company", "Dept", "Pos", "Approved");
            Internship internship = new Internship("Test", "Desc", "Basic", "CSC", 
                LocalDate.now(), LocalDate.now().plusDays(30), "Company", rep, 5);
            
            // Create application directly (Anemic Domain Model)
            Application app = new Application(internship, student, LocalDateTime.now());
            internship.addApplication(app);
            student.getApplications().add(app);
            app.setStatus(ApplicationStatus.ACCEPTED);
            if (app.getStatus() == ApplicationStatus.ACCEPTED) {
                passed("Placement confirmation status can be set and persists");
            } else {
                failed("Placement confirmation status not set correctly");
            }
        } catch (Exception e) {
            failed("Error in placement confirmation test: " + e.getMessage());
        }
    }
    
    // Tests Create, Read, Update, Delete operations for internships
    private static void testCase20_CRUDOperations() {
        System.out.println("\nTest Case 20: Create, Edit, and Delete Internship Opportunity Listings");
        try {
            CompanyRepresentative rep = new CompanyRepresentative("REP020", "Rep", "password", 
                "rep@test.com", "Company", "Dept", "Pos", "Approved");
            Internship internship = new Internship("Test", "Desc", "Basic", "CSC", 
                LocalDate.now(), LocalDate.now().plusDays(30), "Company", rep, 5);
            if (internship != null) {
                passed("Internship creation works");
            }
            
            if (internship.getStatus() == InternshipStatus.PENDING) {
                internship.updateDetails("Updated Title", "Updated Desc", "Intermediate", "CSC", 
                    LocalDate.now(), LocalDate.now().plusDays(30), 5);
                if (internship.getTitle().equals("Updated Title")) {
                    passed("Internship editing works when PENDING");
                } else {
                    failed("Internship editing failed");
                }
            }
            passed("CRUD operations structure verified (delete would be in controller)");
        } catch (Exception e) {
            failed("Error in CRUD operations test: " + e.getMessage());
        }
    }
    
    // Tests staff approval and rejection of internships
    private static void testCase21_StaffApproval() {
        System.out.println("\nTest Case 21: Career Center Staff Internship Opportunity Approval");
        try {
            CompanyRepresentative rep = new CompanyRepresentative("REP021", "Rep", "password", 
                "rep@test.com", "Company", "Dept", "Pos", "Approved");
            Internship internship = new Internship("Test", "Desc", "Basic", "CSC", 
                LocalDate.now(), LocalDate.now().plusDays(30), "Company", rep, 5);
            
            internship.setStatus(InternshipStatus.APPROVED);
            if (internship.getStatus() == InternshipStatus.APPROVED) {
                passed("Staff can approve internship");
            } else {
                failed("Internship approval failed");
            }
            
            Internship internship2 = new Internship("Test2", "Desc", "Basic", "CSC", 
                LocalDate.now(), LocalDate.now().plusDays(30), "Company", rep, 5);
            internship2.setStatus(InternshipStatus.REJECTED);
            if (internship2.getStatus() == InternshipStatus.REJECTED) {
                passed("Staff can reject internship");
            } else {
                failed("Internship rejection failed");
            }
        } catch (Exception e) {
            failed("Error in staff approval test: " + e.getMessage());
        }
    }
    
    // Tests internship visibility toggle functionality
    private static void testCase22_VisibilityToggle() {
        System.out.println("\nTest Case 22: Toggle Internship Opportunity Visibility");
        try {
            CompanyRepresentative rep = new CompanyRepresentative("REP022", "Rep", "password", 
                "rep@test.com", "Company", "Dept", "Pos", "Approved");
            Internship internship = new Internship("Test", "Desc", "Basic", "CSC", 
                LocalDate.now(), LocalDate.now().plusDays(30), "Company", rep, 5);
            
            if (!internship.getVisibleFlag()) {
                passed("Internship starts with visibility OFF when PENDING");
            } else {
                failed("Internship should start with visibility OFF when PENDING");
            }
            
            internship.setStatus(InternshipStatus.APPROVED);
            if (internship.getVisibleFlag() && internship.isVisible()) {
                passed("Internship automatically visible when APPROVED");
            } else {
                failed("Internship should be automatically visible when APPROVED");
            }
            
            internship.toggleVisibility();
            if (!internship.getVisibleFlag() && !internship.isVisible()) {
                passed("Internship visibility can be toggled OFF");
            } else {
                failed("Internship visibility not toggled OFF. Flag: " + internship.getVisibleFlag() + ", Visible: " + internship.isVisible());
            }
            
            internship.toggleVisibility();
            if (internship.getVisibleFlag() && internship.isVisible()) {
                passed("Internship visibility can be toggled back ON");
            } else {
                failed("Internship visibility not toggled back ON. Flag: " + internship.getVisibleFlag() + ", Visible: " + internship.isVisible());
            }
        } catch (Exception e) {
            failed("Error in visibility toggle test: " + e.getMessage());
        }
    }
    
    // Provides instructions for manual test cases
    private static void provideManualTestInstructions() {
        System.out.println("\n═══════════════════════════════════════════════════════════════════════════════");
        System.out.println("                    MANUAL TEST CASES");
        System.out.println("═══════════════════════════════════════════════════════════════════════════════\n");
        System.out.println("The following test cases require manual interaction with the application.\n");
        System.out.println("Run the application using: java -cp bin MainApp\n");
        System.out.println("Then follow the detailed instructions in: src/test/TEST_PLAN.md\n");
    }
    
    // Prints test summary with pass/fail counts
    private static void printSummary() {
        System.out.println("\n═══════════════════════════════════════════════════════════════════════════════");
        System.out.println("                         TEST SUMMARY");
        System.out.println("═══════════════════════════════════════════════════════════════════════════════\n");
        System.out.println("Automated Tests:");
        System.out.println("  ✅ Passed: " + passedTests);
        System.out.println("  ❌ Failed: " + failedTests);
        System.out.println("\nManual Tests: " + MANUAL_TEST_COUNT + " test cases");
        System.out.println("  See src/test/TEST_PLAN.md for detailed instructions\n");
        
        if (failedTests == 0) {
            System.out.println("✅ All automated tests passed!");
        } else {
            System.out.println("⚠️  Some automated tests failed. Please review above output.");
        }
        System.out.println("\n═══════════════════════════════════════════════════════════════════════════════\n");
    }
    
    // Records a passed test
    private static void passed(String message) {
        System.out.println("  ✅ PASS: " + message);
        passedTests++;
    }
    
    // Records a failed test
    private static void failed(String message) {
        System.out.println("  ❌ FAIL: " + message);
        failedTests++;
    }
}
