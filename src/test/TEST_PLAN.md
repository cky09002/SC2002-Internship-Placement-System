# Test Plan - Internship Management System

## Overview
This document outlines all 22 test cases for the Internship Management System. Test cases 1-8 are automated, while test cases 1-22 require manual verification.

**Run automated tests:**
```bash
java -cp bin test.TestRunner
```

**Run application for manual testing:**
```bash
java -cp bin MainApp
```

---

## Automated Tests (Test Cases 1-8)

These tests are automatically executed by `TestRunner.java`:

1. **CSV File Loading** - Verifies CSV files are loaded correctly
2. **User Creation** - Tests UserFactory for creating users from CSV
3. **Validation** - Tests ValidationHelper for email and field validation
4. **UserRegistry Singleton** - Verifies singleton pattern implementation
5. **Application Creation** - Tests Application object creation
6. **Internship Creation** - Tests Internship object creation
7. **Status Transitions** - Verifies enum values for status types
8. **Filter Settings** - Tests FilterSettings functionality

---

## Manual Test Cases (Test Cases 1-22)

### Test Case 1: Valid User Login
**Expected:** User can access dashboard based on role

**Steps:**
1. Launch app → Select option 1 (Login)
2. Login as student → Verify student dashboard
3. Logout → Login as staff → Verify staff dashboard
4. Logout → Login as approved company rep → Verify company rep dashboard

**Failure:** Cannot log in, wrong dashboard, or incorrect menu options

---

### Test Case 2: Invalid ID
**Expected:** Error message for invalid ID

**Steps:**
1. Launch app → Select option 1 (Login)
2. Enter invalid ID (e.g., 'INVALID123') + any password
3. Verify: Error "Invalid user ID. Please try again."
4. Verify: Remains on login screen

**Failure:** Login succeeds with invalid ID, or error message unclear

---

### Test Case 3: Incorrect Password
**Expected:** Error message for incorrect password

**Steps:**
1. Launch app → Select option 1 (Login)
2. Enter valid user ID + wrong password
3. Verify: Error "Incorrect password. Please try again."
4. Verify: Not logged in

**Failure:** Login succeeds with wrong password, or error message unclear

---

### Test Case 4: Password Change Functionality
**Expected:** Password updates, requires re-login with new password

**Steps:**
1. Login with valid credentials
2. Select password change (in profile/settings)
3. Enter current password + new password
4. Verify: Password change confirmation
5. Logout
6. Try old password → Should fail
7. Try new password → Should succeed

**Failure:** Password not updated, new password doesn't work, or no re-login prompt

---

### Test Case 5: Company Representative Account Creation
**Expected:** New company rep can only login after staff approval

**Steps:**
1. Launch app → Select option 3 (Register)
2. Register new company rep with valid details
3. Verify: Registration success
4. Try to login with new credentials
5. Verify: Error "Your account is pending approval..."
6. Login as staff → Navigate to pending approvals
7. Approve the company rep
8. Logout → Login as company rep again
9. Verify: Login succeeds

**Failure:** Company rep can login without approval, or approval process fails

---

### Test Case 6: Internship Opportunity Visibility Based on User Profile and Toggle
**Expected:** Students see only eligible internships (matching major/level) when visible

**Steps:**
1. Login as student (e.g., Year 2, Computer Science)
2. View internships → Verify: Only matching internships shown
3. Login as staff → Toggle visibility OFF for an internship
4. Login as student → View internships
5. Verify: Toggled internship not visible
6. Toggle visibility back ON as staff
7. Verify: Internship reappears for eligible students

**Failure:** Students see wrong major/level internships, or visibility toggle doesn't work

---

### Test Case 7: Internship Application Eligibility
**Expected:** Students can only apply for relevant internships when visible

**Steps:**
1. Login as student (Year 2, Computer Science)
2. Try to apply for different major → Should fail
3. Try to apply for Intermediate/Advanced level as Year 2 → Should fail
4. Try to apply for Basic level matching major → Should succeed
5. Login as staff → Toggle visibility OFF for an internship
6. Login as student → Try to apply → Should fail
7. Toggle visibility ON → Verify: Can apply

**Failure:** Students can apply for wrong major/level, or when visibility is off

---

### Test Case 8: Viewing Application Status after Visibility Toggle Off
**Expected:** Students can still access their application details even if visibility is off

**Steps:**
1. Login as student → Apply for eligible internship
2. Verify: Application visible in student's list
3. Note application ID
4. Login as staff → Toggle visibility OFF for that internship
5. Login as student → View applications
6. Verify: Application still accessible with all details

**Failure:** Application becomes inaccessible when visibility toggled off

---

### Test Case 10: Single Internship Placement Acceptance per Student
**Expected:** Accepting one placement automatically withdraws all other applications

**Steps:**
1. Login as student → Apply for multiple internships (up to 3)
2. Verify: All applications PENDING
3. As company rep (or staff), accept one application
4. Login as student → View application status
5. Verify: One shows ACCEPTED, others show WITHDRAWN
6. Try to apply for another → Should fail (already accepted)

