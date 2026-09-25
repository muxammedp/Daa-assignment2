import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Arrays;
import java.util.Random;

public final class Benchmark {
    private static final int[] SIZES = {100, 1000, 10000, 100000};
    private static final int REPETITIONS = 5;
    private static final int WARMUPS = 2;
    private static volatile long sink;

    private static final class Input {
        private final int[] values;
        private final int[] indices = new int[10000];
        private final int[] searches = new int[1000];
        private final int[] insertions = new int[1000];

        private Input(int n) {
            Random random = new Random(42);
            values = new int[n];
            for (int i = 0; i < n; i++) {
                values[i] = 2 * random.nextInt(1000000);
            }
            for (int i = 0; i < indices.length; i++) {
                indices[i] = random.nextInt(n);
            }
            for (int i = 0; i < searches.length; i++) {
                searches[i] = i % 2 == 0 ? values[random.nextInt(n)] : 2 * random.nextInt(1000000) + 1;
                insertions[i] = random.nextInt();
            }
        }
    }

    private static final class Sample {
        private long nanos;
        private long accesses;
        private long movements;
        private long comparisons;
        private long checksum;
        private int batches;

        private void include(long elapsed, Metrics metrics, long value) {
            nanos += elapsed;
            accesses += metrics.accesses();
            movements += metrics.movements();
            comparisons += metrics.comparisons();
            checksum += value;
            batches++;
        }
    }

    public static void main(String[] args) throws IOException {
        Path root = Paths.get(args.length == 0 ? "results" : args[0]);
        Files.createDirectories(root.resolve("tables"));
        writeEnvironment(root);
        try (BufferedWriter writer = Files.newBufferedWriter(root.resolve("tables/raw.csv"), StandardCharsets.UTF_8)) {
            writer.write("workload,structure,n,m,repetition,elapsed_ns,accesses,movements,comparisons,batches,checksum\n");
            for (int n : SIZES) {
                Input input = new Input(n);
                for (int repetition = -WARMUPS; repetition < REPETITIONS; repetition++) {
                    for (int slot = 0; slot < 2; slot++) {
                        boolean array = (slot + repetition + WARMUPS) % 2 == 0;
                        String structure = array ? "DynamicArray" : "LinkedList";
                        record(writer, "random_access", structure, n, 10000, repetition, randomAccess(array, input));
                        record(writer, "search", structure, n, 1000, repetition, search(array, input));
                        record(writer, "insert_front", structure, n, 1000, repetition, insert(array, input, 0));
                        record(writer, "remove_front", structure, n, 1000, repetition, remove(array, input, 0));
                        record(writer, "insert_middle", structure, n, 1000, repetition, insert(array, input, n / 2));
                        record(writer, "remove_middle", structure, n, 1000, repetition, remove(array, input, n / 2));
                    }
                    priority(writer, input, repetition);
                }
                writer.flush();
                System.out.println("Completed n=" + n + " with " + REPETITIONS + " measured repetitions");
            }
        }
        System.out.println("Benchmark complete; checksum=" + sink);
    }

    private static IntSequence populated(boolean array, Input input) {
        IntSequence sequence = array ? new DynamicArray() : new LinkedList();
        for (int value : input.values) {
            sequence.add(value);
        }
        sequence.metrics().reset();
        return sequence;
    }

    private static Sample randomAccess(boolean array, Input input) {
        IntSequence sequence = populated(array, input);
        long checksum = 0;
        long start = System.nanoTime();
        for (int index : input.indices) {
            checksum += sequence.get(index);
        }
        long elapsed = System.nanoTime() - start;
        Sample sample = new Sample();
        sample.include(elapsed, sequence.metrics(), checksum);
        long expected = 0;
        for (int index : input.indices) {
            expected += input.values[index];
        }
        require(checksum == expected, "Random access checksum");
        sink = checksum;
        return sample;
    }

    private static Sample search(boolean array, Input input) {
        IntSequence sequence = populated(array, input);
        long found = 0;
        long start = System.nanoTime();
        for (int value : input.searches) {
            if (sequence.contains(value)) {
                found++;
            }
        }
        long elapsed = System.nanoTime() - start;
        Sample sample = new Sample();
        sample.include(elapsed, sequence.metrics(), found);
        require(found == 500, "Search hit count");
        sink = found;
        return sample;
    }

