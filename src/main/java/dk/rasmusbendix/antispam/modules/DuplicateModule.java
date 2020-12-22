package dk.rasmusbendix.antispam.modules;

import dk.rasmusbendix.antispam.Message;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;

public class DuplicateModule extends ChatModule {

    public static final String IDENTIFIER = "duplicate-module";

    // Max amount of similar/duplicate messages
    @Getter @Setter private int maxDuplicateMessages;

    // Less than 0 to disable altered duplication check
    // Don't set this too low, or short messages may get flagged
    // I'd recommend setting it above 10 as it slices the message in half to compare
    @Getter @Setter private int alteredDuplicateCheckLength;

    public DuplicateModule(int maxDuplicateMessages) {
        this(maxDuplicateMessages, -1);
    }

    public DuplicateModule(int maxDuplicateMessages, int alteredDuplicateCheckLength) {
        super(IDENTIFIER);
        this.maxDuplicateMessages = maxDuplicateMessages;
        this.alteredDuplicateCheckLength = alteredDuplicateCheckLength;
    }

    public DuplicateModule(FileConfiguration config) {
        super(IDENTIFIER);
        loadSettingsFromConfig(config);
    }

    @Override
    public boolean allowChatEvent(Message message, ArrayList<Message> history) {

        boolean violates = countDuplicates(message.getContent(), history) > maxDuplicateMessages;
        if(!violates)
            return true;

        message.flag(this);

        return hasAcceptableHistory(message, history);

    }

    public int countDuplicates(String content, ArrayList<Message> messages) {
        int duplicateCount = 0;
        for(Message msg : messages) {
            if(msg.getContent().equalsIgnoreCase(content)) {
                duplicateCount++;
                continue;
            }
            // If the first half is the same, it is duplicate
            if(alteredDuplicateCheckLength > 0 && content.length() >= alteredDuplicateCheckLength) {
                if (content.toLowerCase().startsWith(msg.getContent().toLowerCase().substring(0, msg.getContent().length() / 2))) {
                    duplicateCount++;
                }
            }
        }
        return duplicateCount;
    }

    @Override
    public void loadSettingsFromConfig(FileConfiguration config) {
        super.loadSettingsFromConfig(config);
        // Low history-value to reduce false-positives.
        // Someone can reply "ok" to a message and have it be an answer to a second message too
        maxDuplicateMessages = config.getInt(getName() + ".max-duplicate-messages", 1);
        alteredDuplicateCheckLength = config.getInt(getName() + ".altered-duplicate-check-length", 20);
    }
}