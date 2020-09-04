import java.awt.event.KeyEvent;
import java.io.IOException;
import java.awt.AWTException;
import java.awt.Robot;
import java.nio.file.Path;
import java.nio.file.Paths;

class StartRStudio {
    public static void main(String args[])
        throws IOException
    {
        /*
        *  file .R da aprire
        *  (to add more: they need to be formatted with two backslashes '\\' instead of one '\')
        */
        //gets parent directory absolute patch
        Path workingDir = Paths.get("..");
        String wk = new String(workingDir.toAbsolutePath().toString());

        String pyPath = Paths.get(wk, "\\Code\\makeFile.py").toString();
        String percorsoEst = Paths.get(wk, "\\R\\est.R0.TD.R").toString();
        String percorsoItaly2020 = Paths.get(wk, "\\data\\Italy.2020.R").toString();
        
        /*
        *  command line to execute in cmd
        *  (one for each file)
        *   the title is needed if the path contains empty spaces
        */
        
        String[] commandsEst = {"cmd", "/c", "start", "\"commandsEst\"", percorsoEst};
        String[] commandsItaly = {"cmd", "/c", "start", "\"commandsItaly\"", percorsoItaly2020};
        String[] commandsPyPath = {"cmd", "/c", "start", "\"commandsPyPath\"", pyPath};

        /*
        *  command execution:
        *  first gets the daily data by the .py script, then opens RStudio
        */
        
        ExecCommands(commandsPyPath);
        ExecCommands(commandsItaly);
        ExecCommands(commandsEst);
    }

    static void ExecCommands(String[] c){
        Process p;
        try {
        //  runs cmd
        p = Runtime.getRuntime().exec(c);
        System.out.println("exec");

            try {
                //  sleeps to let RStudio open
                    Thread.sleep(5000);
                    System.out.println("sleep");
            } catch (InterruptedException e) {
                    e.printStackTrace();
                } 

        } catch (IOException e){
            e.printStackTrace();
            }

        

        /*
        *   presses the shortcut CTRL+SHIFT+S - Source 
        */
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
    
}