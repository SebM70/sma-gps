package sma.gps.process;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sma.gps.fich.FichPolish;
import sma.gps.model.Track;
import sma.gps.model.TrackPoint;

/**
 * Transformation aroud tracks
 * 
 * @author marsolle
 * 
 */
public class TrackProcessor {

	/** Log4J */
	private static final Logger sLog = LoggerFactory.getLogger(TrackProcessor.class);

	/** parameter to distribute speed */
	static SpeedParameter[] paramSpeed = new SpeedParameter[] {
			// low speed
			new SpeedParameter(FichPolish.LINE_ALLEY, 0f),
			// medium speed
			new SpeedParameter(FichPolish.LINE_ARTERIAL, 15f),
			//
			new SpeedParameter(FichPolish.LINE_COLLECTOR, 30f),
			//
			new SpeedParameter(FichPolish.LINE_PRINC_HWAY, 55f) };

	static {
		completeParameters();
	}

	private static void completeParameters() {
		// set the maximum speed to the next minimum speed
		for (int i = 0; i < (paramSpeed.length -1); i++) {
			SpeedParameter speedParameter = paramSpeed[i];
			speedParameter.setMaximumSpeed(paramSpeed[i + 1].getMinimumSpeed());
		}

	}
	/**
	 * put different polish type depending on speed
	 * 
	 * @param pLstTrack
	 * @return
	 */
	public List<Track> processSpeed(List<Track> pLstTrack) {

		List<Track> newTracks = new LinkedList<Track>();
		for (Track track : pLstTrack) {
			this.processOneTrack(track, newTracks);
		}

		return newTracks;
	}


	/**
	 * Main alogorythm
	 * 
	 * @param pTrack
	 * @param pNewTracks
	 */
	private void processOneTrack(Track pTrack, Collection<Track> pNewTracks) {

		List<TrackPoint> lstTrackPt = pTrack.getLTrackPt();
		Track currentTrack = null;
		// SpeedParameter oldParam = this.getSpeedParameter(lstTrackPt.get(0).getSpeed());
		SpeedParameter oldParam = null;
		TrackPoint oldTrackPoint = null;
		for (TrackPoint trackPoint : lstTrackPt) {
			boolean createNewTrack = false;
			SpeedParameter curentParam = this.getSpeedParameter(trackPoint.getSpeed());
			// compare by pointer because of static param
			if (curentParam != oldParam) {
				createNewTrack = true;
				oldParam = curentParam;
			}
			if (createNewTrack || trackPoint.isFirst()) {
				currentTrack = pTrack.createCopyTrack();
				currentTrack.setTypePolish(curentParam.getPolishType());
				currentTrack.setName(pTrack.getName() + " " + curentParam.getLabel());
				// To have visual link between tracks
				if (oldTrackPoint != null) {
					currentTrack.getLTrackPt().add(oldTrackPoint);
				}
				pNewTracks.add(currentTrack);
			}
			currentTrack.getLTrackPt().add(trackPoint);
			oldTrackPoint = trackPoint;
		}

	}

	/**
	 * Find the correct speed parameter
	 * 
	 * @param pSpeed
	 * @return
	 */
	private SpeedParameter getSpeedParameter(float pSpeed) {
		for (int i = 0; i < (paramSpeed.length); i++) {
			SpeedParameter speedParameter = paramSpeed[i];
			if ((speedParameter.getMinimumSpeed() <= pSpeed) && (pSpeed < speedParameter.getMaximumSpeed())) {
				return speedParameter;
			}
		}
		sLog.warn("No SpeedParameter found for pSpeed=" + pSpeed);
		return null;
	}

}
