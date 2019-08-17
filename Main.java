import java.io.*;
import java.awt.*;

public class Main
{
    public static GUI menu;
    public static File config;
    public static Point location = new Point(0, 0);
    public static String theme = "Light";
    
    public static void main(String[] args) throws IOException
    {
        new File(System.getProperty("user.home") + File.separator + "AppData" + File.separator + "Roaming" + File.separator + "Youtube-DL GUI").mkdir();
        config = new File(System.getProperty("user.home") + File.separator + "AppData" + File.separator + "Roaming" + File.separator + "Youtube-DL GUI" + File.separator + "config.txt");
        
        if (config.createNewFile())
        {
            //System.out.println("File Created");
            String configNew = "version=2\n"
                + "output=" + System.getProperty("user.home") + File.separator + "Downloads" + File.separator + "\n"
                + "ignore=true\n"
                + "skip=true\n"
                + "overwrite=true\n"
                + "sounds=true\n"
                + "theme=Light\n"
                + "args=\n";

            BufferedWriter writer = new BufferedWriter(new FileWriter(config));
            writer.write(configNew);

            writer.close();
        } 
        else
        {               
            //System.out.println("File Already Exists");
            String[] settings;
            try {
                settings = Settings.loadSettings();
            }
            catch (Exception e) {
                settings = new String[8];
            }
            if (settings[7] == null || !settings[7].equals("2")) {
                String configNew = "version=2\n"
                    + "output=" + System.getProperty("user.home") + File.separator + "Downloads" + File.separator + "\n"
                    + "ignore=true\n"
                    + "skip=true\n"
                    + "overwrite=true\n"
                    + "sounds=true\n"
                    + "theme=Light\n"
                    + "args=\n";

                BufferedWriter writer = new BufferedWriter(new FileWriter(config));
                writer.write(configNew);

                writer.close();
            }                
        }

        menu = new GUI();
    }
}
