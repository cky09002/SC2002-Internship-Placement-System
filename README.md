# Internship Placement System

**Course:** SC2002 Object-Oriented Design and Programming  
**Semester:** 2025S1  
**Repository:** [GitHub](https://github.com/cky09002/SC2002-Internship-Placement-System)

A comprehensive command-line application for managing internship placements, applications, and user interactions between students, company representatives, and career center staff.

---

## 🎯 Project Overview

This system provides a centralized platform for managing the entire internship placement lifecycle, from internship creation and posting to application processing and placement confirmation. The application follows Object-Oriented Design principles and implements several design patterns for maintainability and extensibility.

---

## ✨ Key Features

### 1. **User Management**
- **Three User Types**: Students, Company Representatives, and Career Center Staff
- **Role-Based Access Control**: Each user type has distinct permissions and capabilities
- **Password Management**: Secure password change functionality
- **User Registration**: Company representatives can self-register (pending staff approval)

### 2. **Internship Management**
- **Internship Creation**: Company representatives can create and post internships
- **Internship Approval Workflow**: Staff must approve internships before they become visible to students
- **Status Tracking**: Pending, Approved, Rejected, Filled, Closed status management
- **Visibility Control**: Company representatives can toggle internship visibility
- **Slot Management**: Track total slots and filled positions

### 3. **Application Processing**
- **Application Submission**: Students can apply to available internships
- **Application Review**: Company representatives can view and manage applications
- **Approval Workflow**: Multi-stage approval process (Pending → Accepted → Confirmed)
- **Application Filtering**: Filter by status, internship, student, or date range
- **Status Tracking**: Real-time status updates throughout the application lifecycle

### 4. **Filtering and Reporting**
- **Advanced Filtering**: Filter internships by level, major, company, status, and date range
- **Filter Persistence**: Saved filter preferences across sessions
- **Comprehensive Reporting**: Staff can generate reports on internships and applications
- **Data Export**: CSV-based data persistence for all entities

### 5. **User Interface**
- **Clean CLI Design**: Formatted tables and menus for better readability
- **Interactive Tables**: Sortable and filterable data presentation
- **Context-Aware Menus**: Dynamic menu options based on user permissions
- **Error Handling**: User-friendly error messages and validation

---

## 🏗️ Build Options

### Option 1: Using Docker (Recommended)

**Prerequisites:** Docker installed on your system

```bash
# Build the Docker image
docker build -t internship-app .

# Run the application
docker run --rm -it internship-app
```

**Advantages:**
- Consistent environment across different systems
- No need to install JDK locally
- Isolated execution environment

### Option 2: Direct Compilation and Execution

**Prerequisites:** Java Development Kit (JDK) 11 or higher

```bash
# Clone the repository
git clone https://github.com/cky09002/SC2002-Internship-Placement-System.git
cd SC2002-Internship-Placement-System

# Create output directory
mkdir -p out

# Compile all Java files
# Windows:
dir /s /b src\*.java > javafiles.txt && javac -d out -sourcepath src @javafiles.txt && del javafiles.txt

# Linux/macOS:
find src -name "*.java" -type f > javafiles.txt && javac -d out -sourcepath src @javafiles.txt && rm javafiles.txt

# Run the application
java -cp out Assignment.src.MainApp
```

**Note:** The `out/` directory contains compiled class files and is ignored by git. You must compile before first run.

### Option 3: GitHub Actions (Automated Testing)

The project includes automated build and test workflows via GitHub Actions. See [`.github/workflows`](.github/workflows/) for workflow definitions.

**Workflow Features:**
- Automatic compilation on push/PR
- Test execution
- Multi-platform testing (Windows, Linux, macOS)
- Build status badges

---

## 📋 System Requirements

- **Java**: JDK 11 or higher (JDK 17 recommended)
- **Operating System**: Windows, macOS, or Linux
- **CSV Data Files**: Required in `sample_file/` directory:
  - `sample_student_list.csv`
  - `sample_staff_list.csv`
  - `sample_company_representative_list.csv`
  - `sample_internships.csv`
  - `sample_applications.csv`

---

## 🧪 Testing

### Run Test Suite

```bash
# After compilation
java -cp out Assignment.src.test.TestRunner
```

### Test Plan Documentation

Comprehensive test cases are documented in [`src/test/TEST_PLAN.md`](src/test/TEST_PLAN.md).

---

## 🔐 Default Credentials

**All default passwords:** `password`

| User Type | User ID Example | Purpose |
|-----------|----------------|---------|
| Student | `U2310001A` | Apply to internships, manage applications |
| Staff | `sng001` | Approve internships, generate reports, approve company reps |
| Company Rep | `Kchong042@e.ntu.edu.sg` | Create internships, review applications (must be approved by staff) |

---

## 📐 Object-Oriented Analysis

### Architecture Overview

The system follows a **Model-View-Controller (MVC)** architecture pattern, ensuring clear separation of concerns:

#### **Models** (`src/model/`)
- `User` (abstract base class)
  - `Student` - Student-specific attributes and behaviors
  - `Staff` - Staff-specific capabilities
  - `CompanyRepresentative` - Company representative functionality
- `Internship` - Internship data and business logic
- `Application` - Application lifecycle management
- `UserRegistry` - Singleton pattern for user management

#### **Views** (`src/view/`)
- `BaseView` - Abstract base class for all views
- `StudentView` - Student-specific UI
- `StaffView` - Staff dashboard
- `CompanyRepresentativeView` - Company rep interface
- `LoginView` - Authentication UI
- `ViewFactory` - Factory pattern for view creation

#### **Controllers** (`src/controller/`)
- `BaseUserController` - Common controller functionality
- `LoginController` - Authentication and registration
- `StudentController` - Student operations
- `StaffController` - Staff operations
- `CompanyRepresentativeController` - Company rep operations
- `InternshipController` - Internship management
- `ApplicationController` - Application processing

### Object-Oriented Principles Applied

1. **Encapsulation**
   - Private attributes with controlled access via getters/setters
   - Validation logic encapsulated within classes
   - Example: `ValidationHelper` utility for input validation

2. **Inheritance**
   - Abstract `User` class with concrete subclasses
   - `BaseView` provides template structure for all views
   - `BaseUserController` contains shared controller logic

3. **Polymorphism**
   - `User` reference can hold any subclass instance
   - `ViewFactory` creates appropriate views based on user type
   - Method overriding for type-specific behaviors

4. **Abstraction**
   - Abstract classes hide implementation details
   - Interface-based design for formatters and handlers
   - Factory pattern abstracts object creation

5. **Single Responsibility Principle**
   - Each class has one clear purpose
   - `UserRegistry` manages user storage/retrieval only
   - Controllers handle specific business logic domains

### Design Patterns Implemented

- **Singleton Pattern**: `UserRegistry` ensures single instance
- **Factory Pattern**: `ViewFactory`, `UserFactory` for object creation
- **Template Method Pattern**: `BaseView` defines view structure
- **Strategy Pattern**: Different formatters for display strategies
- **Observer Pattern**: Status change notifications

---

## 👥 Collaborators

This project is developed as part of the SC2002 course assignment.

---

## 📚 Documentation

- **Design Report**: [`REPORT.md`](REPORT.md) - Comprehensive design documentation
- **UML Class Diagram**: [`UML_CLASS_DIAGRAM.md`](UML_CLASS_DIAGRAM.md) - System architecture
- **Sequence Diagrams**: [`SEQUENCE_DIAGRAM_COMPANY_REP_REGISTRATION.md`](SEQUENCE_DIAGRAM_COMPANY_REP_REGISTRATION.md) - Registration flow
- **Test Plan**: [`src/test/TEST_PLAN.md`](src/test/TEST_PLAN.md) - Testing documentation
- **Source Code**: Organized in `src/` directory with clear package structure

---

## 🚀 Quick Start Guide

1. **Clone the repository**
   ```bash
   git clone https://github.com/cky09002/SC2002-Internship-Placement-System.git
   cd SC2002-Internship-Placement-System
   ```

2. **Choose your build method** (see [Build Options](#-build-options) above)

3. **Run the application**
   ```bash
   java -cp out Assignment.src.MainApp
   ```

4. **Login with default credentials** (see [Default Credentials](#-default-credentials))

---

## 🛠️ Troubleshooting

### Build Issues

**Problem:** "Cannot find symbol" errors during compilation  
**Solution:** Ensure all Java files are compiled together:
```bash
# Find and compile all files at once
find src -name "*.java" -type f > javafiles.txt
javac -d out -sourcepath src @javafiles.txt
```

**Problem:** "ClassNotFoundException" at runtime  
**Solution:** Verify the classpath includes the `out/` directory:
```bash
java -cp out Assignment.src.MainApp
```

### Runtime Issues

**Problem:** CSV files not found  
**Solution:** Ensure `sample_file/` directory exists in the project root and contains all required CSV files.

**Problem:** Application crashes on startup  
**Solution:** Check that CSV files are properly formatted and contain valid data.

---

## 📝 Project Structure

```
Assignment/
├── src/
│   ├── constant/          # Enums and constants
│   ├── controller/        # Business logic controllers
│   ├── model/            # Domain models
│   ├── utils/            # Utility classes (formatters, handlers)
│   ├── view/             # UI components
│   ├── test/             # Test suite
│   ├── InternshipApp.java
│   └── MainApp.java      # Entry point
├── sample_file/          # CSV data files
├── doc/                  # UML diagrams (VPP files)
├── Dockerfile           # Docker configuration
├── .gitignore          # Git ignore rules
└── README.md           # This file
```

---

## 📄 License

This project is created for educational purposes as part of SC2002 Object-Oriented Design and Programming course.

---

## 🔗 Repository Links

- **GitHub Repository**: https://github.com/cky09002/SC2002-Internship-Placement-System
- **Branch**: `feature/docker-or-cli-setup`
- **Issues**: Report issues via GitHub Issues
- **Actions**: View build status in GitHub Actions tab

---

## 📧 Contact

For questions or issues related to this project, please refer to the course documentation or contact the course instructor.

---

**Last Updated**: 2025 S1
