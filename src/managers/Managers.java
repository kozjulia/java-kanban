package managers;

import managers.historymanagers.HistoryManager;
import managers.historymanagers.InMemoryHistoryManager;
import managers.taskmanagers.InMemoryTaskManager;
import managers.taskmanagers.TaskManager;

// утилитарный класс-менеджер
public class Managers {

    private Managers() {
    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

}
