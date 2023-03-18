package managers.historymanagers;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryHistoryManagerTest {
    public static HistoryManager historyManager;
    public static Task task1;
    public static Task task2;
    public static Epic epic3;
    public static Subtask subtask4;
    public static Epic epic5;

    @BeforeAll
    public static void beforeAll() {
        task1 = new Task("Test task1", "Test task1 description");
        task2 = new Task("Test task2", "Test task2 description");
        epic3 = new Epic("Test epic3", "Test epic3 description");
        subtask4 = new Subtask("Test subtask4", "Test subtask4 description", epic3.getId());
        epic5 = new Epic("Test epic5", "Test epic5 description");
    }

    @BeforeEach
    public void beforeEach() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    public void add() {
        // тест добавления задачи в историю
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(epic3);
        List<Task> history = historyManager.getHistory();

        assertNotNull(history, "История не пустая.");
        assertEquals(3, history.size(), "История не пустая.");
        assertEquals(task1, history.get(2), "Очередность истории неправильная.");
        assertEquals(task2, history.get(1), "Очередность истории неправильная.");
        assertEquals(epic3, history.get(0), "Очередность истории неправильная.");

        // дублирование
        historyManager.add(task1);
        history = historyManager.getHistory();

        assertEquals(3, history.size(), "История не пустая.");
        assertEquals(task2, history.get(2), "Очередность истории неправильная.");
        assertEquals(epic3, history.get(1), "Очередность истории неправильная.");
        assertEquals(task1, history.get(0), "Очередность истории неправильная.");
    }

    @Test
    public void addNull() {
        // тест добавления нулевой задачи в историю
        historyManager.add(null);
        final List<Task> history = historyManager.getHistory();

        assertNull(history, "История не пустая.");
    }

    @Test
    public void remove() {
        // тест удаления задачи из истории
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(epic3);
        historyManager.add(subtask4);
        historyManager.add(epic5);
        // удаление из середины истории
        historyManager.remove(3);
        List<Task> history = historyManager.getHistory();

        assertNotNull(history, "История не пустая.");
        assertEquals(4, history.size(), "История не пустая.");
        assertEquals(task1, history.get(3), "Очередность истории неправильная.");
        assertEquals(task2, history.get(2), "Очередность истории неправильная.");
        assertEquals(subtask4, history.get(1), "Очередность истории неправильная.");
        assertEquals(epic5, history.get(0), "Очередность истории неправильная.");

        // удаление с конца истории
        historyManager.remove(5);
        history = historyManager.getHistory();

        assertNotNull(history, "История не пустая.");
        assertEquals(3, history.size(), "История не пустая.");
        assertEquals(task1, history.get(2), "Очередность истории неправильная.");
        assertEquals(task2, history.get(1), "Очередность истории неправильная.");
        assertEquals(subtask4, history.get(0), "Очередность истории неправильная.");

        // удаление с начала истории
        historyManager.remove(1);
        history = historyManager.getHistory();

        assertNotNull(history, "История не пустая.");
        assertEquals(2, history.size(), "История не пустая.");
        assertEquals(task2, history.get(1), "Очередность истории неправильная.");
        assertEquals(subtask4, history.get(0), "Очередность истории неправильная.");
    }

    @Test
    public void removeWrong() {
        // тест удаления задачи по несуществующему id из истории
        historyManager.remove(100);
        final List<Task> history = historyManager.getHistory();

        assertNull(history, "История пустая.");
    }

    @Test
    public void getHistory() {
        // тест получения истории
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(epic3);
        final List<Task> history = historyManager.getHistory();

        assertNotNull(history, "История не пустая.");
        assertEquals(3, history.size(), "История не пустая.");
    }

    @Test
    public void getNullHistory() {
        // тест получения нулевой истории
        final List<Task> history = historyManager.getHistory();

        assertNull(history, "История пустая.");
    }
}