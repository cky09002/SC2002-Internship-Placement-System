# Test Case Call Sequences

Complete documentation of all 22 test cases with detailed call sequence explanations.

---

## Test Case 1: CSV File Loading

**Purpose:** Verifies CSV loading mechanism initialization

**Call Sequence:**
1. `TestRunner.testCase1_CSVLoading()` is called
2. Calls `UserRegistry.getInstance()` to get singleton instance
3. Singleton pattern ensures only one instance exists
4. If instance is not null, test passes

**Classes Involved:**
- `TestRunner` → `UserRegistry`

---

## Test Case 2: User Creation via Factory

**Purpose:** Tests UserFactory creation of User objects from CSV data

**Call Sequence:**
1. `TestRunner.testCase2_UserCreation()` is called
2. Creates test data array with student information
3. Calls `UserFactory.fromCsv(UserType.STUDENT, studentCols)`
4. `UserFactory` creates new `Student` object using constructor
5. Verifies student attributes: `getYearOfStudy()` and `getMajor()`
6. If attributes match expected values, test passes

**Classes Involved:**
- `TestRunner` → `UserFactory` → `Student` constructor → `Student.getYearOfStudy()`, `Student.getMajor()`

---

## Test Case 3: Validation Helper

**Purpose:** Tests ValidationHelper for email and field validation

**Call Sequence:**
1. `TestRunner.testCase3_Validation()` is called
2. Calls `ValidationHelper.validateEmail("valid@email.com")` - should succeed
3. Calls `ValidationHelper.validateEmail("invalid-email")` - should throw `IllegalArgumentException`
4. Calls `ValidationHelper.validateNotEmpty("test", "TestField")` - should succeed
5. If all validations behave correctly, test passes

**Classes Involved:**
- `TestRunner` → `ValidationHelper.validateEmail()`, `ValidationHelper.validateNotEmpty()`

---

## Test Case 4: UserRegistry Singleton Pattern

**Purpose:** Verifies singleton pattern implementation

**Call Sequence:**
1. `TestRunner.testCase4_UserRegistrySingleton()` is called
2. Calls `UserRegistry.getInstance()` twice, storing in `instance1` and `instance2`
3. Compares references using `==` operator
4. If both references point to same object, singleton pattern works correctly
5. Test passes if instances are identical

**Classes Involved:**
- `TestRunner` → `UserRegistry.getInstance()` (called twice)

---

## Test Case 5: Application Creation

**Purpose:** Tests Application object creation with PENDING status

**Call Sequence:**
1. `TestRunner.testCase5_ApplicationCreation()` is called
2. Creates `Student` object using constructor
3. Creates `CompanyRepresentative` object using constructor
4. Creates `Internship` object using constructor
5. Creates `Application` object: `new Application(internship, student, LocalDateTime.now())`
6. Calls `app.getStatus()` to verify status is `ApplicationStatus.PENDING`
7. If status is PENDING, test passes

**Classes Involved:**
- `TestRunner` → `Student` constructor → `CompanyRepresentative` constructor → `Internship` constructor → `Application` constructor → `Application.getStatus()`

---

## Test Case 6: Internship Creation

**Purpose:** Tests Internship object creation with PENDING status

**Call Sequence:**
1. `TestRunner.testCase6_InternshipCreation()` is called
2. Creates `CompanyRepresentative` object using constructor
3. Creates `Internship` object: `new Internship(...)` with all required parameters
4. Calls `internship.getStatus()` to verify status is `InternshipStatus.PENDING`
5. If status is PENDING, test passes

**Classes Involved:**
- `TestRunner` → `CompanyRepresentative` constructor → `Internship` constructor → `Internship.getStatus()`

---

## Test Case 7: Status Transitions

**Purpose:** Verifies enum values for status types

**Call Sequence:**
1. `TestRunner.testCase7_StatusTransitions()` is called
2. Calls `ApplicationStatus.values()` to get all enum values
3. Checks if length is at least 6 (PENDING, SUCCESSFUL, ACCEPTED, UNSUCCESSFUL, WITHDRAWAL_REQUESTED, WITHDRAWN)
4. Calls `InternshipStatus.values()` to get all enum values
5. Checks if length is at least 4 (PENDING, APPROVED, REJECTED, FILLED)
6. If both enums have required values, test passes

