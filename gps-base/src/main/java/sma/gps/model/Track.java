/*
 * Track.java
 *
 * Created on 1 septembre 2001, 14:01
 */

package sma.gps.model;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import sma.SmaTool;
import sma.gps.kml.KmlElement;

/**
 * 
 * @author MARSOLLE
 * @version
 */
public class Track  implements KmlElement {

	// possible values: clampToGround absolute relativeToGround
	public static String altitudeMode = "clampToGround";

	// Log4J
	private static final Logger sLog = LoggerFactory.getLogger(Track.class);

	// list of track points
	private List<TrackPoint> lTrackPt;

	// list of subTrack (each subtrack start with a fist point)
	private List<Track> subTracks;

	public String nom;

	private String description;

	// Style is like:
	// 2#32#8421376#8#-1#0#0#0#-1#-1#-1#420#-825#0#Arial#-1#0#0#100#93#1310#1#0#0#2#13#1#0##0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#16777215#0#0#32#3#0#0#0#0#0#0#
	private String style;

	private String styleCol[];

	/** type to be written in polish files */
	private String typePolish;

	/** Creates new Track */
	public Track() {
		lTrackPt = new LinkedList<TrackPoint>();
	}

	public void add(Coord coord) {
		lTrackPt.add(new TrackPoint(coord));
	}

	public void add(final Coord coord, final boolean first) {
		TrackPoint tp = new TrackPoint(coord);
		tp.setFirst(first);
		lTrackPt.add(tp);
	}

	public void add(final Coord coord, final boolean first, final float speed) {
		TrackPoint tp = new TrackPoint(coord);
		tp.setFirst(first);
		tp.setSpeed(speed);
		lTrackPt.add(tp);
	}

	public List<TrackPoint> getLTrackPt() {
		return lTrackPt;
	}

