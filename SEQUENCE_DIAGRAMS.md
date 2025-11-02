# Sequence Diagrams for Internship Placement System

This document provides sequence diagrams for key functionalities in the Internship Placement System.

---

## 1. Valid User Login

**Actor:** User (Student/Staff/Company Representative)  
**Purpose:** User logs in to access their role-specific dashboard

```
User -> InternshipApp: Run Application
InternshipApp -> InternshipApp: Load users from CSV
InternshipApp -> User: Display Main Menu
User -> InternshipApp: Select Option 1 (Login)
InternshipApp -> LoginController: login()
LoginController -> User: Prompt User ID
User -> LoginController: Enter User ID
LoginController -> UserRegistry: findById(userID)
UserRegistry -> LoginController: Return User object (or null)

alt User not found
    LoginController -> User: Display "Invalid user ID"
else User found
    LoginController -> User: Prompt Password
    User -> LoginController: Enter Password
    LoginController -> User: verifyPassword(password)
    
    alt Password incorrect
        User -> LoginController: Return false
        LoginController -> User: Display "Incorrect password"
    else Password correct
        alt Company Representative not approved
            LoginController -> CompanyRepresentative: isApproved()
            CompanyRepresentative -> LoginController: Return false (Pending)
            LoginController -> User: Display "Account pending approval"
        else User approved / not Company Rep
            User -> LoginController: setLoggedIn(true)
            LoginController -> InternshipApp: Return User
            InternshipApp -> InternshipApp: routeToController(User)
            
            alt User is Student
                InternshipApp -> StudentController: new (Student, StudentView)
                InternshipApp -> StudentController: run()
                StudentController -> StudentView: showMenuDialog()
                StudentView -> User: Display Student Dashboard
            else User is Staff
                InternshipApp -> StaffController: new (Staff, StaffView)
                InternshipApp -> StaffController: run()
                StaffController -> StaffView: displayMenu()
                StaffView -> User: Display Staff Dashboard
            else User is Company Rep
                InternshipApp -> CompanyRepresentativeController: new (CompanyRep, CompanyRepView)
                InternshipApp -> CompanyRepresentativeController: run()
                CompanyRepresentativeController -> CompanyRepresentativeView: showMenuDialog()
                CompanyRepresentativeView -> User: Display Company Rep Dashboard
            end
        end
    end
end
```

---

## 2. Password Change Functionality

**Actor:** User  
**Purpose:** Change password from login page

```
User -> InternshipApp: Select Option 2 (Change Password)
InternshipApp -> LoginController: changePasswordAtLoginPage()
LoginController -> User: Prompt User ID
User -> LoginController: Enter User ID
LoginController -> UserRegistry: findById(userID)
UserRegistry -> LoginController: Return User (or null)

alt User not found
    LoginController -> User: Display "Invalid user ID"
else User found
    LoginController -> User: Prompt Current Password
    User -> LoginController: Enter Current Password
    LoginController -> User: Prompt New Password
    User -> LoginController: Enter New Password
    LoginController -> User: changePassword(oldPassword, newPassword)
    User -> LoginController: Verify and update password
    LoginController -> UserRegistry: savePasswordChangeToCsv(User)
    UserRegistry -> UserRegistry: Read CSV file
    UserRegistry -> UserRegistry: Update password field
    UserRegistry -> UserRegistry: Write back to CSV
    LoginController -> User: Display "Password changed successfully"
end
```

---

## 3. Company Representative Account Creation

**Actor:** Company Representative  
**Purpose:** Register new company representative account

```
User -> InternshipApp: Select Option 3 (Register as Company Rep)
InternshipApp -> LoginController: registerCompanyRepresentative()
LoginController -> User: Prompt Email (User ID)
User -> LoginController: Enter Email
LoginController -> UserRegistry: findById(email)
UserRegistry -> LoginController: Return null (not exists)
LoginController -> User: Prompt Name
User -> LoginController: Enter Name
LoginController -> User: Prompt Company Name
User -> LoginController: Enter Company Name
LoginController -> User: Prompt Department
User -> LoginController: Enter Department
LoginController -> User: Prompt Position
User -> LoginController: Enter Position
LoginController -> User: Prompt Password
User -> LoginController: Enter Password
LoginController -> CompanyRepresentative: new (email, name, password, email, company, dept, position, "Pending")
CompanyRepresentative -> LoginController: Create object with PENDING status
LoginController -> UserRegistry: saveCompanyRepToCsv(CompanyRep)
UserRegistry -> UserRegistry: Append to CSV file
LoginController -> User: Display "Registration successful. Awaiting approval."
```

