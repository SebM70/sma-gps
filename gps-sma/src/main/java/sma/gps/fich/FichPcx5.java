/*
 * FichPcx5.java
 *
 * Created on 1 septembre 2001, 16:29
 */

package sma.gps.fich;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import sma.gps.model.Coord;
import sma.gps.model.Track;
import sma.gps.model.Waypoint;

/**
 * Gére les fichier de type PCX5
 * 
 * @author MARSOLLE
 */
public class FichPcx5 extends FichGps {
    // header courant, ex: H  LATITUDE    LONGITUDE    DATE      TIME     ALT   ;track
    private String curHeader;
    private Map curHeadMap;
    
    /** Creates new FichPcx5 */
    public FichPcx5() {
    }
    
    public void decodeHeader() {
        curHeadMap = new HashMap();
        StringTokenizer st = new StringTokenizer(curHeader);
        String lastTitre = null;
        while (st.hasMoreTokens()) {
            String titre = st.nextToken();
            int[] tint = { -2, -2}; //new int[2];
            curHeadMap.put(titre, tint);
            tint[0] = curHeader.indexOf(titre);
            // derni�re pos, par d�faut le dernier car
            tint[1] = titre.length() - 1;
            if (lastTitre != null) {
                ((int[]) curHeadMap.get(lastTitre))[1] = tint[0] - 1;
            }
            lastTitre = titre;
        }
        // le dernier titre
        ((int[]) curHeadMap.get(lastTitre))[1] = curHeader.length();
    }
    public String getElement(String titre, String ligne) {
        int[] tint = (int[]) curHeadMap.get(titre);
        //System.out.println(titre + "   " + tint[0] + "   " + tint[1]);
        int fin = tint[1];
        if (fin > ligne.length()) fin = ligne.length();
        return ligne.substring(tint[0], fin).trim();
    }
    
    public void saveFile() throws IOException {
    }
    
    public void loadFile() throws IOException {
        BufferedReader in = new  BufferedReader( new FileReader(nomFichier) );
        String ligne;
        String curTrackName = null;
        //int c;
        
        curHeader = null;
        Track curTrack = null;
        boolean nextFirst = true; // prochain trackpoint est un 1er
        //while ((c = in.read()) != -1) {
        while ((ligne = in.readLine()) != null) {
            ligne = ligne.trim();
            // ignore les lignes vides
            if (ligne.length() > 0) {
                char car0 = ligne.charAt(0);
                
                // nom de Track
                if (ligne.startsWith("H  TN")) {
                    curTrack = new Track();
                    configGps.lTrack.add(curTrack);
                    nextFirst = true;
                    curTrackName = ligne.substring(5).trim();
                    curTrack.nom = curTrackName;
                    //configGps.lTrack.add(curTrack);
                    continue;
                }
                
                if (car0 == 'H') {
                    curHeader = ligne;
                    //System.out.println("[" + ligne + "]");
                    decodeHeader();
                    // on change de courant
                    //curTrack = null;
                    nextFirst = true;
                    continue;
                }
                
                if (car0 == 'M') { // map Datum
                    configGps.datum = ligne.substring(5,25).trim();
                    //System.out.println("Datum ; " + configGps.datum);
                    continue;
                }
                
                if (car0 == 'U') { // D ou D M ou D M S
                    ligne.substring(3).trim();
                    //System.out.println("Coord : " + ligne.substring(3).trim());
                    continue;
                }
                if (car0 == 'W') { // waypoint
                    Waypoint wpt = new Waypoint();
                    Coord nCoord = new Coord();
                    // aval le T
                    nCoord.setLatitude(getElement("LATITUDE", ligne));
                    nCoord.setLongitude(getElement("LONGITUDE", ligne));
                    wpt.coord = nCoord;
                    wpt.name = getElement("IDNT", ligne);
                    //System.out.println("name : " + wpt.name);
                    wpt.comment = getElement("DESCRIPTION", ligne);
                    // dernier nombre avant chiffre
                    
                    wpt.symbol = ligne.substring(ligne.lastIndexOf(' ')+1);
                    //System.out.println("ligne : " + ligne);
                    //System.out.println("wpt.symbol : [" + wpt.symbol + "]");
                    //wpt.symbol = getElement("SYMBOL", ligne);
                    //System.out.println("comment : " + wpt.comment);
                    configGps.lWaypoint.add(wpt);
                    continue;
                }
                
                // 1 ligne de trace
                if (car0 == 'T') {
                    if (curTrack == null) {
                        // nouvelle trace non nomm�e
                        curTrack = new Track();
                        configGps.lTrack.add(curTrack);
                        if (curTrackName == null) {
                            curTrack.nom = "Track" + configGps.lTrack.size();
                        } else {
                            curTrack.nom = curTrackName ;
                        }
                        
                    }
                    StringTokenizer st = new StringTokenizer(ligne);
                    Coord nCoord = new Coord();
                    // aval le T
                    st.nextToken();
                    nCoord.setLatitude(st.nextToken());
                    nCoord.setLongitude(st.nextToken());
                    curTrack.add(nCoord, nextFirst);
                    nextFirst = false;
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
        if (ext.equals(".TRK") || ext.equals(".GRM") || ext.equals(".RTE") || ext.equals(".WPT")) {
            boolean comp = false;
            try {
                BufferedReader in = new  BufferedReader( new FileReader(nomFichier) );
                String ligne;
                while ((ligne = in.readLine()) != null) {
                    ligne = ligne.trim();
                    // ignore les lignes vides
                    if (ligne.length() > 0) {
                        if (ligne.equals("H  SOFTWARE NAME & VERSION")) {
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
