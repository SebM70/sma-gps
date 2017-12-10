/*
 * ConfigGps.java
 *
 * Created on 1 septembre 2001, 16:30
 */

package sma.gps.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author  MARSOLLE
 * @version
 */
public class ConfigGps {
  
  public List<Track> lTrack = new ArrayList<Track>();
  public List<Waypoint> lWaypoint = new LinkedList<Waypoint>();
	// public List lRoute = new ArrayList();
  public String datum = "WGS 84";
  
  /** Creates new ConfigGps */
  public ConfigGps() { 
  }
  
  // pour le moment uniquement Track
  public void filterZone(Zone z) {
    Iterator<Track> it = lTrack.iterator();
    while (it.hasNext()) {
      Track t = it.next();
      t.filterZone(z);
    }
  }
  
}
