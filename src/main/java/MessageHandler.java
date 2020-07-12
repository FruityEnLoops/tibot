import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;

public class MessageHandler {
    private final static String argSeparator = " ";

    // Gets each argument (an argument is separated by argSeparator, here a space)
    public static String[] getArgumentList(String message){
        return message.split(argSeparator);
    }

    public static void addAuthorized(MessageReceivedEvent e){
        String[] args = getArgumentList(e.getMessage().getContentRaw());
        if(args.length == 3 && args[2] != null){
            Main.authorized.add(Long.parseLong(args[2]));
            e.getChannel().sendMessage("Added Discord user " + Long.parseLong(args[2]) + " to authorized list.").queue();
            System.out.println("[DEBUG] Added Discord user " + Long.parseLong(args[2]) + " to authorized list.");
        }
    }

    public static void removeAuthorized(MessageReceivedEvent e){
        String[] args = getArgumentList(e.getMessage().getContentRaw());
        if(args.length == 3 && args[2] != null){
            Main.authorized.remove(Long.parseLong(args[2]));
            e.getChannel().sendMessage("Removed Discord user " + Long.parseLong(args[2]) + " from authorized list.").queue();
            System.out.println("[DEBUG] Removed Discord user " + Long.parseLong(args[2]) + " from authorized list.");
        }
    }

    public static void sendAdminList(MessageReceivedEvent event){
        event.getChannel().sendMessage("Current authorized Discord UIDs are: " + Main.authorized.toString()).queue();
    }

    public static void handlePrefix(MessageReceivedEvent e){
        String[] args = getArgumentList(e.getMessage().getContentRaw());
        if(args[0].contains("~~")){
            return;
        }
        if (args.length == 2) {
            if(args[0].equals("~add") && !e.getMessage().getAttachments().isEmpty()){
                File file = new File("quotes/" + e.getMessage().getAttachments().get(0).getFileName());
                e.getMessage().getAttachments().get(0).download(file);
                Main.itemList.add(new SendableItem(file, args[1], e.getChannel().getIdLong()));
                e.getChannel().sendMessage("Added quote : " + file.toString()).queue();
                System.out.println("[DEBUG] Added item " + file.getName() + " from item list. Requested by " +
                        e.getAuthor().getName() +
                        " on " +
                        LocalDate.now() +
                        " " + LocalTime.now());
            } else if(args[0].equals("~add") && e.getMessage().getAttachments().isEmpty()){
                e.getChannel().sendMessage("You need to provide a file to create your quote.").queue();
            } else if(args[0].equals("~remove")){
                // TODO redo it so it works
                e.getChannel().sendMessage("Quote " + args[1] + " not found.").queue();
            } else {
                e.getChannel().sendMessage("You typed the prefix, but you didn't make any sense. Maybe try doing `" + Main.prefix + "help` ?").queue();
            }
        } else if(args.length == 1){
            if(args[0].equals("~help")) {
                e.getChannel().sendMessage("**Available commands :**\n" +
                        "Bot prefix : `" + Main.prefix + "`\n" +
                        "`add <trigger text>` (+ attach your file) - Adds a quote that will trigger when the text is detected in the message, and send the provided file\n" +
                        "`remove <trigger text>` - Removes the quote triggered with trigger text.\n" +
                        "`help` - what you're reading right now\n\n" +
                        "tiBot by FruityEnLoops / blobdash - Source at https://github.com/FruityEnLoops/tibot").queue();
            } else if(args[0].equals("~tibo")) {
                e.getChannel().sendFile(new File("quotes/tibo.wav")).queue();
            } else {
                e.getChannel().sendMessage("You typed the prefix, but you didn't make any sense. Maybe try doing `" + Main.prefix + "help` ?").queue();
            }
        } else {
            e.getChannel().sendMessage("You typed the prefix, but you didn't make any sense. Maybe try doing `" + Main.prefix + "help` ?").queue();
        }
    }

    public static void sendQuoteList(MessageReceivedEvent event) {
        event.getChannel().sendMessage("Current quotes : " + Main.itemList.toString()).queue();
    }
}
