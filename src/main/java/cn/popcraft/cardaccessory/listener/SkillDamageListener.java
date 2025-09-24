package cn.popcraft.cardaccessory.listener;

import cn.popcraft.cardaccessory.CardAccessorySystem;
import cn.popcraft.cardaccessory.manager.EffectManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.lang.reflect.Method;

public class SkillDamageListener implements Listener {
    private boolean attributePlusEnabled = false;
    private Method getAttackerMethod = null;
    private Class<?> attrEntityAttackEventClass = null;
    
    public SkillDamageListener() {
        // 尝试加载AttributePlus的AttrEntityAttackEvent
        try {
            attrEntityAttackEventClass = Class.forName("com.mcstarrysky.attrplus.api.event.AttrEntityAttackEvent");
            getAttackerMethod = attrEntityAttackEventClass.getMethod("getAttacker");
            attributePlusEnabled = true;
        } catch (Exception e) {
            attributePlusEnabled = false;
        }
    }
    
    // 监听AttributePlus的技能攻击事件
    @EventHandler
    public void onAttrEntityAttack(Object event) {
        if (!attributePlusEnabled || !attrEntityAttackEventClass.isInstance(event)) {
            return;
        }
        
        try {
            // 获取攻击者
            Player player = (Player) getAttackerMethod.invoke(event);
            
            // 获取饰品提供的技能伤害加成
            double multiplier = EffectManager.getAccessoryMultiplier(player);
            
            // 应用伤害加成（仅当有加成时）
            if (multiplier > 1.0) {
                // 注意：这里需要根据AttributePlus的具体API来修改伤害
                // 由于我们没有确切的API文档，暂时无法实现
                // TODO: 实现AttributePlus技能伤害加成
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Bukkit原生伤害事件作为备选方案
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        // 如果MythicMobs可用，优先使用MythicDamageEvent
        if (CardAccessorySystem.getInstance().isMythicMobsEnabled()) {
            return;
        }
        
        // 如果AttributePlus可用，优先使用AttributePlus事件
        if (attributePlusEnabled) {
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