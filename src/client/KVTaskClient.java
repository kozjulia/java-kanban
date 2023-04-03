package client;

import managers.Managers;
import server.KVServer;
import tasks.Task;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

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

// HTTP-клиент для работы с хранилищем
public class KVTaskClient {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String url;
    private final Gson gson = Managers.getGson();
    // при регистрации выдаётся токен (API_TOKEN), который нужен при работе с сервером
    private static final String API_TOKEN = "DEBUG";
    private final String key;

    public KVTaskClient() {
        url = "http://localhost:";
        key = this.register();
    }

    public KVTaskClient(String url) {
        this.url = url;
        key = this.register();
    }

    public String getKey() {
        return key;
    }

    public static void main(String[] args) throws IOException {
        KVServer kvServer = new KVServer();
        kvServer.start();
        KVTaskClient client = new KVTaskClient();
        testSprint(client);  // тестовые данные для 8-го ТЗ
        kvServer.stop();
    }

    private static void testSprint(KVTaskClient client) {
        Task task1 = new Task("Task1", "Task1 descr",
                LocalDateTime.of(2023, 03, 19, 10, 00), 30);
        List<Task> listTask1 = new ArrayList<>();
        listTask1.add(task1);
        Map<String, List<Task>> mapHttpTask = new HashMap<>();
        mapHttpTask.put("tasks", listTask1);
        client.put(client.getKey(), client.gson.toJson(mapHttpTask));

        System.out.print("\nДля ключа = " + client.key + " добавлен новый объект:");
        System.out.println(client.load(client.key));

        String key2 = "1111111111";
        Task task2 = new Task("Task2", "Task2 descr",
                LocalDateTime.of(2023, 03, 20, 10, 15), 45);
        List<Task> listTask2 = new ArrayList<>();
        listTask2.add(task2);
        Map<String, List<Task>> mapHttpTask2 = new HashMap<>();
        mapHttpTask2.put("tasks", listTask2);
        client.put(key2, client.gson.toJson(mapHttpTask2));
        System.out.print("\nДля ключа = " + key2 + " добавлен новый объект:");
        System.out.println(client.load(key2));

        System.out.print("\nДля ключа = " + client.key + " загружен объект:");
        System.out.println(client.load(client.key));
    }

    private String register() {
        URI uri = URI.create(url + KVServer.PORT + "/register");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        String key = "";
        try {
            final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                JsonElement jsonElement = JsonParser.parseString(response.body());
                key = jsonElement.getAsString();
            } else {
                System.out.println("Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode());
            }
        } catch (NullPointerException | IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
        return key;
    }

    public void put(String key, String json) {
        URI uri = URI.create(url + KVServer.PORT + "/save/" + key + "?API_TOKEN=" + API_TOKEN);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(body)
                .build();

        try {
            final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 201) {
                System.out.println("Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode());
            }
        } catch (NullPointerException | IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    public Map<String, List<Task>> load(String key) {
        URI uri = URI.create(url + KVServer.PORT + "/load/" + key + "?API_TOKEN=" + API_TOKEN);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        Map<String, List<Task>> mapHttpTask = new HashMap<>();
        try {
            final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                Type userType = new TypeToken<Map<String, List<Task>>>() {
                }.getType();
                mapHttpTask = gson.fromJson(response.body(), userType);

            } else {
                System.out.println("Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode());
            }
        } catch (NullPointerException | IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
        return mapHttpTask;
    }
}