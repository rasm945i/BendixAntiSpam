package dk.rasmusbendix.antispam.modules;

import dk.rasmusbendix.antispam.Message;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;

public class CapsModule extends ChatModule {

    public static final String IDENTIFIER = "caps-module";

    // Minimum amount of characters in a message before caps-check is applied
    @Getter @Setter private int minimumCapsCheck;
    // How large a percentage that must be caps for it to be flagged.
    // For this to have effect, minimum caps check must also be "violated"
    @Getter @Setter private double capsThreshold;

    public CapsModule(int minimumCapsCheck, double capsThreshold) {
        super(IDENTIFIER);
        this.minimumCapsCheck = minimumCapsCheck;
        this.capsThreshold = capsThreshold;
    }

    public CapsModule(FileConfiguration config) {
        super(IDENTIFIER);
        loadSettingsFromConfig(config);
    }

    @Override
    public boolean allowChatEvent(Message message, ArrayList<Message> history) {

        // Short message, allow it
        if(message.getContent().length() < minimumCapsCheck)
            return true;

        double capsPercent = uppercasePercentage(message.getContent());

        // 0.95 is not 1.0, allow message. 1.0 value can be adjusted
        if(capsPercent < capsThreshold) {
            return true;
        }

        // Message is caps, flag it
        message.flag(this);

        // Read comments in this method if you question it
        return hasAcceptableHistory(message, history);

    }

    private double uppercasePercentage(String content) {

        double upper = 0;
        char[] chars = content.toCharArray();

        for(char ch : chars) {
            if(Character.isUpperCase(ch)) {
                upper++;
            }
        }

        return upper / chars.length;

    }

    @Override
    public void loadSettingsFromConfig(FileConfiguration config) {
        super.loadSettingsFromConfig(config);
        // Allow sponge-bob meme text
        // 0.6 -> A bit more than half of the message can be caps
        capsThreshold = config.getDouble(getName() + ".caps-threshold", 0.6);
        minimumCapsCheck = config.getInt(getName() + ".minimum-characters-to-check", 3);
    }

}