**Classes Involved:**
- `TestRunner` → `ApplicationStatus.values()`, `InternshipStatus.values()`

---

## Test Case 8: Filter Settings

**Purpose:** Tests FilterSettings functionality and active filter tracking

**Call Sequence:**
1. `TestRunner.testCase8_FilterSettings()` is called
2. Creates new `FilterSettings` object
3. Calls `settings.hasActiveFilters()` - should return false initially
4. Calls `settings.setMajorFilter("CSC")` to set major filter
5. Calls `settings.setLevelFilter("Basic")` to set level filter
6. Calls `settings.hasActiveFilters()` again - should return true
7. If filter tracking works correctly, test passes

**Classes Involved:**
- `TestRunner` → `FilterSettings` constructor → `FilterSettings.hasActiveFilters()` → `FilterSettings.setMajorFilter()` → `FilterSettings.setLevelFilter()`

---

## Test Case 1 (Manual): Valid User Login

**Purpose:** Tests valid login for Student, Staff, and approved CompanyRepresentative

**Call Sequence:**
1. `TestRunner.testCase1_ValidUserLogin()` is called
2. Gets `UserRegistry` instance: `UserRegistry.getInstance()`
3. Creates `LoginController`: `new LoginController(registry)`
4. **For Student:**
   - Creates `Student` object
   - Calls `registry.register(student)` to register student
   - Calls `loginController.authenticate("UTEST001", "password")`
   - `LoginController.authenticate()` calls `userRegistry.findById(userID)`
   - `LoginController.authenticate()` calls `user.verifyPassword(password)`
   - `LoginController.authenticate()` calls `user.setLoggedIn(true)`
   - Verifies `loggedInStudent.isLoggedIn()` returns true
5. **For Staff:** Same sequence as Student
6. **For CompanyRepresentative:** Same sequence, but also checks `isApproved()` before allowing login
7. If all three user types can login successfully, test passes

**Classes Involved:**
- `TestRunner` → `UserRegistry.getInstance()` → `LoginController` constructor → `UserRegistry.register()` → `LoginController.authenticate()` → `UserRegistry.findById()` → `User.verifyPassword()` → `User.setLoggedIn()` → `User.isLoggedIn()`

---

## Test Case 2 (Manual): Invalid ID

**Purpose:** Tests rejection of invalid user ID during login

**Call Sequence:**
1. `TestRunner.testCase2_InvalidID()` is called
2. Gets `UserRegistry` instance
3. Creates `LoginController`
4. Calls `loginController.authenticate("INVALID123", "password")`
5. `LoginController.authenticate()` calls `userRegistry.findById("INVALID123")`
6. `UserRegistry.findById()` returns null (user not found)
7. `LoginController.authenticate()` throws `IllegalArgumentException` with message "Invalid user ID. Please try again."
8. Test catches exception and verifies error message
9. If exception thrown with correct message, test passes

**Classes Involved:**
- `TestRunner` → `UserRegistry.getInstance()` → `LoginController` constructor → `LoginController.authenticate()` → `UserRegistry.findById()` → throws `IllegalArgumentException`

---

## Test Case 3 (Manual): Incorrect Password

**Purpose:** Tests rejection of incorrect password during login

**Call Sequence:**
1. `TestRunner.testCase3_IncorrectPassword()` is called
2. Gets `UserRegistry` instance
3. Creates `Student` and registers it: `registry.register(student)`
4. Creates `LoginController`
5. Calls `loginController.authenticate("UTEST002", "wrongpass")`
6. `LoginController.authenticate()` calls `userRegistry.findById("UTEST002")` - returns student
7. `LoginController.authenticate()` calls `user.verifyPassword("wrongpass")` - returns false
8. `LoginController.authenticate()` throws `IllegalArgumentException` with message "Incorrect password. Please try again."
9. Test catches exception and verifies error message
10. If exception thrown with correct message, test passes

**Classes Involved:**
- `TestRunner` → `UserRegistry.getInstance()` → `Student` constructor → `UserRegistry.register()` → `LoginController` constructor → `LoginController.authenticate()` → `UserRegistry.findById()` → `User.verifyPassword()` → throws `IllegalArgumentException`

---

## Test Case 4 (Manual): Password Change Functionality

