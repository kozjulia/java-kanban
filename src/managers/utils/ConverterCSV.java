package managers.utils;

import tasks.*;
import managers.historymanagers.HistoryManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

import static managers.utils.LocalDateTimeAdapter.FORMATTER;

public class ConverterCSV {
    public ConverterCSV() {
    }

    public static String toString(Task task) { // метод сохранения задачи в строку
        return task.toString();
    }

    public static Task fromString(String value) { // метод создания задачи из строки
        if (value.isBlank() || value.isEmpty() || value == null) {
            return null;
        }
        String[] valueTask = value.split(",");
        switch (valueTask[1]) {
            case "TASK":
                return fillTask(valueTask);
            case "EPIC":
                return fillEpic(valueTask);
            case "SUBTASK":
                return fillSubTask(valueTask);
            default:
                return null;
        }
    }

    private static Task fillTask(String[] params) { // создать задачу из параметров файла
        if (params == null) {
            return null;
        }
        Task task = new Task();
        task.setId(Integer.parseInt(params[0]));
        task.setTitle(params[2]);
        task.setStatusTask(fillStatusFromString(params[3]));
        task.setDescription(params[4]);
        task.setStartTime(LocalDateTime.parse(params[6], FORMATTER));
        task.setDuration(Integer.parseInt(params[7]));
        checkNextId(task);
        return task;
    }

    private static Subtask fillSubTask(String[] params) { // создать подзадачу из параметров файла
        if (params == null) {
            return null;
        }
        Subtask subtask = new Subtask();
        subtask.setId(Integer.parseInt(params[0]));
        subtask.setTitle(params[2]);
        subtask.setStatusTask(fillStatusFromString(params[3]));
        subtask.setDescription(params[4]);
        subtask.setIdEpic(Integer.parseInt(params[5]));
        subtask.setStartTime(LocalDateTime.parse(params[6], FORMATTER));
        subtask.setDuration(Integer.parseInt(params[7]));
        checkNextId(subtask);
        return subtask;
    }

    private static Epic fillEpic(String[] params) { // создать эпик из параметров файла
        if (params == null) {
            return null;
        }
        Epic epic = new Epic();
        epic.setId(Integer.parseInt(params[0]));
        epic.setTitle(params[2]);
        epic.setStatusTask(fillStatusFromString(params[3]));
        epic.setDescription(params[4]);
        epic.setStartTime(LocalDateTime.parse(params[6], FORMATTER));
        epic.setDuration(Integer.parseInt(params[7]));
        epic.setEndTime(LocalDateTime.parse(params[8], FORMATTER));
        checkNextId(epic);
        return epic;
    }

    // проверяем самый последний id для сквозной нумерации новых задач после загрузки из файла
    private static void checkNextId(Task task) {
        if (task.getId() > Task.getNextId()) {
            Task.setNextId(task.getId());
        }
    }

    // заполнение статуса задач из строки
    public static StatusTask fillStatusFromString(String statusString) {
        switch (statusString) {
            case "NEW":
                return StatusTask.NEW;
            case "IN_PROGRESS":
                return StatusTask.IN_PROGRESS;
            case "DONE":
                return StatusTask.DONE;
        }
        return StatusTask.NEW;
    }

    // метод восстановления менеджера истории из CSV
    public static List<Integer> historyFromString(String value) {
        List<Integer> idHistoryList = new ArrayList<>();
        if (value == null) {
            return idHistoryList;
        }
        String[] idHistory = value.split(",");
        for (String s : idHistory) {
            if (s.isEmpty() || s.isBlank() || s == null) {
                continue;
            }
            idHistoryList.add(Integer.parseInt(s));
        }
        return idHistoryList;
    }

    // метод сохранения менеджера истории в строку файла CSV
    public static String historyToString(HistoryManager manager) {
        List<Task> historyTaskList = manager.getHistory();
        if (historyTaskList == null) {
            return "";
        }
        StringBuilder historyBuf = new StringBuilder();
        for (Task task : historyTaskList) {
            historyBuf.append(task.getId() + ",");
        }
        return historyBuf.substring(0, historyBuf.length() - 1);
    }
}