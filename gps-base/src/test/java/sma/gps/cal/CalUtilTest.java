package sma.gps.cal;

import junit.framework.Assert;

import org.junit.Test;

public class CalUtilTest {

	@Test
	public void testgetLastDelimited() {
		String extract = CalUtil.getLastDelimited("C:\\etrt\\toto.txt", '\\');
		Assert.assertEquals("toto.txt", extract);
	}

}
