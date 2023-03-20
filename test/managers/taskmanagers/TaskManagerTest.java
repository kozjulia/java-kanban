package managers.taskmanagers;

import tasks.Task;
import tasks.Epic;
import tasks.Subtask;
import tasks.StatusTask;
import managers.exception.SaveTaskException;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {

    // тесты для абстрактного класса TaskManagerTest, которые одинаковы у всех классов-наследников
    protected T taskManager;
    protected static Task task1;
    protected static Task task2;
    protected static Epic epic3;
    protected static Subtask subtask4;
    protected static Subtask subtask5;
    protected static Subtask subtask6;
    protected static Epic epic7;

    @Test
    @DisplayName("тест получения списка всех задач")
    public void getAllTask() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        final List<Task> tasks = taskManager.getAllTask();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(2, tasks.size(), "Неверное количество задач.");
        assertTrue(tasks.contains(task1), "Задача не записалась.");
        assertTrue(tasks.contains(task2), "Задача не записалась.");
    }

    @Test
    @DisplayName("тест получения пустого списка всех задач")
    public void getAllNullTask() {
        final List<Task> tasks = taskManager.getAllTask();

        assertTrue(tasks.isEmpty(), "Задачи возвращаются.");
    }

    @Test
    @DisplayName("тест удаления списка всех задач")
    public void deleteAllTask() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.deleteAllTask();
        final List<Task> tasks = taskManager.getAllTask();

        assertTrue(tasks.isEmpty(), "Задачи возвращаются.");
    }

    @Test
    @DisplayName("тест удаления пустого списка всех задач")
    public void deleteAllNullTask() {
        taskManager.deleteAllTask();
        final List<Task> tasks = taskManager.getAllTask();

        assertTrue(tasks.isEmpty(), "Задачи возвращаются.");
    }

    @Test
    @DisplayName("тест получения задачи по id")
    public void getTask() {
        final int idTask = taskManager.createTask(task1);
        Task getTask = taskManager.getTask(idTask);

        assertNotNull(getTask, "Задача не найдена.");
        assertEquals(task1, getTask, "Задачи не совпадают.");
    }

    @Test
    @DisplayName("тест получения задачи по несуществующему id")
    public void getWrongTask() {
        taskManager.createTask(task1);
        Task getTask = taskManager.getTask(100);

        assertNull(getTask, "Задача найдена.");
    }

    @Test
    @DisplayName("тест создания задачи")
    public void createTask() {
        Task task = new Task("Test createNewTask", "Test createNewTask description",
                LocalDateTime.of(2023, 03, 19, 10, 00), 30);
        final int idTask = taskManager.createTask(task);
        final Task savedTask = taskManager.getTask(idTask);
        final List<Task> tasks = taskManager.getAllTask();

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(savedTask, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    @DisplayName("тест создания пустой задачи")
    public void createNullTask() {
        Task taskNull = null;
        final int idTask = taskManager.createTask(taskNull);
        final List<Task> tasks = taskManager.getAllTask();

        assertEquals(0, idTask, "Нулевая задача создалась");
        assertTrue(tasks.isEmpty(), "Нулевые задачи возвращаются.");
    }

    @Test
    @DisplayName("тест создания пересекающейся во времени задачи")
    public void createCrossTask() {
        taskManager.createTask(task2);
        taskManager.createEpic(epic3);
        taskManager.createSubtask(subtask4);
        Task crossTask = new Task("Cross task", "Cross task description",
                LocalDateTime.of(2023, 03, 15, 9, 30), 45);

        // после исполнения блока ошибка попадёт в переменную exception
        final SaveTaskException exception = assertThrows(
                // класс ошибки
                SaveTaskException.class,
                // создание и переопределение экземпляра класса Executable
                () -> taskManager.createTask(crossTask));
        assertEquals(String.format("Невозможно создать новую задачу %s из-за пересечения во времени",
                crossTask), exception.getMessage());
        assertEquals(2, taskManager.getPrioritizedTasks().size(),
                "Размер списка сортированных задач неверен");
    }

    @Test
    @DisplayName("тест обновления задачи")
    public void updateTask() {
        taskManager.createTask(task1);
        Task updateTask = new Task("Test updateTask", "Test updateTask description",
                LocalDateTime.of(2023, 03, 19, 10, 00), 30);
        updateTask.setId(task1.getId());
        taskManager.updateTask(updateTask);
        final List<Task> tasks = taskManager.getAllTask();

        assertNotEquals(task1, updateTask, "Задачи совпадают.");
        assertEquals(task1.getId(), updateTask.getId(), "ID задач не совпадают.");
        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(updateTask, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    @DisplayName("тест обновления пустой задачи")
    public void updateNullTask() {
        Task updateTask = null;
        taskManager.updateTask(updateTask);
        final List<Task> tasks = taskManager.getAllTask();

        assertTrue(tasks.isEmpty(), "Нулевая задача возвращается.");
    }

    @Test
    @DisplayName("тест обновления пересекающейся во времени задачи")
    public void updateCrossTask() {
        taskManager.createTask(task2);
        taskManager.createEpic(epic3);
        taskManager.createSubtask(subtask4);
        Task crossTask = new Task("Cross task", "Cross task description",
                LocalDateTime.of(2023, 03, 15, 9, 30), 45);
        crossTask.setId(2);

        // после исполнения блока ошибка попадёт в переменную exception
        final SaveTaskException exception = assertThrows(
                // класс ошибки
                SaveTaskException.class,
                // создание и переопределение экземпляра класса Executable
                () -> taskManager.updateTask(crossTask));
        assertEquals(String.format("Невозможно обновить задачу %s из-за пересечения во времени",
                crossTask), exception.getMessage());
        assertEquals(2, taskManager.getPrioritizedTasks().size(),
                "Размер списка сортированных задач неверен");
    }

    @Test
    @DisplayName("тест удаления задачи по id")
    public void deleteTask() {
        final int idTask = taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.deleteTask(idTask);
        final List<Task> tasks = taskManager.getAllTask();

        assertFalse(tasks.contains(task1), "Задача найдена.");
        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task2, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    @DisplayName("тест удаления задачи по несуществующему id")
    public void deleteWrongTask() {
        taskManager.createTask(task1);
        taskManager.deleteTask(100);
        final List<Task> tasks = taskManager.getAllTask();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task1, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    @DisplayName("тест получения списка всех эпиков")
    public void getAllEpic() {
        taskManager.createEpic(epic3);
        taskManager.createEpic(epic7);
        final List<Epic> epics = taskManager.getAllEpic();

        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(2, epics.size(), "Неверное количество эпиков.");
        assertTrue(epics.contains(epic3), "Эпик не записался.");
        assertTrue(epics.contains(epic7), "Эпик не записался.");
    }

    @Test
    @DisplayName("тест получения пустого списка всех эпиков")
    public void getAllNullEpic() {
        final List<Epic> epics = taskManager.getAllEpic();

        assertTrue(epics.isEmpty(), "Эпики возвращаются.");
    }

    @Test
    @DisplayName("тест удаления списка всех эпиков")
    public void deleteAllEpic() {
        taskManager.createEpic(epic3);
        taskManager.createEpic(epic7);
        taskManager.deleteAllEpic();
        final List<Epic> epics = taskManager.getAllEpic();

        assertTrue(epics.isEmpty(), "Эпики возвращаются.");
    }

    @Test
    @DisplayName("тест удаления пустого списка всех эпиков")
    public void deleteAllNullEpic() {
        taskManager.deleteAllEpic();
        final List<Epic> epics = taskManager.getAllEpic();

        assertTrue(epics.isEmpty(), "Эпики возвращаются.");
    }

    @Test
    @DisplayName("тест получения эпика по id")
    public void getEpic() {
        final int idEpic = taskManager.createEpic(epic3);
        Epic getEpic = taskManager.getEpic(idEpic);

        assertNotNull(getEpic, "Эпик не найден.");
        assertEquals(epic3, getEpic, "Эпики не совпадают.");
    }

    @Test
    @DisplayName("тест получения эпика по несуществующему id")
    public void getWrongEpic() {
        taskManager.createEpic(epic3);
        Epic getEpic = taskManager.getEpic(100);

        assertNull(getEpic, "Эпик найден.");
    }

    @Test
    @DisplayName("тест создания эпика")
    public void createEpic() {
        Epic epic = new Epic("Test createNewEpic", "Test createNewEpic description");
        final int idEpic = taskManager.createEpic(epic);
        final Epic savedEpic = taskManager.getEpic(idEpic);
        final List<Epic> epics = taskManager.getAllEpic();

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");
        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(savedEpic, epics.get(0), "Эпики не совпадают.");
    }

    @Test
    @DisplayName("тест создания эпика и подзадачи для проверки правильности расчёта времени начала, " +
            "окончания и продолжительности эпика")
    public void createEpicAndSubtask() {
        epic3 = new Epic("Test epic3", "Test epic3 description");
        subtask4 = new Subtask("Test subtask4", "Test subtask4 description", epic3.getId(),
                LocalDateTime.of(2023, 03, 16, 10, 00), 15);
        subtask5 = new Subtask("Test subtask5", "Test subtask5 description", epic3.getId(),
                LocalDateTime.of(2023, 03, 15, 12, 00), 30);
        taskManager.createEpic(epic3);
        taskManager.createSubtask(subtask4);
        taskManager.createSubtask(subtask5);

        assertEquals(subtask5.getStartTime(), epic3.getStartTime(),
                "Дата начала действия эпика рассчитывается неправильно.");
        assertEquals(45, epic3.getDuration(),
                "Длительность эпика рассчитывается неправильно.");
        assertEquals(subtask4.getEndTime(), epic3.getEndTime(),
                "Дата окончания действия эпика рассчитывается неправильно.");
    }

    @Test
    @DisplayName("тест создания пустого эпика")
    public void createNullEpic() {
        Epic epicNull = null;
        final int idEpic = taskManager.createEpic(epicNull);
        final List<Epic> epics = taskManager.getAllEpic();

        assertEquals(0, idEpic, "Нулевой эпик создался");
        assertTrue(epics.isEmpty(), "Нулевые эпики возвращаются.");
    }

    @Test
    @DisplayName("тест обновления эпика")
    public void updateEpic() {
        taskManager.createEpic(epic3);
        Epic updateEpic = new Epic("Test updateEpic", "Test updateEpic description");
        updateEpic.setId(epic3.getId());
        taskManager.updateEpic(updateEpic);
        final List<Epic> epics = taskManager.getAllEpic();

        assertNotEquals(epic3, updateEpic, "Эпики совпадают.");
        assertEquals(epic3.getId(), updateEpic.getId(), "ID эпиков не совпадают.");
        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(updateEpic, epics.get(0), "Эпики не совпадают.");
    }

    @Test
    @DisplayName("тест обновления пустого эпика")
    public void updateNullEpic() {
        Epic updateEpic = null;
        taskManager.updateEpic(updateEpic);
        final List<Epic> epics = taskManager.getAllEpic();

        assertTrue(epics.isEmpty(), "Нулевой эпик возвращается.");
    }

    @Test
    @DisplayName("тест удаления эпика по id")
    public void deleteEpic() {
        final int idEpic = taskManager.createEpic(epic3);
        taskManager.createEpic(epic7);
        taskManager.deleteEpic(idEpic);
        final List<Epic> epics = taskManager.getAllEpic();

        assertFalse(epics.contains(epic3), "Эпик найден.");
        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic7, epics.get(0), "Эпики не совпадают.");
    }

    @Test
    @DisplayName("тест удаления эпика по несуществующему id")
    public void deleteWrongEpic() {
        taskManager.createEpic(epic3);
        taskManager.deleteEpic(100);
        final List<Epic> epics = taskManager.getAllEpic();

        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic3, epics.get(0), "Эпики не совпадают.");
    }

    @Test
    @DisplayName("тест получения списка всех подзадач")
    public void getAllSubtask() {
        taskManager.createEpic(epic3);
        taskManager.createSubtask(subtask4);
        taskManager.createSubtask(subtask5);
        final List<Subtask> subtasks = taskManager.getAllSubtask();

        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(2, subtasks.size(), "Неверное количество подзадач.");
        assertTrue(subtasks.contains(subtask4), "Подзадача не записалась.");
        assertTrue(subtasks.contains(subtask5), "Подзадача не записалась.");
    }

    @Test
    @DisplayName("тест получения пустого списка всех подзадач")
    public void getAllNullSubtask() {
        final List<Subtask> subtasks = taskManager.getAllSubtask();

        assertTrue(subtasks.isEmpty(), "Подзадачи возвращаются.");
    }

    @Test
    @DisplayName("тест удаления списка всех подзадач")
    public void deleteAllSubtask() {
        taskManager.createEpic(epic3);
        taskManager.createSubtask(subtask4);
        taskManager.createSubtask(subtask5);
        taskManager.deleteAllSubtask();
        final List<Subtask> subtasks = taskManager.getAllSubtask();

        assertTrue(subtasks.isEmpty(), "Задачи возвращаются.");
    }

    @Test
    @DisplayName("тест удаления пустого списка всех подзадач")
    public void deleteAllNullSubtask() {
        taskManager.deleteAllSubtask();
        final List<Subtask> subtasks = taskManager.getAllSubtask();

        assertTrue(subtasks.isEmpty(), "Задачи возвращаются.");
    }

    @Test
    @DisplayName("тест получения подзадачи по id")
    public void getSubtask() {
        taskManager.createEpic(epic3);
        final int idSubtask = taskManager.createSubtask(subtask4);
        Subtask getSubtask = taskManager.getSubtask(idSubtask);

        assertNotNull(getSubtask, "Подзадача не найдена.");
        assertEquals(subtask4, getSubtask, "Подзадачи не совпадают.");
    }

    @Test
    @DisplayName("тест получения подзадачи по несуществующему id")
    public void getWrongSubtask() {
        taskManager.createEpic(epic3);
        taskManager.createSubtask(subtask4);
        Subtask getSubtask = taskManager.getSubtask(100);

        assertNull(getSubtask, "Подзадача найдена.");
    }


    @Test
    @DisplayName("тест создания подзадачи")
    public void createSubtask() {
        Epic epic = new Epic("Test Epic", "Test Epic description");
        final int idEpic = taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Test createNewSubtask", "Test createNewSubtask description",
                idEpic, LocalDateTime.of(2023, 03, 19, 10, 00), 30);
        final int idSubtask = taskManager.createSubtask(subtask);
        final Subtask savedSubtask = taskManager.getSubtask(idSubtask);
        final List<Subtask> subtasks = taskManager.getAllSubtask();

        assertNotNull(savedSubtask, "Подзадача не найдена.");
        assertEquals(subtask, savedSubtask, "Подзадачи не совпадают.");
        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(savedSubtask, subtasks.get(0), "Подзадачи не совпадают.");
    }

    @Test
    @DisplayName("тест создания пересекающейся во времени подзадачи")
    public void createCrossSubtask() {
        taskManager.createTask(task2);
        taskManager.createEpic(epic3);
        Subtask crossSubtask = new Subtask("Cross subtask", "Cross subtask description", 3,
                LocalDateTime.of(2023, 03, 19, 9, 15), 60);

        // после исполнения блока ошибка попадёт в переменную exception
        final SaveTaskException exception = assertThrows(
                // класс ошибки
                SaveTaskException.class,
                // создание и переопределение экземпляра класса Executable
                () -> taskManager.createTask(crossSubtask));
        assertEquals(String.format("Невозможно создать новую задачу %s из-за пересечения во времени",
                crossSubtask), exception.getMessage());
        assertEquals(1, taskManager.getPrioritizedTasks().size(),
                "Размер списка сортированных задач неверен");
    }

    @Test
    @DisplayName("тест создания пустой подзадачи")
    public void createNullSubtask() {
        Subtask subtaskNull = null;
        final int idSubtask = taskManager.createSubtask(subtaskNull);
        final List<Subtask> subtasks = taskManager.getAllSubtask();

        assertEquals(0, idSubtask, "Нулевая подзадача создалась");
        assertTrue(subtasks.isEmpty(), "Нулевые подзадачи возвращаются.");
    }

    @Test
    @DisplayName("тест обновления подзадачи")
    public void updateSubtask() {
        final int idEpic = taskManager.createEpic(epic3);
        taskManager.createSubtask(subtask4);
        Subtask updateSubtask = new Subtask("Test updateSubtask", "Test updateSubtask description",
                idEpic, LocalDateTime.of(2023, 03, 19, 10, 00), 30);
        updateSubtask.setId(subtask4.getId());
        taskManager.updateSubtask(updateSubtask);
        final List<Subtask> subtasks = taskManager.getAllSubtask();

        assertNotEquals(subtask4, updateSubtask, "Подзадачи совпадают.");
        assertEquals(subtask4.getId(), updateSubtask.getId(), "ID подзадач не совпадают.");
        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(updateSubtask, subtasks.get(0), "Подзадачи не совпадают.");
    }

    @Test
    @DisplayName("тест обновления пустой подзадачи")
    public void updateNullSubtask() {
        Subtask updateSubtask = null;
        taskManager.updateSubtask(updateSubtask);
        final List<Subtask> subtasks = taskManager.getAllSubtask();

        assertTrue(subtasks.isEmpty(), "Нулевая подзадача возвращается.");
    }

    @Test
    @DisplayName("тест обновления пересекающейся во времени подзадачи")
    public void updateCrossSubtask() {
        taskManager.createTask(task2);
        taskManager.createEpic(epic3);
        taskManager.createSubtask(subtask4);
        taskManager.createSubtask(subtask5);
        taskManager.createSubtask(subtask6);
        Subtask crossSubtask = new Subtask("Cross subtask", "Cross subtask description", 3,
                LocalDateTime.of(2023, 03, 15, 10, 15), 120);
        crossSubtask.setId(4);

        // после исполнения блока ошибка попадёт в переменную exception
        final SaveTaskException exception = assertThrows(
                // класс ошибки
                SaveTaskException.class,
                // создание и переопределение экземпляра класса Executable
                () -> taskManager.updateSubtask(crossSubtask));
        assertEquals(String.format("Невозможно обновить подзадачу %s из-за пересечения во времени",
                crossSubtask), exception.getMessage());
        assertEquals(4, taskManager.getPrioritizedTasks().size(),
                "Размер списка сортированных задач неверен");
    }

    @Test
    @DisplayName("тест удаления подзадачи по id")
    public void deleteSubtask() {
        taskManager.createEpic(epic3);
        final int idSubtask = taskManager.createSubtask(subtask4);
        taskManager.createSubtask(subtask5);
        taskManager.deleteSubtask(idSubtask);
        final List<Subtask> subtasks = taskManager.getAllSubtask();

        assertFalse(subtasks.contains(subtask4), "Подзадача найдена.");
        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask5, subtasks.get(0), "Подзадачи не совпадают.");
    }

    @Test
    @DisplayName("тест удаления подзадачи по несуществующему id")
    public void deleteWrongSubtask() {
        taskManager.createEpic(epic3);
        taskManager.createSubtask(subtask4);
        taskManager.deleteSubtask(100);
        final List<Subtask> subtasks = taskManager.getAllSubtask();

        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask4, subtasks.get(0), "Подзадачи не совпадают.");
    }

    @Test
    @DisplayName("тест обновления статуса задачи")
    public void updateStatusTask() {
        taskManager.createTask(task1);
        taskManager.updateStatusTask(task1, StatusTask.IN_PROGRESS);

        assertEquals(StatusTask.IN_PROGRESS, task1.getStatusTask(),
                "Не обновился статус задачи");
    }

    @Test
    @DisplayName("тест обновления статуса нулевой задачи")
    public void updateStatusNullTask() {
        Task task = null;
        taskManager.updateStatusTask(task, StatusTask.IN_PROGRESS);

        assertNull(task, "У нулевой задачи статус доступен");
    }


    @Test
    @DisplayName("тест обновления статуса эпика")
    public void updateStatusEpic() {
        taskManager.createEpic(epic3);
        taskManager.createSubtask(subtask4);
        taskManager.createSubtask(subtask5);
        taskManager.createEpic(epic7);

        assertEquals(StatusTask.NEW, epic3.getStatusTask(),
                "Все подзадачи со статусом NEW, статус эпик неверен");
        assertEquals(StatusTask.NEW, epic7.getStatusTask(),
                "Пустой список подзадач, статус эпик неверен");

        taskManager.updateStatusSubtask(subtask4, StatusTask.DONE);
        assertEquals(StatusTask.IN_PROGRESS, epic3.getStatusTask(),
                "Все подзадачи со статусом NEW и DONE, статус эпик неверен");

        taskManager.updateStatusSubtask(subtask5, StatusTask.DONE);
        assertEquals(StatusTask.DONE, epic3.getStatusTask(),
                "Все подзадачи со DONE, статус эпик неверен");

        taskManager.updateStatusSubtask(subtask4, StatusTask.IN_PROGRESS);
        taskManager.updateStatusSubtask(subtask5, StatusTask.IN_PROGRESS);
        assertEquals(StatusTask.IN_PROGRESS, epic3.getStatusTask(),
                "Все подзадачи со статусом IN_PROGRESS, статус эпик неверен");
    }

    @Test
    @DisplayName("тест обновления статуса нулевого эпика")
    public void updateStatusNullEpic() {
        Epic epic = null;
        taskManager.updateStatusEpic(epic);

        assertNull(epic, "У нулевого эпика статус доступен");
    }

    @Test
    @DisplayName("тест обновления статуса подзадачи")
    public void updateStatusSubtask() {
        taskManager.createEpic(epic3);
        taskManager.createSubtask(subtask4);
        taskManager.updateStatusSubtask(subtask4, StatusTask.IN_PROGRESS);

        assertEquals(StatusTask.IN_PROGRESS, subtask4.getStatusTask(),
                "Не обновился статус подзадачи");
        assertEquals(StatusTask.IN_PROGRESS, epic3.getStatusTask(),
                "Не обновился статус эпика");
    }

    @Test
    @DisplayName("тест обновления статуса нулевой подзадачи")
    public void updateStatusNullSubtask() {
        Subtask subtask = null;
        taskManager.updateStatusSubtask(subtask, StatusTask.IN_PROGRESS);

        assertNull(subtask, "У нулевой задачи статус доступен");
    }

    @Test
    @DisplayName("тест получения отсортированного по startTime задач и подзадач")
    public void getPrioritizedTasks() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createEpic(epic3);
        taskManager.createSubtask(subtask4);
        taskManager.createSubtask(subtask5);
        final List<Task> sortList = taskManager.getPrioritizedTasks();

        assertNotNull(sortList, "Список сортировки не создан");
        assertEquals(4, sortList.size(), "Размер списка сортировки не верен");
        assertEquals(subtask4, sortList.get(0), "Сортировка некорректна");
        assertEquals(subtask5, sortList.get(1), "Сортировка некорректна");
        assertEquals(task2, sortList.get(2), "Сортировка некорректна");
        assertEquals(task1, sortList.get(3), "Сортировка некорректна");
    }
}