// эпик, состоит из подзадач
public class Epic extends Task {

    public Epic(String title, String description, String status) {
        super(title, description, status);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "title='" + super.getTitle() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", uin=" + super.getUin() +
                ", status='" + super.getStatus() + '\'' +
                '}';
    }
}
