package dk.rasmusbendix.antispam;

import dk.rasmusbendix.antispam.modules.*;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public final class AntiSpam extends JavaPlugin {

    @Getter private static ChatListener chatListener;
    private ChatModule.ReloadMethod reloadMethod;
    private static long lastConfigReload;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        lastConfigReload = System.currentTimeMillis();

        reloadMethod = (module, config) -> {
            // Only reload the config once a second at max
            // Multiple modules use the same config
            if(lastConfigReload + 1000 < System.currentTimeMillis())
                reloadConfig();
            module.loadSettingsFromConfig(config);
        };

        chatListener = new ChatListener(this);
        if(isModuleEnabled(CapsModule.IDENTIFIER))
            registerCapsModule();

        if(isModuleEnabled(CharactersModule.IDENTIFIER))
            registerCharactersModule();

        if(isModuleEnabled(FloodModule.IDENTIFIER))
            registerFloodModule();

        if(isModuleEnabled(DuplicateModule.IDENTIFIER))
            registerDuplicateModule();

        //noinspection ConstantConditions
        getServer().getPluginCommand("antispam").setExecutor(new AntiSpamCommand(this));

    }

    public boolean isModuleEnabled(String path) {
        return getConfig().getBoolean(path + ".enabled", false);
    }

    public void registerCapsModule() {
        CapsModule capsModule = new CapsModule(getConfig());
        capsModule.setReloadMethod(reloadMethod);
        chatListener.registerChatModule(capsModule);
    }

    public void registerCharactersModule() {
        CharactersModule charsModule = new CharactersModule(getConfig());
        charsModule.setReloadMethod(reloadMethod);
        chatListener.registerChatModule(charsModule);
    }

    public void registerFloodModule() {
        FloodModule floodModule = new FloodModule(getConfig());
        floodModule.setReloadMethod(reloadMethod);
        chatListener.registerChatModule(floodModule);
    }

    public void registerDuplicateModule() {
        DuplicateModule duplicateModule = new DuplicateModule(getConfig());
        duplicateModule.setReloadMethod(reloadMethod);
        chatListener.registerChatModule(duplicateModule);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
