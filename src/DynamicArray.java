public final class DynamicArray implements IntSequence {
    private int[] elements = new int[8];
    private int size;
    private final Metrics metrics = new Metrics();

    public void add(int value) {
        add(size, value);
    }

    public void add(int index, int value) {
        checkIndex(index, true);
        ensureCapacity();
        for (int j = size; j > index; j--) {
            elements[j] = elements[j - 1];
            metrics.access();
            metrics.move();
        }
        elements[index] = value;
        size++;
    }

    public int remove(int index) {
        checkIndex(index, false);
        int removed = elements[index];
        metrics.access();
        for (int j = index; j < size - 1; j++) {
            elements[j] = elements[j + 1];
            metrics.access();
            metrics.move();
        }
        elements[--size] = 0;
        return removed;
    }

    public int get(int index) {
        checkIndex(index, false);
        metrics.access();
        return elements[index];
    }

    public boolean contains(int value) {
        for (int i = 0; i < size; i++) {
            metrics.access();
            metrics.compare();
            if (elements[i] == value) {
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

    private void ensureCapacity() {
        if (size < elements.length) {
            return;
        }
        int[] expanded = new int[Math.multiplyExact(elements.length, 2)];
        for (int i = 0; i < size; i++) {
            expanded[i] = elements[i];
            metrics.access();
            metrics.move();
        }
        elements = expanded;
    }

    private void checkIndex(int index, boolean insertion) {
        if (index < 0 || index > size || (!insertion && index == size)) {
            throw new IndexOutOfBoundsException("index=" + index + ", size=" + size);
        }
    }
}