**Failure:** Student can accept multiple placements, or other applications remain active

---

### Test Case 13: Company Representative Internship Opportunity Creation
**Expected:** Company reps can create opportunities only with valid data and within limits

**Steps:**
1. Login as approved company rep
2. Try to create with invalid data → Verify: Error messages
3. Create valid internship → Verify: Created with PENDING status
4. Check if at maximum opportunities
5. Try to create when at maximum → Should fail with error

**Failure:** Invalid data accepted, or exceeds maximum without error

---

### Test Case 14: Internship Opportunity Approval Status
**Expected:** Company reps can view pending/approved/rejected status updates

**Steps:**
1. Login as company rep → Create internship
2. Verify: Status shows PENDING
3. Login as staff → Approve internship
4. Login as company rep → View internships
5. Verify: Status updated to APPROVED
6. Repeat but reject instead → Verify: Status shows REJECTED

**Failure:** Status not visible, incorrect, or not saved properly

---

### Test Case 15: Internship Detail Access for Company Representative
**Expected:** Company reps can always access full details of their own internships

**Steps:**
1. Login as company rep → Create internship
2. View full details → Should be accessible
3. Login as staff → Toggle visibility OFF
4. Login as company rep → View own internships
5. Verify: Full details still accessible with all fields visible

**Failure:** Details become inaccessible or incomplete when visibility off

---

### Test Case 16: Restriction on Editing Approved Opportunities
**Expected:** Company reps cannot edit opportunities after approval

**Steps:**
1. Login as company rep → Create internship
2. Verify: Edit option available (PENDING status)
3. Edit details → Verify: Changes saved
4. Login as staff → Approve internship
5. Login as company rep → View approved internship
6. Verify: Edit option not available/disabled
7. Try to edit through other means → Should fail

**Failure:** Company rep can edit approved opportunities

---

### Test Case 18: Student Application Management and Placement Confirmation
**Expected:** Company reps retrieve correct applications, update slots, confirm placements

**Steps:**
1. Login as student → Apply for internship
2. Login as company rep (who created internship)
3. View applications for internship
4. Verify: Student application listed with correct details
5. Note current slot availability
6. Accept student application
7. Verify: Slot availability decreases by 1
8. Confirm placement
9. Verify: Placement confirmation status updated
10. Verify: Student's application shows ACCEPTED

**Failure:** Incorrect application retrieval, slot count wrong, or placement details not recorded

---

### Test Case 19: Internship Placement Confirmation Status Update
**Expected:** Placement confirmation status updates correctly

**Steps:**
1. Login as company rep → View applications for internship
2. Accept student's application
3. Confirm placement
4. Verify: Status changes to CONFIRMED
5. View application details again → Verify: Status persisted
6. Login as student → View application status
7. Verify: Shows ACCEPTED and placement confirmed

**Failure:** Status not updated or incorrect

---

### Test Case 20: Create, Edit, and Delete Internship Opportunity Listings
**Expected:** Company reps can create, edit (before approval), and delete opportunities

**Steps:**
1. Login as company rep → Create new opportunity
2. Verify: Created with all details
3. Edit opportunity (while PENDING) → Verify: Changes saved
4. Delete opportunity → Verify: Removed from system
5. Create another → Get it approved by staff
6. Try to edit → Should fail (from Test Case 16)
7. Try to delete approved → Verify behavior (restricted or allowed)

**Failure:** Cannot create/edit/delete, or errors during operations

---

### Test Case 21: Career Center Staff Internship Opportunity Approval
**Expected:** Staff can review and approve/reject submitted opportunities

**Steps:**
1. Login as company rep → Create internship
2. Login as staff → Navigate to pending internships
3. Verify: Submitted internship visible with all details
4. Review details → Approve
5. Verify: Status changes to APPROVED
6. Verify: Internship becomes visible to eligible students
7. Create another internship as company rep
8. Login as staff → Reject it
9. Verify: Status changes to REJECTED
10. Verify: Rejected internship not visible to students

**Failure:** Cannot access submissions, approval/rejection fails, or status not updated

---

### Test Case 22: Toggle Internship Opportunity Visibility
**Expected:** Visibility changes reflect accurately in student's opportunity list

**Steps:**
1. Login as staff → Ensure internship is approved and visible
2. Login as eligible student → View internships
3. Note visible internships
4. Login as staff → Toggle visibility OFF for one internship
5. Login as student → View internships
6. Verify: Toggled internship no longer in list
7. Toggle visibility back ON
8. Verify: Internship reappears in student's list
9. Verify: Other internships remain unchanged

**Failure:** Visibility settings don't update or don't affect listing

---

## Test Summary

- **Automated Tests:** 8 test cases (run via TestRunner)
- **Manual Tests:** 22 test cases (require human interaction)
- **Total Test Cases:** 22

**Note:** Manual tests require running the application and following the steps above. Results should be verified against the expected behavior and failure indicators listed for each test case.