**Purpose:** Tests password change functionality and verification

**Call Sequence:**
1. `TestRunner.testCase4_PasswordChange()` is called
2. Gets `UserRegistry` instance
3. Creates `Student` and registers it
4. Creates `LoginController`
5. Calls `loginController.changePassword("UTEST003", "oldpass", "newpass")`
6. `LoginController.changePassword()` calls `userRegistry.findById("UTEST003")` - returns student
7. `LoginController.changePassword()` calls `user.changePassword(oldPassword, newPassword)`
8. `User.changePassword()` verifies old password, then updates to new password
9. `LoginController.changePassword()` calls `userCsvHandler.savePasswordChangeToCsv(user)` to persist
10. Test attempts login with old password - should fail
11. Test attempts login with new password - should succeed
12. If old password rejected and new password accepted, test passes

**Classes Involved:**
- `TestRunner` → `UserRegistry.getInstance()` → `Student` constructor → `UserRegistry.register()` → `LoginController` constructor → `LoginController.changePassword()` → `UserRegistry.findById()` → `User.changePassword()` → `UserCsvHandler.savePasswordChangeToCsv()` → `LoginController.authenticate()` (twice: old and new password)

---

## Test Case 5 (Manual): Company Representative Registration & Approval

**Purpose:** Tests company rep registration, pending status, and approval workflow

**Call Sequence:**
1. `TestRunner.testCase5_CompanyRepApproval()` is called
2. Gets `UserRegistry` instance
3. Creates `LoginController`
4. Creates `CompanyRepresentative` with status "Pending"
5. Calls `registry.register(rep)` to register company rep
6. Attempts login: `loginController.authenticate("REP002", "password")`
7. `LoginController.authenticate()` calls `userRegistry.findById("REP002")` - returns company rep
8. `LoginController.authenticate()` calls `user.verifyPassword("password")` - succeeds
9. `LoginController.authenticate()` checks if user is `CompanyRepresentative` and not approved
10. Throws `IllegalArgumentException` with message "Your account is pending approval..."
11. Test catches exception and verifies message
12. Calls `rep.setApprovalStatus(StaffApprovalStatus.APPROVED)` to approve
13. Attempts login again: `loginController.authenticate("REP002", "password")`
14. This time login succeeds because `isApproved()` returns true
15. If pending rep cannot login but approved rep can login, test passes

**Classes Involved:**
- `TestRunner` → `UserRegistry.getInstance()` → `LoginController` constructor → `CompanyRepresentative` constructor → `UserRegistry.register()` → `LoginController.authenticate()` → `UserRegistry.findById()` → `User.verifyPassword()` → `CompanyRepresentative.isApproved()` → throws exception → `CompanyRepresentative.setApprovalStatus()` → `LoginController.authenticate()` (second attempt) → succeeds

---

## Test Case 10 (Manual): Single Placement Acceptance per Student

**Purpose:** Tests that accepting one placement automatically withdraws other applications

**Call Sequence:**
1. `TestRunner.testCase10_SinglePlacementAcceptance()` is called
2. Creates `Student` object
3. Creates `CompanyRepresentative` object
4. Creates two `Internship` objects: `internship1` and `internship2`
5. Student submits applications:
   - Calls `student.submitApplication(internship1)` → creates `Application` with PENDING status
   - Calls `student.submitApplication(internship2)` → creates `Application` with PENDING status
6. Verifies `student.getApplications().size()` equals 2
7. Sets `app1.setStatus(ApplicationStatus.SUCCESSFUL)`
8. Calls `student.acceptApplication(app1)`
9. `Student.acceptApplication()` performs:
   - Sets `app1.setStatus(ApplicationStatus.ACCEPTED)`
   - Calls `applications.stream().filter(a -> a != app).forEach(a -> a.setStatus(ApplicationStatus.WITHDRAWN))` to withdraw others
   - Calls `app.getInternship().confirmPlacement()` to update filled slots
10. Verifies `app2.getStatus()` is `WITHDRAWN` and `app1.getStatus()` is `ACCEPTED`
11. If other applications are withdrawn when one is accepted, test passes

