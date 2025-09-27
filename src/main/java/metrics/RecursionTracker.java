package metrics;

public class RecursionTracker implements AutoCloseable {
    private final Metrics metrics;

    public RecursionTracker(Metrics metrics) {
        this.metrics = metrics;
        metrics.enterRecursion();
    }

    @Override
    public void close() {
        metrics.exitRecursion();
    }
}