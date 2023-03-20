package managers.taskmanagers;

import managers.Managers;
import managers.exception.SaveTaskException;
import managers.historymanagers.HistoryManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.StatusTask;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {

    private final Map<Integer, Task> mapTask = new HashMap<>(); // таблица задач
    private final Map<Integer, Subtask> mapSubtask = new HashMap<>(); // таблица подзадач
    private final Map<Integer, Epic> mapEpic = new HashMap<>(); // таблица эпиков
    public HistoryManager historyManager = Managers.getDefaultHistory();
    // компаратор для множества задач
    Comparator<Task> comparator = (o1, o2) ->
            o1.getStartTime().compareTo(o2.getStartTime());
    // компаратор для дерева
    Comparator<LocalDateTime> comparatorMap = LocalDateTime::compareTo;

    // сортированное по началу даты множество задач и подзадач
    private final Set<Task> sortSetTask = new TreeSet<>(comparator);
    // дерево времени - занятое задачами и подзадачами за 2023 год, 15-минутные промежутки
    private final Map<LocalDateTime, Integer> busyTime = new TreeMap<>(comparatorMap);

    public InMemoryTaskManager() {
        // заполняем дерево за год с разницей в 15 минут
        fillBusyTime(LocalDateTime.of(2023, 1, 1, 0, 0),
                LocalDateTime.of(2024, 1, 1, 0, 0), 0);
    }

    private void fillBusyTime(LocalDateTime curTime, LocalDateTime endTime, Integer idFlag) {
        while (true) {
            if (curTime.isEqual(LocalDateTime.MAX)) {
                break;
            }
            if (curTime.isEqual(LocalDateTime.MAX.minusNanos(999999999))) {
                break;
            }
            busyTime.put(curTime, idFlag);
            curTime = curTime.plusMinutes(15);
            if (curTime.isEqual(endTime)) {
                break;
            }
        }
    }

    // Методы для класса tasks.Task
    @Override
    public List<Task> getAllTask() { // получение списка всех задач
        return new ArrayList<>(mapTask.values());
    }

    @Override
    public void deleteAllTask() { // удаление всех задач
        for (Integer id : mapTask.keySet()) {
            sortSetTask.remove(getTask(id));
            fillBusyTime(getTask(id).getStartTime(), getTask(id).getEndTime(), 0);
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
    public int createTask(Task task) { // создание
        if (task == null) {
            return 0;
        }
        //if (!checkTimeCross(task)) { // проверка за O(n)
        if (!checkTimeCrossMap(task)) {
            throw new SaveTaskException(String.format(
                    "Невозможно создать новую задачу %s из-за пересечения во времени", task));
        }
        mapTask.put(task.getId(), task);
        sortSetTask.add(task);
        fillBusyTime(task.getStartTime(), task.getEndTime(), task.getId());
        return task.getId();
    }

    @Override
    public void updateTask(Task task) { // обновление
        if (task == null) {
            return;
        }
        //if (!checkTimeCross(task)) { // проверка за O(n)
        if (!checkTimeCrossMap(task)) {
            throw new SaveTaskException(String.format(
                    "Невозможно обновить задачу %s из-за пересечения во времени", task));
        }
        mapTask.put(task.getId(), task);
        sortSetTask.add(task);
        fillBusyTime(task.getStartTime(), task.getEndTime(), task.getId());
    }

    @Override
    public void deleteTask(int id) { // удаление по идентификатору
        if (mapTask.containsKey(id)) {
            sortSetTask.remove(getTask(id));
            fillBusyTime(getTask(id).getStartTime(), getTask(id).getEndTime(), 0);
            mapTask.remove(id);
            historyManager.remove(id);
        }
    }

    // Методы для класса tasks.Epic
    @Override
    public List<Epic> getAllEpic() { // получение списка всех задач
        return new ArrayList<>(mapEpic.values());
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

    // получение по идентификатору для обновления без записи в историю
    public Epic getEpicForUpdate(int id) {
        return mapEpic.getOrDefault(id, null);
    }

    @Override
    public int createEpic(Epic epic) { // создание
        if (epic == null) {
            return 0;
        }
        mapEpic.put(epic.getId(), epic);
        return epic.getId();
    }

    @Override
    public void updateEpic(Epic epic) { // обновление
        if (epic == null) {
            return;
        }
        ArrayList<Integer> idSubtasks = new ArrayList<>(); // обновляем список подзадач
        for (Subtask subtask : getListSubtaskByEpic(epic)) {
            idSubtasks.add(subtask.getId());
        }
        updateTimesDurationEpic(epic);
        epic.setIdSubtask(idSubtasks);
        mapEpic.put(epic.getId(), epic);
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

    // Методы для класса tasks.Subtask
    @Override
    public List<Subtask> getAllSubtask() { // получение списка всех задач
        return new ArrayList<>(mapSubtask.values());
    }

    @Override
    public void deleteAllSubtask() { // удаление всех задач
        for (Integer id : mapSubtask.keySet()) {
            sortSetTask.remove(getSubtask(id));
            fillBusyTime(getSubtask(id).getStartTime(), getSubtask(id).getEndTime(), 0);
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
    public int createSubtask(Subtask subtask) { // создание
        if (subtask == null) {
            return 0;
        }
        //if (!checkTimeCross(subtask)) { // проверка за O(n)
        if (!checkTimeCrossMap(subtask)) {
            throw new SaveTaskException(String.format(
                    "Невозможно создать новую подзадачу %s из-за пересечения во времени", subtask));
        }
        mapSubtask.put(subtask.getId(), subtask);
        sortSetTask.add(subtask);
        fillBusyTime(subtask.getStartTime(), subtask.getEndTime(), subtask.getId());
        // записываем новое уин подзадачи в эпик
        updateStatusEpic(getEpicForUpdate(subtask.getIdEpic()));
        return subtask.getId();
    }

    @Override
    public void updateSubtask(Subtask subtask) { // обновление
        if (subtask == null) {
            return;
        }
        //if (!checkTimeCross(subtask)) { // проверка за O(n)
        if (!checkTimeCrossMap(subtask)) {
            throw new SaveTaskException(String.format(
                    "Невозможно обновить подзадачу %s из-за пересечения во времени", subtask));
        }
        mapSubtask.put(subtask.getId(), subtask);
        sortSetTask.add(subtask);
        fillBusyTime(subtask.getStartTime(), subtask.getEndTime(), subtask.getId());
        updateStatusEpic(getEpicForUpdate(subtask.getIdEpic()));
    }

    @Override
    public void deleteSubtask(int id) { // удаление по идентификатору
        Epic epic = null;
        if (mapSubtask.containsKey(id)) {
            epic = getEpicForUpdate(mapSubtask.get(id).getIdEpic());
            sortSetTask.remove(getSubtask(id));
            fillBusyTime(getSubtask(id).getStartTime(), getSubtask(id).getEndTime(), 0);
            mapSubtask.remove(id);
            historyManager.remove(id);
        }
        if (mapEpic.containsValue(epic)) {
            updateStatusEpic(epic);
        }
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
    public void updateStatusEpic(Epic epic) {
        if (epic == null) {
            return;
        }
        List<Subtask> listSubtask = getListSubtaskByEpic(epic);
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

    @Override
    public void updateStatusSubtask(Subtask subtask, StatusTask status) {
        if (subtask == null) {
            return;
        }
        subtask.setStatusTask(status);
        updateSubtask(subtask);
        if (mapEpic.containsValue(subtask.getIdEpic())) {
            updateStatusEpic(getEpicForUpdate(subtask.getIdEpic()));
        }
    }

    // история просмотра задач
    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    // возвращает список задач и подзадач по приоритету — то есть по startTime
    @Override
    public List<Task> getPrioritizedTasks() {
        return sortSetTask.stream().collect(Collectors.toList());
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

    // проверка статуса подзадач эпика - все новые или всё сделано
    private boolean checkStatusSubtask(List<Subtask> listSubtask, StatusTask checkStatus) {
        boolean check = true;
        for (Subtask subtask : listSubtask) {
            if (!subtask.getStatusTask().equals(checkStatus)) {
                check = false;
            }
        }
        return check;
    }

    // обновление дат, времени начала и окончания эпика, его продолжительности
    private void updateTimesDurationEpic(Epic epic) {
        final List<Subtask> subtasks = getListSubtaskByEpic(epic);
        LocalDateTime startTime = LocalDateTime.MAX;
        int duration = 0;
        LocalDateTime endTime = LocalDateTime.MIN;

        for (Subtask subtask : subtasks) {
            if (subtask.getStartTime().isBefore(startTime)) {
                startTime = subtask.getStartTime();
            }
            duration += subtask.getDuration();
            if (subtask.getEndTime().isAfter(endTime)) {
                endTime = subtask.getEndTime();
            }
        }
        if (startTime.isAfter(endTime)) {
            endTime = startTime;
        }
        epic.setStartTime(startTime);
        epic.setDuration(duration);
        epic.setEndTime(endTime);
    }

    // проверка на пересечение во времени задач и подзадач с помощью множества
    private boolean checkTimeCross(Task task) {
        final List<Task> sortTasks = getPrioritizedTasks();
        for (Task sortTask : sortTasks) {
            if (sortTask.getId() == task.getId()) {
                continue; // если обновляем задачу
            }
            if (((sortTask.getStartTime().isBefore(task.getEndTime())) ||
                    (sortTask.getStartTime().isEqual(task.getEndTime()))) &&
                    ((sortTask.getEndTime().isAfter(task.getStartTime())) ||
                            (sortTask.getEndTime().isEqual(task.getStartTime())))) {
                return false;
            }
        }
        return true;
    }

    // проверка на пересечение во времени задач и подзадач с помощью дерева
    private Boolean checkTimeCrossMap(Task task) {
        if (task == null) {
            return false;
        }
        LocalDateTime curTime = task.getStartTime();
        while (true) {
            if (curTime.isEqual(LocalDateTime.MAX)) {
                return true;
            }
            if (curTime.isEqual(LocalDateTime.MAX.minusNanos(999999999))) {
                return true;
            }
            int curId = busyTime.get(curTime);
            if ((curId != 0) && (curId != task.getId())) {
                return false;
            }
            curTime = curTime.plusMinutes(15);
            if (curTime.isEqual(task.getEndTime())) {
                break;
            }
        }
        return true;
    }
}