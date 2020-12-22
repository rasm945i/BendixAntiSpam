package dk.rasmusbendix.antispam.modules;

import dk.rasmusbendix.antispam.Message;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.WordUtils;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;

public abstract class ChatModule {

    @Getter @Setter protected GeneralModuleSettings settings;
    @Getter private final String name;

    public ChatModule(String name) {
        this.name = name;
    }


    public abstract boolean allowChatEvent(Message message, ArrayList<Message> history);

    public int countFlags(ArrayList<Message> history) {
        return countFlags(this, history);
    }

    public int countFlags(ChatModule module, ArrayList<Message> history) {
        return countFlags(module, history.size(), history);
    }

    public int countFlags(ChatModule module, int depth , ArrayList<Message> history) {

        int flags = 0;

        for (int i = history.size()-1; i >= history.size() - (depth + 1); i--) {
            // Safety against weird depths
            if(i < 0)
                break;
            Message message = history.get(i);
            // If checking any module and the message is flagged for anything
            // Or, the message is flagged for the module we check against
            if((module == null && message.getTotalFlags() > 0) || message.hasFlag(module)) {
                flags++;
            }

        }

        return flags;

    }

    public boolean hasAcceptableHistory(Message message, ArrayList<Message> history) {

        if(!settings.checkingHistory) {
            // If we don't check the history..
            // Check if this message has the flag for the current module
            // If it does NOT, return TRUE
            // hasFlag returns false, we invert that, we get true
            // If current message is not flagged and we do not check history, history is technically acceptable
            // I hope you enjoyed my essay, cheers.
            return !message.hasFlag(this);
        }

        if(countFlags(this, settings.depthIntoHistory, history) > settings.acceptableHistory)
            return false;

        return true;

    }

    public void loadSettingsFromConfig(FileConfiguration config) {
        setSettings(GeneralModuleSettings.fromConfigurationSection(config, getName()));
    }

    public String getFriendlyName() {
        return WordUtils.capitalizeFully(getName().replace("-", " ").replace("_", " "));
    }

}
