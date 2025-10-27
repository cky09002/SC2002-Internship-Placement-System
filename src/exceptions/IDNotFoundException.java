package exceptions;

public class IDNotFoundException extends RuntimeException {
    public IDNotFoundException(String type, int id) {
        super(String.format("Couldn't find %s with ID %d.", type, id));
    }
}
