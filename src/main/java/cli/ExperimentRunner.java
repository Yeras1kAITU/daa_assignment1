package cli;

import algorithms.*;
import metrics.Metrics;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class ExperimentRunner {

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.out.println("Usage: java ExperimentRunner <max_n> <output.csv>");
            return;
        }

        int maxN = Integer.parseInt(args[0]);
        String outputFile = args[1];

        try (FileWriter writer = new FileWriter(outputFile)) {
            writer.write("n,algorithm,time_ns,comparisons,allocations,max_depth\n");

            for (int n = 100; n <= maxN; n *= 2) {
                runExperimentsForN(n, writer);
            }
        }
    }

    private static void runExperimentsForN(int n, FileWriter writer) throws IOException {
        int[] array = generateRandomArray(n);

        // Test MergeSort
        runAlgorithm("MergeSort", array, MergeSort::sort, writer);

        // Test QuickSort
        runAlgorithm("QuickSort", array, QuickSort::sort, writer);

        // Test Deterministic Select (median)
        if (n > 0) {
            runSelectAlgorithm(array, n/2, writer);
        }

        // Test Closest Pair (for smaller n due to O(n log n) but large constant factors)
        if (n <= 10000) {
            runClosestPairAlgorithm(n, writer);
        }
    }

    private static void runAlgorithm(String name, int[] array,
                                     Algorithm algorithm, FileWriter writer) throws IOException {
        int[] copy = array.clone();
        Metrics metrics = new Metrics();

        long startTime = System.nanoTime();
        algorithm.run(copy, metrics);
        long endTime = System.nanoTime();

        writer.write(String.format("%d,%s,%d,%d,%d,%d\n",
                array.length, name, endTime - startTime,
                metrics.getComparisons(), metrics.getAllocations(),
                metrics.getMaxRecursionDepth()));
    }

    private static void runSelectAlgorithm(int[] array, int k,
                                           FileWriter writer) throws IOException {
        int[] copy = array.clone();
        Metrics metrics = new Metrics();

        long startTime = System.nanoTime();
        DeterministicSelect.select(copy, k, metrics);
        long endTime = System.nanoTime();

        writer.write(String.format("%d,Select,%d,%d,%d,%d\n",
                array.length, endTime - startTime,
                metrics.getComparisons(), metrics.getAllocations(),
                metrics.getMaxRecursionDepth()));
    }

    private static void runClosestPairAlgorithm(int n, FileWriter writer) throws IOException {
        ClosestPair.Point[] points = generateRandomPoints(n);
        Metrics metrics = new Metrics();

        long startTime = System.nanoTime();
        ClosestPair.findClosestPair(points, metrics);
        long endTime = System.nanoTime();

        writer.write(String.format("%d,ClosestPair,%d,%d,%d,%d\n",
                n, endTime - startTime,
                metrics.getComparisons(), metrics.getAllocations(),
                metrics.getMaxRecursionDepth()));
    }

    private static int[] generateRandomArray(int n) {
        Random random = new Random(42);
        int[] array = new int[n];
        for (int i = 0; i < n; i++) {
            array[i] = random.nextInt(n * 10);
        }
        return array;
    }

    private static ClosestPair.Point[] generateRandomPoints(int n) {
        Random random = new Random(42);
        ClosestPair.Point[] points = new ClosestPair.Point[n];
        for (int i = 0; i < n; i++) {
            points[i] = new ClosestPair.Point(random.nextDouble() * 1000,
                    random.nextDouble() * 1000);
        }
        return points;
    }

    interface Algorithm {
        void run(int[] array, Metrics metrics);
    }
}