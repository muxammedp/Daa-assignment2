public final class Metrics {
    private long accesses;
    private long movements;
    private long comparisons;

    public void access() {
        accesses++;
    }

    public void move() {
        movements++;
    }

    public void compare() {
        comparisons++;
    }

    public long accesses() {
        return accesses;
    }

    public long movements() {
        return movements;
    }

    public long comparisons() {
        return comparisons;
    }

    public void reset() {
        accesses = 0;
        movements = 0;
        comparisons = 0;
    }
}
