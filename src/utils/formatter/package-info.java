/**
 * Formatting utilities for display and presentation.
 * 
 * <p>This package provides formatters for converting domain objects into display-ready strings:
 * <ul>
 *   <li>{@link utils.formatter.Formatter} - Base interface for formatters</li>
 *   <li>{@link utils.formatter.ApplicationFormatter} - Format application data for tables</li>
 *   <li>{@link utils.formatter.InternshipFormatter} - Format internship data for tables</li>
 *   <li>{@link utils.formatter.UserFormatter} - Format user profiles</li>
 *   <li>{@link utils.formatter.TableFormatter} - Generic table layout and column width calculation</li>
 *   <li>{@link utils.formatter.FilterFormatter} - Format filter settings display</li>
 *   <li>{@link utils.formatter.ViewFormatter} - Common view formatting (headers, borders, text centering)</li>
 * </ul>
 * 
 * <p>Design pattern: Strategy pattern for flexible formatting algorithms.
 * 
 * <p>Features:
 * <ul>
 *   <li>Consistent table formatting with fixed column widths</li>
 *   <li>Pagination support</li>
 *   <li>MVC compliance through reflection-based formatting</li>
 *   <li>Reusable UI components (headers, borders, centered text)</li>
 * </ul>
 * 
 * @author NTU SC2002 Group
 * @version 1.0
 * @since 2025-11-16
 */
package utils.formatter;
