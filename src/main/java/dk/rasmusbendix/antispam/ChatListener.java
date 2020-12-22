package dk.rasmusbendix.antispam;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class ChatListener implements Listener {

    @Getter @Setter private long chatDelayMs; // Minimum delay between messages
    @Getter @Setter private int maxDuplicateMessages; // Max amount of similar/duplicate messages
    @Getter @Setter private long messageExpireTimeMs; // Time it takes for stored messages to expire
    @Getter @Setter private int alteredDuplicateCheckLength; // Don't set this too low, or short messages may get flagged
    @Getter @Setter private int minimumCapsCheck; // Minimum amount of characters in a message before caps-check is applied
    @Getter @Setter private int maxRepetitiveChars; // Max amount of times a single character can be after itself
    @Getter @Setter private int maxRepetitiveSymbols; // Max amount of times a character that is not a-zA-Z0-9 can be present in a row

    private final HashMap<UUID, ArrayList<Message>> msgMap;

    public ChatListener(AntiSpam plugin) {
        chatDelayMs = 1500; // 1,5 seconds between each message
        maxDuplicateMessages = 0; // I hate retards that copy/paste
        messageExpireTimeMs = 30000; // Messages are removed from memory after 30 seconds
        alteredDuplicateCheckLength = 14; // Only check for similarity on messages longer than this
        minimumCapsCheck = 6; // Messages must be equal or longer than this value to be checked for caps. Allow "FUCK" outbursts.
        maxRepetitiveChars = 10; // Max amount of times any character can be repeated, ie. Weeeeeeee can max have 10 e's
        maxRepetitiveSymbols = 5; // Same as above, but with symbols of any kind. Prevents excessive !!!! and @@@@  $!$@@ and shit like that
        this.msgMap = new HashMap<>();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {

        UUID user = e.getPlayer().getUniqueId();
        if(!msgMap.containsKey(user))
            msgMap.put(user, new ArrayList<>());

        ArrayList<Message> messages = msgMap.get(user);

        if(!canChat(user)) {
            e.setCancelled(true);
            e.getPlayer().sendMessage("You're sending messages too fast, calm your keyboard.");
            return;
        }

        if(isCapsLock(e.getMessage())) {
            e.setCancelled(true);
            e.getPlayer().sendMessage("Please do not use all-caps");
            return;
        }

        if(isRepetitive(e.getMessage())) {
            e.setCancelled(true);
            e.getPlayer().sendMessage("Your message contains very the same character many times in a row and was detected as spam.");
            return;
        }

        if(isSymbolsRepetitive(e.getMessage())) {
            e.setCancelled(true);
            e.getPlayer().sendMessage("Please refrain from spamming symbols like that.");
            return;
        }

        if(countDuplicates(e.getMessage(), messages) > maxDuplicateMessages) {
            e.setCancelled(true);
            e.getPlayer().sendMessage("Your message was detected as a duplicate.");
            return;
        }

        // The user passed all checks, weehoo!
        addMessage(user, new Message(e.getMessage()));

    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent e) {
        msgMap.remove(e.getPlayer().getUniqueId());
    }


    private void addMessage(UUID uuid, Message message) {
        // Usually just append to the existing list
        if(msgMap.containsKey(uuid)) {
            msgMap.get(uuid).add(message);
        } else {
            // User hasn't sent any messages, create new list
            ArrayList<Message> list = new ArrayList<>();
            list.add(message);
            msgMap.put(uuid, list);
        }
    }

    public int countDuplicates(String content, ArrayList<Message> messages) {
        int duplicateCount = 0;
        for(Message msg : messages) {
            if(msg.getContent().equalsIgnoreCase(content)) {
                duplicateCount++;
                continue;
            }
            // If the first half is the same, it is duplicate
            if(content.length() >= alteredDuplicateCheckLength) {
                if (content.toLowerCase().startsWith(msg.getContent().toLowerCase().substring(0, msg.getContent().length() / 2))) {
                    duplicateCount++;
                }
            }
        }
        return duplicateCount;
    }


    public boolean isCapsLock(String content) {
        return content.length() >= minimumCapsCheck && StringUtils.isAllUpperCase(content);
    }

    public boolean isRepetitive(String msg) {
        char lastChar = ' ';
        int count = 0;

        char[] chars = msg.toCharArray();
        for(char ch : chars) {
            if(ch == lastChar)
                count++;
            lastChar = ch;
        }
        return count >= maxRepetitiveChars;
    }

    public boolean isSymbolsRepetitive(String msg) {

        int count = 0;

        char[] chars = msg.toCharArray();
        for(char ch : chars) {

            if(!Character.isLetterOrDigit(ch) && !Character.isSpaceChar(ch))
                // Not a letter, digit or space, probably is a symbol
                count++;
            else
                // Reset once a valid character has been met
                count = 0;

        }

        // If symbols take up half the message, id call that excessive or unnecessary
        return count >= maxRepetitiveSymbols && count >= msg.length()/2;

    }



    // Check if the cooldown period has passed
    public boolean canChat(UUID uuid) {
        Message latest = getLastMessage(uuid);
        if(latest == null)
            return true;

        return hasMessageExpired(latest, System.currentTimeMillis() - chatDelayMs);

    }

    public Message getLastMessage(UUID uuid) {
        if(!msgMap.containsKey(uuid))
            return null;
        ArrayList<Message> list = msgMap.get(uuid);
        return list.get(list.size()-1); // Return last element
    }

    public void removeExpiredMessages(ArrayList<Message> list) {
        long timestamp = System.currentTimeMillis();
        list.removeIf(msg -> hasMessageExpired(msg, timestamp));
    }

    public boolean hasMessageExpired(Message message, long time) {
        return message.getTimestamp() + messageExpireTimeMs <= time;
    }

    public boolean hasMessageExpired(Message message) {
        return hasMessageExpired(message, System.currentTimeMillis());
    }

}
