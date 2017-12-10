package sma.gps.fich;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * décaleur de niveau
 * 
 * @author S. MARSOLLE
 */
public class DecalMap {
	
	Map mapDecal = new HashMap();
	// Log4J
	private static final Logger sLog = LoggerFactory.getLogger(DecalMap.class);

	public DecalMap() {
		super();
	}
	
	public void initMap(Element mapElem) {
		NodeList lstDecal = mapElem.getElementsByTagName("decal");
		for (int i = 0; i < lstDecal.getLength(); i++) {
			Element decal = (Element) lstDecal.item(i);
			String type = decal.getAttribute("type");
			Integer decalage = Integer.decode(decal.getAttribute("level"));
			mapDecal.put(type, decalage);
		}
		sLog.info("initMap, mapDecal.size()="+mapDecal.size());
	}
	
	public int calcateLevel(String pType, int initialLevel) {
		Integer decalage = (Integer) mapDecal.get(pType);
		if (decalage != null) {
			initialLevel = initialLevel + decalage.intValue();
			sLog.debug("pType="+pType+ " level=" + initialLevel);
		}
		return initialLevel;
	}

}
