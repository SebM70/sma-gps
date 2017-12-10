package sma.gps.fich;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sma.gps.model.Coord;
import sma.gps.model.Track;
import sma.gps.model.TrackPoint;
import sma.gps.model.Waypoint;

/**
 * Polish file (MP)
 * 
 * @author S. MARSOLLE
 */
public class FichPolish {
	
	public static String LINE_ALLEY = "0x07";
	public static String LINE_ROAD = "0x00";
	public static String LINE_COLLECTOR = "0x05";
	public static String LINE_ARTERIAL = "0x04";
	public static String LINE_PRINC_HWAY = "0x02";
	public static String LINE_MAJOR_HWAY = "0x01";
	public static String LINE_HWAY_CONNECT = "0x0b";
	public static String LINE_RUNWAY = "0x27";

	File mFile = null;
	BufferedReader mBr = null;
	Writer mWriter = null;

	PrintStream mOut = null;
	Map<String, String[]> poiMap = null;
	DecimalFormat formCoord;

	/** Log4J */
	private static final Logger sLog = LoggerFactory.getLogger(FichPolish.class);

	public FichPolish(PrintStream pOut) {
		super();
		this.mOut = pOut;
		DecimalFormatSymbols fs = new DecimalFormatSymbols();
		fs.setDecimalSeparator('.');
		formCoord = new DecimalFormat("##0.0000000", fs);
	}
	
	public FichPolish(File f) {
		super();
		mFile = f;
	}
	
	/**
	 * open file to read it
	 * @throws FileNotFoundException 
	 */
	public void openRead() throws FileNotFoundException {
		mBr = 
	        new BufferedReader(new InputStreamReader( new FileInputStream( mFile) ));
	}
	
	public void closeRead() throws IOException {
		mBr.close();
		mBr = null;
	}
	
	public PolishEntity readEntity() throws IOException {
		String curLine;
		PolishEntity vPolishEntity = null;
		// look for beginning
		while ((curLine = mBr.readLine()) != null) {
			// remove blanks
			curLine = curLine.trim();
            if (curLine.startsWith("[")) {
            	sLog.debug(curLine);
            	vPolishEntity = new PolishEntity();
            	// remove last ]
            	vPolishEntity.setFamily(curLine.substring(1, curLine.length() - 1));
            	sLog.debug("found " + vPolishEntity.getFamily());
            	break;
            }
        }
		
		// take body and look for end
		if (vPolishEntity != null) {
			while ((curLine = mBr.readLine()) != null) {
				// remove blanks
				curLine = curLine.trim();
	            if (curLine.startsWith("[")) {
	            	sLog.debug("end");
	            	break;
	            } else {
	            	// normal line
	            	vPolishEntity.addLine(curLine);
	            }
	        }
			
		}
		
		return vPolishEntity;
	}

	/**
	 * Write a track to a polish file
	 * @param trk
	 * @param type
	 */
	public void writeTrack(Track trk, String level) {

		DecimalFormatSymbols fs = new DecimalFormatSymbols();
		fs.setDecimalSeparator('.');
		DecimalFormat formCoord = new DecimalFormat("##0.0000000", fs);

		List<TrackPoint> lstPoints = trk.getLTrackPt();
		if (lstPoints.size() > 0) {
			// first point is first point
			TrackPoint first = lstPoints.get(0);
			first.setFirst(true);
			boolean realFirst = true;

			for (Iterator<TrackPoint> iter = lstPoints.iterator(); iter.hasNext();) {
				TrackPoint element = (TrackPoint) iter.next();
				if (element.isFirst()) {
					if (realFirst) {
						realFirst = false;
					} else {
						// finish previouse element
						mOut.println();
						mOut.println("[END-RGN40]");
					}
					mOut.println("[RGN40]");
					mOut.print("Type=");
					mOut.println(trk.getTypePolish());
					mOut.println("Levels=" + level);
					mOut.println("Label=" + trk.getName());
					// Data0=(31.64135,9.24103),(31.64179,9.24120)
					mOut.print("Data0=(");

				} else {
					mOut.print(",(");

				}
				Coord coord = element.getCoord();
				mOut.print(formCoord.format(coord.latit));
				mOut.print(",");
				mOut.print(formCoord.format(coord.longit));
				mOut.print(")");

			}
			mOut.println();
			mOut.println("[END-RGN40]");
		}
	}

	public void writePoint(Waypoint wpt, int level, DecalMap pDecalMap) {
		// sym = Integer.parseInt( wpt.symbol );

		String[] translate = poiMap.get(wpt.symbol);
		String rgn;
		String type;
		if (translate == null) {
			rgn = "10";
			type = "0x5e00";
			translate = (String[]) poiMap.get("default");
			if (translate != null) {
				rgn = translate[1];
				type = translate[2];
			}
			mOut.println("; " + wpt.symbol + "  ->default");
		} else {
			rgn = translate[1];
			type = translate[2];
			mOut.println("; " + wpt.symbol);
		}
		level = pDecalMap.calcateLevel(type,level);
		
		sLog.debug(wpt.name +" wpt.symbol="+wpt.symbol +" type="+type);

		mOut.println("[POI]");
		mOut.println("Type=" + type);
		mOut.print("Label=");
		mOut.println(wpt.name);
		mOut.println("levels=" + level);
		//out.print("Origin0=(");
		mOut.print("Data0=("); //		out.print(level); out.print("=(");
		mOut.print(formCoord.format(wpt.coord.latit));
		mOut.print(",");
		mOut.print(formCoord.format(wpt.coord.longit));
		mOut.println(")");
		mOut.println("[END]");
			
	}

	public void setPoiMap(Map<String, String[]> poiMap) {
		this.poiMap = poiMap;
	}

	public void openWrite() throws FileNotFoundException {
		FileOutputStream fos = new FileOutputStream(mFile);
		mWriter  = 
	        new BufferedWriter(new OutputStreamWriter(fos));

		
	}

	public void closeWrite() throws IOException {
		mWriter.flush();
		mWriter.close();
	}

	public void writeEntity(PolishEntity pol) throws IOException {
		mWriter.write("[");		
		mWriter.write(pol.getFamily());		
		mWriter.write("]\n");
		
		for (Iterator<String> iter = pol.getLstLines().iterator(); iter.hasNext();) {
			String line = iter.next();
			mWriter.write(line);
			mWriter.write("\n");
		}
		
		mWriter.write("[");		
		mWriter.write("END");		
		mWriter.write("]\n\n");		
	}

}
