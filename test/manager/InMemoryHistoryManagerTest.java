package manager;

import org.junit.jupiter.api.Test;
import tasks.Status;
import tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    HistoryManager historyManager = new InMemoryHistoryManager();

    @Test
    void addShouldStorePreviousVersionsOfTasks() {
        Task task1 = new Task("Task", "Description", Status.NEW);
        task1.setId(1);
        historyManager.add(task1);
        task1.setDescription("New Description");
        task1.setTitle("New title");
        historyManager.add(task1);
        List<Task> history = historyManager.getHistory();

        assertEquals(2, history.size());
        assertNotEquals(history.get(0).getDescription(), history.get(1).getDescription());
        assertNotEquals(history.get(0).getTitle(), history.get(1).getTitle());
    }

}