package ru.voidrp.modsell;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class MSellInfoCommand implements CommandExecutor {

    private final ModSellPlugin plugin;

    public MSellInfoCommand(ModSellPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cКоманда только для игроков.");
            return true;
        }

        ItemStack hand = player.getInventory().getItemInMainHand();
        String modId = ItemUtil.getModId(hand);
        if (modId == null) {
            player.sendMessage("§cВозьмите предмет в руку.");
            return true;
        }

        SellConfig.Entry entry = plugin.getPriceService().getEntry(modId);
        double price = plugin.getPriceService().getSellPrice(modId);

        player.sendMessage("§8ID: §7" + modId);
        if (entry != null) {
            player.sendMessage("§8Название: §f" + entry.name());
        }
        if (price > 0) {
            player.sendMessage("§8Цена продажи: §e" + plugin.getPriceService().formatMoney(price) + " §8за шт.");
            int count = ItemUtil.countInInventory(player, modId);
            player.sendMessage("§8В инвентаре: §f" + count + " §8шт. → §e"
                    + plugin.getPriceService().formatMoney(price * count));
        } else {
            player.sendMessage("§cЭтот предмет нельзя продать через /modsell.");
        }
        return true;
    }
}
