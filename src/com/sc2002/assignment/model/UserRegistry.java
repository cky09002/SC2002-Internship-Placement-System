package com.sc2002.assignment.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Central database for all users in the system.
 * Uses the Singleton pattern - only one instance exists throughout the application.
 * Called from InternshipApp when loading users, and from controllers when looking up users.
 */
public class UserRegistry {
    private static UserRegistry instance;
    private final Map<String,User> users = new HashMap<>();
    
    private UserRegistry() {}
    
    /**
     * Get the single instance of UserRegistry.
     * Called from anywhere in the app that needs to access user data.
     */
    public static UserRegistry getInstance() {
        if (instance == null) {
            instance = new UserRegistry();
        }
        return instance;
    }

    /**
     * Add a new user to the registry.
     * Called from InternshipApp when loading users from CSV files.
     */
    public boolean register(User user){
        if(user == null || user.getUserID() == null) return false;
        if (users.containsKey(user.getUserID())) return false;
        users.put(user.getUserID(),user);
        return true;
    }

    /**
     * Find a user by their ID.
     * Called from LoginController during login and password changes.
     */
    public User findById(String userID){
        if (userID == null) return null;
        return users.get(userID);
    }
    
    /**
     * Get all company representatives in the system.
     * Called from StaffController to show the list of company reps for approval/rejection.
     */
    public java.util.List<CompanyRepresentative> getAllCompanyReps() {
        return users.values().stream()
                .filter(u -> u instanceof CompanyRepresentative)
                .map(u -> (CompanyRepresentative) u)
                .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * Save a newly registered company rep to the CSV file.
     * Called from LoginController when a new company rep registers in-app.
     */
    public void saveCompanyRepToCsv(CompanyRepresentative compRep) {
        try {
            FileWriter writer = new FileWriter("sample_file/sample_company_representative_list.csv", true); // append mode
            String status = compRep.getStatusStringForCsv();
            writer.write(String.format("%s,%s,%s,%s,%s,%s,%s,%s\n",
                compRep.getUserID(),
                compRep.getName(),
                compRep.getCompanyName(),
                compRep.getDepartment(),
                compRep.getPosition(),
                compRep.getEmail(),
                compRep.getPassword(),
                status));
            writer.close();
        } catch (IOException e) {
            System.out.println("Error saving company representative to CSV: " + e.getMessage());
        }
    }
    
    /**
     * Update a company rep's approval status in the CSV file.
     * Called from StaffController when staff approves or rejects a company rep account.
     */
    public void updateCompanyRepStatusInCsv(CompanyRepresentative compRep) {
        try {
            // Read the entire CSV file into memory
            BufferedReader reader = new BufferedReader(new FileReader("sample_file/sample_company_representative_list.csv"));
            List<String> lines = new ArrayList<>();
            String line = reader.readLine(); // header row
            lines.add(line);
            
            // Go through each row and update the matching company rep
            while ((line = reader.readLine()) != null) {
                String[] cols = line.split(",");
                if (cols.length >= 1 && cols[0].equals(compRep.getUserID())) {
                    // Found the company rep - replace with updated data
                    String updatedLine = String.format("%s,%s,%s,%s,%s,%s,%s,%s",
                        compRep.getUserID(),
                        compRep.getName(),
                        compRep.getCompanyName(),
                        compRep.getDepartment(),
                        compRep.getPosition(),
                        compRep.getEmail(),
                        compRep.getPassword(),
                        compRep.getStatusStringForCsv());
                    lines.add(updatedLine);
                } else {
                    lines.add(line);
                }
            }
            reader.close();
            
            // Write everything back to the file
            FileWriter writer = new FileWriter("sample_file/sample_company_representative_list.csv");
            for (String l : lines) {
                writer.write(l + "\n");
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Error updating company representative status in CSV: " + e.getMessage());
        }
    }
    
    /**
     * Save password changes to the appropriate CSV file.
     * Called from LoginController after a user successfully changes their password.
     */
    public void savePasswordChangeToCsv(User user) {
        String filename;
        if (user instanceof Student) {
            filename = "sample_file/sample_student_list.csv";
        } else if (user instanceof Staff) {
            filename = "sample_file/sample_staff_list.csv";
        } else if (user instanceof CompanyRepresentative) {
            filename = "sample_file/sample_company_representative_list.csv";
        } else {
            return;
        }
        
        updatePasswordInCsv(filename, user.getUserID(), user.getPassword());
    }
    
    /**
     * Helper method to update just the password column in a CSV file.
     * Finds the user by ID and only changes their password value.
     */
    private void updatePasswordInCsv(String filename, String userID, String newPassword) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            List<String> lines = new ArrayList<>();
            String line = reader.readLine(); // header row
            lines.add(line);
            
            while ((line = reader.readLine()) != null) {
                String[] cols = line.split(",");
                if (cols.length >= 1 && cols[0].equals(userID)) {
                    // Found the user - find which column is Password and update it
                    String[] header = lines.get(0).split(",");
                    int passwordCol = -1;
                    for (int i = 0; i < header.length; i++) {
                        if (header[i].equalsIgnoreCase("Password")) {
                            passwordCol = i;
                            break;
                        }
                    }
                    
                    if (passwordCol >= 0 && passwordCol < cols.length) {
                        cols[passwordCol] = newPassword;
                        line = String.join(",", cols);
                    }
                }
                lines.add(line);
            }
            reader.close();
            
            FileWriter writer = new FileWriter(filename);
            for (String l : lines) {
                writer.write(l + "\n");
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Error updating password in CSV: " + e.getMessage());
        }
    }
}