package manager;

import org.junit.jupiter.api.Test;
import tasks.Status;
import tasks.Task;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private final Path file = Path.of("data/data.csv");

    @Override
    FileBackedTaskManager createTaskManager() {
        return new FileBackedTaskManager(file);
    }

    @Test
    void shouldThrowManagerSaveExceptionWhenFileWritingFails() {
        FileBackedTaskManager fbm = new FileBackedTaskManager(Path.of("data"));
        Task task = new Task("Test Task", "Description", Status.NEW);
        task.setId(1);

        assertThrows(ManagerSaveException.class, () -> fbm.addTask(task));
    }

    @Test
    void shouldReturnCorrectTaskFromString() {
        Task task = new Task(1, Status.NEW, "Task 1", "Description 1");
        String stringTask = FileBackedTaskManager.taskToString(task);
        Task restoredTask = FileBackedTaskManager.taskFromString(stringTask);

        assertEquals(task, restoredTask);
        assertEquals(task.getDescription(), restoredTask.getDescription());
        assertEquals(task.getTaskType(), restoredTask.getTaskType());
    }

}
