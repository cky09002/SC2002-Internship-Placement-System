# Sequence Diagram - Company Representative Registration Process

## Overview
This sequence diagram shows the complete flow of a Company Representative registration process, including registration, approval by Staff, and login capability.

## Sequence Diagram (Text Representation)

```
┌─────────────────────────────────────────────────────────────────────────────┐
│              COMPANY REPRESENTATIVE REGISTRATION PROCESS                    │
└─────────────────────────────────────────────────────────────────────────────┘

┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│  LoginView   │  │ LoginControl │  │ UserRegistry │  │CompanyRep    │  │  StaffView   │
│              │  │    ler       │  │  (Singleton) │  │   (Model)    │  │              │
└──────┬───────┘  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘
       │                 │                  │                 │                 │
       │                 │                  │                 │                 │
   [User wants to register as Company Rep]
       │                 │                  │                 │                 │
       │ 1. displayRegistrationForm()      │                 │                 │
       │────────────────>│                  │                 │                 │
       │                 │                  │                 │                 │
       │ 2. getRegistrationInput()         │                 │                 │
       │<────────────────│                  │                 │                 │
       │                 │                  │                 │                 │
       │ 3. registerCompanyRepresentative( │                 │                 │
       │    email, name, password,         │                 │                 │
       │    company, dept, position)       │                 │                 │
       │────────────────>│                  │                 │                 │
       │                 │                  │                 │                 │
       │                 │ 4. validateEmail(email)            │                 │
       │                 │─────────────────────────────────────────────────────────>│
       │                 │                  │                 │                 │
       │                 │ 5. findById(email)                 │                 │
       │                 │────────────────>│                 │                 │
       │                 │                  │                 │                 │
       │                 │ 6. user not found                 │                 │
       │                 │<────────────────│                 │                 │
       │                 │                  │                 │                 │
       │                 │ 7. new CompanyRepresentative(      │                 │
       │                 │    email, name, password, email,   │                 │
       │                 │    company, dept, position,       │                 │
       │                 │    "Pending")                      │                 │
       │                 │───────────────────────────────────>│                 │
       │                 │                  │                 │                 │
       │                 │ 8. CompanyRep created with        │                 │
       │                 │    status = PENDING               │                 │
       │                 │<───────────────────────────────────│                 │
       │                 │                  │                 │                 │
       │                 │ 9. register(compRep)              │                 │
       │                 │────────────────>│                 │                 │
       │                 │                  │                 │                 │
       │                 │ 10. users.put(email, compRep)    │                 │
       │                 │<────────────────│                 │                 │
       │                 │                  │                 │                 │
       │                 │ 11. saveCompanyRepToCsv(compRep)  │                 │
       │                 │────────────────>│                 │                 │
       │                 │                  │                 │                 │
       │                 │ 12. write to CSV file            │                 │
       │                 │                  │                 │                 │
       │                 │ 13. return true                   │                 │
       │                 │<────────────────│                 │                 │
       │                 │                  │                 │                 │
       │ 14. Registration successful!      │                 │                 │
       │     Pending approval message       │                 │                 │
       │<────────────────│                  │                 │                 │
       │                 │                  │                 │                 │
       │                 │                  │                 │                 │
   [Staff logs in and views pending Company Reps]
       │                 │                  │                 │                 │
       │                 │                  │                 │                 │
       │                 │                  │                 │                 │
       │                 │                  │                 │                 │
       │                 │                  │                 │                 │
       │ 15. displayPendingCompanyReps()   │                 │                 │
       │──────────────────────────────────────────────────────────────────────>│
       │                 │                  │                 │                 │
       │ 16. getPendingCompanyReps()        │                 │                 │
       │────────────────>│                  │                 │                 │
       │                 │                  │                 │                 │
       │                 │ 17. getAllCompanyReps()          │                 │
       │                 │────────────────>│                 │                 │
       │                 │                  │                 │                 │
       │                 │ 18. filter(!isApproved())         │                 │
       │                 │                  │                 │                 │
       │                 │ 19. return List<CompanyRep>       │                 │
       │                 │<────────────────│                 │                 │
       │                 │                  │                 │                 │
       │ 20. return list                   │                 │                 │
       │<────────────────│                  │                 │                 │
       │                 │                  │                 │                 │
       │ 21. display list                  │                 │                 │
       │──────────────────────────────────────────────────────────────────────>│
       │                 │                  │                 │                 │
   [Staff selects a Company Rep and approves]
       │                 │                  │                 │                 │
       │ 22. approveRejectCompanyRep(repID,│                 │                 │
       │     true)                          │                 │                 │
       │────────────────>│                  │                 │                 │
       │                 │                  │                 │                 │
       │                 │ 23. approveRejectCompanyRep(      │                 │
       │                 │     repID, true)                  │                 │
       │                 │────────────────>│                  │                 │
       │                 │                  │                 │                 │
       │                 │ 24. findById(repID)               │                 │
       │                 │────────────────>│                 │                 │
       │                 │                  │                 │                 │
       │                 │ 25. return CompanyRep             │                 │
       │                 │<────────────────│                 │                 │
       │                 │                  │                 │                 │
       │                 │ 26. setApproved(true)              │                 │
       │                 │───────────────────────────────────>│                 │
       │                 │                  │                 │                 │
       │                 │ 27. status = APPROVED              │                 │
       │                 │                  │                 │                 │
       │                 │ 28. updateCompanyRepStatusInCsv(  │                 │
       │                 │     compRep)                       │                 │
       │                 │────────────────>│                 │                 │
       │                 │                  │                 │                 │
       │                 │ 29. update CSV file              │                 │
       │                 │                  │                 │                 │
       │                 │ 30. return                       │                 │
       │                 │<────────────────│                 │                 │
       │                 │                  │                 │                 │
       │ 31. return      │                  │                 │                 │
       │<────────────────│                  │                 │                 │
       │                 │                  │                 │                 │
   [Company Rep attempts to login]
       │                 │                  │                 │                 │
       │ 32. authenticate(email, password) │                 │                 │
       │────────────────>│                  │                 │                 │
       │                 │                  │                 │                 │
       │                 │ 33. findById(email)               │                 │
       │                 │────────────────>│                 │                 │
       │                 │                  │                 │                 │
       │                 │ 34. return CompanyRep             │                 │
       │                 │<────────────────│                 │                 │
       │                 │                  │                 │                 │
       │                 │ 35. verifyPassword(password)     │                 │
       │                 │───────────────────────────────────>│                 │
       │                 │                  │                 │                 │
       │                 │ 36. password verified            │                 │
       │                 │<───────────────────────────────────│                 │
       │                 │                  │                 │                 │
       │                 │ 37. isApproved()                  │                 │
       │                 │───────────────────────────────────>│                 │
       │                 │                  │                 │                 │
       │                 │ 38. return true                  │                 │
       │                 │<───────────────────────────────────│                 │
       │                 │                  │                 │                 │
       │                 │ 39. setLoggedIn(true)             │                 │
       │                 │───────────────────────────────────>│                 │
       │                 │                  │                 │                 │
       │ 40. login successful, redirect   │                 │                 │
       │     to CompanyRepDashboard        │                 │                 │
       │<────────────────│                  │                 │                 │
       │                 │                  │                 │                 │
```

