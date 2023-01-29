package managers;

import tasks.Epic;
import tasks.StatusTask;
import tasks.Subtask;
import tasks.Task;

import java.util.List;

public interface TaskManager {

    // Методы для класса tasks.Task
    List<Task> getAllTask();

    void deleteAllTask();

    Task getTask(int id);

    void createTask(String title, String description);

    void updateTask(Task task);

    void deleteTask(int id);

    // Методы для класса tasks.Subtask
    List<Subtask> getAllSubtask();

    void deleteAllSubtask();

    Subtask getSubtask(int id);

    void createSubtask(String title, String description, int idEpic);

    void updateSubtask(Subtask subtask);

    void deleteSubtask(int id);

    // Методы для класса tasks.Epic
    List<Epic> getAllEpic();

    void deleteAllEpic();

    Epic getEpic(int id);

    int createEpic(String title, String description);

    void updateEpic(Epic epic);

    void deleteEpic(int id);

    // управление статусами задач
    void updateStatusTask(Task task, StatusTask status);

    void updateStatusSubtask(Subtask subtask, StatusTask status);

    void updateStatusEpic(Epic epic);

    List<Task> getHistory();
}


