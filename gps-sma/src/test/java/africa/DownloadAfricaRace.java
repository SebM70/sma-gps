package africa;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sma.fichier.FiletUtils;

public class DownloadAfricaRace {
	private static final Logger LOGGER = LoggerFactory.getLogger(DownloadAfricaRace.class);

	@Test
	public void donwload() throws IOException {
		String basehttpAdr = "http://vulcain.iritrack.net/tdcom/eviewer/AFRICARACE2014/rss/ETAPE6/trk0200.xml";
		// http://vulcain.iritrack.net/tdcom/eviewer/AFRICARACE2014/rss/ETAPE6/trk0200.xml

		download(233);

	}

	private void download(int concurent) throws IOException {
		for (int i = 3; i <= 10; i++) {
			download(concurent, i);
		}
	}

	private void download(int concurent, int etape) throws IOException {
		String fileName = "trk0" + concurent + ".xml";
		String sourceUrl = "http://vulcain.iritrack.net/tdcom/eviewer/AFRICARACE2014/rss/ETAPE" + etape + "/" + fileName;
		LOGGER.info(sourceUrl);
		URL url = new URL(sourceUrl);

		File targetDir = new File("src/test/resources/2014/e" + etape);
		targetDir.mkdirs();
		File targetFile = new File(targetDir, fileName);

		FileOutputStream out = new FileOutputStream(targetFile);

		FiletUtils.copyStream(url.openStream(), out);

		out.flush();
		out.close();

	}

}
