package africa;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sma.ConvertTtqvToPolish;


public class DecodeAfricaRace {

	private static final Logger LOGGER = LoggerFactory.getLogger(ConvertTtqvToPolish.class);

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		// String baseName = "src/test/resources/e10/SCHLESSER10";
		String baseName = "src/test/resources/2014/e7/trk0233";
		String content = getFile(baseName + ".txt");
		LOGGER.info("content" + content);

		List<String> csvContent = new ArrayList<String>();

		StringTokenizer st = new StringTokenizer(content);
		while (st.hasMoreTokens()) {
			// System.out.println(st.nextToken());
			String token = st.nextToken();
			csvContent.add(token + ";" + st.nextToken());
			// LOGGER.info(token);
		}

		LOGGER.info("csvContent=" + csvContent);
		write(baseName + ".csv", csvContent);

	}

	private static String getFile(String pString) {
		InputStream is = null;
		BufferedReader br = null;
		String line;
		// ArrayList<String> list = new ArrayList<String>();

		StringBuilder sb = new StringBuilder();

		try {
			is = new FileInputStream(new File(pString));
			br = new BufferedReader(new InputStreamReader(is));
			while (null != (line = br.readLine())) {
				// list.add(line);
				sb.append(" ");
				sb.append(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
				if (is != null)
					is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	public static void write(String file, List<String> csvContent) throws IOException {
//			FileWriter aWriter = new FileWriter(file);
			
			 FileOutputStream fos = new FileOutputStream(file);
		      Writer w = 
		        new BufferedWriter(new OutputStreamWriter(fos, "Cp850"));
			
			for (String string : csvContent) {
			w.append(string);
			w.append("\n");
			}
			
		// System.out.println(currentTime + " " + msg);
		w.flush();
		w.close();

	}

}
