# Measured results

Each mean and sample standard deviation uses five measured repetitions. Counts are identical across all five repetitions; they are totals per workload run, not per operation. Times are milliseconds. Theory describes the entire timed workload; m is shown explicitly.

## Random access

| Structure | n | m | Mean ms | SD ms | Accesses | Movements | Comparisons | Batches | Workload theory |
|---|---:|---:|---:|---:|---:|---:|---:|---:|---|
| DynamicArray | 100 | 10,000 | 0.071600 | 0.043581 | 10,000 | 0 | 0 | 1 | Theta(m) |
| LinkedList | 100 | 10,000 | 0.451700 | 0.062261 | 511,327 | 0 | 0 | 1 | Theta(mn) expected |
| DynamicArray | 1,000 | 10,000 | 0.050020 | 0.016781 | 10,000 | 0 | 0 | 1 | Theta(m) |
| LinkedList | 1,000 | 10,000 | 5.403840 | 0.515878 | 5,021,262 | 0 | 0 | 1 | Theta(mn) expected |
| DynamicArray | 10,000 | 10,000 | 0.028580 | 0.034281 | 10,000 | 0 | 0 | 1 | Theta(m) |
| LinkedList | 10,000 | 10,000 | 60.405340 | 5.190401 | 50,188,951 | 0 | 0 | 1 | Theta(mn) expected |
| DynamicArray | 100,000 | 10,000 | 0.016580 | 0.008460 | 10,000 | 0 | 0 | 1 | Theta(m) |
| LinkedList | 100,000 | 10,000 | 677.218600 | 31.119612 | 504,940,938 | 0 | 0 | 1 | Theta(mn) expected |

## Search

| Structure | n | m | Mean ms | SD ms | Accesses | Movements | Comparisons | Batches | Workload theory |
|---|---:|---:|---:|---:|---:|---:|---:|---:|---|
| DynamicArray | 100 | 1,000 | 0.055540 | 0.025948 | 75,130 | 0 | 75,130 | 1 | Theta(mn) expected |
| LinkedList | 100 | 1,000 | 0.128340 | 0.038621 | 75,130 | 0 | 75,130 | 1 | Theta(mn) expected |
| DynamicArray | 1,000 | 1,000 | 0.260500 | 0.048583 | 751,127 | 0 | 751,127 | 1 | Theta(mn) expected |
| LinkedList | 1,000 | 1,000 | 0.916280 | 0.111462 | 751,127 | 0 | 751,127 | 1 | Theta(mn) expected |
| DynamicArray | 10,000 | 1,000 | 2.636200 | 0.713809 | 7,451,537 | 0 | 7,451,537 | 1 | Theta(mn) expected |
| LinkedList | 10,000 | 1,000 | 8.827180 | 0.872234 | 7,451,537 | 0 | 7,451,537 | 1 | Theta(mn) expected |
| DynamicArray | 100,000 | 1,000 | 24.265640 | 6.154817 | 74,711,244 | 0 | 74,711,244 | 1 | Theta(mn) expected |
| LinkedList | 100,000 | 1,000 | 114.873480 | 20.729379 | 74,711,244 | 0 | 74,711,244 | 1 | Theta(mn) expected |

## Front insertion

| Structure | n | m | Mean ms | SD ms | Accesses | Movements | Comparisons | Batches | Workload theory |
|---|---:|---:|---:|---:|---:|---:|---:|---:|---|
| DynamicArray | 100 | 1,000 | 0.438960 | 0.840738 | 601,420 | 601,420 | 0 | 1 | Theta(mn + m^2) |
| LinkedList | 100 | 1,000 | 0.023160 | 0.007229 | 0 | 0 | 0 | 1 | Theta(m) |
| DynamicArray | 1,000 | 1,000 | 0.087020 | 0.003008 | 1,500,524 | 1,500,524 | 0 | 1 | Theta(mn + m^2) |
| LinkedList | 1,000 | 1,000 | 0.007200 | 0.002316 | 0 | 0 | 0 | 1 | Theta(m) |
| DynamicArray | 10,000 | 1,000 | 0.512880 | 0.206705 | 10,499,500 | 10,499,500 | 0 | 1 | Theta(mn + m^2) |
| LinkedList | 10,000 | 1,000 | 0.045060 | 0.059013 | 0 | 0 | 0 | 1 | Theta(m) |
| DynamicArray | 100,000 | 1,000 | 5.507140 | 0.154635 | 100,499,500 | 100,499,500 | 0 | 1 | Theta(mn + m^2) |
| LinkedList | 100,000 | 1,000 | 0.003840 | 0.000826 | 0 | 0 | 0 | 1 | Theta(m) |

## Front removal

| Structure | n | m | Mean ms | SD ms | Accesses | Movements | Comparisons | Batches | Workload theory |
|---|---:|---:|---:|---:|---:|---:|---:|---:|---|
| DynamicArray | 100 | 1,000 | 0.028580 | 0.005752 | 50,500 | 49,500 | 0 | 10 | Theta(mn) under batching rule |
| LinkedList | 100 | 1,000 | 0.021780 | 0.003596 | 1,000 | 0 | 0 | 10 | Theta(m) |
| DynamicArray | 1,000 | 1,000 | 0.043400 | 0.016738 | 500,500 | 499,500 | 0 | 1 | Theta(mn) under batching rule |
| LinkedList | 1,000 | 1,000 | 0.004660 | 0.000329 | 1,000 | 0 | 0 | 1 | Theta(m) |
| DynamicArray | 10,000 | 1,000 | 0.441460 | 0.037453 | 9,500,500 | 9,499,500 | 0 | 1 | Theta(mn) under batching rule |
| LinkedList | 10,000 | 1,000 | 0.016560 | 0.013736 | 1,000 | 0 | 0 | 1 | Theta(m) |
| DynamicArray | 100,000 | 1,000 | 7.440560 | 1.140473 | 99,500,500 | 99,499,500 | 0 | 1 | Theta(mn) under batching rule |
| LinkedList | 100,000 | 1,000 | 0.002660 | 0.000391 | 1,000 | 0 | 0 | 1 | Theta(m) |

