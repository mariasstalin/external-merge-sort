# High-Performance External Merge Sort Engine in Java

A high-performance implementation of an **External Merge Sort** algorithm built from scratch in Java. This project is engineered to solve a fundamental systems challenge: **how to efficiently sort massive datasets (e.g., 10GB+) when available system RAM is strictly limited (e.g., 1GB)**, thereby avoiding `OutOfMemoryError` exceptions.

---

## 🚀 Key Features

- **Bounded Memory Utilization:** Processes large files by breaking them down into manageable, memory-safe streams instead of loading entire files into the heap.
- **Java NIO Optimization:** Leverages low-level `FileChannel` and `ByteBuffer` for high-throughput, low-latency binary file I/O.
- **Concurrent Chunk Processing:** Utilizes Java's `ExecutorService` thread pool to parallelize CPU-bound read-and-sort operations across available processor cores.
- **Zero External Dependencies:** Built completely from scratch using core Java libraries, including custom-crafted data structures and sorting algorithms.
- **K-Way External Merge:** Coordinates multiple file streams simultaneously using a custom generic min-heap data structure.

---

## 🏗️ Architecture & Workflow

The sorting process is executed in two primary phases:

### Phase 1: Parallel Chunk Splitting & Sorting
1. **Generation:** `RandomNumbersFileGenerator` populates a target binary file with raw 32-bit integers.
2. **Chunking & Reading:** `Main` computes optimal chunk sizes based on available heap constraints and uses `IntChunkReader` to load data segments.
3. **Parallel In-Memory Sort:** Each chunk is independently sorted using a custom implementation of `HeapSort` in parallel threads.
4. **Flushing:** Sorted chunks are written out to disk as individual binary files (`chunk-X.bin`) via `IntChunkWriter`.

### Phase 2: K-Way External Merge
1. **Context Initialization:** Each temporary chunk file is wrapped in an `IntChunkReaderContext` which implements `Iterator` and `Comparable`.
2. **Min-Heap Coordination:** A custom generic `MinHeap` tracks the current smallest element across all active chunk streams.
3. **Buffered Merging:** The engine polls the minimum element from the heap, routes it through `IntChunkMerger`, and flushes the fully sorted sequence to the final output file.

---

## 📂 Project Structure

```text
file-sorting/
│
├── com/sorting/
│   ├── HeapSort.java              # In-place heap sort implementation for primitive int arrays
│   ├── IntChunkMerger.java        # Handles writing and buffering the final merged sorted output
│   ├── IntChunkReader.java        # Low-level NIO reader for reading binary chunks by offset
│   ├── IntChunkReaderContext.java # Stream context implementing Iterator and Comparable for K-way merge
│   ├── IntChunkWriter.java        # Writes sorted integer arrays out to temporary chunk files
│   ├── Main.java                  # Core orchestrator managing concurrency and the two-phase workflow
│   ├── MinHeap.java               # Generic min-heap data structure built from scratch
│   └── RandomNumbersFileGenerator.java # Utility to generate large test binary files
```

---

## 📊 Algorithmic Complexity

- **Time Complexity:** $\mathcal{O}(N \log N)$ where $N$ is the total number of elements. Each element is read, sorted in chunks, written to disk, read back, and merged via the min-heap.
- **Space Complexity:** $\mathcal{O}(M)$ where $M$ represents the maximum heap memory limit allocated for chunks and merge buffers, strictly bounded independently of file size $N$.

---

## 💻 Getting Started

### Prerequisites
- Java Development Kit (JDK) 11 or higher.

### 1. Configure File Paths
Open `RandomNumbersFileGenerator.java` and `Main.java`, and update the `inputFilePath` and `directoryPath` variables to match your local file system directories.

### 2. Generate a Test Dataset
Run `RandomNumbersFileGenerator` to create a large binary input file (configured by default for 10GB):
```bash
java com.sorting.RandomNumbersFileGenerator
```

### 3. Run the Sorting Engine
Execute `Main` to trigger multi-threaded chunk sorting and the external merge phase:
```bash
java com.sorting.Main
```

---

## 🛠️ Tech Stack
- **Language:** Java
- **Core APIs:** Java NIO (`java.nio.channels.FileChannel`, `java.nio.ByteBuffer`), Concurrency (`java.util.concurrent.*`)
