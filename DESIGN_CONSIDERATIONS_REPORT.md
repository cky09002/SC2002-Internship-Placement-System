# Design Considerations Report - Internship Placement System

## Overview
This report discusses the design considerations and implementation of Object-Oriented Programming (OOP) concepts and principles in the Internship Placement System. The Company Representative registration, approval/rejection, and login sequence is used as the primary concrete example throughout this document to illustrate how OOP concepts and SOLID principles are applied in practice.

---

## 1. Architecture (Entity-Boundary-Control / MVC Structure)

### 1.1 Architecture Overview
The system follows the **Model-View-Controller (MVC)** architecture pattern, which is a variant of the Entity-Boundary-Control pattern. This separation ensures clear boundaries between data management, business logic, and user interface.

**Entity (Model)**: Domain objects representing business entities  
**Boundary (View)**: User interface components for interaction  
**Control (Controller)**: Business logic and coordination between Model and View

### 1.2 MVC Layers in Company Representative Flow

#### Model Layer (Entity)
- **`User`** (abstract): Base class for all users
- **`CompanyRepresentative`**: Concrete user type with company-specific attributes
- **`UserRegistry`**: Singleton managing in-memory user storage
- **`StaffApprovalStatus`**: Enumeration for approval states (PENDING, APPROVED, REJECTED)

#### View Layer (Boundary)
- **`LoginView`**: Handles registration and login UI
- **`StaffView`**: Handles approval/rejection UI for staff
- **`CompanyRepresentativeView`**: Dashboard for approved company representatives

#### Controller Layer (Control)
- **`LoginController`**: Manages authentication, registration, and company rep approval
- **`StaffController`**: Handles staff-specific operations including company rep approval
- **`CompanyRepresentativeController`**: Manages company rep-specific operations

### 1.3 MVC Separation in Company Representative Registration

**Registration Flow:**
1. **View** (`LoginView.showRegistrationDialog()`): Collects user input (email, name, password, company details)
2. **Controller** (`LoginController.registerCompanyRepresentative()`): Validates input, creates model, coordinates persistence
3. **Model** (`CompanyRepresentative`): Represents the business entity
4. **Controller** (`UserCsvHandler`): Handles persistence (separated from model per SRP)

**Key Design Decision**: Models contain no persistence logic. All file I/O is handled by controllers and CSV handlers, maintaining strict MVC compliance.

---

## 2. OO Concepts

### 2.1 Abstraction

**Definition**: Hiding complex implementation details and showing only essential features.

#### Example from Company Representative Flow

**Abstract Base Class - `User`**:
```java
public abstract class User {
    private String userID;
    private String name;
    private String password;
    private String email;
    
    // Abstract methods - subclasses must implement
    public abstract String getUserType();
    public abstract String getCsvFilename();
    public abstract DashboardStrategy createDashboardStrategy();
    
    // Concrete methods - shared implementation
    public boolean verifyPassword(String input) {
        return this.password.equals(input);
    }
}
```

**Abstraction Benefits in Registration Flow**:
- `LoginController.authenticate()` works with `User` reference, not specific types
- `UserRegistry` stores `Map<String, User>`, accepting any User subclass
- `UserCsvHandler.savePasswordChangeToCsv(User)` uses polymorphic `getCsvFilename()` method

**Interface Abstraction - `UserCsvHandlerInterface`**:
```java
public interface UserCsvHandlerInterface {
    void saveCompanyRepToCsv(CompanyRepresentative compRep);
    void updateCompanyRepStatusInCsv(CompanyRepresentative compRep);
    void savePasswordChangeToCsv(User user);
}
```

**Abstraction Benefits**:
- `LoginController` depends on `UserCsvHandlerInterface`, not concrete `UserCsvHandler`
- Enables testing with mock implementations
- Allows swapping CSV implementation without changing controller code

**Design Consideration**: Abstract classes provide shared implementation, while interfaces define contracts. The system uses both: abstract `User` for common functionality, interfaces for dependency inversion.

---

### 2.2 Encapsulation

**Definition**: Bundling data and methods that operate on the data within a single unit, restricting direct access.