    private static Sample insert(boolean array, Input input, int index) {
        IntSequence sequence = populated(array, input);
        long start = System.nanoTime();
        for (int value : input.insertions) {
            sequence.add(index, value);
        }
        long elapsed = System.nanoTime() - start;
        Sample sample = new Sample();
        sample.include(elapsed, sequence.metrics(), sequence.size());
        require(sequence.size() == input.values.length + 1000, "Insertion size");
        for (int i = 0; i < 1000; i++) {
            require(sequence.get(index + i) == input.insertions[999 - i], "Inserted values");
        }
        require(sequence.get(index + 1000) == input.values[index], "Insertion suffix");
        require(sequence.get(sequence.size() - 1) == input.values[input.values.length - 1], "Insertion tail");
        if (index > 0) {
            require(sequence.get(index - 1) == input.values[index - 1], "Insertion prefix");
        }
        sink = sequence.size();
        return sample;
    }

    private static Sample remove(boolean array, Input input, int index) {
        Sample sample = new Sample();
        int remaining = 1000;
        while (remaining > 0) {
            IntSequence sequence = populated(array, input);
            int count = Math.min(remaining, input.values.length - index);
            long checksum = 0;
            long start = System.nanoTime();
            for (int i = 0; i < count; i++) {
                checksum += sequence.remove(index);
            }
            long elapsed = System.nanoTime() - start;
            sample.include(elapsed, sequence.metrics(), checksum);
            long expected = 0;
            for (int i = index; i < index + count; i++) {
                expected += input.values[i];
            }
            require(checksum == expected, "Removal checksum");
            require(sequence.size() == input.values.length - count, "Removal size");
            if (sequence.size() > 0) {
                require(sequence.get(0) == input.values[index == 0 ? count : 0], "Removal first element");
                int last = input.values.length - (index + count == input.values.length ? count : 0) - 1;
                require(sequence.get(sequence.size() - 1) == input.values[last], "Removal last element");
            }
            remaining -= count;
        }
        sink = sample.checksum;
        return sample;
    }

    private static void priority(BufferedWriter writer, Input input, int repetition) throws IOException {
        MinHeap heap = new MinHeap();
        int[] extracted = new int[input.values.length];
        long start = System.nanoTime();
        for (int value : input.values) {
            heap.insert(value);
        }
        long elapsed = System.nanoTime() - start;
        Sample insertion = new Sample();
        insertion.include(elapsed, heap.metrics(), heap.size());
        require(heap.isValidHeap(), "Heap property after bulk insertion");
        heap.metrics().reset();
        start = System.nanoTime();
        for (int i = 0; i < extracted.length; i++) {
            extracted[i] = heap.extractMin();
        }
        elapsed = System.nanoTime() - start;
        Sample extraction = new Sample();
        long checksum = 0;
        for (int i = 0; i < extracted.length; i++) {
            require(i == 0 || extracted[i - 1] <= extracted[i], "Extraction order");
            checksum += extracted[i];
        }
        extraction.include(elapsed, heap.metrics(), checksum);
        int[] sorted = input.values.clone();
        Arrays.sort(sorted);
        require(Arrays.equals(sorted, extracted), "Extraction values");
        require(heap.size() == 0 && heap.isValidHeap(), "Empty heap after extraction");
        sink = checksum;
        record(writer, "heap_insert", "MinHeap", input.values.length, input.values.length, repetition, insertion);
        record(writer, "heap_extract", "MinHeap", input.values.length, input.values.length, repetition, extraction);
    }

    private static void record(BufferedWriter writer, String workload, String structure, int n, int m,
                               int repetition, Sample sample) throws IOException {
        if (repetition < 0) {
            return;
        }
        writer.write(workload + "," + structure + "," + n + "," + m + "," + (repetition + 1) + ","
                + sample.nanos + "," + sample.accesses + "," + sample.movements + "," + sample.comparisons
                + "," + sample.batches + "," + sample.checksum + "\n");
    }

    private static void writeEnvironment(Path root) throws IOException {
        String metadata = "Recorded at (UTC): " + Instant.now() + "\n"
                + "Java: " + System.getProperty("java.runtime.version") + "\n"
                + "VM: " + System.getProperty("java.vm.name") + "\n"
                + "OS: " + System.getProperty("os.name") + " " + System.getProperty("os.version") + "\n"
                + "Architecture: " + System.getProperty("os.arch") + "\n"
                + "Available processors: " + Runtime.getRuntime().availableProcessors() + "\n"
                + "CPU: " + System.getenv("PROCESSOR_IDENTIFIER") + "\n"
                + "Maximum JVM heap bytes: " + Runtime.getRuntime().maxMemory() + "\n"
                + "VM arguments: " + ManagementFactory.getRuntimeMXBean().getInputArguments() + "\n"
                + "Seed: 42; warmups per configuration: " + WARMUPS + "; measured repetitions: " + REPETITIONS + "\n";
        Files.write(root.resolve("environment.txt"), metadata.getBytes(StandardCharsets.UTF_8));
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
