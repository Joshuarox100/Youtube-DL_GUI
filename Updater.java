import java.io.*;
import java.awt.*;
import javax.swing.*;
public class Updater extends Thread
{
    // Updates youtube-dl
    public void updateYTDL(){
        Main.menu.console.setText("");
        Main.menu.liveOut.setText("Updating...");
        String command = "data\\bin\\ytdl\\youtube-dl.exe -U";
        
        try
        { 
            // Launch command prompt and run command
            ProcessBuilder cmd = new ProcessBuilder(new String[] {"cmd", "/C", command});
            cmd.redirectErrorStream(true);
            Process proc = cmd.start();

            // Read the output and error log from command prompt
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream(), "UTF8"));

            // Show the output from the command prompt
            String s = null;
            while ((s = stdInput.readLine()) != null) {
                if (s.indexOf("Updated youtube-dl") != -1)
                {
                    proc.destroy();
                    Main.menu.console.setText(Main.menu.console.getText() + s.substring(s.indexOf("Updated youtube-dl")) + "\n");
                    Main.menu.liveOut.setText("Update Complete");
                    stdInput.close();
                }
                else
                    Main.menu.console.setText(Main.menu.console.getText() + s + "\n");
                if (s.indexOf("up-to-date") != -1) 
                {
                    Main.menu.liveOut.setText("No Update Available");
                    proc.destroy();
                    stdInput.close();
                }
            }
            stdInput.close();
        } 
        catch (Exception e) 
        {  
            Main.menu.settingsWindow.updateEnable();
            //Main.menu.console.append("Something Broke Here... \n");
        } 
    }
    
    public void run(){
        Main.menu.download.setEnabled(false);
        updateYTDL();
        Main.menu.download.setEnabled(true);
    }
}
