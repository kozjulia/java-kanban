package managers.historymanagers;

import tasks.Task;

import java.util.List;

// интерфейс для управления историей просмотров
public interface HistoryManager {
    void add(Task task);

    void remove(int id);

    List<Task> getHistory();
}