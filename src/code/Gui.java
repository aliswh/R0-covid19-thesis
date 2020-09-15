package code;


//actions
//scanner
//data
//gui
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import java.awt.GridLayout;
import java.io.FileNotFoundException;
//exceptions
import java.io.IOException;
import java.awt.AWTException;
import javax.swing.UnsupportedLookAndFeelException;

public class Gui {
public static void main(String[] args) {

  // interface
  try {
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); // Set system L&F
  } catch (UnsupportedLookAndFeelException e) {
    e.printStackTrace();
  } catch (ClassNotFoundException e) {
    e.printStackTrace();
  } catch (InstantiationException e) {
    e.printStackTrace();
  } catch (IllegalAccessException e) {
    e.printStackTrace();
  }

  JFrame f = new JFrame("R0(t) for Covid-19");
  f.setSize(300, 300);
  f.setLocation(50, 300);

  final JButton downloadButton = new JButton("Download data");
  downloadButton.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
      try {
        Op.GetAllData();
      } catch (IOException err) {
        err.printStackTrace();
      }
      // notify when download is completed
      JOptionPane.showMessageDialog(f, "Data downloaded");
    }
  });

  final JButton openRstudioButton = new JButton("Open RStudio");
  openRstudioButton.addActionListener(e -> {
    try {
      Op.OpenRStudio();
    } catch (AWTException | FileNotFoundException er) {
      er.printStackTrace();
    }
  });

  final JButton sourceFilesButton = new JButton("Source files");
  sourceFilesButton.addActionListener(e -> {
    try {
      Op.SourceFiles();
    } catch (IOException err) {
      err.printStackTrace();
    }
    // notify when download is completed
    JOptionPane.showMessageDialog(f, "Operation completed");
  });

  final JButton openDashboardButton = new JButton("Open dashboard.pdf");
  openDashboardButton.addActionListener(e -> Op.OpenPDF());

  final JButton openDashPanelButton = new JButton("Open dashboard panel");
  openDashPanelButton.addActionListener(e -> Op.ShowBox(f));

  // add buttons to layout
  f.setLayout(new GridLayout(5, 1)); // 5 rows 1 column
  f.add(downloadButton);
  f.add(openRstudioButton);
  f.add(sourceFilesButton);
  f.add(openDashboardButton);
  f.add(openDashPanelButton);

  f.setVisible(true);
}
}
