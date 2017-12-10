/*
 * GeoFilter.java
 *
 * Created on 22 mai 2003, 12:25
 */

package sma.dxf;

import geotransform.coords.Gdc_Coord_3d;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 
 * @author smarsoll
 */
public class GeoFilter {
  
  public double minEst = -10000;
  public double maxEst = 10000;
  public double maxNorth = 91;
  public double minNorth = -91;
  
  /** Creates new GeoFilter */
  public GeoFilter() {
  }
  
  public boolean isInScope(Gdc_Coord_3d gdc) {
    return (gdc.latitude >= minNorth) & (gdc.latitude <= maxNorth)
    & (gdc.longitude >= minEst) & (gdc.longitude <= maxEst);
  }
  
  public List getFilterdList(List lstPoint) {
    List newLst = new ArrayList(lstPoint.size());
    // check each point
    Iterator it = lstPoint.iterator() ;
    while (it.hasNext()) {
      Gdc_Coord_3d gdc = (Gdc_Coord_3d) it.next() ;
      if (isInScope(gdc)) newLst.add(gdc);
    }
    
    return newLst;
  }
  
}
