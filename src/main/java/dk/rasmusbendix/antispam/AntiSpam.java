package dk.rasmusbendix.antispam;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public final class AntiSpam extends JavaPlugin {

    @Getter ChatListener chatListener;

    @Override
    public void onEnable() {
        chatListener = new ChatListener(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
