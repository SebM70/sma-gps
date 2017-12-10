package sma.gps.kml;

import org.w3c.dom.Element;

/**
 * a KmlElement can be saved in a DOM for GoogleEarth KML. 
 * @author S. MARSOLLE
 */
public interface KmlElement {
	
	/**
	 * create dom element in the pere Element
	 * @param pere
	 * @return
	 */
	public Element addDomElement(Element pere);

}
