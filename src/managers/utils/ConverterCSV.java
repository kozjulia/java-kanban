package managers.utils;

import managers.historymanagers.HistoryManager;
import tasks.Epic;
import tasks.StatusTask;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class ConverterCSV {
    public ConverterCSV() {
    }

    public String toString(Task task) { // метод сохранения задачи в строку
        return task.toString();
    }

    public Task fromString(String value) { // метод создания задачи из строки
        if (value.isBlank() || value.isEmpty()) {
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

    private Task fillTask(String[] params) { // создать задачу из параметров файла
        // params = [id,type,name,status,description,epic]
        if (params == null) {
            return null;
        }
        Task task = new Task();
        task.setId(Integer.parseInt(params[0]));
        task.setTitle(params[2]);
        task.setStatusTask(fillStatusFromString(params[3]));
        task.setDescription(params[4]);
        return task;
    }


    private Subtask fillSubTask(String[] params) { // создать подзадачу из параметров файла
        // params = [id,type,name,status,description,epic]
        if (params == null) {
            return null;
        }
        Subtask subtask = new Subtask();
        subtask.setId(Integer.parseInt(params[0]));
        subtask.setTitle(params[2]);
        subtask.setStatusTask(fillStatusFromString(params[3]));
        subtask.setDescription(params[4]);
        subtask.setIdEpic(Integer.parseInt(params[5]));
        return subtask;
    }

    private Epic fillEpic(String[] params) { // создать эпик из параметров файла
        // params = [id,type,name,status,description,epic]
        if (params == null) {
            return null;
        }
        Epic epic = new Epic();
        epic.setId(Integer.parseInt(params[0]));
        epic.setTitle(params[2]);
        epic.setStatusTask(fillStatusFromString(params[3]));
        epic.setDescription(params[4]);
        return epic;
    }

    // заполнение статуса задач из строки
    private StatusTask fillStatusFromString(String statusString) {
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

    // метод сохранения менеджера истории из CSV
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

    // метод восстановления менеджера истории из CSV
    public static List<Integer> historyFromString(String value) {
        String[] idHistory = value.split(",");
        List<Integer> idHistoryList = new ArrayList<>();
        for (int i = 0; i < idHistory.length; i++) {
            idHistoryList.add(Integer.parseInt(idHistory[i]));
        }
        return idHistoryList;
    }

}
