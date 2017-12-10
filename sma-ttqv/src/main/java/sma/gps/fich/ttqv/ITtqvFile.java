package sma.gps.fich.ttqv;

import java.io.IOException;
import java.util.List;

import sma.gps.model.Track;

/**
 * Abstraction of TTQV File.
 * 
 * @author marsolle
 * 
 */
public interface ITtqvFile {

	void connect() throws IOException;

	void close() throws IOException;

	/**
	 * 
	 * @param pTrackTable
	 * @return list of tracks
	 * @throws Exception
	 */
	List<Track> getTracksFromTable(String pTrackTable) throws Exception;

}
