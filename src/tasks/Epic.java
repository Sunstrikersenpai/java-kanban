package tasks;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {

    private final ArrayList<Integer> subtaskIds = new ArrayList<>();
    private LocalDateTime endTime = null;

    public Epic(String title, String description) {
        super(title, description, Status.NEW);
    }

    public Epic(int id, Status status, String title, String description) {
        super(id, status, title, description);
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.EPIC;
    }

    public void addSubtask(int subtaskId) {
        if (this.getId() == subtaskId) {
            System.out.println("cannotAddEpicAsSubtaskToItself");
            return;
        }
        subtaskIds.add(subtaskId);
    }

    @Override
    public LocalDateTime getEndTime() {
        return this.endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public ArrayList<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void removeSubtask(int subtaskId) {
        subtaskIds.remove(Integer.valueOf(subtaskId));
    }

    public void clearSubtasks() {
        subtaskIds.clear();
    }

    @Override
    public String toString() {
        return "tasks.Epic{" +
                "subtaskIds=" + subtaskIds +
                ", " + super.toString() +
                '}';
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;

        Epic epic = (Epic) object;
        return this.subtaskIds.equals(epic.subtaskIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtaskIds);
    }

}