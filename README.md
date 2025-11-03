# Internship Management System

**Course:** SC2002 Object-Oriented Design and Programming  
**Semester:** 2025S1

## Quick Start

### 1. Clone Repository
```bash
git clone <repository-url>
cd Assignment
```

### 2. Compile
```bash
javac -d build -sourcepath src src/MainApp.java src/**/*.java
```

### 3. Run
```bash
java -cp build Assignment.src.MainApp
```

### 4. Run Tests
```bash
java -cp build Assignment.src.test.TestRunner
```

## Requirements

- **Java:** JDK 11+
- **OS:** Windows/macOS/Linux
- **CSV Files:** Must be in `sample_file/` directory

## Default Credentials

All users use password: `password`

- **Student:** `U2310001A` / `password`
- **Staff:** `sng001` / `password`
- **Company Rep:** `Kchong042@e.ntu.edu.sg` / `password` (must be approved)

## Troubleshooting

**Build folder not found?** That's normal - it's excluded from git. Just compile (Step 2).

**Can't find symbol errors?** Compile all files:
```bash
find src -name "*.java" -print0 | xargs -0 javac -d build -sourcepath src
```

**CSV files missing?** Ensure `sample_file/` directory contains all CSV files.

## Documentation

- `UML_CLASS_DIAGRAM.md` - Class diagram
- `SEQUENCE_DIAGRAM_COMPANY_REP_REGISTRATION.md` - Sequence diagram
- `REPORT.md` - Design report
- `src/test/TEST_PLAN.md` - Test cases
