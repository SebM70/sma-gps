package sma.firefox;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sma.gmap.Dalle;

public class DecodeCache {
	
	/** Log4J */
	private static final Logger sLog = LoggerFactory.getLogger(DecodeCache.class);

	static final String CACHE_DIR = 
		"C:\\Documents and Settings\\smarsoll\\Local Settings\\Application Data\\Mozilla\\Firefox\\Profiles\\8gp7661e.default"+ 
		"\\Cache\\";
	
	Map<String, CacheRecord> mapDalles;
	
	/**
	 * init map from cache files
	 * @throws IOException
	 */
	public DecodeCache() throws IOException {
		super();
	}

	/**
	 * Unit test
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		sLog.info("start unit test...");
		
		try {
			DecodeCache cache = new DecodeCache();
			cache.decodeCacheMap();
			
		} catch (Throwable e) {
			sLog.error("error in main", e);
		}

		sLog.info("...end");
	}

	public void decodeCacheMap() throws IOException {
		mapDalles = new HashMap<String, CacheRecord>(10000);
		File f = new File(CACHE_DIR + "_CACHE_MAP_");
		CacheMapFileReader cacheMap = new CacheMapFileReader(f);
		CacheRecord cr;
		int nbEntries = 0;
		int nbValidEntries = 0;
		int nbDalles = 0;
		while ( (cr = cacheMap.readCacheRecord()) != null) {
			nbEntries++;
			if (cr.hashNumber == 0 ) {
				sLog.debug("hashNumber == 0 ! " + cr.evictionRank + " " + cr.metadataLocation + " " + cr.dataLocation);
			} else {
				int metaDataLocId = cr.getMetaDataLocId();
				if( metaDataLocId >= 1) {
					nbValidEntries++;
					BlockFile bf = BlockFile.getBlockFile(metaDataLocId);
					byte[] metaDataBlock = bf.readBlockFromFile(cr.getMetaDataStartBlock(), cr.getMetaDataSizeBlock());
					MetadataEntry me = new MetadataEntry(metaDataBlock);
					cr.setMetatData(me);
					
					if (sLog.isDebugEnabled()) {
						byte[] data = this.getDataForCacheRecord(cr);
						sLog.debug(new String(data));
					}
					
		
					String url = me.getMRequest();
					if (url.startsWith("HTTP:http://")) {
						// the first characters are not used
						url = url.substring(12);
						// if (url.contains(".google.") && url.contains("n=404&") && ("200".equals(me.getHttpResponse()))) {
						// if (url.contains(".google.") && url.contains("?") && ("200".equals(me.getHttpResponse()))) {
						if (url.contains(".google.") && ("200".equals(me.getHttpResponse()))) {
							nbDalles++;
							
							String keyDalle = Dalle.getGoogleKeyDalleFromUrl(url);
							//sLog.debug(keyDalle + " " + url);
							if (keyDalle == null) {
								//sLog.warn("can not identify\n" + url); 
							} else {
								mapDalles.put(keyDalle, cr);
							}
						}
					}
				} else {
					sLog.debug("metaDataLocId=" + metaDataLocId + " !");
				}
			}
//			if (nbValidEntries > 10) {
//				break;
//			}
			if (sLog.isDebugEnabled())
				sLog.debug("==================================================================== " + nbEntries);
		}
		
		cacheMap.close();
		sLog.info("Cache entries: " + nbEntries + " , valid: " + nbValidEntries + " , googleMap tiles: " + nbDalles);
	}


	public static void decodeCacheFile(String fileName, int blockSize) throws IOException {
		File f = new File(CACHE_DIR + fileName);
		// block size can be 256, 1024 or 4096
		byte[] buf = new byte[blockSize];

		FileInputStream fis  = new FileInputStream(f);
		
		// first empty block:
		fis.skip(4096);
		
		int nbEntries = 0;
		//int nbReadBytes = 0;
        while ((/*nbReadBytes =*/ fis.read(buf)) != -1) {
            //fos.write(buf, 0, i);
			sLog.info("nbEntries={}", nbEntries);
        	MetadataEntry ce = new MetadataEntry(blockSize);
        	ce.read(buf, fis);
        	//System.out.println(buf);
        	nbEntries++;
        }
        fis.close();
		sLog.info("nb entries: " + nbEntries);
	}

	/**
	 * read data bytes associated to a google tile
	 * @param d
	 * @return
	 * @throws IOException 
	 */
	public byte[] getDataForDalle(Dalle d) throws IOException {
		byte[] resultByte = null;
		CacheRecord cr = mapDalles.get(d.getKeyDalle());
		if (cr == null) {
			// no dalle in the cache
			return null;
		}
		// data location id gives the file logic
		resultByte = this.getDataForCacheRecord(cr);
		return resultByte;
	}
	
	public byte[] getDataForCacheRecord(CacheRecord cr) throws IOException {
		byte[] resultByte = null;
		// data location id gives the file logic
		int locId = cr.getDataLocId();
		if (locId == 0) {
			// data is in a file
			resultByte = new byte[cr.getMetaData().datasize];
			if (sLog.isDebugEnabled()) sLog.debug(cr.getMetaData().getMRequest());
			// filename is hash number in hexa
			File f = new File(CACHE_DIR + cr.getDataFileName());
			FileInputStream fis = new FileInputStream(f);
			fis.read(resultByte);
			fis.close();
			//sLog.warn("not coded !");
		} else {
			// data is in block file
			int dataSize = cr.getMetaData().datasize;
			resultByte = new byte[dataSize];
			if (sLog.isDebugEnabled()) sLog.debug("locId=" + locId + " " + cr.getDataStartBlock() + " " + cr.getDataSizeBlock());
			BlockFile bf = BlockFile.getBlockFile(locId);
			//byte[] readBlock = bf.readBlockFromFile(cr.getDataStartBlock(), cr.getDataSizeBlock());
			// we copy only required bytes
			//System.arraycopy(readBlock, 0, resultByte, 0, dataSize);
			resultByte = bf.readBlockFromFile(cr.getDataStartBlock(), cr.getDataSizeBlock());
			if (sLog.isDebugEnabled()) sLog.info("datasize=" + cr.getMetaData().datasize);
			
		}
		return resultByte;
	}

}
