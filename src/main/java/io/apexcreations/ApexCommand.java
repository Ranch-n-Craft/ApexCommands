package io.apexcreations;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class ApexCommand extends Command {

    private final Map<String, ApexSubCommand> subCommandCache = new HashMap<>();
    private final boolean playerOnly;

    public ApexCommand(String name) {
        this(name, false);
    }

    public ApexCommand(String name, boolean playerOnly) {
        this(name, "Unknown Description", "", playerOnly);
    }

    public ApexCommand(String name, String description, String permission,
            boolean playerOnly, String... aliases) {
        super(name);
        this.setDescription(description);
        this.setPermission(permission);
        this.playerOnly = playerOnly;
        this.setAliases(Arrays.asList(aliases));
        this.setPermissionMessage(ChatColor.RED + "You do not have permission for this command!");
    }

    @Override
    public boolean execute(CommandSender commandSender, String label, String[] args) {
        if (!(commandSender instanceof Player) && this.playerOnly) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    this.getPermissionMessage()));
            return false;
        }
        if (!commandSender.hasPermission(getPermission())) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    this.getPermissionMessage()));
            return true;
        }

        if (args.length > 0) {
            Optional<ApexSubCommand> optionalSubCommand = this.getSubCommand(args[0]);
            optionalSubCommand.ifPresent(subCommand -> {
                if (!commandSender.hasPermission(subCommand.getPermission())) {
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            getPermissionMessage()));
                    return;
                }
                subCommand.execute(commandSender, args);
            });
            return true;
        }
        return executeCommand(commandSender, label, args);
    }

    public abstract boolean executeCommand(CommandSender commandSender, String label,
            String[] args);

    private Optional<ApexSubCommand> getSubCommand(String key) {
        return Optional.ofNullable(getSubCommandCache().get(key));
    }

    public void registerSubCommand(ApexSubCommand... apexSubCommands) {
        for (ApexSubCommand apexSubCommand : apexSubCommands) {
            getSubCommandCache().put(apexSubCommand.getName().toLowerCase(), apexSubCommand);
        }
    }

    public boolean isSubCommand(String key) {
        return getSubCommandCache().containsKey(key);
    }

    public Map<String, ApexSubCommand> getSubCommandCache() {
        return this.subCommandCache;
    }
}