package Assignment.src.utils;

import Assignment.src.model.*;
import Assignment.src.constant.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * CSV handler for Internship model.
 * Implements CsvHandler interface for consistency.
 */
public class InternshipCsvHandler implements CsvHandler {
    
    /**
     * Load all internships from CSV file.
     * Called from InternshipApp on startup.
     */
    public static void loadFromCsv() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("sample_file/sample_internships.csv"));
            String line = reader.readLine(); // skip header
            
            while ((line = reader.readLine()) != null && !line.trim().isEmpty()) {
                String[] cols = line.split(",");
                if (cols.length >= 12) {
                    int id = Integer.parseInt(cols[0]);
                    String title = cols[1];
                    String description = cols[2];
                    String level = cols[3];
                    String major = cols[4];
                    LocalDate openDate = LocalDate.parse(cols[5]);
                    LocalDate closeDate = LocalDate.parse(cols[6]);
                    String company = cols[7];
                    String creatorID = cols[8];
                    boolean visible = Boolean.parseBoolean(cols[9]);
                    int numSlots = Integer.parseInt(cols[10]);
                    int filledSlots = Integer.parseInt(cols[11]);
                    InternshipStatus status = InternshipStatus.valueOf(cols[12]);
                    
                    // Find the creator (CompanyRepresentative)
                    CompanyRepresentative creator = 
                        (CompanyRepresentative) UserRegistry.getInstance().findById(creatorID);
                    if (creator == null) {
                        System.out.println("Warning: Creator not found for internship " + id + ", skipping.");
                        continue;
                    }
                    
                    // Create internship using helper method
                    Internship internship = Internship.createForCsv(id, title, description, level, major,
                                                                   openDate, closeDate, company, creator, 
                                                                   numSlots, visible, status, filledSlots);
                    
                    // Add to list
                    Internship.getInternshipsList().add(internship);
                }
            }
            reader.close();
            
            // Update nextID to continue from max ID after loading
            List<Internship> loaded = Internship.getInternshipsList();
            if (!loaded.isEmpty()) {
                int maxID = loaded.stream().mapToInt(Internship::getID).max().getAsInt();
                Internship.setNextID(maxID + 1);
            }
            // If no internships loaded, nextID remains at default 100000
        } catch (IOException e) {
            System.out.println("Error loading internships: " + e.getMessage());
        }
    }
    
    /**
     * Save internship to CSV file.
     * Called when internship is created, updated, or status changes.
     */
    public static void saveToCsv(Internship internship) {
        try {
            // Read all existing internships
            BufferedReader reader = new BufferedReader(new FileReader("sample_file/sample_internships.csv"));
            List<String> lines = new ArrayList<>();
            String line = reader.readLine(); // header
            lines.add(line);
            
            boolean found = false;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    String[] cols = line.split(",");
                    if (cols.length > 0 && Integer.parseInt(cols[0]) == internship.getID()) {
                        // Update this line
                        lines.add(formatCsvLine(internship));
                        found = true;
                    } else {
                        lines.add(line);
                    }
                }
            }
            
            if (!found) {
                // New internship, append
                lines.add(formatCsvLine(internship));
            }
            
            reader.close();
            
            // Write back
            FileWriter writer = new FileWriter("sample_file/sample_internships.csv");
            for (String l : lines) {
                writer.write(l + "\n");
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Error saving internship to CSV: " + e.getMessage());
        }
    }
    
    /**
     * Format internship data as CSV line.
     */
    private static String formatCsvLine(Internship internship) {
        return String.format("%d,%s,%s,%s,%s,%s,%s,%s,%s,%s,%d,%d,%s",
                internship.getID(), 
                CsvHandler.escapeCSV(internship.getTitle()), 
                CsvHandler.escapeCSV(internship.getDescription()), 
                internship.getLevel(), 
                internship.getPreferredMajor(), 
                internship.getOpenDate(), 
                internship.getCloseDate(), 
                internship.getCompanyName(), 
                internship.getCreator().getUserID(),
                internship.isVisiblePrivate(), 
                internship.getNumSlots(), 
                internship.getFilledSlots(), 
                internship.getStatus());
    }
    
    // Use CsvHandler.escapeCSV() for consistency
}

