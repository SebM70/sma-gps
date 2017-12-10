package sma.gmap.download;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sma.gmap.Dalle;

public class DownloadThread implements Runnable {

	/** Log4J */
	private static final Logger sLog = LoggerFactory.getLogger(DownloadThread.class);

	public static int MIN_MS = 500;
	public static int MAX_MS = 2000;

	/** fournisseur de dalles à télécharger */
	DownloadeurMultiple fournisseur;
	int id;

	/** nombre de download */
	int nbDownload = 0;

	public DownloadThread(DownloadeurMultiple fournisseur, int id) {
		super();
		this.fournisseur = fournisseur;
		this.id = id;
	}

	/**
	 * thread processing
	 */
	public void run() {
		// sLog.warn("nothing coded ! ");
		Dalle d;
		try {
			while ((d = this.fournisseur.getADalleToDownload()) != null) {
				nbDownload++;
				if (!d.fileExist()) { 
					String url = d.getUrlAdress();
					String fName = d.getFileName();
					sLog.info("importing dalle " + id + " " + nbDownload + "  " + url + " in " + fName);
					// create containing directory if is it does not exists
					File f = new File(fName);
					f.getParentFile().mkdirs();
					
					// take file from googleMap server
					SuckURL suckUrl = new SuckURL(url, fName);
					suckUrl.doit();
					
					// wait time 1 s
					long waitMs = (long) (DownloadThread.MIN_MS + (Math.random() * DownloadThread.MAX_MS));
					Thread.sleep(waitMs);
					
				}
			}
		} catch (Exception e) {
			sLog.error("can not download dalle, stoping thread", e);
		}

	}

}
