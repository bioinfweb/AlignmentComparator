package info.bioinfweb.alignmentcomparator.externaltests.runtime;

import java.io.*;

public class Exec {
  public static void main(String args[]) {
    try {
      String line;
      //Process p = Runtime.getRuntime().exec("cmd /c dir");
      Process p = Runtime.getRuntime().exec("C:\\Users\\BenStoever\\Documents\\Studium\\Projekte\\Promotion\\AlignmentEvaluation\\Eclipse Arbeitsplatz\\AlignmentComparator\\bin\\cmd\\muscle3.8.31_i86win32.exe" , null, new File("C:\\Users\\BenStoever\\Documents\\Studium\\Projekte\\Promotion\\AlignmentEvaluation\\Eclipse Arbeitsplatz\\AlignmentComparator\\bin\\cmd"));
      //Process p = Runtime.getRuntime().exec("C:\\muscle3.8.31_i86win32.exe");
      BufferedReader bri = new BufferedReader
        (new InputStreamReader(p.getInputStream()));
      BufferedReader bre = new BufferedReader
        (new InputStreamReader(p.getErrorStream()));
      while ((line = bri.readLine()) != null) {
        System.out.println(line);
      }
      bri.close();
      while ((line = bre.readLine()) != null) {
        System.out.println(line);
      }
      bre.close();
      p.waitFor();
      System.out.println("Done.");
    }
    catch (Exception err) {
      err.printStackTrace();
    }
  }
}