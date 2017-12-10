package sma.lbc;

import org.htmlparser.util.ParserException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sma.lbc.mgr.LbcParser;

public class ParseHtmlOnlineTest {

	/** Logger. */
	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Test
	public void testParseUrl() throws ParserException {
		LbcParser lbc = new LbcParser();
		lbc.decode("http://www.leboncoin.fr/voitures/offres/midi_pyrenees/occasions/?f=a&sp=0&th=1&q=iltis");
		log.info("end test");
	}
}
