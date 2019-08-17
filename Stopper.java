import java.io.*;
import java.util.stream.*;
import java.util.*;

public class Stopper extends Thread
{
    public void run(){
        String command = "taskkill /F /IM ";
        try
        { 
            for (int i = 0; i < Main.menu.downloads.size(); i++) {
                Main.menu.downloads.get(i).stop = true;
                Process curr = Main.menu.downloads.get(i).proc;
                Stream<ProcessHandle> temp = curr.descendants();
                ArrayList<ProcessHandle> kids = new ArrayList<ProcessHandle>();
                temp.forEach(s -> kids.add(s));
                for (ProcessHandle k : kids)
                {
                    if (k.info().command().get().indexOf("youtube-dl.exe") != -1 || k.info().command().get().indexOf("ffmpeg.exe") != -1)
                    {
                        ProcessBuilder cmd = new ProcessBuilder(new String[] {"cmd", "/C", command + String.valueOf(k.pid())});
                        cmd.redirectErrorStream(true);
                        Process proc = cmd.start();
                    }
                }
            }
        } 
        catch (Exception e) 
        {  
            //Main.menu.console.append("Something Broke Here... \n");
        } 
        while (Main.menu.downloads.size() > 0) {
            Main.menu.downloads.get(0).stop = true;
            Main.menu.downloads.get(0).proc.destroy();
            Main.menu.downloads.remove(0).interrupt();
        }
        try  
        {
            sleep(500); 
        }           
        catch (Exception e) {}
        Main.menu.console.setText(Main.menu.console.getText() + "Download Stopped \n");
        Main.menu.liveOut.setText("Download Stopped");
    }
}
