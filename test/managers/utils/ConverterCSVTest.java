package managers.utils;

import tasks.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import static managers.utils.LocalDateTimeAdapter.FORMATTER;
import static org.junit.jupiter.api.Assertions.*;

public class ConverterCSVTest {

    @BeforeEach
    public void beforeEach() {
        Task.setNextId(0); // обновляем внутренний счётчик сквозной нумерации
    }

    @Test
    @DisplayName("тест создания задачи из строки файла")
    public void fromStringTask() {
        // params = [id,type,name,status,description,epic,startTime,duration,endTime]
        Task task = ConverterCSV.fromString("1,TASK,Test task,NEW," +
                "Test task description,,15.03.2023 10:00:00,15,");

        assertNotNull(task, "Задача не найдена.");
        assertEquals(1, task.getId(), "Неверное id задачи.");
        assertEquals(TypeTask.TASK, task.getType(), "Неверный тип задачи.");
        assertEquals("Test task", task.getTitle(), "Неверное наименование задачи.");
        assertEquals(StatusTask.NEW, task.getStatusTask(), "Неверный статус задачи.");
        assertEquals("Test task description", task.getDescription(), "Неверное описание задачи.");
        assertEquals(LocalDateTime.parse("15.03.2023 10:00:00", FORMATTER), task.getStartTime(),
                "Неверное время старта задачи.");
        assertEquals(15, task.getDuration(), "Неверная продолжительность задачи.");
        assertEquals(1, Task.getNextId(), "Неверное id следующей задачи.");
    }

    @Test
    @DisplayName("тест создания эпика из строки файла")
    public void fromStringEpic() {
        // params = [id,type,name,status,description,epic,startTime,duration,endTime]
        Epic epic = (Epic) ConverterCSV.fromString("3,EPIC,Test epic," +
                "IN_PROGRESS,Test epic description,,31.12.+999999999 23:59:59,0,31.12.+999999999 23:59:59");

        assertNotNull(epic, "Эпик не найден.");
        assertEquals(3, epic.getId(), "Неверное id эпика.");
        assertEquals(TypeTask.EPIC, epic.getType(), "Неверный тип эпика.");
        assertEquals("Test epic", epic.getTitle(), "Неверное наименование эпика.");
        assertEquals(StatusTask.IN_PROGRESS, epic.getStatusTask(), "Неверный статус эпика.");
        assertEquals("Test epic description", epic.getDescription(), "Неверное описание эпика.");
        assertEquals(LocalDateTime.parse("31.12.+999999999 23:59:59", FORMATTER),
                epic.getStartTime(), "Неверное время старта эпика.");
        assertEquals(0, epic.getDuration(), "Неверная продолжительность эпика.");
        assertEquals(LocalDateTime.parse("31.12.+999999999 23:59:59", FORMATTER),
                epic.getEndTime(), "Неверное время окончания эпика.");
        assertEquals(3, Epic.getNextId(), "Неверное id следующей задачи.");
    }

    @Test
    @DisplayName("тест создания подзадачи из строки файла")
    public void fromStringSubtask() {
        // params = [id,type,name,status,description,epic,startTime,duration,endTime]
        Subtask subtask = (Subtask) ConverterCSV.fromString("4,SUBTASK,Test subtask," +
                "DONE,Test subtask description,3,15.03.2023 10:00:00,15,");

        assertNotNull(subtask, "Задача не найдена.");
        assertEquals(4, subtask.getId(), "Неверное id задачи.");
        assertEquals(TypeTask.SUBTASK, subtask.getType(), "Неверный тип задачи.");
        assertEquals("Test subtask", subtask.getTitle(), "Неверное наименование задачи.");
        assertEquals(StatusTask.DONE, subtask.getStatusTask(), "Неверный статус задачи.");
        assertEquals("Test subtask description", subtask.getDescription(),
                "Неверное описание задачи.");
        assertEquals(3, subtask.getIdEpic(), "Неверный id эпика подзадачи.");
        assertEquals(LocalDateTime.parse("15.03.2023 10:00:00", FORMATTER),
                subtask.getStartTime(), "Неверное время старта подзадачи.");
        assertEquals(15, subtask.getDuration(), "Неверная продолжительность подзадачи.");
        assertEquals(4, Subtask.getNextId(), "Неверное id следующей задачи.");
    }

    @Test
    @DisplayName("тест создания нулевой задачи из строки файла")
    public void fromStringEmptyTask() {
        Task task = ConverterCSV.fromString("");

        assertNull(task, "Задача найдена.");
    }

    @Test
    @DisplayName("тест восстановления менеджера истории из строки CSV")
    public void historyFromString() {
        final List<Integer> idHistory = ConverterCSV.historyFromString("3,6,1");

        assertNotNull(idHistory, "История задач не найдена.");
        assertEquals(3, idHistory.get(0), "Неверное id истории задачи.");
        assertEquals(6, idHistory.get(1), "Неверное id истории задачи.");
        assertEquals(1, idHistory.get(2), "Неверное id истории задачи.");
    }

    @Test
    @DisplayName("тест восстановления менеджера истории из пустой строки CSV")
    public void historyFromEmptyString() {
        final List<Integer> idHistory = ConverterCSV.historyFromString("");

        assertEquals(0, idHistory.size(), "История задач найдена.");
    }

    @Test
    @DisplayName("тест восстановления менеджера истории из нулевой строки CSV")
    public void historyFromNullString() {
        final List<Integer> idHistory = ConverterCSV.historyFromString(null);

        assertEquals(0, idHistory.size(), "История задач найдена.");
    }
}