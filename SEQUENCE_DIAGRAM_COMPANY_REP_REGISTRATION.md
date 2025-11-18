# Sequence Diagram - Company Representative Registration Flow

## Overview
Complete flow: Registration → Staff Approval → Login

This document describes the exact call sequence as implemented in the code, step by step.

---

## PHASE 1: REGISTRATION

### Step-by-Step Call Sequence

1. **LoginView.showRegistrationDialog()** is called
   - Displays: "=== Company Representative Registration ==="
   - Prompts user for email (will be user ID)
   - Reads email from Scanner: `sc.nextLine().trim()`
   - Prompts user for name
   - Reads name from Scanner: `sc.nextLine().trim()`
   - Prompts user for password
   - Reads password from Scanner: `sc.nextLine()`
   - Prompts user for company name
   - Reads companyName from Scanner: `sc.nextLine().trim()`
   - Prompts user for department
   - Reads department from Scanner: `sc.nextLine().trim()`
   - Prompts user for position
   - Reads position from Scanner: `sc.nextLine().trim()`

2. **LoginView** calls **controller.registerCompanyRepresentative(email, name, password, companyName, department, position)**

3. **LoginController.registerCompanyRepresentative(email, name, password, companyName, department, position)** is executed:
   - Calls **ValidationHelper.validateEmail(email)**
     - Checks if email is null or empty → throws IllegalArgumentException if invalid
     - Checks if email matches EMAIL_PATTERN regex → throws IllegalArgumentException if invalid
   - Calls **userRegistry.findById(email)**
     - Returns User object if found, null if not found
   - If userRegistry.findById(email) returns non-null:
     - Throws IllegalArgumentException: "An account with this email already exists. Please login instead."
   - Creates new **CompanyRepresentative** object:
     - Constructor: `new CompanyRepresentative(email, name, password, email, companyName, department, position, "Pending")`
     - Inside CompanyRepresentative constructor:
       - Calls `super(userID, name, password, email)` (User constructor)
       - Sets `this.companyName = companyName`
       - Sets `this.department = department`
       - Sets `this.position = position`
       - Calls `parseStatusFromCsv("Pending")` which returns `StaffApprovalStatus.PENDING`
       - Sets `this.approvalStatus = StaffApprovalStatus.PENDING`
   - Calls **userRegistry.register(compRep)**
     - Checks if compRep is null or compRep.getUserID() is null → returns false
     - Checks if users.containsKey(compRep.getUserID()) → returns false if already exists
     - Calls `users.put(compRep.getUserID(), compRep)` to store in memory
     - Returns true
   - If userRegistry.register(compRep) returns true:
     - Calls **userCsvHandler.saveCompanyRepToCsv(compRep)**
       - Opens FileWriter in append mode: `new FileWriter("sample_file/sample_company_representative_list.csv", true)`
       - Calls `compRep.getApprovalStatus().toCsvString()` to get status string
       - Writes CSV line: `compRep.getUserID(), compRep.getName(), compRep.getCompanyName(), compRep.getDepartment(), compRep.getPosition(), compRep.getEmail(), compRep.getPasswordForPersistence(), status`
       - Closes FileWriter
     - Returns true
   - If userRegistry.register(compRep) returns false:
     - Throws IllegalArgumentException: "Registration failed. Please try again."

4. **LoginView** receives return value:
   - If successful (no exception):
     - Displays: "Registration successful!"
     - Displays: "Your account is pending approval from Career Center Staff."
     - Displays: "You will be able to login once your account has been approved."
   - If exception thrown:
     - Catches exception and displays: "Error: " + e.getMessage()

---

## PHASE 2: STAFF APPROVAL

### Step-by-Step Call Sequence

1. **StaffView.handleApproveRejectCompanyRep()** is called
   - Calls **controller.getPendingCompanyReps()**

2. **StaffController.getPendingCompanyReps()** is executed:
   - Calls **loginController.getAllCompanyReps()**
     - **LoginController.getAllCompanyReps()** is executed:
       - Calls **userRegistry.getAllCompanyReps()**
         - Returns `users.values().stream().filter(u -> u instanceof CompanyRepresentative).map(u -> (CompanyRepresentative) u).collect(Collectors.toList())`
         - Returns List<CompanyRepresentative> containing all company representatives
       - Returns List<CompanyRepresentative> to StaffController
   - Filters the list: `.filter(rep -> rep.getApprovalStatus() == StaffApprovalStatus.PENDING)`
   - Maps each rep: `.map(rep -> UserFormatter.formatProfile(rep))`
   - Collects to list: `.collect(Collectors.toList())`
   - Returns List<String> of formatted pending company representatives

