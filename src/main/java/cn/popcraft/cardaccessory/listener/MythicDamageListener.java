package cn.popcraft.cardaccessory.listener;

import cn.popcraft.cardaccessory.CardAccessorySystem;
import cn.popcraft.cardaccessory.manager.EffectManager;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.bukkit.events.MythicDamageEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MythicDamageListener implements Listener {
    
    @EventHandler
    public void onMythicDamage(MythicDamageEvent event) {
        // 检查MythicMobs是否启用
        if (!CardAccessorySystem.getInstance().isMythicMobsEnabled()) {
            return;
        }
        
        // 检查攻击者是否为玩家
        AbstractEntity attacker = event.getCaster().getEntity();
        if (!(attacker.getBukkitEntity() instanceof Player)) {
            return;
        }
        
        Player player = (Player) attacker.getBukkitEntity();
        
        // 获取饰品提供的技能伤害加成
        double multiplier = EffectManager.getAccessoryMultiplier(player);
        
        // 应用伤害加成（仅当有加成时）
        if (multiplier > 1.0) {
            event.setDamage(event.getDamage() * multiplier);
        }
    }
}