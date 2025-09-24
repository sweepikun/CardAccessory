package cn.popcraft.cardaccessory.util;

import cn.popcraft.cardaccessory.CardAccessorySystem;
import org.bukkit.NamespacedKey;

public class NBTKeys {
    public static final NamespacedKey CARD_ID = new NamespacedKey(CardAccessorySystem.getInstance(), "card_id");
    public static final NamespacedKey ACCESSORY_ID = new NamespacedKey(CardAccessorySystem.getInstance(), "accessory_id");
}