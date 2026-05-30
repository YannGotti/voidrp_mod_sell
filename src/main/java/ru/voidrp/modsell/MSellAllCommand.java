package ru.voidrp.modsell;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public final class MSellAllCommand implements CommandExecutor {

    private final ModSellPlugin plugin;

    public MSellAllCommand(ModSellPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cКоманда только для игроков.");
            return true;
        }

        // Collect unique mod IDs in inventory and their counts
        Map<String, Integer> found = new HashMap<>();
        for (ItemStack stack : player.getInventory().getContents()) {
            String id = ItemUtil.getModId(stack);
            if (id == null) continue;
            if (plugin.getPriceService().getSellPrice(id) <= 0) continue;
            found.merge(id, stack.getAmount(), Integer::sum);
        }

        if (found.isEmpty()) {
            player.sendMessage("§cВ инвентаре нет продаваемых модовых предметов.");
            return true;
        }

        double totalEarned = 0;
        int totalItems = 0;
        for (Map.Entry<String, Integer> e : found.entrySet()) {
            String id = e.getKey();
            int count = e.getValue();
            double price = plugin.getPriceService().getSellPrice(id);
            int removed = ItemUtil.removeFromInventory(player, id, count);
            double earned = Math.round(price * removed * 100.0) / 100.0;
            plugin.getEconomy().depositPlayer(player, earned);
            totalEarned += earned;
            totalItems += removed;
        }

        player.sendMessage("§aПродано §e" + totalItems + " §aпредметов за §e"
                + plugin.getPriceService().formatMoney(totalEarned));
        return true;
    }
}
