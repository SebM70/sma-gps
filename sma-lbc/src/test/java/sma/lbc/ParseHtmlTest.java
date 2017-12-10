package sma.lbc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.htmlparser.util.ParserException;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sma.lbc.mgr.LbcParser;
import sma.lbc.model.LbcSearch;

public class ParseHtmlTest {

	/** Logger. */
	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Ignore("Old format")
	@Test
	public void testParse1File() throws ParserException {
		LbcParser lbc = new LbcParser();
		LbcSearch lbcSearch = lbc.decode("src/test/resources/data/leboncoin.1.htm");

		Assert.assertEquals(
				"Annonces Voitures toyota hzj or toyota bj or toyota hdj or toyota kzj or toyota lj or toyota hj or iltis Régions voisines Midi-Pyrénées - leboncoin.fr",
				lbcSearch.getTitleSearch());

		Assert.assertEquals(35, lbcSearch.getLstAnnonces().size());
		log.info("end testParse1File");
	}

	/**
	 * Version of 10-jan-2013
	 * 
	 * @throws ParserException
	 */
	@Test
	public void testParseFile2() throws ParserException {
		LbcParser lbc = new LbcParser();
		LbcSearch lbcSearch = lbc.decode("src/test/resources/data/leboncoin.2.htm");

		Assert.assertEquals(
				"Annonces Voitures toyota hzj or toyota bj or toyota hdj or toyota kzj or toyota lj or toyota hj or iltis Régions voisines Midi-Pyrénées - leboncoin.fr",
				lbcSearch.getTitleSearch());

		Assert.assertEquals(35, lbcSearch.getLstAnnonces().size());
		log.info("end testParse1File");
	}

	@Ignore
	@Test
	public void testMerge2Files() throws ParserException {
		LbcParser lbc = new LbcParser();

		List<String> lstFiles = new ArrayList<String>();
		lstFiles.add("src/test/resources/data/leboncoin.1.htm");
		lstFiles.add("src/test/resources/data/leboncoin.bj73.htm");

		LbcSearch lbcSearch = lbc.decodeList(lstFiles);

		log.info("end testMerge2Files");
	}

	@Test
	public void testParseDate1() {
		Date date = LbcParser.parseDate("14 sept", "16:53");
		log.info("date={}", date);
	}

	@Ignore
	@Test
	public void testParseDate2() {
		Date date = LbcParser.parseDate("Hier", "07:49");
	}

	@Ignore
	@Test
	public void testParseDate3() {
		Date date = LbcParser.parseDate("Aujourd'hui", "07:27");
	}

	@Test
	public void testtranslateHtmlEntities() {
		Assert.assertEquals("é", LbcParser.translateHtmlEntities("&eacute;"));
		Assert.assertEquals("GPS « TOM TOM ONE V1 » - Carte France",
				LbcParser.translateHtmlEntities("GPS &laquo; TOM TOM ONE V1 &raquo; &#45; Carte France"));
	}

}
