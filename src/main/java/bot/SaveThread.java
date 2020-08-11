package bot;

import java.util.TimerTask;

public class SaveThread extends TimerTask {
    public void run() {
        System.out.println("[DEBUG] (SaveThread) Saving serialized objects.");
        Main.save();
    }
}
