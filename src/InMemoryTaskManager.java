import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {
    private int nextUin = 0; // счётчик по сквозной нумерации сущностей
    private Map<Integer, Task> mapTask = new HashMap<>(); // таблица задач
    private Map<Integer, Subtask> mapSubtask = new HashMap<>(); // таблица подзадач
    private Map<Integer, Epic> mapEpic = new HashMap<>(); // таблица эпиков
    public HistoryManager historyManager = Managers.getDefaultHistory();

    private int generateUin() {
        return ++nextUin;
    }

    // Методы для класса Task
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
        mapTask.clear();
    }

    @Override
    public Task getTask(int uin) { // получение по идентификатору
        if (mapTask.containsKey(uin)) {
            historyManager.addHistory(mapTask.get(uin));
            return mapTask.get(uin);
        } else {
            return null;
        }
    }

    @Override
    public void createTask(String title, String description, StatusTask status) { // создание
        Task task = new Task(title, description, status);
        task.setUin(generateUin());
        mapTask.put(task.getUin(), task);
    }

    @Override
    public void updateTask(Task task) { // обновление
        if (task != null) {
            mapTask.put(task.getUin(), task);
        }
    }

    @Override
    public void deleteTask(int uin) { // удаление по идентификатору
        if (mapTask.containsKey(uin)) {
            mapTask.remove(uin);
        }
    }

    // Методы для класса Subtask
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
        mapSubtask.clear();
        for (Epic epic : mapEpic.values()) {
            updateEpic(epic);
            updateStatusEpic(epic);
        }
    }

    @Override
    public Subtask getSubtask(int uin) { // получение по идентификатору
        if (mapSubtask.containsKey(uin)) {
            historyManager.addHistory(mapSubtask.get(uin));
            return mapSubtask.get(uin);
        } else {
            return null;
        }
    }

    @Override
    public void createSubtask(String title, String description, StatusTask status, int uinEpic) { // создание
        Subtask subtask = new Subtask(title, description, status, uinEpic);
        subtask.setUin(generateUin());
        mapSubtask.put(subtask.getUin(), subtask);
        // записываем новое уин подзадачи в эпик
        updateEpic(getEpic(subtask.getUinEpic()));
        updateStatusEpic(getEpic(subtask.getUinEpic()));
    }

    @Override
    public void updateSubtask(Subtask subtask) { // обновление
        if (subtask != null) {
            mapSubtask.put(subtask.getUin(), subtask);
            updateEpic(getEpic(subtask.getUinEpic()));
            updateStatusEpic(getEpic(subtask.getUinEpic()));
        }
    }

    @Override
    public void deleteSubtask(int uin) { // удаление по идентификатору
        Epic epic = null;
        if (mapSubtask.containsKey(uin)) {
            epic = getEpic(mapSubtask.get(uin).getUinEpic());
            mapSubtask.remove(uin);
        }
        if (mapEpic.containsValue(epic)) {
            updateEpic(epic);
            updateStatusEpic(epic);
        }
    }

    // Методы для класса Epic
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
        mapEpic.clear();
        mapSubtask.clear();
    }

    @Override
    public Epic getEpic(int uin) { // получение по идентификатору
        if (mapEpic.containsKey(uin)) {
            historyManager.addHistory(mapEpic.get(uin));
            return mapEpic.get(uin);
        } else {
            return null;
        }
    }

    @Override
    public int createEpic(String title, String description, StatusTask status) { // создание
        Epic epic = new Epic(title, description, status);
        epic.setUin(generateUin());
        mapEpic.put(epic.getUin(), epic);
        return epic.getUin();
    }

    @Override
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

    @Override
    public void deleteEpic(int uin) { // удаление по идентификатору
        List<Subtask> listSubtask = new ArrayList<>();
        if (mapEpic.containsKey(uin)) {
            listSubtask = getListSubtaskByEpic(mapEpic.get(uin));
            mapEpic.remove(uin);
        }
        for (Subtask subtask : listSubtask) {
            deleteSubtask(subtask.getUin());
        }
    }

    // получение списка всех подзадач определённого эпика
    public List<Subtask> getListSubtaskByEpic(Epic epic) {
        List<Subtask> listSubtask = new ArrayList<>();
        for (Subtask subtask : mapSubtask.values()) {
            if (subtask.getUinEpic() == epic.getUin()) {
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
        if (mapEpic.containsValue(subtask.getUinEpic())) {
            updateEpic(getEpic(subtask.getUinEpic()));
            updateStatusEpic(getEpic(subtask.getUinEpic()));
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
