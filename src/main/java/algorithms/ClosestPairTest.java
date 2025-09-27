package algorithms;

import metrics.Metrics;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class ClosestPairTest {

    private Metrics metrics;

    @BeforeEach
    void setUp() {
        metrics = new Metrics();
    }

    @Test
    void testClosestPairNullPoints() {
        ClosestPair.Point[] points = null;
        assertThrows(IllegalArgumentException.class, () -> {
            ClosestPair.findClosestPair(points, metrics);
        });
    }

    @Test
    void testClosestPairEmptyArray() {
        ClosestPair.Point[] points = new ClosestPair.Point[0];
        assertThrows(IllegalArgumentException.class, () -> {
            ClosestPair.findClosestPair(points, metrics);
        });
    }

    @Test
    void testClosestPairSinglePoint() {
        ClosestPair.Point[] points = {new ClosestPair.Point(1, 1)};
        assertThrows(IllegalArgumentException.class, () -> {
            ClosestPair.findClosestPair(points, metrics);
        });
    }

    @Test
    void testClosestPairTwoPoints() {
        ClosestPair.Point[] points = {
                new ClosestPair.Point(1, 1),
                new ClosestPair.Point(4, 5)
        };
        double result = ClosestPair.findClosestPair(points, metrics);
        double expected = points[0].distanceTo(points[1]);
        assertEquals(expected, result, 1e-9);
        assertEquals(1, metrics.getComparisons());
    }

    @Test
    void testClosestPairThreePoints() {
        ClosestPair.Point[] points = {
                new ClosestPair.Point(1, 1),
                new ClosestPair.Point(4, 5),
                new ClosestPair.Point(2, 2)
        };
        double result = ClosestPair.findClosestPair(points, metrics);
        double expected = Math.min(
                points[0].distanceTo(points[2]),
                Math.min(points[0].distanceTo(points[1]), points[1].distanceTo(points[2]))
        );
        assertEquals(expected, result, 1e-9);
        assertTrue(metrics.getComparisons() > 0);
    }

    @Test
    void testClosestPairHorizontalLine() {
        ClosestPair.Point[] points = {
                new ClosestPair.Point(1, 5),
                new ClosestPair.Point(3, 5),
                new ClosestPair.Point(6, 5),
                new ClosestPair.Point(8, 5)
        };
        double result = ClosestPair.findClosestPair(points, metrics);
        double expected = 2.0; // Distance between (1,5) and (3,5)
        assertEquals(expected, result, 1e-9);
    }

    @Test
    void testClosestPairVerticalLine() {
        ClosestPair.Point[] points = {
                new ClosestPair.Point(5, 1),
                new ClosestPair.Point(5, 3),
                new ClosestPair.Point(5, 6),
                new ClosestPair.Point(5, 8)
        };
        double result = ClosestPair.findClosestPair(points, metrics);
        double expected = 2.0; // Distance between (5,1) and (5,3)
        assertEquals(expected, result, 1e-9);
    }

    @Test
    void testClosestPairDiagonalPoints() {
        ClosestPair.Point[] points = {
                new ClosestPair.Point(1, 1),
                new ClosestPair.Point(2, 2),
                new ClosestPair.Point(5, 5),
                new ClosestPair.Point(6, 6)
        };
        double result = ClosestPair.findClosestPair(points, metrics);
        double expected = Math.sqrt(2); // Distance between (1,1)-(2,2) or (5,5)-(6,6)
        assertEquals(expected, result, 1e-9);
    }

    @Test
    void testClosestPairCrossStrip() {
        // Points where closest pair crosses the dividing line
        ClosestPair.Point[] points = {
                new ClosestPair.Point(1, 1),
                new ClosestPair.Point(4, 4),
                new ClosestPair.Point(5, 1),  // Close to (1,1) across the divide
                new ClosestPair.Point(8, 4)
        };
        double result = ClosestPair.findClosestPair(points, metrics);
        double expected = points[0].distanceTo(points[2]); // Distance between (1,1) and (5,1)
        assertEquals(expected, result, 1e-9);
    }

    @Test
    void testClosestPairLargeDistance() {
        ClosestPair.Point[] points = {
                new ClosestPair.Point(0, 0),
                new ClosestPair.Point(100, 100),
                new ClosestPair.Point(200, 200)
        };
        double result = ClosestPair.findClosestPair(points, metrics);
        double expected = Math.sqrt(100*100 + 100*100); // Distance between consecutive points
        assertEquals(expected, result, 1e-9);
    }

    @Test
    void testClosestPairDuplicatePoints() {
        ClosestPair.Point[] points = {
                new ClosestPair.Point(1, 1),
                new ClosestPair.Point(1, 1), // Duplicate point
                new ClosestPair.Point(3, 3),
                new ClosestPair.Point(4, 4)
        };
        double result = ClosestPair.findClosestPair(points, metrics);
        assertEquals(0.0, result, 1e-9); // Distance between duplicate points is 0
    }

    @Test
    void testClosestPairSmallRandomSet() {
        ClosestPair.Point[] points = {
                new ClosestPair.Point(2, 3),
                new ClosestPair.Point(12, 30),
                new ClosestPair.Point(40, 50),
                new ClosestPair.Point(5, 1),
                new ClosestPair.Point(12, 10),
                new ClosestPair.Point(3, 4)
        };

        double result = ClosestPair.findClosestPair(points, metrics);
        double bruteForceResult = bruteForceClosestPair(points);

        assertEquals(bruteForceResult, result, 1e-9);
    }

    @Test
    void testClosestPairRecursionDepth() {
        int n = 16;
        ClosestPair.Point[] points = generateRandomPoints(n, 42);

        ClosestPair.findClosestPair(points, metrics);

        // Depth should be O(log n)
        int maxExpectedDepth = (int) (2 * (Math.log(n) / Math.log(2)) + 5);
        assertTrue(metrics.getMaxRecursionDepth() <= maxExpectedDepth,
                "Depth: " + metrics.getMaxRecursionDepth() + " should be <= " + maxExpectedDepth);
    }

    @Test
    void testClosestPairComparisonWithBruteForce() {
        // Test on small sets where brute force is feasible
        for (int n = 4; n <= 10; n++) {
            ClosestPair.Point[] points = generateRandomPoints(n, n);

            double algorithmResult = ClosestPair.findClosestPair(points, new Metrics());
            double bruteForceResult = bruteForceClosestPair(points);

            assertEquals(bruteForceResult, algorithmResult, 1e-9,
                    "Mismatch for n=" + n);
        }
    }

    @Test
    void testClosestPairStripBehavior() {
        // Create points where the closest pair will be found in the strip
        ClosestPair.Point[] points = {
                new ClosestPair.Point(1, 10),
                new ClosestPair.Point(2, 15),
                new ClosestPair.Point(3, 5),   // Left side
                new ClosestPair.Point(4, 12),
                new ClosestPair.Point(6, 11),  // Right side - close to (4,12)
                new ClosestPair.Point(7, 20),
                new ClosestPair.Point(8, 8)
        };

        double result = ClosestPair.findClosestPair(points, metrics);
        double expected = points[3].distanceTo(points[4]); // These should be closest
        assertEquals(expected, result, 1e-9);
    }

    @Test
    void testClosestPairSevenPointLimit() {
        // Test that the algorithm correctly limits to 7 comparisons in strip
        ClosestPair.Point[] points = new ClosestPair.Point[20];
        // Create points in a vertical strip with increasing y
        for (int i = 0; i < 20; i++) {
            points[i] = new ClosestPair.Point(5.0 + (i % 2 == 0 ? 0.1 : -0.1), i * 1.0);
        }

        double result = ClosestPair.findClosestPair(points, metrics);
        double expected = 0.2; // Minimum distance between points at same x with small offset

        // The algorithm should still find the correct minimum despite the 7-point limit
        // because points are spaced vertically
        assertEquals(expected, result, 1e-9);
    }

    @Test
    void testClosestPairAllPointsInStrip() {
        // All points are within the strip width
        ClosestPair.Point[] points = {
                new ClosestPair.Point(4.9, 1),
                new ClosestPair.Point(5.0, 2),
                new ClosestPair.Point(5.1, 3),
                new ClosestPair.Point(5.0, 4),
                new ClosestPair.Point(4.9, 5)
        };

        double result = ClosestPair.findClosestPair(points, metrics);
        double bruteForceResult = bruteForceClosestPair(points);
        assertEquals(bruteForceResult, result, 1e-9);
    }

    @Test
    void testClosestPairLargeDataset() {
        int n = 1000;
        ClosestPair.Point[] points = generateRandomPoints(n, 42);

        // For large n, we can't use brute force, but we can verify the algorithm runs
        // and returns a reasonable result without throwing exceptions
        double result = ClosestPair.findClosestPair(points, metrics);

        assertTrue(result >= 0, "Distance should be non-negative");
        assertTrue(Double.isFinite(result), "Distance should be finite");

        // Verify O(n log n) behavior through comparisons count
        assertTrue(metrics.getComparisons() <= 20 * n * Math.log(n) / Math.log(2),
                "Comparisons should be O(n log n)");
    }

    @Test
    void testClosestPairPrecision() {
        // Test with very close points to check floating point precision
        ClosestPair.Point[] points = {
                new ClosestPair.Point(1.0000000001, 1.0000000001),
                new ClosestPair.Point(1.0000000002, 1.0000000002),
                new ClosestPair.Point(2.0, 2.0),
                new ClosestPair.Point(3.0, 3.0)
        };

        double result = ClosestPair.findClosestPair(points, metrics);
        double expected = points[0].distanceTo(points[1]);
        assertEquals(expected, result, 1e-12);
    }

    @Test
    void testClosestPairNegativeCoordinates() {
        ClosestPair.Point[] points = {
                new ClosestPair.Point(-5, -5),
                new ClosestPair.Point(-3, -3),
                new ClosestPair.Point(-1, -1),
                new ClosestPair.Point(-4, -6)
        };

        double result = ClosestPair.findClosestPair(points, metrics);
        double bruteForceResult = bruteForceClosestPair(points);
        assertEquals(bruteForceResult, result, 1e-9);
    }

    @Test
    void testClosestPairMixedCoordinates() {
        ClosestPair.Point[] points = {
                new ClosestPair.Point(-2, 3),
                new ClosestPair.Point(1, -4),
                new ClosestPair.Point(0, 0),
                new ClosestPair.Point(-1, -1),
                new ClosestPair.Point(5, 2)
        };

        double result = ClosestPair.findClosestPair(points, metrics);
        double bruteForceResult = bruteForceClosestPair(points);
        assertEquals(bruteForceResult, result, 1e-9);
    }

    @Test
    void testClosestPairArrayNotModified() {
        ClosestPair.Point[] original = {
                new ClosestPair.Point(1, 1),
                new ClosestPair.Point(2, 2),
                new ClosestPair.Point(3, 3)
        };
        ClosestPair.Point[] points = original.clone();

        ClosestPair.findClosestPair(points, metrics);

        // The points array should not be modified (except for internal sorting)
        assertEquals(original.length, points.length);
        for (int i = 0; i < original.length; i++) {
            assertEquals(original[i].x, points[i].x, 1e-9);
            assertEquals(original[i].y, points[i].y, 1e-9);
        }
    }

    // Helper method to generate random points
    private ClosestPair.Point[] generateRandomPoints(int n, long seed) {
        Random random = new Random(seed);
        ClosestPair.Point[] points = new ClosestPair.Point[n];
        for (int i = 0; i < n; i++) {
            points[i] = new ClosestPair.Point(
                    random.nextDouble() * 100,
                    random.nextDouble() * 100
            );
        }
        return points;
    }

    // Helper method for brute force closest pair (for validation)
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