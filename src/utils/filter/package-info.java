/**
 * Filtering and sorting utilities for internship searches.
 * 
 * <p>This package provides flexible filtering capabilities:
 * <ul>
 *   <li>{@link utils.filter.InternshipFilter} - Filter and sort internships by multiple criteria</li>
 *   <li>{@link utils.filter.FilterSettings} - Encapsulate filter configuration state</li>
 * </ul>
 * 
 * <p>Supported filters:
 * <ul>
 *   <li>Status filtering (PENDING, APPROVED, FILLED, REJECTED)</li>
 *   <li>Major matching</li>
 *   <li>Level filtering (Year 1-4)</li>
 *   <li>Date range filtering (posting and closing dates)</li>
 *   <li>Company name filtering</li>
 *   <li>Keyword search in titles</li>
 *   <li>Visibility filtering</li>
 * </ul>
 * 
 * <p>Supported sorting:
 * <ul>
 *   <li>Alphabetical by company name (default)</li>
 *   <li>By closing date</li>
 *   <li>By posting date</li>
 * </ul>
 * 
 * <p>Features:
 * <ul>
 *   <li>Persistent filter state across menu navigation</li>
 *   <li>Multiple filters applied simultaneously</li>
 *   <li>Clear/reset filter functionality</li>
 * </ul>
 * 
 * @author NTU SC2002 Group
 * @version 1.0
 * @since 2025-11-16
 */
package utils.filter;
