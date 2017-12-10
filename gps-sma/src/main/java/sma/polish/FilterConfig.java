package sma.polish;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * Filter configuration to filter mp.
 * 
 * @author marsolle
 * 
 */
public class FilterConfig {
	/**
	 * in IMG ID
	 */
	private Collection<String> removeKey = new ArrayList<String>();

	private Collection<String> filteredEntities = new HashSet<String>();

	private int maxLevel = 10;

	public Collection<String> getRemoveKey() {
		return removeKey;
	}

	public Collection<String> getFilteredEntities() {
		return filteredEntities;
	}

	public int getMaxLevel() {
		return maxLevel;
	}

	public void setMaxLevel(int pMaxLevel) {
		maxLevel = pMaxLevel;
	}

}
