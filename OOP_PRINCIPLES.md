# Object-Oriented Programming Principles Implementation

This document explains how the Internship Placement System implements the four core OOP principles (Abstraction, Encapsulation, Inheritance, Polymorphism) and the five SOLID principles.

---

## Core OOP Principles

### 1. Abstraction

**Definition:** Hiding complex implementation details and showing only essential features.

**Implementation Examples:**

#### Abstract Base Classes
```java
// User.java - Abstract base class
public abstract class User {
    // Common attributes
    private String userID;
    private String name;
    private String password;
    
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

**Benefits:**
- Hides complexity of user management
- Forces consistent interface across user types
- Allows generic code to work with `User` references

#### Interface Abstraction
```java
// FilterOptionsProvider.java
public interface FilterOptionsProvider {
    String[] getStatusFilterOptions();
}

// BaseUserController implements interface
public abstract class BaseUserController implements FilterOptionsProvider {
    // Each subclass provides its own implementation
}
```

**Benefits:**
- Views depend on interface, not concrete implementations
- Easy to add new filter option providers
- Decouples filter logic from user type enum

---

### 2. Encapsulation

**Definition:** Bundling data and methods that operate on the data within a single unit, restricting direct access.

**Implementation Examples:**

#### Private Attributes with Controlled Access
```java
// Internship.java
public class Internship {
    private int id;
    private String title;
    private InternshipStatus status;
    private int numSlots;
    private int filledSlots;
    
    // Controlled access via getters
    public int getID() { return id; }
    public InternshipStatus getStatus() { return status; }
    
    // Validation in setters
    public void setStatus(InternshipStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        this.status = status;
    }
}
```

#### Protected Access for Inheritance
```java
// User.java
public abstract class User {
    private String password;  // Private - no direct access
    
    protected String getPassword() {  // Protected - subclasses can access
        return password;
    }
    
    public String getPasswordForPersistence() {  // Public - for CSV handlers
        return password;
    }
}
```

**Benefits:**
- Data integrity through validation
- Controlled modification prevents invalid states
- Clear access boundaries

---

### 3. Inheritance

**Definition:** A mechanism where one class acquires the properties and behaviors of another class.

**Implementation Examples:**

#### User Hierarchy
```java
// Base class
public abstract class User {
    protected String userID;
    protected String name;
    protected String email;
    
    public User(String userID, String name, String password, String email) {
        // Common initialization
    }
    
    public boolean verifyPassword(String input) {
        // Shared implementation
    }
}

// Subclass
public class Student extends User {
    private int yearOfStudy;
    private String major;
    private List<Application> applications;
    
    public Student(String userID, String name, String password, 
                   String email, int yearOfStudy, String major) {
        super(userID, name, password, email);  // Call parent constructor
        this.yearOfStudy = yearOfStudy;
        this.major = major;
    }
    
    @Override
    public String getUserType() {
        return "Student";  // Override abstract method
    }
}
```

#### Controller Hierarchy
```java
// Base controller
public abstract class BaseUserController implements UserController, FilterOptionsProvider {
    protected final User user;
    public final InternshipControllerInterface internshipController;
    protected final ApplicationControllerInterface applicationController;
    
    protected BaseUserController(User user) {
        this.user = user;
        // Initialize shared dependencies
    }
    
    public boolean isLoggedIn() {
        return user.isLoggedIn();
    }
}

// Subclass
public class StudentController extends BaseUserController {
    private final Student student;
    
    public StudentController(Student student) {
        super(student);  // Call parent constructor
        this.student = student;
    }
    
    @Override
    public String[] getStatusFilterOptions() {
        return new String[]{"All", "Available", "Filled"};
    }
}
```

**Benefits:**
- Code reuse - common functionality in base class
- Consistent interface across subclasses
- Easy to add new user types or controllers

---

### 4. Polymorphism

**Definition:** The ability of an object to take on many forms - same interface, different implementations.

**Implementation Examples:**

#### Runtime Polymorphism (Method Overriding)
```java
// User.java
public abstract class User {
    public abstract String getUserType();
    public abstract DashboardStrategy createDashboardStrategy();
}

// Student.java
@Override
public String getUserType() {
    return "Student";
}

@Override
public DashboardStrategy createDashboardStrategy() {
    return new StudentDashboardStrategy(this);
}

// Staff.java
@Override
public String getUserType() {
    return "Staff";
}

@Override
public DashboardStrategy createDashboardStrategy() {
    return new StaffDashboardStrategy(this);
}
```

#### Interface Polymorphism
```java
// ViewFactory.java
public static DashboardStrategy createDashboardStrategy(User user) {
    return user.createDashboardStrategy();  // Polymorphic call
    // Returns StudentDashboardStrategy, StaffDashboardStrategy, or CompanyRepDashboardStrategy
}

