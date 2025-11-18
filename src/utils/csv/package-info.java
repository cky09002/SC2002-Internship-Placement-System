/**
 * CSV persistence layer for the Internship Placement System.
 * 
 * <p>This package handles all CSV file operations for data persistence:
 * <ul>
 *   <li>{@link utils.csv.CsvHandler} - Interface for converting objects to CSV format</li>
 *   <li>{@link utils.csv.ApplicationCsvHandler} - Implements CsvHandler&lt;Application&gt; for Application CSV conversion</li>
 *   <li>{@link utils.csv.InternshipCsvHandler} - Implements CsvHandler&lt;Internship&gt; for Internship CSV conversion</li>
 * </ul>
 * 
 * <p>Features:
 * <ul>
 *   <li>Automatic CSV escaping for commas and quotes</li>
 *   <li>Batch loading and saving operations</li>
 *   <li>Individual record updates</li>
 *   <li>Error handling for file I/O</li>
 * </ul>
 * 
 * @author NTU SC2002 Group
 * @version 1.0
 * @since 2025-11-16
 */
package utils.csv;
