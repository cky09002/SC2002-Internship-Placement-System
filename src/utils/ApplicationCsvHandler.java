package Assignment.src.utils;

import Assignment.src.model.Application;
import Assignment.src.model.Internship;
import Assignment.src.model.Student;
import Assignment.src.model.UserRegistry;
import Assignment.src.constant.ApplicationStatus;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * CSV handler for Application model.
 * Implements CsvHandler interface for consistency.
 */
public class ApplicationCsvHandler implements CsvHandler {
    
    /**
     * Load all applications from CSV file.
     * Called from InternshipApp on startup.
     */
    public static void loadFromCsv() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("sample_file/sample_applications.csv"));
            String line = reader.readLine(); // skip header
            
            while ((line = reader.readLine()) != null && !line.trim().isEmpty()) {
                try {
                    String[] cols = line.split(",");
                    if (cols.length >= 5) {
                        int id = Integer.parseInt(cols[0]);
                        int internshipID = Integer.parseInt(cols[1]);
                        String studentID = cols[2];
                        LocalDateTime dateApplied = LocalDateTime.parse(cols[3]);
                        
                        // Handle invalid status values (e.g., CONFIRMED should be SUCCESSFUL)
                        String statusStr = cols[4].trim();
                        ApplicationStatus status;
                        try {
                            status = ApplicationStatus.valueOf(statusStr);
                        } catch (IllegalArgumentException e) {
                            // Convert old CONFIRMED status to SUCCESSFUL
                            if ("CONFIRMED".equals(statusStr)) {
                                System.out.println("Warning: Converting deprecated CONFIRMED status to SUCCESSFUL for application " + id);
                                status = ApplicationStatus.SUCCESSFUL;
                            } else {
                                System.out.println("Warning: Invalid application status '" + statusStr + "' for application " + id + ", skipping.");
                                continue;
                            }
                        }
                        
                        ApplicationStatus previousStatus = cols.length >= 6 && !cols[5].isEmpty() 
                                ? (cols[5].trim().equals("CONFIRMED") ? ApplicationStatus.SUCCESSFUL : ApplicationStatus.valueOf(cols[5].trim())) : null;
                        
                        String withdrawalReason = cols.length >= 7 && !cols[6].isEmpty() ? cols[6] : null;
                        
                        // Find the internship
                        Internship internship = Internship.findWithID(internshipID);
                        if (internship == null) {
                            System.out.println("Warning: Internship " + internshipID + " not found for application " + id + ", skipping.");
                            continue;
                        }
                        
                        // Find the student
                        Student student = (Student) UserRegistry.getInstance().findById(studentID);
                        if (student == null) {
                            System.out.println("Warning: Student " + studentID + " not found for application " + id + ", skipping.");
                            continue;
                        }
                        
                        // Create application using helper method
                        Application application = Application.createForCsv(id, internship, student, dateApplied, status, previousStatus, withdrawalReason);
                        
                        // Add to all tracking lists (list is already cleared before this, so no duplicates)
                        Application.getAllApplicationsList().add(application);
                        internship.getApplications().add(application);
                        student.getApplications().add(application);
                        
                        // Update nextID to avoid collisions
                        int currentNextID = Application.getNextID();
                        if (id >= currentNextID) {
                            Application.setNextID(id + 1);
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Warning: Error loading application from line: " + line + " - " + e.getMessage());
                    // Continue loading other applications
                }
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("Error loading applications: " + e.getMessage());
        }
    }
    
    /**
     * Save application to CSV file.
     * Called when application is created or status changes.
     */
    public static void saveToCsv(Application application) {
        try {
            // Read all existing applications
            BufferedReader reader = new BufferedReader(new FileReader("sample_file/sample_applications.csv"));
            List<String> lines = new ArrayList<>();
            String line = reader.readLine(); // header
            lines.add(line);
            
            boolean found = false;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    String[] cols = line.split(",");
                    if (cols.length > 0 && Integer.parseInt(cols[0]) == application.getId()) {
                        // Update this line
                        lines.add(formatCsvLine(application));
                        found = true;
                    } else {
                        // Keep existing line
                        lines.add(line);
                    }
                }
            }
            
            if (!found) {
                // New application, append
                lines.add(formatCsvLine(application));
            }
            
            reader.close();
            
            // Write back
            FileWriter writer = new FileWriter("sample_file/sample_applications.csv");
            for (String l : lines) {
                writer.write(l + "\n");
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Error saving application to CSV: " + e.getMessage());
        }
    }
    
    /**
     * Format application data as CSV line.
     */
    private static String formatCsvLine(Application application) {
        return String.format("%d,%d,%s,%s,%s,%s,%s",
                application.getId(), 
                application.getInternship().getID(), 
                application.getApplicant().getUserID(), 
                application.getDateApplied(), 
                application.getStatus(), 
                application.getPreviousStatus() != null ? application.getPreviousStatus() : "",
                application.getWithdrawalReason() != null ? application.getWithdrawalReason() : "");
    }
}

