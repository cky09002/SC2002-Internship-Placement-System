package Assignment.src.exceptions;

public class TooManyApplicationsException extends RuntimeException {
    public TooManyApplicationsException(int max) {
        super(String.format("Maximum of %d applications allowed.", max));
    }
}