**Classes Involved:**
- `TestRunner` → `Student` constructor → `CompanyRepresentative` constructor → `Internship` constructor (twice) → `Student.submitApplication()` (twice) → `Application` constructor → `Student.getApplications()` → `Application.setStatus()` → `Student.acceptApplication()` → `Application.setStatus()` → `Internship.confirmPlacement()` → `Internship.updateFilledSlots()`

---

## Test Case 13 (Manual): Company Representative Internship Opportunity Creation

**Purpose:** Tests internship creation by company rep with PENDING status

**Call Sequence:**
1. `TestRunner.testCase13_InternshipCreationByRep()` is called
2. Creates `CompanyRepresentative` with "Approved" status
3. Creates `Internship` object: `new Internship(...)` with all required parameters
4. Calls `internship.getStatus()` to verify status is `InternshipStatus.PENDING`
5. Checks `CompanyRepresentative.MAX_INTERNSHIPS` constant exists
6. If internship created with PENDING status and constant exists, test passes

**Classes Involved:**
- `TestRunner` → `CompanyRepresentative` constructor → `Internship` constructor → `Internship.getStatus()` → `CompanyRepresentative.MAX_INTERNSHIPS`

---

## Test Case 14 (Manual): Internship Opportunity Approval Status

**Purpose:** Tests internship status transitions (PENDING → APPROVED → REJECTED)

**Call Sequence:**
1. `TestRunner.testCase14_InternshipApprovalStatus()` is called
2. Creates `CompanyRepresentative` object
3. Creates `Internship` object
4. Calls `internship.getStatus()` - should return `PENDING`
5. Calls `internship.setStatus(InternshipStatus.APPROVED)`
6. `Internship.setStatus()` updates status and sets `visible = true` if APPROVED
7. Calls `internship.getStatus()` - should return `APPROVED`
8. Calls `internship.setStatus(InternshipStatus.REJECTED)`
9. `Internship.setStatus()` updates status and sets `visible = false` if REJECTED
10. Calls `internship.getStatus()` - should return `REJECTED`
11. If all status transitions work correctly, test passes

**Classes Involved:**
- `TestRunner` → `CompanyRepresentative` constructor → `Internship` constructor → `Internship.getStatus()` → `Internship.setStatus()` (twice: APPROVED, then REJECTED) → `Internship.getStatus()` (multiple times)

---

## Test Case 16 (Manual): Restriction on Editing Approved Opportunities

**Purpose:** Tests that internships can be edited when PENDING but not when APPROVED

**Call Sequence:**
1. `TestRunner.testCase16_EditRestriction()` is called
2. Creates `CompanyRepresentative` object
3. Creates `Internship` object with PENDING status
4. Calls `internship.getStatus()` - verifies PENDING
5. Calls `internship.updateDetails(...)` with new values
6. `Internship.updateDetails()` checks `canEdit()` which returns true for PENDING
7. Updates all internship fields (title, description, level, etc.)
8. Calls `internship.getTitle()` - should return new title
9. Calls `internship.setStatus(InternshipStatus.APPROVED)` to change status
10. `Internship.setStatus()` updates status and visibility
11. If editing works when PENDING and status can be changed to APPROVED, test passes
12. Note: Edit restriction for APPROVED internships is enforced in controller layer

**Classes Involved:**
- `TestRunner` → `CompanyRepresentative` constructor → `Internship` constructor → `Internship.getStatus()` → `Internship.updateDetails()` → `Internship.canEdit()` → `Internship.getTitle()` → `Internship.setStatus()`

---

## Test Case 18 (Manual): Student Application Management and Placement Confirmation

**Purpose:** Tests application management and status updates

**Call Sequence:**
1. `TestRunner.testCase18_ApplicationManagement()` is called
2. Creates `Student` object
3. Creates `CompanyRepresentative` object
4. Creates `Internship` object
5. Student submits application: `student.submitApplication(internship)`
6. `Student.submitApplication()` creates new `Application` object with PENDING status
7. Calls `app.setStatus(ApplicationStatus.ACCEPTED)` to update status
8. Calls `app.getStatus()` - should return `ACCEPTED`
9. If application status can be updated correctly, test passes

**Classes Involved:**
- `TestRunner` → `Student` constructor → `CompanyRepresentative` constructor → `Internship` constructor → `Student.submitApplication()` → `Application` constructor → `Application.setStatus()` → `Application.getStatus()`

