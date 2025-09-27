package algorithms;

import metrics.Metrics;
import metrics.RecursionTracker;
import util.ArrayUtils;

public class DeterministicSelect {

    public static int select(int[] array, int k, Metrics metrics) {
        if (array == null || array.length == 0) {
            throw new IllegalArgumentException("Array must not be null or empty");
        }
        if (k < 0 || k >= array.length) {
            throw new IllegalArgumentException("k must be in [0, array.length-1]");
        }
        return select(array, 0, array.length - 1, k, metrics);
    }

    private static int select(int[] array, int left, int right, int k, Metrics metrics) {
        if (left == right) return array[left];

        try (RecursionTracker rt = new RecursionTracker(metrics)) {
            int pivotIndex = medianOfMedians(array, left, right, metrics);
            pivotIndex = partition(array, left, right, pivotIndex, metrics);

            if (k == pivotIndex) {
                return array[k];
            } else if (k < pivotIndex) {
                return select(array, left, pivotIndex - 1, k, metrics);
            } else {
                return select(array, pivotIndex + 1, right, k, metrics);
            }
        }
    }

    private static int medianOfMedians(int[] array, int left, int right, Metrics metrics) {
        int n = right - left + 1;
        if (n <= 5) {
            return medianOfFive(array, left, right, metrics);
        }

        // Group into n/5 groups of 5
        int numGroups = (n + 4) / 5;
        int[] medians = new int[numGroups];
        metrics.incrementAllocations();

        for (int i = 0; i < numGroups; i++) {
            int groupLeft = left + i * 5;
            int groupRight = Math.min(groupLeft + 4, right);
            medians[i] = medianOfFive(array, groupLeft, groupRight, metrics);
        }

        // Recursively find median of medians
        return select(medians, 0, numGroups - 1, numGroups / 2, metrics);
    }

    private static int medianOfFive(int[] array, int left, int right, Metrics metrics) {
        // Simple insertion sort for 5 elements
        for (int i = left + 1; i <= right; i++) {
            int key = array[i];
            int j = i - 1;
            while (j >= left) {
                metrics.incrementComparisons();
                if (array[j] > key) {
                    array[j + 1] = array[j];
                    j--;
                } else {
                    break;
                }
            }
            array[j + 1] = key;
        }
        return left + (right - left) / 2;
    }

    private static int partition(int[] array, int left, int right, int pivotIndex, Metrics metrics) {
        int pivotValue = array[pivotIndex];
        ArrayUtils.swap(array, pivotIndex, right);

        int storeIndex = left;
        for (int i = left; i < right; i++) {
            metrics.incrementComparisons();
            if (array[i] < pivotValue) {
                ArrayUtils.swap(array, storeIndex, i);
                storeIndex++;
            }
        }

        ArrayUtils.swap(array, storeIndex, right);
        return storeIndex;
    }
}