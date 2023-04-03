package managers.taskmanagers;

import client.KVTaskClient;
import managers.Managers;
import managers.exception.ManagerSaveException;
import tasks.Task;
import tasks.Epic;
import tasks.Subtask;
import tasks.TypeTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

// реализация менеджера задач для хранения данных на сервере
public class HttpTaskManager extends FileBackedTasksManager {
    private final String url;
    public final KVTaskClient client;
    private final Gson gson = Managers.getGson();

    public HttpTaskManager() {
        super();
        this.client = new KVTaskClient();
        this.url = "http://localhost:";
    }

    @Override
    protected void save() {
        Map<String, List<Task>> mapHttpTask = new HashMap<>();
        List<Task> tasks = new ArrayList<>();
        try {
            tasks.addAll(super.getAllTask());
            tasks.addAll(super.getAllEpic());
            tasks.addAll(super.getAllSubtask());
            mapHttpTask.put("tasks", tasks);
            mapHttpTask.put("history", super.getHistory());
            client.put(client.getKey(), gson.toJson(mapHttpTask));
        } catch (Exception e) {
            throw new ManagerSaveException("Ошибка сохранения на сервер");
        }
    }

    public void load() {
        Map<String, List<Task>> mapHttpTask = new HashMap<>(client.load(client.getKey()));
        List<Task> tasks = new ArrayList<>(mapHttpTask.get("tasks"));
        List<Task> history = new ArrayList<>(mapHttpTask.get("history"));

        for (Task task : tasks) {
            if (task.getType() == TypeTask.TASK) {
                createTask(task);
            } else if (task.getType() == TypeTask.EPIC) {
                createEpic((Epic) task);
            } else if (task.getType() == TypeTask.SUBTASK) {
                createSubtask((Subtask) task);
            }
        }

        for (Task task : history) {
            historyManager.add(task);
        }
    }
}