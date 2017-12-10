package sma;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sma.gmap.Dalle;
import sma.gmap.DalleMichelin;
import sma.gmap.MichelinMap;

/**
 * import maps from Googlemap to TTQV format
 * @author smarsoll
 *
 */
public class ImportMichelinMap {
	
	
	/** Log4J */
	private static final Logger sLog = LoggerFactory.getLogger(ImportMichelinMap.class);


	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
	
		long debut = System.currentTimeMillis();
		sLog.info("starting...");
		
//		DalleMichelin d = new DalleMichelin(DalleMichelin.SERIE_ESP_400, 14, 18);
//		sLog.info(d.getUrlAdress());

		Dalle.setSAVE_PATH("D:/map/Java/tmp");
	
		MichelinMap gm = new MichelinMap(DalleMichelin.SERIE_ESP_400, 19, 30, 19, 30);
		int nbDalle = gm.getNbDalles();
		sLog.info("Serie = " + DalleMichelin.SERIE_ESP_400 + "  nb dalles = " + nbDalle + "    enter Y to proceed (less than 1000 is OK).");
		gm.importDallesMichelin(3);
		
		// sauve en un gros fichier
		gm.saveMap();
		
		double tempstotal = (System.currentTimeMillis() - debut)/1000.0;
		sLog.info("...the end in " + tempstotal + " secondes");

	}

}
