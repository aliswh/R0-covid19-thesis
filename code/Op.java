
//actions
import java.awt.event.KeyEvent;
import java.awt.Robot;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.FileSystems;
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
import javax.swing.JFrame;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ImageIcon;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.Panel;
import java.awt.Dimension;
import java.awt.BorderLayout;
//exceptions
import java.io.IOException;
import java.awt.AWTException;

public class Op {
    public static void GetAllData() throws IOException {
        String wk = Util.getWD();
        // create 'plots' folder in 'data' folder
        String docpath = Paths.get(wk, "/data/").toString();
        Path plotspath = Paths.get(docpath, "plots");
        if (Files.notExists(plotspath)) {
            Files.createDirectory(plotspath);
        }

        String pyPath = Paths.get(wk, "/code/getalldata.py").toString();
        Util.runCommand("\"get all data\"", pyPath);

        /*
         * wait for the .py script to end, so I can open RStudio only when all files are
         * made I scan the tasklist, looking for "py.exe" the process that handles my
         * script While this process exists, keep scanning only when "py.exe" doesn't
         * exists break the while true loop
         */

        String line; // input line to read processes
        String linecheck = ""; // string to append all processes names
        int count = 0; // times the py.exe process is counted
                       // if it is 0, it means it wasn't open before so it should continue to scan
        while (true) {
            // get all processes names in tasklist.exe
            Process p = Runtime.getRuntime().exec(System.getenv("windir") + "/system32/" + "tasklist.exe");
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
            linecheck = ""; // clear
        }

    }

    public static void OpenRStudio() throws AWTException {
        // path to RStudio
        String path = "C:/Program Files/RStudio/bin/rstudio.exe";
        Util.runCommand("\"Opening RStudio\"", path);
        Util.sleep(9000);   // TODO define this value

        // closes all currently open files in RStudio
        Robot robot = new Robot();

        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_SHIFT);
        robot.keyPress(KeyEvent.VK_W);

