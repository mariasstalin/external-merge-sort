package com.sorting;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RandomNumbersFileGenerator {

	public static void main(String[] args) throws Exception {

		final long targetFileSize = 10L * 1024 * 1024 * 1024; // 10GB
		final String outputFilePath = "D:/projects/file-sorting/files/input.bin";
		final int bufferSize = 64 * 1024 * 1024; // 64MB buffer

		try (FileOutputStream fos = new FileOutputStream(outputFilePath); FileChannel fileChannel = fos.getChannel()) {
			long bytesWritten = 0;
			Random random = new Random();
			while (bytesWritten < targetFileSize) {
				ByteBuffer buffer = ByteBuffer.allocateDirect(bufferSize);
				for (int i = 0; i < bufferSize / Integer.BYTES; i++) {
					buffer.putInt(random.nextInt(Integer.MAX_VALUE - 1) + 1);
				}
				buffer.flip();
				fileChannel.write(buffer);
				bytesWritten += buffer.capacity();
				buffer.clear();
				
				System.out.println("Bytes Written: " + bytesWritten);
			}

			System.out.println("File generation complete: " + outputFilePath);

		} catch (Exception e) {
			System.err.println("Error generating the file: " + e.getMessage());
		}
	}

}
