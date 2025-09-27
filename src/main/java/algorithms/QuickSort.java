package algorithms;

import metrics.Metrics;
import metrics.RecursionTracker;
import java.util.Random;

public class QuickSort {
    private static final Random RANDOM = new Random();

    public static void sort(int[] array, Metrics metrics) {
        if (array == null || array.length <= 1) return;
        shuffle(array); // Randomize for expected O(n log n)
        sort(array, 0, array.length - 1, metrics);
    }

    private static void sort(int[] array, int left, int right, Metrics metrics) {
        while (left < right) {
            try (RecursionTracker rt = new RecursionTracker(metrics)) {
                int pivotIndex = partition(array, left, right, metrics);

                // Recurse on smaller partition first to bound stack depth
                if (pivotIndex - left < right - pivotIndex) {
                    sort(array, left, pivotIndex - 1, metrics);
                    left = pivotIndex + 1;
                } else {
                    sort(array, pivotIndex + 1, right, metrics);
                    right = pivotIndex - 1;
                }
            }
        }
    }

    private static int partition(int[] array, int left, int right, Metrics metrics) {
        int pivotIndex = left + RANDOM.nextInt(right - left + 1);
        int pivotValue = array[pivotIndex];
        swap(array, pivotIndex, right);

        int storeIndex = left;
        for (int i = left; i < right; i++) {
            metrics.incrementComparisons();
            if (array[i] < pivotValue) {
                swap(array, i, storeIndex);
                storeIndex++;
            }
        }

        swap(array, storeIndex, right);
        return storeIndex;
    }

    // Internal utility methods (will be refactored later)
    private static void swap(int[] array, int i, int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    private static void shuffle(int[] array) {
        for (int i = array.length - 1; i > 0; i--) {
            int j = RANDOM.nextInt(i + 1);
            swap(array, i, j);
        }
    }
}