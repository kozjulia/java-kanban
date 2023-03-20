package managers.taskmanagers;

import tasks.Task;
import tasks.Epic;
import tasks.Subtask;
import tasks.StatusTask;
import managers.exception.ManagerSaveException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    @BeforeEach
    public void beforeEach() {
        taskManager = new FileBackedTasksManager("resources" + File.separator + "data.csv");
        Task.setNextId(0); // обновляем внутренний счётчик сквозной нумерации
        task1 = new Task("Test task1", "Test task1 description", LocalDateTime.MAX, 15);
        task2 = new Task("Test task2", "Test task2 description",
                LocalDateTime.of(2023, 03, 19, 10, 00), 30);
        epic3 = new Epic("Test epic3", "Test epic3 description");
        subtask4 = new Subtask("Test subtask4", "Test subtask4 description", epic3.getId(),
                LocalDateTime.of(2023, 03, 15, 10, 00), 15);
        subtask5 = new Subtask("Test subtask5", "Test subtask5 description", epic3.getId(),
                LocalDateTime.of(2023, 03, 15, 12, 00), 15);
        subtask6 = new Subtask("Test subtask6", "Test subtask6 description", epic3.getId(),
                LocalDateTime.of(2023, 03, 16, 11, 00), 45);
        epic7 = new Epic("Test epic7", "Test epic7 description");
    }

    @Test
    @DisplayName("тест сохранения текущего состояния менеджера в указанный файл")
    public void save() {
        loadTestData();
        // построчно собираю файл для будущей проверки
        final List<String> linesFile = new ArrayList<>(getLinesFile());

        // проверяю правильное построчное сохранение
        assertNotNull(linesFile, "Файл пуст");
        assertEquals(9, linesFile.size(), "Неверное количество строк в файле");
        assertEquals("id,type,name,status,description,epic,startTime,duration,endTime",
                linesFile.get(0), "Неверно сохранена строка 0");
        assertEquals("1,TASK,Test task1,NEW,Test task1 description,,31.12.+999999999 23:59:59,15,",
                linesFile.get(1), "Неверно сохранена строка 1");
        assertEquals("3,EPIC,Test epic3,IN_PROGRESS,Test epic3 description,,15.03.2023 10:00:00,75," +
                "16.03.2023 11:45:00", linesFile.get(2), "Неверно сохранена строка 2");
        assertEquals("7,EPIC,Test epic7,NEW,Test epic7 description,,31.12.+999999999 23:59:59,0," +
                "31.12.+999999999 23:59:59", linesFile.get(3), "Неверно сохранена строка 3");
        assertEquals("4,SUBTASK,Test subtask4,NEW,Test subtask4 description,3,15.03.2023 10:00:00,15,",
                linesFile.get(4), "Неверно сохранена строка 4");
        assertEquals("5,SUBTASK,Test subtask5,NEW,Test subtask5 description,3,15.03.2023 12:00:00,15,",
                linesFile.get(5), "Неверно сохранена строка 5");
        assertEquals("6,SUBTASK,Test subtask6,DONE,Test subtask6 description,3,16.03.2023 11:00:00,45,",
                linesFile.get(6), "Неверно сохранена строка 6");
        assertEquals("", linesFile.get(7), "Неверно сохранена строка 7");
        assertEquals("6,1,3", linesFile.get(8), "Неверно сохранена строка 8");
    }

    private void loadTestData() {
        // загружаю тестовые данные
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
        try (BufferedReader fileReader = new BufferedReader(new FileReader(
                taskManager.file, StandardCharsets.UTF_8))) {
            linesFile.add(fileReader.readLine());
            // params = [id,type,name,status,description,epic,startTime,duration,endTime]
            while (fileReader.ready()) {
                linesFile.add(fileReader.readLine());
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения файла");
        }
        return linesFile;
    }

    @Test
    @DisplayName("тест сохранения пустого менеджера в указанный файл")
    public void saveEmpty() {
        taskManager.createTask(task1);
        taskManager.deleteTask(1);
        // построчно собираю файл для будущей проверки
        final List<String> linesFile = new ArrayList<>(getLinesFile());

        // проверяю правильное построчное сохранение
        assertNotNull(linesFile, "Файл пуст");
        assertEquals(2, linesFile.size(), "Неверное количество строк в файле");
        assertEquals("id,type,name,status,description,epic,startTime,duration,endTime",
                linesFile.get(0), "Неверно сохранена строка 0");
        assertEquals("", linesFile.get(1), "Неверно сохранена строка 1");
    }

    @Test
    @DisplayName("тест сохранения 1 эпика в указанный файл")
    public void save1Epic() {
        taskManager.createEpic(epic3);
        // построчно собираю файл для будущей проверки
        final List<String> linesFile = new ArrayList<>(getLinesFile());

        // проверяю правильное построчное сохранение
        assertNotNull(linesFile, "Файл пуст");
        assertEquals(3, linesFile.size(), "Неверное количество строк в файле");
        assertEquals("id,type,name,status,description,epic,startTime,duration,endTime",
                linesFile.get(0), "Неверно сохранена строка 0");
        assertEquals("3,EPIC,Test epic3,NEW,Test epic3 description,,31.12.+999999999 23:59:59,0," +
                "31.12.+999999999 23:59:59", linesFile.get(1), "Неверно сохранена строка 1");
        assertEquals("", linesFile.get(2), "Неверно сохранена строка 2");
    }

    @Test
    @DisplayName("тест заполнения из csv-файла")
    public void loadFromFile() {
        loadTestData();
        FileBackedTasksManager.loadFromFile("resources" + File.separator + "data.csv");
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
        assertEquals(subtask6, listHistory.get(0), "Неверная история");
        assertEquals(task1, listHistory.get(1), "Неверная история");
        assertEquals(epic3, listHistory.get(2), "Неверная история");
    }

    @Test
    @DisplayName("тест заполнения из пустого csv-файла")
    public void loadFromEmptyFile() {
        taskManager.createTask(task1);
        taskManager.deleteTask(1);
        FileBackedTasksManager.loadFromFile("resources" + File.separator + "data.csv");
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
    @DisplayName("тест заполнения из csv-файла с 1 эпиком")
    public void loadFromFile1Epic() {
        taskManager.createEpic(epic3);
        FileBackedTasksManager.loadFromFile("resources" + File.separator + "data.csv");
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