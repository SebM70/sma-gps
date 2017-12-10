package sma.gps.fich;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sma.gps.fich.ttqv.FichTtqv3;
import sma.gps.fich.ttqv.ITtqvFile;
import sma.gps.fich.ttqv.TtqvQu4;
import sma.gps.fich.ttqv.TtqvQu4Mem;
import sma.gps.model.Track;
import sma.ttqv.model.TtqvCatalog;

/**
 * Test reading of TTQV files.
 * 
 * @author marsolle
 * 
 */
public class FichTtqv3Test {

	/** Log4J */
	private static final Logger sLog = LoggerFactory.getLogger(FichTtqv3Test.class);

	// private static final String TTQV_DB = "C:/SMa/QU4_Afrique/ALG TRK 2011.qu4";
	private static final String TTQV_DB = "src/test/resources/qu4/unit-test.qu4";

	private static final String TRACK_TABLE = "Nicolas PC";

	@Test
	public void testReadTrackTtqv3() throws Exception {

		ITtqvFile ttqvDb = new FichTtqv3(new File(TTQV_DB));
		ttqvDb.connect();
		long timeBefore = System.currentTimeMillis();
		List<Track> lstTrack = ttqvDb.getTracksFromTable(TRACK_TABLE);
		ttqvDb.close();
		sLog.info("read in " + (System.currentTimeMillis() - timeBefore) + " ms");
		Assert.assertNotNull(lstTrack);
		Assert.assertTrue(lstTrack.size() >= 1);
		Assert.assertTrue("No point in first track!", lstTrack.iterator().next().getLTrackPt().size() >= 1);
		sLog.info("lstTrack.size()=" + lstTrack.size());
		

		// TrackProcessor trackProcessor = new TrackProcessor();
		// List<Track> lstTrackProcessed = trackProcessor.processSpeed(lstTrack);
		// sLog.info("lstTrackProcessed.size()=" + lstTrackProcessed.size());
		// sLog.info("name of first track = " + lstTrackProcessed.get(0).getName());

	}

	/**
	 * Test with TtqvQu4 implementation.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testReadTrackQu4() throws Exception {

		ITtqvFile ttqvDb = new TtqvQu4(new File(TTQV_DB));
		ttqvDb.connect();
		long timeBefore = System.currentTimeMillis();
		List<Track> lstTrack = ttqvDb.getTracksFromTable(TRACK_TABLE);
		ttqvDb.close();
		sLog.info("read in " + (System.currentTimeMillis() - timeBefore) + " ms");
		Assert.assertNotNull(lstTrack);
		Assert.assertTrue(lstTrack.size() >= 1);
		Assert.assertTrue("No point in first track!", lstTrack.iterator().next().getLTrackPt().size() >= 1);
		sLog.info("lstTrack.size()=" + lstTrack.size());
		// sLog.info("lstTrack=\n" + lstTrack);
	}

	/**
	 * Test with TtqvQu4 implementation.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testReadTrackQu4Mem() throws Exception {
		ITtqvFile ttqvDb = getMemDb();
		ttqvDb.connect();
		long timeBefore = System.currentTimeMillis();
		List<Track> lstTrack = ttqvDb.getTracksFromTable(TRACK_TABLE);
		ttqvDb.close();
		sLog.info("read in " + (System.currentTimeMillis() - timeBefore) + " ms");
		Assert.assertNotNull(lstTrack);
		Assert.assertTrue(lstTrack.size() >= 1);
		Assert.assertTrue("No point in first track!", lstTrack.iterator().next().getLTrackPt().size() >= 1);
		sLog.info("lstTrack.size()=" + lstTrack.size());
	}

	private TtqvQu4Mem getMemDb() throws FileNotFoundException, IOException {
		File file = new File(TTQV_DB);
		FileInputStream is = new FileInputStream(file);
		byte[] memQu4 = new byte[(int) file.length()];
		is.read(memQu4);
		is.close();
		sLog.info("File loaded in memory, {} bytes", memQu4.length);
		TtqvQu4Mem ttqvDb = new TtqvQu4Mem(memQu4);
		return ttqvDb;
	}

	@Test
	public void testCompare() throws Exception {

		ITtqvFile ttqvDb = new FichTtqv3(new File(TTQV_DB));
		ttqvDb.connect();
		List<Track> lstTrackTtqv3 = ttqvDb.getTracksFromTable(TRACK_TABLE);
		ttqvDb.close();
		// sLog.info("lstTrackTtqv3=\n" + lstTrackTtqv3);
		ttqvDb = new TtqvQu4(new File(TTQV_DB));
		ttqvDb.connect();
		List<Track> lstTrackQu4 = ttqvDb.getTracksFromTable(TRACK_TABLE);
		ttqvDb.close();
		String strTtqv3 = lstTrackTtqv3.toString();
		Assert.assertEquals(strTtqv3, lstTrackQu4.toString());
		sLog.info("Comparison OK on " + strTtqv3.length() + " chars");

		// mem deb
		ITtqvFile ttqvMemDb = getMemDb();
		ttqvMemDb.connect();
		List<Track> lstTrackMemQu4 = ttqvMemDb.getTracksFromTable(TRACK_TABLE);
		ttqvMemDb.close();
		Assert.assertEquals(strTtqv3, lstTrackMemQu4.toString());
	}

	/**
	 * Read all tables.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testReadTCatalogMem() throws Exception {
		TtqvQu4Mem ttqvDb = getMemDb();
		ttqvDb.connect();
		TtqvCatalog catalog = ttqvDb.getCatalog();
		ttqvDb.close();
		sLog.info("catalog={}", catalog);
	}

}
