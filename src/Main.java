public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        Task task1 = new Task("Task 1", "Task 1", Status.NEW);
        Task task2 = new Task("Task 2", "Task 2", Status.NEW);
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        Epic epic1 = new Epic("Epic 1", "Epic 1");
        taskManager.addEpic(epic1);
        Subtask subtask1 = new Subtask("Subtask 1", "Subtask 1", Status.NEW, epic1.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Subtask 2", Status.NEW, epic1.getId());
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        Epic epic2 = new Epic("Epic 2", "Epic 2");
        taskManager.addEpic(epic2);
        Subtask subtask3 = new Subtask("Subtask 3", "Subtask 3", Status.NEW, epic2.getId());
        taskManager.addSubtask(subtask3);

        System.out.println("Tasks: " + taskManager.getAllTasks());
        System.out.println("Epics: " + taskManager.getAllEpics());
        System.out.println("Subtasks: " + taskManager.getAllSubtasks());

        task1.setStatus(Status.DONE);
        taskManager.updateTask(task1);
        subtask1.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1);
        subtask2.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask2);
        subtask3.setStatus(Status.DONE);
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

        taskManager.clearAll();
    }
}
