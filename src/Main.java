import managers.Managers;
import managers.TaskManager;
import tasks.Epic;
import tasks.StatusTask;
import tasks.Subtask;
import tasks.Task;

// Трекер задач (бэкенд)
public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        testSprint(taskManager);  // тестовые данные для ФЗ спринта
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
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }
    }

    private static void testSprint(TaskManager taskManager) {
        taskManager.createTask("Вымыть пол", "со средством");
        taskManager.createTask("Поиграть", "в настольные игры");
        int idEpic;
        idEpic = taskManager.createEpic("Сделать покупки", "продукты");
        taskManager.createSubtask("Яблоки", "красные", idEpic);
        taskManager.createSubtask("Творог", "200 гр.", idEpic);
        idEpic = taskManager.createEpic("Подготовиться к д/р", "детское");
        taskManager.createSubtask("Купить шарики", "белые", idEpic);
        printAllTasks(taskManager);
        printHistory(taskManager);

        taskManager.updateStatusTask(taskManager.getTask(1), StatusTask.IN_PROGRESS);
        taskManager.updateStatusSubtask(taskManager.getSubtask(1), StatusTask.DONE); // нет такой подзадачи
        taskManager.updateStatusSubtask(taskManager.getSubtask(4), StatusTask.DONE);
        taskManager.updateStatusSubtask(taskManager.getSubtask(7), StatusTask.DONE);
        printAllTasks(taskManager);
        printHistory(taskManager);

        taskManager.deleteTask(2);
        taskManager.deleteSubtask(5);
        printAllTasks(taskManager);
        printHistory(taskManager);
    }
}