#### Example from Company Representative Flow

**Private Attributes with Controlled Access**:
```java
public class CompanyRepresentative extends User {
    private String companyName;      // Private - no direct access
    private String department;       // Private - no direct access
    private StaffApprovalStatus approvalStatus;  // Private - no direct access
    
    // Controlled access via getters
    public String getCompanyName() { return companyName; }
    public StaffApprovalStatus getApprovalStatus() { return approvalStatus; }
    
    // Validation in setters
    public void setApprovalStatus(StaffApprovalStatus status) {
        this.approvalStatus = status;  // Direct assignment, but controlled through method
    }
    
    public void setCompanyName(String companyName) {
        ValidationHelper.validateNotEmpty(companyName, "Company name");
        this.companyName = companyName;  // Validation before assignment
    }
}
```

**Encapsulation in Approval Process**:
- `LoginController.approveRejectCompanyRep()` calls `compRep.setApprovalStatus()`, not direct field access
- Status changes are controlled through methods, ensuring consistency
- `isApproved()` method encapsulates the logic: `return approvalStatus == StaffApprovalStatus.APPROVED`

**Protected Access for Inheritance**:
```java
public abstract class User {
    private String password;  // Private - no direct access
    
    protected String getPassword() {  // Protected - subclasses can access
        return password;
    }
    
    public String getPasswordForPersistence() {  // Public - for CSV handlers only
        return password;
    }
}
```

**Design Consideration**: Access modifiers create clear boundaries:
- **Private**: Internal implementation, not accessible outside class
- **Protected**: Accessible to subclasses for inheritance
- **Public**: Controlled access for specific purposes (e.g., persistence)

**Benefits in Company Rep Flow**:
- Password cannot be directly modified; must use `changePassword()` with old password verification
- Approval status changes go through `setApprovalStatus()`, enabling future logging/auditing
- CSV handlers use `getPasswordForPersistence()` instead of accessing private fields

---

### 2.3 Inheritance

**Definition**: A mechanism where one class acquires the properties and behaviors of another class.

#### Example from Company Representative Flow

**User Hierarchy**:
```java
// Base class
public abstract class User {
    protected String userID;
    protected String name;
    protected String email;
    
    public User(String userID, String name, String password, String email) {
        this.userID = userID;
        this.name = name;
        this.password = password;
        this.email = email;
    }
    
    public boolean verifyPassword(String input) {
        return this.password.equals(input);
    }
}

// Subclass
public class CompanyRepresentative extends User {
    private String companyName;
    private String department;
    private String position;
    private StaffApprovalStatus approvalStatus;
    
    public CompanyRepresentative(String userID, String name, String password, 
                                String email, String companyName, String department, 
                                String position, String statusFromCsv) {
        super(userID, name, password, email);  // Call parent constructor
        this.companyName = companyName;
        this.department = department;
        this.position = position;
        this.approvalStatus = parseStatusFromCsv(statusFromCsv);
    }
    
    @Override
    public String getUserType() {
        return "CompanyRepresentative";  // Override abstract method
    }
    
    @Override
    public String getCsvFilename() {
        return "sample_file/sample_company_representative_list.csv";
    }
    
    @Override
    public DashboardStrategy createDashboardStrategy() {
        return new CompanyRepDashboardStrategy(this);
    }
}
```

**Inheritance Benefits in Registration**:
- `CompanyRepresentative` inherits `verifyPassword()`, `changePassword()`, `setLoggedIn()` from `User`
- Common user operations (login, password management) are defined once in base class
- Registration creates `CompanyRepresentative` but can be stored as `User` reference in `UserRegistry`

**Controller Hierarchy**:
```java
// Base controller
public abstract class BaseUserController {
    protected final User user;
    protected final LoginControllerInterface loginController;
    
    protected BaseUserController(User user, ..., LoginControllerInterface loginController) {
        this.user = user;
        this.loginController = loginController;
    }
}

// Subclass
public class StaffController extends BaseUserController {
    private final Staff staff;
    
    public StaffController(Staff staff) {
        super(staff, ..., new LoginController(...));  // Call parent constructor
        this.staff = staff;
    }
    
    public void approveRejectCompanyRep(String repID, boolean approve) {
        loginController.approveRejectCompanyRep(repID, approve);  // Uses inherited loginController
    }
}
```

