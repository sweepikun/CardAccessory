package cn.popcraft.cardaccessory.manager;

import cn.popcraft.cardaccessory.CardAccessorySystem;
import cn.popcraft.cardaccessory.model.Accessory;
import cn.popcraft.cardaccessory.model.EquipmentSlot;
import cn.popcraft.cardaccessory.model.PlayerEquipment;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EffectManager {
    // 存储玩家属性修饰符的UUID，用于移除时使用
    private static final Map<UUID, Map<String, UUID>> playerAttributeModifiers = new HashMap<>();

    public static void applyCardEffects(Player player) {
        PlayerEquipment equipment = CardAccessorySystem.getInstance()
            .getEquipManager().getPlayerEquipment(player);
        
        // 遍历所有卡牌槽位并应用属性
        for (int i = 0; i < 4; i++) {
            EquipmentSlot cardSlot = equipment.getCard(i);
            if (cardSlot != null && !cardSlot.isEmpty()) {
                String cardId = cardSlot.getId();
                var card = CardAccessorySystem.getInstance().getItemManager().getCard(cardId);
                if (card != null) {
                    // 应用所有属性
                    card.getAttributes().forEach((attribute, value) -> 
                        applyBukkitAttribute(player, attribute, value)
                    );
                }
            }
        }
    }
    
    public static void removeCardEffects(Player player) {
        PlayerEquipment equipment = CardAccessorySystem.getInstance()
            .getEquipManager().getPlayerEquipment(player);
        
        // 遍历所有卡牌槽位并移除属性
        for (int i = 0; i < 4; i++) {
            EquipmentSlot cardSlot = equipment.getCard(i);
            if (cardSlot != null && !cardSlot.isEmpty()) {
                String cardId = cardSlot.getId();
                var card = CardAccessorySystem.getInstance().getItemManager().getCard(cardId);
                if (card != null) {
                    // 移除所有属性
                    card.getAttributes().forEach((attribute, value) -> 
                        removeBukkitAttribute(player, attribute, value)
                    );
                }
            }
        }
    }
    
    public static double getAccessoryMultiplier(Player player) {
        double multiplier = 1.0;
        
        PlayerEquipment equipment = CardAccessorySystem.getInstance()
            .getEquipManager().getPlayerEquipment(player);
        
        // 遍历所有饰品槽位并计算伤害加成
        for (int i = 0; i < 2; i++) {
            EquipmentSlot accessorySlot = equipment.getAccessory(i);
            if (accessorySlot != null && !accessorySlot.isEmpty()) {
                String accessoryId = accessorySlot.getId();
                var accessory = CardAccessorySystem.getInstance()
                    .getItemManager().getAccessory(accessoryId);
                if (accessory != null) {
                    multiplier *= accessory.getSkillDamageMultiplier();
                }
            }
        }
        
        return multiplier;
    }
    
    // Bukkit原生属性应用
    private static void applyBukkitAttribute(Player player, String attribute, double value) {
        try {
            AttributeInstance attributeInstance = null;
            String attributeKey = null;
            
            // 根据配置文件中的属性名映射到Bukkit属性
            switch (attribute.toUpperCase()) {
                case "MAX_HEALTH":
                    attributeInstance = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                    attributeKey = "MAX_HEALTH";
                    break;
                case "ATTACK_DAMAGE":
                    attributeInstance = player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
                    attributeKey = "ATTACK_DAMAGE";
                    break;
                case "MOVEMENT_SPEED":
                    attributeInstance = player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
                    attributeKey = "MOVEMENT_SPEED";
                    break;
                default:
                    // 不支持的属性
                    return;
            }
            
            if (attributeInstance != null) {
                // 为每个属性生成唯一的UUID
                UUID modifierId = UUID.randomUUID();
                AttributeModifier modifier = new AttributeModifier(
                    modifierId, 
                    "CardAccessory-" + attributeKey, 
                    value, 
                    AttributeModifier.Operation.ADD_NUMBER
                );
                
                // 存储修饰符UUID以便后续移除
                playerAttributeModifiers
                    .computeIfAbsent(player.getUniqueId(), k -> new HashMap<>())
                    .put(attributeKey, modifierId);
                
                // 添加修饰符
                attributeInstance.addModifier(modifier);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Bukkit原生属性移除
    private static void removeBukkitAttribute(Player player, String attribute, double value) {
        try {
            AttributeInstance attributeInstance = null;
            String attributeKey = null;
            
            // 根据配置文件中的属性名映射到Bukkit属性
            switch (attribute.toUpperCase()) {
                case "MAX_HEALTH":
                    attributeInstance = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                    attributeKey = "MAX_HEALTH";
                    break;
                case "ATTACK_DAMAGE":
                    attributeInstance = player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
                    attributeKey = "ATTACK_DAMAGE";
                    break;
                case "MOVEMENT_SPEED":
                    attributeInstance = player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
                    attributeKey = "MOVEMENT_SPEED";
                    break;
                default:
                    // 不支持的属性
                    return;
            }
            
            if (attributeInstance != null) {
                // 获取之前存储的修饰符UUID
                Map<String, UUID> modifiers = playerAttributeModifiers.get(player.getUniqueId());
                if (modifiers != null && modifiers.containsKey(attributeKey)) {
                    UUID modifierId = modifiers.get(attributeKey);
                    
                    // 通过查找修饰符对象来移除
                    AttributeModifier targetModifier = null;
                    for (AttributeModifier modifier : attributeInstance.getModifiers()) {
                        if (modifier.getUniqueId().equals(modifierId)) {
                            targetModifier = modifier;
                            break;
                        }
                    }
                    
                    if (targetModifier != null) {
                        // 移除修饰符
                        attributeInstance.removeModifier(targetModifier);
                    }
                    
                    // 从存储中移除该修饰符记录
                    modifiers.remove(attributeKey);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // 清除玩家的所有属性修饰符记录
    public static void clearPlayerModifiers(Player player) {
        playerAttributeModifiers.remove(player.getUniqueId());
    }
}