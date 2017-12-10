package sma.gmap;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Rectangle2D.Double;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sma.firefox.DecodeCache;
import sma.gmap.download.DownloadeurMultiple;
import sma.gmap.download.SuckURL;
import sma.gps.cal.CalOzi;
import sma.gps.cal.CalTtqv;
import sma.gps.model.Coord;

/**
 * représente une carte GoogleMap composée de plusieurs dalles
 * 
 * @author smarsoll
 * 
 */
public class GoogleMap {
	// Log4J
	private static final Logger sLog = LoggerFactory.getLogger(GoogleMap.class);

	public static String SAVE_PATH = "D:/map/Java/googleMap" + "/";
	// private static final String TEMP_PATH = "D:/map/Java/tmp" + "/";

	int zoomLevel;
	/** zone à récupérer */
	double lat1, lon1, lat2, lon2;

	Point pointMin, pointMax;
	
	/** S: satellite, P: Plan*/
	String mapType;
	
	List<Dalle> allDalles;
	
	public GoogleMap(int zoom, double lat1, double lon1, double lat2, double lon2) {
		super();
		this.zoomLevel = zoom;
		this.lat1 = lat1;
		this.lon1 = lon1;
		this.lat2 = lat2;
		this.lon2 = lon2;

		String satelliteRef = GoogleTileUtils2.getSatelliteRef(lat1, lon1, zoom);
		Point point1 = GoogleTileUtils2.satelliteRefToTileXY(satelliteRef);
		sLog.debug(point1.x + " " + point1.y);

		String satelliteRef2 = GoogleTileUtils2.getSatelliteRef(lat2, lon2, zoom);
		Point point2 = GoogleTileUtils2.satelliteRefToTileXY(satelliteRef2);
		sLog.debug(point2.x + " " + point2.y);

		pointMin = new Point();
		pointMax = new Point();
		if (point1.x < point2.x) {
			pointMin.x = point1.x;
			pointMax.x = point2.x;
		} else {
			pointMin.x = point2.x;
			pointMax.x = point1.x;
		}
		if (point1.y < point2.y) {
			pointMin.y = point1.y;
			pointMax.y = point2.y;
		} else {
			pointMin.y = point2.y;
			pointMax.y = point1.y;
		}

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
	 * récupère les dalles satellites
	 * 
	 * @throws Exception
	 */
	public void importDallesSatellites() throws Exception {
		int nbDallesTotal = this.getNbDalles();
		int dalleCourante = 0;
		for (int x = pointMin.x; x <= pointMax.x; x++) {
			for (int y = pointMin.y; y <= pointMax.y; y++) {
				dalleCourante++;
				Dalle d = new DalleSatellite(zoomLevel, x, y);
				if (!d.fileExist()) {
					String url = d.getUrlAdress();
					sLog.info("importing satellite " + x + " " + y + "   " + dalleCourante + "/" + nbDallesTotal + " "
							+ url);
					// take file from googleMap server
					SuckURL suckUrl = new SuckURL(url, d.getFileName());
					suckUrl.doit();
				}
			}
		}
	}

	/**
	 * récupère les dalles satellites
	 * 
	 * @throws InterruptedException
	 */
	public void importDallesFromInternet(int nbThreads) throws InterruptedException {
		int nbDallesTotal = this.getNbDalles();
		Collection<Dalle> lstD = new HashSet<Dalle>(nbDallesTotal);
		int dalleCourante = 0;
		for (int x = pointMin.x; x <= pointMax.x; x++) {
			for (int y = pointMin.y; y <= pointMax.y; y++) {
				dalleCourante++;
				Dalle d = new DalleVirtualEath(this.zoomLevel, x, y, this.mapType);
				lstD.add(d);
			}
		}
		
		// keep dalles for next processing
		allDalles = new ArrayList<Dalle>(lstD);

		DownloadeurMultiple dm = new DownloadeurMultiple();
		dm.setNbThreads(nbThreads);
		dm.setLstDalles(new ArrayList<Dalle>(lstD));
		dm.downloadAllDalles();
	}

	/**
	 * récupère les dalles transparentes
	 * 
	 * @throws InterruptedException
	 */
	public void importDallesTransparentes(int nbThreads) throws InterruptedException {
		int nbDallesTotal = this.getNbDalles();
		// init list with a HashSet and transform it to List not be sorted
		Collection<Dalle> lstD = new HashSet<Dalle>(nbDallesTotal);
		int dalleCourante = 0;
		for (int x = pointMin.x; x <= pointMax.x; x++) {
			for (int y = pointMin.y; y <= pointMax.y; y++) {
				dalleCourante++;
				Dalle d = new DalleTransparente(zoomLevel, x, y);
				lstD.add(d);
			}
		}
		
		DownloadeurMultiple dm = new DownloadeurMultiple();
		dm.setNbThreads(nbThreads);
		dm.setLstDalles(new ArrayList<Dalle>(lstD));
		dm.downloadAllDalles();
	}

	/**
	 * récupère les dalles transparentes
	 * 
	 * @throws Exception
	 */
	public void importDallesTransparentes() throws Exception {
		int nbDallesTotal = this.getNbDalles();
		int dalleCourante = 0;
		for (int x = pointMin.x; x <= pointMax.x; x++) {
			for (int y = pointMin.y; y <= pointMax.y; y++) {
				dalleCourante++;
				Dalle d = new DalleTransparente(zoomLevel, x, y);
				String fileName = d.getFileName();
				// check if file already exists
				File f = new File(fileName);
				if (!f.exists()) {
					String url = d.getUrlAdress();
					sLog.debug("importing transparente " + x + " " + y + "   " + dalleCourante + "/" + nbDallesTotal
							+ "  " + url);
					// take file from googleMap server
					SuckURL suckUrl = new SuckURL(url, fileName);
					suckUrl.doit();
				}
			}
		}
	}

	/**
	 * generated image filename
	 * 
	 * @return
	 */
	public String getImageFileName() {
		return this.getBaseImageFileName() + ".jpg";
	}
	private String getBaseImageFileName() {
		return this.mapType + this.zoomLevel + "_" + this.pointMin.x + "_" + this.pointMin.y;
	}

	/**
	 * create big JPEG image file from all tiles in Temp directory
	 * 
	 * @throws IOException
	 */
	public void saveMapFromTmpDir() throws IOException {
		String imagefileName = SAVE_PATH + this.getImageFileName();
		sLog.info("creating big image file " + imagefileName);

		BufferedImage img = new BufferedImage(GoogleTileUtils2.TILE_SIZE * getNbDallesX(), GoogleTileUtils2.TILE_SIZE
				* getNbDallesY(), BufferedImage.TYPE_3BYTE_BGR);

		Graphics g = img.getGraphics();
		
		// pour chaque dalle
		for (Dalle d : allDalles) {
			String fileName = d.getFileName();
			// check if file already exists
			File f = new File(fileName);
			if (f.exists()) {
				BufferedImage imgDalle = ImageIO.read(f);
				g.drawImage(imgDalle, (d.x - pointMin.x) * GoogleTileUtils2.TILE_SIZE, (d.y - pointMin.y)
						* GoogleTileUtils2.TILE_SIZE, null);
			}

			
		}

		// point 0,0
		g.setColor(new Color(200, 100, 200, 180));
		g.setFont(new Font("Arial", Font.BOLD, 20));
		g.drawString("SébM", 10, 20);

		g.dispose();
		// save new image file
		File f = new File(imagefileName);
		ImageIO.write(img, "jpg", f);

	}

	/**
	 * create big JPEG image file from all tiles in Temp directory
	 * 
	 * @throws IOException
	 */
	public void saveMapFromTmpDirOld() throws IOException {
		String imagefileName = SAVE_PATH + this.getImageFileName();
		sLog.info("creating big image file " + imagefileName);

		BufferedImage img = new BufferedImage(GoogleTileUtils2.TILE_SIZE * getNbDallesX(), GoogleTileUtils2.TILE_SIZE
				* getNbDallesY(), BufferedImage.TYPE_3BYTE_BGR);

		Graphics g = img.getGraphics();

		// pour chaque dalle
		for (int x = pointMin.x; x <= pointMax.x; x++) {
			for (int y = pointMin.y; y <= pointMax.y; y++) {
				sLog.debug("drawing " + x + " " + y);
				{
					Dalle d = new DalleSatellite(zoomLevel, x, y);
					BufferedImage imgDalle;
					String fileName = d.getFileName();
					try {
						File f = new File(fileName);
						if (f.exists()) {
							imgDalle = ImageIO.read(f);
							g.drawImage(imgDalle, (x - pointMin.x) * GoogleTileUtils2.TILE_SIZE, (y - pointMin.y)
									* GoogleTileUtils2.TILE_SIZE, null);
						}

					} catch (IOException e) {
						sLog.error("can not read file " + fileName);
						throw e;
					}

				}
				{
					// dalle transparente
					Dalle d = new DalleTransparente(zoomLevel, x, y);
					String fileName = d.getFileName();
					// check if file already exists
					File f = new File(fileName);
					if (f.exists()) {
						BufferedImage imgDalle = ImageIO.read(f);
						g.drawImage(imgDalle, (x - pointMin.x) * GoogleTileUtils2.TILE_SIZE, (y - pointMin.y)
								* GoogleTileUtils2.TILE_SIZE, null);
					}

				}

			}
		}

		// point 0,0
		g.setColor(new Color(200, 100, 200, 180));
		g.setFont(new Font("Arial", Font.BOLD, 20));
		g.drawString("SébM", 10, 20);

		g.dispose();
		// save new image file
		File f = new File(imagefileName);
		ImageIO.write(img, "jpg", f);

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
		// cal.calCoords.add(new Coord(1, 3));

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



	/**
	 * create big JPEG image file
	 * 
	 * @throws IOException
	 */
	public void saveMapFromCache() throws IOException {
		String imagefileName = SAVE_PATH + this.getImageFileName();
		sLog.info("creating big image file " + imagefileName);
	
		BufferedImage img = new BufferedImage(GoogleTileUtils2.TILE_SIZE * getNbDallesX(), GoogleTileUtils2.TILE_SIZE
				* getNbDallesY(), BufferedImage.TYPE_3BYTE_BGR);
	
		Graphics g = img.getGraphics();
		
		// create object that embeds cache files
		DecodeCache cache = new DecodeCache();
		// ask to read the main cache map
		cache.decodeCacheMap();
		
		// we use treemap to have sorted keys 
		Map<String, String> urlToVisit = new TreeMap<String, String>();
		
		boolean isCarteSat = this.mapType.equals(Dalle.GM_SAT);
	
		// pour chaque dalle
		for (int x = pointMin.x; x <= pointMax.x; x++) {
			for (int y = pointMin.y; y <= pointMax.y; y++) {
				sLog.debug("drawing " + x + " " + y);
				if (isCarteSat) {
					Dalle d = new DalleSatellite(zoomLevel, x, y);
					//sLog.debug(d.getKeyDalle());
					//BufferedImage imgDalle;
					byte[] dataFileImage = cache.getDataForDalle(d);
					if (dataFileImage != null) {
						InputStream is = new ByteArrayInputStream(dataFileImage);
						BufferedImage imgDalle = ImageIO.read(is);
						g.drawImage(imgDalle, (x - pointMin.x) * GoogleTileUtils2.TILE_SIZE, (y - pointMin.y)
								* GoogleTileUtils2.TILE_SIZE, null);
						is.close();
					} else {
						String proposedUrl = this.getUrlToVisitForXY(x, y);
						// divide key by two not to have to many urls
						urlToVisit.put((x/4) + ":" + (y/4), proposedUrl);
						//sLog.warn("not in cache ! " + d.getKeyDalle() + "\n" + proposedUrl );		
					}
//					String fileName = d.getFileName();
//					try {
//						File f = new File(fileName);
//						if (f.exists()) {
//							imgDalle = ImageIO.read(f);
//							g.drawImage(imgDalle, (x - pointMin.x) * GoogleTileUtils2.TILE_SIZE, (y - pointMin.y)
//									* GoogleTileUtils2.TILE_SIZE, null);
//						}
//	
//					} catch (IOException e) {
//						sLog.error("can not read file " + fileName);
//						throw e;
//					}
	
				}
				{
					// dalle transparente
					Dalle d;
					if (isCarteSat) {
						d = new DalleTransparente(zoomLevel, x, y);
					} else {
						// it's a "plan"
						d = new DallePlan(zoomLevel, x, y);
					}
					byte[] dataFileImage = cache.getDataForDalle(d);
					if (dataFileImage != null) {
						InputStream is = new ByteArrayInputStream(dataFileImage);
						BufferedImage imgDalle = ImageIO.read(is);
						g.drawImage(imgDalle, (x - pointMin.x) * GoogleTileUtils2.TILE_SIZE, (y - pointMin.y)
								* GoogleTileUtils2.TILE_SIZE, null);
						is.close();
					} else if (!isCarteSat) {
						// for carte sat, we do not get transparent dalle
						String proposedUrl = this.getUrlToVisitForXY(x, y);
						// divide key by two not to have to many urls
						urlToVisit.put((x/4) + ":" + (y/4), proposedUrl);
						sLog.warn("not in cache ! " + d.getKeyDalle() );
					}
//					String fileName = d.getFileName();
//					// check if file already exists
//					File f = new File(fileName);
//					if (f.exists()) {
//						BufferedImage imgDalle = ImageIO.read(f);
//						g.drawImage(imgDalle, (x - pointMin.x) * GoogleTileUtils2.TILE_SIZE, (y - pointMin.y)
//								* GoogleTileUtils2.TILE_SIZE, null);
//					}
	
				}
	
			}
		}
	
		// point 0,0
		g.setColor(new Color(200, 100, 200, 180));
		g.setFont(new Font("Arial", Font.BOLD, 20));
		g.drawString("SébM", 10, 20);
	
		g.dispose();
		// save new image file
		File f = new File(imagefileName);
		ImageIO.write(img, "jpg", f);
		
		// URLs to visit
		if (urlToVisit.size() > 0) {
			
			this.generateHtmlFileToBrowse(urlToVisit.values());
			this.generateReRunBatFile();
		}
	}

	/**
	 * generate a bat file to re-run importation of map
	 */
	private void generateReRunBatFile() {
		// TODO Auto-generated method stub
		
	}



	private void generateHtmlFileToBrowse(Collection<String> urlValues) throws IOException {
		String fileName = SAVE_PATH + this.getBaseImageFileName() + ".htm";
		sLog.warn("You should browse this file to load tiles in cache: " + fileName + 
				" (" + urlValues.size() + " urls to browse)");
		
		FileOutputStream fos = new FileOutputStream(fileName);
		Writer w = new BufferedWriter(new OutputStreamWriter(fos, "Cp850"));
		w.write("<html><head>\n<title>");
		w.write(this.getBaseImageFileName());
		w.write("</title>\n</head>\n<body>\n");
		w.write("<p>Vous devriez cliquer sur chacun des lines pour charger les dalles google map dans le cache du navigateur :</p>\n");
		w.write("<p>You should browse these URLs to load tiles in cache:</p>\n");
		
		w.write("nb urls: " + urlValues.size() + "<br>");
		for (String url : urlValues) {
			//sLog.warn(url);
			w.write("<a href=\"");
			w.write(url);
			w.write("\">");w.write(url);
			w.write("</a><br>\n");
			
		}
		w.write("<img src=\"");w.write(this.getImageFileName());w.write("\" width=\"800px\" heigth=\"600px\">\n");
		w.write("\n</body>\n</html>");
		
		w.flush();
		w.close();  
	}



	/**
	 * caculate URL that you should visit to load tiles in cache
	 * @param x
	 * @param y
	 * @return
	 */
	private String getUrlToVisitForXY(int x, int y) {
		
		// format decimal number
		DecimalFormatSymbols fs = new DecimalFormatSymbols();
		fs.setDecimalSeparator('.');
		DecimalFormat formCoord = new DecimalFormat("##0.00000", fs);
		
		Double rect = GoogleTileUtils2.getTileRect(x, y, this.zoomLevel);
		String lat = formCoord.format(rect.y);
		String lon = formCoord.format(rect.x);
		String proposedUrl = null;
		if (this.mapType.equals(Dalle.GM_SAT)) {
			proposedUrl = "http://maps.google.fr/maps?&t=h&ll=" + lat +"," + lon
			+ "&z=" + (17 - this.zoomLevel);
		} else if (this.mapType.equals(Dalle.GM_PLAN)) {
			// ex: http://maps.google.fr/maps?client=firefox-a&hl=fr&ie=UTF8&ll=43.603478,1.445663&spn=0.008158,0.018582&z=16
			proposedUrl = "http://maps.google.fr/maps?client=firefox-a&hl=fr&ie=UTF8&ll=" + lat +"," + lon
			+ "&z=" + (17 - this.zoomLevel);
		}
		
		return proposedUrl;
	}



	public String getMapType() {
		return mapType;
	}



	public void setMapType(String mapType) {
		this.mapType = mapType;
	}


	/**
	 * save a calibration file for OziExplore
	 * @throws IOException
	 */
	public void saveCalibrationOzi() throws IOException {

		CalOzi cal = new CalOzi();
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

		List<int[]> calPoints = cal.getCalPoints();
		calPoints.add(p0);
		calPoints.add(p1);
		calPoints.add(p2);
		calPoints.add(p3);

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
			calPoints.add(pCenter);
			// geographic coordinates
			rect = GoogleTileUtils2.getTileRect(pointMin.x + xCenter, pointMin.y + yCenter, this.zoomLevel);
			calCoords.add(new Coord(rect.getCenterY(), rect.getCenterX()));

		}


		// extents of the map
		cal.calcExtent();

		// save file
		cal.calculateCalibrationFileName();
		cal.saveFile();
	
		
	}

}
