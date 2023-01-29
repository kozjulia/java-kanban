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
        taskManager.createTask("Вымыть пол", "со средством", StatusTask.NEW);
        taskManager.createTask("Поиграть", "в настольные игры", StatusTask.IN_PROGRESS);
        int uinEpic;
        uinEpic = taskManager.createEpic("Сделать покупки", "продукты", StatusTask.NEW);
        taskManager.createSubtask("Яблоки", "красные", StatusTask.DONE, uinEpic);
        taskManager.createSubtask("Творог", "200 гр.", StatusTask.DONE, uinEpic);
        uinEpic = taskManager.createEpic("Подготовиться к д/р", "детское", StatusTask.IN_PROGRESS);
        taskManager.createSubtask("Купить шарики", "белые", StatusTask.NEW, uinEpic);
        printAllTasks(taskManager);
        printHistory(taskManager);

        taskManager.updateStatusTask(taskManager.getTask(1), StatusTask.IN_PROGRESS);
        taskManager.updateStatusSubtask(taskManager.getSubtask(1), StatusTask.NEW); // нет такой подзадачи
        taskManager.updateStatusSubtask(taskManager.getSubtask(4), StatusTask.NEW);
        taskManager.updateStatusSubtask(taskManager.getSubtask(7), StatusTask.IN_PROGRESS);
        printAllTasks(taskManager);
        printHistory(taskManager);

        taskManager.deleteTask(2);
        taskManager.deleteSubtask(5);
        printAllTasks(taskManager);
        printHistory(taskManager);
    }
}
