package algorithms;

import metrics.Metrics;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class DeterministicSelectTest {

    private Metrics metrics;

    @BeforeEach
    void setUp() {
        metrics = new Metrics();
    }

    @Test
    void testSelectEmptyArray() {
        int[] array = {};
        assertThrows(IllegalArgumentException.class, () -> {
            DeterministicSelect.select(array, 0, metrics);
        });
    }

    @Test
    void testSelectNullArray() {
        int[] array = null;
        assertThrows(IllegalArgumentException.class, () -> {
            DeterministicSelect.select(array, 0, metrics);
        });
    }

    @Test
    void testSelectInvalidK() {
        int[] array = {1, 2, 3};
        assertThrows(IllegalArgumentException.class, () -> {
            DeterministicSelect.select(array, -1, metrics);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            DeterministicSelect.select(array, 3, metrics);
        });
    }

    @Test
    void testSelectSingleElement() {
        int[] array = {42};
        int result = DeterministicSelect.select(array, 0, metrics);
        assertEquals(42, result);
        assertEquals(0, metrics.getComparisons()); // No comparisons needed for single element
    }

    @Test
    void testSelectTwoElements() {
        int[] array = {2, 1};

        // Test k=0 (smallest)
        int result0 = DeterministicSelect.select(array.clone(), 0, new Metrics());
        assertEquals(1, result0);

        // Test k=1 (largest)
        int result1 = DeterministicSelect.select(array.clone(), 1, new Metrics());
        assertEquals(2, result1);
    }

    @Test
    void testSelectThreeElements() {
        int[] array = {3, 1, 2};

        for (int k = 0; k < 3; k++) {
            int[] copy = array.clone();
            int result = DeterministicSelect.select(copy, k, new Metrics());

            Arrays.sort(array);
            assertEquals(array[k], result, "Failed for k=" + k);
        }
    }

    @Test
    void testSelectMedianOfMediansSmallArray() {
        int[] array = {5, 3, 8, 1, 2, 7, 4, 6};

        for (int k = 0; k < array.length; k++) {
            int[] copy = array.clone();
            int result = DeterministicSelect.select(copy, k, new Metrics());

            Arrays.sort(array);
            assertEquals(array[k], result, "Failed for k=" + k);
        }
    }

    @Test
    void testSelectLargeArray() {
        int n = 100;
        int[] array = new int[n];
        Random random = new Random(42);
        for (int i = 0; i < n; i++) {
            array[i] = random.nextInt(1000);
        }

        // Test multiple k values
        int[] testKs = {0, n/4, n/2, 3*n/4, n-1};

        for (int k : testKs) {
            int[] copy = array.clone();
            Metrics testMetrics = new Metrics();
            int result = DeterministicSelect.select(copy, k, testMetrics);

            Arrays.sort(array);
            assertEquals(array[k], result, "Failed for k=" + k);

            // Verify O(n) behavior - comparisons should be linear
            assertTrue(testMetrics.getComparisons() <= 100 * n,
                    "Comparisons should be O(n), got: " + testMetrics.getComparisons());
        }
    }

    @Test
    void testSelectWithDuplicates() {
        int[] array = {5, 2, 5, 1, 2, 5, 1, 5};

        for (int k = 0; k < array.length; k++) {
            int[] copy = array.clone();
            int result = DeterministicSelect.select(copy, k, new Metrics());

            Arrays.sort(array);
            assertEquals(array[k], result, "Failed for k=" + k);
        }
    }

    @Test
    void testSelectAllSameElements() {
        int[] array = {7, 7, 7, 7, 7};

        for (int k = 0; k < array.length; k++) {
            int[] copy = array.clone();
            int result = DeterministicSelect.select(copy, k, new Metrics());
            assertEquals(7, result, "Failed for k=" + k);
        }
    }

    @Test
    void testSelectAlreadySorted() {
        int[] array = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

        for (int k = 0; k < array.length; k++) {
            int[] copy = array.clone();
            int result = DeterministicSelect.select(copy, k, new Metrics());
            assertEquals(k + 1, result, "Failed for k=" + k);
        }
    }

    @Test
    void testSelectReverseSorted() {
        int[] array = {10, 9, 8, 7, 6, 5, 4, 3, 2, 1};

        for (int k = 0; k < array.length; k++) {
            int[] copy = array.clone();
            int result = DeterministicSelect.select(copy, k, new Metrics());

            Arrays.sort(array);
            assertEquals(array[k], result, "Failed for k=" + k);
        }
    }

    @Test
    void testSelectMedianValue() {
        int[] array = {3, 1, 4, 1, 5, 9, 2, 6};
        int medianIndex = array.length / 2;

        int result = DeterministicSelect.select(array.clone(), medianIndex, metrics);

        Arrays.sort(array);
        assertEquals(array[medianIndex], result);
        assertTrue(metrics.getComparisons() > 0);
    }

    @Test
    void testSelectMinimumElement() {
        int[] array = {5, 3, 8, 1, 2, 7, 4, 6};
        int result = DeterministicSelect.select(array.clone(), 0, metrics);

        Arrays.sort(array);
        assertEquals(array[0], result);
    }

    @Test
    void testSelectMaximumElement() {
        int[] array = {5, 3, 8, 1, 2, 7, 4, 6};
        int maxIndex = array.length - 1;
        int result = DeterministicSelect.select(array.clone(), maxIndex, metrics);

        Arrays.sort(array);
        assertEquals(array[maxIndex], result);
    }

    @Test
    void testSelectWithNegativeNumbers() {
        int[] array = {-3, -1, -4, -2, 0, 2, 1, -5};

        for (int k = 0; k < array.length; k++) {
            int[] copy = array.clone();
            int result = DeterministicSelect.select(copy, k, new Metrics());

            Arrays.sort(array);
            assertEquals(array[k], result, "Failed for k=" + k);
        }
    }

    @Test
    void testSelectArrayNotModified() {
        int[] original = {5, 3, 8, 1, 2, 7, 4, 6};
        int[] array = original.clone();

        DeterministicSelect.select(array, 3, metrics);

        // The array might be partially modified during selection, but should contain same elements
        Arrays.sort(original);
        Arrays.sort(array);
        assertArrayEquals(original, array);
    }

    @Test
    void testSelectRecursionDepth() {
        int n = 100;
        int[] array = new int[n];
        Random random = new Random(42);
        for (int i = 0; i < n; i++) {
            array[i] = random.nextInt(1000);
        }

        DeterministicSelect.select(array.clone(), n/2, metrics);

        // Depth should be O(log n) due to good pivots from median-of-medians
        int maxExpectedDepth = (int) (5 * (Math.log(n) / Math.log(2)) + 10);
        assertTrue(metrics.getMaxRecursionDepth() <= maxExpectedDepth,
                "Depth: " + metrics.getMaxRecursionDepth() + " should be <= " + maxExpectedDepth);
    }

    @Test
    void testSelectGroupsOfFive() {
        // Test specifically with size that's multiple of 5
        int[] array = {25, 14, 33, 42, 11, 19, 27, 36, 45, 8, 17, 26, 35, 44, 3};

        for (int k = 0; k < array.length; k++) {
            int[] copy = array.clone();
            int result = DeterministicSelect.select(copy, k, new Metrics());

            Arrays.sort(array);
            assertEquals(array[k], result, "Failed for k=" + k);
        }
    }

    @Test
    void testSelectGroupsNotMultipleOfFive() {
        // Test with size not multiple of 5
        int[] array = {5, 3, 8, 1, 2, 7, 4, 6, 9}; // 9 elements

        for (int k = 0; k < array.length; k++) {
            int[] copy = array.clone();
            int result = DeterministicSelect.select(copy, k, new Metrics());

            Arrays.sort(array);
            assertEquals(array[k], result, "Failed for k=" + k);
        }
    }

    @Test
    void testSelectComparisonWithSorting() {
        int n = 50;
        int[] array = new int[n];
        Random random = new Random(42);
        for (int i = 0; i < n; i++) {
            array[i] = random.nextInt(1000);
        }

        // Test 100 random k values
        for (int trial = 0; trial < 100; trial++) {
            int k = random.nextInt(n);
            int[] copy = array.clone();
            int result = DeterministicSelect.select(copy, k, new Metrics());

            Arrays.sort(array);
            assertEquals(array[k], result, "Failed for k=" + k + " in trial " + trial);
        }
    }

    @Test
    void testSelectLinearTimeComplexity() {
        // Test that runtime grows linearly by comparing different sizes
        int[] sizes = {10, 50, 100, 200};
        double[] comparisonsPerElement = new double[sizes.length];

        for (int i = 0; i < sizes.length; i++) {
            int n = sizes[i];
            int[] array = new int[n];
            Random random = new Random(42);
            for (int j = 0; j < n; j++) {
                array[j] = random.nextInt(n * 10);
            }

            Metrics sizeMetrics = new Metrics();
            DeterministicSelect.select(array, n/2, sizeMetrics);

            comparisonsPerElement[i] = (double) sizeMetrics.getComparisons() / n;
        }

        // Check that comparisons per element doesn't grow significantly
        for (int i = 1; i < comparisonsPerElement.length; i++) {
            double ratio = comparisonsPerElement[i] / comparisonsPerElement[0];
            assertTrue(ratio < 5.0, "Comparisons per element should not grow significantly");
        }
    }

    @Test
    void testSelectBoundaryValues() {
        int[] array = {Integer.MIN_VALUE, -100, 0, 100, Integer.MAX_VALUE};

        for (int k = 0; k < array.length; k++) {
            int[] copy = array.clone();
            int result = DeterministicSelect.select(copy, k, new Metrics());

            Arrays.sort(array);
            assertEquals(array[k], result, "Failed for k=" + k);
        }
    }

    @Test
    void testSelectStability() {
        int[] array = {5, 3, 8, 1, 2, 7, 4, 6};
        int k = 3;

        // Multiple runs should produce same result
        int expected = DeterministicSelect.select(array.clone(), k, new Metrics());

        for (int i = 0; i < 10; i++) {
            int[] copy = array.clone();
            int result = DeterministicSelect.select(copy, k, new Metrics());
            assertEquals(expected, result, "Result should be stable across runs");
        }
    }
}