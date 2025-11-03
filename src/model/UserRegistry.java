package Assignment.src.model;

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
     * Get all users in the system.
     * Used for clearing application references when reloading from CSV.
     */
    public java.util.Collection<User> getAllUsers() {
        return new java.util.ArrayList<>(users.values());
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
            // Log error - models should not have UI output
            // Error will be handled by calling code (controller)
            throw new RuntimeException("Error saving company representative to CSV: " + e.getMessage(), e);
        }
    }
    
    /**
     * Update a company rep's approval status in the CSV file.
     * Called from StaffController when staff approves or rejects a company rep account.
     */
    public void updateCompanyRepStatusInCsv(CompanyRepresentative compRep) {
        String newLine = String.format("%s,%s,%s,%s,%s,%s,%s,%s", compRep.getUserID(), compRep.getName(),
            compRep.getCompanyName(), compRep.getDepartment(), compRep.getPosition(),
            compRep.getEmail(), compRep.getPassword(), compRep.getStatusStringForCsv());
        updateCsvLine("sample_file/sample_company_representative_list.csv", compRep.getUserID(), newLine);
    }
    
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
     * Save password changes to the appropriate CSV file.
     * Called from LoginController after a user successfully changes their password.
     */
    public void savePasswordChangeToCsv(User user) {
        String filename = user instanceof Student ? "sample_file/sample_student_list.csv" :
                          user instanceof Staff ? "sample_file/sample_staff_list.csv" :
                          user instanceof CompanyRepresentative ? "sample_file/sample_company_representative_list.csv" : null;
        if (filename != null) updatePasswordInCsv(filename, user.getUserID(), user.getPassword());
    }
    
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