package tasks;

// подзадача, обязательно входит  в эпик
public class Subtask extends Task {
    private int idEpic; // уин эпика для подзадачи

    public Subtask(String title, String description, int idEpic) {
        super(title, description);
        this.idEpic = idEpic;
    }

    public int getIdEpic() {
        return idEpic;
    }

    public void setIdEpic(int idEpic) {
        this.idEpic = idEpic;
    }

    @Override
    public String toString() {
        return "tasks.Subtask{" +
                "title='" + super.getTitle() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", id=" + super.getId() +
                ", status='" + super.getStatusTask() + '\'' +
                ", idEpic='" + idEpic + '\'' +
                '}';
    }
}
