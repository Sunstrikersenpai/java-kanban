package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.List;
import java.util.Optional;
import java.util.TreeSet;

public interface TaskManager {
    Task addTask(Task task);

    Epic addEpic(Epic epic);

    Subtask addSubtask(Subtask subtask);

    List<Task> getAllTasks();

    List<Epic> getAllEpics();

    List<Subtask> getAllSubtasks();

    void clearAllTasks();

    void clearAllEpics();

    void clearAllSubtasks();

    Optional<Task> getTaskById(int id);

    Optional<Epic> getEpicById(int id);

    Optional<Subtask> getSubtaskById(int id);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    void deleteTask(int id);

    void deleteEpic(int id);

    void deleteSubtask(int id);

    List<Subtask> getSubtasksOfEpic(int epicId);

    List<Task> getHistory();

    TreeSet<Task> getPrioritizedTasks();

}
