
//actions
import java.awt.event.KeyEvent;
import java.awt.Robot;
import java.nio.file.Path;
import java.nio.file.Paths;
//scanner
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Scanner;
//data
import java.util.List;
import java.util.ArrayList;
import java.io.File;
//gui
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import java.awt.GridLayout;
//exceptions
import java.io.IOException;
import java.awt.AWTException;
import java.io.FileNotFoundException;


import java.io.FileWriter;
import java.io.BufferedWriter;

public class Util {
  public static void main(String[] args) throws IOException {
    
    SourceFiles();
  }

  static void SourceFiles(){
    File file = new File("test.est.R0.TD.txt");//change to .R
    Path workingDir = Paths.get("..");
    String wk = new String(workingDir.toAbsolutePath().toString());

    String[] arr = new String[0];
    String filename =  Paths.get(wk, "\\Data\\" + "zones_list").toString();;
    
    try {
      Scanner s = new Scanner(new File(filename));
      List<String> lines = new ArrayList<String>(); 

      while (s.hasNextLine()) { 
        lines.add(s.nextLine()); 
      }
      // add zone names to array 'arr' as Strings
      arr = lines.toArray(new String[0]);

    

    String filepath_est_R0 = Paths.get(wk, "\\tests\\" + "est.R0.TD.R").toString();  //quello del prof va cambiato!!
    for(int i=0; i<arr.length; i++) { 
      //String path = Paths.get(wk, "\\Data\\" + arr[i] + ".2020.R").toString();
      //runCommand("\"Loading\"", path);
      
      uncomment(filepath_est_R0, arr[i], file);
      //Source su est.R0.TD.R [test]  
      //uncomment(false, filepath, arr[i], file);
      //sleep(2000);
      //source();
    }
  } catch (IOException e) {
    e.printStackTrace();}
  }

  

  static void uncomment(String filepath_est_R0, String zone, File file) throws IOException {
    FileWriter fr = null;
    try {
      fr = new FileWriter(file, false); // if false, overwrites file, if true appends
      int count = 0;
      boolean flag = false;
      Scanner s = new Scanner(new File(filepath_est_R0));
      String data = // beginning of file
        "#Loading package\n"
      + "library(R0)\n"
      + "## Data is taken from the Department of Italian Civil Protection for key transmission parameters of an institutional\n"
      + "## outbreak during the 2020 SARS-Cov2 pandemic in Italy\n"
      + "\n";

      while (s.hasNextLine()) {        
        String nextLine = s.nextLine();
        if (nextLine.length() == 0) 
          continue; // skip blank lines

        if(nextLine.contains(zone.toUpperCase())){
          flag = true;
          while(count<3){
            nextLine = s.nextLine();
            nextLine = nextLine.replaceFirst("#", "");  // uncomment '#'
            data += (nextLine + "\n");
            count += 1;
          }
          count = 0;
        }
      } 
      // if the zone isn't found by the scanner, add a standard simulation for it
      if(!flag){
        String standardSim =
          "# STANDARD SIMULATION\n"
        +  "data(" + zone + ".2020)\n"
        + "mGT<-generation.time(\"gamma\", c(3, 1.5))\n"
        + "TD <- est.R0.TD(" + zone + ".2020, mGT, begin=1, end=93, nsim=1450)"
        + "\n";
        data += standardSim;
      }
      
      data += // end of file
          "\n"
        + "# Warning messages:\n"
        + "# 1: In est.R0.TD(Italy.2020, mGT) : Simulations may take several minutes.\n"
        + "# 2: In est.R0.TD(Italy.2020, mGT) : Using initial incidence as initial number of cases.\n"
        + "TD\n"
        + "# Reproduction number estimate using  Time-Dependent  method.\n"
        + "# 2.322239 2.272013 1.998474 1.843703 2.019297 1.867488 1.644993 1.553265 1.553317 1.601317 ...\n"
        + "## An interesting way to look at these results is to agregate initial data by longest time unit,\n"
        + "## such as weekly incidence. This gives a global overview of the epidemic.\n"
        + "TD.weekly <- smooth.Rt(TD, 4)\n"
        + "print(TD.weekly[[\"conf.int\"]])\n"
        + "print(TD.weekly[[\"R\"]])\n"
        + "# Reproduction number estimate using  Time-Dependant  method.\n"
        + "# 1.878424 1.580976 1.356918 1.131633 0.9615463 0.8118902 0.8045254 0.8395747 0.8542518 0.8258094..\n"
        + "plot(TD.weekly)\n"; 
        
      System.out.println(data);
      fr.write(data);
         
      } catch (IOException e) {
        e.printStackTrace();
    } finally {
      try {
        fr.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    /* handle thread notify
    try {
      Thread.sleep(1000);
    } catch (Exception e) {
      //TODO: handle exception
    } */
    
  }
}