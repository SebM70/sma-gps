package sma.firefox;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CacheMapFileReader {
	/** Log4J */
	private static final Logger sLog = LoggerFactory.getLogger(CacheMapFileReader.class);

	FileInputStream fis;
	DataInputStream dis;
	
	/** size of one record 4 by 4 bytes */
	private static final int RECORD_SIZE = 4*4;

	public CacheMapFileReader(File f) throws IOException {
		fis  = new FileInputStream(f);
		dis = new DataInputStream(fis);
		// skip initial header
		//int toSkip = 4096 + 4;
		int toSkip = (0*4*4) + 4;
		dis.skip(toSkip);
		sLog.warn("we are not sure we should skip " + toSkip + " bytes");
		
	}

	/**
	 * Read a valid cache record. We may skip several invalid cache record to read one valid.
	 * @return
	 * @throws IOException
	 */
	public CacheRecord readCacheRecord() throws IOException {
		if (dis.available() < (RECORD_SIZE)) {
			sLog.debug("dis.available() = " + dis.available());
			return null;
		}
		CacheRecord result = new CacheRecord();
		this.readInternal(result);
		while (result.dataLocation == 0) {
			if (dis.available() < (RECORD_SIZE)) {
				sLog.debug("dis.available() = " + dis.available());
				return null;
			}
			this.readInternal(result);
		}
		sLog.debug("dataLocation = " +  Integer.toHexString( result.dataLocation) );
		sLog.debug("hashNumber = " +  Integer.toHexString( result.hashNumber) );
		//sLog.debug("evictionRank = " +  Integer.toHexString( result.evictionRank) );
		
		sLog.debug("result.getMetaDataLocId() = " + result.getMetaDataLocId() );
		sLog.debug("result.getMetaDataStartBlock() = " +  result.getMetaDataStartBlock() + " " + result.getMetaDataSizeBlock());
		sLog.debug("result.getDataLocId() = " + ( result.getDataLocId() ) );
		sLog.debug("result.getDataStartBlock() = " + result.getDataStartBlock() + " " + result.getDataSizeBlock() );
		
		return result;
	}

	/**
	 * read a cache record that can be invalid.
	 * 4 x 32 bits = 16 bytes
	 * @param result
	 * @throws IOException
	 */
	private void readInternal(CacheRecord result) throws IOException {
		result.hashNumber = dis.readInt();
		result.evictionRank = dis.readInt();
		result.dataLocation = dis.readInt();
		result.metadataLocation = dis.readInt();
	}

	public void close() throws IOException {
		dis.close();
		fis.close();
	}

}
