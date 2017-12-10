package sma.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Utility for XML
 * 
 * @author marsolle
 * 
 */
public final class XmlUtil {

	/** Log4J */
	private static final Logger sLog = LoggerFactory.getLogger(XmlUtil.class);

	private static DocumentBuilderFactory dbf;
	private static DocumentBuilder db;
	static {
		dbf = DocumentBuilderFactory.newInstance();
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			sLog.error("init error", e);
		}
	}

	/**
	 * Load an XML file
	 * 
	 * @param filename
	 * @return root Element
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 */
	public static Element getRootElementFromXmlFile(String filename) throws SAXException, IOException {
		Document doc = db.parse(filename);
		return doc.getDocumentElement();
	}

	public static Element getRootElementFromXmlFile(File pFile) throws SAXException, IOException {
		Document doc = db.parse(pFile);
		return doc.getDocumentElement();
	}

	public static Element getSingleElement(Element pRootElem, String name) {
		return (Element) pRootElem.getElementsByTagName(name).item(0);
	}

	public static Collection<String> getListElementValue(Element pImgId, String pString) {
		// TODO Auto-generated method stub
		NodeList nList = pImgId.getElementsByTagName(pString);
		ArrayList<String> result = new ArrayList<String>(nList.getLength());
		for (int i = 0; i < nList.getLength(); i++) {
			Element element = (Element) nList.item(i);
			result.add(element.getTextContent());
		}
		return result;
	}

}
