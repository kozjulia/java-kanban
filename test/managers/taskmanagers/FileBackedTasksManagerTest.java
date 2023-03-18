package managers.taskmanagers;

import managers.exception.ManagerSaveException;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.StatusTask;
import tasks.Subtask;
import tasks.Task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {
    // тесты для класса InMemoryTaskManagerTest
    //private static TaskManager taskManager = new FileBackedTasksManager("resources" + File.separator + "data.csv");

    @Override
    public void setTaskManager() {
        taskManager = new FileBackedTasksManager("resources" + File.separator + "data.csv");
    }

    @BeforeEach
    // устанавливаем тип менеджера в наследнике
    /////// Очень хотелось бы сделать это с аннотацией BeforeAll, но он статик
    /////// испробовала всё, что знаю, не получилось. Может подскажешь?
    public void beforeEach() {
        setTaskManager();
        Task.setNextId(0); // обновляем внутренний счётчик сквозной нумерации
        task1 = new Task("Test task1", "Test task1 description");
        task2 = new Task("Test task2", "Test task2 description");
        epic3 = new Epic("Test epic3", "Test epic3 description");
        subtask4 = new Subtask("Test subtask4", "Test subtask4 description", epic3.getId());
        subtask5 = new Subtask("Test subtask5", "Test subtask5 description", epic3.getId());
        subtask6 = new Subtask("Test subtask6", "Test subtask6 description", epic3.getId());
        epic7 = new Epic("Test epic7", "Test epic7 description");
    }

    @Test
    public void save() {
        // тест сохранения текущего состояния менеджера в указанный файл
        loadTestData();

        // построчно собираю файл для будущей проверки
        final List<String> linesFile = new ArrayList<>(getLinesFile());

        // проверяю правильное построчное сохранение
        assertNotNull(linesFile, "Файл пуст");
        assertEquals(9, linesFile.size(), "Неверное количество строк в файле");
        assertEquals("id,type,name,status,description,epic", linesFile.get(0),
                "Неверно сохранена строка 0");
        assertEquals("1,TASK,Test task1,NEW,Test task1 description,", linesFile.get(1),
                "Неверно сохранена строка 1");
        assertEquals("3,EPIC,Test epic3,IN_PROGRESS,Test epic3 description,", linesFile.get(2),
                "Неверно сохранена строка 2");
        assertEquals("7,EPIC,Test epic7,NEW,Test epic7 description,", linesFile.get(3),
                "Неверно сохранена строка 3");
        assertEquals("4,SUBTASK,Test subtask4,NEW,Test subtask4 description,3", linesFile.get(4),
                "Неверно сохранена строка 4");
        assertEquals("5,SUBTASK,Test subtask5,NEW,Test subtask5 description,3", linesFile.get(5),
                "Неверно сохранена строка 5");
        assertEquals("6,SUBTASK,Test subtask6,DONE,Test subtask6 description,3", linesFile.get(6),
                "Неверно сохранена строка 6");
        assertEquals("", linesFile.get(7), "Неверно сохранена строка 7");
        assertEquals("3,6,1", linesFile.get(8), "Неверно сохранена строка 8");
    }

    private void loadTestData() {
        // загрузить тестовые данные
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createEpic(epic3);
        taskManager.createSubtask(subtask4);
        taskManager.createSubtask(subtask5);
        taskManager.createSubtask(subtask6);
        taskManager.createEpic(epic7);
        taskManager.getTask(1);
        taskManager.getEpic(3);
        taskManager.getTask(2);
        taskManager.getTask(1);
        taskManager.updateStatusSubtask(taskManager.getSubtask(6), StatusTask.DONE);
        taskManager.deleteTask(2);
    }

    private List<String> getLinesFile() {
        // построчно получаю файл csv для тестирования
        final List<String> linesFile = new ArrayList<>();
        try (BufferedReader fileReader = new BufferedReader(new FileReader(taskManager.file, StandardCharsets.UTF_8))) {
            linesFile.add(fileReader.readLine()); // params = [id,type,name,status,description,epic]
            while (fileReader.ready()) {
                linesFile.add(fileReader.readLine());
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения файла");
        }
        return linesFile;
    }

    @Test
    public void saveEmpty() {
        // тест сохранения пустого менеджера в указанный файл
        taskManager.createTask(task1);
        taskManager.deleteTask(1);

        // построчно собираю файл для будущей проверки
        final List<String> linesFile = new ArrayList<>(getLinesFile());

        // проверяю правильное построчное сохранение
        assertNotNull(linesFile, "Файл пуст");
        assertEquals(2, linesFile.size(), "Неверное количество строк в файле");
        assertEquals("id,type,name,status,description,epic", linesFile.get(0),
                "Неверно сохранена строка 0");
        assertEquals("", linesFile.get(1), "Неверно сохранена строка 1");
    }

    @Test
    public void save1Epic() {
        // тест сохранения 1 эпика в указанный файл
        taskManager.createEpic(epic3);

        // построчно собираю файл для будущей проверки
        final List<String> linesFile = new ArrayList<>(getLinesFile());

        // проверяю правильное построчное сохранение
        assertNotNull(linesFile, "Файл пуст");
        assertEquals(3, linesFile.size(), "Неверное количество строк в файле");
        assertEquals("id,type,name,status,description,epic", linesFile.get(0),
                "Неверно сохранена строка 0");
        assertEquals("3,EPIC,Test epic3,NEW,Test epic3 description,", linesFile.get(1),
                "Неверно сохранена строка 1");
        assertEquals("", linesFile.get(2), "Неверно сохранена строка 2");
    }

    @Test
    public void loadFromFile() {
        // тест заполнения из csv-файла
        loadTestData();
        taskManager.loadFromFile("resources" + File.separator + "data.csv");
        final List<Task> listTasks = taskManager.getAllTask();
        final List<Epic> listEpics = taskManager.getAllEpic();
        final List<Subtask> listSubtasks = taskManager.getAllSubtask();
        final List<Task> listHistory = taskManager.getHistory();

        assertNotNull(taskManager, "Менеджер пуст");
        assertEquals(1, listTasks.size(), "Неверное количество задач");
        assertEquals(task1, listTasks.get(0), "Неверная задача");
        assertEquals(2, listEpics.size(), "Неверное количество эпиков");
        assertTrue(listEpics.contains(epic3), "Неверный эпик");
        assertTrue(listEpics.contains(epic7), "Неверный эпик");
        assertEquals(3, listSubtasks.size(), "Неверное количество подзадач");
        assertTrue(listSubtasks.contains(subtask4), "Неверная подзадача");
        assertTrue(listSubtasks.contains(subtask5), "Неверная подзадача");
        assertTrue(listSubtasks.contains(subtask6), "Неверная подзадача");
        assertEquals(3, listHistory.size(), "Неверная история");
        assertEquals(epic3, listHistory.get(0), "Неверная история");
        assertEquals(subtask6, listHistory.get(1), "Неверная история");
        assertEquals(task1, listHistory.get(2), "Неверная история");
    }

    @Test
    public void loadFromEmptyFile() {
        // тест заполнения из пустого csv-файла
        taskManager.createTask(task1);
        taskManager.deleteTask(1);
        taskManager.loadFromFile("resources" + File.separator + "data.csv");
        final List<Task> listTasks = taskManager.getAllTask();
        final List<Epic> listEpics = taskManager.getAllEpic();
        final List<Subtask> listSubtasks = taskManager.getAllSubtask();
        final List<Task> listHistory = taskManager.getHistory();

        assertNotNull(taskManager, "Менеджер пуст");
        assertEquals(0, listTasks.size(), "Неверное количество задач");
        assertEquals(0, listEpics.size(), "Неверное количество эпиков");
        assertEquals(0, listSubtasks.size(), "Неверное количество подзадач");
        assertNull(listHistory, "Неверная история");
    }

    @Test
    public void loadFromFile1Epic() {
        // тест заполнения из csv-файла с 1 эпиком
        taskManager.createEpic(epic3);
        taskManager.loadFromFile("resources" + File.separator + "data.csv");
        final List<Task> listTasks = taskManager.getAllTask();
        final List<Epic> listEpics = taskManager.getAllEpic();
        final List<Subtask> listSubtasks = taskManager.getAllSubtask();
        final List<Task> listHistory = taskManager.getHistory();

        assertNotNull(taskManager, "Менеджер пуст");
        assertEquals(0, listTasks.size(), "Неверное количество задач");
        assertEquals(1, listEpics.size(), "Неверное количество эпиков");
        assertEquals(epic3, listEpics.get(0), "Неверный эпик");
        assertEquals(0, listSubtasks.size(), "Неверное количество подзадач");
        assertNull(listHistory, "Неверная история");
    }
}