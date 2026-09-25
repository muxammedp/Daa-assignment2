import java.util.NoSuchElementException;

public final class MinHeap {
    private int[] elements = new int[8];
    private int size;
    private final Metrics metrics = new Metrics();

    public void insert(int value) {
        ensureCapacity();
        int index = size++;
        elements[index] = value;
        while (index > 0) {
            int parent = (index - 1) / 2;
            metrics.compare();
            if (elements[parent] <= elements[index]) {
                break;
            }
            swap(parent, index);
            index = parent;
        }
    }

    public int peekMin() {
        requireNonEmpty();
        return elements[0];
    }

    public int extractMin() {
        requireNonEmpty();
        int minimum = elements[0];
        elements[0] = elements[--size];
        elements[size] = 0;
        int index = 0;
        while (index < size / 2) {
            int left = 2 * index + 1;
            int right = left + 1;
            int smaller = left;
            if (right < size) {
                metrics.compare();
                if (elements[right] < elements[left]) {
                    smaller = right;
                }
            }
            metrics.compare();
            if (elements[index] <= elements[smaller]) {
                break;
            }
            swap(index, smaller);
            index = smaller;
        }
        return minimum;
    }

    public int size() {
        return size;
    }

    public Metrics metrics() {
        return metrics;
    }

    public boolean isValidHeap() {
        for (int child = 1; child < size; child++) {
            if (elements[(child - 1) / 2] > elements[child]) {
                return false;
            }
        }
        return true;
    }

    private void ensureCapacity() {
        if (size < elements.length) {
            return;
        }
        int[] expanded = new int[Math.multiplyExact(elements.length, 2)];
        for (int i = 0; i < size; i++) {
            expanded[i] = elements[i];
        }
        elements = expanded;
    }

    private void swap(int first, int second) {
        int value = elements[first];
        elements[first] = elements[second];
        elements[second] = value;
    }

    private void requireNonEmpty() {
        if (size == 0) {
            throw new NoSuchElementException("Heap is empty");
        }
    }
}
