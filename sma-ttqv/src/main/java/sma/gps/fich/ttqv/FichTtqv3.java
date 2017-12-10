/**
 * FichOzi.java
 *
 * Created on 1 septembre 2001, 16:28
 * 
 * To read *.QU3 files from TTQV
 * 
 */

package sma.gps.fich.ttqv;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import sma.gps.fich.FichGps;
import sma.gps.kml.AutoOneFolder;
import sma.gps.kml.Folder;
import sma.gps.model.Coord;
import sma.gps.model.Route;
import sma.gps.model.Track;
import sma.gps.model.Waypoint;

/**
 * Read TTQV file using SQL Access.
 * 
 * @author MARSOLLE
 * @version
 */
public class FichTtqv3 extends FichGps implements ITtqvFile {
	/** Log4J */
	private static final Logger sLog = LoggerFactory.getLogger(FichTtqv3.class);

	// list of table names
	Collection<String> wptTables = new HashSet<String>();

	Collection<String> rteTables = new HashSet<String>();

	Collection<String> trkTables = new HashSet<String>();

	public Collection<String> getWptTables() {
		return wptTables;
	}

	public Collection<String> getRteTables() {
		return rteTables;
	}

	public Collection<String> getTrkTables() {
		return trkTables;
	}

	// JDBC connection
	Connection mCon = null;

	/** Creates new FichTtqv3 */
	public FichTtqv3(String nomF) {
		super();
		setNomFichier(nomF);
	}

	/**
	 * Creates new FichTtqv3
	 * 
	 * @throws IOException
	 */
	public FichTtqv3(File dbFile) throws IOException {
		super();
		setNomFichier(dbFile.getCanonicalPath());
	}

	/**
	 * Load all table names.
	 * 
	 * @throws SQLException
	 */
	public void loadCatalog() throws SQLException {
		// determine base name
		File f = new File(nomFichier);
		sLog.debug("f.getName()=" + f.getName() + f.getName().substring(0, f.getName().lastIndexOf('.')));
		setBaseName(f.getName().substring(0, f.getName().lastIndexOf('.')));

		Set<String> allTables = new HashSet<String>(100);

		// connect to DB
		// connect(); => done by caller before

		try {

			// log("con.getCatalog()=" + con.getCatalog());
			DatabaseMetaData meta = mCon.getMetaData();
			ResultSet rs = meta.getTables(null, null, null, null);

			// from our query
			if (rs != null) // if rs == null, then there is no ResultSet to view
				while (rs.next()) // this will step through our data
				// row-by-row

				{
					/*
					 * the next line will get the first column in our current
					 * row's ResultSet as a String ( getString( columnNumber) )
					 * and output it to the screen
					 */
					// System.out.println(rs.getString(1) + " - " +
					if ("TABLE".equals(rs.getString("TABLE_TYPE"))) {
						allTables.add(rs.getString("TABLE_NAME"));
					}

				}

			// log(allTables);
			// log("----");

			// let's find track tables
			Iterator<String> it = allTables.iterator();
			while (it.hasNext()) {
				String table = it.next();
				if (table.endsWith("_trp")) {
					trkTables.add((table.substring(0, table.length() - 4)));
					it.remove();
				}
			}
			// remove them
			allTables.removeAll(trkTables);
			// log(allTables);
			// log("----");

			// let's find route tables
			it = allTables.iterator();
			while (it.hasNext()) {
				String table = it.next();
				if (table.endsWith("_rwp")) {
					rteTables.add((table.substring(0, table.length() - 4)));
					it.remove();
				}
			}
			// remove them
			allTables.removeAll(rteTables);

			allTables.remove("version");

			// log(allTables);
			// log("----");

			// remains waypoint tables
			// rs = meta.getColumns(null,null,"0012Laurent_Chaleil",null);
			// SmaTool.logRS(rs);

			// filter remaining tables to be sure they are Waypoint tables
			// because we can have drawing tables for instance
			for (Iterator<String> iter = allTables.iterator(); iter.hasNext();) {
				String tablename = (String) iter.next();
				// get columns definition of table
				ResultSet rsc = meta.getColumns(null, null, tablename, null);
				// look for wp_idx in COLUMN_NAME
				if (rsc != null) // if rs == null, then there is no ResultSet
					while ((rsc != null) && rsc.next()) {
						if ("wp_idx".equals(rsc.getString("COLUMN_NAME"))) {
							wptTables.add(tablename);
							rsc = null;
						}

					}
			}

			// wptTables.addAll(allTables);

			// this.close();

		} catch (SQLException e) {
			sLog.error("", e);
		}
	}

