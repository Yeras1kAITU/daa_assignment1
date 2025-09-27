package benchmarks;

import algorithms.DeterministicSelect;
import algorithms.MergeSort;
import algorithms.QuickSort;
import metrics.Metrics;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(2)
public class SortingBenchmark {

    @Param({"100", "1000", "10000", "50000"})
    private int arraySize;

    private int[] data;
    private int[] dataCopy;
    private Random random;

    @Setup(Level.Iteration)
    public void setup() {
        random = new Random(42);
        data = new int[arraySize];
        for (int i = 0; i < arraySize; i++) {
            data[i] = random.nextInt(arraySize * 10);
        }
        dataCopy = data.clone();
    }

    @Benchmark
    public void mergeSort(Blackhole bh) {
        int[] array = data.clone();
        Metrics metrics = new Metrics();
        MergeSort.sort(array, metrics);
        bh.consume(array);
        bh.consume(metrics);
    }

    @Benchmark
    public void quickSort(Blackhole bh) {
        int[] array = data.clone();
        Metrics metrics = new Metrics();
        QuickSort.sort(array, metrics);
        bh.consume(array);
        bh.consume(metrics);
    }

    @Benchmark
    public void arraysSort(Blackhole bh) {
        int[] array = data.clone();
        long start = System.nanoTime();
        Arrays.sort(array);
        long end = System.nanoTime();
        bh.consume(array);
        bh.consume(end - start);
    }
}