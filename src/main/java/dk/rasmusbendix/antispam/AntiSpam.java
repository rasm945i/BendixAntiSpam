package dk.rasmusbendix.antispam;

import org.bukkit.plugin.java.JavaPlugin;

public final class AntiSpam extends JavaPlugin {

    @Override
    public void onEnable() {
        ChatListener listener = new ChatListener(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