	public void filterZone(Zone z) {
		Iterator<TrackPoint> it = lTrackPt.iterator();
		boolean hasBeenRemoved = false;
		while (it.hasNext()) {
			TrackPoint tp = it.next();
			if (z.isInZone(tp.getCoord())) {
				// point to keep, check if previous point has been removed
				if (hasBeenRemoved)
					tp.setFirst(true);
				hasBeenRemoved = false;
			} else {
				// point to remove
				it.remove();
				hasBeenRemoved = true;
			}
		}

	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * for KML exportation
	 */
	public Element addDomElement(Element pere) {
		// build sub lists
		buildSubTracks();
		boolean trkIsContinuous = (subTracks.size() == 1);

		Element foldElem;

		// if 1 continuous track then do not need to create a folder
		if (trkIsContinuous) {
			foldElem = SmaTool.addSimpleElementT(pere, "Placemark", null);
		} else {
			foldElem = SmaTool.addSimpleElementT(pere, "Folder", null);
		}

		SmaTool.addSimpleElementT(foldElem, "name", nom);
		SmaTool.addSimpleElementT(foldElem, "description", description);
		int numSub = 1;

		for (Iterator<Track> iter = subTracks.iterator(); iter.hasNext();) {
			Track subTrk = iter.next();
			Element mainElem;
			if (trkIsContinuous) {
				mainElem = foldElem;
			} else {
				mainElem = SmaTool.addSimpleElementT(foldElem, "Placemark", null);
				SmaTool.addSimpleElementT(mainElem, "name", nom + "-" + numSub);
			}
			numSub++;

			/*
			 * <Style> <LineStyle> <color>7000ffff</color> <width>6</width>
			 * </LineStyle> </Style>
			 */
			// getColor();
			// add ff000000 for no transparency
			String colStr = Integer.toHexString(getColor() + getTransparency() * 256 * 256 * 256);
			// SmaTool.log(colStr);
			Element st = SmaTool.addSimpleElementT(mainElem, "Style", null);
			st = SmaTool.addSimpleElementT(st, "LineStyle", null);
			SmaTool.addSimpleElementT(st, "color", colStr);
			SmaTool.addSimpleElementT(st, "width", "" + getSize());

			Element lineString = SmaTool.addSimpleElementT(mainElem, "LineString", null);
			SmaTool.addSimpleElementT(lineString, "tessellate", "1");
			SmaTool.addSimpleElementT(lineString, "altitudeMode", altitudeMode);

			// build long string with point coordinates
			StringBuffer sb = new StringBuffer(10 * subTrk.getLTrackPt().size());
			for (Iterator<TrackPoint> itSub = subTrk.getLTrackPt().iterator(); itSub.hasNext();) {
				TrackPoint trp = itSub.next();
				Coord c = trp.getCoord();
				sb.append(c.longit + "," + c.latit + "," + c.getAlti());
				sb.append(",\n");
			}
			// remove last ,\n
			sb.deleteCharAt(sb.length() - 1);
			sb.deleteCharAt(sb.length() - 1);
			SmaTool.addSimpleElementT(lineString, "coordinates", sb.toString());
		}

		return foldElem;
	}

	/**
	 * build the list of sub tracks
	 * 
	 */
	private void buildSubTracks() {
		subTracks = new LinkedList<Track>();

		Track curTrk = null;
		if (lTrackPt.size() > 0) {
			// checking on first point
			boolean firstIsFirst = ((TrackPoint) lTrackPt.get(0)).isFirst();
			if (!firstIsFirst) {
				sLog.warn("isFirst=" + firstIsFirst + " for track " + nom);
				((TrackPoint) lTrackPt.get(0)).setFirst(true);
			}
		}
		for (Iterator<TrackPoint> iter = lTrackPt.iterator(); iter.hasNext();) {
			TrackPoint trp = iter.next();
			if (trp.isFirst()) {
				curTrk = new Track();
				subTracks.add(curTrk);
			}
			curTrk.lTrackPt.add(trp);
		}
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String pStyle) {
		this.style = pStyle;
		this.styleCol = pStyle.split("#");
	}

	public int getColor() {
		// 65#3#32768#7#0#0#0#0#-1#-1#0#-195#-195#0#Small
		// Fonts#0#0#0#100#93#1310#0#0#0#0#0#0#0#
		// 65#3#48573#7#0#0#0#0#-1#-1#0#-195#-195#0#Small
		// Fonts#0#0#0#100#93#1310#0#0#0#10#9#0#0##0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#16777215#0#0#32#3#0#0#0#0#0#0#
		String col;
		col = styleCol[2];
		// SmaTool.log("col=" + col);
		return Integer.parseInt(col);
	}

	public int getTransparency() {
		if (styleCol.length > 25) {
			if ("9".equals( styleCol[25])) {
				return 120;
			}
		}
		// default no transparent
		return 255;
	}

	public int getSize() {
		// 65#3#32768#7#0#0#0#0#-1#-1#0#-195#-195#0#Small
		// Fonts#0#0#0#100#93#1310#0#0#0#2#13#0#0##0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#16777215#0#0#32#3#0#0#0#0#0#0#
		String col[];
		col = styleCol;
//		for (int i = 0; i < col.length; i++) {
//			String value = col[i];
//			FichGps.log("i" + i + "   =" + value);
//		}
		if (col.length > 24) {
			return Integer.parseInt(col[24]);
		}
		// default value
		return 3;
	}

	public void setName(String pName) {
		nom = pName;
	}
	
	public String getName() {
		return nom;
	}

	public String getTypePolish() {
		return typePolish;
	}

	public void setTypePolish(String pTypePolish) {
		typePolish = pTypePolish;
	}

	public Track createCopyTrack() {
		Track newTrack = new Track();
		newTrack.nom = this.nom;
		newTrack.description = this.description;
		newTrack.typePolish = this.typePolish;

		return newTrack;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(200);
		sb.append("Track[\n");
		sb.append(this.nom);
		sb.append("]");
		for (TrackPoint tp : this.lTrackPt) {
			if (tp.isFirst()) {
				sb.append("\n");
			}
			sb.append("p");
		}
		return sb.toString();
	}

}
