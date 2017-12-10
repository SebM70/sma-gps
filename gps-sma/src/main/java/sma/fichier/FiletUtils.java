package sma.fichier;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FiletUtils {

	public static void copyStream(InputStream fis, OutputStream fos) throws IOException {
		byte[] buf = new byte[1024];
		int i = 0;
		while ((i = fis.read(buf)) != -1) {
			fos.write(buf, 0, i);
		}
	}

}
