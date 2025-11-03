# Test Plan - Internship Management System

## Test Setup
1. Ensure all CSV files are loaded from `sample_file/` directory
2. Compile and run the application: `java -cp build src.MainApp`

---

## Test Cases

### **Test Case 1: Valid User Login**
**Expected**: User should access their dashboard based on their roles  
**Steps**:
1. Run the application
2. Select option 1 (Login)
3. Enter valid Student ID (e.g., from `sample_student_list.csv`)
4. Enter correct password
5. **Verify**: Student Dashboard appears with menu options

**Test for each role**:
- Student login
- Staff login  
- Company Representative login (must be approved)

---

### **Test Case 2: Invalid ID**
**Expected**: User receives notification about incorrect ID  
**Steps**:
1. Select option 1 (Login)
2. Enter invalid ID (e.g., "INVALID123")
3. Enter any password
4. **Verify**: Error message appears: "Error: [meaningful error message]"

---

### **Test Case 3: Incorrect Password**
**Expected**: System denies access and alerts user to incorrect password  
**Steps**:
1. Select option 1 (Login)
2. Enter valid Student ID
3. Enter incorrect password
4. **Verify**: Error message appears indicating incorrect password
5. **Verify**: User is NOT logged in

---

### **Test Case 4: Password Change Functionality**
**Expected**: System updates password, prompts re-login, allows login with new credentials  
**Steps**:
1. From main menu, select option 2 (Change Password)
2. Enter valid user ID
3. Enter current password
4. Enter new password
5. **Verify**: Success message appears
6. Logout (if logged in)
7. Login with new password
8. **Verify**: Login successful with new password

---

### **Test Case 5: Company Representative Account Creation**
**Expected**: Company Rep can only log in after approval by Career Center Staff  
**Steps**:
1. From main menu, select option 3 (Register as Company Representative)
2. Enter registration details (email, name, password, company, department, position)
3. **Verify**: Success message: "Registration successful! Your account is pending approval..."
4. Try to login with new Company Rep credentials
5. **Verify**: Login should fail (or show pending status)
6. As Staff user, approve the Company Representative
7. Try logging in again as Company Rep
8. **Verify**: Login successful after approval

---

### **Test Case 6: Internship Opportunity Visibility Based on User Profile and Toggle**
**Expected**: Internships visible based on year of study, major, level eligibility, and visibility setting  
**Steps**:
1. Login as Student
2. Select option 1 (List available internships)
3. **Verify**: Only internships matching:
   - Student's major (e.g., Computer Science student sees CS internships)
   - Student's year level (Basic for Year 1, Intermediate for Year 2+, Advanced for Year 3+)
   - Visibility = true
   - Status = APPROVED
   - Within open/close date range
4. As Company Rep, toggle visibility OFF for an internship
5. As Student, list internships again
6. **Verify**: Internship with visibility OFF is NOT visible

---

### **Test Case 7: Internship Application Eligibility**
**Expected**: Students can only apply for eligible internships  
**Steps**:
1. Login as Student
2. List available internships (should only show eligible ones)
3. Select option 2 (Create application)
4. Try to apply for an internship ID from the visible list
5. **Verify**: Application created successfully
6. **Note**: Since students only see eligible internships, this should always work

---

### **Test Case 8: Viewing Application Status after Visibility Toggle Off**
**Expected**: Students can access their application details even if internship visibility is toggled off  
**Steps**:
1. Login as Student
2. Create an application for an internship
3. Note the application ID
4. Logout and login as Company Rep (creator of that internship)
5. Toggle internship visibility OFF
6. Logout and login as Student
7. Select option 3 (View my applications)
8. **Verify**: Application details are still visible
9. **Verify**: Application shows correct internship details even though visibility is OFF

---

### **Test Case 10: Single Internship Placement Acceptance per Student**
**Expected**: Accepting one placement automatically withdraws all other applications  
**Steps**:
1. Login as Student
2. Create multiple applications (apply for 2-3 different internships)
3. List all applications (option 3) and note all application IDs
4. Login as Company Rep for one internship
5. Approve/confirm one of the student's applications
6. Logout and login as Student
7. Select option 5 (Accept a successful application)
8. Enter the application ID for the confirmed placement
9. **Verify**: Success message
10. View applications again (option 3)
11. **Verify**: Other applications are automatically withdrawn/status changed

---

### **Test Case 13: Company Representative Internship Opportunity Creation**
**Expected**: Company Reps can create internships meeting system requirements  
**Steps**:
1. Login as approved Company Representative
2. Select option 2 (Create new internship)
3. Enter valid data:
   - Title, Description
   - Level (Basic/Intermediate/Advanced)
   - Preferred Major
   - Open Date (YYYY-MM-DD)
   - Close Date (YYYY-MM-DD)
   - Number of slots (positive integer)
