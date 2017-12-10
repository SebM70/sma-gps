/*
 * CalOzi.java
 *
 * Created on 6 ao�t 2002, 20:40
 */

package sma.gps.cal;

/**
 * 
 * @author MARSOLLE
 * @version
 */
import geotransform.coords.Gdc_Coord_3d;
import geotransform.coords.Utm_Coord_3d;
import geotransform.transforms.Utm_To_Gdc_Converter;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sma.gps.model.Coord;

public class CalOzi extends Calibration {

	// Log4J
	private static final Logger sLog = LoggerFactory.getLogger(CalOzi.class);

	// for the 4 corner points
	public List<int[]> cornPoints = new ArrayList<int[]>(20);
	public List<Coord> cornCoords = new ArrayList<Coord>(20);
	static PrintStream out = System.out;

	/** Creates new CalOzi */
	public CalOzi() {
	}

	public void loadFile(InputStream ins) throws IOException {
		// BufferedReader inReader = new BufferedReader(new FileReader(getFile().getAbsolutePath()));
		BufferedReader inReader = new BufferedReader(new InputStreamReader(ins));
		int num_ligne = 0;
		String ligne;

		while ((ligne = inReader.readLine()) != null) {
			ligne = ligne.trim();
			num_ligne++;
			// ignore les lignes vides
			if (ligne.length() > 0) {

				// nom de fichier image
				// ex : C:\Dir\name.bmp
				if (num_ligne == 3) {
					sLog.debug("ligne={}", ligne);
					imageFileName = CalUtil.getFileNameFromPath(ligne);
					sLog.debug("imageFileName={}", imageFileName);
					continue;
				}

				// map datum
				if (num_ligne == 5) {
					mapDatum = ligne.substring(0, ligne.indexOf(','));
					sLog.debug("mapDatum :" + mapDatum);
					continue;
				}

				// point de calibration
				if (ligne.startsWith("Point")) {
					try {
						String[] lstElem = decodeLigneVirgule(ligne);
						// s'il y a un point de calibration (valeur en colone 2)
						if (lstElem[2].length() > 0) {
							// System.out.println("calibr :"+ lstElem[2] + " " +
							// lstElem[3]);
							int[] point = { Integer.parseInt(lstElem[2]), Integer.parseInt(lstElem[3]) };

							if ((lstElem[13].length() > 0) && (lstElem[14].length() > 0)) {
								// utm grid
								// System.out.println("utm :"+ lstElem[14] + ":"
								// + lstElem[15] + ":" + lstElem[13] + ":");
								Utm_Coord_3d utm = new Utm_Coord_3d(Double.parseDouble(lstElem[14]), Double
										.parseDouble(lstElem[15]), 0, Byte.parseByte(lstElem[13]), lstElem[16]
										.equals("N"));

								// degr�s
								Gdc_Coord_3d gdc = new Gdc_Coord_3d();

								// System.out.println("UTM:"+ utm);

								// Utm_To_Gdc_Converter.Init(new
								// WE_Ellipsoid());
								Utm_To_Gdc_Converter.Init(getElipsoid());

								// convert the points.
								Utm_To_Gdc_Converter.Convert(utm, gdc);

								// System.out.println("Pos:"+ gdc.latitude + " "
								// + gdc.longitude);
								Coord coordCal = new Coord();
								coordCal.latit = gdc.latitude;
								coordCal.longit = gdc.longitude;
								calCoords.add(coordCal);
							} else {
								// degr�s
								// 0 1 2 3 4 5 6 7 8 9 10 11
								// Point01,xy, 191, 433,in, deg, 26, 0.0000,N,
								// 6, 0.0000,E, grid, , , ,N
								// System.out.println("degr�s");
								Coord coordCal = new Coord();
								coordCal.latit = Double.parseDouble(lstElem[6])
										+ (Double.parseDouble(lstElem[7]) / 60.0);
								if (lstElem[8].equals("S"))
									coordCal.latit = -coordCal.latit;

								coordCal.longit = Double.parseDouble(lstElem[9])
										+ (Double.parseDouble(lstElem[10]) / 60.0);
								if (lstElem[11].equals("W"))
									coordCal.longit = -coordCal.longit;
								calCoords.add(coordCal);
							}
							calPoints.add(point);
						}
					} catch (NumberFormatException e) {
						out.println("line not recognized: " + e);
						out.println(ligne);
					}

					continue;
				}

				// point de corner
				// MMPXY,3,2358,4544
				if (ligne.startsWith("MMPXY,")) {
					// System.out.println("MMPXY,");
					String[] lstElem = decodeLigneVirgule(ligne);
					int[] point = { Integer.parseInt(lstElem[2]), Integer.parseInt(lstElem[3]) };
					cornPoints.add(point);
					if (point[0] > imageWidth)
						imageWidth = point[0];
					if (point[1] > imageHeight)
						imageHeight = point[1];
					continue;
				}
				// MMPLL,3, 2.560104, 42.010702
				if (ligne.startsWith("MMPLL,")) {
					// System.out.println("MMPLL,");
					String[] lstElem = decodeLigneVirgule(ligne);
					Coord coordCal = new Coord();
					if (ligne.indexOf('.') >= 0) {
						coordCal.latit = Double.parseDouble(lstElem[3]);
						coordCal.longit = Double.parseDouble(lstElem[2]);
					} else {
						// bug , instead of .
						sLog.warn("bug , instead of .");
						// MMPLL,2, 002,455276, 42,178590
						coordCal.latit = Double.parseDouble(lstElem[4] + "." + lstElem[5]);
						coordCal.longit = Double.parseDouble(lstElem[2] + "." + lstElem[3]);
					}
					cornCoords.add(coordCal);
					continue;
				}

			}

		}

		// No close internally ! inReader.close();
		sLog.debug(calCoords.size() + " calibrations points read");
	}



