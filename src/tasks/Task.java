package tasks;

import manager.TaskType;

import java.util.Objects;

public class Task {
    private Integer id;
    private String title;
    private String description;
    private Status status;
    private TaskType taskType;

    public TaskType getTaskType() {
        return taskType;
    }

    public Task(String title, String description, Status status) {
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public Task(int id, Status status, String title, String description, TaskType type) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.id = id;
        this.taskType = type;
    }

    public Task(Task task) {
        this.id = task.getId();
        this.status = task.getStatus();
        this.description = task.getDescription();
        this.title = task.getTitle();
        this.taskType = task.getTaskType();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "tasks.Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;

        Task task = (Task) object;
        return Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}

