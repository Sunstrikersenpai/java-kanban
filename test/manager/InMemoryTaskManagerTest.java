package manager;

import org.junit.jupiter.api.Test;
import tasks.Status;
import tasks.Task;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertThrows;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager();
    }

    @Test
    void shouldReturnFalseIfTasksOverlap() {
        Task task1 = new Task("Task 1", "Description 1", Status.NEW);
        task1.setStartTime(startTime);
        task1.setDuration(Duration.ofMinutes(60));
        taskManager.addTask(task1);
        Task task2 = new Task("Task 2", "Description 2", Status.NEW);
        task2.setStartTime(startTime);
        task2.setDuration(Duration.ofMinutes(60));

        assertThrows(IllegalArgumentException.class, () -> taskManager.addTask(task2));
    }
}