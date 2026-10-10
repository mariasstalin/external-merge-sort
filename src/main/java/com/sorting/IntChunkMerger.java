package com.sorting;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class IntChunkMerger implements Closeable {

	private FileOutputStream fos;

	private FileChannel fileChannel;

	private ByteBuffer buffer;

	public IntChunkMerger(String filePath, int chunkSize) throws Exception {
		fos = new FileOutputStream(filePath);
		fileChannel = fos.getChannel();
		buffer = ByteBuffer.allocate(chunkSize);
	}

	public void merge(int value) throws Exception {
		if (!buffer.hasRemaining()) {
			buffer.flip();
			while (buffer.hasRemaining()) {
				fileChannel.write(buffer);
			}
			buffer.clear();
		}
		buffer.putInt(value);
	}

	@Override
	public void close() throws IOException {
		if (buffer.position() > 0) {
			buffer.flip();
			while (buffer.hasRemaining()) {
				fileChannel.write(buffer);
			}
		}

		fileChannel.close();
		fos.close();
	}

}
