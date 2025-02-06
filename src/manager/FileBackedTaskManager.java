package manager;

import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    Path file;

    public FileBackedTaskManager(Path file) {
        this.file = file;

        try {
            if (!Files.exists(file)) {
                Files.createFile(file);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка конструктора", e);
        }
    }

    //
    public void save() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(this.file.toFile(), StandardCharsets.UTF_8))) {
            bw.write("id,type,name,status,description,epicId\n");
            for (Task task : getAllTasks()) {
                bw.write(taskToString(task) + "\n");
            }
            for (Epic epic : getAllEpics()) {
                bw.write(taskToString(epic) + "\n");
            }
            for (Subtask subtask : getAllSubtasks()) {
                bw.write(taskToString(subtask) + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения задач в файл", e);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fbm = new FileBackedTaskManager(file.toPath());

        if (file.length() == 0) {
            System.out.println("Файл пуст");
            return fbm;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            br.readLine();
            while (br.ready()) {
                Task task = taskFromString(br.readLine());
                fbm.loadTask(task);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки из файла", e);
        }
        return fbm;
    }

    private void loadTask(Task task) {
        if (task instanceof Epic epic) {
            super.addEpic(epic);
            epic.setTaskType(TaskType.EPIC);
        } else if (task instanceof Subtask subtask) {
            super.addSubtask(subtask);
            subtask.setTaskType(TaskType.SUBTASK);
        } else {
            super.addTask(task);
            task.setTaskType(TaskType.TASK);
        }
    }

    @Override
    public Task addTask(Task task) {
        super.addTask(task);
        task.setTaskType(TaskType.TASK);
        save();
        return task;
    }

    @Override
    public Epic addEpic(Epic epic) {
        super.addEpic(epic);
        epic.setTaskType(TaskType.EPIC);
        save();
        return epic;
    }

    @Override
    public Subtask addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        subtask.setTaskType(TaskType.SUBTASK);
        save();
        return subtask;
    }

    public static Task taskFromString(String value) {
        String[] ar = value.split(",");
        int id = Integer.parseInt(ar[0]);
        TaskType type = TaskType.valueOf(ar[1]);
        Status status = Status.valueOf(ar[2]);
        String title = ar[3];
        String description = ar[4];
        if (type == TaskType.SUBTASK) {
            return new Subtask(id, type, status, title, description, Integer.parseInt(ar[5]));
        }
        if (type == TaskType.EPIC) {
            return new Epic(id, type, status, title, description);
        }

        return new Task(id, type, status, title, description);
    }

    private static String taskToString(Task task) {
        StringBuilder toReturn = new StringBuilder(String.join(",", String.valueOf(task.getId()), task.getTaskType().toString(), task.getStatus().toString(), task.getTitle(), task.getDescription()));
        if (task instanceof Subtask) {
            toReturn.append(",").append(((Subtask) task).getEpicId());
        }
        return toReturn.toString();
    }

    public static void main(String[] args) {
        Path filePath = Paths.get("java-kanban\\data\\data.csv");
        /*FileBackedTaskManager manager = new FileBackedTaskManager(filePath);

        Task task1 = new Task(1, TaskType.TASK, Status.NEW, "Task 1", "Description 1");
        Task task2 = new Task(2, TaskType.TASK, Status.NEW, "Task 2", "Description 2");
        manager.addTask(task1);
        manager.addTask(task2);

        Epic epic1 = new Epic("Epic 1", "Epic description");
        manager.addEpic(epic1);

        Subtask subtask1 = new Subtask("Subtask 1", "Subtask1 description", Status.NEW, epic1.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Subtask2 description", Status.IN_PROGRESS, epic1.getId());
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);*/

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(filePath.toFile());

        for (Task task : loadedManager.getAllTasks()) {
            System.out.println(task);
        }
        for (Epic epic : loadedManager.getAllEpics()) {
            System.out.println(epic);
        }
        for (Subtask subtask : loadedManager.getAllSubtasks()) {
            System.out.println(subtask);
        }
    }
}
