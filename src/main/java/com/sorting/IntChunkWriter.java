package com.sorting;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class IntChunkWriter implements Closeable {

	private FileOutputStream fos;

	private FileChannel fileChannel;

	public IntChunkWriter(String directoryPath, int chunkIndex) throws Exception {
		fos = new FileOutputStream(directoryPath + "/chunk-" + chunkIndex + ".bin");
		fileChannel = fos.getChannel();
	}

	public void write(int[] numbers) throws Exception {
		ByteBuffer buffer = ByteBuffer.allocate(numbers.length * Integer.BYTES);
		for (int number : numbers) {
			buffer.putInt(number);
		}
		buffer.flip();
		fileChannel.write(buffer);
	}

	@Override
	public void close() throws IOException {
		fileChannel.close();
		fos.close();
	}

}