3. **StaffView** receives the list:
   - Displays header: "COMPANY REPRESENTATIVES"
   - If list is empty, displays: "No pending company representatives."
   - Otherwise, displays numbered list of pending company representatives
   - Prompts: "Enter company representative number to approve/reject (0 to go back to menu): "
   - Reads selection from Scanner
   - If selection is 0, returns to menu
   - Otherwise, extracts repID from the selected string using `extractStringIDFromString()`
   - Prompts: "Approve (A) or Reject (R)? "
   - Reads approval choice from Scanner
   - If choice is "A" or "a", sets approve = true
   - If choice is "R" or "r", sets approve = false

4. **StaffView** calls **controller.approveRejectCompanyRep(repID, approve)**

5. **StaffController.approveRejectCompanyRep(repID, approve)** is executed:
   - Calls **loginController.approveRejectCompanyRep(repID, approve)**

6. **LoginController.approveRejectCompanyRep(repID, approve)** is executed:
   - Calls **userRegistry.findById(repID)**
     - Returns User object if found, null if not found
   - If userRegistry.findById(repID) returns null:
     - Throws IllegalArgumentException: "Company representative not found."
   - If user is not instance of CompanyRepresentative:
     - Throws IllegalArgumentException: "Company representative not found."
   - Casts to CompanyRepresentative: `CompanyRepresentative compRep = (CompanyRepresentative) rep`
   - Calls **compRep.setApprovalStatus(approve ? StaffApprovalStatus.APPROVED : StaffApprovalStatus.REJECTED)**
     - Sets `this.approvalStatus = status` in CompanyRepresentative
   - Calls **userCsvHandler.updateCompanyRepStatusInCsv(compRep)**
     - Formats new CSV line: `compRep.getUserID(), compRep.getName(), compRep.getCompanyName(), compRep.getDepartment(), compRep.getPosition(), compRep.getEmail(), compRep.getPasswordForPersistence(), compRep.getApprovalStatus().toCsvString()`
     - Calls private method `updateCsvLine("sample_file/sample_company_representative_list.csv", compRep.getUserID(), newLine)`
       - Reads all lines from CSV file
       - Finds line where first column (userID) matches compRep.getUserID()
       - Replaces that line with newLine
       - Writes all lines back to CSV file

7. **StaffView** receives return (no exception):
   - Displays: "Company representative approved successfully." (if approve = true)
   - Or displays: "Company representative rejected successfully." (if approve = false)

---

## PHASE 3: LOGIN

### Step-by-Step Call Sequence

1. **LoginView.showLoginDialog()** is called
   - Displays: "=== Login ==="
   - Prompts: "Enter user ID: "
   - Reads userID from Scanner: `sc.nextLine().trim()`

2. **LoginView** calls **controller.userExists(userID)**

3. **LoginController.userExists(userID)** is executed:
   - Calls **userRegistry.findById(userID)**
     - Returns User object if found, null if not found
   - Returns `userRegistry.findById(userID) != null` (true if user exists, false otherwise)

4. **LoginView** receives return value:
   - If false (user not found):
     - Displays: "Error: User ID not found. Please check your user ID and try again."
     - Returns null (login fails)
   - If true (user exists):
     - Prompts: "Enter password: "
     - Reads password from Scanner: `sc.nextLine()`

5. **LoginView** calls **controller.authenticateAndGetProfile(userID, password)**

6. **LoginController.authenticateAndGetProfile(userID, password)** is executed:
   - Calls **authenticate(userID, password)**
     - **LoginController.authenticate(userID, password)** is executed:
       - Calls **userRegistry.findById(userID)**
         - Returns User object if found, null if not found
       - If userRegistry.findById(userID) returns null:
         - Throws IllegalArgumentException: "Invalid user ID. Please try again."
       - Calls **user.verifyPassword(password)**
         - Returns `this.password.equals(input)` (boolean)
       - If user.verifyPassword(password) returns false:
         - Throws IllegalArgumentException: "Incorrect password. Please try again."
       - If user is instance of CompanyRepresentative:
         - Calls **((CompanyRepresentative) user).isApproved()**
           - Returns `approvalStatus == StaffApprovalStatus.APPROVED` (boolean)
         - If isApproved() returns false:
           - Throws IllegalArgumentException: "Your account is pending approval. Please wait for Career Center Staff approval."
       - Calls **user.setLoggedIn(true)**
         - Sets `this.loggedIn = true` in User object
       - Returns User object
     - Receives User object
   - Calls **getFormattedProfile(user)**
     - **LoginController.getFormattedProfile(user)** is executed:
       - Calls **UserFormatter.formatProfile(user)**
         - Formats user profile as string based on user type
         - Returns formatted profile string
       - Returns formatted profile string
   - Returns formatted profile string

7. **LoginView** receives return value:
   - If successful (no exception):
     - Displays: "Login successful!"
     - Displays the formatted profile string
     - Returns userID string
   - If exception thrown:
     - Catches exception and displays: "Error: " + e.getMessage()
     - Returns null (login fails)

