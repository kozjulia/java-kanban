package managers.taskmanagers;

import managers.exception.SaveTaskException;
import managers.exception.ManagerSaveException;
import managers.utils.ConverterCSV;
import tasks.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

// логика автосохранения в файл
public class FileBackedTasksManager extends InMemoryTaskManager {
    // определение объекта для директории
    public final File file;

    public FileBackedTasksManager() {
        super();
        this.file = new File("resources" + File.separator + "data.csv");
    }

    public FileBackedTasksManager(String path) {
        super();
        this.file = new File(path);
    }

    public static void main(String[] args) {
        TaskManager taskManager = new FileBackedTasksManager();
        testSprint(taskManager);  // тестовые данные для ФЗ 7-го спринта

        TaskManager taskManagerNew = FileBackedTasksManager.loadFromFile();
        testSprintNew(taskManagerNew);  // тестовые данные для ФЗ 7-го спринта загрузка из файла
    }

    public static void printAllTasks(TaskManager taskManager) {
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

    public static void printHistory(TaskManager taskManager) {
        System.out.println("\nИстория просмотра: ");
        if (taskManager.getHistory() == null) {
            return;
        }
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }
    }

    public static void printSortSet(TaskManager taskManager) {
        System.out.println("\nСортированный список задач и подзадач:");
        for (Task task : taskManager.getPrioritizedTasks()) {
            System.out.println(task);
        }
    }

    public static void testSprint(TaskManager taskManager) {
        taskManager.createTask(new Task("Покормить животных", "вкусным кормом",
                LocalDateTime.MAX, 0));
        taskManager.createTask(new Task("Поиграть", "в настольные игры",
                LocalDateTime.of(2023, 03, 19, 10, 00), 30));
        int idEpic = taskManager.createEpic(new Epic("Сделать покупки", "продукты"));
        taskManager.createSubtask(new Subtask("Яблоки", "красные", idEpic,
                LocalDateTime.of(2023, 03, 15, 10, 00), 15));
        taskManager.createSubtask(new Subtask("Творог", "200 гр.", idEpic,
                LocalDateTime.of(2023, 03, 15, 12, 00), 15));
        taskManager.createSubtask(new Subtask("Молоко", "2 литра", idEpic,
                LocalDateTime.of(2023, 03, 16, 11, 00), 45));
        taskManager.createEpic(new Epic("Подготовиться к д/р", "детское"));
        taskManager.getTask(1);
        taskManager.getEpic(3);
        taskManager.getTask(2);
        taskManager.getTask(1);
        taskManager.updateStatusSubtask(taskManager.getSubtask(6), StatusTask.DONE);
        //taskManager.deleteTask(2);
        try {
            taskManager.createTask(new Task("Не сохранится", "из-за пересечения",
                    LocalDateTime.of(2023, 03, 15, 10, 15), 20));
        } catch (SaveTaskException exp) {
            System.out.println(exp.getMessage());
        }
        printAllTasks(taskManager);
        printHistory(taskManager);
        printSortSet(taskManager);
    }

    protected static void testSprintNew(TaskManager taskManagerNew) {
        printAllTasks(taskManagerNew);
        printHistory(taskManagerNew);
        printSortSet(taskManagerNew);
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
    public int createTask(Task task) {
        int idTask = super.createTask(task);
        save();
        return idTask;
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
    public int createEpic(Epic epic) {
        int idEpic = super.createEpic(epic);
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
    public int createSubtask(Subtask subtask) {
        int idSubtask = super.createSubtask(subtask);
        save();
        return idSubtask;
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

    // методы для работы с файлом
    protected void save() { // сохраняет текущее состояние менеджера в указанный файл
        try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            fileWriter.append("id,type,name,status,description,epic,startTime,duration,endTime\n");
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

    public static FileBackedTasksManager loadFromFile(String path) {
        FileBackedTasksManager taskManager = new FileBackedTasksManager(path);
        taskManager.fillFromFile();

        return taskManager;
    }

    private void fillFromFile() {
        try (BufferedReader fileReader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String lineOfFile = fileReader.readLine();
            // params = [id,type,name,status,description,epic,startTime,duration,endTime]
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
                createTask(task);
            } else if (task.getType() == TypeTask.EPIC) {
                createEpic((Epic) task);
            } else if (task.getType() == TypeTask.SUBTASK) {
                createSubtask((Subtask) task);
            }
        } else {
            List<Integer> historyList = new ArrayList<>(ConverterCSV.historyFromString(lineOfFile));
            Collections.reverse(historyList); // история хранится с конца, при загрузке переворачиваем
            for (Integer idHistory : historyList) {
                if (getTask(idHistory) != null) {
                    historyManager.add(getTask(idHistory));
                } else if (getSubtask(idHistory) != null) {
                    historyManager.add(getSubtask(idHistory));
                } else if (getEpicForUpdate(idHistory) != null) {
                    historyManager.add(getEpic(idHistory));
                }
            }
        }
        return isHistory;
    }
}