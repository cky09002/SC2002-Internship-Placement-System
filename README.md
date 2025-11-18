# Internship Placement System

**Course:** SC2002 Object-Oriented Design and Programming  
**Semester:** 2025S1  
**Repository:** [GitHub](https://github.com/cky09002/SC2002-Internship-Placement-System)

A command-line application for managing internship placements, applications, and user interactions between students, company representatives, and career center staff.

---

## 🎯 Project Overview

This system manages the internship placement lifecycle: internship creation, approval, application processing, and placement confirmation. Built with MVC architecture and SOLID principles.

---

## ✨ Key Features

### 1. **User Management**
- **Three User Types**: Students, Company Representatives, Career Center Staff
- **Registration Flow**: Company reps register → Staff approve → Login enabled
- **Password Management**: Secure password change functionality
- **Role-Based Access**: Each user type has distinct permissions

### 2. **Internship Management**
- **Creation**: Company representatives create internships (status: PENDING)
- **Approval Workflow**: Staff must approve before visibility to students
- **Status Tracking**: PENDING → APPROVED/REJECTED → FILLED
- **Visibility Control**: Staff can toggle internship visibility
- **Slot Management**: Track available and filled positions

### 3. **Application Processing**
- **Application Submission**: Students apply to eligible internships
- **Application Review**: Company reps view and manage applications
- **Approval Workflow**: PENDING → SUCCESSFUL → ACCEPTED → CONFIRMED
- **Auto-Withdrawal**: Accepting one placement withdraws others
- **Status Tracking**: Real-time status updates

### 4. **Company Representative Flow**
1. **Registration**: Company rep registers with email, name, password, company details
2. **Pending Status**: Account created with PENDING approval status
3. **Staff Approval**: Staff reviews and approves/rejects registration
4. **Login Enabled**: Only approved company reps can login
5. **Internship Management**: Create, edit (before approval), delete internships
6. **Application Management**: View applications, accept/reject, confirm placements

---

## 🏗️ Build & Run

### Compile
```bash
javac -cp "src" -d "bin" -sourcepath "src" src/MainApp.java
```

### Run Application
```bash
java -cp bin MainApp
```

### Run Tests
```bash
java -cp bin test.TestRunner
```

### Docker
```bash
docker build -t internship-app .
docker run --rm -it internship-app
```

---

## 📋 System Requirements

- **Java**: JDK 17 or higher
- **CSV Files**: Required in `sample_file/` directory

---

## 🔐 Default Credentials

**All default passwords:** `password`

| User Type | User ID Example | Purpose |
|-----------|----------------|---------|
| Student | `U2310001A` | Apply to internships, manage applications |
| Staff | `sng001` | Approve internships, approve company reps |
| Company Rep | `Kchong042@e.ntu.edu.sg` | Create internships, review applications (must be approved) |

---

## 📐 Architecture

### MVC Structure

**Model** (`src/model/`)
- `User` (abstract) → `Student`, `Staff`, `CompanyRepresentative`
- `Internship` - Internship data and business logic
- `Application` - Application lifecycle
- `UserRegistry` - Singleton for user management

**View** (`src/view/`)
- `BaseView` (abstract) → `StudentView`, `StaffView`, `CompanyRepresentativeView`
- `LoginView` - Authentication UI

**Controller** (`src/controller/`)
- `LoginController` - Authentication and registration
- `StudentController` - Student operations
- `StaffController` - Staff operations
- `CompanyRepresentativeController` - Company rep operations
- `InternshipController` - Internship management
- `ApplicationController` - Application processing

### SOLID Principles
- **Single Responsibility**: Each class has one clear purpose
- **Open/Closed**: Factory method pattern for extensibility
- **Liskov Substitution**: Subclasses properly substitute base classes
- **Interface Segregation**: Small, focused interfaces
- **Dependency Inversion**: Dependencies on abstractions

### Design Patterns
- **Singleton**: `UserRegistry` for centralized user management
- **Factory**: `ViewFactory`, `UserFactory` for object creation
- **Template Method**: `BaseView` defines view structure
- **Strategy**: Dashboard strategies for different user types

---

## 📚 Documentation

- **Design Report**: [`REPORT.md`](REPORT.md)
- **UML Class Diagram**: [`UML_CLASS_DIAGRAM.md`](UML_CLASS_DIAGRAM.md)
- **Sequence Diagram**: [`SEQUENCE_DIAGRAM_COMPANY_REP_REGISTRATION.md`](SEQUENCE_DIAGRAM_COMPANY_REP_REGISTRATION.md)
- **Test Plan**: [`src/test/TEST_PLAN.md`](src/test/TEST_PLAN.md)
- **Test Results**: [`TEST_RESULTS.md`](TEST_RESULTS.md)

---

## 🚀 Quick Start

1. **Clone repository**
   ```bash
   git clone https://github.com/cky09002/SC2002-Internship-Placement-System.git
   cd SC2002-Internship-Placement-System
   ```

2. **Compile**
   ```bash
   javac -cp "src" -d "bin" -sourcepath "src" src/MainApp.java
   ```

3. **Run**
   ```bash
   java -cp bin MainApp
   ```

4. **Login** with default credentials (see above)

---

**Last Updated**: 2025-11-18
