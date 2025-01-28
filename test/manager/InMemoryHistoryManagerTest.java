package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Status;
import tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void addTaskToHistory() {
        Task task = new Task("Task 1", "Description 1", Status.NEW);
        task.setId(1);
        historyManager.add(task);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertTrue(history.contains(task));
    }

    @Test
    void addSameTaskMovesToEnd() {
        Task task1 = new Task("Task 1", "Description 1", Status.NEW);
        task1.setId(1);
        Task task2 = new Task("Task 2", "Description 2", Status.NEW);
        task2.setId(2);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task1);

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task2, history.get(0));
        assertEquals(task1, history.get(1));
    }

    @Test
    void aaaaaaaa() {
        Task task1 = new Task("Task 1", "Description 1", Status.NEW);
        task1.setId(1);
        Task task2 = new Task("Task 2", "Description 2", Status.NEW);
        task2.setId(2);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.remove(task1.getId());

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertFalse(history.contains(task1));
        assertTrue(history.contains(task2));
    }

    @Test
    void uuuuuuuu() {
        Task task1 = new Task("Task 1", "Description 1", Status.NEW);
        task1.setId(1);
        Task task2 = new Task("Task 2", "Description 2", Status.NEW);
        task2.setId(2);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.remove(task2.getId());

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertTrue(history.contains(task1));
        assertFalse(history.contains(task2));
    }
}
