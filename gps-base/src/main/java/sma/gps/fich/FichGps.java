/*
 * FichGps.java
 *
 * Created on 1 septembre 2001, 16:45
 */

package sma.gps.fich;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import sma.gps.model.ConfigGps;
import sma.gps.model.Zone;

/**
 * Base class to map on a File that contains GPS data.
 * 
 * @author MARSOLLE
 */
public abstract class FichGps extends Object {

	protected String nomFichier;

	// nom de base sans extension
	public String baseName;

	public ConfigGps configGps = new ConfigGps();

	/** Creates new FichGps */
	public FichGps() {
	}

//	public FichGps(String nomF) {
//		super();
//		setNomFichier(nomF);
//	}

	public void setNomFichier(String nomF) {
		nomFichier = nomF;
	}

	public String getNomFichier() {
		return nomFichier;
	}


	public String[] getFirstLines(int nbLines) {
		String[] tLines = new String[nbLines];
		int nbLu = 0;
		try {
			BufferedReader in = new BufferedReader(new FileReader(nomFichier));
			String ligne;
			while ((ligne = in.readLine()) != null) {
				ligne = ligne.trim();
				// ignore les lignes vides
				if (ligne.length() > 0) {
					tLines[nbLu] = ligne;
					nbLu++;
					if (nbLu >= nbLines) {
						break;
					}
				}
			}
			in.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}

		return tLines;
	}

	public void loadFile() throws IOException {
	}

	public abstract boolean isCompatible();

	public void filterZone(Zone z) {
		configGps.filterZone(z);
	}

	// log debug messages
	public static void log(String arg) {
		System.out.println(arg);
	}

	public String getBaseName() {
		return baseName;
	}

	public void setBaseName(String baseName) {
		this.baseName = baseName;
	}

}
