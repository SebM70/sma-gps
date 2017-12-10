/*
 * Filter.java
 *
 * Created on 15 mai 2003, 21:43
 */

package sma.dxf;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author  smarsoll
 */
public class Filter {
  
  /** Creates new Filter */
  public Filter() {
  }
  
  /**
   * @param args the command line arguments
   */
  public static void main(String args[]) {
    
    if (args.length < 1) {
      System.out.println( "Exemple : java  Filter <file name> " );
      System.exit(1);
    }
    
    GeoFilter geof = new GeoFilter();
    boolean geofUsed = false;
    
    // d�code param�tres
    for (int i=1; i< args.length; i++) {
      if (args[i].equals("-mps") ){
        DxfEntity.outputMode = DxfEntity.MOD_MPS;
      }
      if (args[i].equals("-dxf") ){
        DxfEntity.outputMode = DxfEntity.MOD_DXF;
      }
      if (args[i].startsWith("-maxE") ){
        geof.maxEst = Double.parseDouble(args[i].substring(5));
        geofUsed = true;
      }
      if (args[i].startsWith("-minE") ){
        geof.minEst = Double.parseDouble(args[i].substring(5));
        geofUsed = true;
      }
      if (args[i].startsWith("-minN") ){
        geof.minNorth = Double.parseDouble(args[i].substring(5));
        geofUsed = true;
      }
      if (args[i].startsWith("-maxN") ){
        geof.maxNorth = Double.parseDouble(args[i].substring(5));
        geofUsed = true;
      }
    }
    
    //System.out.println("# begin");
    try {
      if (geofUsed) {
        loadFile(args[0], geof);
      } else loadFile(args[0], null);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    
    
  }
  
  
  public static void loadFile(String fileName, GeoFilter geof) throws IOException {
    BufferedReader in = new  BufferedReader( new FileReader(fileName) );
    int num_ligne = 0;
    int num_entity = 0;
    String ligne;
    DxfData oneData;
    DxfEntity curEnt = null;
    
    while ((ligne = in.readLine()) != null) {
      //ligne = ligne.trim();
      num_ligne++;
      num_ligne++;
      // ignore les lignes vides
      //if (ligne.length() > 0) {      }
      oneData = new DxfData();
      
      oneData.code = Integer.parseInt(ligne.trim());
      
      oneData.data = in.readLine();
      
      if (oneData.code == 0) {
        // is old entity to output ?
        if (curEnt != null) {
          if (curEnt.isEntityToKeep()) {
            //System.out.println(curEnt.isEntityToKeep());
            curEnt.toGdcList();
            if (geof != null) {
              curEnt.lstPoint = geof.getFilterdList(curEnt.lstPoint);
            }
            // there are points to print
            if (curEnt.lstPoint.size() > 0)
              curEnt.printInStream(System.out);
          }
        }
        
        
        // new entity
        curEnt = new DxfEntity();
        num_entity++;
      } else {
      }
      
      curEnt.lstData.add(oneData);
      
      
    }
    // EOF
    //curEnt.printInStream(System.out);
    
    in.close();
    in = null;
    //System.out.println("# " + num_ligne + " lines read");
    //System.out.println("# " + num_entity + " num_entity read");
    
  }
}
