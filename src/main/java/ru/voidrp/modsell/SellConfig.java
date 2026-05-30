package ru.voidrp.modsell;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class SellConfig {

    public record Entry(String modId, String name, double sellPrice) {}

    private final Map<String, Entry> byId;

    public SellConfig(FileConfiguration cfg) {
        Map<String, Entry> map = new HashMap<>();
        ConfigurationSection items = cfg.getConfigurationSection("items");
        if (items != null) {
            for (String key : items.getKeys(false)) {
                String id = key.toLowerCase();
                double price = items.getDouble(key + ".price", 0);
                String name = items.getString(key + ".name", id);
                if (price > 0) {
                    map.put(id, new Entry(id, name, price));
                }
            }
        }
        this.byId = Collections.unmodifiableMap(map);
    }

    public Entry get(String modId) {
        return modId == null ? null : byId.get(modId.toLowerCase());
    }

    public Map<String, Entry> all() {
        return byId;
    }
}