8. **MainApp** or **ViewFactory** receives userID:
   - If userID is not null:
     - Calls **userRegistry.findById(userID)** to get User object
     - Calls **ViewFactory.createAndRunView(user)**
       - Calls **ViewFactory.createDashboardStrategy(user)**
         - Calls **user.createDashboardStrategy()** (polymorphic call)
           - If user is Student: returns `new StudentDashboardStrategy((Student) user)`
           - If user is Staff: returns `new StaffDashboardStrategy((Staff) user)`
           - If user is CompanyRepresentative: returns `new CompanyRepDashboardStrategy((CompanyRepresentative) user)`
         - Returns DashboardStrategy
       - Creates **DashboardContext** with strategy: `new DashboardContext(strategy)`
       - Calls **context.executeDashboard()**
         - Calls **strategy.runDashboard()**
           - For CompanyRepresentative: runs CompanyRepDashboardStrategy.runDashboard()
             - Calls `view.run()` which displays CompanyRepresentativeView dashboard

---

## Error Handling Paths

### Registration Errors:
- **Invalid email format**: ValidationHelper.validateEmail() throws IllegalArgumentException → caught by LoginView → displays error message
- **Email already exists**: LoginController checks userRegistry.findById() → throws IllegalArgumentException → caught by LoginView → displays error message
- **Registration failed**: userRegistry.register() returns false → LoginController throws IllegalArgumentException → caught by LoginView → displays error message

### Approval Errors:
- **Company representative not found**: LoginController.approveRejectCompanyRep() checks if user exists and is CompanyRepresentative → throws IllegalArgumentException → caught by StaffView → displays error message

### Login Errors:
- **User ID not found**: LoginView checks controller.userExists() → returns false → displays error and returns null
- **Invalid user ID**: LoginController.authenticate() checks userRegistry.findById() → returns null → throws IllegalArgumentException → caught by LoginView → displays error message
- **Incorrect password**: LoginController.authenticate() checks user.verifyPassword() → returns false → throws IllegalArgumentException → caught by LoginView → displays error message
- **Account pending approval**: LoginController.authenticate() checks if CompanyRepresentative and not approved → throws IllegalArgumentException → caught by LoginView → displays error message

---

## Key Classes and Methods

### LoginView
- `showRegistrationDialog()` - Initiates registration flow
- `showLoginDialog()` - Initiates login flow
- `controller` - LoginController instance

### LoginController
- `registerCompanyRepresentative(email, name, password, companyName, department, position)` - Handles registration
- `authenticate(userID, password)` - Authenticates user
- `authenticateAndGetProfile(userID, password)` - Authenticates and returns profile
- `userExists(userID)` - Checks if user exists
- `getAllCompanyReps()` - Gets all company representatives
- `approveRejectCompanyRep(repID, approve)` - Approves/rejects company rep
- `userRegistry` - UserRegistry instance
- `userCsvHandler` - UserCsvHandlerInterface instance

### UserRegistry
- `findById(userID)` - Finds user by ID
- `register(user)` - Registers user in memory
- `getAllCompanyReps()` - Gets all company representatives

### CompanyRepresentative
- Constructor: `CompanyRepresentative(userID, name, password, email, companyName, department, position, statusFromCsv)`
- `isApproved()` - Checks if approved
- `setApprovalStatus(status)` - Sets approval status
- `getApprovalStatus()` - Gets approval status
- `parseStatusFromCsv(status)` - Parses status from CSV string

### UserCsvHandler
- `saveCompanyRepToCsv(compRep)` - Saves new company rep to CSV
- `updateCompanyRepStatusInCsv(compRep)` - Updates company rep status in CSV
- `updateCsvLine(filename, userID, newLine)` - Updates specific line in CSV

### StaffView
- `handleApproveRejectCompanyRep()` - Handles approval/rejection UI flow
- `controller` - StaffController instance

### StaffController
- `getPendingCompanyReps()` - Gets pending company representatives
- `approveRejectCompanyRep(repID, approve)` - Approves/rejects company rep
- `loginController` - LoginControllerInterface instance (from BaseUserController)

### ValidationHelper
- `validateEmail(email)` - Validates email format

### ViewFactory
- `createAndRunView(user)` - Creates and runs dashboard for user
- `createDashboardStrategy(user)` - Creates dashboard strategy for user

### DashboardContext
- `executeDashboard()` - Executes dashboard strategy

---

## Status Transitions

```
Registration:  [No Account] → [PENDING]
Approval:      [PENDING] → [APPROVED] or [REJECTED]
Login:         [APPROVED] → [Logged In]
               [PENDING/REJECTED] → [Login Denied]
```

---

**Last Updated**: 2025-11-18 
**Version**: 2.0