        robot.keyRelease(KeyEvent.VK_W);
        robot.keyRelease(KeyEvent.VK_SHIFT);
        robot.keyRelease(KeyEvent.VK_CONTROL);

    }

    public static void SourceFiles() throws IOException {
        File temp_file = new File("temp.est.R0.TD.R");// change to .R

        String wk = Util.getWD();

        String filename = Paths.get(wk, "/data/" + "zones_list").toString();
        String function_path = Paths.get(wk, "/R/" + "est.R0.TD.R").toString();
        String temp_file_path = Paths.get(wk, "/code/" + "temp.est.R0.TD.R").toString();

        String[] arr = new String[0];

        Scanner s = new Scanner(new File(filename));
        List<String> lines = new ArrayList<String>();

        while (s.hasNextLine()) {
            lines.add(s.nextLine());
        }
        // add zone names to array 'arr' as Strings
        arr = lines.toArray(new String[0]);

        Util.runCommand("source on est.R0.TD.R", function_path);
        Util.sleep(2000);
        Util.source();

        String filepath_est_R0 = Paths.get(wk, "/tests/" + "est.R0.TD.R").toString(); // quello del prof va
                                                                                      // cambiato!!

        int daycount = Integer.parseInt(arr[0]); // gets first element of the file, which is the number of days

        for (int i = 1; i < arr.length; i++) { // starts at 'arr[1]' because 'arr[0]' is the number of days
            String path = Paths.get(wk, "/data/" + arr[i] + ".2020.R").toString();
            Util.runCommand("\"Loading\"", path); //
            Util.sleep(2000);
            Util.source();
            uncomment(filepath_est_R0, arr[i], temp_file, daycount); // create file with data about the right zone
                                                                     // to source 'est.R0.TD.R' on
            Util.runCommand("\"Plot\"", temp_file_path);
            Util.sleep(2000);
            Util.source();
        }

        // creates dashboard
        Util.sleep(2000);
        String pyPath = Paths.get(wk, "/code/makeboard.py").toString();
        Util.runCommand("\"dashboard\"", pyPath);
    }

    public static void uncomment(String filepath_est_R0, String zone, File file, int daycount) throws IOException {
        FileWriter fr = null;
        String regex = "end=\\d*,"; // 'end=' followed by zero or more repetitions of any number [0-9] until char
                                    // ','
        String endstring = "end=" + daycount + ",";
        try {
            fr = new FileWriter(file, false); // if false, overwrites file, if true appends
            int count = 0;
            boolean flag = false;
            Scanner s = new Scanner(new File(filepath_est_R0));
            String data = // beginning of file
                    "#Loading package\n" + "library(R0)\n"
                            + "## Data is taken from the Department of Italian Civil Protection for key transmission parameters of an institutional\n"
                            + "## outbreak during the 2020 SARS-Cov2 pandemic in Italy\n" + "\n";

            while (s.hasNextLine()) {
                String nextLine = s.nextLine();
                if (nextLine.length() == 0)
                    continue; // skip blank lines

                if (nextLine.contains(zone.toUpperCase())) {
                    flag = true;
                    while (count < 3) {
                        nextLine = s.nextLine();
                        nextLine = nextLine.replaceFirst("#", ""); // uncomment '#'
                        if (count == 2) { // replace the 'end' value that determines the end of the temporal window to
                                          // be considered for plotting
                            nextLine = nextLine.replaceAll(regex, endstring);
                        }
                        data += (nextLine + "\n");
                        count += 1;
                    }
                    count = 0;
                }
            }
            // if the zone isn't found by the scanner, add a standard simulation for it
            if (!flag) {
                String standardSim = "# STANDARD SIMULATION\n" + "data(" + zone + ".2020)\n"
                        + "mGT<-generation.time(\"gamma\", c(3, 1.5))\n" + "TD <- est.R0.TD(" + zone
                        + ".2020, mGT, begin=1, end=" + daycount + ", nsim=1450)" + "\n";
                data += standardSim;
            }

            String operSys = System.getProperty("os.name").toLowerCase();

            // gets path to save graphs inside "data/plots"
            String wk = Util.getWD();
            String dirpath = Paths.get(wk, "/data/plots/").toString();
            if (operSys.contains("win")) {
                dirpath = "\"" + dirpath.replace("\\", "\\\\") + "\\\\";
            } else
                dirpath = "\"" + dirpath + "/";

            data += // end of file
                    "\n" + "# Warning messages:\n"
                            + "# 1: In est.R0.TD(Italy.2020, mGT) : Simulations may take several minutes.\n"
                            + "# 2: In est.R0.TD(Italy.2020, mGT) : Using initial incidence as initial number of cases.\n"
                            + "TD\n" + "# Reproduction number estimate using  Time-Dependent  method.\n"
                            + "# 2.322239 2.272013 1.998474 1.843703 2.019297 1.867488 1.644993 1.553265 1.553317 1.601317 ...\n"
                            + "## An interesting way to look at these results is to agregate initial data by longest time unit,\n"
                            + "## such as weekly incidence. This gives a global overview of the epidemic.\n"
                            + "TD.weekly <- smooth.Rt(TD, 4)\n" + "print(TD.weekly[[\"conf.int\"]])\n"
                            + "print(TD.weekly[[\"R\"]])\n"
                            + "# Reproduction number estimate using  Time-Dependant  method.\n"
                            + "# 1.878424 1.580976 1.356918 1.131633 0.9615463 0.8118902 0.8045254 0.8395747 0.8542518 0.8258094..\n"
                            + "png(file = " + dirpath + zone + ".png\", width = 1000, height = 400)\n" // exports graph
                                                                                                       // as .png
                            + "plot(TD.weekly)\n" + "dev.off()\n";

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

    // opens pdf file created after sourcing
    public static void OpenPDF() {
        Path workingDir = FileSystems.getDefault().getPath(new String()).toAbsolutePath();
        String wk = workingDir.getParent().toString();
        String filename = Paths.get(wk, "/data/" + "_dashboard.pdf").toString();
        Util.runCommand("\"Open PDF\"", filename);
    }

    // opens dialog box, that shows graphs made after sourcing
    public static void ShowBox(JFrame f) {
        String wk = Util.getWD();
        File filedir = Paths.get(wk, "/data/plots").toFile();

        String[] filelist = filedir.list(); // list with all images names

        JDialog d = new JDialog(f, "");

        Panel panel = new Panel(); // where it shows images
        panel.setSize(new Dimension(1000, 400));

        JList<String> folderlist = new JList<String>(filelist);
        JScrollPane scrollPane = new JScrollPane(folderlist);
        folderlist.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent arg0) {
                panel.removeAll();
                String link = folderlist.getSelectedValue().toString();
                panel.add(new JLabel(new ImageIcon(Paths.get(wk, "/data/plots/" + link).toString())));
                d.validate();
                d.repaint();
            }
        });

        d.add(scrollPane, BorderLayout.LINE_START);
        d.add(panel, BorderLayout.LINE_END);

        d.setSize(1150, 400);
        d.setLocation(350, 300);
        d.setVisible(true);
    }
}
