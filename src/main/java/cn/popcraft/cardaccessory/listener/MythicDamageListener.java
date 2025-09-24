package cn.popcraft.cardaccessory.listener;

import cn.popcraft.cardaccessory.CardAccessorySystem;
import cn.popcraft.cardaccessory.manager.EffectProcessor;
import cn.popcraft.cardaccessory.manager.EffectManager;
import cn.popcraft.cardaccessory.model.EquipmentSlot;
import cn.popcraft.cardaccessory.model.PlayerEquipment;
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
        
        // 获取饰品提供的技能伤害加成（保持向后兼容）
        double multiplier = EffectManager.getAccessoryMultiplier(player);
        
        // 应用伤害加成（仅当有加成时）
        if (multiplier > 1.0) {
            event.setDamage(event.getDamage() * multiplier);
        }
        
        // 处理饰品的其他效果
        processAccessoryEffects(player, event);
    }
    
    /**
     * 处理饰品的其他效果
     * @param player 玩家
     * @param event MythicDamage事件
     */
    private void processAccessoryEffects(Player player, MythicDamageEvent event) {
        PlayerEquipment equipment = CardAccessorySystem.getInstance()
            .getEquipManager().getPlayerEquipment(player);
        
        // 遍历所有饰品槽位并应用效果
        for (int i = 0; i < 2; i++) {
            EquipmentSlot accessorySlot = equipment.getAccessory(i);
            if (accessorySlot != null && !accessorySlot.isEmpty()) {
                String accessoryId = accessorySlot.getId();
                var accessory = CardAccessorySystem.getInstance()
                    .getItemManager().getAccessory(accessoryId);
                if (accessory != null && accessory.getEffects() != null) {
                    // 处理饰品效果
                    EffectProcessor.processMythicDamageEffects(event, accessory.getEffects());
                }
            }
        }
    }
}