/**
 * Input validation utilities.
 * 
 * <p>This package provides centralized validation logic:
 * <ul>
 *   <li>{@link utils.validation.ValidationHelper} - Utility class with validation methods</li>
 * </ul>
 * 
 * <p>Validation types:
 * <ul>
 *   <li>Email format validation (pattern matching)</li>
 *   <li>User ID format validation (alphanumeric with underscores)</li>
 *   <li>Non-empty string validation</li>
 *   <li>Numeric range validation</li>
 * </ul>
 * 
 * <p>Features:
 * <ul>
 *   <li>Consistent error messages</li>
 *   <li>IllegalArgumentException throwing for invalid input</li>
 *   <li>Reusable across controllers and models</li>
 * </ul>
 * 
 * @author NTU SC2002 Group
 * @version 1.0
 * @since 2025-11-16
 */
package utils.validation;
