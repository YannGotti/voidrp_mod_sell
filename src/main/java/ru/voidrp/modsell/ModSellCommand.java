package ru.voidrp.modsell;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class ModSellCommand implements CommandExecutor {

    private final ModSellPlugin plugin;

    public ModSellCommand(ModSellPlugin plugin) {
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

        double price = plugin.getPriceService().getSellPrice(modId);
        if (price <= 0) {
            player.sendMessage("§cЭтот предмет нельзя продать: §8" + modId);
            return true;
        }

        int inHand = hand.getAmount();
        int requested = inHand;
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("all") || args[0].equals("*")) {
                requested = ItemUtil.countInInventory(player, modId);
            } else {
                try {
                    requested = Integer.parseInt(args[0]);
                } catch (NumberFormatException e) {
                    player.sendMessage("§cНеверное количество: §f" + args[0]);
                    return true;
                }
            }
        }

        if (requested <= 0) {
            player.sendMessage("§cНет предметов для продажи.");
            return true;
        }

        int available = ItemUtil.countInInventory(player, modId);
        int toSell = Math.min(requested, available);
        if (toSell <= 0) {
            player.sendMessage("§cУ вас нет §e" + modId + " §cдля продажи.");
            return true;
        }

        int removed = ItemUtil.removeFromInventory(player, modId, toSell);
        double total = Math.round(price * removed * 100.0) / 100.0;
        plugin.getEconomy().depositPlayer(player, total);

        SellConfig.Entry entry = plugin.getPriceService().getEntry(modId);
        String name = entry != null ? entry.name() : modId;
        player.sendMessage("§aПродано: §f" + name + " §ex" + removed
                + " §8за §e" + plugin.getPriceService().formatMoney(total));

        return true;
    }
}
