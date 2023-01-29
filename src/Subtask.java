// подзадача, обязательно входит  в эпик
public class Subtask extends Task {
    private int uinEpic; // уин эпика для подзадачи

    public Subtask(String title, String description, StatusTask status, int uinEpic) {
        super(title, description, status);
        this.uinEpic = uinEpic;
    }

    public int getUinEpic() {
        return uinEpic;
    }

    public void setUinEpic(int uinEpic) {
        this.uinEpic = uinEpic;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "title='" + super.getTitle() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", uin=" + super.getUin() +
                ", status='" + super.getStatusTask() + '\'' +
                ", uinEpic='" + uinEpic + '\'' +
                '}';
    }
}
