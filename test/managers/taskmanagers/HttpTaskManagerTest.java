package managers.taskmanagers;

import managers.Managers;
import server.KVServer;
import tasks.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {
    private KVServer server;
    private HttpClient httpClient;
    private String key;
    private final Gson gson = Managers.getGson();

    @BeforeEach
    public void beforeEach() throws IOException {
        server = new KVServer();
        server.start();
        taskManager = new HttpTaskManager();
        httpClient = HttpClient.newHttpClient();
        key = taskManager.client.getKey();

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

    @AfterEach
    public void afterEach() {
        server.stop();
    }

    @Test
    @DisplayName("тест сохранения текущего состояния менеджера на сервер")
    public void save() throws IOException, InterruptedException {
        loadTestData();
        URI uri = URI.create("http://localhost:8078/load/" + key + "?API_TOKEN=DEBUG");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        final HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        Type userType = new TypeToken<Map<String, List<Task>>>() {
        }.getType();
        Map<String, List<Task>> mapHttpTask = new HashMap<>(gson.fromJson(response.body(), userType));
        List<Task> tasks = new ArrayList<>(mapHttpTask.get("tasks"));
        List<Task> history = new ArrayList<>(mapHttpTask.get("history"));

        assertEquals(6, tasks.size(), "Неверное количество задач");
        assertEquals(task1, tasks.get(0), "Неверно сохранена задача");
        assertEquals(epic3, tasks.get(1), "Неверно сохранен эпик");
        assertEquals(epic7, tasks.get(2), "Неверно сохранен эпик");
        assertEquals(subtask4, tasks.get(3), "Неверно сохранена подзадача");
        assertEquals(subtask5, tasks.get(4), "Неверно сохранена подзадача");
        assertEquals(subtask6, tasks.get(5), "Неверно сохранена подзадача");

        assertEquals(3, history.size(), "История не пустая.");
        assertEquals(subtask6, history.get(0), "История неправильно формируется.");
        assertEquals(task1, history.get(1), "История неправильно формируется.");
        assertEquals(epic3, history.get(2), "История неправильно формируется.");
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

    @Test
    @DisplayName("тест заполнения с сервера")
    public void loadFromFile() {
        loadTestData();
        Map<String, List<Task>> mapHttpTask = new HashMap<>(taskManager.client.load(key));
        List<Task> tasks = new ArrayList<>(mapHttpTask.get("tasks"));
        List<Task> history = new ArrayList<>(mapHttpTask.get("history"));

        assertEquals(6, tasks.size(), "Неверное количество задач");
        assertEquals(task1, tasks.get(0), "Неверно сохранена задача");
        assertEquals(epic3, tasks.get(1), "Неверно сохранен эпик");
        assertEquals(epic7, tasks.get(2), "Неверно сохранен эпик");
        assertEquals(subtask4, tasks.get(3), "Неверно сохранена подзадача");
        assertEquals(subtask5, tasks.get(4), "Неверно сохранена подзадача");
        assertEquals(subtask6, tasks.get(5), "Неверно сохранена подзадача");

        assertEquals(3, history.size(), "История не пустая.");
        assertEquals(subtask6, history.get(0), "История неправильно формируется.");
        assertEquals(task1, history.get(1), "История неправильно формируется.");
        assertEquals(epic3, history.get(2), "История неправильно формируется.");
    }

    @Test
    @DisplayName("тест заполнения с пустого сервера")
    public void loadFromEmpty() {
        Map<String, List<Task>> mapTasks = new HashMap<>(taskManager.client.load(key));

        assertEquals(0, mapTasks.size());
    }
}