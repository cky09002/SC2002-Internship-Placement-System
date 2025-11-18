# Internship Management System - Design Report

## 1. System Overview

A CLI application managing internships, applications, and users (Students, Company Representatives, Career Center Staff). Follows MVC architecture and SOLID principles.

### Key Features
- User management with three user types
- Internship creation and approval workflow
- Application processing and placement confirmation
- Company representative registration and approval flow
- CSV-based data persistence

---

## 2. Design Considerations

### 2.1 MVC Architecture
**Model**: Domain objects (User, Student, Staff, CompanyRepresentative, Internship, Application)  
**View**: UI components (BaseView, StudentView, StaffView, CompanyRepresentativeView, LoginView)  
**Controller**: Business logic (LoginController, StudentController, StaffController, CompanyRepresentativeController)

**Rationale**: Separation allows independent modification of UI, business logic, and data structures.

### 2.2 User Hierarchy
Abstract `User` class with concrete subclasses:
- `Student extends User`
- `Staff extends User`
- `CompanyRepresentative extends User`

**Rationale**: Encapsulates common functionality (login, password) while allowing type-specific behaviors.

### 2.3 Controller Hierarchy
`BaseUserController` provides common functionality:
- Shared controllers (LoginController, InternshipController, ApplicationController)
- Common validation logic

**Rationale**: Reduces code duplication and ensures consistent behavior.

---

## 3. Object-Oriented Principles

### 3.1 Encapsulation
- Private attributes with controlled access via getters/setters
- Validation logic in setters using `ValidationHelper`
- Example: `Student.setYearOfStudy()` validates range (1-5)

### 3.2 Inheritance
- Abstract `User` class defines common interface
- Method overriding in subclasses
- Benefit: Code reuse and polymorphism

### 3.3 Polymorphism
- `User` reference can hold any subclass instance
- `ViewFactory` creates appropriate views based on User type
- Benefit: Generic code works with all user types

### 3.4 Abstraction
- Abstract `User` class hides implementation details
- `BaseView` provides template structure
- Benefit: Simplifies complex system

### 3.5 Single Responsibility Principle
Each class has one clear purpose:
- `UserRegistry`: User storage and retrieval
- `ApplicationController`: Application business logic
- `InternshipController`: Internship business logic
- `LoginController`: Authentication and registration

### 3.6 Open/Closed Principle
- System open for extension (new user types via inheritance)
- Closed for modification (existing code unchanged)
- Benefit: New features without breaking existing functionality

---

## 4. Design Patterns

### 4.1 Singleton Pattern
**Class**: `UserRegistry`
- Ensures single source of truth for user data
- Appropriate for CLI application

### 4.2 Factory Pattern
**Classes**: `UserFactory`, `ViewFactory`
- `UserFactory`: Creates User objects from CSV data
- `ViewFactory`: Creates View objects based on User type
- Benefit: Easy to add new user types or views

### 4.3 Template Method Pattern
**Classes**: `BaseView`, `BaseUserController`
- Defines template methods that call abstract methods
- Subclasses implement specific behaviors
- Benefit: Consistent structure, reduces duplication

### 4.4 MVC Pattern
- Complete separation: Model ↔ Controller ↔ View
- Controllers mediate all Model-View communication
- Benefit: Each layer can be modified independently

---

## 5. Company Representative Flow

### Registration → Approval → Login

1. **Registration Phase**
   - Company rep provides: email, name, password, company details
   - `LoginController.registerCompanyRepresentative()` validates email
   - Creates `CompanyRepresentative` with `PENDING` status
   - Saves to `UserRegistry` and CSV file

2. **Approval Phase**
   - Staff views pending company reps via `StaffController`
   - Staff approves/rejects via `LoginController.approveRejectCompanyRep()`
   - Status updated: `PENDING` → `APPROVED`/`REJECTED`
   - CSV file updated immediately

3. **Login Phase**
   - Company rep attempts login via `LoginController.authenticate()`
   - System checks approval status
   - If `APPROVED`: Login successful, access dashboard
   - If `PENDING`/`REJECTED`: Login denied with error message

---

## 6. Extensibility and Maintainability

### Adding New User Types
1. Create new class extending `User`
2. Implement `getUserType()` method
3. Update `UserFactory` to handle new type
4. Create corresponding Controller and View classes

**No changes needed** to existing code.

### Code Organization
- Clear package structure (model, view, controller, utils, constant)
- Consistent naming conventions
- Javadoc comments for public methods

---

## 7. Trade-offs

### CSV vs Database
**Chosen**: CSV file persistence  
**Rationale**: No setup required, portable, sufficient for scope

### Singleton vs Dependency Injection
**Chosen**: Singleton for `UserRegistry`  
**Rationale**: Simpler, no external dependencies, appropriate for CLI application

### Inheritance vs Composition
**Chosen**: Inheritance for User hierarchy  
**Rationale**: Users have distinct, mutually exclusive roles. Inheritance is natural and simpler.

---

## 8. Reflection

### Key Insights
1. **MVC is Powerful**: Strict MVC separation makes code maintainable
2. **Patterns are Tools**: Use patterns appropriately, not everywhere
3. **Simplicity Matters**: Sometimes the simplest solution is best
4. **Start with Design**: Spending time on architecture upfront saves time later

### Design Philosophy
- **Simplicity First**: Prefer simple solutions
- **Separation of Concerns**: Each component has clear responsibility
- **Extensibility**: Design for future changes
- **Maintainability**: Code should be easy to understand and modify

---

**Report Generated**: 2025-11-18  
**Course**: SC2002 Object-Oriented Design and Programming  
**Semester**: 2025S1
