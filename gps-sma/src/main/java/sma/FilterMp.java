package sma;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import sma.gps.fich.FichPolish;
import sma.gps.fich.PolishEntity;
import sma.polish.FilterConfig;
import sma.util.XmlUtil;

/**
 * Filter polish file
 * 
 * @author S. MARSOLLE
 */
public class FilterMp {

	/** Log4J */
	private static final Logger sLog = LoggerFactory.getLogger(FilterMp.class);

	private static String sDestPath;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		sLog.info("begin FilterMp...");

		long lBegin = System.currentTimeMillis();

		// BasicConfigurator.configure();
		// PropertyConfigurator.configure(Loader.getResource("log4j.properties"));

		try {

			sLog.info("args.length=" + args.length);

			String source = args[0];
			sDestPath = args[1];

			File fSource = new File(source);
			if (fSource.isFile()) {
				filterOneFile(fSource);
			} else if (fSource.isDirectory()) {
				sLog.debug("lenth" + fSource.listFiles().length);

				// load xml File
				loadConf(new File(fSource, "filter.xml"));

				File[] allFiles = fSource.listFiles();
				for (int i = 0; i < allFiles.length; i++) {
					File aFile = allFiles[i];
					if (aFile.getName().endsWith(".mp")) {
						filterOneFile(aFile);
					}
				}
			} else {
				sLog.error("unknow type for " + fSource);
			}

		} catch (Exception e) {
			sLog.error("error in main", e);
		}

		float lTime = (System.currentTimeMillis() - lBegin) / 1000.0f;

		sLog.info("...end in " + lTime + " s");
	}

	static FilterConfig filterConfig;

	private static void loadConf(File pFile) throws SAXException, IOException {
		sLog.info("loading conf from {}", pFile);
		Element rootElem = XmlUtil.getRootElementFromXmlFile(pFile);
		filterConfig = new FilterConfig();

		Element imgId = XmlUtil.getSingleElement(rootElem, "imgId");
		filterConfig.getRemoveKey().addAll(XmlUtil.getListElementValue(imgId, "removeKey"));

		{
			Element entity = XmlUtil.getSingleElement(rootElem, "entity");
			filterConfig.setMaxLevel(Integer.parseInt(entity.getAttribute("maxLevel")));
			NodeList nList = entity.getElementsByTagName("filtered");
			for (int i = 0; i < nList.getLength(); i++) {
				Element elemFiltered = (Element) nList.item(i);
				filterConfig.getFilteredEntities().add(
						elemFiltered.getAttribute("family") + ":" + elemFiltered.getAttribute("type"));
			}
		}

	}

	private static void filterOneFile(File f) throws IOException {
		File fCible = new File(sDestPath, f.getName());
		if (fCible.exists()) {
			sLog.info("file " + fCible + " already existing, skip it");
			return;
		}
		sLog.info("processing " + f.getAbsolutePath() + " to " + fCible.getAbsolutePath());
		FichPolish fp = new FichPolish(f);
		FichPolish fpCible = new FichPolish(fCible);
		fp.openRead();
		fpCible.openWrite();

		PolishEntity vPol;

		int nbEntitySource = 0;
		int nbEntityCible = 0;

		// first entity
		vPol = fp.readEntity();
		sLog.debug("readEntity ");
		if ("IMG ID".equals(vPol.getFamily())) {
			// keep only 1 levels : 1 + 1 that is empty
			// vPol.setValueForKey("Levels", Integer.toString(filterConfig.getMaxLevel() + 2));
			// vPol.setValueForKey("Level0", "23");
			// vPol.setValueForKey("Level1", "22");

			sLog.debug("removeKey ");
			for (String keyR : filterConfig.getRemoveKey()) {
				sLog.info("removeKey {}", keyR);
				vPol.removeKey(keyR);
			}
			// vPol.removeKey("Level2");
			// vPol.removeKey("Level3");
			// vPol.removeKey("Level4");
			// faster compilation
			vPol.setOrAddValueForKey("Preprocess", "N");

			// compresion
			vPol.setOrAddValueForKey("TreSize", "1500");
			vPol.setOrAddValueForKey("RgnLimit", "400");

			vPol.setOrAddValueForKey("POIIndex", "N");

		} else {
			sLog.error("not the requested family entity !!! " + vPol.getFamily());
		}
		fpCible.writeEntity(vPol);

		while ((vPol = fp.readEntity()) != null) {
			nbEntitySource++;
			if (!isFiltered(vPol)) {
				// not filtered so copy it
				fpCible.writeEntity(vPol);
				nbEntityCible++;
			}
		}

		fp.closeRead();
		fpCible.closeWrite();
		sLog.info("" + nbEntityCible + " entities copied on " + nbEntitySource + "  remaining "
				+ (nbEntityCible * 100 / nbEntitySource) + "%");
	}

	/**
	 * @param pPol
	 * @return true to filter it or false to keep it
	 */
	private static boolean isFiltered(PolishEntity pPol) {
		String family = pPol.getFamily();
		String type = pPol.getValueForKey("Type");
		// if (pPol.getValueForKey("Data0") == null) {
		// // keep only level 0
		// return true;
		// }
		if (pPol.getLevel() > filterConfig.getMaxLevel()) {
			return true;
		}

		if ("POLYLINE".equals(family)) {
			if ("0x20".equals(type)) {
				return true;
			}
			// Interm. land contour (1/2) (0x21, polyline)
			if ("0x21".equals(type)) {
				return true;
			}
			// Major land contour (1/1) (0x22, polyline)
			if ("0x22".equals(type)) {
				return true;
			}
			// Marine hazard (no line) (0x2b, polyline) Catalu�a
			// if ("0x2b".equals(type)) {
			// return true;
			// }

			// if ("0x5".equals(type)) {
			// // change grosse route -> route étroite
			// pPol.setValueForKey("Type", "0x6");
			// return false;
			// }
			// sLog.info("keep "+ type);

		} else if ("POLYGON".equals(family)) {
			if ("0x13".equals(type) || "0x50".equals(type)) {
				return true;
			}
			// plantation, sport
			if ("0x4e".equals(type) || "0x17".equals(type)) {
				return true;
			}
			// Background=Y
			if ("0x4b".equals(type)) {
				return true;
			}

		} else if ("POI".equals(family)) {
			if ("0x6411".equals(type)) {
				String label = pPol.getValueForKey("Label");
				if ("PYLONE".equals(label)) {
					return true;
				}
			}
			// Label (0x2800, point) 1 lettre dans Cataluna
			// if ("0x2800".equals(type)) {
			// String label = pPol.getValueForKey("Label");
			// if (label.length() <= 1) {
			// return true;
			// }
			// }
		} else if ("ZipCodes".equals(family)) {
			// no index of zip codes
			return true;
		} else {
			sLog.info("unknown family " + family);
		}
		// by default, no filter
		return false;
	}

}
