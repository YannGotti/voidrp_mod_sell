package ru.voidrp.modsell;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public final class MSellReloadCommand implements CommandExecutor {

    private final ModSellPlugin plugin;

    public MSellReloadCommand(ModSellPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("voidrp.modsell.admin")) {
            sender.sendMessage("§cНет прав.");
            return true;
        }
        plugin.reload();
        sender.sendMessage("§aModSell конфиг перезагружен. Предметов: §f"
                + plugin.getSellConfig().all().size());
        return true;
    }
}
