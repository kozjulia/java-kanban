package managers.taskmanagers;

import tasks.Task;
import tasks.Epic;
import tasks.Subtask;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    public void beforeEach() {
        taskManager = new InMemoryTaskManager();
        Task.setNextId(0); // обновляем внутренний счётчик сквозной нумерации
        task1 = new Task("Test task1", "Test task1 description", LocalDateTime.MAX, 15);
        task2 = new Task("Test task2", "Test task2 description",
                LocalDateTime.of(2023, 03, 19, 10, 00), 30);
        epic3 = new Epic("Test epic3", "Test epic3 description");
        subtask4 = new Subtask("Test subtask4", "Test subtask4 description", epic3.getId(),
                LocalDateTime.of(2023, 03, 15, 10, 00), 15);
        subtask5 = new Subtask("Test subtask5", "Test subtask5 description", epic3.getId(),
                LocalDateTime.of(2023, 03, 15, 12, 00), 15);
        subtask6 = new Subtask("Test subtask6", "Test subtask6 description", epic3.getId(),
                LocalDateTime.of(2023, 03, 16, 11, 00), 45);
        epic7 = new Epic("Test epic7", "Test epic7 description");
    }

    @Test
    @DisplayName("тест получения списка подзадач по эпику")
    public void getListSubtaskByEpic() {
        taskManager.createEpic(epic3);
        taskManager.createSubtask(subtask4);
        taskManager.createSubtask(subtask5);
        final List<Subtask> subtasks = taskManager.getListSubtaskByEpic(epic3);

        assertNotNull(subtasks, "Список подзадач эпика пуст");
        assertEquals(2, subtasks.size(), "Размер списка подзадач эпика неправилен");
        assertTrue(subtasks.contains(subtask4), "Подзадача для эпика не найдена");
        assertTrue(subtasks.contains(subtask5), "Подзадача для эпика не найдена");
    }
}