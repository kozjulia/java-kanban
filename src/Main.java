import managers.Managers;
import managers.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

// Трекер задач (бэкенд)
public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        testSprint(taskManager);  // тестовые данные для ФЗ 5-го спринта
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
        printAllTasks(taskManager);
        printHistory(taskManager);

        taskManager.getTask(1);
        taskManager.getSubtask(4);
        taskManager.getEpic(3);
        printHistory(taskManager);

        taskManager.getEpic(3);
        taskManager.getTask(2);
        taskManager.getTask(1);
        printHistory(taskManager);

        taskManager.deleteTask(2);
        taskManager.deleteEpic(7);
        printAllTasks(taskManager);
        printHistory(taskManager);

        taskManager.deleteEpic(3);
        printAllTasks(taskManager);
        printHistory(taskManager);
    }
}
