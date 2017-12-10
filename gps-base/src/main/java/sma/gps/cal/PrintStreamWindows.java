package sma.gps.cal;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Specialisation that write CR followed by LF.
 * 
 * @author marsolle
 * 
 */
public class PrintStreamWindows extends PrintStream {

	public PrintStreamWindows(OutputStream pOut) {
		super(pOut);
	}

	@Override
	public void println() {
		print("\r\n");
	}

	@Override
	public void println(String pString) {
		print(pString);
		print("\r\n");
	}

}
