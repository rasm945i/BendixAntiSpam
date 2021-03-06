# BendixAntiSpam
A Minecraft Plugin with various methods of detect spam such as duplicate messages, repetitive characters and flooding

## Made with developers in mind
The whole plugin is based on modules, which allows developers to easily add extra functionality if they wish.

## Default modules
Note that the default configuration is very forgiving and is meant to only punish the worst offenders.  
That way, regular players wont stumble upon the anti-spam when doing a single outburst.
#### Caps Module
Prevents the use of excessive caps-lock.
#### Duplication Module
Prevents duplicate and similar messages
#### Character Module
Prevents users sending a message with the same character many times in a row, like "Hiiiiiiii"
#### Flood Module
Prevents a user from sending messages too fast

## Very configurable
Each module provided by default can be configured, disabled and tweaked to your needs.  
Third-party modules will have to implement that by themselves, but I have made it as easy as I can for them.

## Commands and permissions
antispam.info  
/antispam - Displays information on loaded modules

antispam.reload  
/antispam reload - Reloads all modules.  
Note that enabling or disabling modules must be done directly from the config file and requires a restart.

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
ChatModule.ReloadMethod reloadMethod = (module) -> {
            // It is recommended to make a system that only reloads the given config
            // max once a second, unless each module has its own dedicated config
            yourPlugin.reloadConfig(); // Assuming you use the default config supplied from the JavaPlugin class
            
            // IMPORTANT! Make sure to provide the new, reloaded, instance of the config!
            module.loadSettingsFromConfig(getConfig());
        };
blockModule.setReloadMethod(reloadMethod);
```
