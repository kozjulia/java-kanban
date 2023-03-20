package managers.taskmanagers;

import tasks.Task;
import tasks.Epic;
import tasks.Subtask;
import tasks.StatusTask;
import managers.exception.SaveTaskException;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    // тесты для абстрактного класса TaskManagerTest которые одинаковы у всех классов-наследников
    public T taskManager;
    public static Task task1;
    public static Task task2;
    public static Epic epic3;
    public static Subtask subtask4;
    public static Subtask subtask5;
    public static Subtask subtask6;
    public static Epic epic7;

    // устанавливаем тип менеджера в наследнике
    public abstract void setTaskManager();

    @Test
    public void getAllTask() {
        // тест получения списка всех задач
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        final List<Task> tasks = taskManager.getAllTask();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(2, tasks.size(), "Неверное количество задач.");
        assertTrue(tasks.contains(task1), "Задача не записалась.");
        assertTrue(tasks.contains(task2), "Задача не записалась.");
    }

    @Test
    public void getAllNullTask() {
        // тест получения пустого списка всех задач
        final List<Task> tasks = taskManager.getAllTask();

        assertTrue(tasks.isEmpty(), "Задачи возвращаются.");
        assertEquals(0, tasks.size(), "Неверное количество задач.");
    }

    @Test
    public void deleteAllTask() {
        // тест удаления списка всех задач
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.deleteAllTask();
        final List<Task> tasks = taskManager.getAllTask();

        assertTrue(tasks.isEmpty(), "Задачи возвращаются.");
        assertEquals(0, tasks.size(), "Неверное количество задач.");
    }

    @Test
    public void deleteAllNullTask() {
        // тест удаления пустого списка всех задач
        taskManager.deleteAllTask();
        final List<Task> tasks = taskManager.getAllTask();

        assertTrue(tasks.isEmpty(), "Задачи возвращаются.");
        assertEquals(0, tasks.size(), "Неверное количество задач.");
    }

    @Test
    public void getTask() {
        // тест получения задачи по id
        final int idTask = taskManager.createTask(task1);
        Task getTask = taskManager.getTask(idTask);

        assertNotNull(getTask, "Задача не найдена.");
        assertEquals(task1, getTask, "Задачи не совпадают.");
    }

    @Test
    public void getWrongTask() {
        // тест получения задачи по несуществующему id
        taskManager.createTask(task1);
        Task getTask = taskManager.getTask(100);

        assertNull(getTask, "Задача найдена.");
    }

    @Test
    public void createTask() {
        // тест создания задачи
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
    public void createNullTask() {
        // тест создания пустой задачи
        Task taskNull = null;
        final int idTask = taskManager.createTask(taskNull);
        final List<Task> tasks = taskManager.getAllTask();

        assertEquals(0, idTask, "Нулевая задача создалась");
        assertTrue(tasks.isEmpty(), "Нулевые задачи возвращаются.");
        assertEquals(0, tasks.size(), "Неверное количество нулевых задач.");
    }

    @Test
    public void createCrossTask() {
        // тест создания пересекающейся во времени задачи
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
    public void updateTask() {
        // тест обновления задачи
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
    public void updateNullTask() {
        // тест обновления пустой задачи
        Task updateTask = null;
        taskManager.updateTask(updateTask);
        final List<Task> tasks = taskManager.getAllTask();

        assertTrue(tasks.isEmpty(), "Нулевая задача возвращается.");
        assertEquals(0, tasks.size(), "Неверное количество нулевых задач.");
    }

    @Test
    public void updateCrossTask() {
        // тест обновления пересекающейся во времени задачи
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
    public void deleteTask() {
        // тест удаления задачи по id
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
    public void deleteWrongTask() {
        // тест удаления задачи по несуществующему id
        taskManager.createTask(task1);
        taskManager.deleteTask(100);
        final List<Task> tasks = taskManager.getAllTask();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task1, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    public void getAllEpic() {
        // тест получения списка всех эпиков
        taskManager.createEpic(epic3);
        taskManager.createEpic(epic7);
        final List<Epic> epics = taskManager.getAllEpic();

        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(2, epics.size(), "Неверное количество эпиков.");
        assertTrue(epics.contains(epic3), "Эпик не записался.");
        assertTrue(epics.contains(epic7), "Эпик не записался.");
    }

    @Test
    public void getAllNullEpic() {
        // тест получения пустого списка всех эпиков
        final List<Epic> epics = taskManager.getAllEpic();

        assertTrue(epics.isEmpty(), "Эпики возвращаются.");
        assertEquals(0, epics.size(), "Неверное количество эпиков.");
    }

    @Test
    public void deleteAllEpic() {
        // тест удаления списка всех эпиков
        taskManager.createEpic(epic3);
        taskManager.createEpic(epic7);
        taskManager.deleteAllEpic();
        final List<Epic> epics = taskManager.getAllEpic();

        assertTrue(epics.isEmpty(), "Эпики возвращаются.");
        assertEquals(0, epics.size(), "Неверное количество эпиков.");
    }

    @Test
    public void deleteAllNullEpic() {
        // тест удаления пустого списка всех эпиков
        taskManager.deleteAllEpic();
        final List<Epic> epics = taskManager.getAllEpic();

        assertTrue(epics.isEmpty(), "Эпики возвращаются.");
        assertEquals(0, epics.size(), "Неверное количество эпиков.");
    }

    @Test
    public void getEpic() {
        // тест получения эпика по id
        final int idEpic = taskManager.createEpic(epic3);
        Epic getEpic = taskManager.getEpic(idEpic);

        assertNotNull(getEpic, "Эпик не найден.");
        assertEquals(epic3, getEpic, "Эпики не совпадают.");
    }

    @Test
    public void getWrongEpic() {
        // тест получения эпика по несуществующему id
        taskManager.createEpic(epic3);
        Epic getEpic = taskManager.getEpic(100);

        assertNull(getEpic, "Эпик найден.");
    }

    @Test
    public void createEpic() {
        // тест создания эпика
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
    public void createEpicAndSubtask() {
        // тест создания эпика и подзадачи для проверки правильности расчёта времени начала,
        // окончания и продолжительности эпика
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
    public void createNullEpic() {
        // тест создания пустого эпика
        Epic epicNull = null;
        final int idEpic = taskManager.createEpic(epicNull);
        final List<Epic> epics = taskManager.getAllEpic();

        assertEquals(0, idEpic, "Нулевой эпик создался");
        assertTrue(epics.isEmpty(), "Нулевые эпики возвращаются.");
        assertEquals(0, epics.size(), "Неверное количество нулевых эпиков.");
    }

    @Test
    public void updateEpic() {
        // тест обновления эпика
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
    public void updateNullEpic() {
        // тест обновления пустого эпика
        Epic updateEpic = null;
        taskManager.updateEpic(updateEpic);
        final List<Epic> epics = taskManager.getAllEpic();

        assertTrue(epics.isEmpty(), "Нулевой эпик возвращается.");
        assertEquals(0, epics.size(), "Неверное количество нулевых эпиков.");
    }

    @Test
    public void deleteEpic() {
        // тест удаления эпика по id
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
    public void deleteWrongEpic() {
        // тест удаления эпика по несуществующему id
        taskManager.createEpic(epic3);
        taskManager.deleteEpic(100);
        final List<Epic> epics = taskManager.getAllEpic();

        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic3, epics.get(0), "Эпики не совпадают.");
    }

    @Test
    public void getAllSubtask() {
        // тест получения списка всех подзадач
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
    public void getAllNullSubtask() {
        // тест получения пустого списка всех подзадач
        final List<Subtask> subtasks = taskManager.getAllSubtask();

        assertTrue(subtasks.isEmpty(), "Подзадачи возвращаются.");
        assertEquals(0, subtasks.size(), "Неверное количество подзадач.");
    }

    @Test
    public void deleteAllSubtask() {
        // тест удаления списка всех подзадач
        taskManager.createEpic(epic3);
        taskManager.createSubtask(subtask4);
        taskManager.createSubtask(subtask5);
        taskManager.deleteAllSubtask();
        final List<Subtask> subtasks = taskManager.getAllSubtask();

        assertTrue(subtasks.isEmpty(), "Задачи возвращаются.");
        assertEquals(0, subtasks.size(), "Неверное количество задач.");
    }

    @Test
    public void deleteAllNullSubtask() {
        // тест удаления пустого списка всех подзадач
        taskManager.deleteAllSubtask();
        final List<Subtask> subtasks = taskManager.getAllSubtask();

        assertTrue(subtasks.isEmpty(), "Задачи возвращаются.");
        assertEquals(0, subtasks.size(), "Неверное количество задач.");
    }

    @Test
    public void getSubtask() {
        // тест получения подзадачи по id
        taskManager.createEpic(epic3);
        final int idSubtask = taskManager.createSubtask(subtask4);
        Subtask getSubtask = taskManager.getSubtask(idSubtask);

        assertNotNull(getSubtask, "Подзадача не найдена.");
        assertEquals(subtask4, getSubtask, "Подзадачи не совпадают.");
    }

    @Test
    public void getWrongSubtask() {
        // тест получения подзадачи по несуществующему id
        taskManager.createEpic(epic3);
        taskManager.createSubtask(subtask4);
        Subtask getSubtask = taskManager.getSubtask(100);

        assertNull(getSubtask, "Подзадача найдена.");
    }


    @Test
    public void createSubtask() {
        // тест создания подзадачи
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
    public void createCrossSubtask() {
        // тест создания пересекающейся во времени подзадачи
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
    public void createNullSubtask() {
        // тест создания пустой подзадачи
        Subtask subtaskNull = null;
        final int idSubtask = taskManager.createSubtask(subtaskNull);
        final List<Subtask> subtasks = taskManager.getAllSubtask();

        assertEquals(0, idSubtask, "Нулевая подзадача создалась");
        assertTrue(subtasks.isEmpty(), "Нулевые подзадачи возвращаются.");
        assertEquals(0, subtasks.size(), "Неверное количество нулевых подзадач.");
    }

    @Test
    public void updateSubtask() {
        // тест обновления подзадачи
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
    public void updateNullSubtask() {
        // тест обновления пустой подзадачи
        Subtask updateSubtask = null;
        taskManager.updateSubtask(updateSubtask);
        final List<Subtask> subtasks = taskManager.getAllSubtask();

        assertTrue(subtasks.isEmpty(), "Нулевая подзадача возвращается.");
        assertEquals(0, subtasks.size(), "Неверное количество нулевых подзадач.");
    }

    @Test
    public void updateCrossSubtask() {
        // тест обновления пересекающейся во времени подзадачи
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
    public void deleteSubtask() {
        // тест удаления подзадачи по id
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
    public void deleteWrongSubtask() {
        // тест удаления подзадачи по несуществующему id
        taskManager.createEpic(epic3);
        taskManager.createSubtask(subtask4);
        taskManager.deleteSubtask(100);
        final List<Subtask> subtasks = taskManager.getAllSubtask();

        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask4, subtasks.get(0), "Подзадачи не совпадают.");
    }

    @Test
    public void updateStatusTask() {
        // обновление статуса задачи
        taskManager.createTask(task1);
        taskManager.updateStatusTask(task1, StatusTask.IN_PROGRESS);

        assertEquals(StatusTask.IN_PROGRESS, task1.getStatusTask(),
                "Не обновился статус задачи");
    }

    @Test
    public void updateStatusNullTask() {
        // обновление статуса нулевой задачи
        Task task = null;
        taskManager.updateStatusTask(task, StatusTask.IN_PROGRESS);

        assertNull(task, "У нулевой задачи статус доступен");
    }


    @Test
    public void updateStatusEpic() {
        // обновление статуса эпика
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
    public void updateStatusNullEpic() {
        // обновление статуса нулевого эпика
        Epic epic = null;
        taskManager.updateStatusEpic(epic);

        assertNull(epic, "У нулевого эпика статус доступен");
    }

    @Test
    public void updateStatusSubtask() {
        // обновление статуса подзадачи
        taskManager.createEpic(epic3);
        taskManager.createSubtask(subtask4);
        taskManager.updateStatusSubtask(subtask4, StatusTask.IN_PROGRESS);

        assertEquals(StatusTask.IN_PROGRESS, subtask4.getStatusTask(),
                "Не обновился статус подзадачи");
        assertEquals(StatusTask.IN_PROGRESS, epic3.getStatusTask(),
                "Не обновился статус эпика");
    }

    @Test
    public void updateStatusNullSubtask() {
        // обновление статуса нулевой подзадачи
        Subtask subtask = null;
        taskManager.updateStatusSubtask(subtask, StatusTask.IN_PROGRESS);

        assertNull(subtask, "У нулевой задачи статус доступен");
    }

    @Test
    public void getPrioritizedTasks() {
        // тест получения отсортированного по startTime задач и подзадач
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