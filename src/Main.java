import managers.Managers;
import managers.taskmanagers.HttpTaskManager;
import managers.taskmanagers.TaskManager;
import managers.taskmanagers.FileBackedTasksManager;
import server.KVServer;

import java.io.IOException;

// Трекер задач (бэкенд)
public class Main {
    public static void main(String[] args) throws IOException {
        KVServer kvServer = new KVServer();
        kvServer.start();
        TaskManager taskManager = Managers.getDefault();
        FileBackedTasksManager.testSprint(taskManager);  // тестовые данные для ФЗ 8-го спринта

        HttpTaskManager taskManagerNew = new HttpTaskManager();
        taskManagerNew.load();
        FileBackedTasksManager.printAllTasks(taskManager);
        FileBackedTasksManager.printHistory(taskManager);
        FileBackedTasksManager.printSortSet(taskManager);
        kvServer.stop();
    }
}