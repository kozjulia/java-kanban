import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    // история просмотров задач, не более 10 элементов

    public List<Task> historyList = new ArrayList<>(); // история просмотра 10 элементов

    @Override
    public List<Task> getHistory() {
        return historyList;
    }

    @Override
    public void addHistory(Task task) {
        if (historyList.size() == 10) {
            historyList.remove(0);
        }
        historyList.add(task);
    }
}
