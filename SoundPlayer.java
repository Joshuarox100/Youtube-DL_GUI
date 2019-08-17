import java.io.*;
import javax.sound.sampled.*;
public class SoundPlayer extends Thread
{
    Long frame;
    Clip clip; 
    AudioInputStream stream;
    
    public SoundPlayer() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        stream = AudioSystem.getAudioInputStream(new File("data\\sfx\\Complete.wav").getAbsoluteFile());
        clip = AudioSystem.getClip();
        clip.open(stream);
    }
    
    public void run() {
        try {
            SoundPlayer player = new SoundPlayer();
            clip.start();
        }
        catch (Exception e) {
            Main.menu.console.setText(Main.menu.console.getText() + "ERROR: Complete.wav not found. Continuing...");
        }
    }
}
