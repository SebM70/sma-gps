/*
 * CalTtqv.java
 *
 * Created on 6 ao�t 2002, 23:28
 */

package sma.gps.cal;

/**
 * 
 * @author MARSOLLE
 * @version
 */
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.DecimalFormatSymbols;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sma.gps.model.Coord;

public class CalTtqv extends Calibration {
	/** Log4J */
	private static final Logger sLog = LoggerFactory.getLogger(CalTtqv.class);

	// calibration mode
	public String proj_mode = "frei";

	/** Creates new CalTtqv */
	public CalTtqv() {
	}

	public String getDatumTtqvDesc() {
		if (mapDatum.startsWith("WGS 84")) {
			return "WGS 84# 6378137# 298.257223563# 0# 0# 0#";
		} else if (mapDatum.startsWith("European 1979")) {
			return "European 1979# 6378388# 297#-86#-98#-119#";
		} else if (mapDatum.startsWith("European 1950")) {
			return "European 1950# 6378388# 297#-87#-98#-121#";
		} else if (mapDatum.startsWith("NAD27")) {
			return "NAD27 CONUS# 6378206.4# 294.9786982#-8# 160# 176#";
		} else if (mapDatum.startsWith("Datum Lisboa")) {
			return "Datum Lisboa (Portugal)# 6378388# 297#-304#-60.6# 103.6#";
		} else if (mapDatum.startsWith("Pulkovo 1942")) {
			return "Pulkovo 1942# 6378245# 298.3# 28#-130#-95#";
		} else {
			System.out.println("getDatumTtqvDesc " + mapDatum
					+ " not supported !");
			System.out
					.println("please recalibrate after in TTQV and check map datum");
			// return mapDatum + " not found";
			return mapDatum + "# 6378137# 298.257223563# 0# 0# 0#";
		}
	}

	public void saveInDir(String dirName) throws IOException {
		File file = new File(dirName, this.calFileName);
		FileOutputStream outStream = new FileOutputStream(file);
		this.saveFile(outStream);
		outStream.close();
	}

	public void saveFile(OutputStream outs) throws IOException {
		sLog.debug("creating calibration file " + getCalFileName());
		PrintStream out = new PrintStreamWindows(outs);
		// out = new OutputStreamWriter( new FileOutputStream( calFileName ),
		// "Cp850" );

		DecimalFormatSymbols fs = new DecimalFormatSymbols();
		fs.setDecimalSeparator('.');
		//DecimalFormat formCoord = new DecimalFormat("##0.000000", fs);

		out.println("; Calibration File for QV Map");
		out.println("; " + Calibration.NAME_VERSION);
		out.println();

		// name = 10 = Guadalajara 536-IV.png
		out.print("name = 10 = ");
		out.println(imageFileName);
		// fname = 10 = Guadalajara 536-IV.png
		out.print("fname = 10 = ");
		out.println(imageFileName);

		// nord = 6 = 42.3345
		// sued = 6 = 42.00857
		// ost = 6 = 2.558511
		// west = 6 = 2.329152
		out.println("nord = 6 =  " + north);
		out.println("sued = 6 =  " + south);
		out.println("ost = 6 =  " + east);
		out.println("west = 6 =  " + west);

		out.println("scale_map = 6 =  0");
		// scale_area = 6 = 3.731021E-07
		// calculate scale_area with map extention and image size
		double scale_area = (north - south) * (east - west);
		scale_area = scale_area / (imageWidth * imageHeight);
		out.print("scale_area = 6 =  ");
		out.println(scale_area);
		
		// map_w = 4 =  2816
		// map_h = 4 =  1280
		if(this.imageWidth > 0) {
			out.print("map_w = 4 =  ");
			out.println(this.imageWidth);
		}
		if(this.imageHeight > 0) {
			out.print("map_h = 4 =  ");
			out.println(this.imageHeight);
		}

		// proj_mode = 10 = frei
		out.print("proj_mode = 10 = ");
		out.println(proj_mode);

		// datum1 = 10 = European 1950# 6378388# 297#-87#-98#-121#
		out.print("datum1 = 10 = ");
		out.println(getDatumTtqvDesc());

		// c1_x = 7 = 33
		// c1_y = 7 = 63
		// c2_x = 7 = 3360
		// c2_y = 7 = 66
		for (int i = 1; i <= 9; i++) {
			if (i <= calPoints.size()) {
				int[] point = calPoints.get(i - 1);
				out.println("c" + i + "_x = 7 =  " + point[0]);
				out.println("c" + i + "_y = 7 =  " + point[1]);
			} else {
				// do nothing (not to have bug when importing in Global Mapper)
				// out.println("c" + i + "_x = 7 =  -1");
				// out.println("c" + i + "_y = 7 =  -1");
			}
		}

		// c1_lat = 7 = 2
		// c1_lon = 7 = 1
		for (int i = 1; i <= 9; i++) {
			if (i <= calCoords.size()) {
				Coord coordCal = (Coord) calCoords.get(i - 1);
				out.println("c" + i + "_lat = 7 =  " + coordCal.latit);
				out.println("c" + i + "_lon = 7 =  " + coordCal.longit);
			} else {
				// do nothing (not to have bug when importing in Global Mapper)
				// out.println("c" + i + "_lat = 7 =  0");
				// out.println("c" + i + "_lon = 7 =  0");
			}
		}

		/*
		 * // un fichier par Track for (int i = 0; i < configGps.lTrack.size() ;
		 * i++) { // nb points de la trace out.println(lTrackPt.size()); //
		 * ligne du type : // 43.016640, 1.500642,1,0,36793.3245486,
		 * 24-sept.-00, 07:47:21 TrackPoint tp = (TrackPoint) lTrackPt.get(iT);
		 * Coord coord = tp.getCoord(); out.print(" ");
		 * out.print(formCoord.format(coord.latit)); out.print(", ");
		 * out.print(formCoord.format(coord.longit)); //out.println(",0, 32767,
		 * 0.0000000, , ");
		 */
		out.flush();
	}

	/**
	 * calFileName is calculated with imageFileName.
	 */
	public void calculateCalibrationFileName() {
		// extends : Guadalajara 536-IV_png.cal
		this.calFileName = this.imageFileName.replace('.', '_') + ".cal";
	}

	/**
	 * Create new calibration from Ozi calibration.
	 * 
	 * @param ozi
	 * @param sortCorner
	 * @return new CalTtqv
	 */
	public static CalTtqv CreateCalFromOzi(CalOzi ozi, boolean sortCorner) {
		CalTtqv ttqv = new CalTtqv();
		ttqv.copyAttributes(ozi);

		if (sortCorner) {
			// priorité aux points des angles
			ttqv.sortPoints();
		}

		ttqv.calculateCalibrationFileName();

		return ttqv;
	}

}
