package algorithms;

import metrics.Metrics;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class QuickSortTest {

    private Metrics metrics;

    @BeforeEach
    void setUp() {
        metrics = new Metrics();
    }

    @Test
    void testQuickSortEmptyArray() {
        int[] array = {};
        QuickSort.sort(array, metrics);
        assertArrayEquals(new int[]{}, array);
        assertEquals(0, metrics.getComparisons());
        assertEquals(0, metrics.getMaxRecursionDepth());
    }

    @Test
    void testQuickSortSingleElement() {
        int[] array = {42};
        QuickSort.sort(array, metrics);
        assertArrayEquals(new int[]{42}, array);
        assertEquals(0, metrics.getComparisons());
        assertEquals(0, metrics.getMaxRecursionDepth());
    }

    @Test
    void testQuickSortAlreadySorted() {
        int[] array = {1, 2, 3, 4, 5};
        int[] expected = {1, 2, 3, 4, 5};
        QuickSort.sort(array, metrics);
        assertArrayEquals(expected, array);
        assertTrue(metrics.getComparisons() > 0);
        assertTrue(metrics.getMaxRecursionDepth() <= 3); // Should be balanced for sorted array with randomization
    }

    @Test
    void testQuickSortReverseSorted() {
        int[] array = {5, 4, 3, 2, 1};
        int[] expected = {1, 2, 3, 4, 5};
        QuickSort.sort(array, metrics);
        assertArrayEquals(expected, array);
        assertTrue(metrics.getComparisons() > 0);
    }

    @Test
    void testQuickSortRandomArray() {
        int[] array = {3, 1, 4, 1, 5, 9, 2, 6};
        int[] expected = {1, 1, 2, 3, 4, 5, 6, 9};
        QuickSort.sort(array, metrics);
        assertArrayEquals(expected, array);
        assertTrue(metrics.getComparisons() > 0);
    }

    @Test
    void testQuickSortWithDuplicates() {
        int[] array = {5, 2, 5, 1, 2, 5};
        int[] expected = {1, 2, 2, 5, 5, 5};
        QuickSort.sort(array, metrics);
        assertArrayEquals(expected, array);
    }

    @Test
    void testQuickSortLargeArray() {
        int n = 1000;
        int[] array = new int[n];
        Random random = new Random(42);
        for (int i = 0; i < n; i++) {
            array[i] = random.nextInt(10000);
        }

        int[] expected = array.clone();
        Arrays.sort(expected);

        QuickSort.sort(array, metrics);
        assertArrayEquals(expected, array);

        // Verify recursion depth is bounded (O(log n))
        int maxExpectedDepth = (int) (2 * (Math.log(n) / Math.log(2)) + 5);
        assertTrue(metrics.getMaxRecursionDepth() <= maxExpectedDepth,
                "Recursion depth " + metrics.getMaxRecursionDepth() + " should be <= " + maxExpectedDepth);
    }

    @Test
    void testQuickSortAllSameElements() {
        int[] array = {7, 7, 7, 7, 7};
        int[] expected = {7, 7, 7, 7, 7};
        QuickSort.sort(array, metrics);
        assertArrayEquals(expected, array);

        // With all same elements, should still make comparisons but depth should be reasonable
        assertTrue(metrics.getComparisons() > 0);
    }

    @Test
    void testQuickSortTwoElements() {
        int[] array = {2, 1};
        QuickSort.sort(array, metrics);
        assertArrayEquals(new int[]{1, 2}, array);
        assertEquals(1, metrics.getComparisons()); // One comparison for partition
        assertEquals(1, metrics.getMaxRecursionDepth());
    }

    @Test
    void testQuickSortThreeElements() {
        int[] array = {3, 1, 2};
        QuickSort.sort(array, metrics);
        assertArrayEquals(new int[]{1, 2, 3}, array);
        assertTrue(metrics.getComparisons() >= 2); // At least 2 comparisons
    }

    @Test
    void testQuickSortStabilityOfMetrics() {
        int[] array1 = {5, 3, 1, 4, 2};
        int[] array2 = {5, 3, 1, 4, 2};

        Metrics metrics1 = new Metrics();
        QuickSort.sort(array1, metrics1);

        Metrics metrics2 = new Metrics();
        QuickSort.sort(array2, metrics2);

        // Comparisons and depth should be similar (may vary due to randomization)
        int diff = Math.abs(metrics1.getComparisons() - metrics2.getComparisons());
        assertTrue(diff <= metrics1.getComparisons() * 0.2); // Within 20% difference

        int depthDiff = Math.abs(metrics1.getMaxRecursionDepth() - metrics2.getMaxRecursionDepth());
        assertTrue(depthDiff <= 2); // Depth should be very similar
    }

    @Test
    void testQuickSortNullArray() {
        int[] array = null;
        // Should not throw exception, just return early
        QuickSort.sort(array, metrics);
        assertNull(array);
    }

    @Test
    void testQuickSortRecursionDepthSmallArray() {
        int[] array = {4, 2, 1, 3, 5};
        QuickSort.sort(array, metrics);

        // For n=5, max depth should be around 3-4
        assertTrue(metrics.getMaxRecursionDepth() >= 2);
        assertTrue(metrics.getMaxRecursionDepth() <= 4);
    }

    @Test
    void testQuickSortWithNegativeNumbers() {
        int[] array = {-3, -1, -4, -2, 0, 2, 1};
        int[] expected = {-4, -3, -2, -1, 0, 1, 2};
        QuickSort.sort(array, metrics);
        assertArrayEquals(expected, array);
    }

    @Test
    void testQuickSortPartitionBehavior() {
        // Test that partition places pivot in correct position
        int[] array = {3, 1, 4, 1, 5, 9, 2, 6};
        Metrics partitionMetrics = new Metrics();

        // We can't test the private partition method directly, but we can verify
        // that after full sort, the array is sorted
        QuickSort.sort(array, partitionMetrics);

        for (int i = 1; i < array.length; i++) {
            assertTrue(array[i] >= array[i-1],
                    "Array not sorted at position " + i + ": " + array[i-1] + " > " + array[i]);
        }
    }

    @Test
    void testQuickSortMultipleRunsConsistency() {
        int[] template = {5, 3, 8, 1, 2, 7, 4, 6};
        int successes = 0;

        // Run multiple times to ensure consistency despite randomization
        for (int i = 0; i < 10; i++) {
            int[] array = template.clone();
            Metrics runMetrics = new Metrics();

            QuickSort.sort(array, runMetrics);

            if (isSorted(array)) {
                successes++;
            }
        }

        assertEquals(10, successes, "QuickSort should consistently produce sorted results");
    }

    private boolean isSorted(int[] array) {
        for (int i = 1; i < array.length; i++) {
            if (array[i] < array[i-1]) {
                return false;
            }
        }
        return true;
    }
}