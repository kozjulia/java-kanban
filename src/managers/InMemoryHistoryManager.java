package managers;

import managers.HistoryManager;
import tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class InMemoryHistoryManager implements HistoryManager {
    // история просмотров задач, не более 10 элементов

    public List<Task> historyList = new ArrayList<>(); // история просмотра 10 элементов

    @Override
    public List<Task> getHistory() {
        List reverse = new ArrayList();
        reverse.addAll(historyList);  // передаем историю, начав с последней просмотренной задачи
        Collections.reverse(reverse);
        return reverse;
    }

    @Override
    public void addHistory(Task task) {
        if (historyList.size() == 10) {
            historyList.remove(0);
        }
        historyList.add(task);
    }
}
