package sma.gmap;


/**
 * Dalle de type transparente PNG
 * 
 * @author smarsoll
 * 
 */
public class DalleTransparente extends Dalle {


	public DalleTransparente(int zoomLevel, int x, int y) {
		super(zoomLevel, x, y);
	}

	@Override
	public String getFileName() {
		return Dalle.SAVE_PATH + "/t" + this.zoom + "/t" + this.zoom + "_" + this.x + "_" + this.y + ".png";
	}

	@Override
	public String getUrlAdress() {
//		String v = "10";
//		if (this.zoom < 7) {
//			v = "23";
//		}
		int server = (this.x + this.y + this.zoom) % 4 ;
		// ok: http://mt1.google.com/mt?n=404&v=w2t.69&hl=fr&x=66&y=51&zoom=10&s=G
		// ok: http://mt1.google.com/mt?v=w2t.69&x=66&y=51&zoom=10
		// http://mt0.google.com/mt?zoom=16&x=1&y=1
		// plan: http://mt0.google.com/mt?zoom=10&x=66&y=51
		
		// http://mt1.google.com/mt?v=w2t.69&x=2278&y=1558&zoom=5 => err 404
		// http://mt2.google.com/mt?n=404&v=w2t.75&hl=fr&x=568&y=387&zoom=7&s=Gal
		
		return "http://mt"+server+".google.com/mt?n=404&v=w2t.75&x="+ this.x +"&y="+ this.y +"&zoom=" + this.zoom;
	}

	@Override
	public String getKeyDalle() {
		return "T:" + this.zoom + ":" + this.x + ":" + this.y;
	}

}