// BaseView.java
protected void handleFilterMenu(...) {
    FilterOptionsProvider provider = getFilterOptionsProvider();
    String[] options = provider.getStatusFilterOptions();  // Polymorphic call
    // Returns different options based on controller type
}
```

#### Collection Polymorphism
```java
// UserRegistry.java
private Map<String, User> users;  // Can store any User subclass

public void register(User user) {
    users.put(user.getUserID(), user);  // Accepts Student, Staff, or CompanyRepresentative
}

public User findById(String userID) {
    return users.get(userID);  // Returns User reference (polymorphic)
}
```

**Benefits:**
- Generic code works with base types
- Easy to extend with new types
- Eliminates `instanceof` checks

---

## SOLID Principles

### 1. Single Responsibility Principle (SRP)

**Definition:** A class should have only one reason to change.

**Implementation Examples:**

#### CSV Handlers - Separate Persistence Logic
```java
// Before: UserRegistry handled both in-memory storage AND file I/O
// After: Separated responsibilities

// UserRegistry.java - Only in-memory management
public class UserRegistry {
    private Map<String, User> users;
    
    public void register(User user) { /* in-memory only */ }
    public User findById(String userID) { /* in-memory only */ }
    // NO file I/O methods
}

// UserCsvHandler.java - Only file persistence
public class UserCsvHandler implements UserCsvHandlerInterface {
    public void saveCompanyRepToCsv(CompanyRepresentative rep) { /* file I/O */ }
    public void updateCompanyRepStatusInCsv(String repID, StaffApprovalStatus status) { /* file I/O */ }
    // NO in-memory management
}
```

#### Model Classes - No Persistence Logic
```java
// Internship.java - Only business logic
public class Internship {
    // Business methods
    public boolean isEligibleForStudent(Student student) { /* ... */ }
    public void confirmPlacement() { /* ... */ }
    // NO CSV handler calls
}

// InternshipController.java - Handles persistence
public class InternshipController implements InternshipWriter {
    public void saveInternship(Internship internship) {
        csvHandler.save(internship);  // Persistence responsibility
    }
}
```

**Benefits:**
- Each class has clear, single purpose
- Changes to persistence don't affect business logic
- Easier to test and maintain

---

### 2. Open/Closed Principle (OCP)

**Definition:** Software entities should be open for extension, but closed for modification.

**Implementation Examples:**

#### Factory Method Pattern
```java
// User.java - Closed for modification
public abstract class User {
    public abstract DashboardStrategy createDashboardStrategy();
}

// Student.java - Extension via inheritance
@Override
public DashboardStrategy createDashboardStrategy() {
    return new StudentDashboardStrategy(this);
}

// ViewFactory.java - No modification needed for new user types
public static DashboardStrategy createDashboardStrategy(User user) {
    return user.createDashboardStrategy();  // Works with any User subclass
}
```

#### Filter Options Provider
```java
// FilterOptionsProvider.java - Interface (closed for modification)
public interface FilterOptionsProvider {
    String[] getStatusFilterOptions();
}

// StudentController.java - Extension via implementation
@Override
public String[] getStatusFilterOptions() {
    return new String[]{"All", "Available", "Filled"};
}

// To add new user type: Just implement interface, no changes to FilterMenu
```

**Benefits:**
- Add new features without modifying existing code
- Reduces risk of breaking existing functionality
- Follows "extend, don't modify" philosophy

---

### 3. Liskov Substitution Principle (LSP)

**Definition:** Subtypes must be substitutable for their base types without altering correctness.

**Implementation Examples:**

#### User Subclasses
```java
// All User subclasses can be used wherever User is expected
User student = new Student(...);
User staff = new Staff(...);
User companyRep = new CompanyRepresentative(...);

// All work with same interface
user.verifyPassword("password");
user.getUserType();
user.createDashboardStrategy();
```

#### Controller Subclasses
```java
// BaseUserController reference can hold any subclass
BaseUserController controller = new StudentController(student);
controller = new StaffController(staff);
controller = new CompanyRepresentativeController(companyRep);

// All provide same interface
controller.isLoggedIn();
controller.getFilterSettings();
controller.getStatusFilterOptions();  // Polymorphic - different implementations
```

**Benefits:**
- Guarantees substitutability
- Enables polymorphism
- Prevents breaking changes in inheritance hierarchy

---

### 4. Interface Segregation Principle (ISP)

**Definition:** Clients should not be forced to depend on interfaces they do not use.

**Implementation Examples:**

#### Split InternshipControllerInterface
```java
// Before: One large interface
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

// After: Segregated interfaces
public interface InternshipReader {
    List<Internship> getAllInternships();
    Internship findInternship(int id);
}

public interface InternshipWriter {
    void saveInternship(Internship internship);
    void deleteInternship(int id);
}

public interface InternshipValidator {
    boolean canEditInternship(int id);
    InternshipStatus getInternshipStatus(int id);
}

// Clients depend only on what they need
public class ApplicationController {
    private final InternshipReader internshipReader;  // Only needs read
    private final InternshipWriter internshipWriter;  // Only needs write
}

