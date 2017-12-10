/*
 * ConvertMpsTxtToOzi.java
 * 
 * Created on 18 octobre 2002, 21:04
 */

package sma;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import sma.gps.fich.DecalMap;
import sma.gps.fich.FichGps;
import sma.gps.fich.FichPolish;
import sma.gps.fich.ttqv.FichTtqv3;
import sma.gps.model.Coord;
import sma.gps.model.Route;
import sma.gps.model.Track;
import sma.gps.model.Waypoint;
import sma.gps.process.TrackProcessor;
import sma.polish.PoiMapMgr;
import sma.util.XmlUtil;

/**
 * @author smarsoll
 * @version
 */
public class ConvertTtqvToPolish {
	
	/** Log4J */
	private static final Logger sLog = LoggerFactory.getLogger(ConvertTtqvToPolish.class);


	/** Creates new ConvertMpsTxtToOzi */
	private ConvertTtqvToPolish() {
	}

	static String poiMapFile = null;

	static Map<String, String[]> _poiMap = null;

	static String outPutDir = ".";

	static PrintStream out = null;

	// translation table from Garmin icon to POI definition
	static Map<String, String[]> getPoiMap() {
		if (_poiMap == null) {
			_poiMap = new HashMap<String, String[]>(100);
			if (poiMapFile != null)
				loadMap(poiMapFile);
		}
		return _poiMap;
	}

	/**
	 * @param args
	 *            the command line arguments
	 * @throws Throwable
	 */
	public static void main(String args[]) throws Throwable {
		sLog.info("begin ConvertTtqvToPolish");
		//BasicConfigurator.configure();
		// PropertyConfigurator.configure(Loader.getResource("log4j.properties"));

		try {
			long start = System.currentTimeMillis();
			convertQu4ToMp(args);
			sLog.info("duration=" + (System.currentTimeMillis() - start) + " ms");
		} catch (Throwable e) {
			sLog.error("Throwable in main2", e);
			throw e;
		}
		
	}

	// load the translation Map from a CSV file
	static void loadMap(String mapFile) {
			//log.info("System.getenv="+System.getenv("REF_QU3"));
			sLog.info("-------->mapFile="+mapFile);
		FichTtqv3 source = new FichTtqv3(mapFile);
			source.connect();
			
			String[] decode;
			int nbligne=0;
			
			List<Waypoint> lstWpt = source.getWaypoints("convert_wpt_icon");
		for (Iterator<Waypoint> iter = lstWpt.iterator(); iter.hasNext();) {
			Waypoint wpt = iter.next();
				decode = SmaTool.StringtoArray(wpt.getDescription(), ",");
				if ("default".equals(wpt.name)) {
					_poiMap.put("default", decode);
				} else {
					_poiMap.put(wpt.symbol, decode);
				}
			}			
			source.close();
			sLog.debug("nbligne="+nbligne);
//			BufferedReader in = new BufferedReader(new FileReader(mapFile));
//			// int num_ligne = 0;
//			String ligne;
//			String[] decode;
//			int nbligne=0;
//
//			while ((ligne = in.readLine()) != null) {
//				ligne = ligne.trim();
//
//				if (!ligne.equals("")) {
//					decode = SmaTool.StringtoArray(ligne, ",");
//					_poiMap.put(decode[0], decode);
//					// out.println(decode[0]);
//					nbligne++;
//				}
//
//			}
//			log.debug("nbligne="+nbligne);
//
//			in.close();
//			in = null;

	}

	// private static DocumentBuilderFactory dbf;
	// private static DocumentBuilder db;
	// static {
	// dbf = DocumentBuilderFactory.newInstance();
	// try {
	// db = dbf.newDocumentBuilder();
	// } catch (ParserConfigurationException e) {
	// sLog.error("init error", e);
	// }
	// }

	/**
	 * Load an XML file
	 * 
	 * @param filename
	 * @return root Element
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 */
	private static Element getRootElementFromXmlFile(String filename)
			throws Exception {
		return XmlUtil.getRootElementFromXmlFile(filename);

		// Document doc = db.parse(filename);
		//
		// return doc.getDocumentElement();
		//

	}

