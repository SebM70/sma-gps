/*
 * Coord.java
 *
 * Created on 1 septembre 2001, 14:00
 */

package sma.gps.model;

/**
 * A geographical coordonate
 * 
 * @author MARSOLLE
 * @version
 */
public class Coord {

	// latitude
	public double latit;

	public double longit;

	private float alti;

	/** Creates new Coord */
	public Coord() {
	}

	public Coord(double lat, double lon) {
		latit = lat;
		longit = lon;
		alti = -1.1111f;
	}

	public Coord(double lat, double lon, float alt) {
		latit = lat;
		longit = lon;
		alti = alt;
	}

	// exemple : "N43.3699894 E002.1561527"
	public void setCoord(java.lang.String latLong) {
		System.out.println("setCoord not implemented !");
	}

	// exemple : "N43.3699894"
	public void setLatitude(java.lang.String sLat) {
		String nombre = sLat.substring(1);
		latit = Double.parseDouble(nombre);
		if ((sLat.charAt(0) == 'S') || (sLat.charAt(0) == '-')) {
			latit = -latit;
		}
	}

	// exemple : "E002.1561527", "-4.545"
	public void setLongitude(java.lang.String sLong) {
		String nombre = sLong.substring(1);
		longit = Double.parseDouble(nombre);
		if ((sLong.charAt(0) == 'W') || (sLong.charAt(0) == '-')) {
			longit = -longit;
		}
	}

	public float getAlti() {
		return alti;
	}

	@Override
	public String toString() {
		return "Coord [" + latit + ", " + longit + "]";
	}

}
