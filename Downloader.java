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
        StringBuilder builder = new StringBuilder();
        builder.append("data\\bin\\ytdl\\youtube-dl.exe ");
        // Learning Switch
        switch(format) {
            // Audio Formats
            case "mp3":
                builder.append("-x --audio-format mp3 ");
                break;
            case "aac":
                builder.append("-x --audio-format aac ");
                break;
            case "flac":
                builder.append("-x --audio-format flac ");
                break;
            case "m4a":
                builder.append("-x --audio-format m4a ");
                break;
            case "opus":
                builder.append("-x --audio-format opus ");
                break;
            case "vorbis":
                builder.append("-x --audio-format vorbis ");
                break;
            case "wav":
                builder.append("-x --audio-format wav ");
                break;
                
            // Video Formats    
            case "mp4":
                builder.append("--recode-video mp4 ");
                break;
            case "flv":
                builder.append("--recode-video flv ");
                break;
            case "ogg":
                builder.append("--recode-video ogg ");
                break;
            case "webm":
                builder.append("--recode-video webm ");
                break;
            case "mkv":
                builder.append("--recode-video mkv ");
                break;
            case "avi":
                builder.append("--recode-video avi ");
                break;
        }

        try
        {
            String[] config = Settings.loadSettings();
            if (!config[0].equals(""))
                builder.append("-o \"" + config[0] + "%(title)s.%(ext)s\" ");
            if (Boolean.parseBoolean(config[1]))
                builder.append("--no-playlist ");
            if (Boolean.parseBoolean(config[2]))
                builder.append("-i ");
            if (!Boolean.parseBoolean(config[3]))
                builder.append("-w ");
            if (start > 0)
                builder.append("--playlist-start " + start + " ");
            if (end > 0)
                builder.append("--playlist-end " + end + " ");
        }
        catch (Exception e)
        {
            console.setText(console.getText() + "Failed to Load Configuration File, Proceeding with Default Settings... \n");
        }

        builder.append("--ignore-config --ffmpeg-location \"data\\bin\\ffmpeg\\bin\\ffmpeg.exe\" ");

        // Making sure there is a URL entered
        if (URL.length() != 0 && URL.indexOf(" ") == -1)
            builder.append("\"" + URL + "\"");
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
            ProcessBuilder cmd = new ProcessBuilder(new String[] {"cmd", "/C", builder.toString() + " && exit"});
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
