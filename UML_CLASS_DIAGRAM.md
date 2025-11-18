# UML Class Diagram - Internship Management System

## Overview
Core class structure focusing on main features. Utility classes (formatters, filters) excluded for clarity.

---

## Core Class Diagram

```
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                           CORE CLASS DIAGRAM                                            │
└─────────────────────────────────────────────────────────────────────────────────────────┘

═══════════════════════════════════════════════════════════════════════════════════════════
                                    MODEL LAYER
═══════════════════════════════════════════════════════════════════════════════════════════

┌──────────────────────────────────────────┐
│          <<abstract>>                    │
│            User                          │
├──────────────────────────────────────────┤
│ - userID: String                         │
│ - name: String                           │
│ - password: String                       │
│ - email: String                          │
│ - loggedIn: boolean                      │
├──────────────────────────────────────────┤
│ + getUserID(): String                    │
│ + getName(): String                      │
│ + verifyPassword(String): boolean        │
│ + changePassword(String, String)         │
│ + setLoggedIn(boolean)                   │
│ + getUserType(): String {abstract}       │
│ + getCsvFilename(): String {abstract}    │
│ + createDashboardStrategy(): DashboardStrategy {abstract}│
└──────────────────────────────────────────┘
            ▲
            │
     ┌──────┴──────┬──────────────────┐
     │             │                  │
┌────▼────┐  ┌─────▼─────┐  ┌────────▼──────────┐
│ Student │  │   Staff   │  │ CompanyRepresentative│
├─────────┤  ├───────────┤  ├────────────────────┤
│ -yearOf │  │ -staffDept│  │ -companyName:      │
│  Study: │  │  :String  │  │   String           │
│  int    │  │           │  │ -department:       │
│ -major: │  │           │  │   String           │
│  String │  │           │  │ -position:         │
│ -appl-  │  │           │  │   String           │
│  ications:│ │           │  │ -approvalStatus:   │
│  List<  │  │           │  │   StaffApprovalStatus│
│  App>   │  │           │  │ +MAX_INTERNSHIPS=5 │
├─────────┤  ├───────────┤  ├────────────────────┤
│ +submit │  │           │  │ +isApproved():    │
│  App(...)│  │           │  │   boolean         │
│ +accept │  │           │  │ +setApprovalStatus()│
│  App(App)│  │           │  │                     │
│ +withdraw│  │           │  │                     │
│  App(App)│  │           │  │                     │
└────┬─────┘  └───────────┘  └──────────┬─────────┘
     │                                   │
     │                                   │
┌────▼───────────────────────────────────▼────┐
│          Application                         │
├──────────────────────────────────────────────┤
│ - id: int                                    │
│ - internship: Internship                     │
│ - applicant: Student                         │
│ - dateApplied: LocalDateTime                 │
│ - status: ApplicationStatus                  │
├──────────────────────────────────────────────┤
│ + getId(): int                               │
│ + getStatus(): ApplicationStatus             │
│ + setStatus(ApplicationStatus)               │
│ + getApplicant(): Student                    │
│ + getInternship(): Internship                │
└────┬───────────────┬─────────────────────────┘
     │               │
     │               │
┌────▼───────────────▼──────────┐
│         Internship             │
├────────────────────────────────┤
│ - id: int                      │
│ - title: String                │
│ - description: String           │
│ - level: String                │
│ - preferredMajor: String       │
│ - openDate: LocalDate          │
│ - closeDate: LocalDate         │
│ - companyName: String           │
│ - creator: CompanyRepresentative│
│ - visible: boolean             │
│ - numSlots: int                │
│ - filledSlots: int             │
│ - status: InternshipStatus     │
│ - applications: List<Application>│
├────────────────────────────────┤
│ + getID(): int                 │
│ + getTitle(): String           │
│ + getStatus(): InternshipStatus│
│ + isVisible(): boolean         │
│ + getCreator(): CompanyRep     │
│ + toggleVisibility()            │
│ + setStatus(InternshipStatus)  │
│ + updateDetails(...)            │
│ + canEdit(): boolean           │
│ + isEligibleForStudent(Student)│
└────────────────────────────────┘

┌──────────────────────────────────────────┐
│       UserRegistry                       │
│    <<Singleton>>                         │
├──────────────────────────────────────────┤
│ - instance: static UserRegistry          │
│ - users: Map<String, User>               │
├──────────────────────────────────────────┤
│ + getInstance(): static UserRegistry     │
│ + register(User): boolean                │
│ + findById(String): User                 │
│ + getAllUsers(): Collection<User>        │
│ + getAllCompanyReps(): List<CompanyRep>  │
└──────────────────────────────────────────┘

═══════════════════════════════════════════════════════════════════════════════════════════
                                   CONTROLLER LAYER
═══════════════════════════════════════════════════════════════════════════════════════════

┌──────────────────────────────────────────────────┐
│         LoginController                           │
├──────────────────────────────────────────────────┤
│ - userRegistry: UserRegistry                     │
│ - userCsvHandler: UserCsvHandlerInterface        │
├──────────────────────────────────────────────────┤
│ + authenticate(String, String): User             │
│ + changePassword(String, String, String): boolean│
│ + registerCompanyRepresentative(...): boolean    │
│ + approveRejectCompanyRep(String, boolean)       │
│ + getAllCompanyReps(): List<CompanyRep>          │
└──────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────┐
│      BaseUserController (abstract)                │
├──────────────────────────────────────────────────┤
│ - user: User                                      │
│ # internshipController: InternshipControllerInterface│
│ # applicationController: ApplicationControllerInterface│
│ # loginController: LoginControllerInterface       │
│ # filterSettings: FilterSettings                  │
├──────────────────────────────────────────────────┤
│ + isLoggedIn(): boolean                          │
│ + logout()                                       │
│ + getProfile(): String                           │
└──────────────────────────────────────────────────┘
            ▲
            │
     ┌──────┴──────┬──────────────────┐
     │             │                  │
┌────▼─────┐  ┌───▼────┐  ┌──────────▼──────────┐
│ Student  │  │ Staff  │  │ CompanyRepresentative│
│Controller│  │Controller│  │Controller           │
├──────────┤  ├─────────┤  ├─────────────────────┤
│ -student:│  │ -staff: │  │ -companyRep:         │
│  Student │  │  Staff  │  │  CompanyRepresentative│
├──────────┤  ├─────────┤  ├─────────────────────┤
│ +getInt  │  │ +approve│  │ +getInternships()   │
│  ernships│  │  Reject │  │ +createInternship(...│
│ +create  │  │  Company│  │ +editInternship(...) │
│  Applicat│  │  Rep(...│  │ +deleteInternship(...│
│  ion(...)│  │ +approve│  │ +getApplicationsFor │
│ +accept  │  │  Reject │  │  Internship(...)     │
│  Applicat│  │  Interns│  │ +confirmPlacement(...│
│  ion(int)│  │  hip(...│  │ +toggleVisibility(...│
│ +withdraw│  │ +getPend│  │                      │
│  Applicat│  │  ingComp│  │                      │
│  ion(...)│  │  anyReps│  │                      │
└──────────┘  └─────────┘  └─────────────────────┘

┌──────────────────────────────────────────────────┐
│      ApplicationController                        │
├──────────────────────────────────────────────────┤
│ + createApplication(...): Application            │
│ + getApplicationsForInternship(int): List<App>   │
│ + getApplicationsForStudent(Student): List<App>  │
│ + findApplicationByID(int): Application          │
│ + confirmPlacement(int, int, InternshipController)│
│ + rejectApplication(int, int, InternshipController)│
│ + approveWithdrawal(int)                         │
│ + rejectWithdrawal(int)                           │
└──────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────┐
│      InternshipController                         │
├──────────────────────────────────────────────────┤
│ + getAllInternships(): List<Internship>          │
│ + getVisibleInternshipsForStudent(...): List<Internship>│
│ + getPendingInternships(): List<Internship>      │
│ + getInternshipsByCreator(...): List<Internship> │
│ + createInternship(...): Internship              │
│ + findInternship(int): Internship                 │
│ + updateInternship(...)                          │
│ + deleteInternship(int)                          │
│ + updateInternshipApproval(int, boolean)         │
│ + toggleInternshipVisibility(int)                │
└──────────────────────────────────────────────────┘

═══════════════════════════════════════════════════════════════════════════════════════════
                                     VIEW LAYER
═══════════════════════════════════════════════════════════════════════════════════════════

┌──────────────────────────────────────────────────┐
│          BaseView (abstract)                      │
├──────────────────────────────────────────────────┤
│ - sc: static Scanner                             │
│ - BORDER: static String                          │
│ - WIDTH: static int                              │
├──────────────────────────────────────────────────┤
│ + run()                                          │
│ # showInitialInternshipsView() {abstract}        │
│ # initializeMenuOptions() {abstract}             │
│ # getDashboardTitle() {abstract}                 │
│ # displayCenteredHeader(String)                  │
│ # displayPaginatedInternshipTable(...)           │
│ # displayPaginatedApplicationTable(...)           │
└──────────────────────────────────────────────────┘
            ▲
            │
     ┌──────┴──────┬──────────────────┐
     │             │                  │
┌────▼─────┐  ┌───▼────┐  ┌──────────▼──────────┐
│ Student  │  │ Staff  │  │ CompanyRepresentative│
│   View   │  │  View │  │       View            │
├──────────┤  ├────────┤  ├──────────────────────┤
│ -control │  │ -cont  │  │ -controller:        │
│  ler:    │  │  roller│  │  CompanyRepresentative│
│  Student │  │  :Staff│  │  Controller         │
│  Contr   │  │  Contr │  │                      │
│  oller   │  │  oller│  │                      │
├──────────┤  ├────────┤  ├──────────────────────┤
│ +handleL │  │ +handle│  │ +handleListMyInternships│
│  istInte │  │  Approve│  │ +handleCreateInternship│
│  rnships │  │  Reject │  │ +handleEditInternship│
│ +handleA │  │  Company│  │ +handleDeleteInternship│
│  pplyToIn │  │  Rep() │  │ +handleViewApplications│
│  ternship│  │ +handle│  │ +handleConfirmPlacement│
│ +handleV │  │  Approve│  │                      │
│  iewAppli│  │  Reject │  │                      │
│  cations │  │  Interns│  │                      │
│           │  │  hip() │  │                      │
└───────────┘  └────────┘  └──────────────────────┘

┌──────────────────────────────────────────────────┐
│         LoginView                                │
├──────────────────────────────────────────────────┤
│ - controller: LoginController                   │
│ - userRegistry: UserRegistry                     │
├──────────────────────────────────────────────────┤
│ + run()                                          │
│ + displayLoginMenu()                             │
│ + displayRegistrationMenu()                      │
│ + handleLogin()                                  │
│ + handleRegistration()                           │
└──────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────┐
│         ViewFactory                               │
├──────────────────────────────────────────────────┤
│ + createAndRunView(User): static void            │
└──────────────────────────────────────────────────┘

═══════════════════════════════════════════════════════════════════════════════════════════
                                   ENUMERATIONS
═══════════════════════════════════════════════════════════════════════════════════════════

┌──────────────────────────────────────────────────┐
│         ApplicationStatus                        │
├──────────────────────────────────────────────────┤
│ PENDING, SUCCESSFUL, ACCEPTED,                   │
│ UNSUCCESSFUL, WITHDRAWAL_REQUESTED, WITHDRAWN    │
└──────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────┐
│         InternshipStatus                         │
├──────────────────────────────────────────────────┤
│ PENDING, APPROVED, REJECTED, FILLED              │
└──────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────┐
│         StaffApprovalStatus                      │
├──────────────────────────────────────────────────┤
│ PENDING, APPROVED, REJECTED                      │
└──────────────────────────────────────────────────┘
```

