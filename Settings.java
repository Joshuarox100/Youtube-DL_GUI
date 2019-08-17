import java.io.*;
import java.awt.*;
import javax.swing.*;
public class Settings extends Thread
{
    private String output;
    private boolean playlist;
    private boolean skip;
    private boolean overwrite;
    private boolean sounds;
    private String theme;
    private String args;

    public Settings(String output, boolean playlist, boolean skip, boolean overwrite, boolean sounds, String theme, String args)
    {
        this.output = output;
        this.playlist = playlist;
        this.skip = skip;
        this.overwrite = overwrite;
        this.sounds = sounds;
        this.theme = theme;
        this.args = args;
    }

    public void saveSettings(String output, boolean playlist, boolean skip, boolean overwrite, boolean sounds, String theme, String args) throws IOException
    {
        String config = "version=2\n";

        config = config + "output=" + output + "\n";

        if (playlist)
            config = config + "ignore=true\n";
        else
            config = config + "ignore=false\n";

        if (skip)
            config = config + "skip=true\n";
        else
            config = config + "skip=false\n";

        if (overwrite)
            config = config + "overwrite=true\n";
        else
            config = config + "overwrite=false\n";

        if (!sounds)
            config = config + "sounds=true\n";
        else
            config = config + "sounds=false\n";

        config = config + "theme=" + theme + "\n";

        config = config + "args=" + args + "\n";

        BufferedWriter writer = new BufferedWriter(new FileWriter(Main.config));
        writer.write(config);

        writer.close();

        Main.menu.console.setText(Main.menu.console.getText() + "Settings Saved! (Some Changes May Require A Restart) \n");
        Main.menu.liveOut.setText("Settings Saved!");

    }

    public static String[] loadSettings() throws Exception
    {
        String[] settings = {System.getProperty("user.home") + File.separator + "Downloads" + File.separator, "true", "true", "true", "false", "Light", "", "0"};

        BufferedReader br = new BufferedReader(new FileReader(Main.config));

        String s;
        while ((s = br.readLine()) != null)
        {
            if (s.indexOf("output=") != -1)
                settings[0] = s.substring(7);
            else if (s.indexOf("ignore=") != -1)
                settings[1] = s.substring(7);
            else if (s.indexOf("skip=") != -1)
                settings[2] = s.substring(5);
            else if (s.indexOf("overwrite=") != -1)
                settings[3] = s.substring(10);
            else if (s.indexOf("sounds=") != -1)
                settings[4] = s.substring(7);
            else if (s.indexOf("theme=") != -1)
                settings[5] = s.substring(6);
            else if (s.indexOf("args=") != -1)
                settings[6] = s.substring(5);
            else if (s.indexOf("version=") != -1)
                settings[7] = s.substring(8);
        }
        br.close();

        return settings;
    }

    public void run()
    {
        try
        {
            saveSettings(output, playlist, skip, overwrite, sounds, theme, args);
        }
        catch (Exception e)
        {
            Main.menu.console.setText(Main.menu.console.getText() + "Failed to Save Settings \n");
            Main.menu.liveOut.setText("Failed to Save Settings");
            //e.printStackTrace();
        }
    }
}
