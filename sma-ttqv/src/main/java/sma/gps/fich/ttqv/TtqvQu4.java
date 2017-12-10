package sma.gps.fich.ttqv;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sma.gps.model.Coord;
import sma.gps.model.Track;
import sma.ttqv.model.TqvRouteTable;
import sma.ttqv.model.TqvTrackTable;
import sma.ttqv.model.TqvWaypointTable;
import sma.ttqv.model.TtqvCatalog;

import com.healthmarketscience.jackcess.Column;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.Table;

/**
 * 
 * @author marsolle
 * 
 */
public class TtqvQu4 implements ITtqvFile {

	/** Class logger. */
	protected static final Logger sLog = LoggerFactory.getLogger(TtqvQu4.class);

	private File file;

	/** the DB API. */
	protected Database db;

	/**
	 * For subclasses
	 */
	public TtqvQu4() {
	}

	/**
	 * Contructor to use
	 * 
	 * @param dbFile
	 */
	public TtqvQu4(File dbFile) {
		this.file = dbFile;
	}

	public void connect() throws IOException {
		// db = Database.open(file, true);
		db = Database.open(file, true);
		sLog.info("db.getFileFormat()={}", db.getFileFormat());
	}

	public void close() throws IOException {
		db.close();
	}

	public List<Track> getTracksFromTable(String tableName) throws Exception {
		List<Track> lstTrk = new LinkedList<Track>();
		// "Select tr_idx,name,info,style From [" + table + "] Where (del=0)";
		Table table = db.getTable(tableName);
		// Map<String, Object> row = Cursor.findRow(table, Collections.singletonMap("del", "0"));

		Map<Integer, Track> mapTracks = new LinkedHashMap<Integer, Track>();

		for (Map<String, Object> row : table) {
			boolean isDel = (Boolean) row.get("del");
			if (!isDel) {

				// create 1 track per line fetched
				Track trk = new Track();
				trk.setName((String) row.get("name"));
				sLog.debug(trk.getName());
				// log("trk.nom=" + trk.nom);
				trk.setDescription((String) row.get("info"));
				trk.setStyle((String) row.get("style"));
				Integer tr_idx = (Integer) row.get("tr_idx");

				mapTracks.put(tr_idx, trk);

				lstTrk.add(trk);
			}
		}

		this.loadTracks(mapTracks, tableName);

		return lstTrk;
	}

	private void loadTracks(Map<Integer, Track> pMapTracks, String tableName) throws IOException {
		// String sql = "Select fp,lat,lon,alt,speed From [" + tableName + "_trp] Where (tr_idx=" + tr_idx + ") and (del=0)";
		// sql += " Order By trp_idx";
		Table table = db.getTable(tableName + "_trp");
		for (Map<String, Object> row : table) {
			boolean isDel = (Boolean) row.get("del");
			if (!isDel) {
				Integer tr_idx = (Integer) row.get("tr_idx");
				Track trk = pMapTracks.get(tr_idx);
				if (trk == null) {
					sLog.warn("Not tr_idx " + tr_idx + " in map!");
				} else {
					Coord coord = new Coord((Double) row.get("lat"), (Double) row.get("lon"));
					boolean first = (Boolean) row.get("fp");
					trk.add(coord, first);
				}
				// sLog.info("fetched tr_idx=" + row.get("tr_idx"));
			}
		}
	}

	public TtqvCatalog getCatalog() throws IOException {
		TtqvCatalog cat = new TtqvCatalog();
		for (Table table : db) {

			String tableName = table.getName();
			sLog.info(tableName);
			Set<String> columns = this.getColumnNames(table);
			if (columns.contains("tr_idx") && !tableName.endsWith("_trp")) {
				sLog.info("is a Track : " + tableName);
				sLog.info("table.isHidden(): " + table.isHidden());
				sLog.info("table.getProperties(): " + table.getProperties());
				cat.addTable(new TqvTrackTable(tableName));
				// this.traceTable(tableName);
			} else if (columns.contains("gpsnumber") && !tableName.endsWith("_rwp")) {
				sLog.info("is a Route : " + tableName);
				cat.addTable(new TqvRouteTable(tableName));

			} else if (columns.contains("wp_idx")) {
				sLog.info("is a Waypoint : " + tableName);
				cat.addTable(new TqvWaypointTable(tableName));
			}
		}
		this.traceTable("Waypoints");
		return cat;

	}

	private Set<String> getColumnNames(Table table) {
		Set<String> colNames = new HashSet<String>();
		for (Column column : table.getColumns()) {
			colNames.add(column.getName());
		}
		return colNames;
	}

	private void traceTable(String tableName) throws IOException {
		Table table = db.getTable(tableName);
		sLog.info("Table structure = \n{}", table);
		sLog.info("First lines = {}", table.display(10L));

	}

}