**Design Consideration**: Inheritance enables code reuse and consistent interfaces. However, composition is preferred when there's no true "is-a" relationship. The system uses inheritance for user types (Student IS-A User) and controllers (StaffController IS-A BaseUserController).

---

### 2.4 Polymorphism

**Definition**: The ability of an object to take on many forms - same interface, different implementations.

#### Example from Company Representative Flow

**Runtime Polymorphism (Method Overriding)**:
```java
// In User (abstract)
public abstract String getUserType();
public abstract DashboardStrategy createDashboardStrategy();

// In CompanyRepresentative
@Override
public String getUserType() {
    return "CompanyRepresentative";
}

@Override
public DashboardStrategy createDashboardStrategy() {
    return new CompanyRepDashboardStrategy(this);
}

// In Student
@Override
public String getUserType() {
    return "Student";
}

@Override
public DashboardStrategy createDashboardStrategy() {
    return new StudentDashboardStrategy(this);
}
```

**Polymorphism in Login Flow**:
```java
// LoginController.authenticate() works with User reference
public User authenticate(String userID, String password) {
    User user = userRegistry.findById(userID);  // Returns User reference (polymorphic)
    if (user instanceof CompanyRepresentative && !((CompanyRepresentative) user).isApproved()) {
        throw new IllegalArgumentException("Account pending approval...");
    }
    user.setLoggedIn(true);  // Polymorphic call - works for any User subclass
    return user;
}
```

**Interface Polymorphism - Factory Method Pattern**:
```java
// ViewFactory uses polymorphic factory method
public static DashboardStrategy createDashboardStrategy(User user) {
    return user.createDashboardStrategy();  // Polymorphic call
    // Returns StudentDashboardStrategy, StaffDashboardStrategy, or CompanyRepDashboardStrategy
    // depending on actual user type
}

// Usage in MainApp
User user = loginController.authenticate(userID, password);
DashboardStrategy strategy = ViewFactory.createDashboardStrategy(user);  // Polymorphic
```

**Collection Polymorphism**:
```java
// UserRegistry stores polymorphic User references
private final Map<String, User> users = new HashMap<>();

public void register(User user) {
    users.put(user.getUserID(), user);  // Accepts Student, Staff, or CompanyRepresentative
}

public User findById(String userID) {
    return users.get(userID);  // Returns User reference (polymorphic)
}

// In LoginController.getAllCompanyReps()
public List<CompanyRepresentative> getAllCompanyReps() {
    return userRegistry.getAllCompanyReps();  // Returns List<CompanyRepresentative>
    // But internally filters from Map<String, User>
}
```

**Polymorphism in CSV Handling**:
```java
// UserCsvHandler uses polymorphic getCsvFilename()
public void savePasswordChangeToCsv(User user) {
    String filename = user.getCsvFilename();  // Polymorphic call
    // Returns different filename based on user type:
    // - Student: "sample_file/sample_student_list.csv"
    // - Staff: "sample_file/sample_staff_list.csv"
    // - CompanyRepresentative: "sample_file/sample_company_representative_list.csv"
    updatePasswordInCsv(filename, user.getUserID(), user.getPasswordForPersistence());
}
```

**Design Consideration**: Polymorphism eliminates `instanceof` checks and enables extensibility. The Factory Method pattern (`User.createDashboardStrategy()`) is preferred over `instanceof` checks in `ViewFactory`, following Open/Closed Principle.

**Benefits in Company Rep Flow**:
- `LoginController` doesn't need to know specific user types for basic operations
- Adding new user types doesn't require modifying existing authentication code
- CSV handlers work with `User` references, automatically using correct file based on type

---

## 3. OO Principles (SOLID)

### 3.1 Single Responsibility Principle (SRP)

**Definition**: A class should have only one reason to change.

#### Example from Company Representative Flow

