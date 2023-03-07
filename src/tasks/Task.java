package tasks;

// класс отдельно стоящей задачи, родитель tasks.Subtask и tasks.Epic
public class Task {
    private int id; // Уникальный Идентификационный Номер задачи
    private TypeTask type; // тип задачи
    private String title; // название
    private StatusTask status; // статус, отображающий прогресс задачи
    private String description; // описание
    private static int nextId = 0; // счётчик по сквозной нумерации сущностей

    public Task() {
        this.type = TypeTask.TASK;
    }

    public Task(String title, String description) {
        this.id = generateId();
        this.type = TypeTask.TASK;
        this.title = title;
        this.status = StatusTask.NEW;
        this.description = description;
    }

    private int generateId() {
        return ++nextId;
    }

    public static int getNextId() {
        return nextId;
    }

    public static void setNextId(int nextId) {
        Task.nextId = nextId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TypeTask getType() {
        return type;
    }

    public void setType(TypeTask type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public StatusTask getStatusTask() {
        return status;
    }

    public void setStatusTask(StatusTask status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return String.format("%d,%S,%s,%S,%s,", id, type, title, status, description);
    }
}
