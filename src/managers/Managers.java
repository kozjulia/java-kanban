package managers;

import managers.historymanagers.HistoryManager;
import managers.historymanagers.InMemoryHistoryManager;
import managers.taskmanagers.FileBackedTasksManager;
import managers.taskmanagers.TaskManager;

// утилитарный класс-менеджер
public class Managers {

    private Managers() {
    }

    public static TaskManager getDefault() {
        return new FileBackedTasksManager(FileBackedTasksManager.file);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

}
