package dk.rasmusbendix.antispam;

import dk.rasmusbendix.antispam.modules.ChatModule;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class ChatListener implements Listener {

    @Getter @Setter private long messageExpireTimeMs; // Time it takes for stored messages to expire

    private final HashMap<UUID, ArrayList<Message>> msgMap;
    private final HashSet<ChatModule> chatModules;

    public ChatListener(AntiSpam plugin) {
        messageExpireTimeMs = 30000; // Messages are removed from memory after 30 seconds
        this.msgMap = new HashMap<>();
        this.chatModules = new HashSet<>();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void registerChatModule(ChatModule module) {
        chatModules.add(module);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {

        UUID user = e.getPlayer().getUniqueId();
        if(!msgMap.containsKey(user))
            msgMap.put(user, new ArrayList<>());

        ArrayList<Message> messages = msgMap.get(user);
        Message current = new Message(e.getMessage());
        ChatModule firstViolatedModule = null;

        // Make sure to go through all modules, as one message could violate multiple modules
        for(ChatModule module : chatModules) {
            if(!module.allowChatEvent(current, messages)) {
                if(firstViolatedModule == null) {
                    firstViolatedModule = module;
                }
            }
        }

        if(firstViolatedModule != null) {
            e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',
                    firstViolatedModule.getViolationMessage()));
            e.setCancelled(true);
            return;
        }

        // The user passed all checks, weehoo!
        addMessage(user, current);

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
            removeExpiredMessages(list);
            msgMap.put(uuid, list);
        }
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
