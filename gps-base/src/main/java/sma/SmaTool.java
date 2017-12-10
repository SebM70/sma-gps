/*
 * SmaTool.java
 *
 * Created on 20 ao�t 2002, 09:45
 */

package sma;

import java.io.File;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * 
 * @author smarsoll
 */
public class SmaTool {
	
	// Log4J
	private static final Logger sLog = LoggerFactory.getLogger(SmaTool.class);


	/** Creates new SmaTool */
	public SmaTool() {
	}

	public static String replace(String target, String from, String to) {
		// target is the original string
		// from is the string to be replaced
		// to is the string which will used to replace
		int start = target.indexOf(from);
		if (start == -1)
			return target;
		int lf = from.length();
		char[] targetChars = target.toCharArray();
		StringBuffer buffer = new StringBuffer();
		int copyFrom = 0;
		while (start != -1) {
			buffer.append(targetChars, copyFrom, start - copyFrom);
			buffer.append(to);
			copyFrom = start + lf;
			start = target.indexOf(from, copyFrom);
		}
		buffer.append(targetChars, copyFrom, targetChars.length - copyFrom);
		return buffer.toString();
	}

	public static String[] StringtoArray(String s, String sep) {
		// convert a String s to an Array, the elements
		// are delimited by sep
		StringBuffer buf = new StringBuffer(s);
		int arraysize = 1;
		for (int i = 0; i < buf.length(); i++) {
			if (sep.indexOf(buf.charAt(i)) != -1)
				arraysize++;
		}
		String[] elements = new String[arraysize];
		int y, z = 0;
		if (buf.toString().indexOf(sep) != -1) {
			while (buf.length() > 0) {
				if (buf.toString().indexOf(sep) != -1) {
					y = buf.toString().indexOf(sep);
					if (y != buf.toString().lastIndexOf(sep)) {
						elements[z] = buf.toString().substring(0, y);
						z++;
						buf.delete(0, y + 1);
					} else if (buf.toString().lastIndexOf(sep) == y) {
						elements[z] = buf.toString().substring(0, buf.toString().indexOf(sep));
						z++;
						buf.delete(0, buf.toString().indexOf(sep) + 1);
						elements[z] = buf.toString();
						z++;
						buf.delete(0, buf.length());
					}
				}
			}
		} else {
			elements[0] = buf.toString();
		}
		buf = null;
		return elements;
	}

	/**
	 * return a list of file from a path like "..\toto\*dsd.plt"
	 */
	public static List<String> getFileNames(String mask) {
		List<String> lstResult = new LinkedList<String>();
		if (mask.indexOf('*') < 0) {
			lstResult.add(mask);
		} else {
			File fMask = new File(mask);
			String endF = fMask.getName().substring(1);
			System.out.println(endF);
			File parent = fMask.getParentFile();
			if (parent == null)
				parent = new File(".");
			File[] lstF = parent.listFiles();
			if (lstF == null) {
				sLog.debug("parent.listFiles() == null for parent=" + parent.getAbsolutePath());
				lstF = new File[0];
			}
			for (int i = 0; i < lstF.length; i++) {
				// System.out.println(fMask.getName());
				if (lstF[i].getName().endsWith(endF)) {
					// System.out.println(lstF[i].getPath());
					lstResult.add(lstF[i].getPath());
				}
			}
		}
		return lstResult;
	}

	public static Element addSimpleElementT(Element pere, String name, String text) {
		Element elem = pere.getOwnerDocument().createElement(name);
		pere.appendChild(elem);
		if (text != null) {
			Text t = pere.getOwnerDocument().createTextNode(text);
			elem.appendChild(t);
		}
		return elem;
	}

	public static void log_old(String arg) {
		System.out.println(arg);
	}

	/**
	 * log a SQL resultset
	 * @param rs
	 * @throws SQLException
	 */
	public static void logRS(ResultSet rs) throws SQLException {
		if (rs != null) {
			ResultSetMetaData metarst = rs.getMetaData();
			int colCount = metarst.getColumnCount();
			sLog.debug("getColumnCount()=" + colCount);
			String columns = "";
			for (int i = 1; i <= colCount; i++) {
				columns += metarst.getColumnName(i) + " | ";
			}
			sLog.debug(columns);

			while (rs.next()) {
				// log(rs.getString(1) + " " + rs.getString(3));
				columns = "";
				for (int i = 1; i <= colCount; i++) {
					columns += rs.getString(i) + " | ";
				}
				sLog.debug(columns);
			}

		}
	}
}
