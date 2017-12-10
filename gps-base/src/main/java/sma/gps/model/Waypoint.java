/*
 * Waypoint.java
 *
 * Created on 1 septembre 2001, 14:03
 */

package sma.gps.model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import sma.SmaTool;
import sma.gps.kml.KmlElement;

/**
 * 
 * @author MARSOLLE
 */
public class Waypoint implements KmlElement {

	public Coord coord;

	public String name;

	public String comment = "";

	// table à faire
	public String symbol = "70";

	public int proximity = 0;

	/** Creates new Waypoint */
	public Waypoint() {
	}

	public void setDescription(String desc) {
		comment = desc;
	}

	public String getDescription() {
		return comment;
	}

	public void setCoord(double lat, double lon) {
		if (coord == null) {
			coord = new Coord();
		}
		coord.latit = lat;
		coord.longit = lon;
	}

	public void setCoord(double lat, double lon, float alti) {
		coord = new Coord(lat, lon, alti);
	}

	public Element addDomElement(Element pere) {
		Document doc = pere.getOwnerDocument();
		Element mainElem = doc.createElement("Placemark");
		// _Rac.appendChild(f1);
		Element elem = doc.createElement("name");
		mainElem.appendChild(elem);
		Text t = doc.createTextNode(name);
		elem.appendChild(t);

		if (comment != null) {
			elem = doc.createElement("description");
			mainElem.appendChild(elem);
			t = doc.createTextNode(comment);
			elem.appendChild(t);
		}

		addStyleElement(mainElem);

		Element elemP = doc.createElement("Point");
		mainElem.appendChild(elemP);

		SmaTool.addSimpleElementT(elemP, "altitudeMode", "clampToGround");
		SmaTool.addSimpleElementT(elemP, "coordinates", coord.longit + "," + coord.latit + ","
				+ coord.getAlti());

		// end
		pere.appendChild(mainElem);
		return mainElem;
	}

	/**
	 * <Style> <IconStyle> <Icon> <href>root://icons/palette-4.png</href>
	 * <y>128</y> <w>32</w> <h>32</h> </Icon> </IconStyle> </Style>
	 * 
	 * 
	 */
	private Element addStyleElement(Element pere) {
		Element style = SmaTool.addSimpleElementT(pere, "Style", null);
		Element elem = SmaTool.addSimpleElementT(style, "IconStyle", null);
		// Icon
		elem = SmaTool.addSimpleElementT(elem, "Icon", null);
		SmaTool.addSimpleElementT(elem, "href", "root://icons/palette-4.png");
		SmaTool.addSimpleElementT(elem, "y", "128");
		SmaTool.addSimpleElementT(elem, "w", "32");
		SmaTool.addSimpleElementT(elem, "h", "32");

		return style;
	}
}
