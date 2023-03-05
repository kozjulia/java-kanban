package tasks;

import java.util.List;
import java.util.ArrayList;

// эпик, состоит из подзадач
public class Epic extends Task {
    private List<Integer> idSubtask = new ArrayList<>(); // уины подзадач эпика

    public Epic() {
        super();
        this.setType(TypeTask.EPIC);
    }

    public Epic(String title, String description) {
        super(title, description);
        this.setType(TypeTask.EPIC);
    }

    public List<Integer> getIdSubtask() {
        return idSubtask;
    }

    public void setIdSubtask(List<Integer> idSubtask) {
        this.idSubtask = idSubtask;
    }

    @Override
    public String toString() {
        return String.format("%d,%S,%s,%S,%s,",
                super.getId(), super.getType(), super.getTitle(),
                super.getStatusTask(), super.getDescription());
    }
}