---

## Test Case 19 (Manual): Internship Placement Confirmation Status Update

**Purpose:** Tests placement confirmation status update to ACCEPTED

**Call Sequence:**
1. `TestRunner.testCase19_PlacementConfirmation()` is called
2. Creates `Student` object
3. Creates `CompanyRepresentative` object
4. Creates `Internship` object
5. Student submits application: `student.submitApplication(internship)`
6. Calls `app.setStatus(ApplicationStatus.ACCEPTED)` to confirm placement
7. Calls `app.getStatus()` - should return `ACCEPTED`
8. If status persists as ACCEPTED, test passes

**Classes Involved:**
- `TestRunner` → `Student` constructor → `CompanyRepresentative` constructor → `Internship` constructor → `Student.submitApplication()` → `Application` constructor → `Application.setStatus()` → `Application.getStatus()`

---

## Test Case 20 (Manual): Create, Edit, and Delete Internship Opportunity Listings

**Purpose:** Tests Create, Read, Update, Delete operations for internships

**Call Sequence:**
1. `TestRunner.testCase20_CRUDOperations()` is called
2. Creates `CompanyRepresentative` object
3. Creates `Internship` object - **CREATE** operation
4. Verifies internship is not null
5. Calls `internship.getStatus()` - should return PENDING
6. Calls `internship.updateDetails(...)` with new values - **UPDATE** operation
7. `Internship.updateDetails()` validates status is PENDING (canEdit returns true)
8. Updates all fields
9. Calls `internship.getTitle()` - should return updated title
10. If creation and editing work correctly, test passes
11. Note: Delete operation is handled in controller layer

**Classes Involved:**
- `TestRunner` → `CompanyRepresentative` constructor → `Internship` constructor → `Internship.getStatus()` → `Internship.updateDetails()` → `Internship.canEdit()` → `Internship.getTitle()`

---

## Test Case 21 (Manual): Career Center Staff Internship Opportunity Approval

**Purpose:** Tests staff approval and rejection of internships

**Call Sequence:**
1. `TestRunner.testCase21_StaffApproval()` is called
2. Creates `CompanyRepresentative` object
3. Creates first `Internship` object
4. Calls `internship.setStatus(InternshipStatus.APPROVED)` - **APPROVE** operation
5. `Internship.setStatus()` updates status to APPROVED and sets `visible = true`
6. Calls `internship.getStatus()` - should return `APPROVED`
7. Creates second `Internship` object
8. Calls `internship2.setStatus(InternshipStatus.REJECTED)` - **REJECT** operation
9. `Internship.setStatus()` updates status to REJECTED and sets `visible = false`
10. Calls `internship2.getStatus()` - should return `REJECTED`
11. If both approval and rejection work correctly, test passes

**Classes Involved:**
- `TestRunner` → `CompanyRepresentative` constructor → `Internship` constructor (twice) → `Internship.setStatus()` (twice: APPROVED, REJECTED) → `Internship.getStatus()` (twice)

---

## Test Case 22 (Manual): Toggle Internship Opportunity Visibility

**Purpose:** Tests internship visibility toggle functionality

**Call Sequence:**
1. `TestRunner.testCase22_VisibilityToggle()` is called
2. Creates `CompanyRepresentative` object
3. Creates `Internship` object with PENDING status
4. Calls `internship.getVisibleFlag()` - should return false (PENDING internships not visible)
5. Calls `internship.setStatus(InternshipStatus.APPROVED)`
6. `Internship.setStatus()` sets `visible = true` when status is APPROVED
7. Calls `internship.getVisibleFlag()` - should return true
8. Calls `internship.isVisible()` - should return true (checks status and flag)
9. Calls `internship.toggleVisibility()` - **TOGGLE OFF**
10. `Internship.toggleVisibility()` flips the `visible` flag
11. Calls `internship.getVisibleFlag()` - should return false
12. Calls `internship.isVisible()` - should return false
13. Calls `internship.toggleVisibility()` again - **TOGGLE ON**
14. `Internship.toggleVisibility()` flips the `visible` flag back
15. Calls `internship.getVisibleFlag()` - should return true
16. Calls `internship.isVisible()` - should return true
17. If visibility toggle works correctly in both directions, test passes

