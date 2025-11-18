package utils.csv;

import model.*;
import constant.ApplicationStatus;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * CSV handler for Application model.
 * Implements CsvHandler interface for object-to-CSV conversion.
 */
public class ApplicationCsvHandler implements CsvHandler<Application> {
    
    /** Singleton instance */
    private static final ApplicationCsvHandler INSTANCE = new ApplicationCsvHandler();
    
    private ApplicationCsvHandler() {}
    
    /**
     * Get singleton instance.
     * @return the singleton instance
     */
    public static ApplicationCsvHandler getInstance() {
        return INSTANCE;
    }
    
    /**
     * Convert Application object to CSV line.
     * @param application the application to convert
     * @return CSV formatted string
     */
    @Override
    public String formatCsvLine(Application application) {
        return String.format("%d,%d,%s,%s,%s,%s,%s",
                application.getId(), 
                application.getInternship().getID(), 
                application.getApplicant().getUserID(), 
                application.getDateApplied(), 
                application.getStatus(), 
                application.getPreviousStatus() != null ? application.getPreviousStatus() : "",
                application.getWithdrawalReason() != null ? application.getWithdrawalReason() : "");
    }
    
    /**
     * Load all applications from CSV file.
     * Called from controllers/repositories (MVC compliance).
     */
    @Override
    public void loadFromCsv() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("sample_file/sample_applications.csv"));
            String line = reader.readLine();
            
            while ((line = reader.readLine()) != null && !line.trim().isEmpty()) {
                try {
                    String[] cols = line.split(",");
                    if (cols.length >= 5) {
                        int id = Integer.parseInt(cols[0]);
                        int internshipID = Integer.parseInt(cols[1]);
                        String studentID = cols[2];
                        LocalDateTime dateApplied = LocalDateTime.parse(cols[3]);
                        String statusStr = cols[4].trim();
                        ApplicationStatus status;
                        try {
                            status = ApplicationStatus.valueOf(statusStr);
                        } catch (IllegalArgumentException e) {
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
                        Internship internship = Internship.findWithID(internshipID);
                        if (internship == null) {
                            System.out.println("Warning: Internship " + internshipID + " not found for application " + id + ", skipping.");
                            continue;
                        }
                        Student student = (Student) UserRegistry.getInstance().findById(studentID);
                        if (student == null) {
                            System.out.println("Warning: Student " + studentID + " not found for application " + id + ", skipping.");
                            continue;
                        }
                        Application application = Application.createForCsv(id, internship, student, dateApplied, status, previousStatus, withdrawalReason);
                        Application.getAllApplicationsList().add(application);
                        internship.getApplications().add(application);
                        student.getApplications().add(application);
                        int currentNextID = Application.getNextID();
                        if (id >= currentNextID) {
                            Application.setNextID(id + 1);
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Warning: Error loading application from line: " + line + " - " + e.getMessage());
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
     * @param application the application to save
     */
    @Override
    public void saveToCsv(Application application) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("sample_file/sample_applications.csv"));
            List<String> lines = new ArrayList<>();
            String line = reader.readLine();
            lines.add(line);
            boolean found = false;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    String[] cols = line.split(",");
                    if (cols.length > 0 && Integer.parseInt(cols[0]) == application.getId()) {
                        lines.add(INSTANCE.formatCsvLine(application));
                        found = true;
                    } else {
                        lines.add(line);
                    }
                }
            }
            if (!found) {
                lines.add(INSTANCE.formatCsvLine(application));
            }
            reader.close();
            FileWriter writer = new FileWriter("sample_file/sample_applications.csv");
            for (String l : lines) {
                writer.write(l + "\n");
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Error saving application to CSV: " + e.getMessage());
        }
    }
    
}

