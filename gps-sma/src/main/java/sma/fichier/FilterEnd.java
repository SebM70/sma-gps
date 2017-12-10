/*
 * Filter.java
 *
 * Created on 13 ao�t 2002, 15:48
 */

package sma.fichier;

/**
 *
 * @author  smarsoll
 */
import java.io.FilenameFilter;

public class FilterEnd implements FilenameFilter {
  
  String pattern;

  /** Creates new Filter */
  public FilterEnd(String str) {
		pattern = str.toUpperCase();
  }
  
  public boolean accept(java.io.File file, String name) {
    return name.toUpperCase().endsWith(pattern);
  }  
 
}
