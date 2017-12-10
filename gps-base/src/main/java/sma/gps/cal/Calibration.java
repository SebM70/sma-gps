/*
 * Calibration.java
 *
 * Created on 6 août 2002, 20:30
 */

package sma.gps.cal;

/**
 *
 * @author  MARSOLLE
 */
import geotransform.ellipsoids.CC_Ellipsoid;
import geotransform.ellipsoids.Ellipsoid;
import geotransform.ellipsoids.IN_Ellipsoid;
import geotransform.ellipsoids.WE_Ellipsoid;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import sma.SmaTool;
import sma.gps.model.Coord;
import sma.gps.model.Waypoint;

public abstract class Calibration {

	/** To store version */
	public static final String VERSION = "0.0.2-2012-07";
	public static final String NAME_VERSION = "Calibration Converter V" + VERSION + " written by S. MARSOLLE";

	protected File dirFile = new File(".");
	/** map image file name */
	protected String imageFileName;
	/** calibration file name */
	protected String calFileName;
	/** Map datum. */
	protected String mapDatum;

	/** pixel points sur l'images, List of int[2.] */
	protected List<int[]> calPoints = new ArrayList<int[]>(20);
	/** coordonnées, List of Coord */
	protected List<Coord> calCoords = new ArrayList<Coord>(20);

	// extent of the map
	/** max north */
	double north;
	/** min south */
	double south;
	/** max east */
	double east;
	/** min west */
	double west;

	// size of image
	int imageWidth = 0;
	int imageHeight = 0;


	/** Creates new Calibration */
	public Calibration() {
	}

	// extends : "a,bc" => { a, bc }
	public String[] decodeLigneVirgule(String lineToDecode) {
		String[] table = SmaTool.StringtoArray(lineToDecode, ",");
		// remove spaces
		for (int i = 0; i < table.length; i++) {
			table[i] = table[i].trim();
		}

		return table;
	}

	public String[] decodeLigneVirgule_Old(String lineToDecode) {
		List<String> lstElem = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(lineToDecode, ",", false);
		while (st.hasMoreTokens()) {
			String elem = st.nextToken();
			// ajout sans les espaces
			lstElem.add(elem.trim());
		}
		String[] retour = new String[lstElem.size()];
		// return lstElem.toArray();
		return (String[]) lstElem.toArray(retour);
	}

	public void changeImageExtension(String newExt) {
		int pos = imageFileName.lastIndexOf('.');
		imageFileName = imageFileName.substring(0, pos + 1) + newExt;
	}

	public void generateCalibrPoints() {
		if (calPoints.size() == 2) {
			// il faut au moins 4 points de calibration
			System.out.println("generating 2 points (buggy from an UTM calibration)");

			// points
			int[] point1 = (int[]) calPoints.get(0);
			int[] point2 = (int[]) calPoints.get(1);

			int[] point3 = new int[2];
			point3[0] = point2[0];
			point3[1] = point1[1];
			calPoints.add(point3);

			int[] point4 = new int[2];
			point4[0] = point1[0];
			point4[1] = point2[1];
			calPoints.add(point4);

			// coordonnées
			Coord c1 = (Coord) calCoords.get(0);
			Coord c2 = (Coord) calCoords.get(1);

			Coord c3 = new Coord();
			c3.longit = c2.longit;
			c3.latit = c1.latit;
			calCoords.add(c3);

			Coord c4 = new Coord();
			c4.longit = c1.longit;
			c4.latit = c2.latit;
			calCoords.add(c4);

		}
	}

	/**
	 * copy all params from an other calibration file
	 * 
	 * @param calModel
	 */
	public void copyAttributes(Calibration calModel) {
		this.dirFile = calModel.dirFile;
		this.imageFileName = calModel.imageFileName;
		this.calFileName = calModel.calFileName;
		this.mapDatum = calModel.mapDatum;
		// List
		this.calPoints = calModel.calPoints;
		this.calCoords = calModel.calCoords;
		// extents
		this.north = calModel.north;
		this.south = calModel.south;
		this.east = calModel.east;
		this.west = calModel.west;
		this.imageWidth = calModel.imageWidth;
		this.imageHeight = calModel.imageHeight;
	}

	public Ellipsoid getElipsoid() {
		if (mapDatum.equalsIgnoreCase("WGS 84")) {
			return new WE_Ellipsoid();
		} else if (mapDatum.equals("European 1979")) {
			return new IN_Ellipsoid();
		} else if (mapDatum.startsWith("European 1950")) {
			return new IN_Ellipsoid();
		} else if (mapDatum.startsWith("NAD27")) {
			return new CC_Ellipsoid();
		} else {
			System.out.println("getElipsoid " + mapDatum + " not found");
			return new IN_Ellipsoid();
		}

	}

	/**
	 * calculate map extension with its own calibrations points
	 * 
	 */
	public void calcExtent() {
		// % extent more
		double ext = 0.04;
		double delta;
		// init for generic algo
		north = -Double.MAX_VALUE;
		south = Double.MAX_VALUE;
		east = -Double.MAX_VALUE;
		west = Double.MAX_VALUE;
		for (int i = 1; i <= calCoords.size(); i++) {
			Coord coordCal = (Coord) calCoords.get(i - 1);
			if (coordCal.latit > north)
				north = coordCal.latit;
			if (coordCal.latit < south)
				south = coordCal.latit;

			if (coordCal.longit < west)
				west = coordCal.longit;
			if (coordCal.longit > east)
				east = coordCal.longit;
		}

		// add % extent
		delta = (north - south) * ext;
		north = north + delta;
		south = south - delta;

		delta = (east - west) * ext;
		east = east + delta;
		west = west - delta;

	}

