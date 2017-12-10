/*
 * ResultDbl.java
 *
 * Created on 7 mars 2004, 18:37
 */

package sma.fichier;

import java.util.*;
import java.io.*;


/**
 *
 * @author  smarsoll
 * @version
 */
public class ResultDbl {
  List lstF = new LinkedList();
  
  /** Creates new ResultDbl */
  public ResultDbl() {
  }
  
  public void add(File f) {
    if (lstF.size() < 3)
      lstF.add(f);
  }
  
  public void report(PrintStream out) {
    if (lstF.size() > 1) {
      out.println(lstF.size());
      Iterator it = lstF.iterator();
      while(it.hasNext()) {
        File f = (File) it.next();
        out.println(f.getAbsolutePath());
        //out.println(f.length());
      }
    }
  }
  
}
