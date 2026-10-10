package com.sorting;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.junit.jupiter.api.Test;

public class MainTest {

	@Test
	public void testFileLength() {
		File inputFile = new File("D:/projects/file-sorting/files/input.bin");
		File outputFile = new File("D:/projects/file-sorting/files/output.bin");

		assertEquals(inputFile.length(), outputFile.length());
	}

	@Test
	public void testSorted() throws Exception {
		File outputFile = new File("D:/projects/file-sorting/files/output.bin");
		try (FileInputStream fis = new FileInputStream(outputFile); FileChannel fileChannel = fis.getChannel()) {
			ByteBuffer buffer = ByteBuffer.allocate(64 * 1024 * 1024);
			Integer previousValue = null;
			while (fileChannel.position() < fileChannel.size()) {
				buffer.clear();
				int bytesRead = fileChannel.read(buffer);
				if (bytesRead <= 0) {
					break;
				}
				buffer.flip();

				while (buffer.remaining() >= Integer.BYTES) {
					int currentValue = buffer.getInt();
					if (previousValue != null && currentValue < previousValue) {
						throw new RuntimeException("File is not sorted in ascending order");
					}

					previousValue = currentValue;
				}
			}
		}
		System.out.println("The file is sorted in ascending order.");
	}

}
