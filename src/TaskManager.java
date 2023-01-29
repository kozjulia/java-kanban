import java.util.List;

public interface TaskManager {

    // Методы для класса Task
    List<Task> getAllTask();

    void deleteAllTask();

    Task getTask(int uin);

    void createTask(String title, String description, StatusTask status);

    void updateTask(Task task);

    void deleteTask(int uin);

    // Методы для класса Subtask
    List<Subtask> getAllSubtask();

    void deleteAllSubtask();

    Subtask getSubtask(int uin);

    void createSubtask(String title, String description, StatusTask status, int uinEpic);

    void updateSubtask(Subtask subtask);

    void deleteSubtask(int uin);

    // Методы для класса Epic
    List<Epic> getAllEpic();

    void deleteAllEpic();

    Epic getEpic(int uin);

    int createEpic(String title, String description, StatusTask status);

    void updateEpic(Epic epic);

    void deleteEpic(int uin);

    // управление статусами задач
    void updateStatusTask(Task task, StatusTask status);

    void updateStatusSubtask(Subtask subtask, StatusTask status);

    void updateStatusEpic(Epic epic);

    List<Task> getHistory();
}


