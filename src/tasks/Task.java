package tasks;

import java.time.Duration;
import java.time.LocalDateTime;

import static managers.utils.LocalDateTimeAdapter.FORMATTER;

// класс отдельно стоящей задачи, родитель tasks.Subtask и tasks.Epic
public class Task {
    private int id; // Уникальный Идентификационный Номер задачи
    private static int nextId = 0; // счётчик по сквозной нумерации сущностей
    private TypeTask type; // тип задачи
    private String title; // название
    private StatusTask status; // статус, отображающий прогресс задачи
    private String description; // описание
    private LocalDateTime startTime; // дата, когда предполагается приступить к выполнению задач
    private int duration;
    // продолжительность задачи, оценка того, сколько времени она займёт в минутах (число)

    //protected final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    public Task() {
        this.type = TypeTask.TASK;
    }

    public Task(String title, String description) {
        this.id = generateId();
        this.type = TypeTask.TASK;
        this.title = title;
        this.status = StatusTask.NEW;
        this.description = description;
        this.startTime = LocalDateTime.MAX;
        this.duration = 0;
    }

    public Task(String title, String description, LocalDateTime startTime, int duration) {
        this.id = generateId();
        this.type = TypeTask.TASK;
        this.title = title;
        this.status = StatusTask.NEW;
        this.description = description;
        this.startTime = startTime;
        this.duration = duration;
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

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public LocalDateTime getEndTime() {
        // расчётное время завершения задачи
        if (this.getStartTime() == null) {
            return null;
        }
        if (this.getStartTime().isEqual(LocalDateTime.MAX)) {
            return LocalDateTime.MAX;
        }
        if (this.getStartTime().isEqual(LocalDateTime.MAX.minusNanos(999999999))) {
            return this.getStartTime();
        }
        return this.getStartTime().plus(Duration.ofMinutes(this.getDuration()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Task task = (Task) o;

        if (id != task.id) return false;
        if (type != task.type) return false;
        if (!title.equals(task.title)) return false;
        if (status != task.status) return false;
        if (!description.equals(task.description)) return false;
        if ((!startTime.isEqual(task.startTime)) &&
                (!startTime.minusNanos(999999999).isEqual(task.startTime)) &&
                (!startTime.isEqual(task.startTime.minusNanos(999999999)))) return false;
        if (duration != task.duration) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + type.hashCode();
        result = 31 * result + title.hashCode();
        result = 31 * result + status.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return String.format("%d,%S,%s,%S,%s,,%s,%d,",
                id, type, title, status, description, startTime.format(FORMATTER), duration);
    }
}