package code;

//actions
import java.awt.event.KeyEvent;
import java.awt.Robot;
import java.nio.file.Path;
import java.nio.file.FileSystems;
//scanner
//data
//gui
//exceptions
import java.io.IOException;
import java.awt.AWTException;

public class Util {
// return the directory where the code is launched
public static String getWD() {
  Path workingDir = FileSystems.getDefault().getPath(new String()).toAbsolutePath();
  String wk = workingDir.toString();
  return wk;
}

// Thread.sleep but with exception caught
public static void sleep(int t) {
  try {
    Thread.sleep(t);
  } catch (InterruptedException e) {
    e.printStackTrace();
  }
}

// executes command in cmd
public static void runCommand(String title, String path) {
  String[] commands = { "cmd", "/c", "start", title, path };
  // title == title given to the window (irrelevant to code purposes)
  try {
    // runs cmd
    Runtime.getRuntime().exec(commands);
  } catch (IOException er) {
    er.printStackTrace();
  }
}

// presses shortcut "CTRL+SHIFT+S" that auto-sources files in RStudio
public static void source() {
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

