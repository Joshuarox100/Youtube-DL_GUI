import java.awt.*;
import javax.swing.*;
import javax.imageio.*;
import java.io.*;

public class QueueEntry extends Thread
{
    public JPanel entry;
    public JTextField spot;
    public String url;
    private JTextField name;
    private JTextField count;
    public int start;
    public int end;
    public JButton removeBtn;

    // Constructor
    public QueueEntry(String link, int start, int end, int index) {
        Color backgroundColor;
        Color foregroundColor;
        Color textColor;
        Color extraColor;

        // Sets Color Palette
        if (Main.theme.equals("Dark"))
        {
            backgroundColor = Main.menu.darkMode[0];
            foregroundColor = Main.menu.darkMode[1];
            textColor = Main.menu.darkMode[2];
            extraColor = Main.menu.darkMode[3];
        }
        else
        {
            backgroundColor = Main.menu.lightMode[0];
            foregroundColor = Main.menu.lightMode[1];
            textColor = Main.menu.lightMode[2];
            extraColor = Main.menu.lightMode[3];
        }

        // Playlist Limits
        this.start = start;
        this.end = end;

        // Component to be Used
        entry = new JPanel();
        entry.setBackground(extraColor);
        entry.setPreferredSize(new Dimension(300, 32));

        // Place in the Queue
        spot = new JTextField();
        spot.setBackground(backgroundColor);
        spot.setForeground(textColor);

        // Title of Playlist/Video
        name = new JTextField();
        name.setBackground(backgroundColor);
        name.setForeground(textColor);

        // Number of Videos
        count = new JTextField();
        count.setBackground(backgroundColor);
        count.setForeground(textColor);

        // Removes Self From Queue
        removeBtn = new JButton();
        removeBtn.setBackground(foregroundColor);

        // Video Link
        url = link;

        // Setting Component Stuffs
        spot.setHorizontalAlignment(JTextField.CENTER);
        spot.setEditable(false);
        spot.setText(String.valueOf(index));
        spot.setPreferredSize(new Dimension(28, 28));

        name.setEditable(false);
        name.setText("Loading Title...");
        name.setPreferredSize(new Dimension(180, 28));

        count.setHorizontalAlignment(JTextField.CENTER);
        count.setEditable(false);
        count.setText("1");
        count.setPreferredSize(new Dimension(36, 28));

        try {
            Image add = ImageIO.read(getClass().getResource("images/Remove.png"));
            removeBtn.setIcon(new ImageIcon(add.getScaledInstance(24, 24,  java.awt.Image.SCALE_DEFAULT)));
        } catch (Exception ex) {
            removeBtn.setText("X");
        } 
        removeBtn.setActionCommand("Remove" + index);
        removeBtn.setPreferredSize(new Dimension(28, 28));
        removeBtn.setEnabled(false);
        removeBtn.addActionListener(Main.menu);

        entry.add(spot);
        entry.add(name);
        entry.add(count);
        entry.add(removeBtn);

        // Refreshes the Display and Starts the Thread
        DisplayUpdater.updateQueue();
        this.start();
    }

    private String getTitle(String link) {
        // Base Values
        String result = "Invalid Link";
        int value = 0;
        try
        { 
            // Launch command prompt and run command
            StringBuilder command = new StringBuilder();
            command.append("data\\bin\\ytdl\\youtube-dl.exe -s --skip-download ");
            if (Boolean.parseBoolean(Main.menu.config[1]))
                command.append("--no-playlist ");
            if (start > 0)
                command.append("--playlist-start " + start + " ");
            if (end > 0)
                command.append("--playlist-end " + end + " ");
            ProcessBuilder cmd = new ProcessBuilder(new String[] {"cmd", "/C", command.toString() + "\"" + link + "\" && exit"});
            cmd.redirectErrorStream(true);
            Process proc = cmd.start();
            // Read the output and error log from command prompt
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream(), "UTF8"));

            // Simulate the Download to Find the Name;
            String s = null;
            String d = null;
            String p = null;
            while ((s = stdInput.readLine()) != null) {
                //System.out.println(s);
                if (s.indexOf("ERROR") == 0 || s.indexOf("youtube-dl.exe: error") == 0) {
                    //System.out.println("Error");
                    break;
                }
                else if (s.indexOf("Downloading playlist:") != -1) {
                    result = s;
                    while((d = stdInput.readLine()) != null) {
                        if (d.indexOf(result.substring(result.indexOf(":") + 1)) != -1) {
                            p = d;
                            break;
                        }
                    }
                    break;
                }             
            }
            // If a Playlist is Detected
            if (p != null) {
                // Isolate the Name and Number of Videos
                result = result.substring(result.indexOf(":") + 1);
                //System.out.println(p);
                String amount = p.substring(27 + result.length());
                if (amount.indexOf("downloading") != -1)
                amount = amount.substring(amount.indexOf("downloading") + 12, amount.indexOf("of") - 1);
                else if (amount.indexOf("Downloading") != -1)
                amount = amount.substring(amount.indexOf("Downloading") + 12, amount.indexOf("videos") - 1);
                value = Integer.parseInt(amount);
                stdInput.close();
            }
            else if (result.equals("Invalid Link")) {
                // Check if the Link is Invalid or a Single Video
                //System.out.println("here");
                String command2 = "data\\bin\\ytdl\\youtube-dl.exe -e --no-playlist ";
                if (start > 0)
                    command2 = command2 + "--playlist-start " + start + " ";
                if (end > 0)
                    command2 = command2 + "--playlist-end " + end + " ";
                ProcessBuilder cmd2 = new ProcessBuilder(new String[] {"cmd", "/C", command2 + "\"" + link + "\" && exit"});
                cmd2.redirectErrorStream(true);
                Process proc2 = cmd2.start();
                // Read the output and error log from command prompt
                BufferedReader stdInput2 = new BufferedReader(new InputStreamReader(proc2.getInputStream(), "UTF8"));

                // Read the output from command prompt
                String t = null;
                while ((t = stdInput2.readLine()) != null) {
                    //System.out.println(t);
                    if (t.indexOf("ERROR") == 0 || t.indexOf("youtube-dl.exe: error") == 0)
                        break;
                    result = t;              
                    value += 1;
                }
                stdInput2.close();
            }
            this.count.setText(String.valueOf(value));
            this.removeBtn.setEnabled(true);
        } 
        catch (Exception e) 
        {  
            e.printStackTrace();
            //Main.menu.console.setText(Main.menu.console.getText() + "Something Broke Here... \n");
        } 
        // Shorten the title if it's too long
        if (result.length() > 32)
            result = result.substring(0, 31) + "...";
        return result;
    }

    public void run() {
        // Wait for UI to update
        try {
            Thread.sleep(1000);
        }
        catch (Exception e) {
            System.out.println("Uh Oh \n");
            e.printStackTrace();
        }
        DisplayUpdater.updateQueue();
        if (!Main.menu.downloading) {
            Main.menu.linkAdd.setEnabled(true);
            Main.menu.urlEntry.setEnabled(true);
        }
        name.setText(getTitle(url));
    }
}
