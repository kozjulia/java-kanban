import java.util.ArrayList;
import java.util.HashMap;

public class Manager {
    private int nextUin = 0; // счётчик по сквозной нумерации сущностей
    private HashMap<Integer, Task> mapTask = new HashMap<>(); // таблица задач
    private HashMap<Integer, Subtask> mapSubtask = new HashMap<>(); // таблица подзадач
    private HashMap<Integer, Epic> mapEpic = new HashMap<>(); // таблица эпиков

    public Manager() {
    }

    private int generateUin() {
        return ++nextUin;
    }

    // Методы для класса Task
    public ArrayList<Task> getAllTask() { // получение списка всех задач
        ArrayList<Task> listTask = new ArrayList<>();
        for (Task task : mapTask.values()) {
            listTask.add(task);
        }
        return listTask;
    }

    public void deleteAllTask() { // удаление всех задач
        mapTask.clear();
    }

    public Task getTaskByUin(int uin) { // получение по идентификатору
        if (mapTask.containsKey(uin)) {
            return mapTask.get(uin);
        } else {
            return null;
        }
    }

    public void createTask(String title, String description, String status) { // создание
        Task task = new Task(title, description, status);
        task.setUin(generateUin());
        mapTask.put(task.getUin(), task);
    }

    public void updateTask(Task task) { // обновление
        if (task != null) {
            mapTask.put(task.getUin(), task);
        }
    }

    public void deleteTask(int uin) { // удаление по идентификатору
        if (mapTask.containsKey(uin)) {
            mapTask.remove(uin);
        }
    }

    // Методы для класса Subtask
    public ArrayList<Subtask> getAllSubtask() { // получение списка всех задач
        ArrayList<Subtask> listSubtask = new ArrayList<>();
        for (Subtask subtask : mapSubtask.values()) {
            listSubtask.add(subtask);
        }
        return listSubtask;
    }

    public void deleteAllSubtask() { // удаление всех задач
        mapSubtask.clear();
        for (Epic epic : mapEpic.values()) {
            updateEpic(epic);
            updateStatusEpic(epic);
        }
    }

    public Subtask getSubtaskByUin(int uin) { // получение по идентификатору
        if (mapSubtask.containsKey(uin)) {
            return mapSubtask.get(uin);
        } else {
            return null;
        }
    }

    public void createSubtask(String title, String description, String status, int uinEpic) { // создание
        Subtask subtask = new Subtask(title, description, status, uinEpic);
        subtask.setUin(generateUin());
        mapSubtask.put(subtask.getUin(), subtask);
        // записываем новое уин подзадачи в эпик
        updateEpic(getEpicByUin(subtask.getUinEpic()));
        updateStatusEpic(getEpicByUin(subtask.getUinEpic()));
    }

    public void updateSubtask(Subtask subtask) { // обновление
        if (subtask != null) {
            mapSubtask.put(subtask.getUin(), subtask);
            updateEpic(getEpicByUin(subtask.getUinEpic()));
            updateStatusEpic(getEpicByUin(subtask.getUinEpic()));
        }
    }

    public void deleteSubtask(int uin) { // удаление по идентификатору
        Epic epic = null;
        if (mapSubtask.containsKey(uin)) {
            epic = getEpicByUin(mapSubtask.get(uin).getUinEpic());
            mapSubtask.remove(uin);
        }
        if (mapEpic.containsValue(epic)) {
            updateEpic(epic);
            updateStatusEpic(epic);
        }
    }

    // Методы для класса Epic
    public ArrayList<Epic> getAllEpic() { // получение списка всех задач
        ArrayList<Epic> listEpic = new ArrayList<>();
        for (Epic epic : mapEpic.values()) {
            listEpic.add(epic);
        }
        return listEpic;
    }

    public void deleteAllEpic() { // удаление всех задач
        mapEpic.clear();
        mapSubtask.clear();
    }

    public Epic getEpicByUin(int uin) { // получение по идентификатору
        if (mapEpic.containsKey(uin)) {
            return mapEpic.get(uin);
        } else {
            return null;
        }
    }

    public int createEpic(String title, String description, String status) { // создание
        Epic epic = new Epic(title, description, status);
        epic.setUin(generateUin());
        mapEpic.put(epic.getUin(), epic);
        return epic.getUin();
    }

    public void updateEpic(Epic epic) { // обновление
        if (epic != null) {
            ArrayList<Integer> uinSubtasks = new ArrayList<>(); // обновляем список подзадач
            for (Subtask subtask : getListSubtaskByEpic(epic)) {
                uinSubtasks.add(subtask.getUin());
            }
            epic.setUinSubtask(uinSubtasks);
            mapEpic.put(epic.getUin(), epic);
        }
    }

    public void deleteEpic(int uin) { // удаление по идентификатору
        ArrayList<Subtask> listSubtask = new ArrayList<>();
        if (mapEpic.containsKey(uin)) {
            listSubtask = getListSubtaskByEpic(mapEpic.get(uin));
            mapEpic.remove(uin);
        }
        for (Subtask subtask : listSubtask) {
            deleteSubtask(subtask.getUin());
        }
    }

    // получение списка всех подзадач определённого эпика
    public ArrayList<Subtask> getListSubtaskByEpic(Epic epic) {
        ArrayList<Subtask> listSubtask = new ArrayList<>();
        for (Subtask subtask : mapSubtask.values()) {
            if (subtask.getUinEpic() == epic.getUin()) {
                listSubtask.add(subtask);
            }
        }
        return listSubtask;
    }

    // управление статусами задач
    public void updateStatusTask(Task task, String status) {
        if (task != null) {
            task.setStatus(status);
            updateTask(task);
        }
    }

    public void updateStatusSubtask(Subtask subtask, String status) {
        if (subtask == null) {
            return;
        }
        subtask.setStatus(status);
        updateSubtask(subtask);
        if (mapEpic.containsValue(subtask.getUinEpic())) {
            updateEpic(getEpicByUin(subtask.getUinEpic()));
            updateStatusEpic(getEpicByUin(subtask.getUinEpic()));
        }
    }

    public void updateStatusEpic(Epic epic) {
        if (epic == null) {
            return;
        }
        ArrayList<Subtask> listSubtask = new ArrayList<>();
        listSubtask = getListSubtaskByEpic(epic);
        boolean change = false; // отслеживаем изменен ли был статус эпика
        // если у эпика нет подзадач, то статус должен быть NEW
        if (listSubtask.size() == 0) {
            epic.setStatus("NEW");
            change = true;
        } else if (checkStatusSubtask(listSubtask, "NEW")) {
            epic.setStatus("NEW");
            change = true;
        } else if (checkStatusSubtask(listSubtask, "DONE")) {
            epic.setStatus("DONE");
            change = true;
        }
        if (!change) {
            epic.setStatus("IN_PROGRESS");
        }
        updateEpic(epic);
    }

    // проверка статуса подзадач эпика - все новые или всё сделано
    public boolean checkStatusSubtask(ArrayList<Subtask> listSubtask, String checkStatus) {
        boolean check = true;
        for (Subtask subtask : listSubtask) {
            if (subtask.getStatus() != checkStatus) {
                check = false;
            }
        }
        return check;
    }
}
