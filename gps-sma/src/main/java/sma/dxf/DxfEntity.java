/*
 * DxfEntity.java
 *
 * Created on 15 mai 2003, 22:05
 */

package sma.dxf;

import geotransform.coords.Gcc_Coord_3d;
import geotransform.coords.Gdc_Coord_3d;
import geotransform.coords.Utm_Coord_3d;
import geotransform.datum.Create_Datum;
import geotransform.datum.Datum;
import geotransform.ellipsoids.Ellipsoid;
import geotransform.ellipsoids.IN_Ellipsoid;
import geotransform.ellipsoids.WE_Ellipsoid;
import geotransform.transforms.Gcc_To_Gdc_Converter;
import geotransform.transforms.Utm_To_Gcc_Converter;

import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author  smarsoll
 */
public class DxfEntity {
  
  // Data from DXF file
  public List<DxfData> lstData;
  // list of points
  public List lstPoint;
  private String dxfLayer;
 
  
  public static final int MOD_DXF=0;
  public static final int MOD_MPS=1;
  
  // resolution in meter for reduction
  public static final double RESOLUTION=25.0;
  
  public static int outputMode = MOD_DXF;
  // default UTM zone
  public static byte utmZone = 30;
  
  /** Creates new DxfEntity */
  public DxfEntity() {
    lstData = new LinkedList<DxfData>() ;
  }
  
  public void buildLstPoints() {
    // 2 data will give 1 point
    lstPoint = new ArrayList<Utm_Coord_3d>(lstData.size()/2);
    DxfData d, d2;
    
    Iterator<DxfData> it = lstData.iterator() ;
    while (it.hasNext()) {
      d = it.next() ;
      if (d.code == 10) {
        d2 = (DxfData) it.next() ;
        
        Utm_Coord_3d utm = new Utm_Coord_3d();
        
        utm.x = Double.parseDouble(d.data);
        utm.y = Double.parseDouble(d2.data);
        utm.z = 0;
        utm.zone = utmZone; // default zone
        utm.hemisphere_north = true;
        
        lstPoint.add(utm);
      }
    }
    
  }
  
  public void reduceLstPoints() {
    List<Utm_Coord_3d> reduceLst = new ArrayList<Utm_Coord_3d>(lstPoint.size());
    Utm_Coord_3d lastKept, current;
    // first point
    lastKept = (Utm_Coord_3d) lstPoint.get(0);
    reduceLst.add( lastKept );
    int nbPointsToProcess = lstPoint.size() - 1;
    double distance;
    
    for (int i=1; i < nbPointsToProcess; i++) {
      current = (Utm_Coord_3d) lstPoint.get(i);
      // not a real distance but quicker
      distance = Math.abs(current.x - lastKept.x) + Math.abs(current.y - lastKept.y);
      if (distance > RESOLUTION) {
        reduceLst.add( current );
        lastKept = current;
      }
    }
    
    // last point
    reduceLst.add( (Utm_Coord_3d) lstPoint.get(lstPoint.size() - 1) );
    
    // new list
    lstPoint = reduceLst;
    
  }
  
  public DxfData get(int num) {
    return (DxfData) lstData.get(num);
  }
  
  public String getDataByCode(int cod) {
    Iterator<DxfData> it = lstData.iterator() ;
    while (it.hasNext()) {
      DxfData d = (DxfData) it.next() ;
      if (d.code == cod) return d.data;
    }
    // not found
    return null;
  }
  
  // first DXF data
  public String getType() {
    return get(0).data;
  }
  
  // specific entity to keep for output
  public boolean isEntityToKeep() {
    boolean fileDefault = (outputMode == MOD_DXF);
    boolean forMapsource = true;
    String type = getType();
    //System.out.println(type);
    if (type.equals("SECTION")) return fileDefault;
    else if (type.equals("ENDSEC")) return fileDefault;
    else if (type.equals("LINE")) return false;
    else if (type.equals("LWPOLYLINE") | type.equals("LINE")) {
      String layer = getDataByCode(8);
      if ((layer != null) & ((layer.equals("105CAMINO")
      | layer.equals("104PISTAS")
      | layer.equals("702CORTAF")
      | layer.equals("505LAGO")
      | layer.equals("xxx103CTRACO"))  )) {
        // is it a dash ?
        if (lstData.size() > 20) return forMapsource;
        else return false;
        //System.out.println("nb="+ lstData.size());
      }
      else return false;
    }
    else if (type.equals("EOF")) return fileDefault;
    else if (type.equals("CLASS")) return fileDefault;
    else if (type.equals("LAYER")) return fileDefault;
    else if (type.equals("TEXT")) return false;
    else if (type.equals("POINT")) return false;
    // default
    return fileDefault;
  }
  
  private void printInStreamDXF(PrintStream stream) {
    Iterator<DxfData> it = lstData.iterator() ;
    while (it.hasNext()) {
      DxfData d = (DxfData) it.next() ;
      stream.println(d.code);
      stream.println(d.data);
    }
  }
  
