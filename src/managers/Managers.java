package managers;

import managers.taskmanagers.TaskManager;
import managers.taskmanagers.InMemoryTaskManager;
import managers.historymanagers.HistoryManager;
import managers.historymanagers.InMemoryHistoryManager;

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
