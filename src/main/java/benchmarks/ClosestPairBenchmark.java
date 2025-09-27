package benchmarks;

import algorithms.ClosestPair;
import metrics.Metrics;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 3, time = 1)
@Fork(1)  // Fewer forks due to longer running time
public class ClosestPairBenchmark {

    @Param({"100", "500", "1000", "2000"})
    private int pointsSize;

    private ClosestPair.Point[] points;
    private Random random;

    @Setup(Level.Iteration)
    public void setup() {
        random = new Random(42);
        points = new ClosestPair.Point[pointsSize];
        for (int i = 0; i < pointsSize; i++) {
            points[i] = new ClosestPair.Point(
                    random.nextDouble() * 1000,
                    random.nextDouble() * 1000
            );
        }
    }

    @Benchmark
    public void closestPairDivideConquer(Blackhole bh) {
        ClosestPair.Point[] pointsCopy = points.clone();
        Metrics metrics = new Metrics();
        double result = ClosestPair.findClosestPair(pointsCopy, metrics);
        bh.consume(result);
        bh.consume(metrics);
    }

    @Benchmark
    public void closestPairBruteForce(Blackhole bh) {
        ClosestPair.Point[] pointsCopy = points.clone();
        double result = bruteForceClosestPair(pointsCopy);
        bh.consume(result);
    }

    private double bruteForceClosestPair(ClosestPair.Point[] points) {
        double minDistance = Double.POSITIVE_INFINITY;
        for (int i = 0; i < points.length; i++) {
            for (int j = i + 1; j < points.length; j++) {
                double dist = points[i].distanceTo(points[j]);
                if (dist < minDistance) {
                    minDistance = dist;
                }
            }
        }
        return minDistance;
    }
}