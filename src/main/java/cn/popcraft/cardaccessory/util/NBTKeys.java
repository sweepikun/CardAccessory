package cn.popcraft.cardaccessory.util;

import cn.popcraft.cardaccessory.CardAccessorySystem;
import org.bukkit.NamespacedKey;

public class NBTKeys {
    private static NamespacedKey cardIdKey;
    private static NamespacedKey accessoryIdKey;

    public static NamespacedKey getCardId() {
        if (cardIdKey == null) {
            cardIdKey = new NamespacedKey(CardAccessorySystem.getInstance(), "card_id");
        }
        return cardIdKey;
    }

    public static NamespacedKey getAccessoryId() {
        if (accessoryIdKey == null) {
            accessoryIdKey = new NamespacedKey(CardAccessorySystem.getInstance(), "accessory_id");
        }
        return accessoryIdKey;
    }
}
