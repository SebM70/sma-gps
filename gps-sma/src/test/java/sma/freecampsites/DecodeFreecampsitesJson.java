package sma.freecampsites;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DecodeFreecampsitesJson {

	private static final char DELIM = ';';
	private static final String DATA_DIR = "C:/SMa/workspace2/gps-trunk/gps-sma/src/test/resources/freecampsites/";
	private static final Logger LOGGER = LoggerFactory.getLogger(DecodeFreecampsitesJson.class);

	// @Test
	// public void decodeUtah() throws IOException, ParseException {
	// decode("Utah.json");
	// LOGGER.info("end");
	// }

	@Test
	public void decodeAll() throws IOException, ParseException {
		File dirIn = new File(DATA_DIR + "in");
		for (File aFile : dirIn.listFiles()) {
			decode(aFile.getName());
		}

		LOGGER.info("end");
	}

	private void decode(String sourFile) throws IOException, ParseException {
		String filePath = DATA_DIR + "in/" + sourFile;
		LOGGER.info(sourFile);

		FileReader reader = new FileReader(filePath);

		JSONParser jsonParser = new JSONParser();
		JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
		reader.close();
		
		// get an array from the JSON object
		List<Map<String, String>> resultList = (JSONArray) jsonObject.get("resultList");
		LOGGER.info("resultList.size()={}", resultList.size());

		FileWriter fw = new FileWriter(DATA_DIR + "out/" + sourFile + ".txt");

		for (Map<String, String> jsObj : resultList) {
			String isFree = jsObj.get("campsite_fee");
			LOGGER.debug("{} / {}", jsObj.get("name"), isFree);
			int symb = 18;
			if ("Free".equals(isFree)) {
				fw.append("Biv ");
				symb = 151;
			} else if ("Pay".equals(isFree)) {
				fw.append("Pay ");
				symb = 8215;
			} else if (isFree.startsWith("Pass")) {
				fw.append("Permit ");
				symb = 8215;
			} else {
				fw.append(isFree);
				fw.append(" ");
			}
			fw.append(limitString(jsObj.get("name")));
			fw.append(DELIM);
			fw.append(jsObj.get("latitude"));
			fw.append(DELIM);
			fw.append(jsObj.get("longitude"));
			fw.append(DELIM);
			fw.append(limitString(jsObj.get("excerpt")));
			fw.append(DELIM);
			fw.append(Integer.toString(symb));
			fw.append(DELIM);
			fw.append('\n');
		}

		fw.flush();
		fw.close();

	}

	private String limitString(String input) {
		String result = StringUtils.replace(input, "\r", " ");
		result = StringUtils.replace(result, "\n", " ");
		result = StringUtils.replace(result, "  ", " ");
		result = StringUtils.replace(result, "  ", " ");
		result = StringUtils.replace(result, "…", "...");
		result = StringUtils.replace(result, ";", ".");

		result = StringUtils.abbreviate(result.trim(), 200);
		return result;
	}

}