	public void loadFile() throws IOException {

	}

	public void saveFile() throws IOException {

	}

	public void saveAsKml() throws Exception {
		// XML document
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		DocumentBuilder builder;
		builder = factory.newDocumentBuilder();
		Document doc = builder.newDocument();
		Element _Rac = null;
		_Rac = doc.createElement("kml");
		doc.appendChild(_Rac);
		// _Rac.setAttribute("baseDir", base.getAbsolutePath());
		/*
		 * Element f1 = doc.createElement("Folder"); _Rac.appendChild(f1);
		 * Element name = doc.createElement("name"); f1.appendChild(name); Text
		 * t = doc.createTextNode("Waypoints"); name.appendChild(t);
		 */

		Folder fMain = new Folder(getBaseName());
		String descr = "Imported from TTQV file " + getNomFichier();
		descr += "\n" + new Date();
		descr += "\n by S. MARSOLLE   http://smarsolle.free.fr/";
		fMain.setDescription(descr);

		connect();

		Folder fRegroup = new AutoOneFolder("Waypoints");
		fMain.appendChild(fRegroup);

		// Waypoints tables
		for (Iterator iter = wptTables.iterator(); iter.hasNext();) {
			String tableName = (String) iter.next();
			// create 1 folder per table
			Folder f = new Folder(tableName);
			f.setDescription("Waypoint table from TTQV by S. MARSOLLE");
			fRegroup.appendChild(f);
			List lst = getWaypoints(tableName);
			for (Iterator iterator = lst.iterator(); iterator.hasNext();) {
				Waypoint wpt = (Waypoint) iterator.next();
				// add waypoint to KML folder
				f.appendChild(wpt);
			}

		}

		// Route tables
		fRegroup = new AutoOneFolder("Routes");
		fMain.appendChild(fRegroup);
		for (Iterator iter = rteTables.iterator(); iter.hasNext();) {
			String tableName = (String) iter.next();
			// create 1 folder per table
			Folder f = new Folder(tableName);
			f.setDescription("Route table from TTQV by S. MARSOLLE");
			fRegroup.appendChild(f);
			List lstRte = getRoutesFromTable(tableName);
			for (Iterator iterator = lstRte.iterator(); iterator.hasNext();) {
				Route rte = (Route) iterator.next();
				f.appendChild(rte);
			}
		}

		// track tables
		fRegroup = new AutoOneFolder("Tracks");
		fMain.appendChild(fRegroup);
		for (Iterator iter = trkTables.iterator(); iter.hasNext();) {
			String tableName = (String) iter.next();
			Folder f = new Folder(tableName);
			f.setDescription("Track table from TTQV by S. MARSOLLE");
			fRegroup.appendChild(f);
			List lstTrk = getTracksFromTable(tableName);
			for (Iterator iterator = lstTrk.iterator(); iterator.hasNext();) {
				Track trk = (Track) iterator.next();
				f.appendChild(trk);
			}
		}

		close();

		// transform to DOM
		sLog.debug("transform to XML DOM");
		// _Rac.appendChild(fMain.toDomElement(doc));
		fMain.addDomElement(_Rac);

		// output in current directory
		String outputFileName = getBaseName() + ".kml";
		sLog.debug("reporting in file " + outputFileName);
		// report();
		FileOutputStream xmlFileStream = new FileOutputStream(outputFileName);
		// Save the XML data in the stream
		StreamResult result = new StreamResult(xmlFileStream);
		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer = tFactory.newTransformer();
		transformer.transform(new DOMSource(doc), result);
		sLog.debug("...end");

	}