---

## Relationships

### Inheritance (Generalization) - ▲
- `Student extends User`
- `Staff extends User`
- `CompanyRepresentative extends User`
- `StudentController extends BaseUserController`
- `StaffController extends BaseUserController`
- `CompanyRepresentativeController extends BaseUserController`
- `StudentView extends BaseView`
- `StaffView extends BaseView`
- `CompanyRepresentativeView extends BaseView`

### Association - →
- `Application` → `Internship` (1-to-1)
- `Application` → `Student` (1-to-1)
- `Internship` → `CompanyRepresentative` (creator, 1-to-1)
- `Internship` → `Application` (1-to-many)
- `Student` → `Application` (1-to-many)
- `LoginController` → `UserRegistry`
- All Controllers → Models (dependency)
- All Views → Controllers (dependency)

### Composition - ◆
- `UserRegistry` ◆ `User` objects (Map<String, User>)
- `Internship` ◆ `Application` objects (List<Application>)
- `Student` ◆ `Application` objects (List<Application>)

---

## Design Patterns

1. **Singleton**: `UserRegistry` - Single instance for user management
2. **Factory**: `ViewFactory`, `UserFactory` - Object creation
3. **MVC**: Model ↔ Controller ↔ View separation
4. **Template Method**: `BaseView` - View structure template
5. **Dependency Inversion**: Controllers depend on interfaces

---

**Last Updated**: 2025-11-18
