package sma.gmap.download;

import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SuckURL {
	String aFile;
	String aURL;

	/** Log4J */
	private static final Logger sLog = LoggerFactory.getLogger(SuckURL.class);

	/** init */
	public SuckURL(String url, String fileName) {
		aURL = url;
		aFile = fileName;
	}

	/** download file at url 
	 * @throws Exception */
	public void doit() throws Exception {
		DataInputStream di = null;
		FileOutputStream fo = null;
		byte[] b = new byte[1];

		try {
			sLog.debug("Sucking " + aFile + "   at " + aURL);
			// input
			URL url = new URL(aURL);
			URLConnection urlConnection = url.openConnection();

			// need to add element not to be black-listed by googlemap
//			request.UserAgent = "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.8.1.14) Gecko/20080404 Firefox/2.0.0.14"; // or another agent
//			request.Referer = "http://maps.google.com/maps";
			// User-Agent: Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 2.0.50727)
			urlConnection.setRequestProperty
			  ( "User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 2.0.50727)" );
			// Referer: http://maps.google.fr/maps?ie=UTF8&t=h&ll=41.787697,19.973145&spn=2.09703,4.790039&z=8
			urlConnection.setRequestProperty( "Referer", "http://maps.google.com/maps" );

			// provisoire en dure
			//urlConnection.setRequestProperty("Cookie", "PREF=ID=dfd0b9af36a3dca3:TM=1215172358:LM=1215172358:S=Qsjhr91AnZMJEusG; NID=12=o31dWF5nLTT4vhcMXeHgZtlCyTd00FW9K8JIhIZt6I2SWwJ2koyJ731mFeOXQjF_7oNz9jbznpJ_W7eBjjGpwiZMyVaNUV7eTKSYLd4XSAD2LxWXf_biBKC8p9fk3Cpt");
			
			urlConnection.connect();
			sLog.debug("urlConnection.getContentType()=" + urlConnection.getContentType());
			
			//this.debugUrlHeader(urlConnection);
			
			
			di = new DataInputStream(urlConnection.getInputStream());

			// output
			fo = new FileOutputStream(aFile);

			// copy the actual file
			// (it would better to use a buffer bigger than this)
			// while(-1 != di.read(b,0,1))
			// fo.write(b,0,1);

			byte[] buf = new byte[1024];
			int i = 0;
			while ((i = di.read(buf)) != -1) {
				fo.write(buf, 0, i);
			}

			di.close();
			fo.close();
			
		} catch (java.io.FileNotFoundException ex) {
			sLog.warn("can not get (404, FileNotFoundException) " + aURL);
		} catch (java.net.UnknownHostException ex) {
			sLog.warn("can not get (UnknownHostException) " + aURL);
			
		} catch (Exception ex) {
			sLog.error("can not get " + aURL, ex);
			throw new Exception("can not get " + aURL, ex);
		}
	}

	private void debugUrlHeader(URLConnection urlConn) {
		// TODO Auto-generated method stub
		int i = 1;
		String hdrKey;
		while ((hdrKey = urlConn.getHeaderFieldKey(i)) != null) {
			sLog.info(hdrKey + "   " + urlConn.getHeaderField(i));
			i++;
		}
	}
	
	
}