**Company Representative Approval Flow:**

```
Staff -> StaffController: Select Option 1 (Approve/Reject Company Rep)
StaffController -> StaffController: getPendingCompanyReps()
StaffController -> UserRegistry: getAllCompanyReps()
UserRegistry -> StaffController: Return all Company Reps
StaffController -> StaffController: Filter by PENDING status
StaffController -> StaffView: showCompanyRepList(list)
StaffView -> Staff: Display Company Reps
Staff -> StaffView: Enter Company Rep ID
StaffView -> StaffController: Return Company Rep ID
StaffController -> StaffView: getApprovalDecision()
StaffView -> Staff: Prompt Approve? (y/n)
Staff -> StaffView: Enter y or n
StaffView -> StaffController: Return approval decision
StaffController -> UserRegistry: findById(repID)
UserRegistry -> StaffController: Return Company Rep
StaffController -> CompanyRepresentative: setApproved(boolean)
CompanyRepresentative -> CompanyRepresentative: Update approval status
StaffController -> UserRegistry: updateCompanyRepStatusInCsv(CompanyRep)
UserRegistry -> UserRegistry: Update CSV file
StaffController -> Staff: Display "Company representative approved/rejected"
```

---

## 4. Student Viewing Available Internships

**Actor:** Student  
**Purpose:** View internships visible to student based on profile

```
Student -> StudentController: Select "View Available Internships"
StudentController -> Internship: getAllInternships()
Internship -> StudentController: Return all internships
StudentController -> StudentController: Filter internships
    For each internship:
        StudentController -> Internship: isVisibleToStudent(Student)
        Internship -> Internship: Check isVisible()
        alt Internship not visible or not approved
            Internship -> StudentController: Return false
        else Internship visible
            Internship -> Internship: Check major match
            alt Major mismatch
                Internship -> StudentController: Return false
            else Major matches
                Internship -> Internship: Check level eligibility (year of study)
                alt Level too advanced for year
                    Internship -> StudentController: Return false
                else Level appropriate
                    Internship -> Internship: Check date range (open/close)
                    alt Outside date range
                        Internship -> StudentController: Return false
                    else Within date range
                        Internship -> StudentController: Return true
                    end
                end
            end
        end
    end
end
StudentController -> StudentView: showInternships(filteredList)
StudentView -> Student: Display filtered internships
```

---

## 5. Student Submitting Application

**Actor:** Student  
**Purpose:** Submit application for an internship

```
Student -> StudentController: Select "Submit Application"
StudentController -> StudentView: showApplyDialog()
StudentView -> Student: Display available internships
Student -> StudentView: Enter Internship ID
StudentView -> StudentController: Return Internship ID
StudentController -> Internship: findWithID(internshipID)
Internship -> StudentController: Return Internship

alt Internship not found
    StudentController -> Student: Display "Internship not found"
else Internship found
    StudentController -> Student: submitApplication(internship)
    Student -> Student: Check applications.size() < MAX_APPLICATIONS
    alt Too many applications
        Student -> StudentController: Throw TooManyApplicationsException
        StudentController -> Student: Display error message
    else Within limit
        Student -> Internship: isEligibleForStudent(Student)
        Internship -> Internship: isVisibleToStudent(Student)
        
        alt Not eligible
            Internship -> Student: Return false
            Student -> StudentController: Throw IllegalStateException
            StudentController -> Student: Display error message
        else Eligible
            Student -> Application: new (Internship, Student, LocalDateTime.now())
            Application -> Application: Create with PENDING status
            Student -> Internship: addApplication(app)
            Internship -> Internship: Add to applications list
            Internship -> Internship: updateFilledSlots()
            Student -> Student: applications.add(app)
            Student -> ApplicationCsvHandler: saveToCsv(app)
            ApplicationCsvHandler -> ApplicationCsvHandler: Write to CSV
            Student -> StudentView: showApplyMsg(app)
            StudentView -> Student: Display success message
        end
    end
end
```

