package managers.utils;

import tasks.Task;
import tasks.Epic;
import tasks.Subtask;
import tasks.StatusTask;

import static managers.utils.ConverterCSV.fillStatusFromString;
import static managers.utils.LocalDateTimeAdapter.FORMATTER;

import java.lang.reflect.Type;
import java.time.LocalDateTime;

import com.google.gson.*;

// корректный десериализатор для всех типов задач
public class TaskJsonAdapter implements JsonDeserializer<Task> {

    public TaskJsonAdapter() {
    }

    @Override
    public Task deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws
            JsonParseException {

        JsonObject jsonObject = json.getAsJsonObject();
        if (jsonObject.get("type") == null) {
            return null;
        }
        String type = jsonObject.get("type").getAsString();
        int id;
        if (jsonObject.get("id") == null) {
            id = 0;
        } else {
            id = jsonObject.get("id").getAsInt();
        }
        String title;
        if (jsonObject.get("title") == null) {
            title = "";
        } else {
            title = jsonObject.get("title").getAsString();
        }
        StatusTask status;
        if (jsonObject.get("status") == null) {
            status = null;
        } else {
            status = fillStatusFromString(jsonObject.get("status").getAsString());
        }
        String description;
        if (jsonObject.get("description") == null) {
            description = "";
        } else {
            description = jsonObject.get("description").getAsString();
        }
        LocalDateTime startTime;
        if (jsonObject.get("startTime") == null) {
            startTime = null;
        } else {
            startTime = LocalDateTime.parse(jsonObject.get("startTime").getAsString(), FORMATTER);
        }
        int duration;
        if (jsonObject.get("duration") == null) {
            duration = 0;
        } else {
            duration = jsonObject.get("duration").getAsInt();
        }
        switch (type) {
            case ("TASK"): {
                Task taskNew;
                if (startTime != null) {
                    taskNew = new Task(title, description, startTime, duration);
                } else {
                    taskNew = new Task(title, description);
                }
                if (id != 0) {
                    taskNew.setId(id);
                    checkNextId(taskNew);
                }
                if (status != null) {
                    taskNew.setStatusTask(status);
                }
                return taskNew;
            }
            case ("EPIC"): {
                Epic epicNew;
                epicNew = new Epic(title, description);
                if (id != 0) {
                    epicNew.setId(id);
                    checkNextId(epicNew);
                }
                if (status != null) {
                    epicNew.setStatusTask(status);
                }
                if (startTime != null) {
                    epicNew.setStartTime(startTime);
                }
                epicNew.setDuration(duration);
                LocalDateTime endTime;
                if (jsonObject.get("endTime") == null) {
                    endTime = null;
                } else {
                    endTime = LocalDateTime.parse(jsonObject.get("endTime").getAsString(), FORMATTER);
                }
                if (endTime != null) {
                    epicNew.setEndTime(endTime);
                }
                return epicNew;
            }
            case ("SUBTASK"): {
                int idEpic = jsonObject.get("idEpic").getAsInt();
                if (idEpic == 0) {
                    return null;
                }
                Subtask subtaskNew;
                if (startTime != null) {
                    subtaskNew = new Subtask(title, description, idEpic, startTime, duration);
                } else {
                    subtaskNew = new Subtask(title, description, idEpic);
                }
                if (id != 0) {
                    subtaskNew.setId(id);
                    checkNextId(subtaskNew);
                }
                if (status != null) {
                    subtaskNew.setStatusTask(status);
                }
                return subtaskNew;
            }
            default:
                return null;
        }
    }

    // проверяем самый последний id для сквозной нумерации новых задач после загрузки с сервера
    private static void checkNextId(Task task) {
        if (task.getId() < Task.getNextId()) {
            Task.setNextId(task.getId());
        }
    }
}