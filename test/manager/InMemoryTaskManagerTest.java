package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private InMemoryTaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    void addTaskAddTaskToManager() {
        Task task = new Task("Task 1", "Description 1", Status.NEW);
        taskManager.addTask(task);
        assertEquals(task, taskManager.getTaskById(task.getId()));
    }

    @Test
    void addTaskPreserveTaskData() {
        Task task = new Task("Task 1", "Description 1", Status.NEW);
        task.setId(1);
        Task taskCopy = new Task(task);
        taskManager.addTask(task);

        assertEquals(taskCopy.getTitle(), taskManager.getTaskById(task.getId()).getTitle());
        assertEquals(taskCopy.getDescription(), taskManager.getTaskById(task.getId()).getDescription());
        assertEquals(taskCopy.getStatus(), taskManager.getTaskById(task.getId()).getStatus());
    }

    @Test
    void tasksWithSetIdDoNotConflict() {
        Task task1 = new Task("Task 1", "Description 1", Status.NEW);
        task1.setId(228);
        taskManager.addTask(task1);
        Task task2 = new Task("Task 2", "Description 2", Status.NEW);
        taskManager.addTask(task2);

        assertNotNull(taskManager.getTaskById(task1.getId()));
        assertNotNull(taskManager.getTaskById(task2.getId()));
        assertNotEquals(taskManager.getTaskById(task1.getId()), taskManager.getTaskById(task2.getId()));
    }

    @Test
    void addEpicAddEpicToManager() {
        Epic epic = new Epic("Epic 1", "Description 1");
        taskManager.addEpic(epic);
        assertEquals(epic, taskManager.getEpicById(epic.getId()));
    }

    @Test
    void addSubtaskAddToManagerAndLinkSubtaskToEpic() {
        Epic epic = new Epic("Epic 1", "Description 1");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask 1", "Description 1", Status.NEW, epic.getId());
        taskManager.addSubtask(subtask);

        assertTrue(epic.getSubtaskIds().contains(subtask.getId()));
        assertEquals(subtask, taskManager.getSubtaskById(subtask.getId()));
    }

    @Test
    void getAllTasksReturnAllAddedTasks() {
        Task task1 = new Task("Task 1", "Description 1", Status.NEW);
        Task task2 = new Task("Task 2", "Description 2", Status.NEW);
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        ArrayList<Task> tasks = taskManager.getAllTasks();

        assertTrue(tasks.contains(task1));
        assertTrue(tasks.contains(task2));
    }

    @Test
    void getAllEpicsReturnAllAddedEpics() {
        Epic epic1 = new Epic("Epic 1", "Description 1");
        Epic epic2 = new Epic("Epic 2", "Description 2");
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        ArrayList<Epic> epics = taskManager.getAllEpics();

        assertTrue(epics.contains(epic1));
        assertTrue(epics.contains(epic2));
    }

    @Test
    void getAllSubtasksReturnAllAddedSubtasks() {
        Epic epic = new Epic("Epic 1", "Description 1");
        taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", Status.NEW, epic.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", Status.NEW, epic.getId());
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        ArrayList<Subtask> subtasks = taskManager.getAllSubtasks();

        assertTrue(subtasks.contains(subtask1));
        assertTrue(subtasks.contains(subtask2));
    }

    @Test
    void clearAllTasksRemoveAllTasks() {
        Task task = new Task("Task 1", "Description 1", Status.NEW);
        taskManager.addTask(task);
        taskManager.clearAllTasks();
        assertEquals(0, taskManager.getAllTasks().size());
    }

    @Test
    void clearAllEpicsRemoveAllEpicsAndSubtasks() {
        Epic epic = new Epic("Epic 1", "Description 1");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask 1", "Description 1", Status.NEW, epic.getId());
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
        taskManager.addSubtask(subtask);
        taskManager.clearAllSubtasks();

        assertEquals(0, taskManager.getAllSubtasks().size());
        assertEquals(0, taskManager.getSubtasksOfEpic(epic.getId()).size());
    }

    @Test
    void getTaskByIdReturnCorrectTask() {
        Task task = new Task("Task 1", "Description 1", Status.NEW);
        taskManager.addTask(task);

        assertEquals(task, taskManager.getTaskById(task.getId()));
    }

    @Test
    void getEpicByIdReturnCorrectEpic() {
        Epic epic = new Epic("Epic 1", "Description 1");
        taskManager.addEpic(epic);

        assertEquals(epic, taskManager.getEpicById(epic.getId()));
    }

    @Test
    void getSubtaskByIdReturnCorrectSubtask() {
        Epic epic = new Epic("Epic 1", "Description 1");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask 1", "Description 1", Status.NEW, epic.getId());
        taskManager.addSubtask(subtask);

        assertEquals(subtask, taskManager.getSubtaskById(subtask.getId()));
    }

    @Test
    void updateTaskModifyTaskData() {
        Task task = new Task("Task 1", "Description 1", Status.NEW);
        taskManager.addTask(task);
        Task task1 = new Task("Task 2", "Description 2", Status.IN_PROGRESS);
        task1.setId(task.getId());
        taskManager.updateTask(task1);

        assertEquals("Task 2", taskManager.getTaskById(task.getId()).getTitle());
        assertEquals("Description 2", taskManager.getTaskById(task.getId()).getDescription());
        assertEquals(Status.IN_PROGRESS, taskManager.getTaskById(task.getId()).getStatus());
    }


    @Test
    void updateEpicModifyEpicData() {
        Epic epic = new Epic("Epic 1", "Description 1");
        taskManager.addEpic(epic);
        Epic epic1 = new Epic("Epic 2", "Description 2");
        epic1.setId(epic.getId());
        taskManager.updateEpic(epic1);

        assertEquals("Epic 2", taskManager.getEpicById(epic.getId()).getTitle());
        assertEquals("Description 2", taskManager.getEpicById(epic.getId()).getDescription());
    }

    @Test
    void updateSubtaskModifySubtaskData() {
        Epic epic = new Epic("Epic 1", "Description 1");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask 1", "Description 1", Status.NEW, epic.getId());
        taskManager.addSubtask(subtask);

        assertEquals("Subtask 1", taskManager.getSubtaskById(subtask.getId()).getTitle());
        assertEquals("Description 1", taskManager.getSubtaskById(subtask.getId()).getDescription());

        Subtask newSubtask = new Subtask("Subtask 2", "Description 2", Status.IN_PROGRESS, epic.getId());
        newSubtask.setId(subtask.getId());
        taskManager.updateSubtask(newSubtask);

        assertEquals("Subtask 2", taskManager.getSubtaskById(subtask.getId()).getTitle());
        assertEquals("Description 2", taskManager.getSubtaskById(subtask.getId()).getDescription());
    }

    @Test
    void addSubtaskNotAllowSubtaskToBeItsOwnEpic() {
        Epic epic = new Epic("Epic 1", "Description 1");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask", "Description", Status.NEW, 1);
        subtask.setId(subtask.getEpicId());
        taskManager.addSubtask(subtask);

        assertNull(taskManager.getSubtaskById(subtask.getId()));
        assertFalse(epic.getSubtaskIds().contains(subtask.getId()));
    }

    @Test
    void deleteTaskRemoveTaskFromManager() {
        Task task = new Task("Task 1", "Description 1", Status.NEW);
        taskManager.addTask(task);
        taskManager.deleteTask(task.getId());

        assertNull(taskManager.getTaskById(task.getId()));
    }

    @Test
    void deleteEpicRemoveEpicFromManagerAndLinkedSubtasks() {
        Epic epic = new Epic("Epic 1", "Description 1");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask 1", "Description 1", Status.NEW, epic.getId());
        taskManager.addSubtask(subtask);
        taskManager.deleteEpic(epic.getId());

        assertNull(taskManager.getEpicById(epic.getId()));
        assertNull(taskManager.getSubtaskById(subtask.getId()));
    }

    @Test
    void deleteSubtaskRemoveSubtaskFromManager() {
        Epic epic = new Epic("Epic 1", "Description 1");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask 1", "Description 1", Status.NEW, epic.getId());
        taskManager.addSubtask(subtask);
        taskManager.deleteSubtask(subtask.getId());

        assertNull(taskManager.getSubtaskById(subtask.getId()));
        assertEquals(0, taskManager.getSubtasksOfEpic(epic.getId()).size());
    }

    @Test
    void getSubtasksOfEpicReturnAllLinkedSubtasks() {
        Epic epic = new Epic("Epic 1", "Description 1");
        taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", Status.NEW, epic.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", Status.NEW, epic.getId());
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        ArrayList<Subtask> subtasks = taskManager.getSubtasksOfEpic(epic.getId());

        assertTrue(subtasks.contains(subtask1));
        assertTrue(subtasks.contains(subtask2));
    }

    @Test
    void getHistorySaveTaskOnGetTaskCall() {
        Task task = new Task("Task 1", "Description 1", Status.NEW);
        taskManager.addTask(task);
        taskManager.getTaskById(task.getId());

        assertEquals(1, taskManager.getHistory().size());
        assertTrue(taskManager.getHistory().contains(task));
    }

    @Test
    void getHistoryContainOnlyLast10CalledTasksAndRemoveOldest() {
        Task task1 = new Task("Task 1", "Description 1", Status.NEW);
        Task task2 = new Task("Task 2", "Description 2", Status.NEW);
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        for (int i = 0; i < 10; i++) {
            taskManager.getTaskById(task1.getId());
        }

        assertTrue(taskManager.getHistory().contains(task1));
        assertEquals(10, taskManager.getHistory().size());

        taskManager.getTaskById(task2.getId());

        assertEquals(10, taskManager.getHistory().size());
        assertTrue(taskManager.getHistory().contains(task2));
    }
}