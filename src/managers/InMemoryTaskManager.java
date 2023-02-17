package managers;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.StatusTask;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {
    private int nextId = 0; // счётчик по сквозной нумерации сущностей
    private final Map<Integer, Task> mapTask = new HashMap<>(); // таблица задач
    private final Map<Integer, Subtask> mapSubtask = new HashMap<>(); // таблица подзадач
    private final Map<Integer, Epic> mapEpic = new HashMap<>(); // таблица эпиков
    public HistoryManager historyManager = Managers.getDefaultHistory();

    private int generateId() {
        return ++nextId;
    }

    // Методы для класса tasks.Task
    @Override
    public List<Task> getAllTask() { // получение списка всех задач
        List<Task> listTask = new ArrayList<>();
        for (Task task : mapTask.values()) {
            listTask.add(task);
        }
        return listTask;
    }

    @Override
    public void deleteAllTask() { // удаление всех задач
        for (Integer id : mapTask.keySet()) {
            historyManager.remove(id);
        }
        mapTask.clear();
    }

    @Override
    public Task getTask(int id) { // получение по идентификатору
        if (mapTask.containsKey(id)) {
            historyManager.add(mapTask.get(id));
            return mapTask.get(id);
        } else {
            return null;
        }
    }

    @Override
    public void createTask(String title, String description) { // создание
        Task task = new Task(title, description);
        task.setId(generateId());
        mapTask.put(task.getId(), task);
    }

    @Override
    public void updateTask(Task task) { // обновление
        if (task != null) {
            mapTask.put(task.getId(), task);
        }
    }

    @Override
    public void deleteTask(int id) { // удаление по идентификатору
        if (mapTask.containsKey(id)) {
            mapTask.remove(id);
            historyManager.remove(id);
        }
    }

    // Методы для класса tasks.Subtask
    @Override
    public List<Subtask> getAllSubtask() { // получение списка всех задач
        List<Subtask> listSubtask = new ArrayList<>();
        for (Subtask subtask : mapSubtask.values()) {
            listSubtask.add(subtask);
        }
        return listSubtask;
    }

    @Override
    public void deleteAllSubtask() { // удаление всех задач
        for (Integer id : mapSubtask.keySet()) {
            historyManager.remove(id);
        }
        mapSubtask.clear();
        for (Epic epic : mapEpic.values()) {
            updateStatusEpic(epic);
        }
    }

    @Override
    public Subtask getSubtask(int id) { // получение по идентификатору
        if (mapSubtask.containsKey(id)) {
            historyManager.add(mapSubtask.get(id));
            return mapSubtask.get(id);
        } else {
            return null;
        }
    }

    @Override
    public void createSubtask(String title, String description, int idEpic) { // создание
        Subtask subtask = new Subtask(title, description, idEpic);
        subtask.setId(generateId());
        mapSubtask.put(subtask.getId(), subtask);
        // записываем новое уин подзадачи в эпик
        updateStatusEpic(getEpic(subtask.getIdEpic()));
    }

    @Override
    public void updateSubtask(Subtask subtask) { // обновление
        if (subtask != null) {
            mapSubtask.put(subtask.getId(), subtask);
            updateStatusEpic(getEpic(subtask.getIdEpic()));
        }
    }

    @Override
    public void deleteSubtask(int id) { // удаление по идентификатору
        Epic epic = null;
        if (mapSubtask.containsKey(id)) {
            epic = getEpic(mapSubtask.get(id).getIdEpic());
            mapSubtask.remove(id);
            historyManager.remove(id);
        }
        if (mapEpic.containsValue(epic)) {
            updateStatusEpic(epic);
        }
    }

    // Методы для класса tasks.Epic
    @Override
    public List<Epic> getAllEpic() { // получение списка всех задач
        List<Epic> listEpic = new ArrayList<>();
        for (Epic epic : mapEpic.values()) {
            listEpic.add(epic);
        }
        return listEpic;
    }

    @Override
    public void deleteAllEpic() { // удаление всех задач
        for (Integer id : mapEpic.keySet()) {
            historyManager.remove(id);
        }
        mapEpic.clear();
        deleteAllSubtask();
    }

    @Override
    public Epic getEpic(int id) { // получение по идентификатору
        if (mapEpic.containsKey(id)) {
            historyManager.add(mapEpic.get(id));
            return mapEpic.get(id);
        } else {
            return null;
        }
    }

    @Override
    public int createEpic(String title, String description) { // создание
        Epic epic = new Epic(title, description);
        epic.setId(generateId());
        mapEpic.put(epic.getId(), epic);
        return epic.getId();
    }

    @Override
    public void updateEpic(Epic epic) { // обновление
        if (epic != null) {
            ArrayList<Integer> idSubtasks = new ArrayList<>(); // обновляем список подзадач
            for (Subtask subtask : getListSubtaskByEpic(epic)) {
                idSubtasks.add(subtask.getId());
            }
            epic.setIdSubtask(idSubtasks);
            mapEpic.put(epic.getId(), epic);
        }
    }

    @Override
    public void deleteEpic(int id) { // удаление по идентификатору
        List<Subtask> listSubtask = new ArrayList<>();
        if (mapEpic.containsKey(id)) {
            listSubtask = getListSubtaskByEpic(mapEpic.get(id));
            mapEpic.remove(id);
            historyManager.remove(id);
        }
        for (Subtask subtask : listSubtask) {
            deleteSubtask(subtask.getId());
        }
    }

    // получение списка всех подзадач определённого эпика
    public List<Subtask> getListSubtaskByEpic(Epic epic) {
        List<Subtask> listSubtask = new ArrayList<>();
        for (Subtask subtask : mapSubtask.values()) {
            if (subtask.getIdEpic() == epic.getId()) {
                listSubtask.add(subtask);
            }
        }
        return listSubtask;
    }

    // управление статусами задач
    @Override
    public void updateStatusTask(Task task, StatusTask status) {
        if (task != null) {
            task.setStatusTask(status);
            updateTask(task);
        }
    }

    @Override
    public void updateStatusSubtask(Subtask subtask, StatusTask status) {
        if (subtask == null) {
            return;
        }
        subtask.setStatusTask(status);
        updateSubtask(subtask);
        if (mapEpic.containsValue(subtask.getIdEpic())) {
            updateStatusEpic(getEpic(subtask.getIdEpic()));
        }
    }

    @Override
    public void updateStatusEpic(Epic epic) {
        if (epic == null) {
            return;
        }
        List<Subtask> listSubtask = new ArrayList<>();
        listSubtask = getListSubtaskByEpic(epic);
        boolean change = false; // отслеживаем изменен ли был статус эпика
        // если у эпика нет подзадач, то статус должен быть NEW
        if (listSubtask.size() == 0) {
            epic.setStatusTask(StatusTask.NEW);
            change = true;
        } else if (checkStatusSubtask(listSubtask, StatusTask.NEW)) {
            epic.setStatusTask(StatusTask.NEW);
            change = true;
        } else if (checkStatusSubtask(listSubtask, StatusTask.DONE)) {
            epic.setStatusTask(StatusTask.DONE);
            change = true;
        }
        if (!change) {
            epic.setStatusTask(StatusTask.IN_PROGRESS);
        }
        updateEpic(epic);
    }

    // проверка статуса подзадач эпика - все новые или всё сделано
    public boolean checkStatusSubtask(List<Subtask> listSubtask, StatusTask checkStatus) {
        boolean check = true;
        for (Subtask subtask : listSubtask) {
            if (!subtask.getStatusTask().equals(checkStatus)) {
                check = false;
            }
        }
        return check;
    }

    // история просмотра задач
    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}
