# Sequence Diagram - Company Representative Registration Flow

## Overview
Complete flow: Registration → Staff Approval → Login

---

## Sequence Diagram

```
┌─────────────────────────────────────────────────────────────────────────────┐
│              COMPANY REPRESENTATIVE REGISTRATION → APPROVAL → LOGIN          │
└─────────────────────────────────────────────────────────────────────────────┘

┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│  LoginView   │  │ LoginControl │  │ UserRegistry │  │CompanyRep    │  │  StaffView   │
│              │  │    ler       │  │  (Singleton) │  │   (Model)    │  │              │
└──────┬───────┘  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘
       │                 │                  │                 │                 │
═══════════════════════════════════════════════════════════════════════════════════
                    PHASE 1: REGISTRATION
═══════════════════════════════════════════════════════════════════════════════════
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
       │                 │                  │                 │                 │
       │                 │ 5. findById(email)                │                 │
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
       │                 │ 9. register(compRep)             │                 │
       │                 │────────────────>│                 │                 │
       │                 │                  │                 │                 │
       │                 │ 10. users.put(email, compRep)     │                 │
       │                 │<────────────────│                 │                 │
       │                 │                  │                 │                 │
       │                 │ 11. saveCompanyRepToCsv(compRep)  │                 │
       │                 │────────────────>│                 │                 │
       │                 │                  │                 │                 │
       │                 │ 12. write to CSV file             │                 │
       │                 │                  │                 │                 │
       │                 │ 13. return true                   │                 │
       │                 │<────────────────│                 │                 │
       │                 │                  │                 │                 │
       │ 14. Registration successful!      │                 │                 │
       │     Pending approval message       │                 │                 │
       │<────────────────│                  │                 │                 │
       │                 │                  │                 │                 │
═══════════════════════════════════════════════════════════════════════════════════
                    PHASE 2: STAFF APPROVAL
═══════════════════════════════════════════════════════════════════════════════════
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
       │ 22. approveRejectCompanyRep(repID,│                 │                 │
       │     true)                          │                 │                 │
       │────────────────>│                  │                 │                 │
       │                 │                  │                 │                 │
       │                 │ 23. findById(repID)               │                 │
       │                 │────────────────>│                 │                 │
       │                 │                  │                 │                 │
       │                 │ 24. return CompanyRep             │                 │
       │                 │<────────────────│                 │                 │
       │                 │                  │                 │                 │
       │                 │ 25. setApprovalStatus(APPROVED)   │                 │
       │                 │───────────────────────────────────>│                 │
       │                 │                  │                 │                 │
       │                 │ 26. status = APPROVED              │                 │
       │                 │                  │                 │                 │
       │                 │ 27. updateCompanyRepStatusInCsv(  │                 │
       │                 │     compRep)                       │                 │
       │                 │────────────────>│                 │                 │
       │                 │                  │                 │                 │
       │                 │ 28. update CSV file               │                 │
       │                 │                  │                 │                 │
       │                 │ 29. return                       │                 │
       │                 │<────────────────│                 │                 │
       │                 │                  │                 │                 │
       │ 30. return      │                  │                 │                 │
       │<────────────────│                  │                 │                 │
       │                 │                  │                 │                 │
═══════════════════════════════════════════════════════════════════════════════════
                    PHASE 3: LOGIN
═══════════════════════════════════════════════════════════════════════════════════
       │                 │                  │                 │                 │
       │ 31. authenticate(email, password) │                 │                 │
       │────────────────>│                  │                 │                 │
       │                 │                  │                 │                 │
       │                 │ 32. findById(email)               │                 │
       │                 │────────────────>│                 │                 │
       │                 │                  │                 │                 │
       │                 │ 33. return CompanyRep             │                 │
       │                 │<────────────────│                 │                 │
       │                 │                  │                 │                 │
       │                 │ 34. verifyPassword(password)     │                 │
       │                 │───────────────────────────────────>│                 │
       │                 │                  │                 │                 │
       │                 │ 35. password verified            │                 │
       │                 │<───────────────────────────────────│                 │
       │                 │                  │                 │                 │
       │                 │ 36. isApproved()                  │                 │
       │                 │───────────────────────────────────>│                 │
       │                 │                  │                 │                 │
       │                 │ 37. return true (APPROVED)        │                 │
       │                 │<───────────────────────────────────│                 │
       │                 │                  │                 │                 │
       │                 │ 38. setLoggedIn(true)             │                 │
       │                 │───────────────────────────────────>│                 │
       │                 │                  │                 │                 │
       │ 39. login successful, redirect   │                 │                 │
       │     to CompanyRepDashboard        │                 │                 │
       │<────────────────│                  │                 │                 │
       │                 │                  │                 │                 │
```

---

## Key Interactions

### Phase 1: Registration
1. **LoginView** → **LoginController**: Initiates registration
2. **LoginController** → **UserRegistry**: Checks if email exists
3. **LoginController** → **CompanyRepresentative**: Creates instance with `PENDING` status
4. **UserRegistry**: Stores in memory
5. **UserCsvHandler**: Saves to CSV file

### Phase 2: Approval
1. **StaffView** → **StaffController**: Requests pending company reps
2. **StaffController** → **LoginController**: Gets company rep list
3. **LoginController** → **UserRegistry**: Retrieves and filters pending reps
4. **Staff** approves → **CompanyRepresentative**: Status updated to `APPROVED`
5. **UserCsvHandler**: Updates CSV file

### Phase 3: Login
1. **LoginView** → **LoginController**: Authentication attempt
2. **LoginController** → **UserRegistry**: Finds company rep
3. **LoginController** → **CompanyRepresentative**: Verifies password and approval status
4. If `APPROVED`: Login successful, access dashboard
5. If `PENDING`/`REJECTED`: Login denied with error message

---

## Status Transitions

```
Registration:  [No Account] → [PENDING]
Approval:      [PENDING] → [APPROVED] or [REJECTED]
Login:         [APPROVED] → [Logged In]
               [PENDING/REJECTED] → [Login Denied]
```

---

## Design Patterns Used

1. **Singleton Pattern**: `UserRegistry` ensures single instance
2. **MVC Pattern**: Clear separation of concerns
3. **Dependency Inversion**: Controllers depend on interfaces
4. **Factory Pattern**: `UserFactory` creates User objects

---

**Last Updated**: 2025-11-18