public class BaseUserController {
    protected void validateOwnership(..., InternshipValidator validator) {
        // Only needs validation
    }
}
```

#### FilterOptionsProvider Interface
```java
// Small, focused interface
public interface FilterOptionsProvider {
    String[] getStatusFilterOptions();
}

// Controllers implement only what they need
public abstract class BaseUserController implements FilterOptionsProvider {
    // Single method to implement
}
```

**Benefits:**
- Clients only depend on methods they use
- Reduces coupling
- Easier to understand and maintain

---

### 5. Dependency Inversion Principle (DIP)

**Definition:** Depend upon abstractions, not concretions.

**Implementation Examples:**

#### Constructor Injection with Interfaces
```java
// Before: Direct dependency on concrete class
public class ApplicationController {
    private ApplicationCsvHandler csvHandler = ApplicationCsvHandler.getInstance();
    private InternshipController internshipController = new InternshipController();
}

// After: Depend on interfaces
public class ApplicationController {
    private final CsvHandler<Application> csvHandler;
    private final InternshipReader internshipReader;
    private final InternshipWriter internshipWriter;
    
    public ApplicationController(CsvHandler<Application> csvHandler,
                                 InternshipReader internshipReader,
                                 InternshipWriter internshipWriter) {
        this.csvHandler = csvHandler;
        this.internshipReader = internshipReader;
        this.internshipWriter = internshipWriter;
    }
}
```

#### View Dependencies
```java
// BaseView depends on FilterOptionsProvider interface
public abstract class BaseView {
    protected void handleFilterMenu(..., String[] statusOptions) {
        FilterOptionsProvider provider = getFilterOptionsProvider();
        String[] options = provider.getStatusFilterOptions();
        // Depends on interface, not concrete controller
    }
    
    protected abstract FilterOptionsProvider getFilterOptionsProvider();
}

// Each view returns its controller (which implements interface)
public class StudentView extends BaseView {
    @Override
    protected FilterOptionsProvider getFilterOptionsProvider() {
        return controller;  // StudentController implements FilterOptionsProvider
    }
}
```

#### LoginController Dependencies
```java
// Depends on UserCsvHandlerInterface, not concrete class
public class LoginController implements LoginControllerInterface {
    private final UserRegistry userRegistry;
    private final UserCsvHandlerInterface userCsvHandler;
    
    public LoginController(UserRegistry userRegistry, 
                          UserCsvHandlerInterface userCsvHandler) {
        this.userRegistry = userRegistry;
        this.userCsvHandler = userCsvHandler;  // Interface dependency
    }
}
```

**Benefits:**
- Easy to swap implementations (e.g., for testing)
- Reduces coupling between classes
- Enables dependency injection
- Makes code more flexible and testable

---

## Design Patterns Supporting OOP Principles

### 1. Factory Method Pattern (OCP)
- **Location**: `User.createDashboardStrategy()`
- **Purpose**: Eliminates `instanceof` checks, enables extension
- **OOP Principle**: Polymorphism, Open/Closed

### 2. Template Method Pattern (Inheritance)
- **Location**: `BaseView`, `BaseUserController`
- **Purpose**: Defines skeleton, subclasses fill details
- **OOP Principle**: Inheritance, Abstraction

### 3. Singleton Pattern (Encapsulation)
- **Location**: `UserRegistry`
- **Purpose**: Single instance, controlled access
- **OOP Principle**: Encapsulation

### 4. Strategy Pattern (Polymorphism)
- **Location**: `DashboardStrategy` implementations
- **Purpose**: Interchangeable algorithms
- **OOP Principle**: Polymorphism, DIP

---

## Summary Table

| OOP Principle | Implementation | Key Classes |
|---------------|---------------|-------------|
| **Abstraction** | Abstract `User` class, interfaces | `User`, `FilterOptionsProvider`, `InternshipReader` |
| **Encapsulation** | Private attributes, controlled access | All model classes, `UserRegistry` |
| **Inheritance** | User hierarchy, Controller hierarchy | `User` → `Student/Staff/CompanyRep`, `BaseUserController` → subclasses |
| **Polymorphism** | Method overriding, interface implementation | `User.getUserType()`, `FilterOptionsProvider.getStatusFilterOptions()` |
| **SRP** | Separated persistence from business logic | `UserCsvHandler`, `InternshipController` |
| **OCP** | Factory method pattern, interface extension | `User.createDashboardStrategy()`, `FilterOptionsProvider` |
| **LSP** | All subclasses substitutable for base | `User` subclasses, `BaseUserController` subclasses |
| **ISP** | Small, focused interfaces | `InternshipReader`, `InternshipWriter`, `InternshipValidator` |
| **DIP** | Constructor injection with interfaces | All controllers, `BaseView` |

---

**Last Updated:** 2025-11-18  
**Version:** 1.0

