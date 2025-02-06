package manager;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private Node<Task> tail;
    private Node<Task> head;

    private final HashMap<Integer, Node<Task>> historyMap = new HashMap<>();

    private void linkLast(Task task) {
        Node<Task> newNode = new Node<>(task);
        if (tail == null) {
            tail = newNode;
            head = newNode;
        } else {
            tail.setNext(newNode);
            newNode.setPrev(tail);
            tail = newNode;
        }
    }

    private void removeNode(Node<Task> node) {
        if (node == null) return;
        if (node.getPrev() == null) {
            head = node.getNext();
            if (head != null) {
                head.setPrev(null);
            } else {
                tail = null;
            }
            return;
        }
        if (node.getNext() == null) {
            tail = node.getPrev();
            if (tail != null) {
                tail.setNext(null);
            } else {
                head = null;
            }
            return;
        }
        node.getPrev().setNext(node.getNext());
        node.getNext().setPrev(node.getPrev());
    }

    private ArrayList<Task> getTasks() {
        ArrayList<Task> tasksList = new ArrayList<>();
        Node<Task> node = head;
        while (node != null) {
            tasksList.add(node.getTask());
            node = node.getNext();
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
