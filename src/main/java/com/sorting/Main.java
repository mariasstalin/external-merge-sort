package com.sorting;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Main {
	
	private static int DEFAULT_CHUNK_SIZE = 64 * 1024 * 1024;
	
	private static int HEAP_SIZE = (int)(float)(1024 * 1024 * 1024 * 0.9); // 90% of 1GB

	public static void main(String[] args) throws Exception {

		String inputFilePath = "D:/projects/file-sorting/files/input.bin"; // Input binary file can be generated using RandomNumbersFileGenerator.java

		String directoryPath = "D:/projects/file-sorting/files";

		int numberOfThreads = Runtime.getRuntime().availableProcessors();
		int approxChunkSize = HEAP_SIZE / (numberOfThreads * Integer.BYTES);
		if(approxChunkSize % 4 != 0) {
			approxChunkSize -= approxChunkSize % 4;
		}
		int chunkSize = Math.min(DEFAULT_CHUNK_SIZE, approxChunkSize);
		
		File inputFile = new File(inputFilePath);
		long fileSize = inputFile.length();

		AtomicInteger chunkCount = new AtomicInteger(0);
		AtomicLong atomicOffset = new AtomicLong(0);
		try (IntChunkReader chunkInputReader = new IntChunkReader(inputFilePath)) {
			List<Callable<Boolean>> readAndSortCallables = new ArrayList<Callable<Boolean>>();
			while (atomicOffset.get() < fileSize) {
				long offset = atomicOffset.get();
				readAndSortCallables.add(() -> {
					int[] array = chunkInputReader.read(offset, chunkSize);
					HeapSort.sort(array);
					try (IntChunkWriter chunkWriter = new IntChunkWriter(directoryPath + "/chunks", chunkCount.incrementAndGet())) {
						chunkWriter.write(array);
					}
					
					System.out.println("Chunk-" + chunkCount + " created.");
					return true;
				});
				atomicOffset.addAndGet(chunkSize);
			}
			
			ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
			List<Future<Boolean>> futures = executorService.invokeAll(readAndSortCallables);
			for(Future<Boolean> future:futures) {
				if(!future.get()) {
					throw new RuntimeException("Error while reading and sorting the chunks.");
				}
			}
			executorService.shutdown();
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("Merging chunks and generating output file...");
		
		int bufferSize = (int)((HEAP_SIZE / chunkCount.get()) * 0.5);
		
		MinHeap<IntChunkReaderContext> minHeap = new MinHeap<IntChunkReaderContext>(chunkCount.get());
		for (int i = 1; i <= chunkCount.get(); i++) {
			IntChunkReaderContext intChunkReader = new IntChunkReaderContext(directoryPath + "/chunks/chunk-" + i + ".bin", bufferSize);
			if (intChunkReader.hasNext()) {
				intChunkReader.next();
			}
			minHeap.add(intChunkReader);
		}
		
		int mergeBufferSize = 64 * 1024 * 1024;
		
		try (IntChunkMerger chunkMerger = new IntChunkMerger(directoryPath + "/output.bin", mergeBufferSize)) {
			while (!minHeap.isEmpty()) {
				IntChunkReaderContext intChunkReader = minHeap.poll();
				chunkMerger.merge(intChunkReader.currentValue());
				if (intChunkReader.hasNext()) {
					intChunkReader.next();
					minHeap.add(intChunkReader);
				} else {
					intChunkReader.close();
				}
			}
			System.out.println("Output generated: " + directoryPath + "/output.bin");
		}

	}

}
