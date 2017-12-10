/*
 * FichOzi.java
 *
 * Created on 1 septembre 2001, 16:28
 */

package sma.gps.fich;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Iterator;
import java.util.List;

import sma.gps.model.Coord;
import sma.gps.model.Track;
import sma.gps.model.TrackPoint;
import sma.gps.model.Waypoint;

/**
 *
 * @author  MARSOLLE
 */
public class FichOzi extends FichGps {
  
  /** Creates new FichOzi */
  public FichOzi() {
  }
  
  public void loadFile() throws IOException {
    BufferedReader in = new  BufferedReader( new FileReader(nomFichier) );
    String ligne;
    int c;
    
    //while ((c = in.read()) != -1) {
    do {
      ligne = in.readLine();
      System.out.println("X" + ligne + "X");
    } while (ligne != null);
    
    in.close();
  }
  
  public void saveFile() throws IOException {
    PrintStream out;
    DecimalFormatSymbols fs = new DecimalFormatSymbols();
    fs.setDecimalSeparator('.');
    DecimalFormat formCoord = new DecimalFormat("##0.000000", fs);
    
    // un fichier par Track
    for (int i = 0; i < configGps.lTrack.size() ; i++) {
      Track curTrack = (Track) configGps.lTrack.get(i);
      String nm = curTrack.nom.replace(' ', '_');
      if (nm.endsWith(".plt") || nm.endsWith(".PLT"))
        nm = nm.substring(0, nm.length() - 4);
      nm = nm.replace('\\', '_').replace('/', '_');
      List lTrackPt = curTrack.getLTrackPt();
      if (lTrackPt.size() == 0) {
        System.out.println("  0 points in " +curTrack.nom + "T" + i + ".plt");
      } else {
        System.out.println(curTrack.nom + "T" + i + ".plt");
        //out = new PrintStream(new FileOutputStream(baseName + "T" + i + ".plt") );
        out = new PrintStream(new FileOutputStream(nm + "." + i + ".plt") );
        out.println("OziExplorer Track Point File Version 2.0");
        out.println(configGps.datum);
        out.println("Altitude is in Feet");
        out.println("Reserved 3");
        
        out.println("0,2,255,"+ curTrack.nom +",1");
        System.out.println(curTrack.nom);
        
        // nb points de la trace
        out.println(lTrackPt.size());
        Iterator it = lTrackPt.iterator();
        //for (int iT = 0; iT < lTrackPt.size() ; iT++) {
        while (it.hasNext()) {
          // ligne du type :
          //   43.016640,   1.500642,1,0,36793.3245486, 24-sept.-00, 07:47:21
          TrackPoint tp = (TrackPoint) it.next();
          Coord coord = tp.getCoord();
          out.print("  ");
          out.print(formCoord.format(coord.latit));
          out.print(",   ");
          out.print(formCoord.format(coord.longit));
          //out.println(",0,  32767,    0.0000000,   ,   ");
          out.print(",");
          if (tp.isFirst()) {out.print("1");}
          else {out.print("0");}
          out.println(",0,36793.3245486, 32700.32700");
        }
        out.close();
      }
    }
    // Waypoint
    if (configGps.lWaypoint.size() > 0) {
      out = new PrintStream(new FileOutputStream(baseName + ".wpt") );
      out.println("OziExplorer Waypoint File Version 1.0");
      out.println(configGps.datum);
      out.println("Reserved 2");
      out.println("Reserved 3");
      for (int iW = 0; iW < configGps.lWaypoint.size() ; iW++) {
        Waypoint wpt = (Waypoint) configGps.lWaypoint.get(iW);
        Coord coord = wpt.coord;
        out.print(iW);out.print(",");
        out.print(wpt.name);out.print(" , ");
        out.print(formCoord.format(coord.longit)); out.print(", ");
        out.print(formCoord.format(coord.latit)); out.print(",");
        out.print("37077.80786,"); // date ?
        out.print(wpt.symbol);out.print(",");
        out.print(" 4,         0,     65535,");
        out.print(wpt.comment.replace(',', '�'));out.print(",");
        //, 0, 0,    0,32766
        out.print(" 0, 0,");
        out.print(wpt.proximity);out.print(",");
        out.println("32766");
        
      }
      
      out.close();
    }
  }
  
  // dit si le fichier est compatible avec la classe
  public boolean isCompatible() {
    // 4 derners car
    String ext = nomFichier.substring(nomFichier.length() - 4).toUpperCase();
    if (ext.equals(".PLT") || ext.equals(".WPT") || ext.equals(".RTE") || ext.equals(".EVT")) {
      boolean comp = false;
      try {
        BufferedReader in = new  BufferedReader( new FileReader(nomFichier) );
        String ligne;
        while ((ligne = in.readLine()) != null) {
          ligne = ligne.trim();
          // ignore les lignes vides
          if (ligne.length() > 0) {
            if (ligne.startsWith("OziExplorer")) {
              comp = true;
            } else {break;}
          }
        }
        return comp;
      } catch (IOException e) {
        System.out.println(e.getMessage());
        return false;
      }
      
    } else return false;
  }
  
}