## Middle insertion

| Structure | n | m | Mean ms | SD ms | Accesses | Movements | Comparisons | Batches | Workload theory |
|---|---:|---:|---:|---:|---:|---:|---:|---:|---|
| DynamicArray | 100 | 1,000 | 0.088420 | 0.031700 | 551,420 | 551,420 | 0 | 1 | Theta(mn + m^2) |
| LinkedList | 100 | 1,000 | 0.065420 | 0.024359 | 50,000 | 0 | 0 | 1 | Theta(mn) |
| DynamicArray | 1,000 | 1,000 | 0.077840 | 0.021141 | 1,000,524 | 1,000,524 | 0 | 1 | Theta(mn + m^2) |
| LinkedList | 1,000 | 1,000 | 0.510160 | 0.003055 | 500,000 | 0 | 0 | 1 | Theta(mn) |
| DynamicArray | 10,000 | 1,000 | 0.235640 | 0.049553 | 5,499,500 | 5,499,500 | 0 | 1 | Theta(mn + m^2) |
| LinkedList | 10,000 | 1,000 | 6.503260 | 1.967996 | 5,000,000 | 0 | 0 | 1 | Theta(mn) |
| DynamicArray | 100,000 | 1,000 | 2.873960 | 0.357913 | 50,499,500 | 50,499,500 | 0 | 1 | Theta(mn + m^2) |
| LinkedList | 100,000 | 1,000 | 73.879140 | 6.378436 | 50,000,000 | 0 | 0 | 1 | Theta(mn) |

## Middle removal

| Structure | n | m | Mean ms | SD ms | Accesses | Movements | Comparisons | Batches | Workload theory |
|---|---:|---:|---:|---:|---:|---:|---:|---:|---|
| DynamicArray | 100 | 1,000 | 0.025020 | 0.005945 | 25,500 | 24,500 | 0 | 20 | Theta(mn) under batching rule |
| LinkedList | 100 | 1,000 | 0.062640 | 0.024865 | 51,000 | 0 | 0 | 20 | Theta(mn) under batching rule |
| DynamicArray | 1,000 | 1,000 | 0.025660 | 0.001297 | 250,500 | 249,500 | 0 | 2 | Theta(mn) under batching rule |
| LinkedList | 1,000 | 1,000 | 0.507980 | 0.005763 | 501,000 | 0 | 0 | 2 | Theta(mn) under batching rule |
| DynamicArray | 10,000 | 1,000 | 0.205360 | 0.013574 | 4,500,500 | 4,499,500 | 0 | 1 | Theta(mn) under batching rule |
| LinkedList | 10,000 | 1,000 | 7.510700 | 1.522482 | 5,001,000 | 0 | 0 | 1 | Theta(mn) under batching rule |
| DynamicArray | 100,000 | 1,000 | 3.665300 | 0.581543 | 49,500,500 | 49,499,500 | 0 | 1 | Theta(mn) under batching rule |
| LinkedList | 100,000 | 1,000 | 67.799820 | 6.666932 | 50,001,000 | 0 | 0 | 1 | Theta(mn) under batching rule |

## Heap insertion

| Structure | n | m | Mean ms | SD ms | Accesses | Movements | Comparisons | Batches | Workload theory |
|---|---:|---:|---:|---:|---:|---:|---:|---:|---|
| MinHeap | 100 | 100 | 0.010140 | 0.008612 | 0 | 0 | 194 | 1 | Theta(n) expected random distinct order; O(n log n) worst |
| MinHeap | 1,000 | 1,000 | 0.034020 | 0.007870 | 0 | 0 | 2,232 | 1 | Theta(n) expected random distinct order; O(n log n) worst |
| MinHeap | 10,000 | 10,000 | 0.240000 | 0.060112 | 0 | 0 | 22,593 | 1 | Theta(n) expected random distinct order; O(n log n) worst |
| MinHeap | 100,000 | 100,000 | 1.268260 | 0.069865 | 0 | 0 | 227,662 | 1 | Theta(n) expected random distinct order; O(n log n) worst |

## Heap extraction

| Structure | n | m | Mean ms | SD ms | Accesses | Movements | Comparisons | Batches | Workload theory |
|---|---:|---:|---:|---:|---:|---:|---:|---:|---|
| MinHeap | 100 | 100 | 0.019420 | 0.021500 | 0 | 0 | 841 | 1 | O(n log n) |
| MinHeap | 1,000 | 1,000 | 0.050060 | 0.002291 | 0 | 0 | 14,994 | 1 | O(n log n) |
| MinHeap | 10,000 | 10,000 | 0.656680 | 0.044061 | 0 | 0 | 216,736 | 1 | O(n log n) |
| MinHeap | 100,000 | 100,000 | 7.809360 | 0.430656 | 0 | 0 | 2,831,463 | 1 | O(n log n) |
