package com.sorting;

import java.io.Closeable;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Iterator;

public class IntChunkReaderContext implements Closeable, Comparable<IntChunkReaderContext>, Iterator<Integer> {

	private FileInputStream fis;

	private FileChannel fileChannel;

	private ByteBuffer buffer;

	private Integer value;

	public IntChunkReaderContext(String inputFile, int chunkSize) throws IOException {
		fis = new FileInputStream(inputFile);
		fileChannel = fis.getChannel();
		buffer = ByteBuffer.allocate(chunkSize);
		buffer.flip();
	}

	public int currentValue() {
		return value;
	}

	@Override
	public boolean hasNext() {
		try {
			return buffer.hasRemaining() || fileChannel.position() < fileChannel.size();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Integer next() {
		try {
			while (buffer.remaining() < Integer.BYTES && fileChannel.position() < fileChannel.size()) {
				buffer.compact();
				int bytesRead = fileChannel.read(buffer);
				if (bytesRead <= 0) {
					break;
				}
				buffer.flip();
			}
			if (buffer.remaining() >= Integer.BYTES) {
				value = buffer.getInt();
				return value;
			} else {
				throw new RuntimeException("Incomplete data at the end of the file");
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public int compareTo(IntChunkReaderContext other) {
		return Integer.compare(this.value, other.value);
	}

	@Override
	public void close() throws IOException {
		fileChannel.close();
		fis.close();
	}

}
