package tasks;

import java.util.List;
import java.util.ArrayList;

// эпик, состоит из подзадач
public class Epic extends Task {
    private List<Integer> idSubtask = new ArrayList<>(); // уины подзадач эпика

    public Epic(String title, String description) {
        super(title, description);
    }

    public List<Integer> getIdSubtask() {
        return idSubtask;
    }

    public void setIdSubtask(List<Integer> idSubtask) {
        this.idSubtask = idSubtask;
    }

    @Override
    public String toString() {
        return "tasks.Epic{" +
                "title='" + super.getTitle() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", id=" + super.getId() +
                ", status='" + super.getStatusTask() + '\'' +
                ", idSubtask.size()='" + idSubtask.size() + '\'' +
                '}';
    }
}
