package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    abstract T createTaskManager();

    protected static LocalDateTime startTime = LocalDateTime.of(2025, 2, 18, 10, 0);

    @BeforeEach
    void setUp() {
        taskManager = createTaskManager();
    }

    @Test
    void addTaskAddTaskToManager() {
        Task task = new Task("Task 1", "Description 1", Status.NEW);
        taskSetDefaultTime(task);
        taskManager.addTask(task);
        assertEquals(task, taskManager.getTaskById(task.getId()).orElse(null));
    }

    @Test
    void addTaskPreserveTaskData() {
        Task task = new Task("Task 1", "Description 1", Status.NEW);
        task.setId(1);
        taskSetDefaultTime(task);
        Task taskCopy = new Task(task);
        taskManager.addTask(task);

        assertEquals(taskCopy.getTitle(), taskManager.getTaskById(task.getId()).map(Task::getTitle).
                orElseThrow(() -> new IllegalArgumentException("Задача не найдена")));
        assertEquals(taskCopy.getDescription(), taskManager.getTaskById(task.getId()).map(Task::getDescription)
                .orElseThrow(() -> new IllegalArgumentException("Задача не найдена")));
        assertEquals(taskCopy.getStatus(), taskManager.getTaskById(task.getId()).map(Task::getStatus)
                .orElseThrow(() -> new IllegalArgumentException("Задача не найдена")));
    }

    @Test
    void tasksWithSetIdDoNotConflict() {
        Task task1 = new Task("Task 1", "Description 1", Status.NEW);
        task1.setId(228);
        taskSetDefaultTime(task1);
        taskManager.addTask(task1);
        Task task2 = new Task("Task 2", "Description 2", Status.NEW);
        taskSetDefaultTime(task2);
        taskManager.addTask(task2);

        assertNotNull(taskManager.getTaskById(task1.getId()));
        assertNotNull(taskManager.getTaskById(task2.getId()));
        assertNotEquals(taskManager.getTaskById(task1.getId()), taskManager.getTaskById(task2.getId()));
    }

    @Test
    void addEpicAddEpicToManager() {
        Epic epic = new Epic("Epic 1", "Description 1");
        taskManager.addEpic(epic);
        assertEquals(epic, taskManager.getEpicById(epic.getId()).orElse(null));
    }

    @Test
    void addSubtaskAddToManagerAndLinkSubtaskToEpic() {
        Epic epic = new Epic("Epic 1", "Description 1");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask 1", "Description 1", Status.NEW, epic.getId());
        taskSetDefaultTime(subtask);
        taskManager.addSubtask(subtask);

        assertTrue(epic.getSubtaskIds().contains(subtask.getId()));
        assertEquals(subtask, taskManager.getSubtaskById(subtask.getId()).orElse(null));
    }

    @Test
    void getAllTasksReturnAllAddedTasks() {
        Task task1 = new Task("Task 1", "Description 1", Status.NEW);
        taskSetDefaultTime(task1);
        Task task2 = new Task("Task 2", "Description 2", Status.NEW);
        taskSetDefaultTime(task2);
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        ArrayList<Task> tasks = (ArrayList<Task>) taskManager.getAllTasks();

        assertTrue(tasks.contains(task1));
        assertTrue(tasks.contains(task2));
    }

    @Test
    void getAllEpicsReturnAllAddedEpics() {
        Epic epic1 = new Epic("Epic 1", "Description 1");
        Epic epic2 = new Epic("Epic 2", "Description 2");
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        ArrayList<Epic> epics = (ArrayList<Epic>) taskManager.getAllEpics();

        assertTrue(epics.contains(epic1));
        assertTrue(epics.contains(epic2));
    }

    @Test
    void getAllSubtasksReturnAllAddedSubtasks() {
        Epic epic = new Epic("Epic 1", "Description 1");
        taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", Status.NEW, epic.getId());
        taskSetDefaultTime(subtask1);

        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", Status.NEW, epic.getId());
        taskSetDefaultTime(subtask2);

        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        ArrayList<Subtask> subtasks = (ArrayList<Subtask>) taskManager.getAllSubtasks();

        assertTrue(subtasks.contains(subtask1));
        assertTrue(subtasks.contains(subtask2));
    }

    @Test
    void clearAllTasksRemoveAllTasks() {
        Task task = new Task("Task 1", "Description 1", Status.NEW);
        taskSetDefaultTime(task);
        taskManager.addTask(task);
        taskManager.clearAllTasks();
        assertEquals(0, taskManager.getAllTasks().size());
    }

    @Test
    void clearAllEpicsRemoveAllEpicsAndSubtasks() {
        Epic epic = new Epic("Epic 1", "Description 1");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask 1", "Description 1", Status.NEW, epic.getId());
        taskSetDefaultTime(subtask);

        taskManager.addSubtask(subtask);
        taskManager.clearAllEpics();

        assertEquals(0, taskManager.getAllEpics().size());
        assertEquals(0, taskManager.getAllSubtasks().size());
    }

    @Test
    void clearAllSubtasksRemoveAllSubtasksAndUnlinkFromEpics() {
        Epic epic = new Epic("Epic 1", "Description 1");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask 1", "Description 1", Status.NEW, epic.getId());
        taskSetDefaultTime(subtask);

        taskManager.addSubtask(subtask);
        taskManager.clearAllSubtasks();

        assertEquals(0, taskManager.getAllSubtasks().size());
        assertEquals(0, taskManager.getSubtasksOfEpic(epic.getId()).size());
    }

    @Test
    void getTaskByIdReturnCorrectTask() {
        Task task = new Task("Task 1", "Description 1", Status.NEW);
        taskSetDefaultTime(task);
        taskManager.addTask(task);

        assertEquals(task, taskManager.getTaskById(task.getId()).orElse(null));
    }

    @Test
    void getEpicByIdReturnCorrectEpic() {
        Epic epic = new Epic("Epic 1", "Description 1");
        taskManager.addEpic(epic);

        assertEquals(epic, taskManager.getEpicById(epic.getId()).orElse(null));
    }

    @Test
    void getSubtaskByIdReturnCorrectSubtask() {
        Epic epic = new Epic("Epic 1", "Description 1");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask 1", "Description 1", Status.NEW, epic.getId());
        taskSetDefaultTime(subtask);
        taskManager.addSubtask(subtask);

        assertEquals(subtask, taskManager.getSubtaskById(subtask.getId()).orElse(null));
    }

    @Test
    void updateTaskModifyTaskData() {
        Task task = new Task("Task 1", "Description 1", Status.NEW);
        taskSetDefaultTime(task);
        taskManager.addTask(task);
        Task task1 = new Task("Task 2", "Description 2", Status.IN_PROGRESS);
        taskSetDefaultTime(task1);
        task1.setId(task.getId());
        taskManager.updateTask(task1);

        assertEquals("Task 2", taskManager.getTaskById(task.getId()).map(Task::getTitle)
                .orElseThrow(() -> new IllegalArgumentException("Задача не найдена")));
        assertEquals("Description 2", taskManager.getTaskById(task.getId()).map(Task::getDescription)
                .orElseThrow(() -> new IllegalArgumentException("Задача не найдена")));
        assertEquals(Status.IN_PROGRESS, taskManager.getTaskById(task.getId()).map(Task::getStatus)
                .orElseThrow(() -> new IllegalArgumentException("Задача не найдена")));
    }


    @Test
    void updateEpicModifyEpicData() {
        Epic epic = new Epic("Epic 1", "Description 1");
        taskManager.addEpic(epic);
        Epic epic1 = new Epic("Epic 2", "Description 2");
        epic1.setId(epic.getId());
        taskManager.updateEpic(epic1);

        assertEquals("Epic 2", taskManager.getEpicById(epic.getId()).map(Task::getTitle)
                .orElseThrow(() -> new IllegalArgumentException("Задача не найдена")));
        assertEquals("Description 2", taskManager.getEpicById(epic.getId()).map(Task::getDescription)
                .orElseThrow(() -> new IllegalArgumentException("Задача не найдена")));
    }

    @Test
    void updateSubtaskModifySubtaskData() {
        Epic epic = new Epic("Epic 1", "Description 1");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask 1", "Description 1", Status.NEW, epic.getId());
        taskSetDefaultTime(subtask);
        taskManager.addSubtask(subtask);

        assertEquals("Subtask 1", taskManager.getSubtaskById(subtask.getId()).map(Task::getTitle)
                .orElseThrow(() -> new IllegalArgumentException("Задача не найдена")));
        assertEquals("Description 1", taskManager.getSubtaskById(subtask.getId()).map(Task::getDescription)
                .orElseThrow(() -> new IllegalArgumentException("Задача не найдена")));

        Subtask newSubtask = new Subtask("Subtask 2", "Description 2", Status.IN_PROGRESS, epic.getId());
        taskSetDefaultTime(newSubtask);

        newSubtask.setId(subtask.getId());
        taskManager.updateSubtask(newSubtask);

        assertEquals("Subtask 2", taskManager.getSubtaskById(subtask.getId()).map(Task::getTitle)
                .orElseThrow(() -> new IllegalArgumentException("Задача не найдена")));
        assertEquals("Description 2", taskManager.getSubtaskById(subtask.getId()).map(Task::getDescription)
                .orElseThrow(() -> new IllegalArgumentException("Задача не найдена")));
    }

    @Test
    void deleteTaskRemoveTaskFromManager() {
        Task task = new Task("Task 1", "Description 1", Status.NEW);
        taskSetDefaultTime(task);
        taskManager.addTask(task);
        taskManager.deleteTask(task.getId());

        assertTrue(taskManager.getTaskById(task.getId()).isEmpty());
    }

    @Test
    void deleteEpicRemoveEpicFromManagerAndLinkedSubtasks() {
        Epic epic = new Epic("Epic 1", "Description 1");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask 1", "Description 1", Status.NEW, epic.getId());
        taskSetDefaultTime(subtask);
        taskManager.addSubtask(subtask);
        taskManager.deleteEpic(epic.getId());

        assertTrue(taskManager.getEpicById(epic.getId()).isEmpty());
        assertTrue(taskManager.getSubtaskById(subtask.getId()).isEmpty());
    }

    @Test
    void deleteSubtaskRemoveSubtaskFromManager() {
        Epic epic = new Epic("Epic 1", "Description 1");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask 1", "Description 1", Status.NEW, epic.getId());
        taskSetDefaultTime(subtask);
        taskManager.addSubtask(subtask);
        taskManager.deleteSubtask(subtask.getId());

        assertTrue(taskManager.getSubtaskById(subtask.getId()).isEmpty());
        assertEquals(0, taskManager.getSubtasksOfEpic(epic.getId()).size());
    }

    @Test
    void getSubtasksOfEpicReturnAllLinkedSubtasks() {
        Epic epic = new Epic("Epic 1", "Description 1");
        taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", Status.NEW, epic.getId());
        taskSetDefaultTime(subtask1);
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", Status.NEW, epic.getId());
        taskSetDefaultTime(subtask2);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        ArrayList<Subtask> subtasks = (ArrayList<Subtask>) taskManager.getSubtasksOfEpic(epic.getId());

        assertTrue(subtasks.contains(subtask1));
        assertTrue(subtasks.contains(subtask2));
    }

    @Test
    void getHistorySaveTaskOnGetTaskCall() {
        Task task = new Task("Task 1", "Description 1", Status.NEW);
        taskSetDefaultTime(task);
        taskManager.addTask(task);
        taskManager.getTaskById(task.getId());

        assertEquals(1, taskManager.getHistory().size());
        assertTrue(taskManager.getHistory().contains(task));
    }

    @Test
    void shouldSetEpicStatusToDoneIfAllSubtasksAreDone() {
        Epic epic = new Epic("Epic 1", "Description 1");
        taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", Status.DONE, epic.getId());
        taskSetDefaultTime(subtask1);
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", Status.DONE, epic.getId());
        taskSetDefaultTime(subtask2);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        assertTrue(taskManager.getAllSubtasks().stream()
                .allMatch(subtask -> subtask.getStatus() == Status.DONE));
        assertSame(Status.DONE, epic.getStatus());
    }

    @Test
    void shouldSetEpicStatusToNewIfAllSubtasksAreNew() {
        Epic epic = new Epic("Epic 1", "Description 1");
        taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", Status.NEW, epic.getId());
        taskSetDefaultTime(subtask1);
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", Status.NEW, epic.getId());
        taskSetDefaultTime(subtask2);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        assertTrue(taskManager.getAllSubtasks().stream()
                .allMatch(subtask -> subtask.getStatus() == Status.NEW));
        assertSame(Status.NEW, epic.getStatus());
    }

    @Test
    void shouldSetEpicStatusToInProgressIfSubtasksAreNewAndDone() {
        Epic epic = new Epic("Epic 1", "Description 1");
        taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", Status.NEW, epic.getId());
        taskSetDefaultTime(subtask1);
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", Status.DONE, epic.getId());
        taskSetDefaultTime(subtask2);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        assertSame(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void shouldSetEpicStatusToInProgressIfAllSubtasksAreInProgress() {
        Epic epic = new Epic("Epic 1", "Description 1");
        taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", Status.IN_PROGRESS, epic.getId());
        taskSetDefaultTime(subtask1);
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", Status.IN_PROGRESS, epic.getId());
        taskSetDefaultTime(subtask2);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        assertSame(Status.IN_PROGRESS, epic.getStatus());
    }

    protected void taskSetDefaultTime(Task task) {
        task.setStartTime(startTime);
        task.setDuration(Duration.ofMinutes(30));
        startTime = startTime.plusMinutes(35);
    }
}
