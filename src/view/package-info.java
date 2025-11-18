/**
 * Contains all view classes responsible for user interface presentation.
 * 
 * <p>This package implements the View layer of the MVC architecture:
 * <ul>
 *   <li>Handles all user interaction and display logic</li>
 *   <li>Never directly accesses model classes</li>
 *   <li>Communicates with models only through controllers</li>
 *   <li>Maintains strict MVC separation of concerns</li>
 * </ul>
 * 
 * <p>View hierarchy:
 * <ul>
 *   <li>{@link view.BaseView} - Abstract base class with common menu operations</li>
 *   <li>{@link view.StudentView} - Student-specific interface (browse, apply, view applications)</li>
 *   <li>{@link view.StaffView} - Staff-specific interface (approve internships, review applications)</li>
 *   <li>{@link view.CompanyRepresentativeView} - Company rep interface (create/edit internships)</li>
 *   <li>{@link view.LoginView} - Authentication interface</li>
 * </ul>
 * 
 * <p>Reusable components:
 * <ul>
 *   <li>{@link view.FilterMenu} - Interactive filter menu for searches</li>
 *   <li>{@link view.TableView} - Paginated table display with sorting</li>
 *   <li>{@link view.ViewFactory} - Factory for creating appropriate views based on user type</li>
 * </ul>
 * 
 * <p>MVC compliance:
 * <ul>
 *   <li>Views accept Object types instead of specific models</li>
 *   <li>Reflection used for type-safe operations without model dependencies</li>
 *   <li>All data access goes through controller methods</li>
 *   <li>Views only format and display data, never modify it directly</li>
 * </ul>
 * 
 * <p>User experience features:
 * <ul>
 *   <li>Pagination for large result sets</li>
 *   <li>Interactive filtering with live preview</li>
 *   <li>Formatted tables with consistent column widths</li>
 *   <li>Input validation with helpful error messages</li>
 * </ul>
 * 
 * @author NTU SC2002 Group
 * @version 1.0
 * @since 2025-11-16
 */
package view;