**Before Refactoring (Violation)**:
```java
// UserRegistry had TWO responsibilities:
// 1. In-memory user management
// 2. CSV file I/O
public class UserRegistry {
    public void register(User user) { /* in-memory */ }
    public void saveCompanyRepToCsv(CompanyRepresentative rep) { /* file I/O */ }
    public void updateCompanyRepStatusInCsv(String repID, StaffApprovalStatus status) { /* file I/O */ }
}
```

**After Refactoring (Compliant)**:
```java
// UserRegistry - ONLY in-memory management
public class UserRegistry {
    private final Map<String, User> users = new HashMap<>();
    
    public boolean register(User user) { /* in-memory only */ }
    public User findById(String userID) { /* in-memory only */ }
    public List<CompanyRepresentative> getAllCompanyReps() { /* in-memory only */ }
    // NO file I/O methods
}

// UserCsvHandler - ONLY file persistence
public class UserCsvHandler implements UserCsvHandlerInterface {
    public void saveCompanyRepToCsv(CompanyRepresentative compRep) { /* file I/O only */ }
    public void updateCompanyRepStatusInCsv(CompanyRepresentative compRep) { /* file I/O only */ }
    public void savePasswordChangeToCsv(User user) { /* file I/O only */ }
    // NO in-memory management
}
```

**SRP in Registration Flow**:
- `LoginController.registerCompanyRepresentative()` coordinates but delegates:
  - Validation → `ValidationHelper`
  - Model creation → `CompanyRepresentative` constructor
  - In-memory storage → `UserRegistry.register()`
  - File persistence → `UserCsvHandler.saveCompanyRepToCsv()`

**Design Consideration**: Each class has one clear responsibility. Changes to CSV format only affect `UserCsvHandler`. Changes to in-memory structure only affect `UserRegistry`.

**Benefits**:
- Easier testing: Mock `UserCsvHandler` without file system
- Easier maintenance: CSV format changes don't affect user management logic
- Clear separation: Each class's purpose is obvious

---

### 3.2 Open/Closed Principle (OCP)

**Definition**: Software entities should be open for extension, but closed for modification.

#### Example from Company Representative Flow

**Factory Method Pattern - Eliminates `instanceof` Checks**:

**Before (Violation)**:
```java
// ViewFactory had to be modified for each new user type
public static DashboardStrategy createDashboardStrategy(User user) {
    if (user instanceof Student) {
        return new StudentDashboardStrategy((Student) user);
    } else if (user instanceof Staff) {
        return new StaffDashboardStrategy((Staff) user);
    } else if (user instanceof CompanyRepresentative) {
        return new CompanyRepDashboardStrategy((CompanyRepresentative) user);
    }
    // Adding new user type requires modifying this method
}
```

**After (Compliant)**:
```java
// User.java - Closed for modification
public abstract class User {
    public abstract DashboardStrategy createDashboardStrategy();
}

// CompanyRepresentative.java - Extension via inheritance
@Override
public DashboardStrategy createDashboardStrategy() {
    return new CompanyRepDashboardStrategy(this);
}

// ViewFactory.java - No modification needed for new user types
public static DashboardStrategy createDashboardStrategy(User user) {
    return user.createDashboardStrategy();  // Works with any User subclass
}
```

**OCP in Filter Options**:

**Before (Violation)**:
```java
// FilterMenu had to check user type enum
public static void showFilterMenu(UserType userType) {
    String[] options;
    if (userType == UserType.STUDENT) {
        options = new String[]{"All", "Available", "Filled"};
    } else if (userType == UserType.COMPANY_REP) {
        options = new String[]{"All", "PENDING", "APPROVED", "REJECTED", "Available", "Filled"};
    }
    // Adding new user type requires modifying this method
}
```

**After (Compliant)**:
```java
// FilterOptionsProvider.java - Interface (closed for modification)
public interface FilterOptionsProvider {
    String[] getStatusFilterOptions();
}

// BaseUserController implements interface
public abstract class BaseUserController implements FilterOptionsProvider {
    // Subclasses provide implementation
}

// CompanyRepresentativeController.java - Extension via implementation
@Override
public String[] getStatusFilterOptions() {
    return new String[]{"All", "PENDING", "APPROVED", "REJECTED", "Available", "Filled"};
}

// BaseView.java - No modification needed
protected void handleFilterMenu(...) {
    FilterOptionsProvider provider = getFilterOptionsProvider();
    String[] options = provider.getStatusFilterOptions();  // Polymorphic call
}
```

