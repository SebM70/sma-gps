//import java.awt.*;
import java.io.*;
import java.net.URL;

/*
 * Created on 18 juin 2004
 * 
 * To change the template for this generated file go to Window - Preferences -
 * Java - Code Generation - Code and Comments
 */

/**
 * @author smarsoll
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class TestGetImage {
	static PrintStream out = System.out;

	public static void main(String[] args) {
		out.println("begin");
		try {

			URL imgURL = new
			 URL("http://geodesie.ign.fr/scripts/gcis.exe?XgoPageName=bdgdif&XgoAnswer=MapImage&sizex=500&sizey=500&XgoBitmapFormat=PNG&XgoNbBits=8&XgoPointX=529500&XgoPointY=1857500&XgoScale=0.00003");
			//URL imgURL = new URL("http://www.google.fr/images/logo_sm.gif");

/*			Toolkit tk = Toolkit.getDefaultToolkit();
			Image img = null;
			img = tk.getImage(imgURL);
			out.println("with=" + img.getWidth(null));
			//ImageIO.write(img, "png", new File("D:\\exemple.png"));
		Object result = imgURL.getContent();
			out.println(result);
*/			//sun.awt.image.URLImageSource 
			//InputStream
			out.println("nb Ko=" + copyUrlToFile(imgURL, new File("D:\\exemple.png")));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		out.println("end");

	}

	public static int copyUrlToFile(URL in, File out) throws Exception {
		InputStream fis = in.openConnection().getInputStream();
		OutputStream fos = new FileOutputStream(out);
		byte[] buf = new byte[1024];
		int i = 0;
		int size = 0;
		while ((i = fis.read(buf)) != -1) {
			fos.write(buf, 0, i);
			size++;
		}
		fis.close();
		fos.close();
		return size;
	}

}
