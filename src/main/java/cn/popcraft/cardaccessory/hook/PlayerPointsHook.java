package cn.popcraft.cardaccessory.hook;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class PlayerPointsHook {
    private static boolean enabled = false;
    private static PlayerPoints playerPointsPlugin = null;
    private static PlayerPointsAPI api = null;
    
    public static void init() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("PlayerPoints");
        enabled = plugin != null && plugin.isEnabled();
        
        if (enabled) {
            try {
                playerPointsPlugin = (PlayerPoints) plugin;
                api = playerPointsPlugin.getAPI();
                Bukkit.getLogger().info("[CardAccessorySystem] PlayerPoints detected, enabling point-based upgrades");
            } catch (Exception e) {
                enabled = false;
                Bukkit.getLogger().warning("[CardAccessorySystem] Failed to hook into PlayerPoints: " + e.getMessage());
            }
        } else {
            Bukkit.getLogger().info("[CardAccessorySystem] PlayerPoints not found, point-based upgrades disabled");
        }
    }
    
    public static boolean isEnabled() {
        return enabled;
    }
    
    public static int getPlayerPoints(Player player) {
        if (!enabled || api == null) {
            return -1; // 表示不可用
        }
        
        try {
            return api.look(player.getUniqueId());
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
    
    public static boolean takePlayerPoints(Player player, int amount) {
        if (!enabled || api == null) {
            return false;
        }
        
        try {
            return api.take(player.getUniqueId(), amount);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean hasPlayerPoints(Player player, int amount) {
        if (!enabled) {
            return false;
        }
        
        return getPlayerPoints(player) >= amount;
    }
}