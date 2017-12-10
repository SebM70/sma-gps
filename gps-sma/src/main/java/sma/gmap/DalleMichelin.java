package sma.gmap;

import sma.util.Base64Coder;

/**
 * Dalle pour t�l�chargement depuis site viamichelin
 * 
 * @author smarsoll
 * 
 */
public class DalleMichelin extends Dalle {
	/** size in pixel */
	public static final int TILE_SIZE = 256;
	public static final String SERIE_ESP_400 = "epr_0400k_r03";
	
	public DalleMichelin(String serie, int x, int y) {
		this.x = x;
		this.y = y;
		this.mapSerie = serie;
	}

	String mapSerie;
	
	// BASE64Encoder is sun specific
	// static final BASE64Encoder ENCODER_64 = new BASE64Encoder();

	@Override
	public String getFileName() {
		return Dalle.SAVE_PATH + "/"+ this.mapSerie +"/"+ this.x + "_" + this.y +  ".png";
	}

	@Override
	public String getUrlAdress() {
		// ex: http://m1.viamichelin.com/mapsgene/dm/mapdirect;ZXByXzA0MDBrX3IwMw==;MDAwMDAwMDAwMjAwMDAwMDAwNjM=
		
		String coord = String.format("%1$010d", this.x) + String.format("%1$010d", this.y);
		
		
		return "http://m1.viamichelin.com/mapsgene/dm/mapdirect;" + encode64(this.mapSerie) + ";" + encode64(coord);
	}
	
	public static String encode64(String s) {
		return Base64Coder.encodeString(s);
		// return ENCODER_64.encode(s.getBytes());
	}

	@Override
	public String getKeyDalle() {
		// TODO Auto-generated method stub
		return null;
	}

}
