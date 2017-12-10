package sma.gmap;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Rectangle2D.Double;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sma.gmap.download.DownloadeurMultiple;
import sma.gps.cal.CalTtqv;
import sma.gps.model.Coord;

/**
 * repr�sente une carte GoogleMap compos�e de plusieurs dalles
 * 
 * @author smarsoll
 * 
 */
public class MichelinMap {
	// Log4J
	private static final Logger sLog = LoggerFactory.getLogger(MichelinMap.class);

	private static final String SAVE_PATH = "D:/map/Java/googleMap" + "/";
	// private static final String TEMP_PATH = "D:/map/Java/tmp" + "/";

	int zoomLevel;
	/** zone à r�cup�rer */
	double lat1, lon1, lat2, lon2;

	Point pointMin, pointMax;

	private String serie;

	public MichelinMap(String serie, int x, int y, int xx, int yy) {
		super();
		this.serie = serie;
		this.pointMin = new Point();
		this.pointMin.x = x;
		this.pointMin.y = y;
		this.pointMax = new Point();
		this.pointMax.x = xx;
		this.pointMax.y = yy;
		

	}
	public int getNbDalles() {
		return (pointMax.x - pointMin.x + 1) * (pointMax.y - pointMin.y + 1);
	}

	public int getNbDallesX() {
		return (pointMax.x - pointMin.x + 1);
	}

	public int getNbDallesY() {
		return (pointMax.y - pointMin.y + 1);
	}


	/**
	 * r�cup�re les dalles satellites
	 * 
	 * @throws InterruptedException
	 */
	public void importDallesMichelin(int nbThreads) throws InterruptedException {
		int nbDallesTotal = this.getNbDalles();
		Collection<Dalle> lstD = new HashSet<Dalle>(nbDallesTotal);
		int dalleCourante = 0;
		for (int x = pointMin.x; x <= pointMax.x; x++) {
			for (int y = pointMin.y; y <= pointMax.y; y++) {
				dalleCourante++;
				Dalle d = new DalleMichelin(serie, x, y);
				lstD.add(d);
			}
		}

		DownloadeurMultiple dm = new DownloadeurMultiple();
		dm.setNbThreads(nbThreads);
		dm.setLstDalles(new ArrayList<Dalle>(lstD));
		dm.downloadAllDalles();
	}


	/**
	 * generated image filename
	 * 
	 * @return
	 */
	public String getImageFileName() {
		return serie + "_" + pointMin.x + "_" + pointMin.y + ".png";
	}

	/**
	 * create big JPEG image file
	 * 
	 * @throws IOException
	 */
	public void saveMap() throws IOException {
		String imagefileName = SAVE_PATH + this.getImageFileName();
		sLog.info("creating big image file " + imagefileName);

//		BufferedImage img = new BufferedImage(GoogleTileUtils2.TILE_SIZE * getNbDallesX(), GoogleTileUtils2.TILE_SIZE
//				* getNbDallesY(), BufferedImage.TYPE_3BYTE_BGR);
		BufferedImage img = new BufferedImage(GoogleTileUtils2.TILE_SIZE * getNbDallesX(), GoogleTileUtils2.TILE_SIZE
				* getNbDallesY(), BufferedImage.TYPE_USHORT_555_RGB);

		Graphics g = img.getGraphics();

		// pour chaque dalle
		for (int x = pointMin.x; x <= pointMax.x; x++) {
			for (int y = pointMin.y; y <= pointMax.y; y++) {
				sLog.debug("drawing " + x + " " + y);
				{
					Dalle d = new DalleMichelin(serie, x, y);
					BufferedImage imgDalle;
					String fileName = d.getFileName();
					try {
						File f = new File(fileName);
						if (f.exists()) {
							imgDalle = ImageIO.read(f);
							g.drawImage(imgDalle, (x - pointMin.x) * DalleMichelin.TILE_SIZE, (y - pointMin.y)
									* DalleMichelin.TILE_SIZE, null);
						}

					} catch (IOException e) {
						sLog.error("can not read file " + fileName);
						throw e;
					}

				}

			}
		}

		// point 0,0
		g.setColor(new Color(200, 100, 200, 180));
		g.setFont(new Font("Arial", Font.BOLD, 20));
		g.drawString("S�bM", 10, 20);

		g.dispose();
		// save new image file
		File f = new File(imagefileName);
		//ImageIO.write(img, "jpg", f);
		ImageIO.write(img, "png", f);

	}

