package managers.taskmanagers;

import managers.utils.ConverterCSV;
import managers.exception.ManagerSaveException;
import tasks.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// логика автосохранения в файл
public class FileBackedTasksManager extends InMemoryTaskManager {
    // определение объекта для директории
    public final File file = new File("resources" + File.separator + "data.csv");

    public FileBackedTasksManager() {
    }

    public static void main(String[] args) {
        TaskManager taskManager = new FileBackedTasksManager();
        testSprint(taskManager);  // тестовые данные для ФЗ 6-го спринта

        TaskManager taskManagerNew = FileBackedTasksManager.loadFromFile();
        testSprintNew(taskManagerNew);  // тестовые данные для ФЗ 6-го спринта загрузка из файла
    }

    private static void printAllTasks(TaskManager taskManager) {
        System.out.println("\nСписок задач:");
        for (Task task : taskManager.getAllTask()) {
            System.out.println(task);
        }
        for (Epic epic : taskManager.getAllEpic()) {
            System.out.println(epic);
        }
        for (Subtask subtask : taskManager.getAllSubtask()) {
            System.out.println(subtask);
        }
    }

    private static void printHistory(TaskManager taskManager) {
        System.out.println("\nИстория просмотра: ");
        if (taskManager.getHistory() == null) {
            return;
        }
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }
    }

    private static void testSprint(TaskManager taskManager) {
        taskManager.createTask("Покормить животных", "вкусным кормом");
        taskManager.createTask("Поиграть", "в настольные игры");
        int idEpic;
        idEpic = taskManager.createEpic("Сделать покупки", "продукты");
        taskManager.createSubtask("Яблоки", "красные", idEpic);
        taskManager.createSubtask("Творог", "200 гр.", idEpic);
        taskManager.createSubtask("Молоко", "2 литра", idEpic);
        taskManager.createEpic("Подготовиться к д/р", "детское");
        taskManager.getTask(1);
        taskManager.getEpic(3);
        taskManager.getTask(2);
        taskManager.getTask(1);
        taskManager.updateStatusSubtask(taskManager.getSubtask(6), StatusTask.DONE);
        taskManager.deleteTask(2);
        printAllTasks(taskManager);
        printHistory(taskManager);
    }

    private static void testSprintNew(TaskManager taskManagerNew) {
        printAllTasks(taskManagerNew);
        printHistory(taskManagerNew);
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
                fileWriter.append(ConverterCSV.toString(task) + "\n");
            }
            for (Epic epic : super.getAllEpic()) {
                fileWriter.append(ConverterCSV.toString(epic) + "\n");
            }
            for (Subtask subTask : super.getAllSubtask()) {
                fileWriter.append(ConverterCSV.toString(subTask) + "\n");
            }
            fileWriter.newLine();
            fileWriter.append(ConverterCSV.historyToString(historyManager));
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения файла");
        }
    }

    public static FileBackedTasksManager loadFromFile() {
        FileBackedTasksManager taskManager = new FileBackedTasksManager();
        taskManager.fillFromFile();

        return taskManager;
    }

    private void fillFromFile() {
        try (BufferedReader fileReader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String lineOfFile = fileReader.readLine(); // params = [id,type,name,status,description,epic]
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
            Task task = ConverterCSV.fromString(lineOfFile);
            if (task.getType() == TypeTask.TASK) {
                updateTask(task);
            } else if (task.getType() == TypeTask.EPIC) {
                updateEpic((Epic) task);
            } else if (task.getType() == TypeTask.SUBTASK) {
                updateSubtask((Subtask) task);
            }
        } else {
            List<Integer> historyList = new ArrayList<>(ConverterCSV.historyFromString(lineOfFile));
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
}