**Design Consideration**: Use interfaces and abstract methods for extension points. Concrete implementations provide specific behavior without modifying existing code.

**Benefits in Company Rep Flow**:
- Adding new user types doesn't require modifying `ViewFactory` or `FilterMenu`
- New user types automatically get dashboard creation and filter options
- Reduced risk of breaking existing functionality

---

### 3.3 Liskov Substitution Principle (LSP)

**Definition**: Subtypes must be substitutable for their base types without altering correctness.

#### Example from Company Representative Flow

**User Subclasses are Substitutable**:
```java
// All User subclasses can be used wherever User is expected
User student = new Student(...);
User staff = new Staff(...);
User companyRep = new CompanyRepresentative(...);

// All work with same interface
user.verifyPassword("password");  // Works for all
user.getUserType();  // Works for all (returns different strings)
user.createDashboardStrategy();  // Works for all (returns different strategies)
user.getCsvFilename();  // Works for all (returns different filenames)
```

**LSP in Authentication**:
```java
// LoginController.authenticate() accepts any User subclass
public User authenticate(String userID, String password) {
    User user = userRegistry.findById(userID);  // Could be Student, Staff, or CompanyRepresentative
    
    // All User subclasses support these operations
    if (!user.verifyPassword(password)) {
        throw new IllegalArgumentException("Incorrect password...");
    }
    
    // CompanyRepresentative-specific check (but doesn't break LSP)
    if (user instanceof CompanyRepresentative && !((CompanyRepresentative) user).isApproved()) {
        throw new IllegalArgumentException("Account pending approval...");
    }
    
    user.setLoggedIn(true);  // Works for all User subclasses
    return user;
}
```

**LSP in CSV Handling**:
```java
// UserCsvHandler works with User reference
public void savePasswordChangeToCsv(User user) {
    String filename = user.getCsvFilename();  // Polymorphic - each subclass returns correct filename
    // Works correctly for Student, Staff, and CompanyRepresentative
    updatePasswordInCsv(filename, user.getUserID(), user.getPasswordForPersistence());
}
```

**Design Consideration**: Subclasses must maintain the contract of the base class. `CompanyRepresentative` can be used anywhere `User` is expected, and all `User` methods work correctly.

**Benefits**:
- Generic code works with base types
- Enables polymorphism
- Prevents breaking changes in inheritance hierarchy

---

### 3.4 Interface Segregation Principle (ISP)

**Definition**: Clients should not be forced to depend on interfaces they do not use.

#### Example from Company Representative Flow

**Segregated InternshipController Interfaces**:

**Before (Violation)**:
```java
// One large interface - clients forced to depend on all methods
public interface InternshipControllerInterface {
    // Read methods
    List<Internship> getAllInternships();
    Internship findInternship(int id);
    
    // Write methods
    void saveInternship(Internship internship);
    void deleteInternship(int id);
    
    // Validation methods
    boolean canEditInternship(int id);
    InternshipStatus getInternshipStatus(int id);
}

// ApplicationController needs read and write, but NOT validation
public class ApplicationController {
    private InternshipControllerInterface controller;  // Forced to depend on validation methods too
}
```

**After (Compliant)**:
```java
// Segregated interfaces
public interface InternshipReader {
    List<Internship> getAllInternships();
    Internship findInternship(int id);
    InternshipStatus getInternshipStatus(int id);
}

public interface InternshipWriter {
    void saveInternship(Internship internship);
    void deleteInternship(int id);
}

public interface InternshipValidator {
    boolean canEditInternship(int id);
    InternshipStatus getInternshipStatus(int id);
}

// ApplicationController depends only on what it needs
public class ApplicationController {
    private final InternshipReader internshipReader;  // Only needs read
    private final InternshipWriter internshipWriter;  // Only needs write
    // No dependency on InternshipValidator
}

// BaseUserController uses only validation
public abstract class BaseUserController {
    protected void validateOwnership(int internshipID, CompanyRepresentative companyRep,
                                    InternshipValidator validator, String action) {
        // Only needs validation, not read or write
    }
}
```

