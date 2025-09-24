package cn.popcraft.cardaccessory.hook;

import cn.popcraft.cardaccessory.CardAccessorySystem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;

public class MythicClassHook {
    private static boolean enabled = false;
    private static Class<?> mythicPlayerClass = null;
    private static Method getProfessionMethod = null;
    private static Class<?> professionClass = null;
    private static Method getNameMethod = null;
    
    public static void init() {
        // 检查MythicMobs是否启用
        if (!CardAccessorySystem.getInstance().isMythicMobsEnabled()) {
            Bukkit.getLogger().info("[CardAccessorySystem] MythicMobs not enabled, class restrictions disabled");
            return;
        }
        
        try {
            // 尝试加载MythicLib的类
            mythicPlayerClass = Class.forName("io.lumine.mythic.lib.api.player.MMOPlayer");
            getProfessionMethod = mythicPlayerClass.getMethod("getProfession");
            professionClass = Class.forName("io.lumine.mythic.lib.api.player.profiling.PlayerProfile");
            getNameMethod = professionClass.getMethod("getName");
            
            enabled = true;
            Bukkit.getLogger().info("[CardAccessorySystem] MythicLib detected, enabling class restrictions");
        } catch (Exception e) {
            enabled = false;
            Bukkit.getLogger().info("[CardAccessorySystem] MythicLib not found, class restrictions disabled");
        }
    }
    
    public static boolean isEnabled() {
        return enabled;
    }
    
    public static String getPlayerClass(Player player) {
        if (!enabled || mythicPlayerClass == null) {
            return ""; // 空字符串表示无限制
        }
        
        try {
            Object mmoPlayer = mythicPlayerClass.getMethod("get", Player.class).invoke(null, player);
            if (mmoPlayer != null) {
                Object profession = getProfessionMethod.invoke(mmoPlayer);
                if (profession != null) {
                    String className = (String) getNameMethod.invoke(profession);
                    return className != null ? className.toLowerCase() : "";
                }
            }
        } catch (Exception e) {
            // 如果出现异常，返回空字符串表示无限制
        }
        
        return ""; // 无职业或出现异常
    }
    
    public static boolean checkPlayerClass(Player player, String requiredClass) {
        // 如果没有设置职业限制，则通过检查
        if (requiredClass == null || requiredClass.isEmpty()) {
            return true;
        }
        
        // 如果MythicLib不可用，则忽略职业限制
        if (!enabled) {
            return true;
        }
        
        String playerClass = getPlayerClass(player);
        // 如果玩家没有职业，但设置了职业限制，则不通过
        if (playerClass == null || playerClass.isEmpty()) {
            return false;
        }
        
        // 比较职业（不区分大小写）
        return playerClass.equalsIgnoreCase(requiredClass);
    }
}