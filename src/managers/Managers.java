package managers;

import managers.taskmanagers.FileBackedTasksManager;
import managers.taskmanagers.HttpTaskManager;
import managers.taskmanagers.TaskManager;
import managers.taskmanagers.InMemoryTaskManager;
import managers.historymanagers.HistoryManager;
import managers.historymanagers.InMemoryHistoryManager;
import managers.utils.LocalDateTimeAdapter;
import managers.utils.TaskJsonAdapter;
import tasks.Task;

import java.time.LocalDateTime;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

// утилитарный класс-менеджер
public class Managers {

    private Managers() {
    }

    public static TaskManager getDefault() {
        return new HttpTaskManager();
        //return FileBackedTasksManager.loadFromFile();
        //return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Task.class, new TaskJsonAdapter());
        return gsonBuilder.create();
    }
}