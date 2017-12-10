package sma.lbc.model;

import java.util.Date;

/**
 * Bean that host an "annonce"
 * 
 * @author marsolle
 * 
 */
public class LbcAnnonce {

	private String href;
	private String strDate;
	private Date date;
	/** URL to image. */
	private String image;
	private String title;
	private String placement;
	/** price in Euro */
	private int price = -1;

	@Override
	public String toString() {
		return "LbcAnnonce [strDate=" + strDate + ", title=" + title + ", placement=" + placement + ", price=" + price
				+ ", href=" + href + "]";
	}


	public String getHref() {
		return href;
	}

	public void setHref(String pHref) {
		href = pHref;
	}

	public String getStrDate() {
		return strDate;
	}

	public void setStrDate(String pStrDate) {
		strDate = pStrDate;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date pDate) {
		date = pDate;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String pImage) {
		image = pImage;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String pTitle) {
		title = pTitle;
	}

	public String getPlacement() {
		return placement;
	}

	public void setPlacement(String pPlacement) {
		placement = pPlacement;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int pPrice) {
		price = pPrice;
	}
	
	
}