---

## 6. Single Internship Placement Acceptance

**Actor:** Student  
**Purpose:** Accept one internship offer, auto-withdraw others

```
Student -> StudentController: Select "Accept Offer"
StudentController -> Student: getApplications()
Student -> StudentController: Return all applications
StudentController -> StudentController: Filter SUCCESSFUL applications
StudentController -> StudentView: viewMyApplications(filteredList)
StudentView -> Student: Display successful applications
Student -> StudentView: Enter Application ID
StudentView -> StudentController: Return Application ID
StudentController -> Application: findApplicationWithID(appID)
Application -> StudentController: Return Application

alt Application not found
    StudentController -> Student: Display "Application not found"
else Application found
    alt Status not SUCCESSFUL
        StudentController -> Student: Display error
    else Status is SUCCESSFUL
        StudentController -> Student: acceptApplication(app)
        Student -> Application: setStatus(CONFIRMED)
        Application -> ApplicationCsvHandler: saveToCsv(app)
        ApplicationCsvHandler -> ApplicationCsvHandler: Update CSV
        Student -> Student: applications.stream() - filter other apps
        Student -> Student: For each other app: setStatus(WITHDRAWN)
        For each app:
            Application -> Application: setStatus(WITHDRAWN)
            Application -> ApplicationCsvHandler: saveToCsv(app)
        end
        Student -> Internship: confirmPlacement()
        Internship -> Internship: updateFilledSlots()
        Internship -> Internship: Count CONFIRMED applications
        Internship -> Internship: Update filledSlots
        Internship -> InternshipCsvHandler: saveToCsv(internship)
        InternshipCsvHandler -> InternshipCsvHandler: Update CSV
        alt filledSlots >= numSlots
            Internship -> Internship: setStatus(FILLED)
            Internship -> InternshipCsvHandler: saveToCsv(internship)
        end
        StudentController -> Student: Display success message
    end
end
```

---

## 7. Company Representative Creating Internship

**Actor:** Company Representative  
**Purpose:** Create new internship opportunity

```
CompanyRep -> CompanyRepresentativeController: Select "Create Internship"
CompanyRepresentativeController -> CompanyRepresentativeView: showCreateInternshipDialog()
CompanyRepresentativeView -> CompanyRep: Display form
CompanyRep -> CompanyRepresentativeView: Enter Title, Description, Level, Major, Dates, Slots
CompanyRepresentativeView -> CompanyRepresentativeController: Return Internship data
CompanyRepresentativeController -> CompanyRepresentativeView: getInternshipData(companyRep)
CompanyRepresentativeView -> Internship: new (title, description, level, major, openDate, closeDate, company, creator, slots)
Internship -> Internship: Create with PENDING status, visible=false
Internship -> Internship: Add to internships list
CompanyRepresentativeController -> InternshipCsvHandler: saveToCsv(internship)
InternshipCsvHandler -> InternshipCsvHandler: Write to CSV
CompanyRepresentativeController -> CompanyRepresentativeView: showInternshipCreatedMsg(internship)
CompanyRepresentativeView -> CompanyRep: Display "Internship created, pending approval"
```

---

## 8. Staff Approving/Rejecting Internship

**Actor:** Staff  
**Purpose:** Review and approve/reject internships

```
Staff -> StaffController: Select Option 2 (Approve/Reject Internship)
StaffController -> StaffController: getPendingInternships()
StaffController -> Internship: getAllInternships()
Internship -> StaffController: Return all internships
StaffController -> StaffController: Filter by PENDING status
StaffController -> StaffView: displayInternshipList(list)
StaffView -> Staff: Display pending internships
Staff -> StaffView: Enter Internship ID
StaffView -> StaffController: Return Internship ID
StaffController -> Internship: findWithID(internshipID)
Internship -> StaffController: Return Internship

alt Internship not found
    StaffController -> Staff: Display error
else Internship found
    StaffController -> StaffView: getApprovalDecision()
    StaffView -> Staff: Prompt Approve? (y/n)
    Staff -> StaffView: Enter y or n
    StaffView -> StaffController: Return decision
    StaffController -> Internship: setStatus(APPROVED or REJECTED)
    Internship -> Internship: Update status
    alt Approved
        Internship -> Internship: set visible = true
    else Rejected
        Internship -> Internship: set visible = false
    end
    Internship -> InternshipCsvHandler: saveToCsv(internship)
    InternshipCsvHandler -> InternshipCsvHandler: Update CSV
    StaffController -> Staff: Display success message
end
```

