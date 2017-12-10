package sma.util.access;

import java.io.IOException;
import java.nio.channels.FileChannel;

import com.healthmarketscience.jackcess.Database;

/**
 * Read only memory database
 * 
 * @author marsolle
 * 
 */
public class MemoryDatabase extends Database {

	protected MemoryDatabase(FileChannel pChannel, boolean pAutoSync) throws IOException {
		super(null, pChannel, pAutoSync, null, null, null, null);
	}

	/**
	 * by S. MARSOLLE.
	 * 
	 * @param bytes
	 * @return
	 * @throws IOException
	 */
	public static Database open(byte[] bytes) throws IOException {
		// open file channel
		FileChannel channel = new MemoryChannel(bytes);

		return new MemoryDatabase(channel, false);
	}

}
