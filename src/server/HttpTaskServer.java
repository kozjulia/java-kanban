package server;

import managers.Managers;
import managers.taskmanagers.TaskManager;
import tasks.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.net.InetSocketAddress;
import java.util.regex.Pattern;

import com.google.gson.*;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class HttpTaskServer {
    public static final int PORT = 8080;
    private final HttpServer server;
    private final Gson gson = Managers.getGson();
    private final TaskManager taskManager;

    public HttpTaskServer() throws IOException {
        this(Managers.getDefault());
    }

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        server = HttpServer.create();
        server.bind(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", new TaskHandler());
    }

    public static void main(String[] args) throws IOException {
        KVServer kvServer = new KVServer();
        kvServer.start();
        HttpTaskServer server = new HttpTaskServer();
        server.start();
        server.stop();
        kvServer.stop();
    }

    public void start() {
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
        System.out.println("http://localhost:" + PORT + "/tasks");
        server.start();
    }

    public void stop() {
        server.stop(0);
        System.out.println("Остановлен сервер на порту: " + PORT);
    }

    private class TaskHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Endpoint endpoint = getEndpoint(exchange.getRequestURI().toString(), exchange.getRequestMethod());

            switch (endpoint) {
                case GET_TASKS: {
                    handleGetTasks(exchange);
                    break;
                }
                case GET_TASK_ID: {
                    handleGetTaskId(exchange);
                    break;
                }
                case POST_TASK: {
                    handlePostTask(exchange);
                    break;
                }
                case DELETE_TASKS: {
                    handleDeleteTasks(exchange);
                    break;
                }
                case DELETE_TASK_ID: {
                    handleDeleteTaskId(exchange);
                    break;
                }
                case GET_EPICS: {
                    handleGetEpics(exchange);
                    break;
                }
                case GET_EPIC_ID: {
                    handleGetEpicId(exchange);
                    break;
                }
                case POST_EPIC: {
                    handlePostEpic(exchange);
                    break;
                }
                case DELETE_EPICS: {
                    handleDeleteEpics(exchange);
                    break;
                }
                case DELETE_EPIC_ID: {
                    handleDeleteEpicId(exchange);
                    break;
                }
                case GET_SUBTASKS: {
                    handleGetSubtasks(exchange);
                    break;
                }
                case GET_SUBTASK_ID: {
                    handleGetSubtaskId(exchange);
                    break;
                }
                case POST_SUBTASK: {
                    handlePostSubtask(exchange);
                    break;
                }
                case DELETE_SUBTASKS: {
                    handleDeleteSubtasks(exchange);
                    break;
                }
                case DELETE_SUBTASK_ID: {
                    handleDeleteSubtaskId(exchange);
                    break;
                }
                case GET_SUBTASKS_BY_EPIC: {
                    handleGetSubtaskByEpic(exchange);
                    break;
                }
                case GET_HISTORY: {
                    handleGetHistory(exchange);
                    break;
                }
                case GET_PRIORITY: {
                    handleGetPriority(exchange);
                    break;
                }
                case UNKNOWN: {
                    writeResponse(exchange, "Неизвестный эндпоинт", 404);
                    break;
                }
                default:
                    writeResponse(exchange, "Такого эндпоинта не существует", 404);
            }
        }

        // обработка endpoint'ов
        private void handleGetTasks(HttpExchange exchange) throws IOException {
            writeResponse(exchange, gson.toJson(taskManager.getAllTask()), 200);
        }

        private void handleGetTaskId(HttpExchange exchange) throws IOException {
            String pathId = exchange.getRequestURI().toString().replaceFirst("/tasks/task/\\?id=", "");
            int id = parsePathId(pathId);
            Task task = taskManager.getTask(id);

            if (task != null) {
                writeResponse(exchange, gson.toJson(task), 200);
            } else {
                writeResponse(exchange, "Задача с идентификатором " + id + " не найдена", 404);
            }
        }

        private void handlePostTask(HttpExchange exchange) throws IOException {
            InputStream inputStream = exchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            Task inputTask;
            try {
                inputTask = gson.fromJson(body, Task.class);
            } catch (JsonSyntaxException ex) {
                writeResponse(exchange, "Получен некорректный JSON задачи", 400);
                return;
            }

            if (taskManager.getTask(inputTask.getId()) != null) {
                taskManager.updateTask(inputTask);
                writeResponse(exchange, "Задача обновлена id = " + inputTask.getId(), 201);
            } else {
                taskManager.createTask(inputTask);
                writeResponse(exchange, "Задача создана id = " + inputTask.getId(), 201);
            }
        }

        private void handleDeleteTasks(HttpExchange exchange) throws IOException {
            taskManager.deleteAllTask();
            writeResponse(exchange, "Все задачи удалены", 200);
        }

        private void handleDeleteTaskId(HttpExchange exchange) throws IOException {
            String pathId = exchange.getRequestURI().toString().replaceFirst("/tasks/task/\\?id=", "");
            int id = parsePathId(pathId);
            Task task = taskManager.getTask(id);

            if (task != null) {
                taskManager.deleteTask(id);
                writeResponse(exchange, "Удалена задача id = " + id, 200);
            } else {
                writeResponse(exchange, "Задача с идентификатором " + id +
                        " не найдена и не удалена", 405);
            }
        }

        private void handleGetEpics(HttpExchange exchange) throws IOException {
            writeResponse(exchange, gson.toJson(taskManager.getAllEpic()), 200);
        }

        private void handleGetEpicId(HttpExchange exchange) throws IOException {
            String pathId = exchange.getRequestURI().toString().replaceFirst("/tasks/epic/\\?id=", "");
            int id = parsePathId(pathId);
            Epic epic = taskManager.getEpic(id);

            if (epic != null) {
                writeResponse(exchange, gson.toJson(epic), 200);
            } else {
                writeResponse(exchange, "Эпик с идентификатором " + id + " не найден", 404);
            }
        }

        private void handlePostEpic(HttpExchange exchange) throws IOException {
            InputStream inputStream = exchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            Epic inputEpic;
            try {
                inputEpic = gson.fromJson(body, Epic.class);
            } catch (JsonSyntaxException ex) {
                writeResponse(exchange, "Получен некорректный JSON эпика", 400);
                return;
            }

            if (taskManager.getEpic(inputEpic.getId()) != null) {
                taskManager.updateEpic(inputEpic);
                writeResponse(exchange, "Эпик обновлен id = " + inputEpic.getId(), 201);
            } else {
                taskManager.createEpic(inputEpic);
                writeResponse(exchange, "Эпик создан id = " + inputEpic.getId(), 201);
            }
        }

        private void handleDeleteEpics(HttpExchange exchange) throws IOException {
            taskManager.deleteAllEpic();
            writeResponse(exchange, "Все эпики удалены", 200);
        }

        private void handleDeleteEpicId(HttpExchange exchange) throws IOException {
            String pathId = exchange.getRequestURI().toString().replaceFirst("/tasks/epic/\\?id=", "");
            int id = parsePathId(pathId);
            Epic epic = taskManager.getEpic(id);

            if (epic != null) {
                taskManager.deleteEpic(id);
                writeResponse(exchange, "Удален эпик id = " + id, 200);
            } else {
                writeResponse(exchange, "Эпик с идентификатором " + id +
                        " не найден и не удален", 405);
            }
        }

        private void handleGetSubtasks(HttpExchange exchange) throws IOException {
            writeResponse(exchange, gson.toJson(taskManager.getAllSubtask()), 200);
        }

        private void handleGetSubtaskId(HttpExchange exchange) throws IOException {
            String pathId = exchange.getRequestURI().toString().replaceFirst("/tasks/subtask/\\?id=", "");
            int id = parsePathId(pathId);
            Subtask subtask = taskManager.getSubtask(id);

            if (subtask != null) {
                writeResponse(exchange, gson.toJson(subtask), 200);
            } else {
                writeResponse(exchange, "Подзадача с идентификатором " + id + " не найдена", 404);
            }
        }

        private void handlePostSubtask(HttpExchange exchange) throws IOException {
            InputStream inputStream = exchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            Subtask inputSubtask;
            try {
                inputSubtask = gson.fromJson(body, Subtask.class);
            } catch (JsonSyntaxException ex) {
                writeResponse(exchange, "Получен некорректный JSON подзадачи", 400);
                return;
            }

            if (taskManager.getSubtask(inputSubtask.getId()) != null) {
                taskManager.updateSubtask(inputSubtask);
                writeResponse(exchange, "Подзадача обновлена id = " + inputSubtask.getId(), 201);
            } else {
                taskManager.createSubtask(inputSubtask);
                writeResponse(exchange, "Подзадача создана id = " + inputSubtask.getId(), 201);
            }
        }

        private void handleDeleteSubtasks(HttpExchange exchange) throws IOException {
            taskManager.deleteAllSubtask();
            writeResponse(exchange, "Все подзадачи удалены", 200);
        }

        private void handleDeleteSubtaskId(HttpExchange exchange) throws IOException {
            String pathId = exchange.getRequestURI().toString().replaceFirst("/tasks/subtask/\\?id=", "");
            int id = parsePathId(pathId);
            Subtask subtask = taskManager.getSubtask(id);

            if (subtask != null) {
                taskManager.deleteSubtask(id);
                writeResponse(exchange, "Удалена подзадача id = " + id, 200);
            } else {
                writeResponse(exchange, "Подзадача с идентификатором " + id +
                        " не найдена и не удалена", 405);
            }
        }

        private void handleGetSubtaskByEpic(HttpExchange exchange) throws IOException {
            String pathId = exchange.getRequestURI().toString()
                    .replaceFirst("/tasks/subtask/epic/\\?id=", "");
            int id = parsePathId(pathId);
            Epic epic = taskManager.getEpic(id);

            if (epic != null) {
                writeResponse(exchange, gson.toJson(taskManager.getListSubtaskByEpic(epic)), 200);
            } else {
                writeResponse(exchange, "Эпик с идентификатором " + id +
                        " не найден, невозможно получить список его подзадач", 404);
            }
        }

        private void handleGetHistory(HttpExchange exchange) throws IOException {
            writeResponse(exchange, gson.toJson(taskManager.getHistory()), 200);
        }

        private void handleGetPriority(HttpExchange exchange) throws IOException {
            writeResponse(exchange, gson.toJson(taskManager.getPrioritizedTasks()), 200);
        }

        // получение enpoint'а из строки запроса: URI, метод
        private Endpoint getEndpoint(String requestPath, String requestMethod) {
            String[] pathParts = requestPath.split("/");
            if (!pathParts[1].equals("tasks")) {
                return Endpoint.UNKNOWN;
            }
            if ((pathParts.length == 2) && (requestMethod.equals("GET"))) {
                return Endpoint.GET_PRIORITY;
            }
            if ((pathParts.length == 2) || (pathParts.length > 5)) {
                return Endpoint.UNKNOWN;
            }
            if ((pathParts.length == 3) && (requestMethod.equals("GET"))
                    && (pathParts[2].equals("history"))) {
                return Endpoint.GET_HISTORY;
            }
            String typeTask = pathParts[2];
            switch (typeTask) {
                case "task": {
                    if ((pathParts.length == 3) && (requestMethod.equals("GET"))) {
                        return Endpoint.GET_TASKS;
                    }
                    if ((pathParts.length == 3) && (requestMethod.equals("DELETE"))) {
                        return Endpoint.DELETE_TASKS;
                    }
                    if (Pattern.matches("^/tasks/task/\\?id=\\d+$", requestPath)) { // проверяем id
                        String pathId = requestPath.replaceFirst("/tasks/task/\\?id=", "");
                        int id = parsePathId(pathId);
                        if (id != -1) {
                            if (requestMethod.equals("GET")) {
                                return Endpoint.GET_TASK_ID;
                            } else if (requestMethod.equals("DELETE")) {
                                return Endpoint.DELETE_TASK_ID;
                            }
                        }
                    }
                    if ((pathParts.length == 3) && (requestMethod.equals("POST"))) {
                        return Endpoint.POST_TASK;
                    }
                    break;
                }
                case "epic": {
                    if ((pathParts.length == 3) && (requestMethod.equals("GET"))) {
                        return Endpoint.GET_EPICS;
                    }
                    if ((pathParts.length == 3) && (requestMethod.equals("DELETE"))) {
                        return Endpoint.DELETE_EPICS;
                    }
                    if (Pattern.matches("^/tasks/epic/\\?id=\\d+$", requestPath)) { // проверяем id
                        String pathId = requestPath.replaceFirst("/tasks/epic/\\?id=", "");
                        int id = parsePathId(pathId);
                        if (id != -1) {
                            if (requestMethod.equals("GET")) {
                                return Endpoint.GET_EPIC_ID;
                            } else if (requestMethod.equals("DELETE")) {
                                return Endpoint.DELETE_EPIC_ID;
                            }
                        }
                    }
                    if ((pathParts.length == 3) && (requestMethod.equals("POST"))) {
                        return Endpoint.POST_EPIC;
                    }
                    break;
                }
                case "subtask": {
                    if ((pathParts.length == 3) && (requestMethod.equals("GET"))) {
                        return Endpoint.GET_SUBTASKS;
                    }
                    if ((pathParts.length == 3) && (requestMethod.equals("DELETE"))) {
                        return Endpoint.DELETE_SUBTASKS;
                    }
                    if (Pattern.matches("^/tasks/subtask/\\?id=\\d+$", requestPath)) { // проверяем id
                        String pathId = requestPath.replaceFirst("/tasks/subtask/\\?id=", "");
                        int id = parsePathId(pathId);
                        if (id != -1) {
                            if (requestMethod.equals("GET")) {
                                return Endpoint.GET_SUBTASK_ID;
                            } else if (requestMethod.equals("DELETE")) {
                                return Endpoint.DELETE_SUBTASK_ID;
                            }
                        }
                    }
                    if ((pathParts.length == 3) && (requestMethod.equals("POST"))) {
                        return Endpoint.POST_SUBTASK;
                    }
                    if (Pattern.matches("^/tasks/subtask/epic/\\?id=\\d+$", requestPath)) { // проверяем id
                        String pathId = requestPath.replaceFirst("/tasks/subtask/epic/\\?id=", "");
                        int id = parsePathId(pathId);
                        if (id != -1) {
                            if (requestMethod.equals("GET")) {
                                return Endpoint.GET_SUBTASKS_BY_EPIC;
                            }
                        }
                    }
                    break;
                }
                default:
                    return Endpoint.UNKNOWN;
            }

            return Endpoint.UNKNOWN;
        }

        private int parsePathId(String path) {
            try {
                return Integer.parseInt(path);
            } catch (NumberFormatException exp) {
                return -1;
            }
        }

        private void writeResponse(HttpExchange exchange,
                                   String responseString,
                                   int responseCode) throws IOException {
            if (responseString.isBlank()) {
                exchange.sendResponseHeaders(responseCode, 0);
            } else {
                byte[] bytes = responseString.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
                exchange.sendResponseHeaders(responseCode, bytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(bytes);
                }
            }
            exchange.close();
        }
    }

    private enum Endpoint {
        GET_TASKS, // получить все задачи
        GET_TASK_ID, // получить задачу по id
        POST_TASK, // создать/обновить задачу
        DELETE_TASKS, // удалить все задачи
        DELETE_TASK_ID, // удалить задачу по id
        GET_EPICS, // получить все эпики
        GET_EPIC_ID, // получить эпик по id
        POST_EPIC, // создать/обновить эпик
        DELETE_EPICS, // удалить все эпики
        DELETE_EPIC_ID, // удалить эпики по id
        GET_SUBTASKS, // получить все подзадачи
        GET_SUBTASK_ID, // получить подзадачу по id
        POST_SUBTASK, // создать/обновить подзадачу
        DELETE_SUBTASKS, // удалить все подзадачи
        DELETE_SUBTASK_ID, // удалить подзадачу по id
        GET_SUBTASKS_BY_EPIC, // получить список подзадач по id эпика
        GET_HISTORY, // получить историю
        GET_PRIORITY, // получить все задачи по приоритету
        UNKNOWN // неизвестный эндпойнт
    }
}