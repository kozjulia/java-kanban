// Трекер задач (бэкенд)
public class Main {

    public static void main(String[] args) {
        Manager manager = new Manager();

        manager.createTask("Вымыть пол", "со средством", "NEW");
        manager.createTask("Поиграть", "в настольные игры", "IN_PROGRESS");
        int uinEpic;
        uinEpic = manager.createEpic("Сделать покупки", "продукты", "NEW");
        manager.createSubtask("Яблоки", "красные", "DONE", uinEpic);
        manager.createSubtask("Творог", "200 гр.", "DONE", uinEpic);
        uinEpic = manager.createEpic("Подготовиться к д/р", "детское", "IN_PROGRESS");
        manager.createSubtask("Купить шарики", "белые", "NEW", uinEpic);
        printAllTasks(manager);

        manager.updateStatusTask(manager.getTaskByUin(1), "IN_PROGRESS");
        manager.updateStatusSubtask(manager.getSubtaskByUin(1), "NEW"); // нет такой подзадачи
        manager.updateStatusSubtask(manager.getSubtaskByUin(4), "NEW");
        manager.updateStatusSubtask(manager.getSubtaskByUin(7), "IN_PROGRESS");
        printAllTasks(manager);

        manager.deleteTask(2);
        manager.deleteSubtask(5);
        printAllTasks(manager);
    }

    private static void printAllTasks(Manager manager) {
        System.out.println();
        for (Task task : manager.getAllTask()) {
            System.out.println(task);
        }
        for (Epic epic : manager.getAllEpic()) {
            System.out.println(epic);
        }
        for (Subtask subtask : manager.getAllSubtask()) {
            System.out.println(subtask);
        }
    }
}
