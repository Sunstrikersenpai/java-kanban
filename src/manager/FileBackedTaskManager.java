package manager;

import exceptions.ManagerSaveException;
import tasks.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final Path file;

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

    protected void save() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(this.file.toFile(), StandardCharsets.UTF_8))) {
            bw.write("id,type,name,status,description,startTime,duration,epicId\n");
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
            String line;
            List<Subtask> list = new ArrayList<>();

            while ((line = br.readLine()) != null) {
                Task task = taskFromString(line);
                switch (task.getTaskType()) {
                    case TASK -> fbm.loadTask(task);
                    case EPIC -> fbm.loadTask((Epic) task);
                    case SUBTASK -> list.add((Subtask) task);
                }
            }
            for (Subtask subtask : list) {
                fbm.loadTask(subtask);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки из файла", e);
        }
        return fbm;
    }

    private void loadTask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        addToSortedTasks(subtask);
        epics.get(subtask.getEpicId()).addSubtask(subtask.getId());
    }

    private void loadTask(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    private void loadTask(Task task) {
        tasks.put(task.getId(), task);
        addToSortedTasks(task);
    }

    @Override
    public Task addTask(Task task) {
        super.addTask(task);
        save();
        return task;
    }

    @Override
    public Epic addEpic(Epic epic) {
        super.addEpic(epic);
        save();
        return epic;
    }

    @Override
    public Subtask addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
        return subtask;
    }

    protected static Task taskFromString(String value) {
        String[] ar = value.split(",");
        int id = Integer.parseInt(ar[0]);
        TaskType type = TaskType.valueOf(ar[1]);
        Status status = Status.valueOf(ar[2]);
        String title = ar[3];
        String description = ar[4];
        String startTime = ar[5];
        String duration = ar[6];
        if (type == TaskType.SUBTASK) {
            return setTimeAndReturnTask(new Subtask(id, status, title, description, Integer.parseInt(ar[7])), startTime, duration);
        }
        if (type == TaskType.EPIC) {
            if (startTime.equals("null") || duration.equals("null")) {
                return new Epic(id, status, title, description);
            }
            return setTimeAndReturnTask(new Epic(id, status, title, description), startTime, duration);
        }
        if (startTime.equals("null") || duration.equals("null")) {
            return new Task(id, status, title, description);
        }
        return setTimeAndReturnTask(new Task(id, status, title, description), startTime, duration);
    }

    protected static String taskToString(Task task) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        String startTime = Optional.ofNullable(task.getStartTime()).map(t -> t.format(formatter)).orElse("null");
        String duration = Optional.ofNullable(task.getDuration()).map(Duration::toString).orElse("null");

        StringBuilder toReturn = new StringBuilder(String.join(",", String.valueOf(task.getId()), task.getTaskType().toString(), task.getStatus().toString(), task.getTitle(), task.getDescription(), startTime, duration));
        if (task.getTaskType() == TaskType.SUBTASK) {
            toReturn.append(",").append(((Subtask) task).getEpicId());
        }
        return toReturn.toString();
    }

    private static Task setTimeAndReturnTask(Task task, String startTime, String duration) {
        task.setStartTime(LocalDateTime.parse(startTime));
        task.setDuration(Duration.parse(duration));
        return task;
    }
}