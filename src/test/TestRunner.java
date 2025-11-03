package Assignment.src.test;

import Assignment.src.model.*;
import Assignment.src.controller.*;
import Assignment.src.utils.*;
import Assignment.src.constant.*;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Comprehensive test runner for all 22 test cases.
 * Executes automated tests and provides manual testing guidance.
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
        
        // Verify CSV files exist
        if (!verifyCsvFiles()) {
            System.out.println("❌ CSV files not found. Please ensure sample_file/ directory contains all CSV files.");
            return;
        }
        
        System.out.println("✅ CSV files verified\n");
        
        // Run automated tests
        runAutomatedTests();
        
        // Provide manual test instructions
        provideManualTestInstructions();
        
        // Print summary
        printSummary();
    }
    
    /**
     * Verify that all required CSV files exist
     */
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
    
    /**
     * Run automated tests that can be executed programmatically
     */
    private static void runAutomatedTests() {
        System.out.println("\n═══════════════════════════════════════════════════════════════════════════════");
        System.out.println("                    AUTOMATED TESTS");
        System.out.println("═══════════════════════════════════════════════════════════════════════════════\n");
        
        // Test Case 1: CSV Loading
        testCase1_CSVLoading();
        
        // Test Case 2: User Creation
        testCase2_UserCreation();
        
        // Test Case 3: Validation
        testCase3_Validation();
        
        // Test Case 4: UserRegistry Singleton
        testCase4_UserRegistrySingleton();
        
        // Test Case 5: Application Creation
        testCase5_ApplicationCreation();
        
        // Test Case 6: Internship Creation
        testCase6_InternshipCreation();
        
        // Test Case 7: Status Transitions
        testCase7_StatusTransitions();
        
        // Test Case 8: Filter Settings
        testCase8_FilterSettings();
    }
    
    /**
     * Test Case 1: Verify CSV loading works
     */
    private static void testCase1_CSVLoading() {
        System.out.println("Test Case 1: CSV File Loading");
        try {
            UserRegistry registry = UserRegistry.getInstance();
            int userCount = registry.getAllUsers().size();
            if (userCount > 0) {
                passed("CSV files loaded successfully. Found " + userCount + " users.");
            } else {
                failed("No users loaded from CSV files.");
            }
        } catch (Exception e) {
            failed("Error loading CSV files: " + e.getMessage());
        }
    }
    
    /**
     * Test Case 2: User Creation and Factory
     */
    private static void testCase2_UserCreation() {
        System.out.println("\nTest Case 2: User Creation via Factory");
        try {
            String[] studentCols = {"U9999999Z", "Test Student", "Computer Science", "3", "test@ntu.edu.sg", "password"};
            User student = UserFactory.fromCsv(UserType.STUDENT, studentCols);
            
            if (student != null && student instanceof Student) {
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
    
    /**
     * Test Case 3: Validation Helper
     */
    private static void testCase3_Validation() {
        System.out.println("\nTest Case 3: Validation Helper");
        try {
            // Test email validation
            try {
                ValidationHelper.validateEmail("valid@email.com");
                passed("Email validation accepts valid email.");
            } catch (IllegalArgumentException e) {
                failed("Email validation failed for valid email.");
            }
            
            // Test invalid email
            try {
                ValidationHelper.validateEmail("invalid-email");
                failed("Email validation should reject invalid email.");
            } catch (IllegalArgumentException e) {
                passed("Email validation correctly rejects invalid email.");
            }
            
            // Test not empty validation
            try {
                ValidationHelper.validateNotEmpty("test", "TestField");
                passed("NotEmpty validation works for non-empty string.");
            } catch (IllegalArgumentException e) {
                failed("NotEmpty validation failed for valid input.");
            }
            
        } catch (Exception e) {
            failed("Error in validation tests: " + e.getMessage());
        }
    }
    
    /**
     * Test Case 4: UserRegistry Singleton
     */
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
    
    /**
     * Test Case 5: Application Creation
     */
    private static void testCase5_ApplicationCreation() {
        System.out.println("\nTest Case 5: Application Creation");
        try {
            // Create test student and internship
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
    
    /**
     * Test Case 6: Internship Creation
     */
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
    
    /**
     * Test Case 7: Status Transitions
     */
    private static void testCase7_StatusTransitions() {
        System.out.println("\nTest Case 7: Status Transitions");
        try {
            // Test ApplicationStatus enum
            ApplicationStatus[] appStatuses = ApplicationStatus.values();
            if (appStatuses.length >= 6) { // PENDING, SUCCESSFUL, ACCEPTED, UNSUCCESSFUL, WITHDRAWAL_REQUESTED, WITHDRAWN
                passed("ApplicationStatus enum has all required values.");
            } else {
                failed("ApplicationStatus enum missing values.");
            }
            
            // Test InternshipStatus enum
            InternshipStatus[] internStatuses = InternshipStatus.values();
            if (internStatuses.length >= 4) { // PENDING, APPROVED, REJECTED, FILLED
                passed("InternshipStatus enum has all required values.");
            } else {
                failed("InternshipStatus enum missing values.");
            }
            
        } catch (Exception e) {
            failed("Error testing status transitions: " + e.getMessage());
        }
    }
    
    /**
     * Test Case 8: Filter Settings
     */
    private static void testCase8_FilterSettings() {
        System.out.println("\nTest Case 8: Filter Settings");
        try {
            FilterSettings settings = new FilterSettings();
            
            // Test initial state
            if (!settings.hasActiveFilters()) {
                passed("FilterSettings starts with no active filters.");
            } else {
                failed("FilterSettings has active filters initially.");
            }
            
            // Test setting filters
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
    
    /**
     * Provide instructions for manual test cases
     */
    private static void provideManualTestInstructions() {
        System.out.println("\n═══════════════════════════════════════════════════════════════════════════════");
        System.out.println("                    MANUAL TEST CASES");
        System.out.println("═══════════════════════════════════════════════════════════════════════════════\n");
        System.out.println("The following test cases require manual interaction with the application.\n");
        System.out.println("Run the application using: java -cp build Assignment.src.MainApp\n");
        System.out.println("Then follow the instructions below:\n");
        
        String[] manualTests = {
            "Test Case 1: Valid User Login - Login with valid credentials for each user type",
            "Test Case 2: Invalid ID - Try logging in with invalid ID",
            "Test Case 3: Incorrect Password - Try logging in with wrong password",
            "Test Case 4: Password Change - Change password and verify new password works",
            "Test Case 5: Company Rep Registration & Approval - Register new company rep and approve as staff",
            "Test Case 6: Internship Visibility Filtering - Verify students only see eligible internships",
            "Test Case 7: Application Eligibility - Apply for internships and verify eligibility checks",
            "Test Case 8: View Application After Visibility Toggle - Create app, toggle visibility OFF, verify still visible",
            "Test Case 10: Single Placement Acceptance - Accept one placement and verify others withdrawn",
            "Test Case 13: Internship Creation - Create new internship as company rep",
            "Test Case 14: Internship Approval Status - View status updates after staff approval",
            "Test Case 15: Company Rep Detail Access - Verify company rep can always see own internships",
            "Test Case 16: Edit Restriction - Verify cannot edit approved internships",
            "Test Case 18: Application Management - Verify slot counting and placement confirmation",
            "Test Case 19: Placement Status Update - Verify status updates correctly",
            "Test Case 20: CRUD Operations - Create, edit (before approval), and delete internships",
            "Test Case 21: Staff Approval/Rejection - Approve or reject internships as staff",
            "Test Case 22: Visibility Toggle - Toggle visibility and verify student view changes"
        };
        
        for (int i = 0; i < manualTests.length; i++) {
            System.out.println((i + 9) + ". " + manualTests[i]);
        }
        
        System.out.println("\nDetailed test case instructions are available in: src/test/TEST_PLAN.md");
    }
    
    /**
     * Print test summary
     */
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
    
    private static void passed(String message) {
        System.out.println("  ✅ PASS: " + message);
        passedTests++;
    }
    
    private static void failed(String message) {
        System.out.println("  ❌ FAIL: " + message);
        failedTests++;
    }
}

