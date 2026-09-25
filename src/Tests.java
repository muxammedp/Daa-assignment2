import java.util.ArrayList;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.function.Supplier;

public final class Tests {
    private static long assertions;

    public static void main(String[] args) {
        testSequence(DynamicArray::new);
        testSequence(LinkedList::new);
        testMetrics();
        testHeap();
        System.out.println("PASS: " + assertions + " assertions");
    }

    private static void testSequence(Supplier<IntSequence> factory) {
        IntSequence sequence = factory.get();
        equal(0, sequence.size());
        check(!sequence.contains(0));
        expect(IndexOutOfBoundsException.class, () -> sequence.get(0));
        expect(IndexOutOfBoundsException.class, () -> sequence.remove(0));
        expect(IndexOutOfBoundsException.class, () -> sequence.add(1, 1));
        sequence.add(7);
        equal(7, sequence.get(0));
        check(sequence.contains(7));
        equal(7, sequence.remove(0));
        sequence.add(0, Integer.MIN_VALUE);
        sequence.add(sequence.size(), Integer.MAX_VALUE);
        sequence.add(1, 7);
        sequence.add(1, 7);
        assertSequence(sequence, Arrays.asList(Integer.MIN_VALUE, 7, 7, Integer.MAX_VALUE));
        expect(IndexOutOfBoundsException.class, () -> sequence.get(-1));
        expect(IndexOutOfBoundsException.class, () -> sequence.get(sequence.size()));
        expect(IndexOutOfBoundsException.class, () -> sequence.remove(-1));
        expect(IndexOutOfBoundsException.class, () -> sequence.remove(sequence.size()));
        expect(IndexOutOfBoundsException.class, () -> sequence.add(-1, 0));
        expect(IndexOutOfBoundsException.class, () -> sequence.add(sequence.size() + 1, 0));
        assertSequence(sequence, Arrays.asList(Integer.MIN_VALUE, 7, 7, Integer.MAX_VALUE));
        while (sequence.size() > 0) {
            sequence.remove(sequence.size() - 1);
        }
        sequence.add(99);
        equal(99, sequence.remove(0));

        ArrayList<Integer> reference = new ArrayList<>();
        Random random = new Random(42);
        for (int step = 0; step < 20000; step++) {
            int value = random.nextInt(101) - 50;
            int operation = random.nextInt(5);
            if (operation == 0 || reference.isEmpty()) {
                sequence.add(value);
                reference.add(value);
            } else if (operation == 1) {
                int index = random.nextInt(reference.size() + 1);
                sequence.add(index, value);
                reference.add(index, value);
            } else if (operation == 2) {
                int index = random.nextInt(reference.size());
                equal(reference.remove(index), sequence.remove(index));
            } else if (operation == 3) {
                int index = random.nextInt(reference.size());
                equal(reference.get(index), sequence.get(index));
            } else {
                check(reference.contains(value) == sequence.contains(value));
            }
            equal(reference.size(), sequence.size());
            if (step % 500 == 0) {
                assertSequence(sequence, reference);
            }
        }
        assertSequence(sequence, reference);

        IntSequence large = factory.get();
        for (int i = 0; i < 100000; i++) {
            large.add(i);
        }
        equal(100000, large.size());
        equal(0, large.get(0));
        equal(50000, large.get(50000));
        equal(99999, large.get(99999));
        check(large.contains(99999));
        check(!large.contains(-1));
        large.add(50000, -1);
        equal(-1, large.remove(50000));
        equal(99999, large.remove(99999));
        equal(0, large.remove(0));
        equal(99998, large.size());
    }

