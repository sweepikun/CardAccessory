package cn.popcraft.cardaccessory.manager;

import cn.popcraft.cardaccessory.CardAccessorySystem;
import cn.popcraft.cardaccessory.model.EquipmentSlot;
import cn.popcraft.cardaccessory.model.PlayerEquipment;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;

import java.util.*;

public class EffectManager {
    // 使用确定性UUID：基于玩家UUID + 属性名生成，避免重复添加
    private static final Map<String, UUID> ATTRIBUTE_UUIDS = new HashMap<>();

    static {
        // 为每个属性预生成命名空间UUID
        ATTRIBUTE_UUIDS.put("MAX_HEALTH", UUID.nameUUIDFromBytes("CardAccessory-MAX_HEALTH".getBytes()));
        ATTRIBUTE_UUIDS.put("ATTACK_DAMAGE", UUID.nameUUIDFromBytes("CardAccessory-ATTACK_DAMAGE".getBytes()));
        ATTRIBUTE_UUIDS.put("MOVEMENT_SPEED", UUID.nameUUIDFromBytes("CardAccessory-MOVEMENT_SPEED".getBytes()));
    }

    public static void applyCardEffects(Player player) {
        PlayerEquipment equipment = CardAccessorySystem.getInstance()
            .getEquipManager().getPlayerEquipment(player);

        // 先移除旧的修饰符
        removeAllBukkitAttributes(player);

        // 计算总属性值
        Map<String, Double> totalAttributes = new HashMap<>();

        for (int i = 0; i < 4; i++) {
            EquipmentSlot cardSlot = equipment.getCard(i);
            if (cardSlot != null && !cardSlot.isEmpty()) {
                String cardId = cardSlot.getId();
                int level = cardSlot.getLevel();
                var card = CardAccessorySystem.getInstance().getItemManager().getCard(cardId);
                if (card != null) {
                    // 基础属性
                    card.getAttributes().forEach((attr, value) ->
                        totalAttributes.merge(attr, value, Double::sum)
                    );
                    // 升级属性
                    if (level > 1 && card.hasUpgradeLevel(level)) {
                        var upgradeLevel = card.getUpgradeLevel(level);
                        if (upgradeLevel != null && upgradeLevel.getAttributes() != null) {
                            upgradeLevel.getAttributes().forEach((attr, value) ->
                                totalAttributes.merge(attr, value, Double::sum)
                            );
                        }
                    }
                }
            }
        }

        // 应用总属性
        totalAttributes.forEach((attr, value) -> applyBukkitAttribute(player, attr, value));
    }

    public static void removeCardEffects(Player player) {
        removeAllBukkitAttributes(player);
    }

    public static double getAccessoryMultiplier(Player player) {
        double multiplier = 1.0;

        PlayerEquipment equipment = CardAccessorySystem.getInstance()
            .getEquipManager().getPlayerEquipment(player);

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

    private static void applyBukkitAttribute(Player player, String attribute, double value) {
        try {
            Attribute bukkitAttr = getBukkitAttribute(attribute);
            if (bukkitAttr == null) return;

            AttributeInstance attributeInstance = player.getAttribute(bukkitAttr);
            if (attributeInstance == null) return;

            UUID modifierId = getAttributeUUID(player, attribute);
            // 先移除同名修饰符
            attributeInstance.removeModifier(modifierId);

            AttributeModifier modifier = new AttributeModifier(
                modifierId,
                "CardAccessory-" + attribute,
                value,
                AttributeModifier.Operation.ADD_NUMBER
            );

            attributeInstance.addModifier(modifier);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void removeAllBukkitAttributes(Player player) {
        String[] attributes = {"MAX_HEALTH", "ATTACK_DAMAGE", "MOVEMENT_SPEED"};
        for (String attr : attributes) {
            try {
                Attribute bukkitAttr = getBukkitAttribute(attr);
                if (bukkitAttr == null) continue;

                AttributeInstance attributeInstance = player.getAttribute(bukkitAttr);
                if (attributeInstance == null) continue;

                UUID modifierId = getAttributeUUID(player, attr);
                attributeInstance.removeModifier(modifierId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static Attribute getBukkitAttribute(String attribute) {
        switch (attribute.toUpperCase()) {
            case "MAX_HEALTH":
                return Attribute.GENERIC_MAX_HEALTH;
            case "ATTACK_DAMAGE":
                return Attribute.GENERIC_ATTACK_DAMAGE;
            case "MOVEMENT_SPEED":
                return Attribute.GENERIC_MOVEMENT_SPEED;
            default:
                return null;
        }
    }

    private static UUID getAttributeUUID(Player player, String attribute) {
        // 为每个玩家+属性组合生成唯一UUID
        UUID baseUuid = ATTRIBUTE_UUIDS.get(attribute.toUpperCase());
        if (baseUuid == null) {
            baseUuid = UUID.nameUUIDFromBytes(("CardAccessory-" + attribute).getBytes());
        }
        return UUID.nameUUIDFromBytes((player.getUniqueId().toString() + baseUuid.toString()).getBytes());
    }

    public static void clearPlayerModifiers(Player player) {
        removeAllBukkitAttributes(player);
    }
}
