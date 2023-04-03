package managers.historymanagers;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class InMemoryHistoryManagerTest {
    private static HistoryManager historyManager;
    private static Task task1;
    private static Task task2;
    private static Epic epic3;
    private static Subtask subtask4;
    private static Epic epic5;

    @BeforeAll
    public static void beforeAll() {
        task1 = new Task("Test task1", "Test task1 description", LocalDateTime.MAX, 20);
        task2 = new Task("Test task2", "Test task2 description",
                LocalDateTime.of(2023, 03, 19, 10, 00), 30);
        epic3 = new Epic("Test epic3", "Test epic3 description");
        subtask4 = new Subtask("Test subtask4", "Test subtask4 description", epic3.getId(),
                LocalDateTime.of(2023, 03, 15, 10, 00), 20);
        epic5 = new Epic("Test epic5", "Test epic5 description");
    }

    @BeforeEach
    public void beforeEach() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    @DisplayName("тест добавления задачи в историю")
    public void add() {
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
    @DisplayName("тест добавления нулевой задачи в историю")
    public void addNull() {
        historyManager.add(null);
        final List<Task> history = historyManager.getHistory();

        assertNull(history, "История не пустая.");
    }

    @Test
    @DisplayName("тест удаления задачи из истории")
    public void remove() {
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

        // удаление из конца истории
        historyManager.remove(5);
        history = historyManager.getHistory();

        assertNotNull(history, "История не пустая.");
        assertEquals(3, history.size(), "История не пустая.");
        assertEquals(task1, history.get(2), "Очередность истории неправильная.");
        assertEquals(task2, history.get(1), "Очередность истории неправильная.");
        assertEquals(subtask4, history.get(0), "Очередность истории неправильная.");

        // удаление из начала истории
        historyManager.remove(1);
        history = historyManager.getHistory();

        assertNotNull(history, "История не пустая.");
        assertEquals(2, history.size(), "История не пустая.");
        assertEquals(task2, history.get(1), "Очередность истории неправильная.");
        assertEquals(subtask4, history.get(0), "Очередность истории неправильная.");
    }

    @Test
    @DisplayName("тест удаления задачи по несуществующему id из истории")
    public void removeWrong() {
        historyManager.remove(100);
        final List<Task> history = historyManager.getHistory();

        assertNull(history, "История пустая.");
    }

    @Test
    @DisplayName("тест получения истории")
    public void getHistory() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(epic3);
        historyManager.remove(2);
        final List<Task> history = historyManager.getHistory();

        assertNotNull(history, "История не пустая.");
        assertEquals(2, history.size(), "История не пустая.");
        assertEquals(epic3, history.get(0), "История неправильно формируется.");
        assertEquals(task1, history.get(1), "История неправильно формируется.");
    }

    @Test
    @DisplayName("тест получения нулевой истории")
    public void getNullHistory() {
        final List<Task> history = historyManager.getHistory();

        assertNull(history, "История пустая.");
    }
}