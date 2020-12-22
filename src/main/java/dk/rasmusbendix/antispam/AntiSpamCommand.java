package dk.rasmusbendix.antispam;

import dk.rasmusbendix.antispam.modules.ChatModule;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;

public class AntiSpamCommand implements CommandExecutor {

    private final AntiSpam plugin;

    public AntiSpamCommand(AntiSpam plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!sender.hasPermission("antispam.info")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to do that.");
            return true;
        }


        if(args.length == 1 && args[0].equalsIgnoreCase("reload")) {

            if(sender.hasPermission("antispam.reload")) {

                int reloads = 0;
                // Enabling and disabling modules requires a restart
                for (ChatModule module : AntiSpam.getChatListener().getChatModules()) {

                    if(module.getReloadMethod() != null) {
                        module.getReloadMethod().onReload(module, module.getAssociatedConfig());
                        reloads++;
                    }

                }

                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&7Reloaded &e" + reloads + "&7 chat-modules!"));
                return true;
            } else {
                sender.sendMessage(ChatColor.RED + "You don't have permission to do that.");
                return true;
            }
        }


        // Print enabled modules settings by default, not module-specific ones tho, as that would be tedious
        StringBuilder builder = new StringBuilder();
        for(ChatModule module : AntiSpam.getChatListener().getChatModules()) {
            builder.append("\n\n&7 - - &e");
            builder.append(module.getFriendlyName());
            builder.append("&7 - -&r");

            builder.append("\n&7History-check: &e");
            builder.append(module.getSettings().isCheckingHistory());
            // Reset color so the console don't look weird on new lines
            builder.append("&r");

            builder.append("\n&7History depth: &e");
            builder.append(module.getSettings().getDepthIntoHistory());
            builder.append("&r");

            builder.append("\n&7Max acceptable history matches: &e");
            builder.append(module.getSettings().getAcceptableHistory());
            builder.append("&r");

            builder.append("\n&7Violation message: &e");
            builder.append(module.getSettings().getViolationMessage());
            builder.append("&r");

        }

        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                builder.toString()));

        return true;

    }
}