	// calculate map extentions
	public void calcExtent() {
		// init for generic algo
		north = -Double.MAX_VALUE;
		south = Double.MAX_VALUE;
		east = -Double.MAX_VALUE;
		west = Double.MAX_VALUE;
		for (int i = 1; i <= cornCoords.size(); i++) {
			Coord coordCal = (Coord) cornCoords.get(i - 1);
			if (coordCal.latit > north)
				north = coordCal.latit;
			if (coordCal.latit < south)
				south = coordCal.latit;

			if (coordCal.longit < west)
				west = coordCal.longit;
			if (coordCal.longit > east)
				east = coordCal.longit;
		}

	}

	public void generateCalibrPoints(int nbMin) {
		if (calPoints.size() < nbMin) {
			// il faut au moins 4 points de calibration
			int nbCorn = cornPoints.size();
			sLog.info("generating " + nbCorn + " points from the corner points");

			for (int i = 0; i < nbCorn; i++) {
				calPoints.add(cornPoints.get(i));
				calCoords.add(cornCoords.get(i));
			}
		}
	}

	public void calculateCalibrationFileName() {
		// extends : Guadalajara 536-IV_png.cal
		this.calFileName = this.imageFileName.replace(".jpg", "") + ".map";
	}

	public void saveFile() throws IOException {
		sLog.error("not coded !");

		String filePath = getFile().getAbsolutePath();
		sLog.info("creating calibration file " + filePath);
		PrintStream out;
		FileOutputStream fis = new FileOutputStream(filePath);
		out = new PrintStream(fis);
		// out = new OutputStreamWriter( new FileOutputStream( calFileName ),
		// "Cp850" );

		DecimalFormatSymbols fs = new DecimalFormatSymbols();
		fs.setDecimalSeparator('.');
		// format for minutes in OZI files
		DecimalFormat formCoord = new DecimalFormat("##0.0000", fs);

		out.println("OziExplorer Map Data File Version 2.2 by SMa");
		out.println(this.imageFileName);
		out.println("C:\\" + this.imageFileName);
		out.println("1 ,Map Code,");
		out.println("WGS 84,,   0.0000,   0.0000,WGS 84");
		out.println("Reserved 1");
		out.println("Reserved 2");
		out.println("Magnetic Variation,,,E");
		out.println("Map Projection,Latitude/Longitude,PolyCal,No,AutoCalOnly,No,BSBUseWPX,No");

		// c1_x = 7 = 33
		// c1_y = 7 = 63
		// c2_x = 7 = 3360
		// c2_y = 7 = 66

		// Point01,xy, 0, 0,in, deg, 33, 1.0726,N, 9, 9.0967,E, grid, , , ,N
		for (int i = 1; i <= 9; i++) {
			if (i <= calPoints.size()) {
				int[] point = (int[]) calPoints.get(i - 1);
				Coord coordCal = (Coord) calCoords.get(i - 1);
				out.print("Point0");
				out.print(i);
				out.print(",xy,");
				out.print(point[0]);
				out.print(",");
				out.print(point[1]);
				out.print(",in, deg,");
				int entier = (int) coordCal.latit;
				out.print(entier);
				out.print(",");
				out.print(formCoord.format((coordCal.latit - entier) * 60.0));
				if (coordCal.latit >= 0.0) {
					out.print(",N,");
				} else {
					out.print(",S,");
				}
				entier = (int) coordCal.longit;
				out.print(entier);
				out.print(",");
				out.print(formCoord.format((coordCal.longit - entier) * 60.0));
				if (coordCal.longit >= 0.0) {
					out.print(",E,");
				} else {
					out.print(",W,");
				}

				out.println(" grid, , , ,N");

			} else {
				// no calibration point
			}
		}

		// c1_lat = 7 = 2
		// c1_lon = 7 = 1
		
		

		out.println("Projection Setup,,,,,,,,,,");
		out.println("Map Feature = MF ; Map Comment = MC     These follow if they exist");
		out.println("Track File = TF      These follow if they exist");
		out.println("Moving Map Parameters = MM?    These follow if they exist");
		out.println("MM0,Yes");
		out.println("MMPNUM,4");
		for (int i = 1; i <= 4; i++) {
			if (i <= calPoints.size()) {
				// MMPXY,1,0,0
				int[] point = (int[]) calPoints.get(i - 1);
				//Coord coordCal = (Coord) calCoords.get(i - 1);
				out.print("MMPXY,");
				out.print(i);
				out.print(",");
				out.print(point[0]);
				out.print(",");
				out.println(point[1]);
			} else {
				// no calibration point
			}
		}		
		formCoord = new DecimalFormat("##0.000000", fs);
		for (int i = 1; i <= 4; i++) {
			if (i <= calPoints.size()) {
				// MMPLL,1,   9.151612,  33.017877
				//int[] point = (int[]) calPoints.get(i - 1);
				Coord coordCal = (Coord) calCoords.get(i - 1);
				out.print("MMPLL,");
				out.print(i);
				out.print(", ");
				out.print(formCoord.format(coordCal.longit));
				out.print(", ");
				out.println(formCoord.format(coordCal.latit));
			} else {
				// no calibration point
			}
		}			
//		The scale of the image meters/pixel, its calculated in the left / right image direction.
//		It is calculated each time OziExplorer is run, the value in the file is used when searching for maps of "more detailed" scale. 

		// longueur nord sud en m�tre
		//out.print("MM1B,");
		
		out.close();
		fis.close();
	}

