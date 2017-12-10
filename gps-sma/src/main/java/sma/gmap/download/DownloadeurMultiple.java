package sma.gmap.download;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import sma.gmap.Dalle;

/**
 * manage les threads
 * @author smarsoll
 *
 */
public class DownloadeurMultiple { 
	
	/** number of threads */
	int nbThreads = 1;
	/** all the dalle to download */
	List<Dalle> lstDalles;
	
	
	public void setNbThreads(int nbThreads) {
		this.nbThreads = nbThreads;
	}


	public void setLstDalles(List<Dalle> lstDalles) {
		this.lstDalles = lstDalles;
	}


	/**
	 * get dalle to download
	 * @return null when no more dalle to download
	 */
	public Dalle getADalleToDownload() {
		
		synchronized (lstDalles) {
			int taille = lstDalles.size();
			if (taille == 0) {
				// no more element to process
				return null;
			} else {
				// return last element and remove it
				return lstDalles.remove(taille - 1);
			}
		}
	}
	
	/**
	 * main processing method
	 * @throws InterruptedException 
	 */
	public void downloadAllDalles() throws InterruptedException {
		Thread[] lstT = new Thread[this.nbThreads];
		// create threads
		for (int i = 0; i < lstT.length; i++) {
			// create a download thread with me as fournisseur
			DownloadThread dt = new DownloadThread(this, i);
			Thread t = new Thread(dt, "download" + i);
			lstT[i] = t;
		}
		
		// start threads
		for (int i = 0; i < lstT.length; i++) {
			lstT[i].start();
		}
		
		
		// wait for end of processing in threads
		for (int i = 0; i < lstT.length; i++) {
			lstT[i].join();
		}
		
	}

}
