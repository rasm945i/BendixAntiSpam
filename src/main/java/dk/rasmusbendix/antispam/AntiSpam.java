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
        chatListener = new ChatListener(this);

        /* Register Caps Module */
        // 0.6 -> A bit more than half of the message can be caps
        // Allow sponge-bob meme text
        CapsModule capsModule = new CapsModule(3, 0.6);
        capsModule.setCheckingHistory(true);
        capsModule.setDepthIntoHistory(4);
        capsModule.setAcceptableHistory(1);
        chatListener.registerChatModule(capsModule);


        // Max 10 identical characters in a row, Weeeeeeeee
        CharactersModule charsModule = new CharactersModule(10);
        charsModule.setCheckingHistory(true);
        charsModule.setDepthIntoHistory(10);
        charsModule.setAcceptableHistory(2);
        chatListener.registerChatModule(charsModule);


        // Sometimes one can send a quick follow-up message
        // 650ms between each message
        FloodModule floodModule = new FloodModule(650);
        chatListener.registerChatModule(floodModule);


        // Low history-value to reduce false-positives.
        // Someone can reply "ok" to a message and have it be an answer to a second message too
        DuplicateModule duplicateModule = new DuplicateModule(1, 20);
        duplicateModule.setCheckingHistory(true);
        duplicateModule.setDepthIntoHistory(3);
        duplicateModule.setAcceptableHistory(1);
        chatListener.registerChatModule(duplicateModule);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
