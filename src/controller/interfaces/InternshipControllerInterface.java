package controller.interfaces;

/**
 * Interface for all internship-related operations.
 * Extends smaller interfaces to follow Interface Segregation Principle.
 * Clients that need full functionality can depend on this interface.
 * Clients that only need specific operations can depend on the smaller interfaces.
 * 
 * @author NTU SC2002 Group
 * @version 1.0
 * @since 2025-11-16
 */
public interface InternshipControllerInterface extends InternshipReader, InternshipWriter, InternshipValidator {
    // All methods inherited from InternshipReader, InternshipWriter, and InternshipValidator
}

