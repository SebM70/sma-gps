/*
 * Zone.java
 *
 * Created on 27 f�vrier 2004, 21:58
 */

package sma.gps.model;


/**
 * Delimited geographical zone.
 * 
 * @author smarsoll
 */
public class Zone {
  private     double _maxEst = +9999;
  private double _minEst = -9999;
  private double _minNorth = -9999;
  private double _maxNorth = +9999;
  
  /** Creates new Zone */
  public Zone(double maxNorth, double minNorth, double maxEst, double minEst) {
    _maxEst = maxEst;
    _minEst = minEst;
    _minNorth = minNorth ;
    _maxNorth = maxNorth;
  }
  
  public boolean isInZone(Coord c) {
    return (c.latit >= _minNorth) && (c.latit <= _maxNorth) && (c.longit >= _minEst) && (c.longit <= _maxEst);
  }
  
}
