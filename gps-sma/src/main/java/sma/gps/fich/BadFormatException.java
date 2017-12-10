/*
 * BadFormatException.java
 *
 * Created on 1 septembre 2001, 21:58
 */

package sma.gps.fich;

/**
 * 
 * @author MARSOLLE
 */
public class BadFormatException extends java.lang.Exception {

    /**
 * Creates new <code>BadFormatException</code> without detail message.
     */
    public BadFormatException() {
    }


    /**
 * Constructs an <code>BadFormatException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public BadFormatException(String msg) {
        super(msg);
    }
}


