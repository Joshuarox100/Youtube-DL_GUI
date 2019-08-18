import java.io.*;
import java.awt.*;
import java.net.*;

public class Documentation extends Thread
{
    // Opens a Webpage with the Documentation for youtube-dl
    public void run() {
        openWebpage();
    }

    public static void openWebpage() {
        try {
            ProcessBuilder cmd = new ProcessBuilder(new String[] {"cmd", "/C", "start https://github.com/ytdl-org/youtube-dl/blob/master/README.md#options"});
            cmd.redirectErrorStream(true);
            Process proc = cmd.start();
        } catch (Exception e) {
            e.printStackTrace();
        }   
    }
}
