import java.util.List;
import java.util.ArrayList;

// эпик, состоит из подзадач
public class Epic extends Task {
    private List<Integer> uinSubtask = new ArrayList<>(); // уины подзадач эпика

    public Epic(String title, String description, StatusTask status) {
        super(title, description, status);
    }

    public List<Integer> getUinSubtask() {
        return uinSubtask;
    }

    public void setUinSubtask(List<Integer> uinSubtask) {
        this.uinSubtask = uinSubtask;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "title='" + super.getTitle() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", uin=" + super.getUin() +
                ", status='" + super.getStatusTask() + '\'' +
                ", uinSubtask.size()='" + uinSubtask.size() + '\'' +
                '}';
    }
}
