package algorithms;

import metrics.Metrics;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import util.ArrayUtils;

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
        assertTrue(metrics.getMaxRecursionDepth() <= 3);
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
        assertTrue(metrics.getComparisons() > 0);
    }

    @Test
    void testQuickSortTwoElements() {
        int[] array = {2, 1};
        QuickSort.sort(array, metrics);
        assertArrayEquals(new int[]{1, 2}, array);
        assertEquals(1, metrics.getComparisons());
        assertEquals(1, metrics.getMaxRecursionDepth());
    }

    @Test
    void testQuickSortThreeElements() {
        int[] array = {3, 1, 2};
        QuickSort.sort(array, metrics);
        assertArrayEquals(new int[]{1, 2, 3}, array);
        assertTrue(metrics.getComparisons() >= 2);
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
        assertTrue(diff <= metrics1.getComparisons() * 0.2);

        int depthDiff = Math.abs(metrics1.getMaxRecursionDepth() - metrics2.getMaxRecursionDepth());
        assertTrue(depthDiff <= 2);
    }

    @Test
    void testQuickSortNullArray() {
        int[] array = null;
        QuickSort.sort(array, metrics);
        assertNull(array);
    }

    @Test
    void testQuickSortRecursionDepthSmallArray() {
        int[] array = {4, 2, 1, 3, 5};
        QuickSort.sort(array, metrics);
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
    void testQuickSortMultipleRunsConsistency() {
        int[] template = {5, 3, 8, 1, 2, 7, 4, 6};
        int successes = 0;

        for (int i = 0; i < 10; i++) {
            int[] array = template.clone();
            Metrics runMetrics = new Metrics();

            QuickSort.sort(array, runMetrics);

            if (ArrayUtils.isSorted(array)) {
                successes++;
            }
        }

        assertEquals(10, successes, "QuickSort should consistently produce sorted results");
    }

    @Test
    void testQuickSortArrayUtilsIntegration() {
        // Test that ArrayUtils.shuffle is properly integrated
        int[] array = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        int[] original = array.clone();

        QuickSort.sort(array, metrics);

        // Array should be sorted regardless of initial shuffle
        assertTrue(ArrayUtils.isSorted(array));
        // Original array should be different due to shuffle (but we can't easily test this)
    }

    @Test
    void testQuickSortSwapOperations() {
        // Test that ArrayUtils.swap is working correctly through integration
        int[] array = {5, 3, 8, 1};
        QuickSort.sort(array, metrics);

        // Verify the final sorted result
        assertArrayEquals(new int[]{1, 3, 5, 8}, array);
        assertTrue(metrics.getComparisons() > 0);
    }

    @Test
    void testQuickSortVeryLargeArray() {
        int n = 10000;
        int[] array = new int[n];
        Random random = new Random(42);
        for (int i = 0; i < n; i++) {
            array[i] = random.nextInt(100000);
        }

        int[] expected = array.clone();
        Arrays.sort(expected);

        Metrics largeMetrics = new Metrics();
        QuickSort.sort(array, largeMetrics);

        assertArrayEquals(expected, array);

        // Verify O(log n) depth even for large n
        int maxExpectedDepth = (int) (2 * (Math.log(n) / Math.log(2)) + 10);
        assertTrue(largeMetrics.getMaxRecursionDepth() <= maxExpectedDepth,
                "Depth: " + largeMetrics.getMaxRecursionDepth() + " should be <= " + maxExpectedDepth);
    }

    @Test
    void testQuickSortAlreadyShuffled() {
        int[] array = {9, 2, 7, 4, 1, 8, 3, 6, 5};
        int[] expected = {1, 2, 3, 4, 5, 6, 7, 8, 9};

        QuickSort.sort(array, metrics);
        assertArrayEquals(expected, array);
    }

    @Test
    void testQuickSortComparisonWithMergeSort() {
        int[] array = {5, 3, 8, 1, 2, 7, 4, 6};
        int[] arrayCopy = array.clone();

        Metrics quickSortMetrics = new Metrics();
        QuickSort.sort(array, quickSortMetrics);

        Metrics mergeSortMetrics = new Metrics();
        MergeSort.sort(arrayCopy, mergeSortMetrics);

        assertArrayEquals(array, arrayCopy); // Both should produce same sorted result
        // QuickSort typically has fewer comparisons but this can vary
        assertTrue(quickSortMetrics.getComparisons() > 0);
        assertTrue(mergeSortMetrics.getComparisons() > 0);
    }

    @Test
    void testQuickSortArrayWithZeros() {
        int[] array = {0, 5, 0, 2, 0, 1};
        int[] expected = {0, 0, 0, 1, 2, 5};
        QuickSort.sort(array, metrics);
        assertArrayEquals(expected, array);
    }

    @Test
    void testQuickSortBoundaryConditions() {
        // Test array with minimum and maximum integer values
        int[] array = {Integer.MIN_VALUE, Integer.MAX_VALUE, 0, -1, 1};
        int[] expected = {Integer.MIN_VALUE, -1, 0, 1, Integer.MAX_VALUE};
        QuickSort.sort(array, metrics);
        assertArrayEquals(expected, array);
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