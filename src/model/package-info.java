/**
 * Contains all domain model classes representing core business entities.
 * 
 * <p>This package defines the data structures and business entities for the Internship Placement System:
 * <ul>
 *   <li>{@link model.User} - Abstract base class for all user types</li>
 *   <li>{@link model.Student} - Student user with major and GPA attributes</li>
 *   <li>{@link model.Staff} - Staff user managing internship approvals</li>
 *   <li>{@link model.CompanyRepresentative} - Company representative managing internships</li>
 *   <li>{@link model.Internship} - Internship posting with requirements and status</li>
 *   <li>{@link model.Application} - Student application for internship</li>
 *   <li>{@link model.UserRegistry} - Singleton registry managing all users</li>
 * </ul>
 * 
 * <p>User hierarchy:
 * <pre>
 * User (abstract)
 *  ├── Student
 *  ├── Staff
 *  └── CompanyRepresentative
 * </pre>
 * 
 * <p>Design features:
 * <ul>
 *   <li>Immutable IDs: User and entity IDs cannot be changed after creation</li>
 *   <li>Validation: All setters validate input using ValidationHelper</li>
 *   <li>State Management: Status transitions controlled by enums</li>
 *   <li>Persistence: CSV-compatible with automatic saving</li>
 * </ul>
 * 
 * <p>Entity relationships:
 * <ul>
 *   <li>Student → Application (1:N) - A student can have multiple applications</li>
 *   <li>Internship → Application (1:N) - An internship can have multiple applications</li>
 *   <li>CompanyRepresentative → Internship (1:N) - A company rep manages multiple internships</li>
 * </ul>
 * 
 * @author NTU SC2002 Group
 * @version 1.0
 * @since 2025-11-16
 */
package model;