---

## 9. Company Representative Toggling Visibility

**Actor:** Company Representative  
**Purpose:** Toggle internship visibility for students

```
CompanyRep -> CompanyRepresentativeController: Select "Toggle Visibility"
CompanyRepresentativeController -> Internship: getAllInternships()
Internship -> CompanyRepresentativeController: Return all internships
CompanyRepresentativeController -> CompanyRepresentativeController: Filter by creator = companyRep AND status = APPROVED
CompanyRepresentativeController -> CompanyRepresentativeView: showInternships(approvedInternships)
CompanyRepresentativeView -> CompanyRep: Display approved internships
CompanyRep -> CompanyRepresentativeView: Enter Internship ID
CompanyRepresentativeView -> CompanyRepresentativeController: Return Internship ID
CompanyRepresentativeController -> Internship: findWithID(internshipID)
Internship -> CompanyRepresentativeController: Return Internship

alt Internship not found
    CompanyRepresentativeController -> CompanyRep: Display error
else Internship found
    CompanyRepresentativeController -> Internship: isOwnedBy(companyRep)
    Internship -> CompanyRepresentativeController: Return boolean
    
    alt Not owned
        CompanyRepresentativeController -> CompanyRep: Display permission error
    else Owned
        CompanyRepresentativeController -> Internship: getStatus()
        Internship -> CompanyRepresentativeController: Return status
        
        alt Status not APPROVED
            CompanyRepresentativeController -> CompanyRep: Display error
        else Status is APPROVED
            CompanyRepresentativeController -> Internship: toggleVisibility()
            Internship -> Internship: visible = !visible
            Internship -> InternshipCsvHandler: saveToCsv(internship)
            InternshipCsvHandler -> InternshipCsvHandler: Update CSV
            CompanyRepresentativeController -> CompanyRep: Display success message
        end
    end
end
```

---

## 10. Student Requesting Withdrawal

**Actor:** Student  
**Purpose:** Request withdrawal from internship application

```
Student -> StudentController: Select "Request Withdrawal"
StudentController -> StudentView: showWithdrawDialog()
StudentView -> Student: Display applications
Student -> StudentView: Enter Application ID
StudentView -> StudentController: Return Application ID
StudentController -> Application: findApplicationWithID(appID)
Application -> StudentController: Return Application

alt Application not found
    StudentController -> Student: Display error
else Application found
    StudentController -> Student: withdrawApplication(app)
    Student -> Application: requestWithdrawal()
    Application -> Application: Check status is PENDING, SUCCESSFUL, or CONFIRMED
    alt Invalid status
        Application -> Student: Throw IllegalStateException
    else Valid status
        Application -> Application: Store previousStatus
        Application -> Application: Set status to WITHDRAWAL_REQUESTED
        Application -> ApplicationCsvHandler: saveToCsv(app)
        ApplicationCsvHandler -> ApplicationCsvHandler: Update CSV
        StudentController -> Student: Display "Request submitted for staff approval"
    end
end
```

---

## 11. Staff Approving/Rejecting Withdrawal

**Actor:** Staff  
**Purpose:** Process student withdrawal requests

