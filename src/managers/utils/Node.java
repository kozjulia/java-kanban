package managers.utils;

public class Node<Task> { //T extends Task> { // узел связного списка CustomLinkedList
    public Task data;
    public Node<Task> next;
    public Node<Task> prev;

    public Node(Node<Task> prev, Task data, Node<Task> next) {
        this.data = data;
        this.next = next;
        this.prev = prev;
    }
}