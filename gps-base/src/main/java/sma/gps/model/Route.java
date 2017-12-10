/*
 LAST
 * Route.java
 *
 * Created on 1 septembre 2001, 14:02
 */

package sma.gps.model;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Element;

import sma.gps.kml.Folder;
import sma.gps.kml.KmlElement;

/**
 * 
 * @author MARSOLLE
 * @version
 */
public class Route implements KmlElement {

	private String name, description;

	private List<Waypoint> lstWpts;

	private String style;

	/** Creates new Route */
	public Route(String aName) {
		name = aName;
		description = null;
		lstWpts = new LinkedList<Waypoint>();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Element addDomElement(Element pere) {
		// a route is folder with points and one track
		Folder f = new Folder(name);
		f.setDescription(description);

		// all waypoints
		for (Iterator iter = lstWpts.iterator(); iter.hasNext();) {
			Waypoint wpt = (Waypoint) iter.next();
			//FichGps.log("wpt.name=" + wpt.name);
			f.appendChild(wpt);
		}
		// corresponding track
		f.appendChild(this.toTrack());

		// return folder transformation
		return f.addDomElement(pere);
	}
	
	/**
	 * transform Route to Track
	 * @return
	 */
	public Track toTrack() {
		Track result = new Track();
		result.setName(name + "_rte");
		result.setStyle(style);
		for (Iterator iter = lstWpts.iterator(); iter.hasNext();) {
			Waypoint wpt = (Waypoint) iter.next();
			result.add(wpt.coord);
		}		
		return result;
	}

	public void setStyle(String pStyle) {
		style = pStyle;
	}

	public void addPoint(Waypoint wpt) {
		lstWpts.add(wpt);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Waypoint> getLstWpts() {
		return lstWpts;
	}

}
