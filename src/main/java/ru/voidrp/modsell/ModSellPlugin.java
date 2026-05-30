package ru.voidrp.modsell;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class ModSellPlugin extends JavaPlugin {

    private Economy economy;
    private SellConfig sellConfig;
    private PriceService priceService;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        sellConfig = new SellConfig(getConfig());
        priceService = new PriceService(this, sellConfig);

        if (!setupEconomy()) {
            getLogger().severe("Vault economy not found! Disabling.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getCommand("modsell").setExecutor(new ModSellCommand(this));
        getCommand("msellall").setExecutor(new MSellAllCommand(this));
        getCommand("msellinfo").setExecutor(new MSellInfoCommand(this));
        getCommand("msellreload").setExecutor(new MSellReloadCommand(this));
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) return false;
        RegisteredServiceProvider<Economy> rsp =
                getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return false;
        economy = rsp.getProvider();
        return true;
    }

    public Economy getEconomy() { return economy; }
    public SellConfig getSellConfig() { return sellConfig; }
    public PriceService getPriceService() { return priceService; }

    public void reload() {
        reloadConfig();
        sellConfig = new SellConfig(getConfig());
        priceService = new PriceService(this, sellConfig);
    }
}
