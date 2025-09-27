package metrics;

public class Metrics {
    private int comparisons;
    private int allocations;
    private int maxRecursionDepth;
    private int currentRecursionDepth;

    public Metrics() {
        this.comparisons = 0;
        this.allocations = 0;
        this.maxRecursionDepth = 0;
        this.currentRecursionDepth = 0;
    }

    public void incrementComparisons() {
        comparisons++;
    }

    public void incrementComparisons(int n) {
        comparisons += n;
    }

    public void incrementAllocations() {
        allocations++;
    }

    public void enterRecursion() {
        currentRecursionDepth++;
        if (currentRecursionDepth > maxRecursionDepth) {
            maxRecursionDepth = currentRecursionDepth;
        }
    }

    public void exitRecursion() {
        currentRecursionDepth--;
    }

    // Getters
    public int getComparisons() { return comparisons; }
    public int getAllocations() { return allocations; }
    public int getMaxRecursionDepth() { return maxRecursionDepth; }
}
