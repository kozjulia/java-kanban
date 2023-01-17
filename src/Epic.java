import java.util.ArrayList;

// эпик, состоит из подзадач
public class Epic extends Task {
    private ArrayList<Integer> uinSubtask = new ArrayList<>(); // уины подзадач эпика

    public Epic(String title, String description, String status) {
        super(title, description, status);
    }

    public ArrayList<Integer> getUinSubtask() {
        return uinSubtask;
    }

    public void setUinSubtask(ArrayList<Integer> uinSubtask) {
        this.uinSubtask = uinSubtask;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "title='" + super.getTitle() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", uin=" + super.getUin() +
                ", status='" + super.getStatus() + '\'' +
                ", uinSubtask.size()='" + uinSubtask.size() + '\'' +
                '}';
    }
}
