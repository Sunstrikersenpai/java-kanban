package manager;

import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HistoryManager historyManager = new InMemoryHistoryManager();
    protected TreeSet<Task> sortedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    protected Map<LocalDateTime, Integer> busySlots = new HashMap<>();
    private int id = 1;

    protected boolean isValid(Task task) {
        if (task.getDuration() == null || task.getStartTime() == null) {
            return true;
        }

        for (int i = 0; i < task.getDuration().toMinutes(); i += 15) {
            LocalDateTime slot = task.getStartTime().plusMinutes(i);

            if (isSlotBusy(slot, task.getId())) {
                return false;
            }
        }
        return true;
    }

    private boolean isSlotBusy(LocalDateTime localDateTime, int id) {
        Integer idInSlot = busySlots.get(localDateTime);
        return idInSlot != null && !idInSlot.equals(id);
    }

    @Override
    public Task addTask(Task task) {
        task.setId(id++);

        if (!isValid(task)) {
            id--;
            throw new IllegalArgumentException("Время задачи пересекается с другими");
        }

        tasks.put(task.getId(), task);

        if (task.getStartTime() != null && task.getDuration() != null) {
            addToSortedTasks(task);
        }
        return task;
    }

    @Override
    public Epic addEpic(Epic epic) {
        epic.setId(id++);
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public Subtask addSubtask(Subtask subtask) {
        if (!epics.containsKey(subtask.getEpicId())) {
            System.out.println("Epic not found");
            return null;
        }

        subtask.setId(id++);

        if (!isValid(subtask)) {
            id--;
            throw new IllegalArgumentException("Время задачи пересекается с другими");
        }

        subtasks.put(subtask.getId(), subtask);
        epics.get(subtask.getEpicId()).addSubtask(subtask.getId());
        updateEpicTimeAndStatus(subtask.getEpicId());

        if (subtask.getStartTime() != null && subtask.getDuration() != null) {
            addToSortedTasks(subtask);
        }

        return subtask;
    }

    protected void addToSortedTasks(Task task) {
        for (int i = 0; i < task.getDuration().toMinutes(); i += 15) {
            LocalDateTime slot = task.getStartTime().plusMinutes(i);
            busySlots.put(slot, task.getId());
        }
        sortedTasks.add(task);
    }

    protected void deleteFromSortedTasks(Task task) {
        for (int i = 0; i < task.getDuration().toMinutes(); i += 15) {
            LocalDateTime slot = task.getStartTime().plusMinutes(i);
            busySlots.remove(slot);
        }
        sortedTasks.remove(task);
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void clearAllTasks() {
        for (Task task : tasks.values()) {
            deleteFromSortedTasks(task);
        }
        tasks.clear();
    }

    @Override
    public void clearAllEpics() {
        clearAllSubtasks();
        epics.clear();
    }

    @Override
    public void clearAllSubtasks() {
        for (Subtask subtask : subtasks.values()) {
            deleteFromSortedTasks(subtask);
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubtasks();
            updateEpicTimeAndStatus(epic.getId());
        }
    }

    @Override
    public Optional<Task> getTaskById(int id) {
        return Optional.ofNullable(tasks.get(id)).map(task -> {
            historyManager.add(task);
            return task;
        });

    }

    @Override
    public Optional<Epic> getEpicById(int id) {
        return Optional.ofNullable(epics.get(id)).map(epic -> {
            historyManager.add(epic);
            return epic;
        });
    }

    @Override
    public Optional<Subtask> getSubtaskById(int id) {
        return Optional.ofNullable(subtasks.get(id)).map(subtask -> {
            historyManager.add(subtask);
            return subtask;
        });
    }

    @Override
    public void updateTask(Task task) {
        if (!tasks.containsKey(task.getId())) {
            return;
        }
        if (!isValid(task)) {
            throw new IllegalArgumentException("Время задачи пересекается с другими");
        }

        tasks.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        if (!epics.containsKey(epic.getId())) {
            return;
        }
        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (!subtasks.containsKey(subtask.getId())) {
            return;
        }
        if (!isValid(subtask)) {
            throw new IllegalArgumentException("Время задачи пересекается с другими");
        }
        subtasks.put(subtask.getId(), subtask);
        updateEpicTimeAndStatus(subtask.getEpicId());
    }

    @Override
    public void deleteTask(int id) {
        Task task = tasks.remove(id);
        if (task != null && task.getStartTime() != null) {
            deleteFromSortedTasks(task);
        }
    }

    @Override
    public void deleteEpic(int id) {
        Epic epic = epics.remove(id);
        if (epic == null) return;

        epic.getSubtaskIds().forEach(subId -> {
            Subtask subtask = subtasks.remove(subId);
            if (subtask != null) {
                deleteFromSortedTasks(subtask);
            }
        });
    }

    @Override
    public void deleteSubtask(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask == null) {
            System.out.println("tasks.Subtask not found");
            return;
        }
        Epic epic = epics.get(subtask.getEpicId());
        if (epic == null) {
            System.out.println("tasks.Epic not found");
            return;
        }

        deleteFromSortedTasks(subtask);
        epic.removeSubtask(id);
        updateEpicTimeAndStatus(subtask.getEpicId());
    }

    @Override
    public ArrayList<Subtask> getSubtasksOfEpic(int epicId) {
        return (ArrayList<Subtask>) epics.get(epicId).getSubtaskIds().stream().map(subtasks::get).collect(Collectors.toList());
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public TreeSet<Task> getPrioritizedTasks() {
        return sortedTasks;
    }

    private void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        boolean allNew = epic.getSubtaskIds().stream().map(subtasks::get).allMatch(subtask -> subtask.getStatus() == Status.NEW);
        boolean allDone = epic.getSubtaskIds().stream().map(subtasks::get).allMatch(subtask -> subtask.getStatus() == Status.DONE);

        if (allNew) {
            epic.setStatus(Status.NEW);
        } else if (allDone) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    private void updateEpicStartTime(int epicId) {
        Epic epic = epics.get(epicId);
        LocalDateTime earliestSubtaskStartTime = epic.getSubtaskIds().stream().map(subtasks::get).map(Task::getStartTime).min(LocalDateTime::compareTo).orElse(null);
        epic.setStartTime(earliestSubtaskStartTime);
    }

    private void updateEpicEndTime(int epicId) {
        Epic epic = epics.get(epicId);
        LocalDateTime latestSubtaskEndTime = epic.getSubtaskIds().stream().map(subtasks::get).map(Subtask::getEndTime).max(LocalDateTime::compareTo).orElse(null);
        epic.setEndTime(latestSubtaskEndTime);
    }

    private void updateEpicDuration(int epicId) {
        Epic epic = epics.get(epicId);
        Duration epicDuration = epic.getSubtaskIds().stream().map(subtasks::get).map(Subtask::getDuration).reduce(Duration.ZERO, Duration::plus);
        epic.setDuration(epicDuration);
    }

    private void updateEpicTimeAndStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic.getSubtaskIds().isEmpty()) {
            epic.setStartTime(null);
            epic.setEndTime(null);
            epic.setDuration(Duration.ZERO);
            epic.setStatus(Status.NEW);
            return;
        }
        updateEpicStatus(epicId);
        updateEpicStartTime(epicId);
        updateEpicEndTime(epicId);
        updateEpicDuration(epicId);
    }
}

