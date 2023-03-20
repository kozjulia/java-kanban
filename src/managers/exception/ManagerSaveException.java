package managers.exception;

// собственное непроверяемое исключение для сохранения и открытия файла
public class ManagerSaveException extends RuntimeException {

    public ManagerSaveException(final String message) {
        super(message);
    }
}
