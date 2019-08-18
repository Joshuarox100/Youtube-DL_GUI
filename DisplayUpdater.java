import javax.swing.*;
import java.awt.*;
public class DisplayUpdater extends Thread
{
    private String event;

    // Constructors
    public DisplayUpdater() { 
        this.event = "";
    }

    public DisplayUpdater(String event) {
        this.event = event;
    }

    // Removes an Item from the Queue
    public void run()
    {
        //System.out.println(Main.menu.queue.size());
        if (!event.equals(""))
            Main.menu.queue.remove(Integer.parseInt(event) - 1);
        try {
            Thread.sleep(1000);
        }
        catch (Exception e) {
            System.out.println("Uh Oh \n");
            e.printStackTrace();
        }
        updateQueue();
    }

    // Updates the Queue Display
    public static void updateQueue() {
        Main.menu.queueDisplay.removeAll();
        for (int i = 0; i < Main.menu.queue.size(); i++)
        {
            Main.menu.queueDisplay.add(Main.menu.queue.get(i).entry);
            int value = i + 1;
            Main.menu.queue.get(i).spot.setText(String.valueOf(value));
            Main.menu.queue.get(i).removeBtn.setActionCommand("Remove" + (i + 1));
        }
        Main.menu.queueDisplay.revalidate();
        Main.menu.queueDisplay.repaint();
        enableRemove();
    }
    
    // Prevent a Removal Interrupting Another in Progress
    public static void disableRemove() {
        for (int i = 0; i < Main.menu.queue.size(); i++)
        {
            Main.menu.queue.get(i).removeBtn.setEnabled(false);
        }
    }
    
    // Re-Enable Remove Buttons
    public static void enableRemove() {
        for (int i = 0; i < Main.menu.queue.size(); i++)
        {
            Main.menu.queue.get(i).removeBtn.setEnabled(true);
        }
        Main.menu.removeFlag = false;
    }
}
