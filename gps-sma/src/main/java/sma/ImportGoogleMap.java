package sma;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sma.gmap.Dalle;
import sma.gmap.GoogleMap;
import sma.gmap.download.DownloadThread;
import sma.gps.cal.Calibration;
import sma.gps.fich.FichPcx5;
import sma.gps.model.Coord;
import sma.gps.model.Waypoint;

/**
 * import maps from Googlemap to TTQV format
 * @author smarsoll
 *
 */
public class ImportGoogleMap {
	
	
	/** Log4J */
	private static final Logger sLog = LoggerFactory.getLogger(ImportGoogleMap.class);


	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
//		String ieTmpDir = "C:\\Documents and Settings\\smarsoll\\Local Settings\\Temporary Internet Files";
//		String testUrl = "http://kh1.google.fr/kh?n=404&v=26&hl=fr&t=trttqrtttqrrqtsr";
//		// provisoirement
//		System.exit(1);
		
		// ==================== zoom level
		int zoom = 7; // 3 est bien pour les dunes
		
		// default is GoogleMapsatellite
		String mapType = Dalle.GM_SAT;
		boolean isVirtualEarth = false;
		boolean isCalibrationOzi = false;
		
		for (int i = 0; i < args.length; i++) {
			if (args[i].startsWith("-z")) {
				zoom = Integer.parseInt(args[i].substring(2));
			} else  if (args[i].startsWith("-tmin")) {
				DownloadThread.MIN_MS = Integer.parseInt(args[i].substring(5));
			} else  if (args[i].startsWith("-tmax")) {
				DownloadThread.MAX_MS = Integer.parseInt(args[i].substring(5));
			} else  if (args[i].startsWith("-tmp:")) {
				Dalle.setSAVE_PATH(args[i].substring(5));
			} else  if (args[i].startsWith("-save")) {
				GoogleMap.SAVE_PATH = args[i].substring(5) + "/";
			} else  if (args[i].startsWith("-t:")) {
				mapType = args[i].substring(3);
				isVirtualEarth = (Dalle.VE_PLAN.equals(mapType) | Dalle.VE_SAT.equals(mapType));
			} else  if (args[i].equals("-calozi")) {
				isCalibrationOzi = true;
			} 
			
		} 

		
		// TODO Auto-generated method stub
		double lon1 = 9.83453;
		double lat1 = 30.33368;
		double lon2 = 9.49653;
		double lat2 = 30.13033;
		
		//Dalle.setSAVE_PATH("D:/map/Java/tmp");
		
		// load a waypoint file to define area
		String wptFile = args[0];
		File f = new File(wptFile);
		sLog.info("using waypoints from " + f.getAbsolutePath());
		FichPcx5 fich = new FichPcx5();
		fich.setNomFichier(wptFile);
		fich.loadFile();
		
		List<Waypoint> lwpt = fich.configGps.lWaypoint;
		if (lwpt.size() < 2) {
			sLog.error("No enough waypoints in file [" + wptFile +"]  You need 2 waypoints at least !");
			System.exit(1);
		} else {
			Coord[] tCoord = Calibration.calculateExtentsWpts(lwpt);
			// Waypoint wpt;
			// wpt = lwpt.get(0);
			lat1 = tCoord[0].latit;
			lon1 = tCoord[0].longit;
			// wpt = lwpt.get(1);
			lat2 = tCoord[1].latit;
			lon2 = tCoord[1].longit;
		}
		

		
		GoogleMap gm = new GoogleMap(zoom, lat1, lon1, lat2, lon2);
		gm.setMapType(mapType);
		
		int nbDalle = gm.getNbDalles();
		if (nbDalle > 500) {
			sLog.warn("Zoom = " + zoom + "  nb dalles = " + nbDalle + "    enter Y to proceed (less than 1000 is OK).");
			
			// ask for confirmation
			Reader r = new BufferedReader(new InputStreamReader(System.in));
			StreamTokenizer st = new StreamTokenizer(r);
			st.nextToken();
			r.close();
			if (!st.sval.equalsIgnoreCase("Y")) {
				sLog.info("stop processing because you did not enter Y.");
				System.exit(1);
			}
		} else {
			sLog.info("Zoom = " + zoom + "  nb dalles = " + nbDalle);
		}
			
		
		long debut = System.currentTimeMillis();
		sLog.info("starting...");
		
		if (isVirtualEarth) {
			int nbThreads = 3;

			// download internet direct
			gm.importDallesFromInternet(nbThreads);
			gm.saveMapFromTmpDir();
		} else {
			// GoogleMap => dans le cache de Firefox
			gm.saveMapFromCache();
		}

		
		gm.saveCalibrationFile();
		if (isCalibrationOzi) {
			gm.saveCalibrationOzi();
		}
		
		double tempstotal = (System.currentTimeMillis() - debut)/1000.0;
		sLog.info("...the end in " + tempstotal + " secondes");

	}

}
