/*
 * TrackPoint.java
 *
 * Created on 17 septembre 2001, 21:26
 */

package sma.gps.model;


/**
 *
 * @author  MARSOLLE
 * @version
 */
public class TrackPoint {
    
    private Coord coord;
    private boolean isFirst = false;

	/** speed in Km/H */
	private float speed;
    
    /** Creates new TrackPoint */
    public TrackPoint() {
    }
    public TrackPoint(Coord newCoord) {
        coord = newCoord;
    }
    public Coord getCoord() {
        return coord;
    }
    public boolean isFirst() {
        return isFirst; 
    }
    public void setFirst(boolean first) {
        isFirst = first;
    }

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}
    
}
