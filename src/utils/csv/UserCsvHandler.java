package utils.csv;

import model.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * CSV handler for User model persistence.
 * Handles saving and updating user data in CSV files.
 * Follows MVC pattern by separating persistence logic from model layer.
 * 
 * Implements UserCsvHandlerInterface to follow Dependency Inversion Principle.
 * 
 * @author NTU SC2002 Group
 * @version 1.0
 * @since 2025-11-16
 */
public class UserCsvHandler implements UserCsvHandlerInterface {
    
    /** Singleton instance */
    private static final UserCsvHandler INSTANCE = new UserCsvHandler();
    
    /**
     * Private constructor for singleton pattern.
     */
    private UserCsvHandler() {
        // Singleton pattern
    }
    
    /**
     * Get singleton instance.
     * @return the singleton instance
     */
    public static UserCsvHandler getInstance() {
        return INSTANCE;
    }
    
    /**
     * Save a newly registered company representative to the CSV file.
     * Appends the new company rep to the end of the file.
     * 
     * @param compRep the company representative to save
     * @throws RuntimeException if file I/O fails
     */
    @Override
    public void saveCompanyRepToCsv(CompanyRepresentative compRep) {
        try {
            FileWriter writer = new FileWriter("sample_file/sample_company_representative_list.csv", true); // append mode
            String status = compRep.getApprovalStatus().toCsvString();
            writer.write(String.format("%s,%s,%s,%s,%s,%s,%s,%s\n",
                compRep.getUserID(),
                compRep.getName(),
                compRep.getCompanyName(),
                compRep.getDepartment(),
                compRep.getPosition(),
                compRep.getEmail(),
                compRep.getPasswordForPersistence(),
                status));
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException("Error saving company representative to CSV: " + e.getMessage(), e);
        }
    }
    
    /**
     * Update a company representative's approval status in the CSV file.
     * Finds the line matching the user ID and replaces it with updated data.
     * 
     * @param compRep the company representative to update
     * @throws RuntimeException if file I/O fails
     */
    public void updateCompanyRepStatusInCsv(CompanyRepresentative compRep) {
        String newLine = String.format("%s,%s,%s,%s,%s,%s,%s,%s", 
            compRep.getUserID(), 
            compRep.getName(),
            compRep.getCompanyName(), 
            compRep.getDepartment(), 
            compRep.getPosition(),
            compRep.getEmail(), 
            compRep.getPasswordForPersistence(), 
            compRep.getApprovalStatus().toCsvString());
        updateCsvLine("sample_file/sample_company_representative_list.csv", compRep.getUserID(), newLine);
    }
    
    /**
     * Save password changes to the appropriate CSV file based on user type.
     * Uses polymorphism via getCsvFilename() method instead of instanceof checks.
     * 
     * @param user the user whose password changed
     * @throws RuntimeException if file I/O fails
     */
    @Override
    public void savePasswordChangeToCsv(User user) {
        String filename = user.getCsvFilename();
        updatePasswordInCsv(filename, user.getUserID(), user.getPasswordForPersistence());
    }
    
    /**
     * Updates a specific line in a CSV file.
     * Finds the line matching the user ID and replaces it with the new line content.
     * 
     * @param filename the CSV file path
     * @param userID the user ID to match
     * @param newLine the new line content
     * @throws RuntimeException if file I/O fails
     */
    private void updateCsvLine(String filename, String userID, String newLine) {
        try {
            List<String> lines = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
                lines.add(reader.readLine()); // header
                reader.lines().forEach(line -> lines.add(line.split(",")[0].equals(userID) ? newLine : line));
            }
            try (FileWriter writer = new FileWriter(filename)) {
                for (String l : lines) writer.write(l + "\n");
            }
        } catch (IOException e) {
            throw new RuntimeException("Error updating CSV: " + e.getMessage(), e);
        }
    }
    
    /**
     * Updates a user's password in a specific CSV file.
     * Finds the line matching the user ID and updates the Password column.
     * 
     * @param filename the CSV file path
     * @param userID the user ID to update
     * @param newPassword the new password
     * @throws RuntimeException if file I/O fails
     */
    private void updatePasswordInCsv(String filename, String userID, String newPassword) {
        try {
            List<String> lines = new ArrayList<>();
            String[] header = null;
            try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
                header = reader.readLine().split(",");
                int passwordCol = java.util.Arrays.asList(header).indexOf("Password");
                lines.add(String.join(",", header));
                reader.lines().forEach(line -> {
                    String[] cols = line.split(",");
                    if (cols.length > 0 && cols[0].equals(userID) && passwordCol >= 0 && passwordCol < cols.length) {
                        cols[passwordCol] = newPassword;
                        line = String.join(",", cols);
                    }
                    lines.add(line);
                });
            }
            try (FileWriter writer = new FileWriter(filename)) {
                for (String l : lines) writer.write(l + "\n");
            }
        } catch (IOException e) {
            throw new RuntimeException("Error updating password in CSV: " + e.getMessage(), e);
        }
    }
}

