/*
 * ConvertMpsTxtToOzi.java
 *
 * Created on 18 octobre 2002, 21:04
 */

package sma;

import java.util.Iterator;

import sma.gps.fich.FichGps;
import sma.gps.fich.FichOzi;
import sma.gps.fich.FichPcx5;
import sma.gps.model.Zone;

/**
 *
 * @author  smarsoll
 */
public class ConvertPcx5ToOzi {
  
  /** Creates new ConvertMpsTxtToOzi */
  public ConvertPcx5ToOzi() {
  }
  
  /**
   * @param args the command line arguments
   */
  public static void main(String args[]) {
    String maskFileName = args[0];
    //String oziFileName = args[1];
    
    //System.out.println(mpsTxtFileName + " - " + oziFileName);
    
    double maxEst = +9999;
    double minEst = -9999;
    double minNorth = -9999;
    double maxNorth = +9999;
    for (int i=1; i< args.length; i++) {
      if (args[i].startsWith("-maxE") ){
        maxEst = Double.parseDouble(args[i].substring(5));
      } else if(args[i].startsWith("-minE") ){
        minEst = Double.parseDouble(args[i].substring(5));
      } else if (args[i].startsWith("-minN") ){
        minNorth = Double.parseDouble(args[i].substring(5));
      } else if (args[i].startsWith("-maxN") ){
        maxNorth = Double.parseDouble(args[i].substring(5));
      }
    }
    
    Iterator iF = SmaTool.getFileNames(maskFileName).iterator();
    while (iF.hasNext()) {
      
      String mpsTxtFileName = (String) iF.next();
      //String oziFileName = args[1];
      
      //System.out.println(mpsTxtFileName + " - " + oziFileName);
      
      FichGps source = new FichPcx5();
      source.setNomFichier( mpsTxtFileName );
      
      
      if ( source.isCompatible() ) {
        try {
          source.loadFile();
          source.filterZone(new Zone(maxNorth, minNorth, maxEst, minEst));
          FichOzi cible = new FichOzi();
          cible.configGps = source.configGps;
          // l'extension sera ajout�e automatiquement
          cible.baseName = source.getNomFichier();
          System.out.println("avant saveFile " + cible.baseName);
          cible.saveFile();
          
        } catch (Exception e) {
          System.out.println( "Pb... ) " + e );
          e.printStackTrace();
        }
        
        
      }
    }
  }
  
}
