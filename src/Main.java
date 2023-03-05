import managers.taskmanagers.FileBackedTasksManager;
import managers.Managers;
import managers.taskmanagers.TaskManager;
import tasks.Epic;
import tasks.StatusTask;
import tasks.Subtask;
import tasks.Task;

// Трекер задач (бэкенд)
public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        testSprint(taskManager);  // тестовые данные для ФЗ 6-го спринта

        TaskManager taskManagerNew = FileBackedTasksManager.loadFromFile(FileBackedTasksManager.file);
        testSprintNew(taskManagerNew);  // тестовые данные для ФЗ 6-го спринта загрузка из файла
    }

    private static void printAllTasks(TaskManager taskManager) {
        System.out.println("\nСписок задач:");
        for (Task task : taskManager.getAllTask()) {
            System.out.println(task);
        }
        for (Epic epic : taskManager.getAllEpic()) {
            System.out.println(epic);
        }
        for (Subtask subtask : taskManager.getAllSubtask()) {
            System.out.println(subtask);
        }
    }

    private static void printHistory(TaskManager taskManager) {
        System.out.println("\nИстория просмотра: ");
        if (taskManager.getHistory() == null) {
            return;
        }
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }
    }

    private static void testSprint(TaskManager taskManager) {
        taskManager.createTask("Покормить животных", "вкусным кормом");
        taskManager.createTask("Поиграть", "в настольные игры");
        int idEpic;
        idEpic = taskManager.createEpic("Сделать покупки", "продукты");
        taskManager.createSubtask("Яблоки", "красные", idEpic);
        taskManager.createSubtask("Творог", "200 гр.", idEpic);
        taskManager.createSubtask("Молоко", "2 литра", idEpic);
        taskManager.createEpic("Подготовиться к д/р", "детское");
        taskManager.getTask(1);
        taskManager.getEpic(3);
        taskManager.getTask(2);
        taskManager.getTask(1);
        taskManager.updateStatusSubtask(taskManager.getSubtask(6), StatusTask.DONE);
        taskManager.deleteTask(2);
        printAllTasks(taskManager);
        printHistory(taskManager);
    }

    private static void testSprintNew(TaskManager taskManagerNew) {
        printAllTasks(taskManagerNew);
        printHistory(taskManagerNew);
    }
}
