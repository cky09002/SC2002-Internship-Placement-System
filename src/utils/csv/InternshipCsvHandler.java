package utils.csv;

import model.*;
import constant.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * CSV handler for Internship model.
 * Implements CsvHandler interface for object-to-CSV conversion.
 * Implements CsvDeletable for deletion operations.
 * 
 * Follows Single Responsibility Principle - only handles CSV persistence for Internship objects.
 * Business logic (finding related objects) is handled by controllers.
 */
public class InternshipCsvHandler implements CsvHandler<Internship>, CsvDeletable<Internship> {
    
    /**
     * Convert Internship object to CSV line.
     * @param internship the internship to convert
     * @return CSV formatted string
     */
    @Override
    public String formatCsvLine(Internship internship) {
        return String.format("%d,%s,%s,%s,%s,%s,%s,%s,%s,%s,%d,%d,%s",
                internship.getID(), 
                escapeCSV(internship.getTitle()), 
                escapeCSV(internship.getDescription()), 
                internship.getLevel(), 
                internship.getPreferredMajor(), 
                internship.getOpenDate(), 
                internship.getCloseDate(), 
                internship.getCompanyName(), 
                internship.getCreator().getUserID(),
                internship.getVisibleFlag(), 
                internship.getNumSlots(), 
                internship.getFilledSlots(), 
                internship.getStatus());
    }
    
    /** Singleton instance */
    private static final InternshipCsvHandler INSTANCE = new InternshipCsvHandler();
    
    private InternshipCsvHandler() {}
    
    /**
     * Get singleton instance.
     * @return the singleton instance
     */
    public static InternshipCsvHandler getInstance() {
        return INSTANCE;
    }
    
    /**
     * Load all internships from CSV file.
     * Called from controllers/repositories (MVC compliance).
     */
    @Override
    public void loadFromCsv() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("sample_file/sample_internships.csv"));
            String line = reader.readLine();
            
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
                    CompanyRepresentative creator = 
                        (CompanyRepresentative) UserRegistry.getInstance().findById(creatorID);
                    if (creator == null) {
                        System.out.println("Warning: Creator not found for internship " + id + ", skipping.");
                        continue;
                    }
                    Internship internship = Internship.createForCsv(id, title, description, level, major,
                                                                   openDate, closeDate, company, creator, 
                                                                   numSlots, visible, status, filledSlots);
                    Internship.getInternshipsList().add(internship);
                }
            }
            reader.close();
            List<Internship> loaded = Internship.getInternshipsList();
            if (!loaded.isEmpty()) {
                int maxID = loaded.stream().mapToInt(Internship::getID).max().getAsInt();
                Internship.setNextID(maxID + 1);
            }
        } catch (IOException e) {
            System.out.println("Error loading internships: " + e.getMessage());
        }
    }
    
    /**
     * Save internship to CSV file.
     * Called when internship is created, updated, or status changes.
     * @param internship the internship to save
     */
    @Override
    public void saveToCsv(Internship internship) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("sample_file/sample_internships.csv"));
            List<String> lines = new ArrayList<>();
            String line = reader.readLine();
            lines.add(line);
            boolean found = false;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    String[] cols = line.split(",");
                    if (cols.length > 0 && Integer.parseInt(cols[0]) == internship.getID()) {
                        lines.add(INSTANCE.formatCsvLine(internship));
                        found = true;
                    } else {
                        lines.add(line);
                    }
                }
            }
            if (!found) {
                lines.add(INSTANCE.formatCsvLine(internship));
            }
            reader.close();
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
     * Delete an internship from CSV file.
     * Removes the row matching the given internship ID.
     * 
     * @param id the ID of the internship to delete
     * @throws RuntimeException if file I/O fails
     */
    @Override
    public void deleteFromCsv(int id) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("sample_file/sample_internships.csv"));
            List<String> lines = new ArrayList<>();
            String line = reader.readLine();
            lines.add(line);
            
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    String[] cols = line.split(",");
                    if (cols.length > 0 && Integer.parseInt(cols[0]) != id) {
                        lines.add(line);
                    }
                }
            }
            reader.close();
            
            FileWriter writer = new FileWriter("sample_file/sample_internships.csv");
            for (String l : lines) {
                writer.write(l + "\n");
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Error deleting internship from CSV: " + e.getMessage());
        }
    }
    
}

