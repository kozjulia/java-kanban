package managers.taskmanagers;

import tasks.Task;
import tasks.Epic;
import tasks.Subtask;
import tasks.StatusTask;

import java.util.List;

public interface TaskManager {

    // Методы для класса tasks.Task
    List<Task> getAllTask();

    void deleteAllTask();

    Task getTask(int id);

    int createTask(Task task);

    void updateTask(Task task);

    void deleteTask(int id);

    // Методы для класса tasks.Epic
    List<Epic> getAllEpic();

    void deleteAllEpic();

    Epic getEpic(int id);

    int createEpic(Epic epic);

    void updateEpic(Epic epic);

    void deleteEpic(int id);

    // Методы для класса tasks.Subtask
    List<Subtask> getAllSubtask();

    void deleteAllSubtask();

    Subtask getSubtask(int id);

    int createSubtask(Subtask subtask);

    void updateSubtask(Subtask subtask);

    void deleteSubtask(int id);

    // управление статусами задач
    void updateStatusTask(Task task, StatusTask status);

    void updateStatusEpic(Epic epic);

    void updateStatusSubtask(Subtask subtask, StatusTask status);

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();

    List<Subtask> getListSubtaskByEpic(Epic epic);
}