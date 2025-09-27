package algorithms;

import metrics.Metrics;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MergeSortTest {
    @Test
    void testSort() {
        int[] array = {5, 3, 1, 4, 2};
        Metrics metrics = new Metrics();
        MergeSort.sort(array, metrics);
        assertArrayEquals(new int[]{1, 2, 3, 4, 5}, array);
    }
}