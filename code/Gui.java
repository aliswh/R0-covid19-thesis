//actions
import java.awt.event.KeyEvent;
import java.awt.Robot;
import java.nio.file.Path;
import java.nio.file.Paths;
//scanner
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.io.FileWriter;
//data
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.nio.file.Files;
//gui
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import java.awt.GridLayout;
//exceptions
import java.io.IOException;
import java.awt.AWTException;
import javax.swing.UnsupportedLookAndFeelException;

public class Gui{
  public static void main(String[] args) {

    // interface
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); // Set system L&F
    } 
    catch (UnsupportedLookAndFeelException e) {e.printStackTrace();}
    catch (ClassNotFoundException e) {e.printStackTrace();}
    catch (InstantiationException e) {e.printStackTrace();}
    catch (IllegalAccessException e) {e.printStackTrace();}

    JFrame f = new JFrame("R0(t) for Covid-19");
    f.setSize(300, 300);
    f.setLocation(300,300);

    final JButton button1 = new JButton("Download data");
    button1.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        try{
        GetAllData();
        } catch(IOException err) {err.printStackTrace();}
        // notify when download is completed
        JOptionPane.showMessageDialog(f, "Data downloaded");
        }
    });

    final JButton button2 = new JButton("Open RStudio");
    button2.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        OpenRStudio();
      }
    });

    final JButton button3 = new JButton("Source files");
    button3.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        SourceFiles();
      }
    });

    final JButton button4 = new JButton("Save Graphs");
    button4.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        //TODO
      }
    });
    
    // add buttons to layout
    f.setLayout(new GridLayout(4,1)); // 4 rows 1 column
    f.add(button1);
    f.add(button2);
    f.add(button3);
    f.add(button4);
  
    f.setVisible(true);
  }

  static void GetAllData() throws IOException {
    Path workingDir = Paths.get(System.getProperty("user.dir"));
    String wk = workingDir.getParent().toString();

    // create 'plots' folder in Documents folder
    String docpath = Paths.get(wk, "/data/").toString();
    Path plotspath = Paths.get(docpath, "plots");
    if (Files.notExists(plotspath)) {
      Files.createDirectory(plotspath);  
    }

    String pyPath = Paths.get(wk, "/code/getalldata.py").toString();
    runCommand("\"get all data\"", pyPath);

    /* 
      wait for the .py script to end, so I can open 
      RStudio only when all files are made
              I scan the tasklist, looking for "py.exe" the process that handles my script
              While this process exists, keep scanning 
              only when "py.exe" doesn't exists break the while true loop
    */
    try {
      String line;            // input line to read processes
      String linecheck = "";  // string to append all processes names
      int count = 0;          // times the py.exe process is counted
                              // if it is 0, it means it wasn't open before so it should continue to scan
      while (true) {
        // get all processes names in tasklist.exe
        Process p = Runtime.getRuntime().exec(System.getenv("windir") +"/system32/"+"tasklist.exe");
        BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
        
        // add all tasklist.exe lines to a String to get all processes names
        while ((line = input.readLine()) != null) {
          linecheck = linecheck.concat(line);
          System.out.print("download data .  .  .  \r");
        }
        // scan the string
        if (linecheck.contains("py.exe") || count == 0) {
          count++;  
        } else { 
          break; 
        } // break the loop when "py.exe" doesn't exist
        linecheck = "";   // clear 
        }
    } catch (Exception err) {
      err.printStackTrace();
    }
  }

  static void SourceFiles(){
    /* boolean osWin = false;
    String operSys = System.getProperty("os.name").toLowerCase();
    if (operSys.contains("win"))
      osWin = true; */

    File temp_file = new File("temp.est.R0.TD.R");//change to .R

    Path workingDir = Paths.get(System.getProperty("user.dir"));
    String wk = workingDir.getParent().toString();
    
    //if(osWin==true) TODO
    String filename =  Paths.get(wk, "/data/" + "zones_list").toString();
    String function_path =  Paths.get(wk, "/R/" + "est.R0.TD.R").toString();
    String temp_file_path = Paths.get(wk, "/code/" + "temp.est.R0.TD.R").toString();
    
    String[] arr = new String[0];

    try {
      Scanner s = new Scanner(new File(filename));
      List<String> lines = new ArrayList<String>(); 

      while (s.hasNextLine()) { 
        lines.add(s.nextLine()); 
      }
      // add zone names to array 'arr' as Strings
      arr = lines.toArray(new String[0]);    

      runCommand("source on est.R0.TD.R", function_path);
      sleep(2000);
      source();

      String filepath_est_R0 = Paths.get(wk, "/tests/" + "est.R0.TD.R").toString();  //quello del prof va cambiato!!
      
      int daycount = Integer.parseInt(arr[0]); // gets first element of the file, which is the number of days

      for(int i=1; i<5; i++) { // starts at 'arr[1]' because 'arr[0]' is the number of days
        String path = Paths.get(wk, "/data/" + arr[i] + ".2020.R").toString();
        runCommand("\"Loading\"", path);  // 
        sleep(2000);
        source();
        uncomment(filepath_est_R0, arr[i], temp_file, daycount);   // create file with data about the right zone to source 'est.R0.TD.R' on
        runCommand("\"Plot\"", temp_file_path);
        sleep(2000);
        source();
      }
    } catch (IOException e) {
      e.printStackTrace();}
    
    // creates dashboard
    sleep(2000);
    String pyPath = Paths.get(wk, "/code/makeboard.py").toString();
    runCommand("\"dashboard\"", pyPath);
  }

  static void OpenRStudio(){
    // path to RStudio
    String path = "C:/Program Files/RStudio/bin/rstudio.exe";
    runCommand("\"Opening RStudio\"", path);
    sleep(7000);

    try { // closes all currently open files in RStudio
      Robot robot = new Robot();
    
      robot.keyPress(KeyEvent.VK_CONTROL);
      robot.keyPress(KeyEvent.VK_SHIFT);
      robot.keyPress(KeyEvent.VK_W);
    
      robot.keyRelease(KeyEvent.VK_W);
      robot.keyRelease(KeyEvent.VK_SHIFT);
      robot.keyRelease(KeyEvent.VK_CONTROL);
        
    } catch (AWTException er) {
      er.printStackTrace();
    }
  }

  static void uncomment(String filepath_est_R0, String zone, File file, int daycount) throws IOException {
    FileWriter fr = null;
    String regex = "end=\\d*,"; //'end=' followed by zero or more repetitions of any number [0-9] until char ','
    String endstring = "end=" + daycount + ",";
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
            if(count==2){ // replace the 'end' value that determines the end of the temporal window to be considered for plotting
              nextLine = nextLine.replaceAll(regex, endstring);
            }
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
        + "TD <- est.R0.TD(" + zone + ".2020, mGT, begin=1, end=" + daycount + ", nsim=1450)"
        + "\n";
        data += standardSim;
      }
      
      String operSys = System.getProperty("os.name").toLowerCase();
      
      // gets path to save graphs inside "data/plots"
      Path workingDir = Paths.get(System.getProperty("user.dir"));
      String wk = workingDir.getParent().toString();
      String dirpath = Paths.get(wk, "/data/plots/").toString();
      if (operSys.contains("win")) {
        dirpath =  "\"" + dirpath.replace("\\", "\\\\") + "\\\\";
      }
      else 
        dirpath = "\"" + dirpath + "/";

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
        + "png(file = "+ dirpath + zone + ".png\", width = 1000, height = 400)\n"   // exports graph as .png 
        + "plot(TD.weekly)\n"
        + "dev.off()\n"; 
        
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
  }

  static void source(){ 
    try {
      Robot robot = new Robot();

      robot.keyPress(KeyEvent.VK_CONTROL);
      robot.keyPress(KeyEvent.VK_SHIFT);
      robot.keyPress(KeyEvent.VK_S);

      robot.keyRelease(KeyEvent.VK_S);
      robot.keyRelease(KeyEvent.VK_SHIFT);
      robot.keyRelease(KeyEvent.VK_CONTROL);
  
    } catch (AWTException e) {
      e.printStackTrace();
    }
  }

  static void sleep(int t){ 
    try {
    Thread.sleep(t);
    } catch (InterruptedException e) {
      e.printStackTrace();
    } 
  }

  static void runCommand(String title, String path){ 
    String[] commands = {"cmd", "/c", "start", title, path};
    // title == title given to the window (irrelevant to code purposes)
    try {
      //  runs cmd
      Runtime.getRuntime().exec(commands);
    } catch (IOException er){
      er.printStackTrace();
    }
  }
}