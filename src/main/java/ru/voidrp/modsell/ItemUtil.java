package ru.voidrp.modsell;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

public final class ItemUtil {

    private ItemUtil() {}

    public static String getModId(ItemStack stack) {
        if (stack == null || stack.getType() == Material.AIR) return null;
        // On Mohist, NeoForge items have their namespace:id as the NamespacedKey
        String key = stack.getType().getKey().toString();
        return key.toLowerCase(Locale.ROOT);
    }

    public static int countInInventory(org.bukkit.entity.Player player, String modId) {
        int total = 0;
        for (ItemStack stack : player.getInventory().getContents()) {
            if (modId.equalsIgnoreCase(getModId(stack))) {
                total += stack.getAmount();
            }
        }
        return total;
    }

    public static int removeFromInventory(org.bukkit.entity.Player player, String modId, int amount) {
        int remaining = amount;
        ItemStack[] contents = player.getInventory().getContents();
        for (int i = 0; i < contents.length && remaining > 0; i++) {
            ItemStack stack = contents[i];
            if (!modId.equalsIgnoreCase(getModId(stack))) continue;
            int sz = stack.getAmount();
            if (sz <= remaining) {
                remaining -= sz;
                player.getInventory().setItem(i, null);
            } else {
                stack.setAmount(sz - remaining);
                remaining = 0;
            }
        }
        return amount - remaining; // actually removed
    }
}