4. **Verify**: Success message with full internship details
5. **Verify**: Internship status = PENDING
6. Try creating with invalid data (e.g., close date before open date)
7. **Verify**: Error message appears

---

### **Test Case 14: Internship Opportunity Approval Status**
**Expected**: Company Reps can view pending, approved, or rejected status  
**Steps**:
1. Login as Company Representative
2. Select option 1 (List my internships)
3. **Verify**: Shows all internships created by this rep with current status
4. Create a new internship (should show PENDING)
5. As Staff, approve or reject the internship
6. As Company Rep, list internships again
7. **Verify**: Status updated correctly (APPROVED or REJECTED)

---

### **Test Case 15: Internship Detail Access for Company Representative**
**Expected**: Company Reps can always access full details of their own internships  
**Steps**:
1. Login as Company Representative
2. Create an internship
3. Toggle visibility OFF
4. Select option 1 (List my internships)
5. **Verify**: Can still see full details of own internship
6. Select option 3 (Edit internship)
7. Enter internship ID
8. **Verify**: Full details displayed even though visibility is OFF

---

### **Test Case 16: Restriction on Editing Approved Opportunities**
**Expected**: Edit functionality restricted for approved internships  
**Steps**:
1. Login as Company Representative
2. Create an internship
3. As Staff, approve the internship
4. As Company Rep, select option 3 (Edit internship)
5. Enter the approved internship ID
6. **Verify**: Error message: "Cannot edit approved internship!"
7. Try to delete (option 4)
8. **Verify**: Error message appears

---

### **Test Case 18: Student Application Management and Placement Confirmation**
**Expected**: Correct application retrieval, accurate slot updates, correct placement confirmation  
**Steps**:
1. Login as Company Representative
2. Create an internship with 10 slots
3. As Staff, approve it and toggle visibility ON
4. As Student, create applications (have 2-3 students apply)
5. Login as Company Rep, select option 6 (View applications)
6. **Verify**: Correct applications are shown
7. **Verify**: Slot count shows correct filled/available (e.g., 2/10 filled, 8 available)
8. Select option 7 (Confirm student placement)
9. Enter internship ID and application ID
10. **Verify**: Success message
11. View applications again
12. **Verify**: Confirmed application status updated
13. **Verify**: Slots updated (e.g., 3/10 filled, 7 available)

---

### **Test Case 19: Internship Placement Confirmation Status Update**
**Expected**: Placement confirmation status updates correctly  
**Steps**:
1. Login as Company Rep
2. Create and get an approved internship
3. As Student, apply for the internship
4. As Company Rep, confirm the placement (option 7)
5. As Student, view applications (option 3)
6. **Verify**: Application status shows as confirmed/successful
7. As Student, should be able to accept (option 5)

---

### **Test Case 20: Create, Edit, and Delete Internship Opportunity Listings**
**Expected**: Company Reps can create, edit (before approval), and delete opportunities  
**Steps**:
1. Login as Company Representative
2. **Create**: Select option 2, enter valid data, verify success
3. **Edit** (before approval):
   - Select option 3 (Edit internship)
   - Enter PENDING internship ID
   - Edit fields (selectively)
   - **Verify**: Updates successful
4. **Delete**:
   - Select option 4 (Delete internship)
   - Enter PENDING internship ID
   - **Verify**: Deletion successful
5. List internships again
6. **Verify**: Deleted internship no longer appears

---

### **Test Case 21: Career Center Staff Internship Opportunity Approval**
**Expected**: Staff can review and approve/reject internship opportunities  
**Steps**:
1. As Company Rep, create an internship
2. Logout and login as Staff
3. Select option 2 (Approve/reject internship listing)
4. **Verify**: Pending internship appears in list
5. Select internship ID
6. Choose approve (y) or reject (n)
7. **Verify**: Success message
8. Login as Company Rep
9. List internships
10. **Verify**: Status updated correctly
11. If approved, verify it becomes visible to eligible students

---

### **Test Case 22: Toggle Internship Opportunity Visibility**
**Expected**: Visibility changes reflected in student internship lists  
**Steps**:
1. As Company Rep, create an approved internship with visibility ON
2. As eligible Student, list internships
3. **Verify**: Internship appears in list
4. As Company Rep, select option 5 (Toggle visibility)
5. Select the internship
6. **Verify**: Success message
7. As Student, list internships again
8. **Verify**: Internship no longer appears (visibility OFF)
9. Toggle visibility ON again
10. As Student, list again
11. **Verify**: Internship reappears

---

## Test Data (from CSV files)

