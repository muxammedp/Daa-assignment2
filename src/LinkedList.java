public final class LinkedList implements IntSequence {
    private static final class Node {
        private final int value;
        private Node next;

        private Node(int value, Node next) {
            this.value = value;
            this.next = next;
        }
    }

    private Node head;
    private Node tail;
    private int size;
    private final Metrics metrics = new Metrics();

    public void add(int value) {
        Node node = new Node(value, null);
        if (tail == null) {
            head = node;
        } else {
            metrics.access();
            tail.next = node;
        }
        tail = node;
        size++;
    }

    public void add(int index, int value) {
        checkIndex(index, true);
        if (index == size) {
            add(value);
            return;
        }
        if (index == 0) {
            head = new Node(value, head);
        } else {
            Node previous = nodeAt(index - 1);
            previous.next = new Node(value, previous.next);
        }
        size++;
    }

    public int remove(int index) {
        checkIndex(index, false);
        Node removed;
        if (index == 0) {
            removed = head;
            metrics.access();
            head = head.next;
        } else {
            Node previous = nodeAt(index - 1);
            removed = previous.next;
            metrics.access();
            previous.next = removed.next;
            if (removed == tail) {
                tail = previous;
            }
        }
        size--;
        if (size == 0) {
            tail = null;
        }
        return removed.value;
    }

    public int get(int index) {
        checkIndex(index, false);
        return nodeAt(index).value;
    }

    public boolean contains(int value) {
        for (Node node = head; node != null; node = node.next) {
            metrics.access();
            metrics.compare();
            if (node.value == value) {
                return true;
            }
        }
        return false;
    }

    public int size() {
        return size;
    }

    public Metrics metrics() {
        return metrics;
    }

    private Node nodeAt(int index) {
        Node node = head;
        metrics.access();
        for (int i = 0; i < index; i++) {
            node = node.next;
            metrics.access();
        }
        return node;
    }

    private void checkIndex(int index, boolean insertion) {
        if (index < 0 || index > size || (!insertion && index == size)) {
            throw new IndexOutOfBoundsException("index=" + index + ", size=" + size);
        }
    }
}
