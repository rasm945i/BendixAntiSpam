# BendixAntiSpam
A Minecraft Plugin with various methods of detect spam such as duplicate messages, repetitive characters and flooding

## Made with developers in mind
The whole plugin is based on modules, which allows developers to easily add extra functionality if they wish.

## Module example
A simple module that would block every message longer than X characters would look something like this:
```java
package dk.rasmusbendix.antispam.modules;

import dk.rasmusbendix.antispam.Message;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;

public class BlockModule extends ChatModule {

    // This is the identifier of this module, which becomes the module name
    // A modules name is used as the path in the config file provided when loading settings
    public static final String IDENTIFIER = "block-module";
    
    private int maxLength;

    // If you don't care much about configuring your module from a config file, this approach is good
    public BlockModule(int maxLength) {
        super(IDENTIFIER);
        this.maxLength = maxLength;
    }
    
    // Recommended constructor
    // Pass in your own config file which is filled out similarly to the one used in this base-plugin
    public BlockModule(FileConfiguration config) {
        super(IDENTIFIER);
        loadSettingsFromConfig(config);
    }

    @Override
    public boolean allowChatEvent(Message message, ArrayList<Message> history) {
    
        // Check if the message is longer than we permit it to be
        if(message.getContent().length() > maxLength) {
            // Flag the message, in case we want to check how often previous messages was flagged
            // Useful for checking if the user has a history of using caps lock and such
            message.flag(this);
        }
        
        // Very easy implementation of history-checking
        // If history-checking is disabled, and the message is flagged, it simply returns false and blocks the message
        // If history-checking is disabled, and the message is not flagged, it returns true and allows the message to pass
        return hasAcceptableHistory(message, history);
        
    }

    // Not required to override, but greatly recommended if you have custom options for your module
    @Override
    public void loadSettingsFromConfig(FileConfiguration config) {
        // Make sure to load general settings, like if its enabled and checks history
        super.loadSettingsFromConfig(config);
        // Load module-specific variables
        maxLength = config.getInt(getName() + ".max-length", 10);
    }

}

```

To register your module, do this:
```java
BlockModule blockModule = new BlockModule(10);
AntiSpam.getChatListener().registerModule(blockModule);
```

By default, the `/antispam reload` command does not reload third-party modules.
To have your module reload along with the built-in modules when the reload command is run, you must use the ReloadMethod interface.
If no ReloadMethod is defined, it will simply load the values from the config again, without the config having gotten any new values.
This interface is in place so you can reload your configuration file when it is neccessary.
```java
ChatModule.ReloadMethod reloadMethod = (module, config) -> {
            // It is recommended to make a system that only reloads the given config
            // max once a second, unless each module has its own dedicated config
            yourPlugin.reloadConfig(); // Assuming you use the default config supplied from the JavaPlugin class
            module.loadSettingsFromConfig(config);
        };
blockModule.setReloadMethod(reloadMethod);
```
