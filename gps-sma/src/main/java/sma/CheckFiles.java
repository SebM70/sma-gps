/*
 * CheckFiles.java
 *
 * Created on 7 mars 2004, 15:56
 */

package sma;

import java.util.*;
import java.io.*;
import sma.fichier.ResultDbl;

/**
 *
 * @author  smarsoll
 * @version
 */
public class CheckFiles {
  
  static PrintStream out = System.out;
  static Map mapF = new HashMap(200);
  static int nbf = 0;
  
  /** Creates new CheckFiles */
  public CheckFiles() {
  }
  
  /**
   * @param args the command line arguments
   */
  public static void main(String args[]) {
    File base = new File(args[0]);
    analyse(base);
    report();
  }
  
  static void analyse(File dir) {
    File[] lf = dir.listFiles();
    for (int i = 0; i<lf.length; i++) {
      if (lf[i].isDirectory()) analyse(lf[i]);
      else addInList(lf[i]);
    }
    if (lf.length == 0) out.println("empty dir:" + dir.getAbsolutePath());
  }
  
  static void addInList(File f) {
    //out.println(f.getName());
    nbf++;
    if ((nbf % 1000) == 0) out.print(".");
    if (f.length() > 5000) {
      
      String cle;
      cle = f.getName() +"|"+ f.length();
      
      //int[] tint = (int[]) mapF.get(cle);
      ResultDbl res = (ResultDbl) mapF.get(cle);
      if (res == null) {
        res = new ResultDbl();
        res.add(f);
        //tint[0] = 1;
        mapF.put(cle, res);
      } else {
        //tint[0]++;
        res.add(f);
        res.report(out);
      }
    }
  }
  
  static void report() {
    out.println(nbf + " files analysed");
    Iterator it = mapF.keySet().iterator();
    while (it.hasNext()) {
      String cle = (String) it.next();
      ResultDbl res = (ResultDbl) mapF.get(cle);
      //if (tint[0] > 1) out.println(cle + "   " + tint[0]);
      //res.report(out);
    }
    
  }
  
}