**ISP in Company Rep Approval**:
```java
// StaffController only needs LoginControllerInterface methods for approval
public class StaffController extends BaseUserController {
    public void approveRejectCompanyRep(String repID, boolean approve) {
        loginController.approveRejectCompanyRep(repID, approve);
        // Doesn't need other LoginController methods like authenticate() or changePassword()
    }
}
```

**Design Consideration**: Split large interfaces into smaller, focused interfaces. Clients depend only on methods they actually use.

**Benefits**:
- Reduced coupling
- Clearer dependencies
- Easier to understand what each client needs

---

### 3.5 Dependency Inversion Principle (DIP)

**Definition**: Depend upon abstractions, not concretions.

#### Example from Company Representative Flow

**Constructor Injection with Interfaces**:

**Before (Violation)**:
```java
// LoginController created concrete dependencies
public class LoginController {
    private UserCsvHandler csvHandler = UserCsvHandler.getInstance();  // Concrete dependency
    private UserRegistry userRegistry = UserRegistry.getInstance();  // Concrete dependency
}
```

**After (Compliant)**:
```java
// LoginController depends on interfaces
public class LoginController implements LoginControllerInterface {
    private final UserRegistry userRegistry;
    private final UserCsvHandlerInterface userCsvHandler;  // Interface dependency
    
    public LoginController(UserRegistry userRegistry, UserCsvHandlerInterface userCsvHandler) {
        this.userRegistry = userRegistry;
        this.userCsvHandler = userCsvHandler;  // Depends on abstraction
    }
}
```

**DIP in ApplicationController**:
```java
// ApplicationController depends on interfaces, not concrete classes
public class ApplicationController implements ApplicationControllerInterface {
    private final CsvHandler<Application> csvHandler;  // Interface
    private final InternshipReader internshipReader;  // Interface
    private final InternshipWriter internshipWriter;  // Interface
    
    public ApplicationController(CsvHandler<Application> csvHandler,
                                 InternshipReader internshipReader,
                                 InternshipWriter internshipWriter) {
        this.csvHandler = csvHandler;
        this.internshipReader = internshipReader;
        this.internshipWriter = internshipWriter;
    }
}
```

**DIP in View Layer**:
```java
// BaseView depends on FilterOptionsProvider interface
public abstract class BaseView {
    protected void handleFilterMenu(...) {
        FilterOptionsProvider provider = getFilterOptionsProvider();  // Interface dependency
        String[] options = provider.getStatusFilterOptions();
        // Depends on interface, not concrete controller type
    }
    
    protected abstract FilterOptionsProvider getFilterOptionsProvider();
}

// Each view returns its controller (which implements interface)
public class CompanyRepresentativeView extends BaseView {
    @Override
    protected FilterOptionsProvider getFilterOptionsProvider() {
        return controller;  // CompanyRepresentativeController implements FilterOptionsProvider
    }
}
```

**Design Consideration**: High-level modules (controllers, views) should not depend on low-level modules (CSV handlers, concrete controllers). Both should depend on abstractions (interfaces).

**Benefits in Company Rep Flow**:
- Easy to swap implementations (e.g., mock `UserCsvHandler` for testing)
- Reduced coupling between classes
- Enables dependency injection
- Makes code more flexible and testable

---

## 4. Design Patterns Supporting OOP/SOLID

### 4.1 Factory Method Pattern (OCP, Polymorphism)
**Location**: `User.createDashboardStrategy()`  
**Purpose**: Eliminates `instanceof` checks, enables extension without modification  
**Example**: `CompanyRepresentative.createDashboardStrategy()` returns `CompanyRepDashboardStrategy`

### 4.2 Strategy Pattern (Polymorphism, DIP)
**Location**: `DashboardStrategy` implementations  
**Purpose**: Interchangeable algorithms for different user types  
**Example**: `CompanyRepDashboardStrategy`, `StudentDashboardStrategy`, `StaffDashboardStrategy`

