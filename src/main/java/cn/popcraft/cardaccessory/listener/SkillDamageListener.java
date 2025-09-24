package cn.popcraft.cardaccessory.listener;

import cn.popcraft.cardaccessory.CardAccessorySystem;
import cn.popcraft.cardaccessory.manager.EffectManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class SkillDamageListener implements Listener {
    
    // Bukkit原生伤害事件作为备选方案
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        // 如果MythicMobs可用，优先使用MythicDamageEvent
        if (CardAccessorySystem.getInstance().isMythicMobsEnabled()) {
            return;
        }
        
        // 检查是否为玩家造成的伤害
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getDamager();
        
        // 获取饰品提供的技能伤害加成
        double multiplier = EffectManager.getAccessoryMultiplier(player);
        
        // 应用伤害加成（仅当有加成时）
        if (multiplier > 1.0) {
            event.setDamage(event.getDamage() * multiplier);
        }
    }
}