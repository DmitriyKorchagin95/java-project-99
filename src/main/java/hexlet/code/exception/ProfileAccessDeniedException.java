package hexlet.code.exception;

public class ProfileAccessDeniedException extends RuntimeException {
    public ProfileAccessDeniedException(String message) {
        super(message);
    }
}
