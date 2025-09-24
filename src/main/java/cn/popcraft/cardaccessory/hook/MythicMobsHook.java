package cn.popcraft.cardaccessory.hook;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class MythicMobsHook {
    private static boolean enabled = false;
    private static Plugin mythicMobsPlugin = null;
    
    public static void init() {
        mythicMobsPlugin = Bukkit.getPluginManager().getPlugin("MythicMobs");
        enabled = mythicMobsPlugin != null && mythicMobsPlugin.isEnabled();
        
        if (enabled) {
            Bukkit.getLogger().info("[CardAccessorySystem] MythicMobs detected, enabling skill damage support");
        } else {
            Bukkit.getLogger().info("[CardAccessorySystem] MythicMobs not found, skill damage support disabled");
        }
    }
    
    public static boolean isEnabled() {
        return enabled;
    }
}