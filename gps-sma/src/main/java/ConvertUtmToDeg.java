/*
 * ConvertUtmToDeg.java
 *
 * Created on 1 mai 2003, 22:51
 */

/**
 *
 * @author  smarsoll
 * @version
 */

import geotransform.coords.Gdc_Coord_3d;
import geotransform.coords.Utm_Coord_3d;
import geotransform.ellipsoids.IN_Ellipsoid;
import geotransform.transforms.Utm_To_Gdc_Converter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import sma.SmaTool;
import sma.fichier.FilterEnd;


public class ConvertUtmToDeg {
  
	/** Creates new ConvertUtmToDeg */
  public ConvertUtmToDeg() {
  }
  
  /**
   * @param args the command line arguments
   */
  public static void main(String args[]) {
    
    if (args.length < 1) {
      System.out.println( "Exemple : java ConvertUtmToDeg \"C:\\tmp\\*.utm\" -epng -corner" );
      System.out.println( "-epng is to use png extension instead of original extention for image files" );
      System.out.println( "-corner is to use the 4 corner points used by Ozi" );
      System.exit(1);
    }
    
    String fileMask = args[0];
    
    if (fileMask.indexOf('*') >= 0 ) {
      System.out.println( "several files to process" );
      FilterEnd filtre = new FilterEnd( fileMask.substring(fileMask.indexOf('*')+1) );
      
      File leFichier = new File(fileMask);
      File repertoire = leFichier.getParentFile();
      File[] lesFichiers = repertoire.listFiles( filtre );
      for (int i=0; i< lesFichiers.length ; i++) {
        //System.out.println( lesFichiers[i].getPath() );
        convertOneFile(lesFichiers[i].getPath());
      }
    } else
      convertOneFile(fileMask);
    ;
  }
  
  // convert 1 map file to a cal file
  public static void convertOneFile(String fileName) {
    try {
      File fileToRead=new File(fileName);
      BufferedReader in = new  BufferedReader( new FileReader( fileToRead.getAbsolutePath()) );
      int num_ligne = 0;
      String ligne;
      String newLine;
      String[] lstElem;
      
      // degr�s
      Gdc_Coord_3d gdc = new Gdc_Coord_3d();
      // utm grid
      Utm_Coord_3d utm = new Utm_Coord_3d();
      utm.hemisphere_north = true;
      
      // initialisation
      Utm_To_Gdc_Converter.Init(new IN_Ellipsoid());
      
      
      // format sortie
      DecimalFormatSymbols fs = new DecimalFormatSymbols();
      fs.setDecimalSeparator('.');
      DecimalFormat formWpName = new DecimalFormat("000", fs);
      
      while ((ligne = in.readLine()) != null) {
        ligne = ligne.trim();
        num_ligne++;
        lstElem = decodeLigneVirgule(ligne);
        
/*        Utm_Coord_3d utm = new Utm_Coord_3d(Double.parseDouble(lstElem[14]),
        Double.parseDouble(lstElem[15]),0,
        Byte.parseByte(lstElem[13]),
        true );*/
        utm.x = Double.parseDouble(lstElem[1]);
        utm.y = Double.parseDouble(lstElem[2]);
        
        utm.zone = Byte.parseByte(lstElem[0]);
        
        // initialisation
        Utm_To_Gdc_Converter.Init(new IN_Ellipsoid());
        // convert the points.
        Utm_To_Gdc_Converter.Convert(utm, gdc);
        
        newLine = formWpName.format( num_ligne ) + "," + gdc.latitude + "," + gdc.longitude;
        System.out.println(newLine);
        
      }
      in.close();
      in = null;
      
    }
    catch (Exception e) {
      //System.out.println( "catch (Exception e)" );
      e.printStackTrace();
    }
    
  }
  
  public static String[] decodeLigneVirgule(String lineToDecode) {
    String[] table = SmaTool.StringtoArray(lineToDecode, ",");
    // remove spaces
    for (int i=0; i<table.length; i++) {
      table[i] = table[i].trim();
    }
    
    return table;
  }
  
  
}