**Classes Involved:**
- `TestRunner` → `CompanyRepresentative` constructor → `Internship` constructor → `Internship.getVisibleFlag()` → `Internship.setStatus()` → `Internship.getVisibleFlag()` → `Internship.isVisible()` → `Internship.toggleVisibility()` (twice) → `Internship.getVisibleFlag()` (multiple times) → `Internship.isVisible()` (multiple times)

---

## Test Case 23: Staff Withdrawal Approval/Rejection

**Purpose:** Tests staff approval and rejection of student withdrawal requests with slot updates

**Call Sequence:**

### Withdrawal Approval Flow:
1. Student requests withdrawal: `student.withdrawApplication(app, reason)`
2. `Student.withdrawApplication()` calls `app.requestWithdrawal(reason)`
3. `Application.requestWithdrawal()` stores previous status and sets status to `WITHDRAWAL_REQUESTED`
4. Staff views withdrawal requests: `staffController.getWithdrawalRequests()`
5. `StaffController.getWithdrawalRequests()` calls `applicationController.getWithdrawalRequests()`
6. `ApplicationController.getWithdrawalRequests()` filters applications with `WITHDRAWAL_REQUESTED` status
7. Staff approves withdrawal: `staffController.approveRejectWithdrawal(appID, true)`
8. `StaffController.approveRejectWithdrawal()` calls `applicationController.approveWithdrawal(appID)`
9. `ApplicationController.approveWithdrawal()` calls `app.approveWithdrawal()`
10. `Application.approveWithdrawal()`:
    - Sets status to `WITHDRAWN`
    - If previous status was `SUCCESSFUL` or `ACCEPTED`, calls `internship.confirmPlacement()`
    - `Internship.confirmPlacement()` calls `updateFilledSlots()` to recount slots
11. `ApplicationController.approveWithdrawal()` calls `csvHandler.saveToCsv(app)` to persist application
12. `ApplicationController.approveWithdrawal()` calls `internshipWriter.saveInternship(internship)` to persist slot changes
13. If withdrawal approved and slots updated correctly, test passes

### Withdrawal Rejection Flow:
1. Staff rejects withdrawal: `staffController.approveRejectWithdrawal(appID, false)`
2. `StaffController.approveRejectWithdrawal()` calls `applicationController.rejectWithdrawal(appID)`
3. `ApplicationController.rejectWithdrawal()` reads `app.getPreviousStatus()` before restoring
4. `ApplicationController.rejectWithdrawal()` calls `app.rejectWithdrawal()`
5. `Application.rejectWithdrawal()` restores status to previous status (SUCCESSFUL or ACCEPTED)
6. `ApplicationController.rejectWithdrawal()` checks if previous status was SUCCESSFUL or ACCEPTED
7. If yes, calls `internship.confirmPlacement()` to update slots (restored application counts again)
8. `ApplicationController.rejectWithdrawal()` calls `csvHandler.saveToCsv(app)` to persist application
9. `ApplicationController.rejectWithdrawal()` calls `internshipWriter.saveInternship(internship)` to persist slot changes
10. If withdrawal rejected and slots updated correctly, test passes

**Classes Involved:**
- `Student.withdrawApplication()` → `Application.requestWithdrawal()` → `StaffController.getWithdrawalRequests()` → `ApplicationController.getWithdrawalRequests()` → `StaffController.approveRejectWithdrawal()` → `ApplicationController.approveWithdrawal()` / `ApplicationController.rejectWithdrawal()` → `Application.approveWithdrawal()` / `Application.rejectWithdrawal()` → `Internship.confirmPlacement()` → `Internship.updateFilledSlots()` → `ApplicationCsvHandler.saveToCsv()` → `InternshipCsvHandler.saveToCsv()`

---

## Summary

All test cases follow MVC architecture:
- **View Layer:** Handles user input/output (manual tests)
- **Controller Layer:** Processes business logic and coordinates between models
- **Model Layer:** Contains domain objects and business rules
- **CSV Handlers:** Persist data to CSV files

**Key Patterns:**
- Singleton: `UserRegistry.getInstance()`
- Factory: `UserFactory.fromCsv()`
- Dependency Injection: Controllers receive dependencies via constructor
- MVC: Clear separation between View, Controller, and Model

---

**Last Updated:** 2025-11-18

