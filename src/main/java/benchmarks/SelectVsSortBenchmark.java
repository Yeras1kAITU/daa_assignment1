package benchmarks;

import algorithms.DeterministicSelect;
import metrics.Metrics;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(2)
public class SelectVsSortBenchmark {

    @Param({"100", "1000", "10000", "50000"})
    private int arraySize;

    private int[] data;
    private Random random;

    @Setup(Level.Iteration)
    public void setup() {
        random = new Random(42);
        data = new int[arraySize];
        for (int i = 0; i < arraySize; i++) {
            data[i] = random.nextInt(arraySize * 10);
        }
    }

    @Benchmark
    public void deterministicSelectMedian(Blackhole bh) {
        int[] array = data.clone();
        Metrics metrics = new Metrics();
        int medianIndex = arraySize / 2;
        int result = DeterministicSelect.select(array, medianIndex, metrics);
        bh.consume(result);
        bh.consume(metrics);
    }

    @Benchmark
    public void sortThenSelectMedian(Blackhole bh) {
        int[] array = data.clone();
        long start = System.nanoTime();
        Arrays.sort(array);
        int medianIndex = arraySize / 2;
        int result = array[medianIndex];
        long end = System.nanoTime();
        bh.consume(result);
        bh.consume(end - start);
    }

    @Benchmark
    public void deterministicSelectVariousK(Blackhole bh) {
        int[] array = data.clone();
        Metrics metrics = new Metrics();

        // Test multiple k values: first quartile, median, third quartile
        int[] kValues = {arraySize / 4, arraySize / 2, 3 * arraySize / 4};

        for (int k : kValues) {
            int[] copy = array.clone();
            int result = DeterministicSelect.select(copy, k, new Metrics());
            bh.consume(result);
        }
        bh.consume(metrics);
    }

    @Benchmark
    public void sortThenSelectVariousK(Blackhole bh) {
        int[] array = data.clone();
        long start = System.nanoTime();
        Arrays.sort(array);
        long end = System.nanoTime();

        int[] kValues = {arraySize / 4, arraySize / 2, 3 * arraySize / 4};
        for (int k : kValues) {
            bh.consume(array[k]);
        }
        bh.consume(end - start);
    }
}