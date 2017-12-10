/*
 * ConvertCalOziToTtqv.java
 *
 * Created on 6 ao�t 2002, 20:42
 */

/**
 *
 * @author  MARSOLLE
 * @version
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sma.fichier.FilterEnd;
import sma.gps.cal.CalOzi;
import sma.gps.cal.CalTtqv;
import sma.gps.cal.Calibration;

/**
 * Convert OZI calibration file to TTQV calibration file.
 * 
 * @author marsolle
 * 
 */
public class ConvertCalOziToTtqv {

	public static boolean useCorner = false;
	public static boolean sortCorner = false;
	// static PrintStream out = System.out;

	/** Log4J */
	private static final Logger sLog = LoggerFactory.getLogger(ConvertCalOziToTtqv.class);

	/** Creates new ConvertCalOziToTtqv */
	public ConvertCalOziToTtqv() {
	}

	/**
	 * @param args
	 *            the command line arguments
	 * 
	 *            ex : GAROTXA1.map png
	 */
	public static void main(String args[]) {

		sLog.info(Calibration.NAME_VERSION);
		if (args.length < 1) {
			sLog.info("Exemple : java ConvertCalOziToTtqv \"C:\\tmp\\*.map\" -epng -corner");
			sLog.info("-epng uses png extension instead of original extention of image files");
			sLog.info("-corner is to force to use the 4 corner points of Ozi");
			sLog.info("-sortcorner to put corner points in first (so you can remove central points when manually recalibrating)");
			System.exit(1);
		}

		String fileMask = args[0];
		String newExt = null;

		// décode paramètres
		for (int i = 0; i < args.length; i++) {
			if (args[i].startsWith("-e")) {
				newExt = args[i].substring(2);
			}
			if (args[i].equals("-corner")) {
				useCorner = true;
			}
			if (args[i].equals("-sortcorner")) {
				sortCorner = true;
			}
		}

		/*
		 * if (args.length > 1) { newExt = args[1]; }
		 * 
		 * if (args.length > 2) { useCorner = true; }
		 */

		System.out.println(fileMask);

		// System.out.println( "File:" + lstFile.getAbsolutePath() );
		// System.out.println( "File:" + lstFile.list().length );
		if (fileMask.indexOf('*') >= 0) {
			sLog.info("several files to process");
			FilterEnd filtre = new FilterEnd(fileMask.substring(fileMask.indexOf('*') + 1));

			File leFichier = new File(fileMask);
			File repertoire = leFichier.getParentFile();
			// case no parent dir
			if (repertoire == null)
				repertoire = new File(".");
			File[] lesFichiers = repertoire.listFiles(filtre);
			for (int i = 0; i < lesFichiers.length; i++) {
				// System.out.println( lesFichiers[i].getPath() );
				convertOneFile(lesFichiers[i].getPath(), newExt);
			}
		} else
			convertOneFile(fileMask, newExt);
	}

	// convert 1 map file to a cal file
	public static void convertOneFile(String fileName, String newImageFileExt) {
		File fichOzi = new File(fileName);
		try {
			InputStream ins = new FileInputStream(fichOzi);
			CalOzi ozi = CalOzi.readOziCalibration(ins, fichOzi.getName(), useCorner);
			ins.close();
			ozi.setDirName(fichOzi.getParentFile().getAbsolutePath());

			CalTtqv ttqv = CalTtqv.CreateCalFromOzi(ozi, sortCorner);

			if (newImageFileExt != null) {
				// change image file extension (ex : .bmp = > .png)
				ttqv.changeImageExtension(newImageFileExt);
				ttqv.calculateCalibrationFileName();
			}


			// check target exist
			if (ttqv.getFile().exists()) {
				sLog.info("Already existing " + ttqv.getFile());
				return;
			}

			// File fich = new File(ttqv.calFileName);
			// System.out.println( "File:" + fich.getAbsolutePath() );

			OutputStream outCalFile = new FileOutputStream(ttqv.getCalFileName());
			// create new cal file
			ttqv.saveFile(outCalFile);
			outCalFile.close();

		} catch (Exception e) {
			sLog.error("error with " + fileName, e);
		}

	}

}
