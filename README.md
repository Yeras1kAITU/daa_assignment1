# Divide-and-Conquer Algorithms Analysis

## Architecture Notes

### Recursion Depth Control
- **MergeSort**: Natural depth Θ(log n) from balanced splitting
- **QuickSort**: Smaller-partition-first recursion ensures O(log n) depth
- **Deterministic Select**: Recursion only on needed partition with median-of-medians pivot
- **Closest Pair**: Balanced splitting gives Θ(log n) depth

### Memory Management
- MergeSort uses reusable buffer to minimize allocations
- QuickSort and Select operate in-place
- Closest Pair uses O(n) auxiliary space for strip processing

## Recurrence Analysis

### 1. MergeSort
**Recurrence**: T(n) = 2T(n/2) + Θ(n)  
**Master Theorem**: Case 2 (a=2, b=2, f(n)=Θ(n))  
**Solution**: T(n) = Θ(n log n)

### 2. QuickSort
**Expected Recurrence**: T(n) = T(n/2) + T(n/2) + Θ(n) = 2T(n/2) + Θ(n)  
**Akra-Bazzi Intuition**: Randomized pivot gives balanced splits in expectation  
**Solution**: E[T(n)] = Θ(n log n)

### 3. Deterministic Select
**Recurrence**: T(n) ≤ T(n/5) + T(7n/10) + Θ(n)  
**Akra-Bazzi Intuition**: p satisfies (1/5)^p + (7/10)^p = 1 ⇒ p < 1  
**Solution**: T(n) = Θ(n)

### 4. Closest Pair
**Recurrence**: T(n) = 2T(n/2) + Θ(n)  
**Master Theorem**: Case 2 (a=2, b=2, f(n)=Θ(n))  
**Solution**: T(n) = Θ(n log n)

## Performance Measurements

Compile by: `javac cli\ExperimentRunner.java algorithms\MergeSort.java algorithms\QuickSort.java metrics\*.java`
Run experiments with: `java cli.ExperimentRunner 10000 results.csv`

Expected results:
- MergeSort: Stable Θ(n log n) time, moderate constant factors
- QuickSort: Faster Θ(n log n) with better cache locality
- Deterministic Select: Θ(n) but with larger constant factors
- Closest Pair: Θ(n log n) with significant constant factors

## Constant Factor Effects

**Cache Effects**: QuickSort benefits from locality  
**GC Overhead**: MergeSort has predictable allocation patterns  
**Branch Prediction**: QuickSort's random access may hurt prediction

## Benchmarking with JMH

### Running Benchmarks

```bash
# Run all benchmarks
mvn clean package
java -jar target/benchmarks.jar

# Run specific benchmark
mvn exec:java -Dexec.mainClass="org.openjdk.jmh.Main" -Dexec.args="SelectVsSortBenchmark"

# Run with specific parameters
mvn exec:java -Dexec.mainClass="org.openjdk.jmh.Main" -Dexec.args=".* -f 2 -wi 3 -i 5"