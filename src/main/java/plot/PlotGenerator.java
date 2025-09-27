package plot;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;

public class PlotGenerator {

    public static void main(String[] args) {
        try {
            String csvFile;
            if (args.length > 0) {
                csvFile = args[0];
            } else {
                csvFile = "results/experimental_data.csv";
                System.out.println("No file specified, using default: " + csvFile);
            }

            // Create results directory if it doesn't exist
            Files.createDirectories(Paths.get("results"));
            Files.createDirectories(Paths.get("results/plots"));

            // Check if file exists
            if (!Files.exists(Paths.get(csvFile))) {
                System.err.println("Error: CSV file not found: " + csvFile);
                System.err.println("Please run ExperimentRunner first:");
                System.err.println("mvn exec:java \"-Dexec.mainClass=cli.ExperimentRunner\" \"-Dexec.args=10000 results/experimental_data.csv\"");
                return;
            }

            System.out.println("Generating plots from: " + csvFile);
            generatePlots(csvFile);
            System.out.println("Plot generation completed! Check results/plots/ directory.");

        } catch (Exception e) {
            System.err.println("Error generating plots: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void generatePlots(String csvFile) throws IOException {
        List<DataPoint> data = loadData(csvFile);

        System.out.println("Loaded " + data.size() + " data points");

        Map<String, List<DataPoint>> byAlgorithm = data.stream()
                .collect(Collectors.groupingBy(d -> d.algorithm));

        System.out.println("Algorithms found: " + byAlgorithm.keySet());
        for (String algo : byAlgorithm.keySet()) {
            System.out.println("  " + algo + ": " + byAlgorithm.get(algo).size() + " data points");
        }

        generateTimePlot(data);
        generateDepthPlot(data);
        generateComparisonsPlot(data);
        generateSelectVsSortPlot(data);
        generateAllocationsPlot(data);

        System.out.println("Generated 5 plots in results/plots/ directory");
    }

    private static List<DataPoint> loadData(String csvFile) throws IOException {
        return Files.lines(Paths.get(csvFile))
                .skip(1) // Skip header
                .map(line -> {
                    try {
                        String[] parts = line.split(",");
                        return new DataPoint(
                                Integer.parseInt(parts[0]),
                                parts[1],
                                Long.parseLong(parts[2]),
                                Integer.parseInt(parts[3]),
                                Integer.parseInt(parts[4]),
                                Integer.parseInt(parts[5])
                        );
                    } catch (Exception e) {
                        System.err.println("Error parsing line: " + line);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private static void generateTimePlot(List<DataPoint> data) {
        Plotter plotter = new Plotter(800, 600);
        plotter.setTitle("Time Complexity Analysis");
        plotter.setXLabel("Input Size (n)");
        plotter.setYLabel("Time (nanoseconds)");
        plotter.setLogScale(true, true);

        Map<String, List<DataPoint>> byAlgorithm = data.stream()
                .collect(Collectors.groupingBy(d -> d.algorithm));

        for (Map.Entry<String, List<DataPoint>> entry : byAlgorithm.entrySet()) {
            String algo = entry.getKey();
            List<DataPoint> points = entry.getValue();

            points.sort(Comparator.comparingInt(d -> d.n));

            double[] x = points.stream().mapToDouble(d -> d.n).toArray();
            double[] y = points.stream().mapToDouble(d -> d.timeNs).toArray();

            plotter.addSeries(algo, x, y);
        }

        plotter.savePlot("results/plots/time_complexity.png");
        System.out.println("✓ Generated time_complexity.png");
    }

    private static void generateDepthPlot(List<DataPoint> data) {
        Plotter plotter = new Plotter(800, 600);
        plotter.setTitle("Recursion Depth Analysis");
        plotter.setXLabel("Input Size (n)");
        plotter.setYLabel("Maximum Recursion Depth");
        plotter.setLogScale(true, false);

        Map<String, List<DataPoint>> byAlgorithm = data.stream()
                .collect(Collectors.groupingBy(d -> d.algorithm));

        for (Map.Entry<String, List<DataPoint>> entry : byAlgorithm.entrySet()) {
            String algo = entry.getKey();
            List<DataPoint> points = entry.getValue();

            points.sort(Comparator.comparingInt(d -> d.n));

            double[] x = points.stream().mapToDouble(d -> d.n).toArray();
            double[] y = points.stream().mapToDouble(d -> d.maxDepth).toArray();

            plotter.addSeries(algo, x, y);
        }

        plotter.savePlot("results/plots/recursion_depth.png");
        System.out.println("✓ Generated recursion_depth.png");
    }

    private static void generateComparisonsPlot(List<DataPoint> data) {
        Plotter plotter = new Plotter(800, 600);
        plotter.setTitle("Comparison Count Analysis");
        plotter.setXLabel("Input Size (n)");
        plotter.setYLabel("Number of Comparisons");
        plotter.setLogScale(true, true);

        Map<String, List<DataPoint>> byAlgorithm = data.stream()
                .collect(Collectors.groupingBy(d -> d.algorithm));

        for (Map.Entry<String, List<DataPoint>> entry : byAlgorithm.entrySet()) {
            String algo = entry.getKey();
            List<DataPoint> points = entry.getValue();

            points.sort(Comparator.comparingInt(d -> d.n));

            double[] x = points.stream().mapToDouble(d -> d.n).toArray();
            double[] y = points.stream().mapToDouble(d -> d.comparisons).toArray();

            plotter.addSeries(algo, x, y);
        }

        plotter.savePlot("results/plots/comparison_count.png");
        System.out.println("✓ Generated comparison_count.png");
    }

    private static void generateAllocationsPlot(List<DataPoint> data) {
        Plotter plotter = new Plotter(800, 600);
        plotter.setTitle("Memory Allocation Analysis");
        plotter.setXLabel("Input Size (n)");
        plotter.setYLabel("Number of Allocations");
        plotter.setLogScale(true, true);

        Map<String, List<DataPoint>> byAlgorithm = data.stream()
                .collect(Collectors.groupingBy(d -> d.algorithm));

        for (Map.Entry<String, List<DataPoint>> entry : byAlgorithm.entrySet()) {
            String algo = entry.getKey();
            List<DataPoint> points = entry.getValue();

            points.sort(Comparator.comparingInt(d -> d.n));

            double[] x = points.stream().mapToDouble(d -> d.n).toArray();
            double[] y = points.stream().mapToDouble(d -> d.allocations).toArray();

            plotter.addSeries(algo, x, y);
        }

        plotter.savePlot("results/plots/memory_allocations.png");
        System.out.println("✓ Generated memory_allocations.png");
    }

    private static void generateSelectVsSortPlot(List<DataPoint> data) {
        Plotter plotter = new Plotter(800, 600);
        plotter.setTitle("Select vs Sort Performance Comparison");
        plotter.setXLabel("Input Size (n)");
        plotter.setYLabel("Time (nanoseconds)");
        plotter.setLogScale(true, true);

        List<DataPoint> selectData = data.stream()
                .filter(d -> d.algorithm.equals("Select"))
                .sorted(Comparator.comparingInt(d -> d.n))
                .collect(Collectors.toList());

        List<DataPoint> quickSortData = data.stream()
                .filter(d -> d.algorithm.equals("QuickSort"))
                .sorted(Comparator.comparingInt(d -> d.n))
                .collect(Collectors.toList());

        List<DataPoint> mergeSortData = data.stream()
                .filter(d -> d.algorithm.equals("MergeSort"))
                .sorted(Comparator.comparingInt(d -> d.n))
                .collect(Collectors.toList());

        if (selectData.isEmpty()) {
            System.out.println("⚠  No Select data found for comparison plot");
            return;
        }

        if (!quickSortData.isEmpty()) {
            double[] x = quickSortData.stream().mapToDouble(d -> d.n).toArray();
            double[] y = quickSortData.stream().mapToDouble(d -> d.timeNs).toArray();
            plotter.addSeries("QuickSort", x, y);
        }

        if (!mergeSortData.isEmpty()) {
            double[] x = mergeSortData.stream().mapToDouble(d -> d.n).toArray();
            double[] y = mergeSortData.stream().mapToDouble(d -> d.timeNs).toArray();
            plotter.addSeries("MergeSort", x, y);
        }

        double[] xSelect = selectData.stream().mapToDouble(d -> d.n).toArray();
        double[] ySelect = selectData.stream().mapToDouble(d -> d.timeNs).toArray();
        plotter.addSeries("Deterministic Select", xSelect, ySelect);

        plotter.savePlot("results/plots/select_vs_sort.png");
        System.out.println("✓ Generated select_vs_sort.png");
    }

    static class DataPoint {
        final int n;
        final String algorithm;
        final long timeNs;
        final int comparisons;
        final int allocations;
        final int maxDepth;

        DataPoint(int n, String algorithm, long timeNs, int comparisons, int allocations, int maxDepth) {
            this.n = n;
            this.algorithm = algorithm;
            this.timeNs = timeNs;
            this.comparisons = comparisons;
            this.allocations = allocations;
            this.maxDepth = maxDepth;
        }

        @Override
        public String toString() {
            return String.format("n=%d, %s, time=%,dns, comps=%,d, allocs=%,d, depth=%d",
                    n, algorithm, timeNs, comparisons, allocations, maxDepth);
        }
    }
}

// Update the Plotter class in your PlotGenerator.java file
class Plotter {
    private String title = "Plot";
    private String xLabel = "X";
    private String yLabel = "Y";
    private boolean logX = false;
    private boolean logY = false;
    private final List<Series> seriesList = new ArrayList<>();
    private static final Color[] COLORS = {
            Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE,
            Color.MAGENTA, Color.CYAN, Color.PINK, Color.YELLOW,
            Color.BLACK, Color.GRAY
    };

    public Plotter(int width, int height) {
        // Dimensions are now handled internally
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setXLabel(String xLabel) {
        this.xLabel = xLabel;
    }

    public void setYLabel(String yLabel) {
        this.yLabel = yLabel;
    }

    public void setLogScale(boolean logX, boolean logY) {
        this.logX = logX;
        this.logY = logY;
    }

    public void addSeries(String name, double[] x, double[] y) {
        seriesList.add(new Series(name, x, y));
    }

    public void savePlot(String filename) {
        try {
            int width = 800;
            int height = 600;
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = image.createGraphics();

            // FIXED: Use compatible rendering hints
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // Remove the problematic text antialiasing hint

            // Fill background
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, width, height);

            drawPlot(g2d, width, height);

            File file = new File(filename);
            file.getParentFile().mkdirs();
            ImageIO.write(image, "png", file);
            System.out.println("✓ Saved plot to: " + filename);

            g2d.dispose();
        } catch (Exception e) {
            System.err.println("✗ Failed to save plot to " + filename + ": " + e.getMessage());
        }
    }

    private void drawPlot(Graphics2D g2d, int width, int height) {
        int margin = 80;
        int plotWidth = width - 2 * margin;
        int plotHeight = height - 2 * margin;

        // Draw title
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        drawCenteredString(g2d, title, width / 2, margin / 3);

        // Draw axes
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(margin, height - margin, margin, margin);
        g2d.drawLine(margin, height - margin, width - margin, height - margin);

        // Draw axis labels
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        drawCenteredString(g2d, xLabel, width / 2, height - margin / 3);

        // Draw Y label without rotation to avoid issues
        FontMetrics fm = g2d.getFontMetrics();
        int yLabelWidth = fm.stringWidth(yLabel);
        g2d.drawString(yLabel, margin / 2 - yLabelWidth / 2, height / 2);

        if (seriesList.isEmpty()) return;

        // Find data ranges
        double minX = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double minY = Double.MAX_VALUE;
        double maxY = Double.MIN_VALUE;

        for (Series series : seriesList) {
            for (double x : series.x) {
                double xVal = logX && x > 0 ? Math.log10(x) : x;
                minX = Math.min(minX, xVal);
                maxX = Math.max(maxX, xVal);
            }
            for (double y : series.y) {
                double yVal = logY && y > 0 ? Math.log10(y) : y;
                minY = Math.min(minY, yVal);
                maxY = Math.max(maxY, yVal);
            }
        }

        // Add padding
        double xRange = maxX - minX;
        double yRange = maxY - minY;
        minX -= xRange * 0.05;
        maxX += xRange * 0.05;
        minY -= yRange * 0.05;
        maxY += yRange * 0.05;

        // Draw grid lines
        g2d.setColor(new Color(240, 240, 240)); // Light gray
        g2d.setStroke(new BasicStroke(1));
        for (int i = 1; i <= 4; i++) {
            double x = minX + (maxX - minX) * i / 5;
            int xPos = margin + (int) ((x - minX) / (maxX - minX) * plotWidth);
            g2d.drawLine(xPos, height - margin, xPos, margin);

            double y = minY + (maxY - minY) * i / 5;
            int yPos = height - margin - (int) ((y - minY) / (maxY - minY) * plotHeight);
            g2d.drawLine(margin, yPos, width - margin, yPos);
        }

        // Draw axis values
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.PLAIN, 10));

        // X-axis values
        for (int i = 0; i <= 5; i++) {
            double xValue = minX + (maxX - minX) * i / 5;
            String label = formatAxisValue(xValue, logX);
            int xPos = margin + (int) ((xValue - minX) / (maxX - minX) * plotWidth);
            int labelWidth = fm.stringWidth(label);
            g2d.drawString(label, xPos - labelWidth / 2, height - margin + 20);
        }

        // Y-axis values
        for (int i = 0; i <= 5; i++) {
            double yValue = minY + (maxY - minY) * i / 5;
            String label = formatAxisValue(yValue, logY);
            int yPos = height - margin - (int) ((yValue - minY) / (maxY - minY) * plotHeight);
            g2d.drawString(label, margin - 40, yPos + 5);
        }

        // Draw data series
        for (int i = 0; i < seriesList.size(); i++) {
            Series series = seriesList.get(i);
            Color color = COLORS[i % COLORS.length];
            g2d.setColor(color);
            g2d.setStroke(new BasicStroke(2.5f));

            // Draw lines
            for (int j = 1; j < series.x.length; j++) {
                double x1 = logX && series.x[j-1] > 0 ? Math.log10(series.x[j-1]) : series.x[j-1];
                double y1 = logY && series.y[j-1] > 0 ? Math.log10(series.y[j-1]) : series.y[j-1];
                double x2 = logX && series.x[j] > 0 ? Math.log10(series.x[j]) : series.x[j];
                double y2 = logY && series.y[j] > 0 ? Math.log10(series.y[j]) : series.y[j];

                int xPos1 = margin + (int) ((x1 - minX) / (maxX - minX) * plotWidth);
                int yPos1 = height - margin - (int) ((y1 - minY) / (maxY - minY) * plotHeight);
                int xPos2 = margin + (int) ((x2 - minX) / (maxX - minX) * plotWidth);
                int yPos2 = height - margin - (int) ((y2 - minY) / (maxY - minY) * plotHeight);

                g2d.drawLine(xPos1, yPos1, xPos2, yPos2);
            }

            // Draw points
            g2d.setStroke(new BasicStroke(1));
            for (int j = 0; j < series.x.length; j++) {
                double x = logX && series.x[j] > 0 ? Math.log10(series.x[j]) : series.x[j];
                double y = logY && series.y[j] > 0 ? Math.log10(series.y[j]) : series.y[j];

                int xPos = margin + (int) ((x - minX) / (maxX - minX) * plotWidth);
                int yPos = height - margin - (int) ((y - minY) / (maxY - minY) * plotHeight);

                g2d.fillOval(xPos - 4, yPos - 4, 8, 8);
                g2d.setColor(Color.WHITE);
                g2d.drawOval(xPos - 4, yPos - 4, 8, 8);
                g2d.setColor(color);
            }
        }

        // Draw legend
        int legendX = width - 180;
        int legendY = margin + 20;
        g2d.setColor(new Color(255, 255, 255, 200)); // Semi-transparent white
        g2d.fillRect(legendX - 10, legendY - 10, 170, seriesList.size() * 25 + 10);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(legendX - 10, legendY - 10, 170, seriesList.size() * 25 + 10);

        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.drawString("Algorithms", legendX, legendY);

        g2d.setFont(new Font("Arial", Font.PLAIN, 11));
        for (int i = 0; i < seriesList.size(); i++) {
            Series series = seriesList.get(i);
            Color color = COLORS[i % COLORS.length];

            g2d.setColor(color);
            g2d.fillRect(legendX, legendY + 15 + i * 25, 12, 12);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(legendX, legendY + 15 + i * 25, 12, 12);
            g2d.drawString(series.name, legendX + 20, legendY + 25 + i * 25);
        }
    }

    private String formatAxisValue(double value, boolean isLog) {
        if (isLog) {
            // Convert back from log scale for display
            double actualValue = Math.pow(10, value);
            if (actualValue < 1000) {
                return String.format("%.0f", actualValue);
            } else if (actualValue < 1000000) {
                return String.format("%.0fK", actualValue / 1000);
            } else {
                return String.format("%.1fM", actualValue / 1000000);
            }
        } else {
            if (value < 1000) {
                return String.format("%.0f", value);
            } else if (value < 1000000) {
                return String.format("%.0fK", value / 1000);
            } else {
                return String.format("%.1fM", value / 1000000);
            }
        }
    }

    private void drawCenteredString(Graphics2D g2d, String text, int x, int y) {
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        g2d.drawString(text, x - textWidth / 2, y);
    }

    private static class Series {
        final String name;
        final double[] x;
        final double[] y;

        Series(String name, double[] x, double[] y) {
            this.name = name;
            this.x = x;
            this.y = y;
        }
    }
}