  private void printInStreamMPS(PrintStream stream) {
    // need UTM EURO50 to DEG WGS84 converions
    // degr�s
    Gdc_Coord_3d gdc;
    
    // initialisation
    //Utm_To_Gdc_Converter.Init(new IN_Ellipsoid());
    
    boolean previous = false;
    String rgn = getMpsRgn();
    stream.print("[RGN");
    stream.print(rgn);
    stream.println("]");
    stream.print("Type=");
    stream.print( getMpsType() );
    
    stream.print("\nData0=");
    
    // format sortie
    DecimalFormatSymbols fs = new DecimalFormatSymbols();
    fs.setDecimalSeparator('.');
    DecimalFormat formCoord = new DecimalFormat("0.00000", fs);
    
    Iterator<?> it = lstPoint.iterator() ;
    while (it.hasNext()) {
      gdc = (Gdc_Coord_3d) it.next() ;
      
      // write in output
      if (previous)  stream.print(",");
      stream.print("(");
      //stream.print(formCoord.format( gdc.latitude  + 0.00005 ));
      stream.print(formCoord.format( gdc.latitude ));
      stream.print(",");
      // empiric delta
      //stream.print(formCoord.format( gdc.longitude + 0.00035 ));
      stream.print(formCoord.format( gdc.longitude ));
      stream.print(")");
      
      previous = true;
      
    }
    stream.print("\n[END-RGN");
    stream.print(rgn);
    stream.print("]\n\n");
    
  }
  
  // transform DXf list to list of gdc points
  public void toGdcList() {
    buildLstPoints();
    reduceLstPoints();
    List<Gdc_Coord_3d> newLst = new ArrayList<Gdc_Coord_3d>(lstPoint.size());
    // need UTM EURO50 to DEG WGS84 converions
    // degr�s
    //Gdc_Coord_3d gdc = new Gdc_Coord_3d();
    // utm grid
    Utm_Coord_3d utm = new Utm_Coord_3d();
    utm.hemisphere_north = true;
    
    //
    Gcc_Coord_3d gccSource = new Gcc_Coord_3d();
    Gcc_Coord_3d gccCible = new Gcc_Coord_3d();
    
    // initialisation
    //Utm_To_Gdc_Converter.Init(new IN_Ellipsoid());
    
    Create_Datum cdat = new Create_Datum();
    cdat.Init();
    //Datum datWGS84 = cdat.Lista[0];
    Ellipsoid elCible = new WE_Ellipsoid();
    Datum datEURO50 = null;
    Ellipsoid elSource = new IN_Ellipsoid();
    
    // search EURO 50 Spain datum
    //stream.println("Create_Datum" + cdat.Lista.length);
    for (int i=0;i < cdat.Lista.length; i++) {
      String datName = cdat.Lista[i].name;
      if ( datName.startsWith("European 1950") & datName.indexOf("Spain") >= 0) {
        //stream.println("; Create_Datum name:" + datName +"  " + cdat.Lista[i].dx);
        datEURO50 = cdat.Lista[i];
        // end loop
        i=cdat.Lista.length;
      }
    }
    
    //DxfData d, d2;
    //String rgn = getMpsRgn();
    
    Iterator it = lstPoint.iterator() ;
    while (it.hasNext()) {
      utm = (Utm_Coord_3d) it.next() ;
      
      Utm_To_Gcc_Converter.Init(elSource);
      Utm_To_Gcc_Converter.Convert(utm, gccSource);
      
      //Datum.Convert(datEURO50, false, gccSource, gccCible);
      // �quivalent :
      gccCible.x = gccSource.x + datEURO50.dx;
      gccCible.y = gccSource.y + datEURO50.dy;
      gccCible.z = gccSource.z + datEURO50.dz;
        /*gccCible.x = gccSource.x;
        gccCible.y = gccSource.y;
        gccCible.z = gccSource.z;*/
      
      // back to deg
      Gdc_Coord_3d gdc = new Gdc_Coord_3d();
      
      Gcc_To_Gdc_Converter.Init(elCible);
      Gcc_To_Gdc_Converter.Convert(gccCible, gdc);
      
      //
      newLst.add(gdc);
    }
    // set new list
    lstPoint = newLst;
  }
  
  
  public String getMpsType() {
    String layer = getDxfLayer();
    if (layer == null) return "0x20"; // Land Contour - thin
    else if ( layer.equals("105CAMINO") ) return "0x07"; // Alley - thin
    else if ( layer.equals("702CORTAF") ) return "0x1a"; // Ferry
    else if ( layer.equals("104PISTAS") ) return "0x0a"; // unpaved
    else if ( layer.equals("103CTRACO") ) return "0x03"; // Principal HWY - medium
    else if ( layer.equals("505LAGO") ) return "0x42"; //
    // default
    return "0x1";
  }
  public String getMpsRgn() {
    String layer = getDxfLayer();
    if (layer == null) return "nullLayer"; // Land Contour - thin
    else if ( layer.equals("505LAGO") ) return "80"; // Principal HWY - medium
    // default
    return "40";
  }
  
  public String getDxfLayer() {
    if (dxfLayer == null) {
      dxfLayer = getDataByCode(8);
      if (dxfLayer == null) dxfLayer = "";
    }
    return dxfLayer;
  }
  
  public void printInStream(PrintStream stream) {
    if (outputMode == MOD_DXF) printInStreamDXF(stream);
    else if (outputMode == MOD_MPS) printInStreamMPS(stream);
    
  }
  
}
