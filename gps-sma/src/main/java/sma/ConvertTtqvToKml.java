/*
 * ConvertMpsTxtToOzi.java
 *
 * Created on 18 octobre 2002, 21:04
 */

package sma;

import sma.gps.fich.ttqv.FichTtqv3;
import sma.gps.model.Track;

/**
 * 
 * @author smarsoll
 * @version
 */
public class ConvertTtqvToKml {

	/** Creates new ConvertMpsTxtToOzi */
	public ConvertTtqvToKml() {
	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String args[]) {
		FichTtqv3 db = new FichTtqv3(args[0]);

		for (int i = 1; i < args.length; i++) {
			String arg = args[i];
			if (arg.startsWith("-altitudeMode:")) {
				// possible values: clampToGround absolute relativeToGround
				Track.altitudeMode = arg.substring("-altitudeMode:".length());
			}

		}

		try {
			db.connect();
			db.loadCatalog();
			db.saveAsKml();

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

}