### Students:
| ID | Name | Major | Year | Password |
|----|------|-------|------|----------|
| U2310001A | Tan Wei Ling | Computer Science | 2 | password |
| U2310002B | Ng Jia Hao | Data Science & AI | 3 | password |
| U2310003C | Lim Yi Xuan | Computer Engineering | 4 | password |
| U2310004D | Chong Zhi Hao | Information Engineering & Media | 1 | password |
| U2310005E | Wong Shu Hui | Computer Science | 3 | password |

### Staff:
| ID | Name | Email | Password |
|----|------|-------|----------|
| sng001 | Dr. Sng Hui Lin | sng001@ntu.edu.sg | password |
| tan002 | Mr. Tan Boon Kiat | tan002@ntu.edu.sg | password |
| lee003 | Ms. Lee Mei Ling | lee003@ntu.edu.sg | password |

### Company Representatives:
| ID | Name | Company | Status | Password |
|----|------|---------|--------|----------|
| Kchong042@e.ntu.edu.sg | Chong Kok Yang | Facebook | Approved | password |
| wenxu@gmail.com | Cky | Tiktok | Pending | password |

### Sample Internship:
| ID | Title | Level | Major | Status | Visible |
|----|-------|-------|-------|--------|---------|
| 100000 | Software Engineer | Basic | Computer Science | APPROVED | true |

---

## Common Issues to Check

1. **Error Messages**: All should be meaningful and user-friendly
2. **State Persistence**: Changes should be saved to CSV files
3. **Data Validation**: Invalid inputs should be rejected with clear errors
4. **Access Control**: Users should only access/modify their own data (except Staff)
5. **Status Transitions**: Status changes should follow correct workflow

---

## Quick Test Checklist

- [ ] Valid login for all user types
- [ ] Invalid ID/Password error handling
- [ ] Password change works
- [ ] Company Rep registration and approval flow
- [ ] Internship visibility filtering for students
- [ ] Application eligibility checking
- [ ] Application details remain accessible after visibility toggle
- [ ] Single placement acceptance withdraws others
- [ ] Internship CRUD operations work
- [ ] Approval status updates correctly
- [ ] Company Rep can access own internships always
- [ ] Edit restrictions on approved internships
- [ ] Slot counting updates correctly
- [ ] Placement confirmation updates status
- [ ] Staff approval/rejection works
- [ ] Visibility toggle affects student view

---

## Quick Test Execution Guide

### Start Testing:
1. **Compile**: `javac -d build -cp build src/**/*.java`
2. **Run**: `java -cp build src.MainApp`

### Test Case 1 - Quick Login Test:
- **Student**: ID: `U2310001A`, Password: `password`
- **Staff**: ID: `sng001@ntu.edu.sg`, Password: `password`
- **Company Rep (Approved)**: ID: `Kchong042@e.ntu.edu.sg`, Password: `password`

### Test Case 2 - Invalid ID:
- Enter: ID: `INVALID999`, Password: `anything`
- **Expected**: "Error: Invalid user ID. Please try again."

### Test Case 3 - Wrong Password:
- Enter: ID: `U2310001A`, Password: `wrongpassword`
- **Expected**: "Error: Incorrect password. Please try again."

### Test Case 5 - Company Rep Registration → Approval Flow:
1. Main menu → Option 3 (Register)
2. Enter new Company Rep details (use new email)
3. Try to login → Should fail: "Error: Your account is pending approval..."
4. Login as Staff (`sng001@ntu.edu.sg`)
5. Option 1 (Approve/reject company representative)
6. Approve the new Company Rep
7. Try login again → Should succeed

### Test Case 6 - Visibility Filtering:
1. Login as Student (`U2310001A` - Computer Science, Year 2)
2. List internships → Should only show:
   - Major: Computer Science
   - Level: Basic (Year 2 eligible)
   - Status: APPROVED
   - Visible: true
   - Date range: current date within open/close dates
3. Login as Company Rep (`Kchong042@e.ntu.edu.sg`)
4. Option 5 (Toggle visibility) → Turn OFF
5. Login as Student → List again → Should NOT appear

### Test Case 10 - Single Acceptance Withdraws Others:
1. Login as Student → Create 2-3 applications
2. Note all application IDs
3. Login as Company Rep → Option 7 (Confirm placement)
4. Confirm one application (sets status to SUCCESSFUL)
5. Login as Student → Option 5 (Accept application)
6. Enter the confirmed application ID
7. View applications (Option 3)
8. **Verify**: 
   - Accepted application = SUCCESSFUL (remains SUCCESSFUL when student accepts)
   - All other applications = WITHDRAWN

### Test Case 16 - Edit Restriction:
1. Company Rep creates internship → Status: PENDING
2. Edit it → Should work
3. Staff approves it → Status: APPROVED
4. Company Rep tries to edit → Should fail: "Cannot edit approved internship!"

