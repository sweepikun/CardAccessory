package cn.popcraft.cardaccessory.listener;

import cn.popcraft.cardaccessory.CardAccessorySystem;
import cn.popcraft.cardaccessory.manager.EffectManager;
import cn.popcraft.cardaccessory.manager.EffectProcessor;
import cn.popcraft.cardaccessory.model.EquipmentSlot;
import cn.popcraft.cardaccessory.model.PlayerEquipment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.lang.reflect.Method;

public class MythicDamageListener implements Listener {

    @EventHandler
    public void onMythicDamage(org.bukkit.event.Event event) {
        // 只处理MythicDamageEvent
        if (!event.getEventName().equals("MythicDamageEvent")) {
            return;
        }

        if (!CardAccessorySystem.getInstance().isMythicMobsEnabled()) {
            return;
        }

        try {
            // 使用反射获取攻击者
            Method getCasterMethod = event.getClass().getMethod("getCaster");
            Object caster = getCasterMethod.invoke(event);

            Method getEntityMethod = caster.getClass().getMethod("getEntity");
            Object abstractEntity = getEntityMethod.invoke(caster);

            Method getBukkitEntityMethod = abstractEntity.getClass().getMethod("getBukkitEntity");
            Object bukkitEntity = getBukkitEntityMethod.invoke(abstractEntity);

            if (!(bukkitEntity instanceof Player)) {
                return;
            }

            Player player = (Player) bukkitEntity;

            // 获取并应用基础伤害加成
            double multiplier = EffectManager.getAccessoryMultiplier(player);

            Method getDamageMethod = event.getClass().getMethod("getDamage");
            Method setDamageMethod = event.getClass().getMethod("setDamage", double.class);
            double damage = (double) getDamageMethod.invoke(event);

            if (multiplier > 1.0) {
                setDamageMethod.invoke(event, damage * multiplier);
            }

            // 处理饰品效果
            processAccessoryEffects(player, event);
        } catch (Exception e) {
            // 反射调用失败，静默忽略
        }
    }

    private void processAccessoryEffects(Player player, org.bukkit.event.Event event) {
        PlayerEquipment equipment = CardAccessorySystem.getInstance()
            .getEquipManager().getPlayerEquipment(player);

        for (int i = 0; i < 2; i++) {
            EquipmentSlot accessorySlot = equipment.getAccessory(i);
            if (accessorySlot != null && !accessorySlot.isEmpty()) {
                String accessoryId = accessorySlot.getId();
                var accessory = CardAccessorySystem.getInstance()
                    .getItemManager().getAccessory(accessoryId);
                if (accessory != null && accessory.getEffects() != null && !accessory.getEffects().isEmpty()) {
                    EffectProcessor.processMythicDamageEffects(event, accessory.getEffects());
                }
            }
        }
    }
}
