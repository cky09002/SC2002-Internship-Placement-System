package exceptions;

public class WrongApplicationStatusException extends RuntimeException {
    public WrongApplicationStatusException(String curStatus, String reqStatus) {
        super(String.format("Application is %s when it should be %s", curStatus, reqStatus));
    }
}
