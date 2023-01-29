import java.util.List;

// интерфейс для управления историей просмотров
public interface HistoryManager {
    List<Task> getHistory();

    void addHistory(Task task);

}
