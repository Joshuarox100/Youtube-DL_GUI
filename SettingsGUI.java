import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.imageio.*;
import java.io.*;
import misc.*;
public class SettingsGUI extends Frame implements ActionListener, WindowListener{
    // Declare all the things.
    private JLabel directory;
    private JLabel themeLbl;
    public JButton update;
    private JButton apply;
    private JButton revert;
    private JCheckBox list;
    private JCheckBox skip;
    private JCheckBox overwrite;
    private JCheckBox sounds;
    private JComboBox theme;
    private JTextField outputFld;
    private JTextField args;
    private String[] themes = {"Light", "Dark"};
    private String[] config;

    // Settings Menu
    public SettingsGUI(){
        // Sets the window's layout to vertical.
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Loads the configuration file.
        try
        {
            config = Settings.loadSettings();
        }
        catch (Exception e)
        {
            //e.printStackTrace();
        }

        // Declare color palette.
        Color backgroundColor;
        Color foregroundColor;
        Color textColor;

        // Set the color palette based on the current theme.
        if (Main.theme.equals("Dark"))
        {
            backgroundColor = Main.menu.darkMode[0];
            foregroundColor = Main.menu.darkMode[1];
            textColor = Main.menu.darkMode[2];
        }
        else
        {
            backgroundColor = Main.menu.lightMode[0];
            foregroundColor = Main.menu.lightMode[1];
            textColor = Main.menu.lightMode[2];
        }

        // The menu
        JTabbedPane menu = new JTabbedPane();
        menu.setBackground(backgroundColor);
        menu.setForeground(textColor);

        // Developer Testing
        // for (int i = 0; i < 4; i++)
        // System.out.println("Config " + (i + 1) + ": " + config[i]);

        // Settings tab
        JPanel settings = new JPanel();
        settings.setLayout(new BoxLayout(settings, BoxLayout.Y_AXIS));
        settings.setBackground(foregroundColor);

        // Download folder panel
        JPanel outputFolder = new JPanel();
        outputFolder.setLayout(new GridLayout(2, 1, 0, 0));
        outputFolder.setBackground(foregroundColor);

        // folder label panel
        JPanel label = new JPanel();
        label.setBackground(foregroundColor);

        // actual label
        directory = new JLabel("Download Folder:");
        directory.setFont(new Font("SansSerif", Font.BOLD, 18));
        directory.setBackground(foregroundColor);
        directory.setForeground(textColor);

        // adding label to panel
        label.add(directory);

        // panel for path
        JPanel entry = new JPanel();
        entry.setBackground(foregroundColor);

        // text field for download path
        outputFld = new JTextField();
        outputFld.setText(config[0]);
        outputFld.setPreferredSize(new Dimension(385, 30));
        outputFld.setToolTipText("Sets the place to save downloads to.");
        outputFld.setBackground(backgroundColor);
        outputFld.setForeground(textColor);

        // button to open folder picker
        JButton select = new JButton();
        select.setBackground(backgroundColor);
        select.setForeground(textColor);
        try {
            Image add = ImageIO.read(getClass().getResource("images/Choose.png"));
            select.setIcon(new ImageIcon(add.getScaledInstance(24, 24,  java.awt.Image.SCALE_DEFAULT)));
        } catch (Exception ex) {
            select.setText("O");
        } 
        select.setToolTipText("Opens a folder picker.");
        select.setFocusPainted(false);
        select.setPreferredSize(new Dimension(28, 28));
        select.setActionCommand("Choose");

        // adding click detection.
        select.addActionListener(this);

        // adding components to panels.
        entry.add(outputFld);
        entry.add(select);

        outputFolder.add(label);
        outputFolder.add(entry);

        // panel for the middle of the menu
        JPanel middle = new JPanel();
        middle.setLayout(new BoxLayout(middle, BoxLayout.X_AXIS));
        middle.setBackground(foregroundColor);

        // panel for the left half of the layout
        JPanel playlist = new JPanel();
        playlist.setLayout(new BoxLayout(playlist, BoxLayout.Y_AXIS));
        playlist.setPreferredSize(new Dimension(200, 80));
        playlist.setBackground(foregroundColor);

        // checkbox to ignore playlists attached to videos
        list = new JCheckBox();
        list.setText("Ignore Linked Playlists");
        list.setSelected(Boolean.parseBoolean(config[1]));
        list.setBackground(foregroundColor);
        list.setForeground(textColor);
        list.setOpaque(true);
        list.setToolTipText("If a URL for a video also contains a link to a playlist, only download the video.");

        // checkbox to skip unavailable videos when downloading playlists
        skip = new JCheckBox();
        skip.setText("Skip Unavailable Videos");
        skip.setSelected(Boolean.parseBoolean(config[2]));
        skip.setBackground(foregroundColor);
        skip.setForeground(textColor);
        skip.setOpaque(true);
        skip.setToolTipText("If a video in a playlist is unavailable, continue downloading the rest of the playlist.");

        // checkbox to disable file overwriting.
        overwrite = new JCheckBox();
        overwrite.setText("Overwrite Files");
        overwrite.setSelected(Boolean.parseBoolean(config[3]));
        overwrite.setBackground(foregroundColor);
        overwrite.setForeground(textColor);
        overwrite.setOpaque(true);
        overwrite.setToolTipText("If the video is already downloaded, skip the video (Only applies when format is set to \"default\")");

        // adds checkboxes to panel
        playlist.add(list);
        playlist.add(skip);
        playlist.add(overwrite);

        // right half of the middle panel
        JPanel other = new JPanel();
        //other.setLayout(new BoxLayout(other, BoxLayout.Y_AXIS));
        other.setPreferredSize(new Dimension(230, 80));
        other.setBackground(foregroundColor);

        // panel for theme switching
        JPanel themePnl = new JPanel();
        themePnl.setBackground(foregroundColor);

        // label for themes
        themeLbl = new JLabel("Theme:");
        themeLbl.setFont(new Font("SansSerif", Font.BOLD, 18));
        themeLbl.setBackground(foregroundColor);
        themeLbl.setForeground(textColor);

        // theme selection box
        theme = new JComboBox(themes);
        theme.setPreferredSize(new Dimension(100, 30));
        theme.setSelectedItem(config[5]);
        theme.setToolTipText("Changes the colors of the GUI (Requires restart)");
        theme.setBackground(backgroundColor);
        theme.setForeground(textColor);

        // adds label and selection box to theme panel
        themePnl.add(themeLbl);
        themePnl.add(theme);

        // checkbox to disable sounds
        sounds = new JCheckBox();
        sounds.setText("Disable Notification Sounds");
        sounds.setSelected(!Boolean.parseBoolean(config[4]));
        sounds.setBackground(foregroundColor);
        sounds.setForeground(textColor);
        sounds.setToolTipText("Disables sound effects.");
        sounds.setOpaque(true);

        // adds theme panel and sound checkbox to right panel
        other.add(themePnl);
        other.add(sounds);

        // adds previous panels to middle panel
        middle.add(Box.createHorizontalStrut(25));
        middle.add(playlist);
        middle.add(Box.createHorizontalStrut(6));
        middle.add(new JSeparator(SwingConstants.VERTICAL));
        middle.add(Box.createHorizontalStrut(1));
        middle.add(other);

        // panel for extra settings
        JPanel extras = new JPanel();
        extras.setBackground(foregroundColor);

        // label for more arguments
        JLabel extraArgs = new JLabel("Extra Arguments:");
        extraArgs.setFont(new Font("SansSerif", Font.BOLD, 16));
        extraArgs.setBackground(foregroundColor);
        extraArgs.setForeground(textColor);

        // textfield to enter more arguments
        args = new JTextField();
        args.setText(config[6]);
        args.setBackground(backgroundColor);
        args.setForeground(textColor);
        args.setPreferredSize(new Dimension(250, 30));
        args.setToolTipText("Set more arguments for Youtube-DL here.");

        // button to open youtube-dl documentation
        JButton help = new JButton();
        try {
            Image add = ImageIO.read(getClass().getResource("images/Help.png"));
            help.setIcon(new ImageIcon(add.getScaledInstance(24, 24,  java.awt.Image.SCALE_DEFAULT)));
        } catch (Exception ex) {
            help.setText("+");
        } 
        help.setBackground(backgroundColor);
        help.setForeground(textColor);
        help.setFocusPainted(false);
        help.setPreferredSize(new Dimension(30, 30));
        help.setActionCommand("Help");
        help.setToolTipText("Opens the documentation page for Youtube-DL");

        // adds click detection to the button
        help.addActionListener(this);

        // adds components to the extras panel
        extras.add(extraArgs);
        extras.add(args);
        extras.add(help);

        // panel for save and revert
        JPanel applyAndRevert = new JPanel();
        applyAndRevert.setBackground(foregroundColor);

        // button to revert changes
        revert = new JButton("<html><p style=\"text-align:center\"><font size=\"5\">" + "Revert Changes" + "</font></p></html>");
        revert.setBackground(backgroundColor);
        revert.setForeground(textColor);
        revert.setFocusPainted(false);
        revert.setActionCommand("Revert");
        revert.setToolTipText("Reloads the saved settings.");
        revert.setPreferredSize(new Dimension(205, 40));

        // button to save changes
        apply = new JButton("<html><p style=\"text-align:center\"><font size=\"5\">" + "Save Changes" + "</font></p></html>");
        apply.setBackground(backgroundColor);
        apply.setForeground(textColor);
        apply.setFocusPainted(false);
        apply.setActionCommand("Save");
        apply.setToolTipText("Saves the selected settings.");
        apply.setPreferredSize(new Dimension(205, 40));

        // adds buttons to their panel
        applyAndRevert.add(revert);
        applyAndRevert.add(Box.createHorizontalStrut(10));
        applyAndRevert.add(apply);

        // adds click detection to the buttons
        revert.addActionListener(this);
        apply.addActionListener(this);

        // adds a listener to the window
        addWindowListener(this);

        // adds all the panels to the settings tab
        settings.add(Box.createVerticalStrut(16));
        settings.add(outputFolder);
        settings.add(Box.createVerticalStrut(10));
        settings.add(middle);
        settings.add(Box.createVerticalStrut(10));
        settings.add(extras);
        settings.add(Box.createVerticalStrut(10));
        settings.add(applyAndRevert);
        settings.add(Box.createVerticalStrut(16));

        // adds the tab to the window
        menu.addTab("Settings", settings);

        // tab for info about the project
        JPanel about = new JPanel();
        about.setLayout(new BoxLayout(about, BoxLayout.Y_AXIS));

        // textpane for displaying information
        JTextPane info = new JTextPane();
        info.setContentType("text/html");
        info.setFont(new JTextArea().getFont().deriveFont(11.5f));
        info.setBackground(backgroundColor);
        info.setForeground(textColor);
        info.setOpaque(true);
        info.setEditable(false);

        // panel for showing the textpane
        JPanel infoArea = new JPanel();
        infoArea.add(info);
        JScrollPane infoPane = new JScrollPane(infoArea, 
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        infoPane.setViewportView(info);
        infoPane.setPreferredSize(new Dimension(320, 220));

        // reads the about file and adds it to the textpane
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File("data\\doc\\info.txt")));
            String r = "";
            if (Main.menu.config[5].equals("Dark")) 
                r = "<html><font face=\"verdana\" size=\"4\" color=\"white\">" + br.readLine() + "</font><font face=\"verdana\" size=\"3\" color=\"white\"";
            else
                r = "<html><font face=\"verdana\" size=\"4\" color=\"black\">" + br.readLine() + "</font><font face=\"verdana\" size=\"3\" color=\"black\"";
            String s;
            while ((s = br.readLine()) != null)
            {
                r = r + "<br>" + s;
            }
            br.close();

