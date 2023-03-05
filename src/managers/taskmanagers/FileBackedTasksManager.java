package managers.taskmanagers;

import managers.utils.ConverterCSV;
import tasks.Task;
import tasks.Subtask;
import tasks.Epic;
import tasks.TypeTask;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// логика автосохранения в файл
public class FileBackedTasksManager extends InMemoryTaskManager {
    // определение объекта для директории
    public static File file = new File("resources" + File.separator + "data.csv");
    ConverterCSV converterCSV = new ConverterCSV();

    public FileBackedTasksManager(File file) {
        this.file = file;
    }

    ////////// про main в ТЗ в данном классе не поняла - ЗАЧЕМ и ПОЧЕМУ?
    static void main(String[] args) {

    }

    // переопределение методов у Task
    @Override
    public void deleteAllTask() {
        super.deleteAllTask();
        save();
    }

    @Override
    public Task getTask(int id) {
        Task task = super.getTask(id);
        save();
        return task;
    }

    @Override
    public void createTask(String title, String description) {
        super.createTask(title, description);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    // переопределение методов у SubTask
    @Override
    public void deleteAllSubtask() {
        super.deleteAllSubtask();
        save();
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = super.getSubtask(id);
        save();
        return subtask;
    }

    @Override
    public void createSubtask(String title, String description, int idEpic) {
        super.createSubtask(title, description, idEpic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        save();
    }

    // переопределение методов у Epic
    @Override
    public void deleteAllEpic() {
        super.deleteAllEpic();
        save();
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = super.getEpic(id);
        save();
        return epic;
    }

    @Override
    public int createEpic(String title, String description) {
        int idEpic = super.createEpic(title, description);
        save();
        return idEpic;
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    // методы для работы с файлом
    private void save() { // сохраняет текущее состояние менеджера в указанный файл
        try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            fileWriter.append("id,type,name,status,description,epic\n");
            for (Task task : super.getAllTask()) {
                fileWriter.append(converterCSV.toString(task) + "\n");
            }
            for (Epic epic : super.getAllEpic()) {
                fileWriter.append(converterCSV.toString(epic) + "\n");
            }
            for (Subtask subTask : super.getAllSubtask()) {
                fileWriter.append(converterCSV.toString(subTask) + "\n");
            }
            fileWriter.newLine();
            fileWriter.append(converterCSV.historyToString(historyManager));
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения файла");
        }
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager taskManager = new FileBackedTasksManager(file);
        taskManager.fillFromFile(file);

        return taskManager;
    }

    private void fillFromFile(File file) {
        try (BufferedReader fileReader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String lineOfFile = fileReader.readLine();
            Boolean isHistory = false;
            while (fileReader.ready()) {
                lineOfFile = fileReader.readLine();
                isHistory = fillLineOfFile(lineOfFile, isHistory);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения файла");
        }
    }

    private Boolean fillLineOfFile(String lineOfFile, Boolean isHistory) {
        if (lineOfFile.isEmpty()) {
            isHistory = true; // пустая строка - началась история
        } else if (!isHistory) { // обновляем - добавляем в мапу
            Task task = converterCSV.fromString(lineOfFile);
            if (task.getType() == TypeTask.TASK) {
                updateTask(task);
            } else if (task.getType() == TypeTask.EPIC) {
                updateEpic((Epic) task);
            } else if (task.getType() == TypeTask.SUBTASK) {
                updateSubtask((Subtask) task);
            }
        } else {
            List<Integer> historyList = new ArrayList<>(converterCSV.historyFromString(lineOfFile));
            Collections.reverse(historyList); // история хранится с конца, при загрузке переворачиваем
            for (Integer idHistory : historyList) {
                if (getTask(idHistory) != null) {
                    historyManager.add(getTask(idHistory));
                } else if (getSubtask(idHistory) != null) {
                    historyManager.add(getSubtask(idHistory));
                } else if (getEpic(idHistory) != null) {
                    historyManager.add(getEpic(idHistory));
                }
            }
        }
        return isHistory;
    }

    private class ManagerSaveException extends RuntimeException { // собственное непроверяемое исключение
        public ManagerSaveException() {
        }

        public ManagerSaveException(final String message) {
            super(message);
        }
    }
}
