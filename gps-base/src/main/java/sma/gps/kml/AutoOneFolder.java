package sma.gps.kml;

import org.w3c.dom.Element;

/**
 * This folder does not display if it own only one element
 * 
 * @author S. MARSOLLE
 * 
 */
public class AutoOneFolder extends Folder {

	public AutoOneFolder(String pName) {
		super(pName);
	}

	public Element addDomElement(Element pere) {
		if (children.size() >= 2) {
			// we need normal Folder structure
			return super.addDomElement(pere);
		} else if (children.size() == 1) {
			// add children in document
			KmlElement ke = (KmlElement) children.get(0);
			// f1.appendChild(ke.toDomElement(doc));
			return ke.addDomElement(pere);
		}

		// nothing to show
		return null;
	}

}
