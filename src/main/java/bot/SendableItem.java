package bot;

import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.io.File;
import java.io.Serializable;

public class SendableItem implements Serializable {
    File item;
    String triggerText;
    long serverID;

    SendableItem(File item, String triggerText, long serverID){
        this.item = item;
        this.triggerText = triggerText;
        this.serverID = serverID;
    }
    public void sendItem(MessageReceivedEvent e){
        e.getChannel().sendFile(item).queue();
    }

    public String toString(){
        return this.item.getName();
    }
}
