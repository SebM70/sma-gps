package sma.gmap;


/**
 * Dalle de type carte espagnole
 * 
 * @author smarsoll
 * 
 */
public class DalleSigPac extends Dalle {

	int zoneUtm = 31;

	public DalleSigPac(int zoomLevel, int x, int y) {
		super(zoomLevel, x, y);
	}

	@Override
	public String getFileName() {
		return Dalle.SAVE_PATH + "/SigPAc" + this.zoom + "_" + this.zoneUtm + "/" 
			+ this.zoom + "_" + this.zoneUtm + "_" + this.x + "_" + this.y + ".jpg";
	}

	@Override
	public String getUrlAdress() {
//		String v = "10";
//		if (this.zoom < 7) {
//			v = "23";
//		}
		
		// http://tilesserver.mapa.es/tilesserver/n=topografico-mtn_25;z=31;r=4000;i=492;j=4589.jpg
		
		return "http://tilesserver.mapa.es/tilesserver/n=topografico-mtn_25;z=31;z="+
			this.zoneUtm +";r="+ this.zoom+ ";i="+ this.x+";j="+this.y+".jpg";
	}

	@Override
	public String getKeyDalle() {
		return "SP:" + this.zoom + ":" + this.x + ":" + this.y;
	}

}