	// dit si le fichier est compatible avec la classe
	public boolean isCompatible() {
		// 4 derners car
		String ext = nomFichier.substring(nomFichier.length() - 4).toUpperCase();
		if (ext.equals(".QU3")) {
			return true;

		} else
			return false;
	}

	/**
	 * read all waypoints from a table
	 * @param table
	 * @return
	 */
	public List<Waypoint> getWaypoints(String table) {
		List<Waypoint> lst = new LinkedList<Waypoint>();
		sLog.info("getWaypoints, table=" + table);

		Statement s;
		try {
			s = mCon.createStatement();
			String sql = "Select name,lat,lon,alt,description,sym From [" + table
					+ "] Where (del=0)";
			// log(sql);
			s.execute(sql);
			// get any ResultSet that from our query
			ResultSet rs = s.getResultSet();
			int nbFetch = 0;
			if (rs != null) // if rs == null, then there is no ResultSet to view
				while (rs.next()) // this will step through our data
				// row-by-row

				{
					// create 1 waypoint per line fetched
					Waypoint wpt = new Waypoint();
					wpt.name = rs.getString("name");
					wpt.setDescription(rs.getString("description"));
					wpt.setCoord(rs.getDouble("lat"), rs.getDouble("lon"), rs.getFloat("alt"));
					wpt.symbol = rs.getString("sym").trim();
					lst.add(wpt);
					nbFetch++;
				}
			// log("nbFetch=" + nbFetch);
			s.close(); // close the Statement to let the database know we're
		} catch (SQLException e) {
			sLog.error("", e);
		}

		return lst;
	}

	// load a track from a trp table
	private void loadTrack(Track trk, String table, String tr_idx) throws SQLException {
		// 
		table += "_trp";
		Statement s = mCon.createStatement();
		String sql = "Select fp,lat,lon,alt,speed From [" + table + "] Where (tr_idx=" + tr_idx
				+ ") and (del=0)";
		// sql += " Order By trp_pos";
		// better results with trp_idx !!!
		sql += " Order By trp_idx";
		// log(sql);
		s.execute(sql);
		// get any ResultSet that from our query
		ResultSet rs = s.getResultSet();
		// SmaTool.logRS (rs);
		int nbFetch = 0;
		if (rs != null) // if rs == null, then there is no ResultSet to view
			while (rs.next()) {
				// boolean fp = rs.getBoolean("fp");
				boolean fp = rs.getBoolean(1);
				// trk.add(new Coord(rs.getDouble("lat"), rs.getDouble("lon"), rs.getFloat("alt")), fp);
				// with speed
				trk.add(new Coord(rs.getDouble(2), rs.getDouble(3), rs.getFloat(4)), fp, rs.getFloat(5));
				nbFetch++;
			}
		// log("nbFetch=" + nbFetch);
		s.close();

	}

	/**
	 * Load all tracks from a table
	 * 
	 * @param table
	 * @return List of Track
	 * @throws SQLException
	 */
	public List<Track> getTracksFromTable(String table) throws SQLException {
		List<Track> lstTrk = new LinkedList<Track>();
		sLog.info("getTracksFromTable, table=" + table);
		Statement s;
		String sql = "Select tr_idx,name,info,style From [" + table + "] Where (del=0)";
		try {
			s = mCon.createStatement();
			// log(sql);
			s.execute(sql);
			// get any ResultSet that from our query
			ResultSet rs = s.getResultSet();
			// SmaTool.logRS (rs);
			int nbFetch = 0;
			if (rs != null) // if rs == null, then there is no ResultSet to view
				while (rs.next()) // this will step through our data
				// row-by-row

				{
					// create 1 track per line fetched
					Track trk = new Track();
					trk.nom = rs.getString("name");
					// log("trk.nom=" + trk.nom);
					trk.setDescription(rs.getString("info"));
					trk.setStyle(rs.getString("style"));
					String tr_idx = rs.getString("tr_idx");

					this.loadTrack(trk, table, tr_idx);

					lstTrk.add(trk);
					nbFetch++;
				}
			// log("nbFetch=" + nbFetch);
			s.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			sLog.error("sql="+sql+ " table="+table, e);
			throw e;
		}
		return lstTrk;
	}

