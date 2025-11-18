# Changes Since Last GitHub Commit

Refactoring to ensure strict MVC architecture, SOLID principles, and OOP best practices.

---

## Class Structure Changes

### New Classes
- **`UserCsvHandler`** - Handles all user persistence (moved from `UserRegistry`)
- **`CsvDeletable`** - Interface for CSV handlers supporting deletion
- **`FilterOptionsProvider`** - Interface for providing user-type-specific filter options (ISP compliance)

### Modified Classes

**Model Layer:**
- **`User`** - Added abstract methods: `getCsvFilename()`, `createDashboardStrategy()`
- **`Student/Staff/CompanyRepresentative`** - Implement factory method for dashboard creation
- **`Internship/Application`** - Removed direct CSV handler calls (SRP compliance)
- **`UserRegistry`** - Removed file I/O operations (now in-memory only)

**Controller Layer:**
- **`ApplicationController`** - Uses `InternshipReader` and `InternshipWriter` interfaces (ISP)
- **`BaseUserController`** - Uses `InternshipValidator` interface (ISP), implements `FilterOptionsProvider`
- **`LoginController`** - Uses `UserCsvHandlerInterface` (DIP)
- **`StudentController/StaffController/CompanyRepresentativeController`** - Implement `getStatusFilterOptions()` for user-specific filter options
- All controllers now use constructor injection with interfaces

**View Layer:**
- **`ViewFactory`** - Uses factory method pattern instead of `instanceof` checks
- **`BaseView`** - Uses `FilterOptionsProvider` interface instead of user type enum (DIP compliance)
- **`FilterMenu`** - Accepts status options array from provider instead of user type

---

## Interface Segregation (ISP)

**Split `InternshipControllerInterface` into:**
- `InternshipReader` - Read operations
- `InternshipWriter` - Write/modify operations  
- `InternshipValidator` - Validation operations
- `InternshipControllerInterface` - Extends all three

**Impact on Sequence Diagrams:**
- `ApplicationController` → `InternshipReader` (read only)
- `ApplicationController` → `InternshipWriter` (write only)
- `BaseUserController` → `InternshipValidator` (validate only)

---

## Dependency Inversion (DIP)

**Before:** Controllers created concrete dependencies
```java
this.csvHandler = ApplicationCsvHandler.getInstance();
this.internshipController = new InternshipController();
```

**After:** Controllers depend on interfaces
```java
public ApplicationController(CsvHandler<Application> csvHandler, 
                            InternshipReader internshipReader,
                            InternshipWriter internshipWriter)
```

**Impact on Sequence Diagrams:**
- All controller interactions now show interface dependencies
- `LoginController` → `UserCsvHandlerInterface` (not concrete class)

---

## Factory Method Pattern

**Before:** `ViewFactory` used `instanceof` checks
```java
if (user instanceof Student) {
    return new StudentDashboardStrategy((Student) user);
}
```

**After:** Polymorphic factory method
```java
// User.java
public abstract DashboardStrategy createDashboardStrategy();

// ViewFactory.java
return user.createDashboardStrategy();
```

**Impact on Sequence Diagrams:**
- `ViewFactory` → `User.createDashboardStrategy()` (polymorphic call)
- Each `User` subclass creates its own dashboard strategy

---

## Package Reorganization

**New Package:** `controller/interfaces/`
- `ApplicationControllerInterface`
- `InternshipControllerInterface`
- `InternshipReader`
- `InternshipWriter`
- `InternshipValidator`
- `LoginControllerInterface`

**Impact on Sequence Diagrams:**
- All controller interface references updated to `controller.interfaces.*`

---

## Sequence Diagram Updates

### Company Representative Registration Flow
**Affected Classes:**
- `LoginView` → `LoginController` → `UserRegistry` → `CompanyRepresentative`
- `UserCsvHandler` now handles CSV persistence (not `UserRegistry`)

**Changes:**
- `LoginController.registerCompanyRepresentative()` → `UserCsvHandler.saveCompanyRepToCsv()`
- `LoginController.approveRejectCompanyRep()` → `UserCsvHandler.updateCompanyRepStatusInCsv()`

### Application Processing Flow
**Affected Classes:**
- `ApplicationController` now uses `InternshipReader` and `InternshipWriter` separately
- Slot updates: `Application.approveWithdrawal()` → `Internship.confirmPlacement()` → `updateFilledSlots()`

**Changes:**
- `ApplicationController.confirmPlacement()` → `InternshipWriter.saveInternship()`
- `ApplicationController.rejectWithdrawal()` → Updates slots if restoring to SUCCESSFUL/ACCEPTED

### Internship Management Flow
**Affected Classes:**
- `CompanyRepresentativeController` → `InternshipValidator` (for validation)
- `InternshipController` implements `InternshipReader`, `InternshipWriter`, `InternshipValidator`

---

## SOLID Principles Summary

| Principle | Implementation |
|-----------|---------------|
| **SRP** | CSV handlers separated from models; `UserRegistry` only manages in-memory storage |
| **OCP** | Factory method pattern allows extension without modification |
| **LSP** | All `User` subclasses properly substitute base class |
| **ISP** | Split large interfaces into focused, role-specific interfaces |
| **DIP** | Controllers depend on interfaces, not concrete classes |

---

## Files Modified

**New Files:**
- `src/utils/csv/UserCsvHandler.java`
- `src/utils/csv/UserCsvHandlerInterface.java`
- `src/utils/csv/CsvDeletable.java`
- `src/utils/filter/FilterOptionsProvider.java`
- `src/controller/interfaces/*` (6 interface files)

**Modified Files:**
- All controller classes (dependency injection)
- All model classes (removed persistence logic)
- `ViewFactory.java` (factory method pattern)
- `UserRegistry.java` (removed file I/O)

**Deleted Files:**
- Old interface files from `controller` package (moved to `controller/interfaces`)

---

## Filter Options Provider (ISP/DIP Compliance)

**Problem:** Filter options were determined by user type enum, violating OCP (adding new user types required modifying filter logic).

**Solution:** Created `FilterOptionsProvider` interface:
- `BaseUserController` implements `FilterOptionsProvider`
- Each controller subclass provides its own filter options via `getStatusFilterOptions()`
- Views get options from controller (polymorphism), not from user type enum

**Impact on Sequence Diagrams:**
- `BaseView.handleFilterMenu()` → `getFilterOptionsProvider().getStatusFilterOptions()`
- Each view subclass returns its controller as `FilterOptionsProvider`
- `FilterMenu` receives status options array (not user type enum)

**Filter Options by User Type:**
- **Students**: "All", "Available", "Filled"
- **Company Representatives**: "All", "PENDING", "APPROVED", "REJECTED", "Available", "Filled"
- **Staff**: "All", "PENDING", "APPROVED", "REJECTED"

---

**Last Updated:** 2025-11-18  
**Version:** 2.1
