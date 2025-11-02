package Assignment.src.model;

import java.util.ArrayList;
import java.util.List;

public class Report {
    private String criteria;
    private List<String> summaryLines;

    public Report(String criteria) {
        this.criteria = criteria;
        this.summaryLines = new ArrayList<>();
    }

    public void generateSummary() {
        summaryLines.add("=== Report Summary ===");
        summaryLines.add("Criteria: " + criteria);
        summaryLines.add("Generated at: " + java.time.LocalDateTime.now());
        summaryLines.add("");
        summaryLines.add("Report generated successfully.");
        
        for (String line : summaryLines) {
            System.out.println(line);
        }
    }
}