## Alternative Flow: Rejection

If Staff rejects the Company Representative:

```
[Staff rejects Company Rep]
       │                 │                  │                 │                 │
       │ 22. approveRejectCompanyRep(repID,│                 │                 │
       │     false)                         │                 │                 │
       │────────────────>│                  │                 │                 │
       │                 │                  │                 │                 │
       │                 │ 23. approveRejectCompanyRep(      │                 │
       │                 │     repID, false)                 │                 │
       │                 │────────────────>│                  │                 │
       │                 │                  │                 │                 │
       │                 │ 24. findById(repID)               │                 │
       │                 │────────────────>│                 │                 │
       │                 │                  │                 │                 │
       │                 │ 25. return CompanyRep             │                 │
       │                 │<────────────────│                 │                 │
       │                 │                  │                 │                 │
       │                 │ 26. setApproved(false)            │                 │
       │                 │───────────────────────────────────>│                 │
       │                 │                  │                 │                 │
       │                 │ 27. status = REJECTED             │                 │
       │                 │                  │                 │                 │
       │                 │ 28. updateCompanyRepStatusInCsv(  │                 │
       │                 │     compRep)                       │                 │
       │                 │────────────────>│                 │                 │
       │                 │                  │                 │                 │
       │                 │ 29. update CSV file               │                 │
       │                 │                  │                 │                 │
       │                 │ 30. return                       │                 │
       │                 │<────────────────│                 │                 │
       │                 │                  │                 │                 │
       │ 31. return      │                  │                 │                 │
       │<────────────────│                  │                 │                 │
```

## Key Interactions

### 1. Registration Phase
- **LoginView** → **LoginController**: Initiates registration
- **LoginController** → **UserRegistry**: Checks if email exists
- **LoginController** → **CompanyRepresentative**: Creates new instance with PENDING status
- **UserRegistry**: Stores in memory and CSV file

### 2. Approval Phase
- **StaffView** → **StaffController**: Requests pending company reps
- **StaffController** → **LoginController**: Gets company rep list
- **LoginController** → **UserRegistry**: Retrieves and filters pending reps
- **Staff** approves/rejects → **CompanyRepresentative**: Status updated
- **UserRegistry**: Updates CSV file

### 3. Login Phase
- **LoginView** → **LoginController**: Authentication attempt
- **LoginController** → **UserRegistry**: Finds company rep
- **LoginController** → **CompanyRepresentative**: Checks approval status
- If approved: Login successful, else: Login denied

## Notes

1. **Singleton Pattern**: UserRegistry ensures single instance across system
2. **Status Management**: Company Rep status transitions: PENDING → APPROVED/REJECTED
3. **CSV Persistence**: All changes persisted to CSV file immediately
4. **Validation**: Email validation and duplicate check before registration
5. **Security**: Only approved Company Reps can login after registration

