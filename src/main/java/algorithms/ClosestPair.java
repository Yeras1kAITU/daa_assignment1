package algorithms;

import metrics.Metrics;
import metrics.RecursionTracker;

import java.util.Arrays;
import java.util.Comparator;

public class ClosestPair {

    public static class Point {
        public final double x, y;
        public Point(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public double distanceTo(Point other) {
            double dx = x - other.x;
            double dy = y - other.y;
            return Math.sqrt(dx * dx + dy * dy);
        }
    }

    public static double findClosestPair(Point[] points, Metrics metrics) {
        if (points == null || points.length < 2) {
            throw new IllegalArgumentException("At least two points required");
        }

        // Sort by x coordinate
        Point[] pointsByX = points.clone();
        Arrays.sort(pointsByX, Comparator.comparingDouble(p -> p.x));
        metrics.incrementAllocations();

        return findClosestPair(pointsByX, 0, points.length - 1, metrics);
    }

    private static double findClosestPair(Point[] pointsByX, int left, int right, Metrics metrics) {
        if (right - left <= 3) {
            return bruteForce(pointsByX, left, right, metrics);
        }

        try (RecursionTracker rt = new RecursionTracker(metrics)) {
            int mid = left + (right - left) / 2;
            double midX = pointsByX[mid].x;

            double dLeft = findClosestPair(pointsByX, left, mid, metrics);
            double dRight = findClosestPair(pointsByX, mid + 1, right, metrics);
            double d = Math.min(dLeft, dRight);

            // Check strip around mid-line
            return Math.min(d, checkStrip(pointsByX, left, right, midX, d, metrics));
        }
    }

    private static double bruteForce(Point[] points, int left, int right, Metrics metrics) {
        double minDistance = Double.POSITIVE_INFINITY;

        for (int i = left; i <= right; i++) {
            for (int j = i + 1; j <= right; j++) {
                double dist = points[i].distanceTo(points[j]);
                metrics.incrementComparisons();
                if (dist < minDistance) {
                    minDistance = dist;
                }
            }
        }

        return minDistance;
    }

    private static double checkStrip(Point[] pointsByX, int left, int right, double midX,
                                     double d, Metrics metrics) {
        // Collect points in strip
        Point[] strip = new Point[right - left + 1];
        int stripSize = 0;

        for (int i = left; i <= right; i++) {
            if (Math.abs(pointsByX[i].x - midX) < d) {
                strip[stripSize++] = pointsByX[i];
            }
        }

        // Sort strip by y coordinate
        Arrays.sort(strip, 0, stripSize, Comparator.comparingDouble(p -> p.y));

        double minDistance = d;

        // Check only next 7 points for each point in strip
        for (int i = 0; i < stripSize; i++) {
            for (int j = i + 1; j < stripSize && (strip[j].y - strip[i].y) < minDistance; j++) {
                if (j - i > 7) break; // Theoretical limit is 7

                double dist = strip[i].distanceTo(strip[j]);
                metrics.incrementComparisons();
                if (dist < minDistance) {
                    minDistance = dist;
                }
            }
        }

        return minDistance;
    }
}