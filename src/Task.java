// класс отдельно стоящей задачи, родитель Subtask и Epic
public class Task {
    private String title; // название
    private String description; // описание
    private int uin; // Уникальный Идентификационный Номер задачи
    private StatusTask status; // статус, отображающий прогресс задачи

    public Task(String title, String description, StatusTask status) {
        this.title = title;
        this.description = description;
        this.status = status;
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

    public int getUin() {
        return uin;
    }

    public void setUin(int uin) {
        this.uin = uin;
    }

    public StatusTask getStatusTask() {
        return status;
    }

    public void setStatusTask(StatusTask status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Task{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", uin=" + uin +
                ", status='" + status + '\'' +
                '}';
    }
}
