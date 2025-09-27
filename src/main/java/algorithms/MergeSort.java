package algorithms;

import metrics.Metrics;
import metrics.RecursionTracker;

public class MergeSort {
    private static final int INSERTION_SORT_CUTOFF = 7;

    public static void sort(int[] array, Metrics metrics) {
        if (array == null || array.length <= 1) return;
        int[] buffer = new int[array.length];
        metrics.incrementAllocations();
        sort(array, 0, array.length - 1, buffer, metrics);
    }

    private static void sort(int[] array, int left, int right, int[] buffer, Metrics metrics) {
        if (right - left <= INSERTION_SORT_CUTOFF) {
            insertionSort(array, left, right, metrics);
            return;
        }

        try (RecursionTracker rt = new RecursionTracker(metrics)) {
            int mid = left + (right - left) / 2;
            sort(array, left, mid, buffer, metrics);
            sort(array, mid + 1, right, buffer, metrics);
            merge(array, left, mid, right, buffer, metrics);
        }
    }

    private static void insertionSort(int[] array, int left, int right, Metrics metrics) {
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
    }

    private static void merge(int[] array, int left, int mid, int right, int[] buffer, Metrics metrics) {
        // Copy to buffer
        System.arraycopy(array, left, buffer, left, right - left + 1);

        int i = left, j = mid + 1, k = left;

        while (i <= mid && j <= right) {
            metrics.incrementComparisons();
            if (buffer[i] <= buffer[j]) {
                array[k++] = buffer[i++];
            } else {
                array[k++] = buffer[j++];
            }
        }

        while (i <= mid) array[k++] = buffer[i++];
        while (j <= right) array[k++] = buffer[j++];
    }
}