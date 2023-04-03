package server;

import managers.Managers;
import managers.taskmanagers.FileBackedTasksManager;
import managers.taskmanagers.TaskManager;
import tasks.Task;
import tasks.Epic;
import tasks.Subtask;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

class HttpTaskServerTest {
    private HttpTaskServer server;
    private HttpClient client;
    private final Gson gson = Managers.getGson();
    private TaskManager taskManager;
    private Task task1;
    private Task task2;
    private Epic epic3;
    private Subtask subtask4;
    private Subtask subtask5;
    private Subtask subtask6;
    private Epic epic7;

    @BeforeEach
    void beforeEach() throws IOException {
        taskManager = new FileBackedTasksManager();
        server = new HttpTaskServer(taskManager);
        server.start();
        client = HttpClient.newHttpClient();

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
    void afterEach() {
        server.stop();
    }

    @DisplayName("получить все задачи")
    @Test
    void getTasks() throws IOException, InterruptedException {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        URI url = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder().
                uri(url).
                GET().
                build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type userType = new TypeToken<ArrayList<Task>>() {
        }.getType();
        List<Task> actual = gson.fromJson(response.body(), userType);

        assertEquals(200, response.statusCode());
        assertEquals(2, actual.size(), "Неверное количество задач.");
        assertTrue(actual.contains(task1), "Задача не записалась.");
        assertTrue(actual.contains(task2), "Задача не записалась.");
    }

    @DisplayName("получить задачу по id")
    @Test
    void getTaskById() throws IOException, InterruptedException {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        URI url = URI.create("http://localhost:8080/tasks/task/?id=1");
        HttpRequest request = HttpRequest.newBuilder().
                uri(url).
                GET().
                build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type userType = new TypeToken<Task>() {
        }.getType();
        Task actual = gson.fromJson(response.body(), userType);

        assertEquals(200, response.statusCode());
        assertNotNull(actual, "Задача не возвращается.");
        assertEquals(task1, actual, "Задача не совпадает.");
    }

    @DisplayName("получить задачу по несуществующему id")
    @Test
    void getTaskByWrongId() throws IOException, InterruptedException {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        URI url = URI.create("http://localhost:8080/tasks/task/?id=8");
        HttpRequest request = HttpRequest.newBuilder().
                uri(url).
                GET().
                build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
        assertEquals("Задача с идентификатором 8 не найдена", response.body());
    }

    @DisplayName("создать/обновить задачу")
    @Test
    void createTask() throws IOException, InterruptedException {
        String json = gson.toJson(task1);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        URI url = URI.create("http://localhost:8080/tasks/task/");

        HttpRequest request = HttpRequest.newBuilder().
                uri(url).
                POST(body).
                build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
    }

    @DisplayName("создать/обновить задачу с некорректным JSON")
    @Test
    void createWrongTask() throws IOException, InterruptedException {
        String json = "{wrong}";
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        URI url = URI.create("http://localhost:8080/tasks/task/");

        HttpRequest request = HttpRequest.newBuilder().
                uri(url).
                POST(body).
                build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
        assertEquals("Получен некорректный JSON задачи", response.body());
    }

    @DisplayName("удалить все задачи")
    @Test
    void deleteTasks() throws IOException, InterruptedException {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        URI url = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder().
                uri(url).
                DELETE().
                build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }

    @DisplayName("удалить задачу по id")
    @Test
    void deleteTaskById() throws IOException, InterruptedException {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        URI url = URI.create("http://localhost:8080/tasks/task/?id=1");
        HttpRequest request = HttpRequest.newBuilder().
                uri(url).
                DELETE().
                build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }

    @DisplayName("удалить задачу по несуществующему id")
    @Test
    void deleteWrongTaskById() throws IOException, InterruptedException {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        URI url = URI.create("http://localhost:8080/tasks/task/?id=8");
        HttpRequest request = HttpRequest.newBuilder().
                uri(url).
                DELETE().
                build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(405, response.statusCode());
        assertEquals("Задача с идентификатором 8 не найдена и не удалена", response.body());
    }

    @DisplayName("получить все эпики")
    @Test
    void getEpicks() throws IOException, InterruptedException {
        taskManager.createEpic(epic3);
        taskManager.createEpic(epic7);
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        HttpRequest request = HttpRequest.newBuilder().
                uri(url).
                GET().
                build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type userType = new TypeToken<ArrayList<Task>>() {
        }.getType();
        List<Task> actual = gson.fromJson(response.body(), userType);

        assertEquals(200, response.statusCode());
        assertEquals(2, actual.size(), "Неверное количество задач.");
        assertTrue(actual.contains(epic3), "Эпик не записался.");
        assertTrue(actual.contains(epic7), "Эпик не записался.");
    }

    @DisplayName("получить эпик по id")
    @Test
    void getEpicById() throws IOException, InterruptedException {
        taskManager.createEpic(epic3);
        taskManager.createEpic(epic7);
        URI url = URI.create("http://localhost:8080/tasks/epic/?id=3");
        HttpRequest request = HttpRequest.newBuilder().
                uri(url).
                GET().
                build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type userType = new TypeToken<Task>() {
        }.getType();
        Task actual = gson.fromJson(response.body(), userType);

        assertEquals(200, response.statusCode());
        assertNotNull(actual, "Эпик не возвращается.");
        assertEquals(epic3, actual, "Эпик не совпадает.");
    }

    @DisplayName("создать/обновить эпик")
    @Test
    void createEpic() throws IOException, InterruptedException {
        String json = gson.toJson(epic3);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        URI url = URI.create("http://localhost:8080/tasks/epic/");

        HttpRequest request = HttpRequest.newBuilder().
                uri(url).
                POST(body).
                build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
    }

    @DisplayName("удалить все эпики")
    @Test
    void deleteEpics() throws IOException, InterruptedException {
        taskManager.createTask(epic3);
        taskManager.createTask(epic7);
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        HttpRequest request = HttpRequest.newBuilder().
                uri(url).
                DELETE().
                build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }

    @DisplayName("удалить эпик по id")
    @Test
    void deleteEpicById() throws IOException, InterruptedException {
        taskManager.createEpic(epic3);
        taskManager.createEpic(epic7);
        URI url = URI.create("http://localhost:8080/tasks/epic/?id=7");
        HttpRequest request = HttpRequest.newBuilder().
                uri(url).
                DELETE().
                build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }

    @DisplayName("получить все подзадачи")
    @Test
    void getSubtasks() throws IOException, InterruptedException {
        taskManager.createEpic(epic3);
        taskManager.createSubtask(subtask4);
        taskManager.createSubtask(subtask5);
        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        HttpRequest request = HttpRequest.newBuilder().
                uri(url).
                GET().
                build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type userType = new TypeToken<ArrayList<Task>>() {
        }.getType();
        List<Task> actual = gson.fromJson(response.body(), userType);

        assertEquals(200, response.statusCode());
        assertEquals(2, actual.size(), "Неверное количество подзадач.");
        assertTrue(actual.contains(subtask4), "Подзадача не записалась.");
        assertTrue(actual.contains(subtask5), "Подзадача не записалась.");
    }

    @DisplayName("получить подзадачу по id")
    @Test
    void getSubtaskById() throws IOException, InterruptedException {
        taskManager.createEpic(epic3);
        taskManager.createSubtask(subtask4);
        taskManager.createSubtask(subtask5);
        URI url = URI.create("http://localhost:8080/tasks/subtask/?id=4");
        HttpRequest request = HttpRequest.newBuilder().
                uri(url).
                GET().
                build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type userType = new TypeToken<Task>() {
        }.getType();
        Task actual = gson.fromJson(response.body(), userType);

        assertEquals(200, response.statusCode());
        assertNotNull(actual, "Подзадача не возвращается.");
        assertEquals(subtask4, actual, "Подзадача не совпадает.");
    }

    @DisplayName("создать/обновить подзадачу")
    @Test
    void createSubtask() throws IOException, InterruptedException {
        String json = gson.toJson(subtask4);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        URI url = URI.create("http://localhost:8080/tasks/subtask/");

        HttpRequest request = HttpRequest.newBuilder().
                uri(url).
                POST(body).
                build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
    }

    @DisplayName("удалить все подзадачи")
    @Test
    void deleteSubtasks() throws IOException, InterruptedException {
        taskManager.createEpic(epic3);
        taskManager.createSubtask(subtask4);
        taskManager.createSubtask(subtask5);
        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        HttpRequest request = HttpRequest.newBuilder().
                uri(url).
                DELETE().
                build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }

    @DisplayName("удалить подзадачу по id")
    @Test
    void deleteSubtaskById() throws IOException, InterruptedException {
        taskManager.createEpic(epic3);
        taskManager.createSubtask(subtask4);
        taskManager.createSubtask(subtask5);
        taskManager.createSubtask(subtask6);
        URI url = URI.create("http://localhost:8080/tasks/subtask/?id=5");
        HttpRequest request = HttpRequest.newBuilder().
                uri(url).
                DELETE().
                build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }

    @DisplayName("получить список подзадач по id эпика")
    @Test
    void getSubtaskByEpic() throws IOException, InterruptedException {
        taskManager.createEpic(epic3);
        taskManager.createSubtask(subtask4);
        taskManager.createSubtask(subtask5);
        taskManager.createSubtask(subtask6);
        URI url = URI.create("http://localhost:8080/tasks/subtask/epic/?id=3");
        HttpRequest request = HttpRequest.newBuilder().
                uri(url).
                GET().
                build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type userType = new TypeToken<ArrayList<Task>>() {
        }.getType();
        List<Task> actual = gson.fromJson(response.body(), userType);

        assertEquals(200, response.statusCode());
        assertEquals(3, actual.size(), "Неверное количество подзадач.");
        assertTrue(actual.contains(subtask4), "Подзадача не получена.");
        assertTrue(actual.contains(subtask5), "Подзадача не получена.");
        assertTrue(actual.contains(subtask6), "Подзадача не получена.");
    }

    @DisplayName("получить список подзадач по несуществующему id эпика")
    @Test
    void getSubtaskByWrongEpic() throws IOException, InterruptedException {
        taskManager.createEpic(epic3);
        taskManager.createSubtask(subtask4);
        URI url = URI.create("http://localhost:8080/tasks/subtask/epic/?id=5");
        HttpRequest request = HttpRequest.newBuilder().
                uri(url).
                GET().
                build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
        assertEquals("Эпик с идентификатором 5 не найден, невозможно получить список его подзадач",
                response.body());
    }

    @DisplayName("получить историю")
    @Test
    void getHistory() throws IOException, InterruptedException {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createEpic(epic3);
        taskManager.getTask(1);
        taskManager.getEpic(3);
        taskManager.getTask(2);

        URI url = URI.create("http://localhost:8080/tasks/history");
        HttpRequest request = HttpRequest.newBuilder().
                uri(url).
                GET().
                build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Type userType = new TypeToken<ArrayList<Task>>() {
        }.getType();
        List<Task> actual = gson.fromJson(response.body(), userType);

        assertEquals(200, response.statusCode());
        assertEquals(3, actual.size(), "История не пустая.");
        assertEquals(task2, actual.get(0), "История неправильно формируется.");
        assertEquals(epic3, actual.get(1), "История неправильно формируется.");
        assertEquals(task1, actual.get(2), "История неправильно формируется.");
    }

    @DisplayName("получить все задачи по приоритету")
    @Test
    void getPriority() throws IOException, InterruptedException {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createEpic(epic3);
        taskManager.createSubtask(subtask4);
        taskManager.createSubtask(subtask5);
        taskManager.createSubtask(subtask6);

        URI url = URI.create("http://localhost:8080/tasks/");
        HttpRequest request = HttpRequest.newBuilder().
                uri(url).
                GET().
                build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Type userType = new TypeToken<ArrayList<Task>>() {
        }.getType();
        List<Task> actual = gson.fromJson(response.body(), userType);

        assertEquals(200, response.statusCode());
        assertEquals(5, actual.size(), "Список приоритета не пустой.");
        assertEquals(subtask4, actual.get(0), "Список приоритета неправильно формируется.");
        assertEquals(subtask5, actual.get(1), "Список приоритета неправильно формируется.");
        assertEquals(subtask6, actual.get(2), "Список приоритета неправильно формируется.");
        assertEquals(task2, actual.get(3), "Список приоритета неправильно формируется.");
        assertEquals(task1, actual.get(4), "Список приоритета неправильно формируется.");
    }

    @DisplayName("неизвестный эндпоинт")
    @Test
    void getUnknown() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasksunknown");
        HttpRequest request = HttpRequest.newBuilder().
                uri(url).
                DELETE().
                build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
        assertEquals("Неизвестный эндпоинт", response.body());
    }
}