package manager;

public class Node<T> {

    private Node<T> next;
    private Node<T> prev;
    private T task;

    public Node(T task) {
        this.task = task;
        this.next = null;
        this.prev = null;
    }

    public Node<T> getNext() {
        return next;
    }

    public void setNext(Node<T> next) {
        this.next = next;
    }

    public Node<T> getPrev() {
        return prev;
    }

    public void setPrev(Node<T> prev) {
        this.prev = prev;
    }

    public T getTask() {
        return task;
    }

    public void setTask(T task) {
        this.task = task;
    }
}
