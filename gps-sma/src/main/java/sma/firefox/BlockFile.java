package sma.firefox;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlockFile {
	
	
	static final String[] FILE_NAME = {"_CACHE_001_", "_CACHE_002_", "_CACHE_003_"};
	static final int[] BLOCK_SIZE = {256, 1024, 4096};
	//static final int[] BLOCK_SIZE = {256, 512, 1024};
	
	static final int FIRST_BLOCK_SIZE = 4096;
	
	
	/** Log4J */
	private static final Logger sLog = LoggerFactory.getLogger(BlockFile.class);

	/** size of block depend of file type */
	int mBlockSize;
	String mFileName;

	private BlockFile(String fileName, int blkSize) {
		this.mBlockSize = blkSize;
		this.mFileName = fileName;
	}

	public static BlockFile getBlockFile(int fileId) {
		return new BlockFile(FILE_NAME[fileId - 1], BLOCK_SIZE[fileId - 1]) ;
	}

	public byte[] readBlockFromFile(int startBlock, int sizeBlock) throws IOException {
		File f = new File(DecodeCache.CACHE_DIR + mFileName);
		//sLog.debug(f);
		// block size can be multiple of 256, 1024 or 4096
		byte[] buf = new byte[(sizeBlock + 1)*mBlockSize];
		//sLog.debug("buf.length=" + buf.length);

		FileInputStream fis  = new FileInputStream(f);
		
		// first empty block:
		fis.skip(FIRST_BLOCK_SIZE);
		
		// blocks before
		long toSkip = startBlock*((long) mBlockSize);
		sLog.debug("toSkip={}", toSkip);
		fis.skip(toSkip);
		
		// the block we look for
		fis.read(buf);
		
		fis.close();
		
		//sLog.info(new String(buf));
		
		return buf;
	}

}
