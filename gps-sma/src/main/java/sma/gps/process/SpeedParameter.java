package sma.gps.process;

import java.text.DecimalFormat;

/**
 * To paramamter the speed to polishtype calculation
 * 
 * @author marsolle
 * 
 */
public class SpeedParameter {

	public SpeedParameter(String pPolishType, float pMinimumSpeed) {
		super();
		polishType = pPolishType;
		minimumSpeed = pMinimumSpeed;
	}

	String polishType;

	float minimumSpeed;

	float maximumSpeed = Float.MAX_VALUE;

	String label = null;

	/**
	 * Calculate label
	 * 
	 * @return
	 */
	public String getLabel() {
		if (label == null) {
			DecimalFormat df = new DecimalFormat("#####0");
			if (maximumSpeed != Float.MAX_VALUE) {
				label = "" + df.format(minimumSpeed) + "-" + df.format(maximumSpeed);
			} else {
				label = "" + df.format(minimumSpeed) + "--";
			}
		}
		return label;
	}

	public String getPolishType() {
		return polishType;
	}

	public void setPolishType(String pPolishType) {
		polishType = pPolishType;
	}

	public float getMinimumSpeed() {
		return minimumSpeed;
	}

	public void setMinimumSpeed(float pMinimumSpeed) {
		minimumSpeed = pMinimumSpeed;
	}

	public float getMaximumSpeed() {
		return maximumSpeed;
	}

	public void setMaximumSpeed(float pMaximumSpeed) {
		maximumSpeed = pMaximumSpeed;
	}

}
