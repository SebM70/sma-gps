/*
 * Created on 22 juil. 2004
 * 
 * To change the template for this generated file go to Window - Preferences -
 * Java - Code Generation - Code and Comments
 */
package sma;

// import java.util.*;
import java.io.*;
import java.util.*;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.*;

/**
 * @author smarsoll
 * 
 * Analyse directories and show size usage
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class ShowDirSize {

	static PrintStream out = System.out;

	static Map _MapFUnique = new HashMap(10000);

	static boolean uniqueNameSize = false;

	static Element _Rac = null;

	static Element _Doublons = null;

	public static void main(String[] args) {

		for (int i = 1; i < args.length; i++) {
			if (args[i].equals("-uniqueNameSize")) {
				// traitement de doublons que l'on liste dans le XML
				uniqueNameSize = true;
			}
		}

		try {
			File base = new File(args[0]);

			// XML document
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			DocumentBuilder builder;
			builder = factory.newDocumentBuilder();
			Document doc = builder.newDocument();
			_Rac = doc.createElement("ShowDirSize");
			doc.appendChild(_Rac);
			_Rac.setAttribute("baseDir", base.getAbsolutePath());

			if (uniqueNameSize) {
				_Doublons = doc.createElement("Double");
				_Rac.appendChild(_Doublons);
			}

			// begin
			analyseDir(base, _Rac);
			// report();
			FileOutputStream xmlFileStream = new FileOutputStream("ShowDirSize_result.xml");
			// Save the XML data in the stream
			StreamResult result = new StreamResult(xmlFileStream);
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer();
			transformer.transform(new DOMSource(doc), result);

			out.println("...end");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * return size of directory
	 * 
	 * @param base
	 */
	private static long analyseDir(File dir, Element pere) {
		long dirSize = 0;

		Document doc = pere.getOwnerDocument();
		// Element newDescr = doc.createElement(fileNameToElement(dir));
		Element newDescr = doc.createElement("dir");

		newDescr.setAttribute("name", dir.getName());

		// TODO Auto-generated method stub
		File[] lf = dir.listFiles();
		for (int i = 0; i < lf.length; i++) {
			if (lf[i].isDirectory())
				dirSize = dirSize + analyseDir(lf[i], newDescr);
			else {
				dirSize = dirSize + lf[i].length();
				if (uniqueNameSize) {
					// we have to check for double
					Element newFile = doc.createElement("file");
					newFile.setAttribute("path", lf[i].getAbsolutePath());
					newFile.setAttribute("name", lf[i].getName());
					newFile.setAttribute("size", String.valueOf(lf[i].length()));

					String cle = lf[i].getName() + "|" + lf[i].length();
					Element res = (Element) _MapFUnique.get(cle);
					if (res == null) {
						_MapFUnique.put(cle, newFile);
					} else {
						// there is a double
						res.appendChild(newFile);
						// store it in double list
						if (res.getParentNode() == null)
							_Doublons.appendChild(res);
					}
				}
			}

		}
		if (lf.length == 0)
			out.println("empty dir:" + dir.getAbsolutePath());
		if (dirSize > 1000000)
			out.println("" + dirSize + "  " + dir.getAbsolutePath());

		newDescr.setAttribute("size", String.valueOf(dirSize));
		// need to order in the future
		pere.appendChild(newDescr);

		return dirSize;
	}

	private static String fileNameToElement(File dir) {
		return dir.getName().replace(' ', '_');
	}
}
