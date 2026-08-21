package exception;

public class DatabaseException extends BusinessException {
    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
