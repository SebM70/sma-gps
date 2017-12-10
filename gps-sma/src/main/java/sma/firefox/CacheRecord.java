package sma.firefox;

/**
 * entry in _CACHE_MAP_ file.
 * @author smarsoll
 *
 */
public class CacheRecord {
	
	public int hashNumber, evictionRank, dataLocation, metadataLocation;
	private MetadataEntry mMetaData;

	/**
	 * 
	 * @return 0, 1, 2 or 3
	 */
	public int getMetaDataLocId() {
		return ((metadataLocation & 0x30000000) >> 28);
	}

	public int getDataLocId() {
		return ((dataLocation & 0x30000000) >> 28);
	}

	public int getMetaDataStartBlock() {
		return (metadataLocation & 0x00FFFFFF);
	}
	public int getMetaDataSizeBlock() {
		return (metadataLocation & 0x03000000) >> 24;
	}

	public int getDataStartBlock() {
		return (dataLocation & 0x00FFFFFF);
	}
	public int getDataSizeBlock() {
		return (dataLocation & 0x03000000) >> 24;
	}

	public void setMetatData(MetadataEntry me) {
		mMetaData = me;
	}

	public MetadataEntry getMetaData() {
		return mMetaData;
	}

	/**
	 * get the name of the data file in cache directory
	 * @return
	 */
	public String getDataFileName() {
		String hexString = (Integer.toHexString(this.hashNumber)).toUpperCase();
		// add leading 0
		while (hexString.length()<8) {
			hexString = "0" + hexString;
		}
		// we add "| 0x100" to have leading 0
		int generationNumber = (this.dataLocation & 0xFF) | 0x100;
		return hexString + "d" + Integer.toHexString(generationNumber).substring(1);
	}

}
