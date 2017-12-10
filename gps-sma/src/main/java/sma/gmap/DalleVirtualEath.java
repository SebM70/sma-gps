package sma.gmap;

import java.awt.Point;

/**
 * Dalle de type Microsoft Virtual Earth
 * http://maps.live.com/
 * 
 * @author smarsoll
 * 
 */
public class DalleVirtualEath extends Dalle {

	String quadrant;

	private String type;


//	public DalleSatellite(String quadrant) {
//		this.quadrant = quadrant;
//	}

	public DalleVirtualEath(int zoomLevel, int x, int y, String type) {
		super(zoomLevel, x, y);
		this.type = type;
		//sLog.info("new DalleVirtualEath =" + x + " " + y);
		this.quadrant = this.calculateSatelliteRef(x, y, zoomLevel);
		// get lat lon
		
//		Double rect = GoogleTileUtils2.getTileRect(x, y, zoom);
//		// get satellite string
//		sLog.debug("rect.getCenterX()=" +rect.getCenterX());
//		this.quadrant = this.calculateSatelliteRef(rect.getCenterY(),
//				rect.getCenterX(), zoom);

	}

	  public String calculateSatelliteRef(int xx, int yy, int zoom) {
	      //Point tileXY = GoogleTileUtils2.toTileXY(lat, lng, zoom);
	      int invZoom  = 17 - zoom;
	      int stepSize = 1 << (17 - zoom);
	      int currentX = 0;
	      int currentY = 0;
	      //sLog.info("tileXY=" + tileXY);

	      //StringBuffer satString = new StringBuffer(zoom);
	      StringBuffer satString = new StringBuffer(20);
	      //satString.append("t");

	      for (int i = 0; i < invZoom; i++) {
	         stepSize >>= 1;

	         if ((currentY + stepSize) > yy) {
	            if ((currentX + stepSize) > xx) {
	               satString.append('0');
	            }
	            else {
	               currentX += stepSize;
	               satString.append('1');
	            }
	         }
	         else {
	            currentY += stepSize;

	            if ((currentX + stepSize) > xx) {
	               satString.append('2');
	            }
	            else {
	               currentX += stepSize;
	               satString.append('3');
	            }
	         }
	      }

	      return satString.toString();
	   }

	
	  public String calculateSatelliteRef(double lat, double lng, int zoom) {
	      Point tileXY = GoogleTileUtils2.toTileXY(lat, lng, zoom);
	      int invZoom  = 17 - zoom;
	      int stepSize = 1 << (17 - zoom);
	      int currentX = 0;
	      int currentY = 0;
	      sLog.info("tileXY=" + tileXY);

	      //StringBuffer satString = new StringBuffer(zoom);
	      StringBuffer satString = new StringBuffer(20);
	      //satString.append("t");

	      for (int i = 0; i < invZoom; i++) {
	         stepSize >>= 1;

	         if ((currentY + stepSize) > tileXY.y) {
	            if ((currentX + stepSize) > tileXY.x) {
	               satString.append('0');
	            }
	            else {
	               currentX += stepSize;
	               satString.append('1');
	            }
	         }
	         else {
	            currentY += stepSize;

	            if ((currentX + stepSize) > tileXY.x) {
	               satString.append('2');
	            }
	            else {
	               currentX += stepSize;
	               satString.append('3');
	            }
	         }
	      }

	      return satString.toString();
	   }
	
	@Override
	public String getFileName() {
		return Dalle.SAVE_PATH + "/" + this.type + this.zoom +"/"+ this.type + this.zoom + "_" + this.quadrant + ".jpg";
	}

	@Override
	public String getUrlAdress() {
	
		// zoom 2 => http://khm1.google.fr/kh?n=404&v=28&hl=fr&t=trttqrrqrssrrqqt
		
		// int server = (this.x + this.y + this.zoom) % 4 ;
		int server = (int) (Math.random()*4.0) ;
		// satellite:
		// http://h1.ortho.tiles.virtualearth.net/tiles/h1202223020211.jpeg?g=173
		// plan relief: 
		// http://r3.ortho.tiles.virtualearth.net/tiles/r1202223020213.png?g=173&shading=hill
		if (Dalle.VE_PLAN.equals(this.type)) {
			return "http://r"+ server +".ortho.tiles.virtualearth.net/tiles/r" 
			+ this.quadrant + ".png?g=173&shading=hill";
		} else {
			// Satellite
			return "http://h"+ server +".ortho.tiles.virtualearth.net/tiles/h" 
			+ this.quadrant + ".jpeg?g=173";
		}

	}

	@Override
	public String getKeyDalle() {
		// S:trttqrrqr
		return "S:" + this.quadrant;
	}

}
