package tasks;

// класс отдельно стоящей задачи, родитель tasks.Subtask и tasks.Epic
public class Task {
    private String title; // название
    private String description; // описание
    private int id; // Уникальный Идентификационный Номер задачи
    private StatusTask status; // статус, отображающий прогресс задачи

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        this.status = StatusTask.NEW;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public StatusTask getStatusTask() {
        return status;
    }

    public void setStatusTask(StatusTask status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "tasks.Task{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status='" + status + '\'' +
                '}';
    }
}
