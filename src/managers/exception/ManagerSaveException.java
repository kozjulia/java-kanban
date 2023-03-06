package managers.exception;

public class ManagerSaveException extends RuntimeException { // собственное непроверяемое исключение
    public ManagerSaveException() {
    }

    public ManagerSaveException(final String message) {
        super(message);
    }
}
