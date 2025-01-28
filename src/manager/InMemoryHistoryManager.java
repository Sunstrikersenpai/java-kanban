package manager;


import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    Node<Task> tail;
    Node<Task> head;

    HashMap<Integer, Node<Task>> historyMap = new HashMap<>();

    private void linkLast(Task task) {
        Node<Task> newNode = new Node<>(task);
        if (tail == null) {
            tail = newNode;
            head = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }
    }

    private void removeNode(Node<Task> node) {
        if (node == null) return;
        if (node.prev == null) {
            head = node.next;
            if (head != null) {
                head.prev = null;
            } else {
                tail = null;
            }
            return;
        }
        if (node.next == null) {
            tail = node.prev;
            if (tail != null) {
                tail.next = null;
            } else {
                head = null;
            }
            return;
        }
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    private ArrayList<Task> getTasks() {
        ArrayList<Task> tasksList = new ArrayList<>();
        Node<Task> node = head;
        while (node != null) {
            tasksList.add(node.task);
            node = node.next;
        }
        return tasksList;
    }


    @Override
    public void add(Task task) {
        if (historyMap.containsKey(task.getId())) {
            Node<Task> oldNode = historyMap.get(task.getId());
            removeNode(oldNode);
        }
        linkLast(task);
        historyMap.put(task.getId(), tail);
    }

    @Override
    public void remove(int id) {
        if (historyMap.containsKey(id)) {
            removeNode(historyMap.get(id));
            historyMap.remove(id);
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }
}
