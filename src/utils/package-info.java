/**
 * Utility classes and subpackages providing reusable services across the application.
 * 
 * <p>This package is organized into focused subpackages for better maintainability:
 * 
 * <h2>Subpackages:</h2>
 * <ul>
 *   <li>{@link utils.csv} - CSV serialization (CsvHandler interface, ApplicationCsvHandler, InternshipCsvHandler)</li>
 *   <li>{@link utils.formatter} - Display formatting (ApplicationFormatter, InternshipFormatter, UserFormatter, TableFormatter, FilterFormatter, ViewFormatter)</li>
 *   <li>{@link utils.filter} - Filtering and sorting (InternshipFilter, FilterSettings)</li>
 *   <li>{@link utils.validation} - Input validation (ValidationHelper)</li>
 *   <li>{@link utils.factory} - Object creation (UserFactory)</li>
 * </ul>
 * 
 * <h2>Design patterns:</h2>
 * <ul>
 *   <li>Strategy Pattern: Filter and formatter classes for flexible algorithms</li>
 *   <li>Factory Pattern: UserFactory for type-safe object creation</li>
 *   <li>Utility Pattern: Static methods for stateless operations</li>
 *   <li>Strategy Pattern: CsvHandler interface for pluggable CSV serialization strategies</li>
 * </ul>
 * 
 * <h2>Key features:</h2>
 * <ul>
 *   <li>All utility classes use private constructors to prevent instantiation</li>
 *   <li>MVC compliance through reflection-based operations</li>
 *   <li>Persistent filter state across menu navigation</li>
 *   <li>Consistent table formatting and pagination</li>
 *   <li>Centralized validation and error handling</li>
 * </ul>
 * 
 * @author NTU SC2002 Group
 * @version 1.0
 * @since 2025-11-16
 */
package utils;
