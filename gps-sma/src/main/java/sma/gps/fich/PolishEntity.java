package sma.gps.fich;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Plosith Entity (for MP files).
 * 
 */
public class PolishEntity {

	String mFamily;
	List<String> mLstLines = new ArrayList<String>();
	int level = -1;

	/** Log4J */
	private static final Logger sLog = LoggerFactory.getLogger(PolishEntity.class);

	public String getFamily() {
		return mFamily;
	}

	public void setFamily(String family) {
		if ("RGN80".equals(family)) {
			this.mFamily = "POLYGON";
		} else if ("RGN40".equals(family)) {
			this.mFamily = "POLYLINE";
		} else if ("RGN10".equals(family)) {
			this.mFamily = "POI";
		} else if ("RGN20".equals(family)) {
			this.mFamily = "POI";
		} else {
			this.mFamily = family;
		}
	}
	
	/**
	 * 
	 * @param pLine
	 *            Data0=(46.265122890,0.892961025),(46.265015602,0.892982483),(46.264564991,0.893347263),(46.264264584,0.893368721
	 *            ),(46.263213158,0.893325806),(46.262204647,0.892703533),(46.261324883,0.892231464),(46.259973049,0.891823769),(
	 *            46.259522438,0.891802311)
	 */
	public void addLine(String pLine) {
		String key = pLine.substring(0, pLine.indexOf('='));
		if (key.startsWith("Data")) {
			level = Integer.parseInt(key.substring(4));
		}
		mLstLines.add(pLine);
	}

	/**
	 * @return List of String
	 */
	public List<String> getLstLines() {
		return mLstLines;
	}
	
	public String getValueForKey (String pKey) {
		String begin = pKey + "=";
		for (Iterator<String> iter = mLstLines.iterator(); iter.hasNext();) {
			String line = iter.next();
			if (line.startsWith(begin)) {
				return line.substring(begin.length(), line.length());
			}
			
		}
		return null;
	}
	
	/**
	 * @param pKey at to set
	 * @param pValue to set
	 */
	public String setValueForKey (String pKey, String pValue) {
		String begin = pKey + "=";
		boolean found = false;
		String line = null;
		for (ListIterator<String> iter = mLstLines.listIterator(); iter.hasNext();) {
			line = iter.next();
			if (line.startsWith(begin)) {
				iter.set(pKey + "=" + pValue);
				found = true;
				break;
			}
			
		}
		if (!found) {
			sLog.warn("key "+ pKey +" not found !");
			return null;
		}
		return line;
	}
	
	public String removeKey (String pKey) {
		String begin = pKey + "=";
		//boolean found = false;
		String line = null;
		for (ListIterator<String> iter = mLstLines.listIterator(); iter.hasNext();) {
			line = iter.next();
			if (line.startsWith(begin)) {
				iter.remove();
				//found = true;
				break;
			}
			
		}
		return line;
	}
	
	public void addValueForKey (String pKey, String pValue) {
		this.addLine(pKey + "=" + pValue);
	}

	public String setOrAddValueForKey(String pKey, String pValue) {
		String result = this.setValueForKey(pKey, pValue);
		if (result == null) {
			this.addValueForKey(pKey, pValue);
		}
		return result;
	}

	public int getLevel() {
		return level;
	}

}