	/**
	 * Calcule les extensions d'une liste de coordonnées
	 * 
	 * @param lstCoords
	 * @return
	 */
	public static Coord[] calculateExtents(Collection<Coord> lstCoords) {
		Coord[] result = new Coord[] { new Coord(), new Coord() };
		// init mins
		result[0].latit = Double.MAX_VALUE;
		result[0].longit = Double.MAX_VALUE;
		// init maxs
		result[1].latit = -Double.MAX_VALUE;
		result[1].longit = -Double.MAX_VALUE;


// for (int i = 1; i <= lstCoords.size(); i++) {
		for (Coord coordCal : lstCoords) {
			// Coord coordCal = lstCoords.get(i - 1);
			if (coordCal.latit > result[1].latit)
				result[1].latit = coordCal.latit;
			if (coordCal.latit < result[0].latit)
				result[0].latit = coordCal.latit;

			if (coordCal.longit < result[0].longit)
				result[0].longit = coordCal.longit;
			if (coordCal.longit > result[1].longit)
				result[1].longit = coordCal.longit;
		}
		return result;
	}

	/**
	 * Calcule les extensions d'une liste de coordonnées
	 * 
	 * @param lstCoords
	 * @return
	 */
	public static Coord[] calculateExtentsWpts(List<Waypoint> lstWpts) {
		Coord[] result = new Coord[] { new Coord(), new Coord() };
		// init mins
		result[0].latit = Double.MAX_VALUE;
		result[0].longit = Double.MAX_VALUE;
		// init maxs
		result[1].latit = -Double.MAX_VALUE;
		result[1].longit = -Double.MAX_VALUE;

		for (int i = 1; i <= lstWpts.size(); i++) {
			Coord coordCal = lstWpts.get(i - 1).coord;
			if (coordCal.latit > result[1].latit)
				result[1].latit = coordCal.latit;
			if (coordCal.latit < result[0].latit)
				result[0].latit = coordCal.latit;

			if (coordCal.longit < result[0].longit)
				result[0].longit = coordCal.longit;
			if (coordCal.longit > result[1].longit)
				result[1].longit = coordCal.longit;
		}
		return result;
	}

	// File on the calibration file
	public File getFile() {
		return new File(dirFile, calFileName);
	}

	// sort points to put external at the begining
	public void sortPoints() {
		// points sur l'images
		List<int[]> calPoints2 = new ArrayList<int[]>(20);
		// coordonnées
		List<Coord> calCoords2 = new ArrayList<Coord>(20);

		// public void transferPoint(int toTra) {
		// };

		int toTransfer;
		toTransfer = getMaxPoint(-1, 1);
		// System.out.println("getMaxPoint(-1,1) = " + toTransfer);

		if (toTransfer >= 0) {
			calPoints2.add(calPoints.get(toTransfer));
			calPoints.remove(toTransfer);
			calCoords2.add(calCoords.get(toTransfer));
			calCoords.remove(toTransfer);
		}
		toTransfer = getMaxPoint(1, 1);
		// System.out.println("getMaxPoint(1,1) = " + toTransfer);
		if (toTransfer >= 0) {
			calPoints2.add(calPoints.get(toTransfer));
			calPoints.remove(toTransfer);
			calCoords2.add(calCoords.get(toTransfer));
			calCoords.remove(toTransfer);
		}
		toTransfer = getMaxPoint(1, -1);
		if (toTransfer >= 0) {
			calPoints2.add(calPoints.get(toTransfer));
			calPoints.remove(toTransfer);
			calCoords2.add(calCoords.get(toTransfer));
			calCoords.remove(toTransfer);
		}
		toTransfer = getMaxPoint(-1, -1);
		// System.out.println("getMaxPoint(-1,-1) = " + toTransfer);
		if (toTransfer >= 0) {
			calPoints2.add(calPoints.get(toTransfer));
			calPoints.remove(toTransfer);
			calCoords2.add(calCoords.get(toTransfer));
			calCoords.remove(toTransfer);
		}
		// le reste
		calPoints2.addAll(calPoints);
		calCoords2.addAll(calCoords);

		//
		calPoints = calPoints2;
		calCoords = calCoords2;

	}

	// used by sortPoints to extract a point from a direction
	public int getMaxPoint(int dirx, int diry) {
		int order = -1;
		int maxDir = -999999;

		for (int i = 0; i < calPoints.size(); i++) {
			int[] point = (int[]) calPoints.get(i);
			int curDir = point[0] * dirx + point[1] * diry;
			if (curDir > maxDir) {
				maxDir = curDir;
				order = i;
			}
		}
		return order;
	}



	// ======== Getters & Setters =========

	public void setDirName(String dirName) {
		this.dirFile = new File(dirName);
	}

	public void setImageFileName(String imageFileName) {
		this.imageFileName = imageFileName;
	}

	public void setCalFileName(String calFileName) {
		this.calFileName = calFileName;
	}

	public void setMapDatum(String mapDatum) {
		this.mapDatum = mapDatum;
	}

	public void setNorth(double north) {
		this.north = north;
	}

	public void setSouth(double south) {
		this.south = south;
	}

	public void setEast(double east) {
		this.east = east;
	}

	public void setWest(double west) {
		this.west = west;
	}

	public void setImageWidth(int imageWidth) {
		this.imageWidth = imageWidth;
	}

	public void setImageHeight(int imageHeight) {
		this.imageHeight = imageHeight;
	}

	public List<Coord> getCalCoords() {
		return calCoords;
	}

	public String getCalFileName() {
		return calFileName;
	}

	public List<int[]> getCalPoints() {
		return calPoints;
	}

}
