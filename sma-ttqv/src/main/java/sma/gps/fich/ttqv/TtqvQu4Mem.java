package sma.gps.fich.ttqv;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import sma.gps.model.Coord;
import sma.gps.model.Track;
import sma.util.access.MemoryDatabase;

import com.healthmarketscience.jackcess.Table;

/**
 * 
 * @author marsolle
 * 
 */
public class TtqvQu4Mem extends TtqvQu4 {

	private byte[] memFile;

	public TtqvQu4Mem(byte[] memQu4) {
		this.memFile = memQu4;
	}

	@Override
	public void connect() throws IOException {
		// db = Database.open(file, true);
		db = MemoryDatabase.open(memFile);
		sLog.info("db.getFileFormat()={}", db.getFileFormat());
	}

	@Override
	public void close() throws IOException {
		db.close();
	}

	@Override
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


}
