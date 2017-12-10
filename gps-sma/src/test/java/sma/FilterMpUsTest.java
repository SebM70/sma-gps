package sma;

import org.junit.Test;

public class FilterMpUsTest {

	@Test
	public void testMain() {
		String dir = "C:/SMa/GPS/edit/mps_TopoUsa/";
		String source = dir + "/mp0";
		String dest = dir + "/mp1";

		// File fDel = new File(dest, "REUNION.mp");
		// fDel.delete();

		FilterMp.main(new String[] { source, dest });
	}
}
