package manager;

import tasks.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    private final int HISTORY_SIZE = 10;
    private final ArrayList<Task> tasksHistory = new ArrayList<>();

    @Override
    public ArrayList<Task> getHistory() {
        return tasksHistory;
    }

    @Override
    public void add(Task task) {
        if(tasksHistory.size() == HISTORY_SIZE) {
            tasksHistory.removeFirst();
        }
        tasksHistory.add(new Task(task));
    }
}
