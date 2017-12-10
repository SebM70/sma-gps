package sma;

import java.io.File;

import org.junit.Test;

public class FilterMpTest {

	@Test
	public void testMain() {
		String dir = "C:\\SMa\\GPS\\edit\\mps_reunion";
		String source = dir + "/origin";
		String dest = dir + "/filtered";

		File fDel = new File(dest, "REUNION.mp");
		fDel.delete();

		FilterMp.main(new String[] { source, dest });
	}
}
