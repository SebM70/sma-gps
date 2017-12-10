package sma.ttqv.model;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * All tables of a QU4 db.
 * 
 * @author marsolle
 * 
 */
public class TtqvCatalog {

	Map<String, TqvWaypointTable> waypoints = new LinkedHashMap<String, TqvWaypointTable>();
	Map<String, TqvRouteTable> routes = new LinkedHashMap<String, TqvRouteTable>();
	Map<String, TqvTrackTable> tracks = new LinkedHashMap<String, TqvTrackTable>();

	public void addTable(TtqvTable tTable) {
		if (tTable instanceof TqvTrackTable) {
			TqvTrackTable new_name = (TqvTrackTable) tTable;
			tracks.put(new_name.getName(), new_name);
		} else if (tTable instanceof TqvWaypointTable) {
			TqvWaypointTable new_name = (TqvWaypointTable) tTable;
			waypoints.put(new_name.getName(), new_name);
		} else if (tTable instanceof TqvRouteTable) {
			TqvRouteTable new_name = (TqvRouteTable) tTable;
			routes.put(new_name.getName(), new_name);
		}
	}

	@Override
	public String toString() {
		return "TtqvCatalog [\nwaypoints=" + waypoints + ", \nroutes=" + routes + ", \ntracks=" + tracks + "]";
	}

}
