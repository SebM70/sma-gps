/*
 * ConvertMpsTxtToOzi.java
 * 
 * Created on 18 octobre 2002, 21:04
 */

package sma;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import sma.gps.fich.FichGps;
import sma.gps.fich.FichPcx5;
import sma.gps.model.Coord;
import sma.gps.model.Waypoint;

/**
 * @author smarsoll
 * @version
 */
public class ConvertPcx5ToPolish {

	/** Creates new ConvertMpsTxtToOzi */
	public ConvertPcx5ToPolish() {
	}

	static String poiMapFile = null;
	static Map _poiMap = null;
	static String outPutDir = ".";

	static PrintStream out = System.out;

	// translation table from Garmin icon to POI definition
	static Map getPoiMap() {
		if (_poiMap == null) {
			_poiMap = new HashMap(100);
			if (poiMapFile != null)
				loadMap(poiMapFile);
		}
		return _poiMap;
	}
	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String args[]) {
		
		String mpsTxtFileMask = args[0];
		Iterator iF = SmaTool.getFileNames(mpsTxtFileMask).iterator();
		while (iF.hasNext()) {
			args[0] = (String) iF.next();
			System.out.println(args[0]);
			main2(args);
		}

	}
	public static void main2(String args[]) {
		String mpsTxtFileName = args[0];
		
		File f = new File(mpsTxtFileName);
		//String oziFileName = args[1];

		//System.out.println(mpsTxtFileName + " - " + oziFileName);

		FichGps source = new FichPcx5();
		source.setNomFichier(mpsTxtFileName);
		double maxEst = +9999;
		double minEst = -9999;
		double minNorth = -9999;
		double maxNorth = +9999;

		int levels = 2;
		// d�code param�tres
		for (int i = 1; i < args.length; i++) {
			if (args[i].startsWith("-maxE")) {
				maxEst = Double.parseDouble(args[i].substring(5));
			} else if (args[i].startsWith("-minE")) {
				minEst = Double.parseDouble(args[i].substring(5));
			} else if (args[i].startsWith("-minN")) {
				minNorth = Double.parseDouble(args[i].substring(5));
			}
			if (args[i].startsWith("-maxN")) {
				maxNorth = Double.parseDouble(args[i].substring(5));
			}
			if (args[i].startsWith("-levels")) {
				levels = Integer.parseInt(args[i].substring(7));
			} else if (args[i].startsWith("-map")) {
				poiMapFile = args[i].substring(4);
			} else if (args[i].startsWith("-out")) {
				outPutDir = args[i].substring(4);
			}

		}

		if (source.isCompatible()) {
			try {
				source.loadFile();

				// open target
				System.out.println(f.getName());
				out =
					new PrintStream(
						new FileOutputStream(outPutDir + "\\"+ f.getName() + ".txt"));

				int sym;
				String rgn;
				String type;
				Coord c;

				DecimalFormatSymbols fs = new DecimalFormatSymbols();
				fs.setDecimalSeparator('.');
				DecimalFormat formCoord = new DecimalFormat("##0.0000000", fs);

				//System.out.println("; " + mpsTxtFileName);
				List lstWpt = source.configGps.lWaypoint;
				Map poiMap;
				Iterator it = lstWpt.iterator();
				while (it.hasNext()) {
					
					Waypoint wpt = (Waypoint) it.next();
					c = wpt.coord;
					if ((c.latit > minNorth)
						& (c.latit <= maxNorth)
						& (c.longit > minEst)
						& (c.longit <= maxEst)) {
						poiMap = getPoiMap();
						//sym = Integer.parseInt( wpt.symbol );

						String[] translate = (String[]) poiMap.get(wpt.symbol);
						if (translate == null) {
							//rgn = "10";
							//type = "0x5e00";
							translate = (String[]) poiMap.get("default");
							rgn = translate[1];
							type = translate[2];
							out.println("; " + wpt.symbol + "  def");
						} else {
							rgn = translate[1];
							type = translate[2];
							out.println("; " + wpt.symbol);
						}

						out.println("[RGN" + rgn + "]");
						out.println("Type=" + type);
						out.print("Label=");
						out.println(wpt.name);
						// default 2 levels
						out.println("levels=" + levels);
						out.print("Origin0=(");
						out.print(formCoord.format(wpt.coord.latit));
						out.print(",");
						out.print(formCoord.format(wpt.coord.longit));
						out.println(")");
						out.println("[END]");
					}

				}

				/*
				 * FichOzi cible = new FichOzi(); cible.configGps =
				 * source.configGps; // l'extension sera ajout�e
				 * automatiquement cible.baseName = source.getNomFichier();
				 * out.println("avant saveFile " + cible.baseName);
				 */

			} catch (Exception e) {
				out.println("Pb... ) " + e);
				e.printStackTrace();
			}
			out.close();
			out = null;

		}

	}

	// load the translation Map from a CSV file
	static void loadMap(String mapFile) {
		try {
			BufferedReader in = new BufferedReader(new FileReader(mapFile));
			//int num_ligne = 0;
			String ligne;
			String[] decode;

			while ((ligne = in.readLine()) != null) {
				ligne = ligne.trim();

				if (!ligne.equals("")) {
					decode = SmaTool.StringtoArray(ligne, ",");
					_poiMap.put(decode[0], decode);
					//out.println(decode[0]);
				}

			}

			in.close();
			in = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
