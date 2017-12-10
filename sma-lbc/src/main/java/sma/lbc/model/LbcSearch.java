package sma.lbc.model;

import java.util.LinkedList;
import java.util.List;

/**
 * One search result from LeBonCoin
 * 
 * @author marsolle
 * 
 */
public class LbcSearch {

	private String sourceUrl;

	private String titleSearch;

	private List<LbcAnnonce> lstAnnonces = new LinkedList<LbcAnnonce>();

	public String getTitleSearch() {
		return titleSearch;
	}

	public void setTitleSearch(String pTitleSearch) {
		titleSearch = pTitleSearch;
	}

	public String getSourceUrl() {
		return sourceUrl;
	}

	public void setSourceUrl(String pSourceUrl) {
		sourceUrl = pSourceUrl;
	}

	public List<LbcAnnonce> getLstAnnonces() {
		return lstAnnonces;
	}

	public void setLstAnnonces(List<LbcAnnonce> pLstAnnonces) {
		lstAnnonces = pLstAnnonces;
	}

}
