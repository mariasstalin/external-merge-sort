package com.sorting;

import java.io.Closeable;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class IntChunkReader implements Closeable {

	private RandomAccessFile raf;

	private FileChannel fileChannel;

	public IntChunkReader(String inputFile) throws Exception {
		raf = new RandomAccessFile(inputFile, "r");
		fileChannel = raf.getChannel();

	}

	public int[] read(long offset, int bufferSize) {
		try {
			ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
			int i = 0;
			int bytesRead = fileChannel.read(buffer, offset);
			int[] array = new int[bytesRead / Integer.BYTES];
			if (bytesRead > 0) {
				buffer.flip();
				while (buffer.remaining() >= Integer.BYTES) {
					array[i++] = buffer.getInt();
				}
			}
			return array;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void close() throws IOException {
		fileChannel.close();
		raf.close();
	}

}
