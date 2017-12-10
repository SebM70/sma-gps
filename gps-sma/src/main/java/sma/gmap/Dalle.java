package sma.gmap;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * of google map tiles
 * 
 * @author smarsoll
 * 
 */
public abstract class Dalle {
	/** Log4J */
	protected static final Logger sLog = LoggerFactory.getLogger(Dalle.class);
	
	public static final String GM_SAT = "S";
	public static final String GM_PLAN = "P";
	public static final String GM_RELIEF = "R";
	public static final String VE_SAT = "MS";
	public static final String VE_PLAN = "MP";

	/**
	 * zoom level
	 */
	int zoom;
	/** coordinates */
	int x, y;

	/** where to store dalle */
	static String SAVE_PATH = ":not init!";

	public Dalle(int zoom, int x, int y) {
		super();
		this.zoom = zoom;
		this.x = x;
		this.y = y;
	}
	
	public Dalle() {
		super();
		this.zoom = 0;
		this.x = -1;
		this.y = -1;
	}

	/**
	 * get temporary file name
	 * 
	 * @return complete path
	 */
	public abstract String getFileName();

	/**
	 * http url on google
	 * 
	 * @return
	 */
	public abstract String getUrlAdress();

	public static void setSAVE_PATH(String save_path) {
		SAVE_PATH = save_path;
	}

	/**
	 * check if local file already exists
	 * @return
	 */
	public boolean fileExist() {
		// check if file already exists
		File f = new File(this.getFileName());
		// sLog.info(f.getAbsolutePath());
		return f.exists();
	}
	
	/**
	 * return a key for HashMap. 
	 * @return
	 */
	public abstract String getKeyDalle() ;

	/**
	 * return a key for HashMap that correspond to URL
	 * 
	 * @param url
	 * @return key like: T:9:143:93 or S:trtqtssqt
	 */
	public static String getGoogleKeyDalleFromUrl(String url) {
		// S: sattelite:		http://khm3.google.fr/kh?n=404&v=29&hl=fr&t=trtqtsstt
		//							   khm2.google.fr/kh?v=32&hl=fr&x=1098&y=866&z=11&s=
		// 						http://khm3.google.fr/kh/v=37&hl=fr&x=4307&y=3522&z=13&s=Gal
		// T: transparente: 	http://mt0.google.com/mt?n=404&v=w2t.75&hl=fr&x=142&y=93&zoom=9&s=Galileo
		//						http://mt1.google.com/mt/v=w2t.92&hl=fr&x=4307&y=3525&z=13&s=Galile
		// R: relief:			http://mt1.google.com/mt?n=404&v=w2p.71&hl=fr&x=143&y=93&zoom=9&s=Ga
		// P: plan: 			http://mt0.google.com/mt?n=404&v=w2.75&hl=fr&x=140&y=96&zoom=9&s=Gali
		if (url.contains("/kh/")) {
			// dalle satellite
			int indexT = url.lastIndexOf("&t=");
			
			if (indexT > 0) {
				String result = url.substring(indexT + 3);
				if (result.contains("&")) {
					sLog.error("satellite key not parsed from " + url + " !");
				}
				return "S:" + result;
			} else {
				// satellite de type khm2.google.fr/kh?v=32&hl=fr&x=1098&y=866&z=11&s=

				String query = url;
				//sLog.info(query);
				String[] params = query.split("&");
			    Map<String, String> mapParam = new HashMap<String, String>();
			    for (String param : params) {
			        String[] nameValue = param.split("=");
			        if (nameValue.length == 2) {
			        	mapParam.put(nameValue[0], nameValue[1]);
			        } else {
			        	mapParam.put(nameValue[0], "");
			        }
			        
			    }
			    // zoom invers�
			    int zoom = Integer.parseInt(mapParam.get("z"));
			    zoom = 17 - zoom;
			    return "S:" + zoom + ":" + mapParam.get("x") + ":" + mapParam.get("y");
			
			}
			
		} else  {
			int indexMt = url.indexOf("/mt/");
			// dalle transparente ou relief
			if (indexMt >= 0) {
				String query = url.substring(indexMt + 4);
				//sLog.info(query);
				String[] params = query.split("&");
			    Map<String, String> mapParam = new HashMap<String, String>();
			    for (String param : params) {
			        String[] nameValue = param.split("=");
			        if (nameValue.length == 2) {
			        	mapParam.put(nameValue[0], nameValue[1]);
			        } else {
			        	mapParam.put(nameValue[0], "");
			        }
			        
			    }
			    String vValue = mapParam.get("v");
			    if (vValue != null) {
			    	// default is Plan
			    	String prefix = "P:";
			    	if (vValue.contains("t.")) {
			    		// dalle transparente
			    		prefix = "T:";
					} else if (vValue.contains("p.")) {
						// relief
						prefix = "R:";
					} 
			    	
				    return prefix + mapParam.get("zoom") + ":" + mapParam.get("x") + ":" + mapParam.get("y");
			    }
			    
			} else if(url.contains("/maps/vp?")) {
				return null;
			} else if(url.contains("/maps?")) {
				return null;
			}
		
			
		}
		sLog.debug("can not identify URL: " + url);
		return null;
	}

	
}
