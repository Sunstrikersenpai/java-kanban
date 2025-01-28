package manager;

public class Node<T> {

    public Node<T> next;
    public Node<T> prev;
    public T task;

    public Node(T task) {
        this.task = task;
        this.next = null;
        this.prev = null;
    }

}
