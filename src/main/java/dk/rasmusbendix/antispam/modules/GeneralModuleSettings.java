package dk.rasmusbendix.antispam.modules;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.file.FileConfiguration;

@AllArgsConstructor
public class GeneralModuleSettings {

    // How deep into the message history the module goes
    // E.g. rather than checking only current message, it checks if X previous messages was flagged for the same thing
    @Getter @Setter protected boolean checkingHistory;
    // 3rd entry gets blocked
    @Getter @Setter protected int acceptableHistory;
    // How deep into the history to look. If 10, only checks flags for 10 most recent
    // messages and compares that amount to acceptableHistory
    // E.g. if this is 20, the past 30 messages can contain 2 flagged messages, the 3rd gets blocked.
    @Getter @Setter protected int depthIntoHistory;
    @Getter @Setter protected String violationMessage;

    public GeneralModuleSettings(boolean checkingHistory, int acceptableHistory, int depthIntoHistory) {
        this(checkingHistory, acceptableHistory, depthIntoHistory, "Please refrain from spamming.");
    }

    private GeneralModuleSettings() {}

    public static GeneralModuleSettings fromConfigurationSection(FileConfiguration config, String section) {

        GeneralModuleSettings settings = new GeneralModuleSettings();
        if(!config.contains(section + ".general")) {
            // Default, crappy config
            return new GeneralModuleSettings(false, 0, 0);
        }

        String path = section + ".general.";

        settings.setCheckingHistory(config.getBoolean(path + "enable-history-check", false));
        settings.setDepthIntoHistory(config.getInt(path + "history-depth", 4));
        settings.setAcceptableHistory(config.getInt(path + "max-matches-in-history", 1));
        settings.setViolationMessage(getViolationMessage(config, section));

        return settings;

    }

    @SuppressWarnings("ConstantConditions")
    private static String getViolationMessage(FileConfiguration config, String section) {

        // Check if a violation message is set
        if(config.contains(section + ".general.violation-message")) {
            // The message is set
            String msg = config.getString(section + ".general.violation-message");
            // If the message is not empty, use that one
            if(!msg.isEmpty())
                return msg;
        }

        // Fallback to the default message
        return config.getString("default-violation-message", "Please do not spam.");

    }

}
