package sma.gps.cal;

/**
 * Simple Java code
 * 
 * @author marsolle
 * 
 */
public class CalUtil {

	/**
	 * ex: C:\Dir\name.bmp => name.bpm
	 * 
	 * @param pLigne
	 * @param delimiter
	 * @return
	 */
	public static String getLastDelimited(String pLigne, char delimiter) {
		int index = pLigne.lastIndexOf(delimiter);
		if (index >= 0) {
			return pLigne.substring(index + 1);
		}
		return null;
	}

	public static String getFileNameFromPath(String ligne) {
		String result = getLastDelimited(ligne, '\\');
		if (result == null) {
			result = getLastDelimited(ligne, '/');
		}
		if (result == null) {
			result = ligne;
		}
		return result;
	}

}
