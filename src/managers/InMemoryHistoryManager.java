package managers;

import tasks.Task;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    // история просмотров задач

    // история просмотра задач без повторных просмотров в ней и ограничения на размер истории
    public CustomLinkedList<Task> historyLinkedList = new CustomLinkedList<>();
    // мапа, где ключ = id задач, а значение — узел связного списка
    public Map<Integer, Node> nodeMap = new HashMap<>();


    @Override
    public List<Task> getHistory() {
        // передаем историю, начав с последней просмотренной задачи
        return historyLinkedList.getTasks();
    }

    @Override
    public void add(Task task) {  // добавить задачу в историю
        int idCur = task.getId();
        if (nodeMap.get(idCur) != null) {
            remove(idCur);
        }
        nodeMap.put(idCur, historyLinkedList.linkLast(task));
    }

    @Override
    public void remove(int id) {  // удалить задачу из истории
        historyLinkedList.removeNode(nodeMap.get(id));
    }

    public class CustomLinkedList<Task> {  // связный список для хранения истории
        Node<Task> head;
        Node<Task> tail;
        private int size = 0;

        public int size() {
            return this.size;
        }

        // добавляет задачу в конец списка
        public Node linkLast(Task task) {
            final Node<Task> oldTail = tail;
            final Node<Task> newNode = new Node<>(oldTail, task, null);
            tail = newNode;
            if (oldTail == null)
                head = newNode;
            else
                oldTail.next = newNode;
            size++;
            return newNode;
        }

        // вырезает узел
        public void removeNode(Node node) {
            if ((node == null) || (head == null) || (tail == null)) {
                return;
            }
            if (node == head) {
                head = node.next;
            }
            if (node == tail) {
                tail = node.prev;
            }
            if (node.prev != null) {
                node.prev.next = node.next;
            }
            if (node.next != null) {
                node.next.prev = node.prev;
            }
            size--;
        }

        // собирает все задачи из двусвязного списка в обычный ArrayList
        public List<Task> getTasks() {
            List<Task> historyList = new ArrayList<>();
            if (historyLinkedList.size == 0) {
                return null;
            }
            Node node = historyLinkedList.tail;
            int index = historyLinkedList.size();
            while ((node != null) && (index > 0)) {
                historyList.add((Task) node.data);
                node = node.prev;
                index--;
            }
            return historyList;
        }
    }
}