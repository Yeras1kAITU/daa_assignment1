package algorithms;

import metrics.Metrics;
import metrics.RecursionTracker;
import util.ArrayUtils;

import java.util.Random;

public class QuickSort {
    private static final Random RANDOM = new Random();

    public static void sort(int[] array, Metrics metrics) {
        if (array == null || array.length <= 1) return;
        ArrayUtils.shuffle(array, RANDOM); // Randomize for expected O(n log n)
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
        ArrayUtils.swap(array, pivotIndex, right);

        int storeIndex = left;
        for (int i = left; i < right; i++) {
            metrics.incrementComparisons();
            if (array[i] < pivotValue) {
                ArrayUtils.swap(array, i, storeIndex);
                storeIndex++;
            }
        }

        ArrayUtils.swap(array, storeIndex, right);
        return storeIndex;
    }
}