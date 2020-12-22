package dk.rasmusbendix.antispam;

import dk.rasmusbendix.antispam.modules.CapsModule;
import dk.rasmusbendix.antispam.modules.CharactersModule;
import dk.rasmusbendix.antispam.modules.DuplicateModule;
import dk.rasmusbendix.antispam.modules.FloodModule;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public final class AntiSpam extends JavaPlugin {

    @Getter private static ChatListener chatListener;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        chatListener = new ChatListener(this);
        if(isModuleEnabled(CapsModule.IDENTIFIER))
            registerCapsModule();

        if(isModuleEnabled(CharactersModule.IDENTIFIER))
            registerCharactersModule();

        if(isModuleEnabled(FloodModule.IDENTIFIER))
            registerFloodModule();

        if(isModuleEnabled(DuplicateModule.IDENTIFIER))
            registerDuplicateModule();

    }

    public boolean isModuleEnabled(String path) {
        return getConfig().getBoolean(path + ".enabled", false);
    }

    public void registerCapsModule() {
        CapsModule capsModule = new CapsModule(getConfig());
        chatListener.registerChatModule(capsModule);
    }

    public void registerCharactersModule() {
        CharactersModule charsModule = new CharactersModule(getConfig());
        chatListener.registerChatModule(charsModule);
    }

    public void registerFloodModule() {
        FloodModule floodModule = new FloodModule(getConfig());
        chatListener.registerChatModule(floodModule);
    }

    public void registerDuplicateModule() {
        DuplicateModule duplicateModule = new DuplicateModule(getConfig());
        chatListener.registerChatModule(duplicateModule);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
