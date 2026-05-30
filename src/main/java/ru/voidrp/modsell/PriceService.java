package ru.voidrp.modsell;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;

public final class PriceService {

    private final ModSellPlugin plugin;
    private final SellConfig config;

    public PriceService(ModSellPlugin plugin, SellConfig config) {
        this.plugin = plugin;
        this.config = config;
    }

    /**
     * Returns current sell price for a mod item ID.
     * Checks VoidRpGameSync EconomyMarketCache first; falls back to config.yml base price.
     * Returns 0 if item is not registered as sellable.
     */
    public double getSellPrice(String modId) {
        if (modId == null) return 0;
        String id = modId.toLowerCase();

        // Try dynamic price from VoidRpGameSync
        double dynamic = getDynamicSellPrice(id);
        if (dynamic > 0) return dynamic;

        // Fall back to base price from config
        SellConfig.Entry entry = config.get(id);
        return entry != null ? entry.sellPrice() : 0;
    }

    public SellConfig.Entry getEntry(String modId) {
        return config.get(modId);
    }

    private double getDynamicSellPrice(String modId) {
        try {
            Plugin gsp = Bukkit.getPluginManager().getPlugin("VoidRpGameSync");
            if (gsp == null || !gsp.isEnabled()) return 0;

            // Get EconomyMarketCache via reflection to avoid compile-time coupling
            Method getCacheMethod = gsp.getClass().getMethod("getEconomyMarketCache");
            Object cache = getCacheMethod.invoke(gsp);
            if (cache == null) return 0;

            Method getMethod = cache.getClass().getMethod("get", String.class);
            Object item = getMethod.invoke(cache, modId.toUpperCase());
            if (item == null) return 0;

            Method sellPrice = item.getClass().getMethod("marketSellPrice");
            return (double) sellPrice.invoke(item);
        } catch (Exception ignored) {
            return 0;
        }
    }

    public String formatMoney(double amount) {
        return String.format("%.0f₽", amount);
    }
}
