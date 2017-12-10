package sma.gmap;

import java.awt.geom.Rectangle2D.Double;

/**
 * Dalle de type satellite JPEG
 * 
 * @author smarsoll
 * 
 */
public class DalleSatellite extends Dalle {

	String quadrant;


//	public DalleSatellite(String quadrant) {
//		this.quadrant = quadrant;
//	}

	public DalleSatellite(int zoomLevel, int x, int y) {
		super(zoomLevel, x, y);
		// get lat lon
		Double rect = GoogleTileUtils2.getTileRect(x, y, zoom);
		// get satellite string
		sLog.debug("rect.getCenterX()=" +rect.getCenterX());
		this.quadrant = GoogleTileUtils2.getSatelliteRef(rect.getCenterY(),
				rect.getCenterX(), zoom);

	}

	@Override
	public String getFileName() {
		return Dalle.SAVE_PATH + "/s"+ this.zoom +"/s"+ this.zoom + "_" + this.quadrant + ".jpg";
	}

	@Override
	public String getUrlAdress() {
		String v = "10";
		if (zoom < 7) {
			// v = "23";
			v = "28";
		}
		// zoom 2 => http://khm1.google.fr/kh?n=404&v=28&hl=fr&t=trttqrrqrssrrqqt
		
		int server = (this.x + this.y + this.zoom) % 4 ;
		// http://kh0.google.com/kh?v=10&t=tq
		return "http://kh"+ server +".google.com/kh?n=404&v="+ v +"&t=" + quadrant;
	}

	@Override
	public String getKeyDalle() {
		// S:trttqrrqr
		return "S:" + this.zoom + ":" + this.x + ":" + this.y;
	}
	public String getKeyDalleOld() {
		// S:trttqrrqr
		return "S:" + this.quadrant;
	}

}
