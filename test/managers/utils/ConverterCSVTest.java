package managers.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ConverterCSVTest {
    @BeforeEach
    public void beforeEach() {
        Task.setNextId(0); // обновляем внутренний счётчик сквозной нумерации
    }

    @Test
    public void fromStringTask() {
        // тест создания задачи из строки файла
        // params = [id,type,name,status,description,epic]
        Task task = ConverterCSV.fromString("1,TASK,Test task,NEW,Test task description,");

        assertNotNull(task, "Задача не найдена.");
        assertEquals(1, task.getId(), "Неверное id задачи.");
        assertEquals(TypeTask.TASK, task.getType(), "Неверный тип задачи.");
        assertEquals("Test task", task.getTitle(), "Неверное наименование задачи.");
        assertEquals(StatusTask.NEW, task.getStatusTask(), "Неверный статус задачи.");
        assertEquals("Test task description", task.getDescription(), "Неверное описание задачи.");
        assertEquals(1, Task.getNextId(), "Неверное id следующей задачи.");
    }

    @Test
    public void fromStringEpic() {
        // тест создания задачи из строки файла
        // params = [id,type,name,status,description,epic]
        Epic epic = (Epic) ConverterCSV.fromString("3,EPIC,Test epic,IN_PROGRESS,Test epic description,");

        assertNotNull(epic, "Эпик не найден.");
        assertEquals(3, epic.getId(), "Неверное id эпика.");
        assertEquals(TypeTask.EPIC, epic.getType(), "Неверный тип эпика.");
        assertEquals("Test epic", epic.getTitle(), "Неверное наименование эпика.");
        assertEquals(StatusTask.IN_PROGRESS, epic.getStatusTask(), "Неверный статус эпика.");
        assertEquals("Test epic description", epic.getDescription(), "Неверное описание эпика.");
        assertEquals(3, Epic.getNextId(), "Неверное id следующей задачи.");
    }

    @Test
    public void fromStringSubtask() {
        // тест создания подзадачи из строки файла
        // params = [id,type,name,status,description,epic]
        Subtask subtask = (Subtask) ConverterCSV.fromString("4,SUBTASK,Test subtask,DONE,Test subtask description,3");

        assertNotNull(subtask, "Задача не найдена.");
        assertEquals(4, subtask.getId(), "Неверное id задачи.");
        assertEquals(TypeTask.SUBTASK, subtask.getType(), "Неверный тип задачи.");
        assertEquals("Test subtask", subtask.getTitle(), "Неверное наименование задачи.");
        assertEquals(StatusTask.DONE, subtask.getStatusTask(), "Неверный статус задачи.");
        assertEquals("Test subtask description", subtask.getDescription(), "Неверное описание задачи.");
        assertEquals(3, subtask.getIdEpic(), "Неверный статус задачи.");
        assertEquals(4, Subtask.getNextId(), "Неверное id следующей задачи.");
    }

    @Test
    public void fromStringEmptyTask() {
        // тест создания нулевой задачи из строки файла
        Task task = ConverterCSV.fromString("");

        assertNull(task, "Задача найдена.");
    }

    @Test
    public void historyFromString() {
        // метод восстановления менеджера истории из строки CSV
        final List<Integer> idHistory = ConverterCSV.historyFromString("3,6,1");

        assertNotNull(idHistory, "История задач не найдена.");
        assertEquals(3, idHistory.get(0), "Неверное id истории задачи.");
        assertEquals(6, idHistory.get(1), "Неверное id истории задачи.");
        assertEquals(1, idHistory.get(2), "Неверное id истории задачи.");
    }

    @Test
    public void historyFromEmptyString() {
        // метод восстановления менеджера истории из пустой строки CSV
        final List<Integer> idHistory = ConverterCSV.historyFromString("");

        assertEquals(0, idHistory.size(), "История задач найдена.");
    }

    @Test
    public void historyFromNullString() {
        // метод восстановления менеджера истории из нулевой строки CSV
        final List<Integer> idHistory = ConverterCSV.historyFromString(null);

        assertEquals(0, idHistory.size(), "История задач найдена.");
    }
}