### 4.3 Singleton Pattern (Encapsulation)
**Location**: `UserRegistry`, `UserCsvHandler`, `InternshipCsvHandler`  
**Purpose**: Single instance, controlled access  
**Example**: `UserRegistry.getInstance()` ensures only one registry exists

### 4.4 Template Method Pattern (Inheritance, Abstraction)
**Location**: `BaseView`, `BaseUserController`  
**Purpose**: Defines skeleton, subclasses fill details  
**Example**: `BaseView.run()` defines dashboard loop, subclasses implement specific menu options

---

## 5. Design Trade-offs and Considerations

### 5.1 Abstract Classes vs Interfaces
**When to use Abstract Classes**: When you need to provide shared implementation (e.g., `User` with common password handling)  
**When to use Interfaces**: When you need to define contracts without implementation (e.g., `UserCsvHandlerInterface`, `FilterOptionsProvider`)  
**Trade-off**: Java allows single inheritance but multiple interface implementation. Use abstract classes for "is-a" relationships with shared code, interfaces for "can-do" relationships.

### 5.2 Inheritance vs Composition
**When to use Inheritance**: True "is-a" relationship with code reuse (e.g., `CompanyRepresentative extends User`)  
**When to use Composition**: "has-a" relationship or when behavior needs to be swapped (e.g., `DashboardContext` has `DashboardStrategy`)  
**Trade-off**: Inheritance creates tight coupling but enables polymorphism. Composition is more flexible but requires more boilerplate.

### 5.3 Dependency Injection vs Direct Instantiation
**When to use Dependency Injection**: For dependencies that might change or need testing (e.g., `LoginController` receives `UserCsvHandlerInterface`)  
**When to use Direct Instantiation**: For stable, internal dependencies (e.g., `UserRegistry.getInstance()` in controllers)  
**Trade-off**: Dependency injection increases flexibility and testability but requires more setup code.

---

## 6. Real-World Scenarios and Benefits

### 6.1 Adding New User Types
**How OOP/SOLID Enables Extension**:
1. Create new class extending `User` (e.g., `Admin extends User`)
2. Implement abstract methods: `getUserType()`, `getCsvFilename()`, `createDashboardStrategy()`
3. Create `AdminDashboardStrategy` implementing `DashboardStrategy`
4. No changes needed to `ViewFactory`, `LoginController`, or `FilterMenu`

**Benefits**: System is open for extension, closed for modification (OCP).

### 6.2 Testing and Maintainability
**How SOLID Improves Testability**:
- Mock `UserCsvHandlerInterface` to test `LoginController` without file I/O
- Mock `InternshipReader` to test `ApplicationController` without loading internships
- Each class has single responsibility, making unit tests focused and simple

**Benefits**: Easier to write tests, faster test execution, better code coverage.

### 6.3 Code Reusability
**How Inheritance and Polymorphism Enable Reuse**:
- `User.verifyPassword()` used by all user types
- `BaseUserController` provides common functionality for all user controllers
- `UserCsvHandler.savePasswordChangeToCsv(User)` works for all user types via polymorphism

**Benefits**: Less code duplication, consistent behavior, easier maintenance.

---

## 7. Conclusion

The Internship Placement System demonstrates comprehensive application of OOP concepts and SOLID principles through the Company Representative registration, approval, and login flow. Key achievements:

1. **MVC Architecture**: Clear separation between Model, View, and Controller layers
2. **Abstraction**: Abstract `User` class and interfaces hide implementation details
3. **Encapsulation**: Private attributes with controlled access maintain data integrity
4. **Inheritance**: User and Controller hierarchies enable code reuse
5. **Polymorphism**: Factory Method and Strategy patterns eliminate type checking
6. **SOLID Principles**: All five principles applied throughout the system
7. **Design Patterns**: Factory Method, Strategy, Singleton, and Template Method support OOP/SOLID

The design is extensible, maintainable, and testable, demonstrating how proper OOP design leads to robust software architecture.

---

**Last Updated**: 2025-11-19  
**Version**: 1.0

