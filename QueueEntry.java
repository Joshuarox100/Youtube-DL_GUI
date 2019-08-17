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

    public QueueEntry(String link, int start, int end, int index) {
        Color backgroundColor;
        Color foregroundColor;
        Color textColor;
        Color extraColor;

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

        this.start = start;
        this.end = end;

        entry = new JPanel();
        entry.setBackground(extraColor);
        entry.setPreferredSize(new Dimension(300, 32));

        spot = new JTextField();
        spot.setBackground(backgroundColor);
        spot.setForeground(textColor);

        name = new JTextField();
        name.setBackground(backgroundColor);
        name.setForeground(textColor);

        count = new JTextField();
        count.setBackground(backgroundColor);
        count.setForeground(textColor);

        removeBtn = new JButton();
        removeBtn.setBackground(foregroundColor);

        url = link;

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

        DisplayUpdater.updateQueue();
        this.start();
    }

    private String getTitle(String link) {
        String result = "Invalid Link";
        int value = 0;
        try
        { 
            // Launch command prompt and run command
            String command = "data\\bin\\ytdl\\youtube-dl.exe -s --skip-download ";
            if (Boolean.parseBoolean(Main.menu.config[1]))
                command = command + "--no-playlist ";
            if (start > 0)
                command = command + "--playlist-start " + start + " ";
            if (end > 0)
                command = command + "--playlist-end " + end + " ";
            ProcessBuilder cmd = new ProcessBuilder(new String[] {"cmd", "/C", command + "\"" + link + "\" && exit"});
            cmd.redirectErrorStream(true);
            Process proc = cmd.start();
            // Read the output and error log from command prompt
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream(), "UTF8"));

            // Show the output from the command prompt
            String s = null;
            String p = null;
            while ((s = stdInput.readLine()) != null) {
                //System.out.println(s);
                if (s.indexOf("ERROR") == 0 || s.indexOf("youtube-dl.exe: error") == 0) {
                    //System.out.println("Error");
                    break;
                }
                else if (s.indexOf("Downloading playlist:") != -1) {
                    result = s;
                    p = stdInput.readLine();
                    break;
                }             
            }

            if (p != null) {
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

                // Show the output from the command prompt
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
        if (result.length() > 32)
            result = result.substring(0, 31) + "...";
        return result;
    }

    public void run() {
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