	/**
	 * Create calibration file for TTQV
	 * 
	 * @throws IOException
	 */
	public void saveCalibrationFile() throws IOException {
		CalTtqv cal = new CalTtqv();
		cal.setDirName(SAVE_PATH);
		cal.setImageFileName(this.getImageFileName());
		cal.setMapDatum("WGS 84");
		// image size
		cal.setImageHeight(GoogleTileUtils2.TILE_SIZE * getNbDallesY());
		cal.setImageWidth(GoogleTileUtils2.TILE_SIZE * getNbDallesX());
		

		// calibration points
		// pixel points
		int[] p0 = { 0, 0 };
		// int[] p1= new int[2];
		int[] p1 = { GoogleTileUtils2.TILE_SIZE * getNbDallesX() - 1, 0 };
		int[] p2 = { GoogleTileUtils2.TILE_SIZE * getNbDallesX() - 1, GoogleTileUtils2.TILE_SIZE * getNbDallesY() - 1 };
		int[] p3 = { 0, GoogleTileUtils2.TILE_SIZE * getNbDallesY() - 1 };

		cal.getCalPoints().add(p0);
		cal.getCalPoints().add(p1);
		cal.getCalPoints().add(p2);
		cal.getCalPoints().add(p3);

		// calibration coordinates
		// this.pointMin.x
		// cal.getCalCoords().add(new Coord(1, 3));

		Double rect;

		rect = GoogleTileUtils2.getTileRect(this.pointMin.x, this.pointMin.y, this.zoomLevel);
		// sLog.info(this.pointMin);
		// sLog.info(rect);
		List<Coord> calCoords = cal.getCalCoords();
		calCoords.add(new Coord(rect.getMaxY(), rect.getMinX()));

		rect = GoogleTileUtils2.getTileRect(this.pointMax.x, this.pointMin.y, this.zoomLevel);
		calCoords.add(new Coord(rect.getMaxY(), rect.getMaxX()));

		rect = GoogleTileUtils2.getTileRect(this.pointMax.x, this.pointMax.y, this.zoomLevel);
		calCoords.add(new Coord(rect.getMinY(), rect.getMaxX()));

		rect = GoogleTileUtils2.getTileRect(this.pointMin.x, this.pointMax.y, this.zoomLevel);
		calCoords.add(new Coord(rect.getMinY(), rect.getMinX()));
		
		
		boolean centerPoint = (this.getNbDallesY() > 3) || (this.getNbDallesX() > 3) ;
		if (centerPoint) {
			int xCenter = this.getNbDallesX() / 2;
			int yCenter = this.getNbDallesY() / 2;
			int[] pCenter = { GoogleTileUtils2.TILE_SIZE * xCenter + (GoogleTileUtils2.TILE_SIZE / 2), 
					GoogleTileUtils2.TILE_SIZE * yCenter + (GoogleTileUtils2.TILE_SIZE /2) };
			cal.getCalPoints().add(pCenter);
			// geographic coordinates
			rect = GoogleTileUtils2.getTileRect(pointMin.x + xCenter, pointMin.y + yCenter, this.zoomLevel);
			calCoords.add(new Coord(rect.getCenterY(), rect.getCenterX()));

		}


		// extents of the map
		cal.calcExtent();

		// save file
		cal.calculateCalibrationFileName();
		cal.saveInDir(SAVE_PATH);
	}

}