    private static void testMetrics() {
        DynamicArray array = new DynamicArray();
        LinkedList list = new LinkedList();
        for (int i = 0; i < 8; i++) {
            array.add(i);
            list.add(i);
        }
        array.metrics().reset();
        array.add(0, -1);
        equal(16, array.metrics().movements());
        equal(16, array.metrics().accesses());
        array.metrics().reset();
        equal(-1, array.remove(0));
        equal(8, array.metrics().movements());
        equal(9, array.metrics().accesses());
        array.metrics().reset();
        array.get(7);
        equal(1, array.metrics().accesses());
        list.metrics().reset();
        list.get(7);
        equal(8, list.metrics().accesses());
        list.metrics().reset();
        list.add(0, -1);
        equal(0, list.metrics().accesses());
        list.remove(0);
        equal(1, list.metrics().accesses());
        list.metrics().reset();
        list.add(4, -1);
        equal(4, list.metrics().accesses());
        list.metrics().reset();
        list.remove(4);
        equal(5, list.metrics().accesses());
        array.metrics().reset();
        list.metrics().reset();
        check(!array.contains(100));
        check(!list.contains(100));
        equal(8, array.metrics().comparisons());
        equal(8, list.metrics().comparisons());
        MinHeap heap = new MinHeap();
        heap.insert(3);
        heap.insert(2);
        heap.insert(1);
        equal(2, heap.metrics().comparisons());
        heap.metrics().reset();
        equal(1, heap.extractMin());
        equal(1, heap.metrics().comparisons());
    }

    private static void testHeap() {
        MinHeap heap = new MinHeap();
        check(heap.isValidHeap());
        expect(NoSuchElementException.class, heap::peekMin);
        expect(NoSuchElementException.class, heap::extractMin);
        heap.insert(7);
        equal(7, heap.peekMin());
        equal(7, heap.extractMin());
        equal(0, heap.size());
        PriorityQueue<Integer> reference = new PriorityQueue<>();
        int[] boundaries = {Integer.MAX_VALUE, 0, -1, Integer.MIN_VALUE, 0, Integer.MIN_VALUE};
        for (int value : boundaries) {
            heap.insert(value);
            reference.add(value);
            check(heap.isValidHeap());
            equal(reference.peek(), heap.peekMin());
        }
        while (!reference.isEmpty()) {
            equal(reference.remove(), heap.extractMin());
            check(heap.isValidHeap());
        }
        Random random = new Random(42);
        for (int i = 0; i < 20000; i++) {
            if (reference.isEmpty() || random.nextBoolean()) {
                int value = random.nextInt(101) - 50;
                heap.insert(value);
                reference.add(value);
            } else {
                equal(reference.remove(), heap.extractMin());
            }
            equal(reference.size(), heap.size());
            check(heap.isValidHeap());
            if (!reference.isEmpty()) {
                equal(reference.peek(), heap.peekMin());
            }
        }
        while (!reference.isEmpty()) {
            equal(reference.remove(), heap.extractMin());
            check(heap.isValidHeap());
        }
        int[] values = new int[100000];
        for (int i = 0; i < values.length; i++) {
            values[i] = random.nextInt();
            heap.insert(values[i]);
        }
        check(heap.isValidHeap());
        Arrays.sort(values);
        for (int i = 0; i < values.length; i++) {
            equal(values[i], heap.extractMin());
            if (i % 1000 == 0) {
                check(heap.isValidHeap());
            }
        }
        check(heap.isValidHeap());
        expect(NoSuchElementException.class, heap::extractMin);
        for (int order = 0; order < 3; order++) {
            for (int i = 0; i < 1000; i++) {
                heap.insert(order == 0 ? i : order == 1 ? 1000 - i : 5);
                check(heap.isValidHeap());
            }
            int previous = Integer.MIN_VALUE;
            while (heap.size() > 0) {
                int next = heap.extractMin();
                check(previous <= next);
                previous = next;
                check(heap.isValidHeap());
            }
        }
    }

    private static void assertSequence(IntSequence actual, java.util.List<Integer> expected) {
        equal(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            equal(expected.get(i), actual.get(i));
        }
    }

    private static void equal(long expected, long actual) {
        assertions++;
        if (expected != actual) {
            throw new AssertionError("Expected " + expected + ", got " + actual);
        }
    }

    private static void check(boolean condition) {
        assertions++;
        if (!condition) {
            throw new AssertionError("Condition failed");
        }
    }

    private static void expect(Class<? extends Throwable> type, Runnable operation) {
        assertions++;
        try {
            operation.run();
        } catch (Throwable error) {
            if (type.isInstance(error)) {
                return;
            }
            throw new AssertionError("Unexpected exception", error);
        }
        throw new AssertionError("Expected " + type.getSimpleName());
    }
}