	/**
	 * Create a new calibration file.
	 * 
	 * @param pIns
	 * @param pName
	 * @param useCorner
	 * @return
	 * @throws IOException
	 */
	public static CalOzi readOziCalibration(InputStream ins, String pName, boolean useCorner) throws IOException {
		CalOzi ozi = new CalOzi();
		ozi.calFileName = pName;
		// load Ozi cal file
		ozi.loadFile(ins);

		if (useCorner) {
			// check coherence of extents
			// Coord[] cornExt = Calibration.calculateExtents(ozi.cornCoords);
			// Coord[] calExt = Calibration.calculateExtents(ozi.calCoords);
			/*
			 * if ( (cornExt[1].latit - cornExt[0].latit) > 3*(calExt[1].latit - calExt[0].latit) ) { System.out.println(
			 * "corner latitude too much extended !" ); } else if ((cornExt[1].longit - cornExt[0].longit) > 3*(calExt[1].longit -
			 * calExt[0].longit)) { System.out.println( "corner longitude too much extended !" ); } else
			 */
			if (ozi.cornPoints.size() >= 4) {
				// use corner points instead of calibration points
				sLog.info("using corner points instead of calibration points");
				ozi.calPoints = ozi.cornPoints;
				ozi.calCoords = ozi.cornCoords;
			}
		} else {
			// if not enough calibr point
			ozi.generateCalibrPoints(4);
		}
		ozi.calcExtent();

		return ozi;
	}
}
