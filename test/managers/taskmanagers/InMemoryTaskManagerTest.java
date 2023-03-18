package managers.taskmanagers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.List;


class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    // тесты для класса InMemoryTaskManagerTest
    @Override
    public void setTaskManager() {
        taskManager = new InMemoryTaskManager();
    }

    @BeforeEach
    // устанавливаем тип менеджера в наследнике
    /////// Очень хотелось бы сделать это с аннотацией BeforeAll, но он статик
    /////// испробовала всё, что знаю, не получилось. Может подскажешь?
    public void beforeEach() {
        setTaskManager();
        Task.setNextId(0); // обновляем внутренний счётчик сквозной нумерации
        task1 = new Task("Test task1", "Test task1 description");
        task2 = new Task("Test task2", "Test task2 description");
        epic3 = new Epic("Test epic3", "Test epic3 description");
        subtask4 = new Subtask("Test subtask4", "Test subtask4 description", epic3.getId());
        subtask5 = new Subtask("Test subtask5", "Test subtask5 description", epic3.getId());
        subtask6 = new Subtask("Test subtask6", "Test subtask6 description", epic3.getId());
        epic7 = new Epic("Test epic7", "Test epic7 description");
    }

    @Test
    public void getListSubtaskByEpic() {
        // получение списка подзадач по эпику
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