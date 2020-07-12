import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Timer;

public class Main extends ListenerAdapter {
    public static ArrayList<Long> authorized = new ArrayList<>();
    public static ArrayList<SendableItem> itemList = new ArrayList<>();
    public static JDA jda;
    // change prefix here
    public final static char prefix = '~';
    public static Timer saveThread = new Timer(true);

    public static void main(String[] args) throws LoginException {
        if(args.length != 1){
            System.out.println("[ERROR] Token not provided, or too much arguments were provided.");
            System.exit(1);
        }

        // initialize admin list
        authorized = loadSerializedAdminList();

        // initialize item list
        itemList = loadSerializedItemList();

        System.out.println("[DEBUG] Started.");
        JDABuilder builder = new JDABuilder(AccountType.BOT);
        builder.setToken(args[0]);
        builder.addEventListener(new Main());
        Main.jda = builder.buildAsync();
        Main.jda.getPresence().setPresence(Game.listening("tibo.wav"), false);
        System.out.println("[DEBUG] Connected.");

        // save thread delay (aka how often the item list & admin list objects are saved to disk)
        long saveDelay = 300000L;

        // schedule save thread
        saveThread.schedule(new SaveThread(), saveDelay, saveDelay);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event){
        // Just to make sure the bot doesn't get stuck reading it's own message, just don't parse the bot's messages. And other bots.
        if(event.getAuthor().isBot()){
            return;
        }

        // handle botadmin messages
        if(authorized.contains(event.getAuthor().getIdLong()) && event.getChannel().getType() == ChannelType.PRIVATE){
            if(event.getMessage().getContentRaw().equals("!botadmin shutdown")){
                System.out.println("[DEBUG] Shutting down. Requested by " +
                        event.getAuthor().getName() +
                        " on " +
                        LocalDate.now() +
                        " " + LocalTime.now());
                saveThread.cancel();
                System.out.println("[DEBUG] (On close) Saving serialized objects.");
                save();
                System.exit(0);
            }
            if(event.getMessage().getContentRaw().contains("!botadmin add")){
                MessageHandler.addAuthorized(event);
            }
            if(event.getMessage().getContentRaw().contains("!botadmin remove")){
                MessageHandler.removeAuthorized(event);
            }
            if(event.getMessage().getContentRaw().equals("!botadmin list admins")){
                MessageHandler.sendAdminList(event);
            }
            if(event.getMessage().getContentRaw().equals("!botadmin save")){
                System.out.println("[DEBUG] (Manual) Saving serialized objects.");
                save();
            }
            if(event.getMessage().getContentRaw().equals("!botadmin list quotes")){
                MessageHandler.sendQuoteList(event);
            }
        }

        // handle prefixed commands
        if(event.getMessage().getContentRaw().charAt(0) == prefix){
            MessageHandler.handlePrefix(event);
            return;
        }

        // handle item sends
        SendableItem a = eventContainsTrigger(event);
        if(a != null){
            System.out.println("[DEBUG] Sending " + a + ". Requested by " +
                    event.getAuthor().getName() +
                    " on " +
                    LocalDate.now() +
                    " " + LocalTime.now());
            a.sendItem(event);
            Main.jda.getPresence().setPresence(Game.listening(a.toString()), false);
        }
    }

    private SendableItem eventContainsTrigger(MessageReceivedEvent e) {
        for (SendableItem item : itemList) {
            // remove "&& e.getChannel().getIdLong() == item.serverID" to make this bot's item list not server specific
            if (e.getMessage().getContentRaw().contains(item.triggerText) && e.getChannel().getIdLong() == item.serverID) {
                return item;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static ArrayList<Long> loadSerializedAdminList(){
        System.out.println("[DEBUG] Attempting to load admin list object...");
        FileInputStream file;
        try{
            file = new FileInputStream("adminList.obj");
            ObjectInputStream stream = new ObjectInputStream(file);
            ArrayList<Long> object = (ArrayList<Long>) stream.readObject();
            stream.close();
            System.out.println("[DEBUG] Success.");
            return object;
        } catch (ClassNotFoundException | IOException e) {
            // adminList is not present, corrupted, or failed to load for some reason. Return an empty list, warn the administrator
            System.out.println("[WARNING] Admin list object \"adminList.obj\" failed to load. Defaulting to an empty list.");
            return new ArrayList<>();
        }
    }

    public static void saveSerializedAdminList(){
        System.out.println("[DEBUG] Attempting to save admin list object...");
        FileOutputStream file;
        try{
            file = new FileOutputStream("adminList.obj");
            ObjectOutputStream stream = new ObjectOutputStream(file);
            stream.writeObject(authorized);
            stream.flush();
            stream.close();
            System.out.println("[DEBUG] Success.");
        } catch (IOException e) {
            // file is locked by another process, or file is non existent even though it was previously opened
            System.out.println("[WARNING] Admin list object \"adminList.obj\" failed to save. File might be used by something else.");
        }
    }

    @SuppressWarnings("unchecked")
    public static ArrayList<SendableItem> loadSerializedItemList(){
        System.out.println("[DEBUG] Attempting to load item list object...");
        FileInputStream file;
        try{
            file = new FileInputStream("itemList.obj");
            ObjectInputStream stream = new ObjectInputStream(file);
            ArrayList<SendableItem> object = (ArrayList<SendableItem>) stream.readObject();
            stream.close();
            System.out.println("[DEBUG] Success.");
            return object;
        } catch (ClassNotFoundException | IOException e) {
            // itemList is not present, corrupted, or failed to load for some reason. Return an empty list, warn the administrator
            System.out.println("[WARNING] Item list object \"itemList.obj\" failed to load. Defaulting to an empty list.");
            return new ArrayList<>();
        }
    }

    public static void saveSerializedItemList(){
        System.out.println("[DEBUG] Attempting to save item list object...");
        FileOutputStream file;
        try{
            file = new FileOutputStream("itemList.obj");
            ObjectOutputStream stream = new ObjectOutputStream(file);
            stream.writeObject(itemList);
            stream.flush();
            stream.close();
            System.out.println("[DEBUG] Success.");
        } catch (IOException e) {
            // file is locked by another process, or file is non existent even though it was previously opened
            System.out.println("[WARNING] Item list object \"itemList.obj\" failed to save. File might be used by something else.");
        }
    }

    public static void save(){
        Main.saveSerializedAdminList();
        Main.saveSerializedItemList();
    }
}
