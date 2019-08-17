import java.io.*;
import java.awt.*;
import javax.swing.*;
import java.util.stream.*;
import java.util.*;
public class Downloader extends Thread
{
    private String format;
    private ArrayList<QueueEntry> queue;
    private JTextPane console;
    private boolean invalid;
    public boolean stop;
    public Process proc;

    public Downloader(String format, ArrayList<QueueEntry> queue, JTextPane console){
        this.format = format;
        this.queue = queue;
        this.console = console;
        invalid = false;
        stop = false;
    }

    // Generates and runs a command to download a video using provided format and URL
    public void downloadVideo(String format, String URL, int start, int end, JTextPane console)
    {   
        // Beginning of command
        String command = "data\\bin\\ytdl\\youtube-dl.exe ";

        // Audio Formats
        if (format.equals("mp3"))
            command = command + "-x --audio-format mp3 ";
        else if (format.equals("aac"))
            command = command + "-x --audio-format aac ";
        else if (format.equals("flac"))
            command = command + "-x --audio-format flac ";
        else if (format.equals("m4a"))
            command = command + "-x --audio-format m4a "; 
        else if (format.equals("opus"))
            command = command + "-x --audio-format opus ";
        else if (format.equals("vorbis"))
            command = command + "-x --audio-format vorbis ";
        else if (format.equals("wav"))
            command = command + "-x --audio-format wav ";

        // Video Formats
        else if (format.equals("mp4"))
            command = command + "--recode-video mp4 ";
        else if (format.equals("flv"))
            command = command + "--recode-video flv ";
        else if (format.equals("ogg"))
            command = command + "--recode-video ogg ";
        else if (format.equals("webm"))
            command = command + "--recode-video webm ";
        else if (format.equals("mkv"))
            command = command + "--recode-video mkv ";
        else if (format.equals("avi"))
            command = command + "--recode-video avi ";   

        try
        {
            String[] config = Settings.loadSettings();
            if (!config[0].equals(""))
                command = command + "-o \"" + config[0] + "%(title)s.%(ext)s\" ";
            if (Boolean.parseBoolean(config[1]))
                command = command + "--no-playlist ";
            if (Boolean.parseBoolean(config[2]))
                command = command + "-i ";
            if (!Boolean.parseBoolean(config[3]))
                command = command + "-w ";
            if (start > 0)
                command = command + "--playlist-start " + start + " ";
            if (end > 0)
                command = command + "--playlist-end " + end + " ";
        }
        catch (Exception e)
        {
            console.setText(console.getText() + "Failed to Load Configuration File, Proceeding with Default Settings... \n");
        }

        command = command + "--ignore-config --ffmpeg-location \"data\\bin\\ffmpeg\\bin\\ffmpeg.exe\" ";

        // Making sure there is a URL entered
        if (URL.length() != 0 && URL.indexOf(" ") == -1)
            command = command + "\"" + URL + "\"";
        else
        {
            console.setText(console.getText() + "Invalid URL. Skiping... \n");
            Main.menu.liveOut.setText("Invalid URL. Skipping...");
            return;
        }

        //System.out.println(command);

        // Attempt to run the command
        try
        { 
            // Launch command prompt and run command
            Main.menu.liveOut.setText("Downloading...");
            ProcessBuilder cmd = new ProcessBuilder(new String[] {"cmd", "/C", command + " && exit"});
            cmd.redirectErrorStream(true);
            proc = cmd.start();
            // Read the output and error log from command prompt
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream(), "UTF8"));

            // Show the output from the command prompt
            String s = null;
            while ((s = stdInput.readLine()) != null) {
                console.setText(console.getText() + s + "\n");
                if (s.indexOf("Merging formats") != -1)
                    Main.menu.liveOut.setText("Converting...");
                else if (s.indexOf("[download]") != -1)
                    Main.menu.liveOut.setText("Downloading...");
            }
            stdInput.close();
        } 
        catch (Exception e) 
        {  
            //console.append("Something Broke Here... \n");
        } 
    }

    public void run(){
        while (queue.size() > 0 && !stop) {
            downloadVideo(format, queue.get(0).url, queue.get(0).start, queue.get(0).end, console);
            queue.remove(0);
        }
        try {
            Main.menu.downloadComplete();
            Main.menu.downloads.remove(0);
            console.setText(console.getText() + "Download Complete \n");
            Main.menu.liveOut.setText("Download Complete");
            String[] config = Settings.loadSettings();
            if (Boolean.parseBoolean(config[4]))
                new SoundPlayer().start();            
        }
        catch (Exception e) { }
        new DisplayUpdater().start();
    }
}