            r = r + "</font></html>";

            info.setText(r);

        }
        catch (Exception e) {
            info.setText("info.txt not found");
        }

        // panel for holding the update button
        JPanel updatePanel = new JPanel();
        updatePanel.setBackground(foregroundColor);
        updatePanel.setPreferredSize(new Dimension(420, 40));

        // button to update youtube-dl
        update = new JButton();
        update.setBackground(backgroundColor);
        update.setForeground(textColor);
        update.setFocusPainted(false);
        update.setText("<html><p style=\"text-align:center\"><font size=\"5\">" + "Update Youtube-DL" + "</font></p></html>");
        update.setPreferredSize(new Dimension(430, 52));
        update.setActionCommand("Update");
        update.setToolTipText("Updates Youtube-DL to the latest version from GitHub");

        // enables or disables the button if downloads are occuring
        if (Main.menu.downloading)
            updateDisable();
        else
            updateEnable();

        // adds click detection to the button
        update.addActionListener(this);

        // adds the button to the panel
        updatePanel.add(update);

        // adds the info pane and update panel to the tab
        about.add(infoPane);
        about.add(updatePanel);

        // adds the tab to the menu
        menu.addTab("About", about);

        // adds the menu to the window
        add(menu);

        // sets the settings for the window
        setTitle("Advanced Settings");
        try {
            Image icon = ImageIO.read(getClass().getResource("images/Settings.png"));
            setIconImage(icon);
        } catch (Exception ex) {
            //System.out.println(ex);
        }
        setBackground(foregroundColor);
        setSize(475, 330);
        setLocation(Main.location);
        pack();
        setResizable(false);
        setVisible(true);
    }

    // enables updating
    public void updateEnable() {
        update.setEnabled(true);
        update.setText("<html><p style=\"text-align:center\"><font size=\"5\">" + "Update Youtube-DL" + "</font></p></html>");
    }

    // disables updating
    public void updateDisable() {
        update.setEnabled(false);
        update.setText("<html><p style=\"text-align:center\"><font size=\"5\">" + "Update Youtube-DL" + "</font></p></html>");
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        if (evt.getActionCommand().equals("Save")) {
            // Save changes
            new Settings(outputFld.getText(), list.isSelected(), skip.isSelected(), overwrite.isSelected(), sounds.isSelected(), theme.getSelectedItem().toString(), args.getText()).start();
            Main.menu.updateSettings();
        }
        else if (evt.getActionCommand().equals("Update")) {
            // Updates youtube-dl
            update.setEnabled(false);
            update.setText("<html><p style=\"text-align:center\"><font size=\"5\">" + "Updating..." + "</font></p></html>");
            new Updater().start();
        }
        else if (evt.getActionCommand().equals("Choose")) {
            // Opens file picker
            JFileChooser chooser = new JFileChooser(); 
            chooser.setCurrentDirectory(new java.io.File("."));
            chooser.setDialogTitle("Select a Folder");
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setAcceptAllFileFilterUsed(false);
            
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) { 
                outputFld.setText(chooser.getSelectedFile() + "\\");
                list.setSelected(Boolean.parseBoolean(config[1]));

            }
            else {
                //System.out.println("No Selection ");
            }
        }
        else if (evt.getActionCommand().equals("Revert")) {
            // Reloads the configuration file.
            try
            {
                config = Settings.loadSettings();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            outputFld.setText(config[0]);
            list.setSelected(Boolean.parseBoolean(config[1]));
            skip.setSelected(Boolean.parseBoolean(config[2]));
        }
        else if (evt.getActionCommand().equals("Help")) {
            new Documentation().start();
        }
    }

    @Override
    public void windowClosing(WindowEvent evt) {
        // Saves the location of the window and closes it.
        Main.location = this.getLocationOnScreen();
        dispose();
    }

    @Override public void windowActivated(WindowEvent evt) {
        // Saves location of the window when focused.
        Main.location = this.getLocationOnScreen();
    }

    @Override public void windowDeactivated(WindowEvent evt) {
        // Saves the location of the window when defocused if still present.
        if (this.isVisible())
            Main.location = this.getLocationOnScreen();
    }

    // Not Used, BUT need to provide an empty body to compile.
    @Override public void windowOpened(WindowEvent evt) {}

    @Override public void windowClosed(WindowEvent evt) {}

    @Override public void windowIconified(WindowEvent evt) {}

    @Override public void windowDeiconified(WindowEvent evt) {}

}
