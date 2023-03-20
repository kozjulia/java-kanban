package managers.exception;

// собственное непроверяемое исключение при создании пересекающихся по времени задач и подзадач
public class SaveTaskException extends IllegalArgumentException {

    public SaveTaskException(final String message) {
        super(message);
    }

}