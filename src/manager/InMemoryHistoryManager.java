package manager;

import tasks.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    private final ArrayList<Task> tasksHistory = new ArrayList<>();

    @Override
    public ArrayList<Task> getHistory() {
        return tasksHistory;
    }

    @Override
    public void add(Task task) {
        if(tasksHistory.size() == 10) {
            tasksHistory.removeFirst();
        }
        tasksHistory.add(new Task(task));
    }
}
