package cn.popcraft.cardaccessory.hook;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultHook {
    private static boolean enabled = false;
    private static Economy economy = null;
    
    public static void init() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("Vault");
        enabled = plugin != null && plugin.isEnabled();
        
        if (enabled) {
            try {
                RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
                if (rsp != null) {
                    economy = rsp.getProvider();
                }
                if (economy != null) {
                    Bukkit.getLogger().info("[CardAccessorySystem] Vault detected, enabling economy-based upgrades");
                } else {
                    enabled = false;
                    Bukkit.getLogger().info("[CardAccessorySystem] Vault found but no economy plugin detected");
                }
            } catch (Exception e) {
                enabled = false;
                Bukkit.getLogger().warning("[CardAccessorySystem] Failed to hook into Vault: " + e.getMessage());
            }
        } else {
            Bukkit.getLogger().info("[CardAccessorySystem] Vault not found, economy-based upgrades disabled");
        }
    }
    
    public static boolean isEnabled() {
        return enabled && economy != null;
    }
    
    public static double getPlayerBalance(org.bukkit.entity.Player player) {
        if (!enabled || economy == null) {
            return -1; // 表示不可用
        }
        
        try {
            return economy.getBalance(player);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
    
    public static boolean takePlayerMoney(org.bukkit.entity.Player player, double amount) {
        if (!enabled || economy == null) {
            return false;
        }
        
        try {
            return economy.withdrawPlayer(player, amount).transactionSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean hasPlayerMoney(org.bukkit.entity.Player player, double amount) {
        if (!enabled || economy == null) {
            return false;
        }
        
        try {
            return economy.has(player, amount);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static String getCurrencyName() {
        if (!enabled || economy == null) {
            return "";
        }
        
        return economy.currencyNamePlural();
    }
}