```
Staff -> StaffController: Select Option 3 (Approve/Reject Withdrawal)
StaffController -> Application: getAllApplications()
Application -> StaffController: Return all applications
StaffController -> StaffController: Filter by WITHDRAWAL_REQUESTED status
StaffController -> StaffView: displayApplications(list)
StaffView -> Staff: Display withdrawal requests
Staff -> StaffView: Enter Application ID
StaffView -> StaffController: Return Application ID
StaffController -> Application: findApplicationByID(appID)
Application -> StaffController: Return Application

alt Application not found
    StaffController -> Staff: Display error
else Application found
    StaffController -> StaffView: getApprovalDecision()
    StaffView -> Staff: Prompt Approve? (y/n)
    Staff -> StaffView: Enter y or n
    StaffView -> StaffController: Return decision
    
    alt Approve
        StaffController -> Application: approveWithdrawal()
        Application -> Application: Set status to WITHDRAWN
        Application -> Application: Check if previousStatus was CONFIRMED
        alt Previous status was CONFIRMED
            Application -> Internship: confirmPlacement()
            Internship -> Internship: updateFilledSlots()
            Internship -> InternshipCsvHandler: saveToCsv(internship)
        end
        Application -> Application: Clear previousStatus
        Application -> ApplicationCsvHandler: saveToCsv(app)
        ApplicationCsvHandler -> ApplicationCsvHandler: Update CSV
        StaffController -> Staff: Display success message
    else Reject
        StaffController -> Application: rejectWithdrawal()
        Application -> Application: Restore previousStatus
        Application -> Application: Clear previousStatus
        Application -> ApplicationCsvHandler: saveToCsv(app)
        ApplicationCsvHandler -> ApplicationCsvHandler: Update CSV
        StaffController -> Staff: Display success message
    end
end
```

---

## 12. Company Representative Confirming Placement

**Actor:** Company Representative  
**Purpose:** Mark application as successful after review

```
CompanyRep -> CompanyRepresentativeController: Select "Confirm Placement"
CompanyRepresentativeController -> CompanyRepresentativeView: showSelectInternshipDialog()
CompanyRepresentativeView -> CompanyRep: Prompt Internship ID
CompanyRep -> CompanyRepresentativeView: Enter Internship ID
CompanyRepresentativeView -> CompanyRepresentativeController: Return Internship ID
CompanyRepresentativeController -> Internship: findWithID(internshipID)
Internship -> CompanyRepresentativeController: Return Internship

alt Internship not found
    CompanyRepresentativeController -> CompanyRep: Display error
else Internship found
    CompanyRepresentativeController -> Internship: isOwnedBy(companyRep)
    Internship -> CompanyRepresentativeController: Return boolean
    
    alt Not owned
        CompanyRepresentativeController -> CompanyRep: Display permission error
    else Owned
        CompanyRepresentativeController -> CompanyRepresentativeView: showSelectApplicationDialog()
        CompanyRepresentativeView -> CompanyRep: Prompt Application ID
        CompanyRep -> CompanyRepresentativeView: Enter Application ID
        CompanyRepresentativeView -> CompanyRepresentativeController: Return Application ID
        CompanyRepresentativeController -> Internship: getApplications()
        Internship -> CompanyRepresentativeController: Return applications
        CompanyRepresentativeController -> CompanyRepresentativeController: Filter by applicationID
        
        alt Application not found
            CompanyRepresentativeController -> CompanyRep: Display error
        else Application found
            CompanyRepresentativeController -> Application: setStatus(SUCCESSFUL)
            Application -> Application: Update status
            Application -> ApplicationCsvHandler: saveToCsv(app)
            ApplicationCsvHandler -> ApplicationCsvHandler: Update CSV
            CompanyRepresentativeController -> CompanyRep: Display "Application marked as successful"
        end
    end
end
```

---

## Summary of Key Design Patterns Used

1. **Singleton Pattern:** `UserRegistry` ensures a single instance for centralized user management
2. **Factory Pattern:** `UserFactory` creates appropriate User subclasses from CSV data
3. **MVC Architecture:** Clear separation of Model (data/business logic), View (display), and Controller (coordination)
4. **Strategy Pattern:** Different controllers for different user roles handle role-specific logic
5. **Observer-like Pattern:** Status changes trigger CSV persistence automatically
6. **Template Method:** CSV handlers follow similar patterns for reading/writing

---

## Data Persistence Flow

All data changes are persisted to CSV files:

1. **User Data:** Loaded from CSV on startup, password/status changes written to CSV
2. **Internship Data:** Loaded from CSV on startup, all modifications written to CSV
3. **Application Data:** Loaded from CSV on startup, all status changes written to CSV

CSV files maintain data across application restarts, ensuring persistent state management.

