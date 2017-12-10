/*
 * FichMpsTxt.java
 *
 * Created on 6 septembre 2001, 20:16
 */

package sma.gps.fich;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

import sma.gps.model.Coord;
import sma.gps.model.Track;

/**
 *
 * @author  MARSOLLE
 */
public class FichMpsTxt extends FichGps {
  
    /** Creates new FichMpsTxt */
  public FichMpsTxt() {
  }
  
  public void loadFile() throws IOException {
    BufferedReader in = new  BufferedReader( new FileReader(nomFichier) );
    String curHeader;
    String ligne;
    StringTokenizer sto;
    int c;
    
    curHeader = null;
    Track curTrack = null;
    //while ((c = in.read()) != -1) {
    while ((ligne = in.readLine()) != null) {
      ligne = ligne.trim();
      // ignore les lignes vides
      if (ligne.length() > 0) {
        char car0 = ligne.charAt(0);
        
        // nom de Track
        if (ligne.startsWith("Track\t")) {
          curTrack = new Track();
          sto = new StringTokenizer(ligne, "\t");
          sto.nextToken();
          curTrack.nom = sto.nextToken().trim();
          configGps.lTrack.add(curTrack);
          continue;
        }
        
        // header
        if (ligne.startsWith("Header\t")) {
          curHeader = ligne;
          System.out.println("[" + ligne + "]");
          continue;
        }
        
        // datum
        if (ligne.startsWith("Datum\t")) {
          sto = new StringTokenizer(ligne, "\t");
          sto.nextToken();
          configGps.datum = sto.nextToken().trim();
          System.out.println("Datum ; " + configGps.datum);
          continue;
        }
        
        // unit�
        if (ligne.startsWith("Grid\t")) { // extends : Grid	Lat/Lon hddd.ddddd�
          ligne.substring(3).trim();
          System.out.println("Coord : " + ligne.substring(3).trim());
          continue;
        }
        
        // 1 ligne de trace
        if (ligne.startsWith("Trackpoint\t")) {
          if (curTrack == null) {
            // nouvelle trace non nomm�e
            curTrack = new Track();
            configGps.lTrack.add(curTrack);
            curTrack.nom = "Track" + configGps.lTrack.size();
          }
          StringTokenizer st = new StringTokenizer(ligne, " \t");
          Coord nCoord = new Coord();
          
          st.nextToken(); // aval le Trackpoint
          nCoord.setLatitude(st.nextToken());
          nCoord.setLongitude(st.nextToken());
          curTrack.add(nCoord);
          continue;
        }
        
      }
      
    }
    
    in.close();
    in = null;
  }
  
  
  public boolean isCompatible() {
    // 4 derners car
    String ext = nomFichier.substring(nomFichier.length() - 4).toUpperCase();
    if (ext.equals(".TXT")) {
      String[] debut = getFirstLines(2);
      /* ex:
       Grid	Lat/Lon hddd.ddddd�
       Datum	WGS 84
       */
      //return true;
      return (debut[0].startsWith("Grid\t") && debut[1].startsWith("Datum\t"));
    } else return false;
  }
}