	/**
		 * @param args
		 *            the command line arguments
		 */
		public static void convertQu4ToMp(String args[]) throws Exception  {
			
			// String mpsTxtFileName = args[0];
			String xmlFile = args[0];
			String ttqvPath = args[1];
			sLog.info("ConvertTtqvToPolish with xmlFile="+xmlFile);
			
			File f = new File(args[2]);
			// String oziFileName = args[1];
	
			// System.out.println(mpsTxtFileName + " - " + oziFileName);
	
			double maxEst = +9999;
			double minEst = -9999;
			double minNorth = -9999;
			double maxNorth = +9999;
	
			int levels = 1;
		// décode paramètres
			for (int i = 1; i < args.length; i++) {
				if (args[i].startsWith("-maxE")) {
					maxEst = Double.parseDouble(args[i].substring(5));
				} else if (args[i].startsWith("-minE")) {
					minEst = Double.parseDouble(args[i].substring(5));
				} else if (args[i].startsWith("-minN")) {
					minNorth = Double.parseDouble(args[i].substring(5));
				}
				if (args[i].startsWith("-maxN")) {
					maxNorth = Double.parseDouble(args[i].substring(5));
				}
				if (args[i].startsWith("-levels")) {
					levels = Integer.parseInt(args[i].substring(7));
					sLog.debug("levels="+levels);
				} else if (args[i].startsWith("-map")) {
					poiMapFile = args[i].substring(4);
					poiMapFile = findTtqvPath(ttqvPath, poiMapFile);
				} else if (args[i].startsWith("-out")) {
					outPutDir = args[i].substring(4);
				}
			}
			sLog.debug("poiMapFile="+poiMapFile);
	
			try {
	
				// open target
				out = new PrintStream(new FileOutputStream(f));
				//System.out.println(";begin");
				FichPolish polish = new FichPolish(out);
	
				// open XML
				Element ttqvParam = getRootElementFromXmlFile(xmlFile);
				
				DecalMap vDecalMap = new DecalMap();
				
				NodeList lstDb = ttqvParam.getElementsByTagName("decalMap");
				for (int i = 0; i < lstDb.getLength(); i++) {
					Element dbElem = (Element) lstDb.item(i);
					vDecalMap.initMap(dbElem);
				}
				
			TrackProcessor trackProcessor = new TrackProcessor();
				
				lstDb = ttqvParam.getElementsByTagName("db");
				for (int i = 0; i < lstDb.getLength(); i++) {
					Element dbElem = (Element) lstDb.item(i);
					String dbPath = findTtqvPath(ttqvPath, dbElem.getAttribute("name"));
						// ttqvPath + "\\" + dbElem.getAttribute("name") + ".qu4";
					
					out.print("; exporting TTQV db ");
					out.println(dbPath);
					sLog.debug("dbPath="+dbPath);
	
					FichTtqv3 ttqvDb = new FichTtqv3(dbPath);
					ttqvDb.connect();
					
					// tracks
	
					NodeList nodLstTrack = dbElem.getElementsByTagName("trackTable");
					for (int j = 0; j < nodLstTrack.getLength(); j++) {
						Element trkElem = (Element) nodLstTrack.item(j);
						String tableName = trkElem.getAttribute("name");
						String typePolish = trkElem.getAttribute("type");
						String vLevel = trkElem.getAttribute("level");
					sLog.debug("typePolish=" + typePolish);
					if (typePolish == null || "".equals(typePolish)) {
						// default type
						// typePolish = FichPolish.LINE_ALLEY;
						typePolish = FichPolish.LINE_MAJOR_HWAY;
						// typePolish = FichPolish.LINE_RUNWAY;
					}
						if (vLevel == null || "".equals(vLevel)) vLevel = ""+levels;
						out.print("; tracks from table ");
						out.println(tableName);
					List<Track> lstTrack = ttqvDb.getTracksFromTable(tableName);
					boolean speedMode = false;
					if ("speed".equals(typePolish)) {
						speedMode = true;
						lstTrack = trackProcessor.processSpeed(lstTrack);
						sLog.info("trackProcessor.processSpeed lstTrack.size()=" + lstTrack.size());
					}
					sLog.debug("writing in file , lstTrack.size()=" + lstTrack.size());
					for (Iterator<Track> iter = lstTrack.iterator(); iter.hasNext();) {
						Track trk = iter.next();
						if (!speedMode) {
							trk.setTypePolish(typePolish);
						}
						polish.writeTrack(trk, vLevel);
					}
				}
					nodLstTrack = null;
					out.println();
					
					// Waypoints
//					String rgn;
//					String type;
					Coord c;
	
	//				DecimalFormatSymbols fs = new DecimalFormatSymbols();
	//				fs.setDecimalSeparator('.');
	//				DecimalFormat formCoord = new DecimalFormat("##0.0000000", fs);
				PoiMapMgr mgr = new PoiMapMgr();
				Map<String, String[]> poiMap = mgr.getPoiMap();
				// Map<String, String[]> poiMap = getPoiMap();
				polish.setPoiMap(poiMap);
	
					NodeList nodLstWpt = dbElem.getElementsByTagName("wptTable");
					for (int j = 0; j < nodLstWpt.getLength(); j++) {
						Element elem = (Element) nodLstWpt.item(j);
						String tableName = elem.getAttribute("name");
						String vLevel = elem.getAttribute("level");
						if (vLevel == null || "".equals(vLevel)) vLevel = ""+levels;
						out.print("; waypoints from table ");
						out.println(tableName);
						List<Waypoint> lstWpt = ttqvDb.getWaypoints(tableName);
						try {
							for (Iterator<Waypoint> iter = lstWpt.iterator(); iter.hasNext();) {
								Waypoint wpt = (Waypoint) iter.next();
							
								c = wpt.coord;
								if ((c.latit > minNorth) & (c.latit <= maxNorth) & (c.longit > minEst)
										& (c.longit <= maxEst)) {
									polish.writePoint(wpt, Integer.parseInt(vLevel), vDecalMap);
								}
		
							}
						} catch (Throwable e) {
							sLog.error("tableName="+ tableName + " vLevel="+vLevel, e);
							throw new Exception(e);
						}
					}
					nodLstWpt = null;
					
					// Routes
	
					NodeList nodLstRte = dbElem.getElementsByTagName("routeTable");
					for (int j = 0; j < nodLstRte.getLength(); j++) {
						Element elem = (Element) nodLstRte.item(j);
						String tableName = elem.getAttribute("name");
						String vLevel = elem.getAttribute("level");

						// we draw a link between points of route
						String vLink = elem.getAttribute("link");
						boolean isLink = false;
						if (vLink != null && !vLink.equals("")) {
							isLink = true;
						}
						Track trkForRoute = null;

						if (vLevel == null || "".equals(vLevel)) vLevel = ""+levels;
						out.print("; routes from table ");
						out.println(tableName);
					List<Route> lstWpt = ttqvDb.getRoutesFromTable(tableName);
						for (Iterator iter = lstWpt.iterator(); iter.hasNext();) {
							Route rte = (Route) iter.next();
						List<Waypoint> lstWpts = rte.getLstWpts();
							
							if (isLink) {
								trkForRoute = new Track();
								trkForRoute.setName( rte.getName() );
							}
							
							for (Iterator iterator = lstWpts.iterator(); iterator.hasNext();) {
								Waypoint wpt = (Waypoint) iterator.next();
								c = wpt.coord;
								if ((c.latit > minNorth) & (c.latit <= maxNorth) & (c.longit > minEst)
										& (c.longit <= maxEst)) {
									polish.writePoint(wpt, Integer.parseInt(vLevel), vDecalMap);
									if (isLink) {
										trkForRoute.add(c);
									}
								}
	
							}
	
						}
						
					if (isLink) {
						if (trkForRoute.getLTrackPt().size() > 0) {
							trkForRoute.setTypePolish(vLink);
							polish.writeTrack(trkForRoute, vLevel);
						}
					}
						
					}
					out.println();
				out.flush();
	
					ttqvDb.close();
	
				}
	
				FichGps source = new FichTtqv3("");
	
				source.loadFile();
	
				// System.out.println("; " + mpsTxtFileName);
				List lstWpt = source.configGps.lWaypoint;
	
				Iterator it = lstWpt.iterator();
				while (it.hasNext()) {
	
					Waypoint wpt = (Waypoint) it.next();
	
				}
	
				/*
				 * FichOzi cible = new FichOzi(); cible.configGps =
				 * source.configGps; // l'extension sera ajout�e automatiquement
				 * cible.baseName = source.getNomFichier(); out.println("avant
				 * saveFile " + cible.baseName);
				 */
	
		} finally {
			out.close();
			out = null;
		}
	
		}

	private static String findTtqvPath(String ttqvPath, String dbName) {
		// find qu4 file in good directory.
		String[] allPaths = ttqvPath.split(";");
		for (int i = 0; i < allPaths.length; i++) {
			File f = new File(allPaths[i] + "\\" + dbName + ".qu4");
			if (f.exists()) {
				sLog.debug("find " + f.getAbsolutePath());
				return f.getAbsolutePath();
			}
		}
		sLog.error("We did not find " + dbName + " in these directoris: " + ttqvPath);
		return ttqvPath + "\\" + dbName + ".qu4";
	}
}
