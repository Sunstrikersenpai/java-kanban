package manager;

import org.junit.jupiter.api.Test;
import tasks.Status;
import tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    TaskManager taskManager = Managers.getDefault();

    @Test
    void historyStoresDifferentTaskVersions() {
        Task task = new Task("Task", "Description", Status.NEW);
        taskManager.addTask(task);
        taskManager.getTaskById(task.getId());
        task.setTitle("Updated Task");
        task.setDescription("Updated Description");
        task.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(task);
        taskManager.getTaskById(task.getId());
        List<Task> history = taskManager.getHistory();

        assertEquals(2, history.size());
        assertNotEquals(history.get(0).getDescription(),history.get(1).getDescription());
        assertNotEquals(history.get(0).getTitle(),history.get(1).getTitle());
    }

}