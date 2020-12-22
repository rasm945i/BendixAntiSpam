package dk.rasmusbendix.antispam.modules;

import dk.rasmusbendix.antispam.Message;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;

public class FloodModule extends ChatModule {

    public static final String IDENTIFIER = "flood-module";

    @Getter @Setter private long chatDelayMs; // Minimum delay between messages

    public FloodModule(long chatDelayMs) {
        super(IDENTIFIER);
        this.chatDelayMs = chatDelayMs;
    }

    public FloodModule(FileConfiguration config) {
        super(IDENTIFIER);
        loadSettingsFromConfig(config);
    }

    @Override
    public boolean allowChatEvent(Message message, ArrayList<Message> history) {

        if(history.isEmpty())
            return true;

        return history.get(history.size()-1).getTimestamp() + chatDelayMs <= System.currentTimeMillis();

    }

    @Override
    public void loadSettingsFromConfig(FileConfiguration config) {
        super.loadSettingsFromConfig(config);
        // Sometimes one can send a quick follow-up message
        // 650ms between each message
        chatDelayMs = config.getLong(getName() + ".minimum-ms-between-messages", 650);
    }
}