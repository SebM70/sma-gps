package sma.polish;

import java.io.IOException;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;
import org.slf4j.LoggerFactory;

/**
 * Test PoiMapMgr.
 * 
 * 
 */
public class PoiMapMgrTest {

	public static final String TRANSLATE_POI_XML = PoiMapMgr.TRANSLATE_POI_XML;
	/** Log4J */
	private static final org.slf4j.Logger sLog = LoggerFactory.getLogger(PoiMapMgr.class);

	/**
	 * Generate new translatePoi.xml.
	 * 
	 * @throws IOException
	 */
	// @Test
	public void transformMapFromTtqvToXml() throws IOException {
		PoiMapMgr mgr = new PoiMapMgr();
		Map<String, String[]> map = mgr.loadMapFromTtqv("C:\\SMa\\QU4_SMa\\FranceSortiesR.qu4");
		mgr.saveToXml(map, "src/main/resources" + TRANSLATE_POI_XML);
		sLog.info("end test");
	}

	/**
	 * Test load of translation map.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testReadMap() throws IOException {
		PoiMapMgr mgr = new PoiMapMgr();
		Map<String, String[]> map = mgr.getPoiMap();
		Assert.assertTrue(map.size() >= 50);
		Assert.assertEquals("0x6401", map.get("117")[2]);
		sLog.info("end testReadMap, map.size()={}", map.size());
	}

}
