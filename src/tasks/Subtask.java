package tasks;

import java.time.LocalDateTime;

// подзадача, обязательно входит в эпик
public class Subtask extends Task {
    private int idEpic; // id эпика для подзадачи

    public Subtask() {
        super();
        this.setType(TypeTask.SUBTASK);
    }

    public Subtask(String title, String description, int idEpic, LocalDateTime startTime, int duration) {
        super(title, description, startTime, duration);
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
        return String.format("%d,%S,%s,%S,%s,%d,%s,%d,",
                super.getId(), super.getType(), super.getTitle(),
                super.getStatusTask(), super.getDescription(), idEpic,
                super.getStartTime().format(super.formatter), super.getDuration());
    }
}