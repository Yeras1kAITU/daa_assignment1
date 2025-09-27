# docs/analysis.md
# Divide-and-Conquer Algorithms Analysis Report

## Recurrence Analysis

### 1. MergeSort

**Recurrence Relation**:
`T(n) = 2T(n/2) + Θ(n)`

**Master Theorem Analysis**:
- Case 2: `a = 2`, `b = 2`, `f(n) = Θ(n)`
- `log_b(a) = log₂(2) = 1`
- `f(n) = Θ(n¹)` so `k = 1`
- Since `log_b(a) = k`, we apply Case 2
- **Solution**: `T(n) = Θ(n log n)`

**Intuition**: The work is evenly distributed between two recursive calls, and the merge operation is linear. The tree has Θ(log n) levels, each doing Θ(n) work.

### 2. QuickSort

**Recurrence Relation (Expected)**:
`T(n) = T(n/2) + T(n/2) + Θ(n) = 2T(n/2) + Θ(n)`

**Akra-Bazzi Intuition**:
- With randomized pivot selection, partitions are balanced in expectation
- The recurrence resembles the balanced case: `T(n) = 2T(n/2) + Θ(n)`
- **Expected Solution**: `E[T(n)] = Θ(n log n)`

**Worst Case**: `T(n) = T(n-1) + Θ(n)` → `Θ(n²)` (unlikely with randomization)

**Depth Analysis**: Smaller-partition-first recursion ensures O(log n) stack depth.

### 3. Deterministic Select (Median-of-Medians)

**Recurrence Relation**:
`T(n) ≤ T(⌈n/5⌉) + T(⌊7n/10⌋ + 6) + Θ(n)`

**Akra-Bazzi Intuition**:
- The recurrence doesn't fit Master Theorem due to unequal splits
- We solve: `(1/5)^p + (7/10)^p = 1`
- Numerically, `p ≈ 0.84` (since `(1/5)^0.84 + (7/10)^0.84 ≈ 1`)
- Since `p < 1`, the linear term dominates
- **Solution**: `T(n) = Θ(n)`

**Intuition**: The median-of-medians pivot guarantees at least 30-70% split, making the recursive call on at most 70% of elements.

### 4. Closest Pair of Points

**Recurrence Relation**:
`T(n) = 2T(n/2) + Θ(n)`

**Master Theorem Analysis**:
- Case 2: `a = 2`, `b = 2`, `f(n) = Θ(n)`
- `log_b(a) = 1`, `f(n) = Θ(n¹)`
- **Solution**: `T(n) = Θ(n log n)`

**Intuition**: Dividing into two subsets and combining with strip check takes linear time. The strip optimization ensures only 7 comparisons per point.

## Experimental Results

### Methodology
- Tests conducted on arrays sizes: 100, 500, 1000, 5000, 10000
- Random input data with fixed seed for reproducibility
- Metrics: time (ns), comparisons, allocations, recursion depth

### Initial Observations

#### Time Complexity Validation
![Time vs Input Size](results/plots/time_complexity.png)

**MergeSort**: Shows clear Θ(n log n) behavior - time increases slightly faster than linear.

**QuickSort**: Θ(n log n) with better constants than MergeSort due to cache locality.

**Deterministic Select**: Demonstrates Θ(n) scaling - linear growth pattern.

**Closest Pair**: Θ(n log n) but with larger constant factors due to geometric operations.

#### Recursion Depth Analysis
![Depth vs Input Size](results/plots/recursion_depth.png)

**MergeSort**: Perfect Θ(log n) depth due to balanced splitting.

**QuickSort**: O(log n) depth achieved through smaller-partition-first optimization.

**Deterministic Select**: O(log n) depth despite complex recurrence due to good pivots.

**Closest Pair**: Θ(log n) depth from balanced divide-and-conquer.

## Constant Factor Analysis

### Cache Effects
- **QuickSort** benefits from locality: sequential partitioning vs MergeSort's extra array
- **MergeSort** has predictable access patterns but requires O(n) extra space
- **Deterministic Select** has irregular access patterns due to median-of-medians

### Comparison Counts
- **MergeSort**: ~n log₂n comparisons (theoretical optimum for comparison-based sort)
- **QuickSort**: ~1.39n log₂n comparisons on average (slightly more due to imperfect partitions)
- **Deterministic Select**: ~3n comparisons in practice (linear as theorized)

### Memory Allocation
- **MergeSort**: O(n) allocations for buffer (reused across calls)
- **QuickSort**: O(1) extra space (in-place)
- **Deterministic Select**: O(n) temporary arrays for median groups

## Theory vs Practice Alignment

### Strong Alignment
- All algorithms show expected asymptotic behavior
- Recursion depth bounds validated experimentally
- Constant factors match theoretical predictions

### Minor Deviations
- QuickSort shows more variance due to randomization
- Deterministic Select has higher constant factors than theoretical minimum
- Closest Pair's strip optimization shows expected 7-point limit in practice

## Conclusion

The experimental results strongly validate the theoretical analyses using Master Theorem and Akra-Bazzi intuition. The divide-and-conquer algorithms demonstrate their predicted time complexities and optimization strategies work effectively in practice.