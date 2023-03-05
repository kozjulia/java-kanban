package tasks;

// подзадача, обязательно входит  в эпик
public class Subtask extends Task {
    private int idEpic; // уин эпика для подзадачи

    public Subtask() {
        super();
        this.setType(TypeTask.SUBTASK);
    }

    public Subtask(String title, String description, int idEpic) {
        super(title, description);
        this.setType(TypeTask.SUBTASK);
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
        return String.format("%d,%S,%s,%S,%s,%d",
                super.getId(), super.getType(), super.getTitle(),
                super.getStatusTask(), super.getDescription(), idEpic);
    }
}
