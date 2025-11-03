# UML Class Diagram - Internship Management System

## Overview
This document presents the complete UML Class Diagram for the Internship Management System, showing all class relationships, inheritance, associations, interfaces, and key design patterns.

## Complete Class Diagram (Text Representation)

```
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                           COMPLETE CLASS DIAGRAM                                         │
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
│ + getName(): String                       │
│ + getEmail(): String                      │
│ + verifyPassword(String): boolean        │
│ + changePassword(String, String)         │
│ + logout()                               │
│ + setLoggedIn(boolean)                   │
│ + setName(String)                        │
│ + setEmail(String)                       │
│ + getUserType(): String {abstract}       │
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
│ +submit │  │ +getStaff │  │ +getCompanyName()  │
│  App(...)│  │  Dept():  │  │ +getDepartment()  │
│ +accept │  │  String   │  │ +getPosition()     │
│  App(App)│  │ +setStaff │  │ +isApproved():    │
│ +withdraw│  │  Dept(...)│  │   boolean         │
│  App(App)│  │           │  │ +getApprovalStatus()│
│ +findApp│  │           │  │ +setApprovalStatus()│
│  WithID()│ │           │  │ +setApproved(bool)  │
│ +getYear│  │           │  │ +getStatusString...()│
│  OfStudy()│ │           │  │ +toString(): String│
│ +getMajor│  │           │  │                     │
│ +setYear │  │           │  │                     │
│  OfStudy()│ │           │  │                     │
│ +setMajor│  │           │  │                     │
└────┬─────┘  └───────────┘  └──────────┬─────────┘
     │                                   │
     │                                   │
     │                                   │
┌────▼───────────────────────────────────▼────┐
│          Application                         │
├──────────────────────────────────────────────┤
│ - id: int                                    │
│ - internship: Internship                    │
│ - applicant: Student                         │
│ - dateApplied: LocalDateTime                 │
│ - status: ApplicationStatus                  │
│ - previousStatus: ApplicationStatus          │
│ - withdrawalReason: String                   │
│ - nextID: static int = 500000               │
│ - allApplications: static List<Application>  │
├──────────────────────────────────────────────┤
│ + getId(): int                               │
│ + getStatus(): ApplicationStatus             │
│ + setStatus(ApplicationStatus)               │
│ + getApplicant(): Student                    │
│ + getInternship(): Internship                │
│ + getDateApplied(): LocalDateTime            │
│ + requestWithdrawal(String)                  │
│ + requestWithdrawal()                        │
│ + approveWithdrawal()                        │
│ + rejectWithdrawal()                        │
│ + getWithdrawalReason(): String              │
│ + getPreviousStatus(): ApplicationStatus     │
│ + getAllApplications(): static List<App>     │
│ + toString(): String                         │
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
│ - openDate: LocalDate           │
│ - closeDate: LocalDate         │
│ - companyName: String           │
│ - creator: CompanyRepresentative│
│ - visible: boolean             │
│ - numSlots: int                │
│ - filledSlots: int              │
│ - status: InternshipStatus      │
│ - applications: List<Application>│
│ - nextID: static int = 100000   │
│ - internships: static List<Internship>│
├────────────────────────────────┤
│ + getID(): int                 │
│ + getTitle(): String            │
│ + getDescription(): String      │
│ + getLevel(): String            │
│ + getPreferredMajor(): String  │
│ + getOpenDate(): LocalDate      │
│ + getCloseDate(): LocalDate    │
│ + getCompanyName(): String     │
│ + getCreator(): CompanyRep     │
│ + getStatus(): InternshipStatus │
│ + isVisible(): boolean          │
│ + isVisiblePrivate(): boolean  │
│ + getNumSlots(): int           │
│ + getFilledSlots(): int        │
│ + getApplications(): List<App> │
│ + addApplication(Application)  │
│ + toggleVisibility()            │
│ + setStatus(InternshipStatus)  │
│ + updateDetails(...)            │
│ + delete()                      │
│ + confirmPlacement()            │
│ + isVisibleToStudent(Student)  │
│ + canStudentViewDetails(Student)│
│ + hasStudentApplied(Student)   │
│ + isEligibleForStudent(Student) │
│ + isFilled(): boolean           │
│ + getDisplayStatus(): String    │
│ + canEdit(): boolean            │
│ + isOwnedBy(CompanyRep): boolean│
│ + findWithID(int): static Internship│
│ + getAllInternships(): static List<Internship>│
│ + toString(): String            │
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
│ + saveCompanyRepToCsv(CompanyRep)       │
│ + updateCompanyRepStatusInCsv(CompanyRep)│
│ + savePasswordChangeToCsv(User)          │
│ - updateCsvLine(String, String, String)  │
│ - updatePasswordInCsv(String, String, String)│
└──────────────────────────────────────────┘

═══════════════════════════════════════════════════════════════════════════════════════════
                                   CONTROLLER LAYER
═══════════════════════════════════════════════════════════════════════════════════════════

┌──────────────────────────────────────────────────┐
│         LoginController                           │
├──────────────────────────────────────────────────┤
│ - userRegistry: UserRegistry                     │
├──────────────────────────────────────────────────┤
│ + authenticate(String, String): User             │
│ + authenticateAndGetProfile(...): String         │
│ + getFormattedProfile(User): String              │
│ + changePassword(String, String, String): boolean│
│ + registerCompanyRepresentative(...): boolean    │
│ + findUserById(String): User                     │
│ + getAllCompanyReps(): List<CompanyRep>          │
│ + approveRejectCompanyRep(String, boolean)       │
└──────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────┐
│      BaseUserController (abstract)                │
├──────────────────────────────────────────────────┤
│ - user: User                                      │
│ # internshipController: InternshipController     │
│ # applicationController: ApplicationController    │
│ # loginController: LoginController                │
│ # filterSettings: FilterSettings                 │
├──────────────────────────────────────────────────┤
│ + isLoggedIn(): boolean                          │
│ + logout()                                       │
│ + getProfile(): String                           │
│ + getFilterSettings(): FilterSettings            │
│ # toStringList(Stream<T>): List<String>          │
│ # toStringList(List<T>): List<String>            │
│ # validateOwnership(...)                         │
│ # validateOwnershipAndEditability(...)            │
│ # validateApprovedStatus(...)                    │
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
│ +listInt │  │ +approve│  │ +listMyInternships()│
│  ernships│  │  Reject │  │ +createInternship(...│
│ +getInt  │  │  Company│  │ +viewApplications(...│
│  ernships│  │  Rep(...│  │ +getApplicationsFor │
│ +create  │  │ +update │  │  Internship(...)     │
│  Applicat│  │  Interns│  │ +getApplicationDetails│
│  ion(...)│  │  hipAppro│  │ +getInternshipDetails│
│ +accept  │  │  val(...│  │ +editInternship(...) │
│  Applicat│  │ +approve│  │ +deleteInternship(...│
│  ion(int)│  │  Reject │  │ +getApprovedInternships│
│ +reject  │  │  Withdraw│  │ +toggleVisibility(...│
│  Placemen│  │  al(...) │  │ +confirmPlacement(...│
│  t(int)  │  │ +getPend│  │ +rejectApplication(...│
│ +withdraw│  │  ingInte│  │ +getInternships()    │
│  Applicat│  │  rships()│  │ +getInternshipsForFilter│
│  ion(...)│  │ +getPend│  │ +editProfile(...)    │
│ +viewApp │  │  ingComp│  │                      │
│  lications│  │  anyReps│  │                      │
│ +getAppli│  │ +getWith│  │                      │
│  cationDet│  │  drawal │  │                      │
│  ails(...)│  │  Requests│  │                      │
│ +getInte │  │ +listInt│  │                      │
│  rnshipDet│  │  ernship│  │                      │
│  ails(...)│  │  Opportun│  │                      │
│ +editProf│  │  ities()│  │                      │
│  ile(...) │  │ +getInt │  │                      │
│           │  │  ernship│  │                      │
│           │  │  Details│  │                      │
│           │  │ +editPro│  │                      │
│           │  │  file(...)│                      │
└───────────┘  └─────────┘  └─────────────────────┘

┌──────────────────────────────────────────────────┐
│      ApplicationController                        │
├──────────────────────────────────────────────────┤
│ + createApplication(...): Application            │
│ + getApplicationsForInternship(int): List<App>   │
│ + getApplicationsForInternship(int, InternshipController): List<App>│
│ + hasStudentAppliedForInternship(...): boolean   │
│ + getApplicationsForStudent(Student): List<App>  │
│ + findApplicationByID(int): Application          │
│ + findApplicationByID(int, Student): Application │
│ + getWithdrawalRequests(): List<Application>     │
│ + withdrawApplication(int, Student, String)      │
│ + acceptApplication(int, Student)                 │
│ + processWithdrawal(int, boolean)                │
│ + approveWithdrawal(int)                         │
│ + rejectWithdrawal(int)                          │
│ + confirmPlacement(int, int, InternshipController)│
│ + confirmPlacement(int)                         │
│ + rejectApplication(int, int, InternshipController)│
│ + rejectApplication(int)                        │
│ + rejectPlacement(int, Student)                  │
│ - getAllApplications(InternshipController): List<App>│
└──────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────┐
│      InternshipController                         │
├──────────────────────────────────────────────────┤
│ + getAllInternships(): List<Internship>          │
│ + getAllInternships(FilterSettings): List<Internship>│
│ + getVisibleInternshipsForStudent(...): List<Internship>│
│ + getPendingInternships(): List<Internship>      │
│ + getInternshipsByCreator(...): List<Internship> │
│ + getApprovedInternshipsByCreator(...): List<Internship>│
│ + createInternship(...): Internship              │
│ + findInternship(int): Internship                 │
│ + updateInternship(...)                          │
│ + deleteInternship(int)                          │
│ + updateInternshipApproval(int, boolean)         │
│ + toggleInternshipVisibility(int)                │
│ + getInternshipStatus(int): InternshipStatus     │
│ + canEditInternship(int): boolean                │
│ + isInternshipOwnedBy(int, CompanyRep): boolean  │
│ + getInternshipDetails(int): String              │
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
│ # loginStatusChecker: Supplier<Boolean>           │
├──────────────────────────────────────────────────┤
│ + run()                                          │
│ # showInitialInternshipsView() {abstract}        │
│ # initializeMenuOptions() {abstract}             │
│ # getDashboardTitle() {abstract}                │
│ # displayCenteredHeader(String)                  │
│ # showMenuDialog(Map<Integer, MenuOption>): MenuOption│
│ # waitForEnter()                                 │
│ # extractIDFromString(String): int              │
│ # extractStringIDFromString(String): String      │
│ # displayNumberedListAndSelect(...): int         │
│ # displayList(String, List<String>, String)      │
│ # promptInt(String): int                        │
│ # promptString(String): String                   │
│ # handleAction(Runnable)                         │
│ # handleAction(Runnable, String)                 │
│ # displayListWithDetails(...)                    │
│ # displayPaginatedInternshipTable(...)           │
│ # displayPaginatedApplicationTable(...)          │
│ # showInternshipsLoop(...)                       │
│ # handleFilterMenu(...)                          │
│ # handleApproveRejectPattern(...)               │
│ # handleApproveRejectPatternInt(...)             │
│ # handleListWithAction(...)                      │
│ # handleActionWithID(...)                        │
│ # handleActionWithTwoIDs(...)                    │
│ # displayMenuAndGetSelection(...): int[]         │
│ # checkEditCancelled(int[]): boolean            │
│ # collectFieldValues(...): String[]              │
│ # parseDate(String): LocalDate                  │
│ # parseInt(String, int): int                     │
│ # getInternshipDetailsForFilter(int): String {abstract}│
└──────────────────────────────────────────────────┘
            ▲
            │
     ┌──────┴──────┬──────────────────┐
     │             │                  │
┌────▼─────┐  ┌───▼────┐  ┌──────────▼──────────┐
│ Student  │  │ Staff  │  │ CompanyRepresentative│
│   View   │  │  View │  │       View            │
├──────────┤  ├────────┤  ├──────────────────────┤
│ -control │  │ -cont  │  │ -controller:         │
│  ler:    │  │  roller│  │  CompanyRepresentative│
│  Student │  │  :Staff│  │  Controller          │
│  Contr   │  │  Contr │  │                      │
│  oller   │  │  oller│  │                      │
├──────────┤  ├────────┤  ├──────────────────────┤
│ #showInit│  │ #showIn│  │ #showInitialInternships│
│  ialInte │  │  itial │  │  View()              │
│  rnships │  │  Inter │  │ #getInternshipDetails│
│  View()  │  │  nships│  │  ForFilter(int)      │
│ #initiali│  │  View()│  │ +handleViewApplicat│
│  zeMenuO │  │ #initi │  │  ionsForInternship(int)│
│  ptions()│  │  alize │  │ #initializeMenuOptions()│
│ #getDash │  │  MenuO │  │ #getDashboardTitle()│
│  boardTi │  │  ptions│  │ #getProfileText()    │
│  tle()   │  │ #getDa │  │ #editProfileFields()│
│ #getProf │  │  shboar│  │ +handleListMyInternships│
│  ileText │  │  dTitl │  │ +handleFilterInternships│
│ #editProf│  │  e()   │  │ +handleCreateInternship│
│  ileField│  │ #getPr │  │ +handleEditInternship│
│  s()     │  │  ofileT│  │ +handleDeleteInternship│
│ +handleL │  │  ext() │  │ +handleToggleVisibility│
│  istInte │  │ #editP │  │ +handleViewApplications│
│  rnships │  │  rofile│  │ +handleConfirmPlacement│
│ +handleF │  │  Field │  │                      │
│ ilterInt │  │  s()   │  │                      │
│  ernships│  │ +handle│  │                      │
│ +handleA │  │  Approve│                      │
│  pplyToIn │  │  Reject│                      │
│  ternship│  │  Company│                      │
│ +handleV │  │  Rep() │                      │
│  iewAppli│  │ +handle│                      │
│  cations │  │  Approve│                      │
│           │  │  Reject│                      │
│           │  │  Interns│                      │
│           │  │  hip() │                      │
│           │  │ +handle│                      │
│           │  │  Approve│                      │
│           │  │  Reject│                      │
│           │  │  Withdraw│                      │
│           │  │  al() │                      │
│           │  │ +handle│                      │
│           │  │  Filter│                      │
│           │  │  Interns│                      │
│           │  │  hips()│                      │
│           │  │ +handle│                      │
│           │  │  ListIn│                      │
│           │  │  ternship│                      │
│           │  │  Opportun│                      │
│           │  │  ities()│                      │
└───────────┘  └────────┘  └──────────────────────┘

┌──────────────────────────────────────────────────┐
│         LoginView                                │
├──────────────────────────────────────────────────┤
│ - controller: LoginController                   │
│ - userRegistry: UserRegistry                     │
├──────────────────────────────────────────────────┤
│ + run()                                          │
│ + displayLoginMenu()                              │
│ + displayRegistrationMenu()                      │
│ + handleLogin()                                  │
│ + handleRegistration()                           │
│ - promptUserID(): String                         │
│ - promptPassword(): String                       │
└──────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────┐
│         ViewFactory                               │
├──────────────────────────────────────────────────┤
│ + createAndRunView(User): static void            │
└──────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────┐
│         MenuOption                               │
├──────────────────────────────────────────────────┤
│ - desc: String                                   │
│ - onSelCallback: Runnable                        │
├──────────────────────────────────────────────────┤
│ + getDesc(): String                              │
│ + getOnSelCallback(): Runnable                   │
└──────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────┐
│         TableView                                 │
├──────────────────────────────────────────────────┤
│ + displayPaginatedTable(...): static boolean     │
└──────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────┐
│         FilterMenu                               │
├──────────────────────────────────────────────────┤
│ - sc: static Scanner                             │
├──────────────────────────────────────────────────┤
│ + showFilterMenu(...): static void               │
│ - showFilterPreview(...): static void            │
│ - createFilterMenuOptions(...): static Map<Integer, MenuOption>│
│ - showFilterMenuDialog(...): static int[]        │
│ - showFullPreview(...): static void              │
└──────────────────────────────────────────────────┘

═══════════════════════════════════════════════════════════════════════════════════════════
                                  UTILITY LAYER
═══════════════════════════════════════════════════════════════════════════════════════════

┌──────────────────────────────────────────────────┐
│         <<interface>>                            │
│         Formatter                                │
├──────────────────────────────────────────────────┤
│ + format(Object): String                         │
└──────────────────────────────────────────────────┘
            ▲
            │
     ┌──────┴──────┬──────────────────┐
     │             │                  │
┌────▼─────┐  ┌───▼────┐  ┌──────────▼──────────┐
│ Application│  │Internship│  │    UserFormatter   │
│ Formatter  │  │ Formatter│  │                    │
├───────────┤  ├──────────┤  ├────────────────────┤
│ -showInt  │  │ -showStat│  │ + formatProfile(User)│
│  ernshipDe│  │  us:     │  │   : static String   │
│  tails:   │  │  boolean │  │                    │
│  boolean  │  │ -header: │  │                    │
│ -header:  │  │  String  │  │                    │
│  String   │  │          │  │                    │
├───────────┤  ├──────────┤  └────────────────────┘
│ +format(  │  │ +format( │
│  Object): │  │  Object):│
│  String   │  │  String  │
│ +formatAs │  │ +formatAs│
│  Row(...)│  │  Row(...)│
│  :static  │  │  :static │
│  String   │  │  String  │
│ +formatDet│  │ +formatDet│
│  ails(...)│  │  ails(...)│
│  :static  │  │  :static │
│  String   │  │  String  │
│ +formatTab│  │          │
│  leRowFrom│  │          │
│  Object(...):│          │
│  static String[]│          │
│ +displayPa│  │          │
│  ginatedTab│  │          │
│  le(...):  │  │          │
│  static boolean│          │
└───────────┘  └──────────┘

┌──────────────────────────────────────────────────┐
│         <<interface>>                            │
│         CsvHandler                               │
├──────────────────────────────────────────────────┤
│ + escapeCSV(String): static String               │
└──────────────────────────────────────────────────┘
            ▲
            │
     ┌──────┴──────┬──────────────────┐
     │             │                  │
┌────▼─────┐  ┌───▼────┐             │
│ Application│  │Internship│             │
│ CsvHandler │  │ CsvHandler│             │
├───────────┤  ├──────────┤             │
│ + loadFrom│  │ + loadFrom│             │
│  Csv():   │  │  Csv():   │             │
│  static   │  │  static   │             │
│  void     │  │  void     │             │
│ + saveToC │  │ + saveToC │             │
│  sv(App): │  │  sv(Interns│             │
│  static   │  │  hip):     │             │
│  void     │  │  static   │             │
│           │  │  void     │             │
│           │  │ -formatCsv│             │
│           │  │  Line(...):│             │
│           │  │  static String│         │
└───────────┘  └──────────┘             │

┌──────────────────────────────────────────────────┐
│         UserFactory                              │
├──────────────────────────────────────────────────┤
│ + fromCsv(UserType, String[]): static User       │
│ - safe(String[], int): static String             │
│ - parseIntSafe(String, int): static int          │
└──────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────┐
│         ValidationHelper                         │
├──────────────────────────────────────────────────┤
│ - EMAIL_PATTERN: static String                   │
│ - STUDENT_ID_PATTERN: static String              │
│ - STAFF_ID_PATTERN: static String                │
├──────────────────────────────────────────────────┤
│ + validateEmail(String): static void             │
│ + validateNotEmpty(String, String): static void  │
│ + validateRange(int, int, int, String): static void│
│ + validateUserID(String, UserType): static void  │
└──────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────┐
│         FilterSettings                           │
├──────────────────────────────────────────────────┤
│ - statusFilter: InternshipStatus                 │
│ - statusFilterStr: String                        │
│ - majorFilter: String                            │
│ - levelFilter: String                            │
│ - openingDateFilter: LocalDate                   │
│ - closingDateFilter: LocalDate                   │
│ - companyFilter: String                          │
│ - keywordFilter: String                          │
│ - sortOrder: String                              │
├──────────────────────────────────────────────────┤
│ + reset()                                        │
│ + getStatusFilter(): InternshipStatus            │
│ + setStatusFilter(InternshipStatus)             │
│ + getMajorFilter(): String                      │
│ + setMajorFilter(String)                         │
│ + getLevelFilter(): String                      │
│ + setLevelFilter(String)                         │
│ + getOpeningDateFilter(): LocalDate             │
│ + setOpeningDateFilter(LocalDate)                │
│ + getClosingDateFilter(): LocalDate             │
│ + setClosingDateFilter(LocalDate)               │
│ + getCompanyFilter(): String                    │
│ + setCompanyFilter(String)                       │
│ + getKeywordFilter(): String                     │
│ + setKeywordFilter(String)                       │
│ + getSortOrder(): String                         │
│ + setSortOrder(String)                           │
│ + hasActiveFilters(): boolean                    │
│ + getFilterSummary(): String                     │
└──────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────┐
│         InternshipFilter                         │
├──────────────────────────────────────────────────┤
│ + applyFilters(List<?>, FilterSettings): static List<?>│
│ + applySorting(List<Internship>, String): static List<Internship>│
│ + getStatusFilterOptions(): static String[]      │
│ + getLevelFilterOptions(): static String[]      │
│ + getMajorFilterOptions(...): static String[]    │
│ + getSortOptions(): static String[]              │
│ - applyStatusFilter(...): static Stream<Internship>│
│ - applyStatusFilterByString(...): static Stream<Internship>│
│ - applyMajorFilter(...): static Stream<Internship>│
│ - applyLevelFilter(...): static Stream<Internship>│
│ - applyOpeningDateFilter(...): static Stream<Internship>│
│ - applyClosingDateFilter(...): static Stream<Internship>│
│ - applyCompanyFilter(...): static Stream<Internship>│
│ - applyKeywordFilter(...): static Stream<Internship>│
└──────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────┐
│         ViewFormatter                            │
├──────────────────────────────────────────────────┤
│ + displayHeader(String, String, int): static void│
│ + displaySmallHeader(String, String, int): static void│
│ + waitForEnter(Scanner): static void             │
│ + extractIDFromString(String): static int        │
│ + extractStringIDFromString(String): static String│
└──────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────┐
│         TableFormatter                           │
├──────────────────────────────────────────────────┤
│ + formatTableRow(...): static String[]           │
│ + formatTable(...): static String                │
└──────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────┐
│         FilterFormatter                          │
├──────────────────────────────────────────────────┤
│ + formatFilterSummary(...): static String        │
└──────────────────────────────────────────────────┘

═══════════════════════════════════════════════════════════════════════════════════════════
                                   ENUMERATIONS
═══════════════════════════════════════════════════════════════════════════════════════════

┌──────────────────────────────────────────────────┐
│         ApplicationStatus                        │
├──────────────────────────────────────────────────┤
│ PENDING                                          │
│ SUCCESSFUL                                       │
│ ACCEPTED                                         │
│ UNSUCCESSFUL                                     │
│ WITHDRAWAL_REQUESTED                             │
│ WITHDRAWN                                        │
└──────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────┐
│         InternshipStatus                         │
├──────────────────────────────────────────────────┤
│ PENDING                                          │
│ APPROVED                                         │
│ REJECTED                                         │
│ FILLED                                           │
└──────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────┐
│         StaffApprovalStatus                      │
├──────────────────────────────────────────────────┤
│ PENDING                                          │
│ APPROVED                                         │
│ REJECTED                                         │
└──────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────┐
│         UserType                                 │
├──────────────────────────────────────────────────┤
│ STUDENT                                          │
│ STAFF                                            │
│ COMPANY_REPRESENTATIVE                           │
└──────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────┐
│         MenuConstants                            │
├──────────────────────────────────────────────────┤
│ MENU_CHOICE_CANCEL: static int                   │
│ MENU_CHOICE_CLEAR: static int                    │
│ MENU_CHOICE_PREVIEW: static int                  │
│ FIELD_INDEX_LEVEL: static int                    │
│ FIELD_INDEX_SLOTS: static int                    │
│ TABLE_CMD_APPROVE: static String                 │
│ TABLE_CMD_CONFIRM: static String                 │
│ TABLE_CMD_REJECT: static String                  │
│ TABLE_CMD_WITHDRAW: static String                │
│ TABLE_CMD_TOGGLE: static String                 │
│ ... (other constants)                            │
└──────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────┐
│         FilterConstants                          │
├──────────────────────────────────────────────────┤
│ (Contains filter-related constants)              │
└──────────────────────────────────────────────────┘
```

