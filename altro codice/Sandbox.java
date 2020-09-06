import java.lang.ClassLoader;
import java.net.URL;
import java.nio.file.FileSystems;


public class Sandbox {
    
    public static void main(String[] args) throws Exception {

      
        // finds resource
        String url = FileSystems.getDefault().getPath(new String()).toAbsolutePath().toString();
        System.out.println("Value = " + url);
  
        // finds resource
        //url = cLoader.getSystemResource("newfolder/a.txt");
        System.out.println("Value = " + url);  
     }
}