/**
 * 
 * a Folder in a KML document
 * 
 * <Folder>
 <description>une description</description>
 <name>Google Earth Examples</name>
 <visibility>0</visibility>
 <open>1</open>
 ... children
 * 
 */

package sma.gps.kml;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public class Folder implements KmlElement {
	private String name;

	protected String description;

	protected List children = new LinkedList();

	public Folder(String pName) {
		super();
		name = pName;
	}

	public Element addDomElement(Element pere) {
		Document doc = pere.getOwnerDocument();
		Element f1 = doc.createElement("Folder");
		// _Rac.appendChild(f1);
		Element elem = doc.createElement("name");
		f1.appendChild(elem);
		Text t = doc.createTextNode(name);
		elem.appendChild(t);

		if (description != null) {
			elem = doc.createElement("description");
			f1.appendChild(elem);
			t = doc.createTextNode(description);
			elem.appendChild(t);
		}

		// add children in document
		for (Iterator iter = children.iterator(); iter.hasNext();) {
			KmlElement ke = (KmlElement) iter.next();
			//f1.appendChild(ke.toDomElement(doc));
			ke.addDomElement(f1);
		}

		// append in parent node
		pere.appendChild(f1);
		return f1;
	}

	public void appendChild(KmlElement ke) {
		children.add(ke);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
