package sma.gps.fich;

import java.io.File;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sma.gps.fich.ttqv.FichTtqv3;
import sma.gps.fich.ttqv.ITtqvFile;
import sma.gps.model.Track;
import sma.gps.process.TrackProcessor;

/**
 * Test reading of TTQV files.
 * 
 * @author marsolle
 * 
 */
public class FichTtqv3Test {

	/** Log4J */
	private static final Logger sLog = LoggerFactory.getLogger(FichTtqv3Test.class);

	private static final String TTQV_DB = "C:/SMa/QU4_Afrique/ALG TRK 2011.qu4";

	private static final String TRACK_TABLE = "Nicolas PC";

	@Test
	public void testTrackProcessor() throws Exception {

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
		
		TrackProcessor trackProcessor = new TrackProcessor();
		List<Track> lstTrackProcessed = trackProcessor.processSpeed(lstTrack);
		sLog.info("lstTrackProcessed.size()=" + lstTrackProcessed.size());
		sLog.info("name of first track = " + lstTrackProcessed.get(0).getName());

	}

}