## Relationships Summary

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
- `ApplicationFormatter implements Formatter`
- `InternshipFormatter implements Formatter`
- `ApplicationCsvHandler implements CsvHandler`
- `InternshipCsvHandler implements CsvHandler`

### Association - →
- `Application` → `Internship` (1-to-1)
- `Application` → `Student` (1-to-1)
- `Internship` → `CompanyRepresentative` (creator, 1-to-1)
- `Internship` → `Application` (1-to-many)
- `Student` → `Application` (1-to-many)
- `LoginController` → `UserRegistry`
- All Controllers → Models (dependency)
- All Views → Controllers (dependency)
- `BaseUserController` → `LoginController`, `InternshipController`, `ApplicationController`
- `BaseView` → Utility classes

### Composition - ◆
- `UserRegistry` ◆ `User` objects (Map<String, User>)
- `Internship` ◆ `Application` objects (List<Application>)
- `Student` ◆ `Application` objects (List<Application>)

### Dependency - ──►
- Controllers depend on Model classes
- Views depend on Controllers (MVC pattern)
- CSV Handlers depend on Models
- Formatters depend on Models
- Controllers depend on Utility classes

## Design Patterns Identified

### 1. Singleton Pattern
- **UserRegistry**: Single instance for centralized user management

### 2. Factory Pattern
- **UserFactory**: Creates User objects from CSV data
- **ViewFactory**: Creates View objects based on User type

### 3. MVC (Model-View-Controller) Pattern
- Complete separation: Model ↔ Controller ↔ View
- Controllers mediate all Model-View communication

### 4. Template Method Pattern
- **BaseView**: Template structure for all views
- **BaseUserController**: Common functionality template

### 5. Strategy Pattern
- **Formatter**: Different formatting strategies
- **FilterSettings + InternshipFilter**: Filtering strategies

### 6. Interface Segregation
- **Formatter**: Formatting interface
- **CsvHandler**: CSV handling interface

## Encapsulation Points
- All attributes are private
- Access through getters/setters
- Validation in setters
- Static factory methods for object creation

## Polymorphism Points
- `User` abstract class with polymorphic behavior
- `Formatter` interface implementations
- `CsvHandler` interface implementations
- Controller hierarchy with method overriding
- View hierarchy with template method pattern
