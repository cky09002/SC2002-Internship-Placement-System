/**
 * Contains all controller classes implementing business logic for the Internship Placement System.
 * 
 * <p>This package follows the Model-View-Controller (MVC) architecture pattern:
 * <ul>
 *   <li>Controllers act as intermediaries between View and Model layers</li>
 *   <li>Enforce business rules and validation</li>
 *   <li>Coordinate data flow between views and models</li>
 *   <li>Maintain separation of concerns</li>
 * </ul>
 * 
 * <p>Controller hierarchy:
 * <ul>
 *   <li>{@link controller.BaseUserController} - Abstract base class with shared functionality</li>
 *   <li>{@link controller.StudentController} - Manages student operations (browse, apply, withdraw)</li>
 *   <li>{@link controller.StaffController} - Manages staff operations (approve internships, review applications)</li>
 *   <li>{@link controller.CompanyRepresentativeController} - Manages company rep operations (create/edit internships)</li>
 *   <li>{@link controller.LoginController} - Handles authentication and registration</li>
 *   <li>{@link controller.ApplicationController} - Manages application CRUD operations</li>
 *   <li>{@link controller.InternshipController} - Manages internship CRUD operations</li>
 * </ul>
 * 
 * <p>Design principles:
 * <ul>
 *   <li>Single Responsibility: Each controller manages one domain area</li>
 *   <li>DRY: Common operations abstracted to BaseUserController</li>
 *   <li>Encapsulation: Views never directly access models</li>
 * </ul>
 * 
 * @author NTU SC2002 Group
 * @version 1.0
 * @since 2025-11-16
 */
package controller;
