import tasks.*;
import manager.*;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        Task task1 = new Task("tasks.Task 1", "tasks.Task 1", Status.NEW);
        Task task2 = new Task("tasks.Task 2", "tasks.Task 2", Status.NEW);
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.getTaskById(1);
        taskManager.getTaskById(0);
        for (Task task : taskManager.getHistory()) {
            System.out.println("////////");
            System.out.println(task);

        }

       /* tasks.Epic epic1 = new tasks.Epic("tasks.Epic 1", "tasks.Epic 1");
        taskManager.addEpic(epic1);
        tasks.Subtask subtask1 = new tasks.Subtask("tasks.Subtask 1", "tasks.Subtask 1", tasks.Status.NEW, epic1.getId());
        tasks.Subtask subtask2 = new tasks.Subtask("tasks.Subtask 2", "tasks.Subtask 2", tasks.Status.NEW, epic1.getId());
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        tasks.Epic epic2 = new tasks.Epic("tasks.Epic 2", "tasks.Epic 2");
        taskManager.addEpic(epic2);
        tasks.Subtask subtask3 = new tasks.Subtask("tasks.Subtask 3", "tasks.Subtask 3", tasks.Status.NEW, epic2.getId());
        taskManager.addSubtask(subtask3);

        System.out.println("Tasks: " + taskManager.getAllTasks());
        System.out.println("Epics: " + taskManager.getAllEpics());
        System.out.println("Subtasks: " + taskManager.getAllSubtasks());

        task1.setStatus(tasks.Status.DONE);
        taskManager.updateTask(task1);
        subtask1.setStatus(tasks.Status.DONE);
        taskManager.updateSubtask(subtask1);
        subtask2.setStatus(tasks.Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask2);
        subtask3.setStatus(tasks.Status.DONE);
        taskManager.updateSubtask(subtask3);

        System.out.println("Tasks: " + taskManager.getAllTasks());
        System.out.println("Epics: " + taskManager.getAllEpics());
        System.out.println("Subtasks: " + taskManager.getAllSubtasks());

        taskManager.deleteTask(task1.getId());
        taskManager.deleteEpic(epic1.getId());
        taskManager.deleteSubtask(subtask3.getId());

        System.out.println("Tasks: " + taskManager.getAllTasks());
        System.out.println("Epics: " + taskManager.getAllEpics());
        System.out.println("Subtasks: " + taskManager.getAllSubtasks());
*/

    }
}
