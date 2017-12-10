/*
 * ConvertMpsTxtToOzi.java
 *
 * Created on 18 octobre 2002, 21:04
 */

package sma;

import sma.gps.fich.FichGps;
import sma.gps.fich.FichMpsTxt;
import sma.gps.fich.FichOzi;

/**
 * 
 * @author smarsoll
 */
public class ConvertMpsTxtToOzi {

	/** Creates new ConvertMpsTxtToOzi */
	public ConvertMpsTxtToOzi() {
	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String args[]) {
		String mpsTxtFileName = args[0];
		// String oziFileName = args[1];

		// System.out.println(mpsTxtFileName + " - " + oziFileName);

		// FichGps source = new FichPcx5();
		FichGps source = new FichMpsTxt();
		source.setNomFichier(mpsTxtFileName);

		// decode parameters
		/*
		 * for (int i=0; i< args.length; i++) { if (args[i].startsWith("-attr") ){ attrString = args[i].substring(5); } if
		 * (args[i].startsWith("-F") ){ filter = args[i].substring(2); } }
		 */

		if (source.isCompatible()) {
			try {
				source.loadFile();
				FichOzi cible = new FichOzi();
				cible.configGps = source.configGps;
				// l'extension sera ajoutée automatiquement
				cible.baseName = source.getNomFichier();
				System.out.println("avant saveFile " + cible.baseName);
				cible.saveFile();

			} catch (Exception e) {
				System.out.println("Pb... ) " + e);
				e.printStackTrace();
			}

		}

	}

}
