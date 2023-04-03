package tasks;

import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;

import static managers.utils.LocalDateTimeAdapter.FORMATTER;

// эпик, состоит из подзадач
public class Epic extends Task {
    private List<Integer> idSubtask = new ArrayList<>(); // id подзадач эпика
    private LocalDateTime endTime; // время завершения задачи

    public Epic() {
        super();
        this.setType(TypeTask.EPIC);
    }

    public Epic(String title, String description) {
        super(title, description);
        this.setType(TypeTask.EPIC);
        this.endTime = this.getStartTime();
    }

    public List<Integer> getIdSubtask() {
        return idSubtask;
    }

    public void setIdSubtask(List<Integer> idSubtask) {
        this.idSubtask = idSubtask;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        // расчётное время завершения задачи
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return String.format("%d,%S,%s,%S,%s,,%s,%d,%s",
                super.getId(), super.getType(), super.getTitle(),
                super.getStatusTask(), super.getDescription(),
                super.getStartTime().format(FORMATTER), super.getDuration(),
                getEndTime().format(FORMATTER));
    }
}