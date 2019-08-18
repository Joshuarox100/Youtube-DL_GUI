    import java.awt.*;
    import java.awt.event.*;
    import javax.swing.*;
    import javax.swing.text.*;
    import javax.imageio.*;
    import java.io.*;
    import java.util.*;
    import misc.*;
    
    public class GUI extends Frame implements ActionListener, WindowListener{
        // declare all the things.
        public JTextPane console;
        public JTextField liveOut;
        public JTextField urlEntry;
        private String[] formats = {"format", "default", "mp4", "flv", "ogg", "webm", "mkv", "avi", "mp3", "aac", "flac", "m4a", "opus", "vorbis", "wav"};
        private JComboBox format;
        private JCheckBox limit;
        private JTextField listStart;
        private JTextField listEnd;
        public JButton linkAdd;
        public JButton download;
        private JButton cancel;
        private JButton settings;
        public JPanel queueDisplay;
        public Color[] lightMode = {Color.WHITE, new Color(240, 240, 240), Color.BLACK, new Color(248, 248, 248)};
        public Color[] darkMode = {Color.BLACK, new Color(51, 51, 51), Color.WHITE, new Color(25, 25, 25)};
        public SettingsGUI settingsWindow;
        public ArrayList<Downloader> downloads = new ArrayList<Downloader>();
        public ArrayList<QueueEntry> queue;
        private Color backgroundColor;
        private Color foregroundColor;
        private Color textColor;
        private Color extraColor;
        public boolean removeFlag = false;
        public boolean downloading = false;
        public String[] config;
    
        // The menu.
        public GUI(){
            // Set main layout to vertical.
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            
            // Load the current config for later use.
            try
            {
                config = Settings.loadSettings();
                Main.theme = config[5];
                // for (int i = 0; i < config.length; i++)
                // {
                // System.out.println(config[i]);
                // }
            }
            catch (Exception e)
            {
                //e.printStackTrace();
            }
            
            // Check current theme and set color palette.
            if (Main.theme.equals("Dark"))
            {
                backgroundColor = darkMode[0];
                foregroundColor = darkMode[1];
                textColor = darkMode[2];
                extraColor = darkMode[3];
            }
            else
            {
                backgroundColor = lightMode[0];
                foregroundColor = lightMode[1];
                textColor = lightMode[2];
                extraColor = lightMode[3];
            }
            
            // Top half of window
            JPanel top = new JPanel();
            top.setBackground(foregroundColor);
            
            // View window for the queue
            JPanel queueArea = new JPanel();
            queueArea.setBackground(foregroundColor);
    
            queueDisplay = new JPanel();
            queueDisplay.setLayout(new ModifiedFlowLayout());
            queueDisplay.setBackground(extraColor);
            queue = new ArrayList<QueueEntry>();
    
            JScrollPane queueScroll = new JScrollPane(queueDisplay, 
                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            queueScroll.setViewportView(queueDisplay);
            queueScroll.setPreferredSize(new Dimension(320, 240));
            new SmartScroller(queueScroll);
    
            queueArea.add(queueScroll);
            
            
            // View window for the log 
            JPanel logArea = new JPanel();
    
            console = new JTextPane();
            console.setFont(new JTextArea().getFont().deriveFont(11.5f));
            console.setBackground(backgroundColor);
            console.setOpaque(true);
            console.setEditable(false);
            logArea.add(console);
            JScrollPane logTemp = new JScrollPane(logArea, 
                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            logTemp.setViewportView(console);
            logTemp.setPreferredSize(new Dimension(320, 220));
            new SmartScroller(logTemp);
            
            // Live output field
            liveOut = new JTextField();
            liveOut.setBackground(backgroundColor);
            liveOut.setForeground(textColor);
            liveOut.setEditable(false);
    
            JPanel log = new JPanel();
            log.setLayout(new BoxLayout(log, BoxLayout.Y_AXIS));
            log.add(logTemp);
            log.add(liveOut);
            
            // Text settings for the log
            StyledDocument doc = console.getStyledDocument();
            SimpleAttributeSet left = new SimpleAttributeSet();
            StyleConstants.setAlignment(left, StyleConstants.ALIGN_LEFT);
            StyleConstants.setForeground(left, textColor);
            doc.setParagraphAttributes(0, doc.getLength(), left, false);
    
            // Placing components in the top half of the window.
            top.add(Box.createHorizontalStrut(3));
            top.add(queueArea);
            top.add(Box.createHorizontalStrut(1));
            top.add(log);
            top.add(Box.createHorizontalStrut(4));
            top.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
    
            // Bottom half of the window.
            JPanel bottom = new JPanel();
            bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
            bottom.setBackground(foregroundColor);
    
            // top part of the bottom half.
            JPanel upper = new JPanel();
            upper.setBackground(foregroundColor);
            
            // URL entry field.
            urlEntry = new HintTextField("Video or Playlist URL");
            urlEntry.setPreferredSize(new Dimension(530, 28));
            urlEntry.setBackground(backgroundColor);
            urlEntry.setForeground(textColor);
            urlEntry.setToolTipText("The link to the video or playlist to be downloaded.");
            urlEntry.setActionCommand("Add");
            
            // Button to add link to the queue.
            linkAdd = new JButton();
            try {
                Image add = ImageIO.read(getClass().getResource("images/Add.png"));
                linkAdd.setIcon(new ImageIcon(add.getScaledInstance(24, 24,  java.awt.Image.SCALE_DEFAULT)));
            } catch (Exception ex) {
                linkAdd.setText("+");
            } 
            linkAdd.setActionCommand("Add");
            linkAdd.setPreferredSize(new Dimension(28 ,28));
            linkAdd.setFocusPainted(false);
            linkAdd.setToolTipText("Adds the URL to the queue.");
            linkAdd.setBackground(backgroundColor);
            linkAdd.setForeground(textColor);
    
            // Format selection menu.
            format = new JComboBox(formats);
            format.setPreferredSize(new Dimension(75, 28));
            format.setBackground(backgroundColor);
            format.setForeground(textColor);
            format.setToolTipText("The format to convert to after downloading.");
            format.setOpaque(true);
    
            // Placing components in the upper part of the bottom half.
            upper.add(Box.createHorizontalStrut(4));
            upper.add(urlEntry);
            upper.add(linkAdd);
            upper.add(Box.createHorizontalStrut(6));
            upper.add(format);
            upper.add(Box.createHorizontalStrut(3));
    
            // lower part of the bottom half.
            JPanel lower = new JPanel();
            lower.setBackground(foregroundColor);
    
            // panel for settings playlist setttings.
            JPanel playlist = new JPanel();
            playlist.setLayout(new BoxLayout(playlist, BoxLayout.Y_AXIS));
            playlist.setBackground(foregroundColor);
    
            // top part of the playlist settings.
            JPanel high = new JPanel();
            high.setBackground(foregroundColor);
            JLabel limitText = new JLabel();
            limitText.setForeground(textColor);
            limitText.setBackground(foregroundColor);
            limitText.setText("Limit Playlist");
            limit = new JCheckBox();
            limit.setBackground(foregroundColor);
            limit.setActionCommand("Limit");
            limit.setToolTipText("Limits the number of videos to be downloaded from the playlist (if any).");
            high.add(limitText);
            high.add(limit);
    
            // lower part of the playlist settings.
            JPanel low = new JPanel();
            low.setBackground(foregroundColor);
            low.setLayout(new GridLayout(1, 2));
            listStart = new HintTextField("First");
            listEnd = new HintTextField("Last");
            listStart.setBackground(backgroundColor);
            listStart.setForeground(textColor);
            listStart.setToolTipText("The index of the first video to download.");
            listStart.setEditable(false);
            listEnd.setBackground(backgroundColor);
            listEnd.setForeground(textColor);
            listEnd.setToolTipText("The index of the last video to download.");
            listEnd.setEditable(false);
            low.add(listStart);
            low.add(listEnd);
    
            // placing the upper and lower half of the playlist settings.
            playlist.add(high);
            playlist.add(Box.createVerticalStrut(3));
            playlist.add(low);
    
            // Button for more settings.
            settings = new JButton("<html><p style=\"text-align:center\"><font size=\"4\">" + "Advanced<br>Settings" + "</font></p></html>");
            settings.setBackground(backgroundColor);
            settings.setForeground(textColor);
            settings.setFocusPainted(false);
            settings.setOpaque(true);
            settings.setPreferredSize(new Dimension(128, 64));
            settings.setActionCommand("Settings");
            settings.setToolTipText("More settings.");
    
            // the download button.
            download = new JButton("<html><p style=\"text-align:center\"><font size=\"5\">" + "Start Download" + "</font></p></html>");
            download.setBackground(backgroundColor);
            download.setForeground(textColor);
            download.setFocusPainted(false);
            download.setPreferredSize(new Dimension(350, 64));
            download.setActionCommand("Download");
            download.setToolTipText("Starts downloading the queue, starting from the top.");
            download.setVisible(true);
    
            // adding click detection.
            limit.addActionListener(this);
            urlEntry.addActionListener(this);
            linkAdd.addActionListener(this);
            download.addActionListener(this);
            settings.addActionListener(this);
    
            // placing components for the lower part of the bottom half.
            lower.add(playlist);
            lower.add(Box.createHorizontalStrut(4));
            lower.add(new JSeparator(SwingConstants.VERTICAL));
            lower.add(Box.createHorizontalStrut(4));
            lower.add(settings);
            lower.add(Box.createHorizontalStrut(4));
            lower.add(new JSeparator(SwingConstants.VERTICAL));
            lower.add(Box.createHorizontalStrut(4));
            lower.add(download);
    
            // adding the halves for the bottom panel.
            bottom.add(upper);
            bottom.add(lower);
            bottom.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
    
            // adding exit button detection.
            addWindowListener(this);
    
            // adding the halves to the window.
            add(top);
            add(Box.createVerticalStrut(1));
            add(bottom);
            add(Box.createVerticalStrut(4));
    
            // setting the title bar and window settings.
            setTitle("Youtube-DL GUI");
            try {
                Image icon = ImageIO.read(getClass().getResource("images/Icon.png"));
                setIconImage(icon);
            } catch (Exception ex) {
                System.out.println(ex);
            } 
            setVisible(true);
            setResizable(false);
            pack();
    
        }
    
        @Override
        public void actionPerformed(ActionEvent evt) {
            //System.out.println(evt.getActionCommand());
            if (evt.getActionCommand().equals("Download")) {
                
                // When the download button is clicked...
                
                if (format.getSelectedItem().toString().equals("format")) {
                    
                    // Check if a format is selected.
                    
                    console.setText("Please Choose a Format (Choose Default to Skip Conversion After Downloading and Keep the Original Format)\n");
                    liveOut.setText("Please Choose a Format");
                    return;
                }
                else if (queue.size() < 1) {
                    
                    // Then check if the queue is empty.
                    
                    if (!urlEntry.getText().equals("")) {
                        
                        // If so, and there is a url entered in the entry field, attempt that url.
                        
                        linkAdd.setEnabled(false);
                        urlEntry.setEnabled(false);
                        console.setText("Queue Empty, Trying Link in Entry Field...");
                        if (limit.isSelected()) {
                            
                            // Checking the playlist limiters.
                            
                            try {
                                queue.add(new QueueEntry(urlEntry.getText(), Integer.parseInt(listStart.getText()), Integer.parseInt(listEnd.getText()), queue.size() + 1));
                            }
                            catch (Exception e) {
                                queue.add(new QueueEntry(urlEntry.getText(), 0, 0, queue.size() + 1));
                            }
                            listStart.setText("");
                            listStart.setEditable(false);
                            listEnd.setText("");
                            listEnd.setEditable(false);
                            limit.setSelected(false);
                        }
                        else {
                            
                            // Adding the url to the queue.
                            
                            queue.add(new QueueEntry(urlEntry.getText(), 0, 0, queue.size() + 1));
                        }
                    }
                    else {
                        
                        // If no url was entered and the queue is empty.
                        
                        console.setText("Please Add A Link to the Queue (Enter A Link and Either Click the Plus Icon or Press Enter on Your Keyboard!)\n");
                        liveOut.setText("Please Add A Link to the Queue");
                        return;
                    }
                }
                try {
                    
                    // Disable updating before downloading.
                    
                    settingsWindow.update.setEnabled(false);
                }
                catch (Exception e) { }
                
                // Disables problem inducing buttons, disables removing items from the queue, and begin downloading.
                
                downloading = true;
                linkAdd.setEnabled(false);
                urlEntry.setEnabled(false);
                DisplayUpdater.disableRemove();
                console.setText("");
                liveOut.setText("Initializing...");
                downloads.add(new Downloader(format.getSelectedItem().toString(), queue, console));
                downloads.get(downloads.size() - 1).start();
                
                // Change the appearance and action of the download button.
                
                download.setText("<html><p style=\"text-align:center\"><font size=\"5\">" + "Cancel" + "</font></p></html>");
                download.setToolTipText("Stops active downloads and removes the current item from the queue.");
                download.setActionCommand("Cancel");
            }
            else if (evt.getActionCommand().equals("Settings")) {
            if (settingsWindow == null) {
                // Create a new settings window
                settingsWindow = new SettingsGUI();
            }
            else {
                // Dispose of the old window and create a new one.
                settingsWindow.dispose();
                settingsWindow = new SettingsGUI();
            }
        }
        else if (evt.getActionCommand().equals("Cancel")) {
            
            // Revert the appearance of the download button, re-enable disabled buttons, and stop active downloads.
            
            download.setText("<html><p style=\"text-align:center\"><font size=\"5\">" + "Start Download" + "</font></p></html>");
            download.setActionCommand("Download");
            download.setToolTipText("Starts downloading videos/playlists in the queue, starting from the top.");
            linkAdd.setEnabled(true);
            urlEntry.setEnabled(true);
            DisplayUpdater.enableRemove();
            new Stopper().start();
        }
        else if (evt.getActionCommand().equals("Add")) {
            
            // Temporarily disables buttons and adds the entered url to the queue.
            
            urlEntry.setEnabled(false);
            linkAdd.setEnabled(false);
            if (limit.isSelected()) {
                try {
                    queue.add(new QueueEntry(urlEntry.getText(), Integer.parseInt(listStart.getText()), Integer.parseInt(listEnd.getText()), queue.size() + 1));
                }
                catch (Exception e) {
                    queue.add(new QueueEntry(urlEntry.getText(), 0, 0, queue.size() + 1));
                }
                listStart.setText("");
                listStart.setEditable(false);
                listEnd.setText("");
                listEnd.setEditable(false);
                limit.setSelected(false);
            }
            else {
                queue.add(new QueueEntry(urlEntry.getText(), 0, 0, queue.size() + 1));
            }
        }
        else if (evt.getActionCommand().indexOf("Remove") != -1 && !removeFlag) {       
            
            // Identifies and removes the selected item from the queue.
            
            removeFlag = true;
            DisplayUpdater.disableRemove();
            new DisplayUpdater(evt.getActionCommand().substring(6)).start();
        }
        else if (evt.getActionCommand().equals("Limit")) {
            
            // Enables/Disables limiting of an entered playlist.
            
            if (limit.isSelected())
            {
                listStart.setEditable(true);
                listEnd.setEditable(true);
            }
            else
            {
                listStart.setEditable(false);
                listStart.setText("");
                listEnd.setEditable(false);
                listEnd.setText("");
            }
        }
    }

    public void downloadComplete() {
        
        // Modifies the download button's appearance and enables buttons.
        
        download.setText("<html><p style=\"text-align:center\"><font size=\"5\">" + "Start Download" + "</font></p></html>");
        download.setActionCommand("Download");
        download.setToolTipText("Starts downloading videos/playlists in the queue, starting from the top.");
        downloading = false;
        linkAdd.setEnabled(true);
        urlEntry.setEnabled(true);
        try {
            settingsWindow.update.setEnabled(true);
        }
        catch (Exception e) { }
        DisplayUpdater.enableRemove();
    }
    
    public void updateSettings() {
        
        // Reloads the configuration file.
        
        try
        {
            config = Settings.loadSettings();
        }
        catch (Exception e)
        {
            //e.printStackTrace();
        }
    }

    @Override
    public void windowClosing(WindowEvent evt) {
        System.exit(0);  // Terminate the program
    }

    // Not Used, BUT need to provide an empty body to compile.
    @Override public void windowOpened(WindowEvent evt) {}

    @Override public void windowClosed(WindowEvent evt) {}

    @Override public void windowIconified(WindowEvent evt) {}

    @Override public void windowDeiconified(WindowEvent evt) {}

    @Override public void windowActivated(WindowEvent evt) {}

    @Override public void windowDeactivated(WindowEvent evt) {}
}