	/**
	 * Connect to QU3/QU4 file
	 * 
	 */
	public void connect() {
		String database = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=";
		database += nomFichier.trim() + ";DriverID=22;READONLY=true";
		// add // on to // the end

		sLog.debug("connecting to database=" + database);

		// load JDBC class
		try {
			Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
			// now we can get the connection from the DriverManager
			mCon = DriverManager.getConnection(database, "", "");
		} catch (ClassNotFoundException e) {
			sLog.error("ClassNotFoundException", e);
		} catch (SQLException e) {
			sLog.error("database="+database, e);
		}

		/*
		 * Enumeration lesDrivers = DriverManager.getDrivers(); while
		 * (lesDrivers.hasMoreElements()) { Driver dri = (Driver)
		 * lesDrivers.nextElement(); log(dri.toString()); }
		 */

	}

	/**
	 * Close connection
	 *
	 */
	public void close() {
		try {
			mCon.close();
		} catch (SQLException e) {
			sLog.error("Error on close", e);
		}
		mCon = null;
	}

	public List<Route> getRoutesFromTable(String table) {
		final List<Route> lstRte = new LinkedList<Route>();
		sLog.info("getRoutesFromTable, table=" + table);
		Statement s;
		final String sql = "Select rt_idx,name,info,style From [" + table + "] Where (del=0)";
		try {
			s = mCon.createStatement();
			// log(sql);
			s.execute(sql);
			// get any ResultSet that from our query
			ResultSet rs = s.getResultSet();
			// SmaTool.logRS (rs);
			int nbFetch = 0;
			if (rs != null) // if rs == null, then there is no ResultSet to view
				while (rs.next()) {
					// create 1 track per line fetched
					Route rte = new Route(rs.getString("name"));
					sLog.debug("name=" + rte.getName());
					rte.setDescription(rs.getString("info"));
					rte.setStyle(rs.getString("style"));
					String rt_idx = rs.getString("rt_idx");

					loadRoute(rte, table, rt_idx);

					lstRte.add(rte);
					nbFetch++;
				}
			// log("nbFetch=" + nbFetch);
			s.close();
		} catch (SQLException e) {
			sLog.error("sql=" + sql, e);
		}
		return lstRte;
	}

	// load a route from a rwp table
	private void loadRoute(Route rte, String table, String rt_idx) throws SQLException {
		// list of waypoint table
		table += "_rwp";
		Statement s = mCon.createStatement();
		String sql = "Select name,lat,lon,description,alt,style,sym From [" + table
				+ "] Where (rt_idx=" + rt_idx + ") and (del=0)";
		sql += " Order By rt_pos";
		// log(sql);
		s.execute(sql);
		// get any ResultSet that from our query
		ResultSet rs = s.getResultSet();
		// SmaTool.logRS (rs);
		int nbFetch = 0;
		if (rs != null) // if rs == null, then there is no ResultSet to view
			while (rs.next()) {
				Waypoint wpt = new Waypoint();
				wpt.name = rs.getString("name");
				wpt.setCoord(rs.getDouble("lat"), rs.getDouble("lon"), rs.getFloat("alt"));
				wpt.setDescription(rs.getString("description").trim());
				wpt.symbol = rs.getString("sym");
				rte.addPoint(wpt);
				nbFetch++;
			}
		// log("nbFetch=" + nbFetch);
		s.close();

	}
}
