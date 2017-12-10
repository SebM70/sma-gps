package sma.polish;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sma.SmaTool;
import sma.gps.fich.ttqv.FichTtqv3;
import sma.gps.model.Waypoint;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * Manage translation of waypoint to POI
 * 
 * @author marsolle
 * 
 */
public class PoiMapMgr {

	/** Log4J */
	private static final Logger sLog = LoggerFactory.getLogger(PoiMapMgr.class);

	public static final String TRANSLATE_POI_XML = "/sma/polish/translatePoi.xml";

	/** Xstream manager */
	private static XStream xstream;

	static {
		xstream = new XStream(new DomDriver());
	}

	static Map<String, String[]> _poiMap = null;

	// load the translation Map from a CSV file
	public Map<String, String[]> loadMapFromTtqv(String mapFile) {
		// log.info("System.getenv="+System.getenv("REF_QU3"));
		sLog.info("mapFile=" + mapFile);
		FichTtqv3 source = new FichTtqv3(mapFile);
		source.connect();

		String[] decode;
		int nbligne = 0;

		Map<String, String[]> newPoiMap = new TreeMap<String, String[]>();

		List<Waypoint> lstWpt = source.getWaypoints("convert_wpt_icon");
		for (Iterator<Waypoint> iter = lstWpt.iterator(); iter.hasNext();) {
			Waypoint wpt = iter.next();
			decode = SmaTool.StringtoArray(wpt.getDescription(), ",");
			if ("default".equals(wpt.name)) {
				newPoiMap.put("default", decode);
			} else {
				newPoiMap.put(wpt.symbol, decode);
			}
		}
		source.close();
		sLog.debug("nbligne=" + nbligne);
		return newPoiMap;

	}

	public void saveToXml(Map<String, String[]> pPoiMap, String xmlFileName) throws IOException {
		// TODO Auto-generated method stub
		sLog.info("saving in " + xmlFileName);
		String xml = xstream.toXML(pPoiMap);
		FileWriter fw = new FileWriter(xmlFileName);
		fw.append(xml);
		fw.flush();
		fw.close();
	}

	public Map<String, String[]> getPoiMap() throws IOException {

		URL url = this.getClass().getResource(TRANSLATE_POI_XML);
		sLog.debug("Loading from " + url);
		_poiMap = (Map<String, String[]>) xstream.fromXML(url.openStream());

		return _poiMap;
	}

}
