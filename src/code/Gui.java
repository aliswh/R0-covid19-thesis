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
//exceptions
import java.io.IOException;
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
  f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
  f.setSize(300, 300);
  f.setLocation(50, 300);

  final JButton button1 = new JButton("Download data");
  button1.addActionListener(new ActionListener() {
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

  final JButton button2 = new JButton("Source files");
  button2.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
      try {
        Op.SourceFiles();
      } catch (IOException err) {
        err.printStackTrace();
      }
    }
  });

  final JButton button3 = new JButton("Open dashboard.pdf");
  button3.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
      Op.OpenPDF();
    }
  });

  final JButton button4 = new JButton("Open dashboard panel");
  button4.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
      Op.ShowBox(f);
    }
  });

  // add buttons to layout
  f.setLayout(new GridLayout(4, 1)); // 4 rows 1 column
  f.add(button1);
  f.add(button2);
  f.add(button3);
  f.add(button4);

  f.setVisible(true);
}
}
