package cn.popcraft.cardaccessory.gui;

import cn.popcraft.cardaccessory.CardAccessorySystem;
import cn.popcraft.cardaccessory.model.EquipmentSlot;
import cn.popcraft.cardaccessory.model.PlayerEquipment;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class GUIManager {
    private static final int CARD_GUI_SIZE = 54; // 6行
    private static final int ACCESSORY_GUI_SIZE = 54; // 6行
    
    public static void openCardGUI(Player player) {
        Inventory inventory = Bukkit.createInventory(player, CARD_GUI_SIZE, "卡牌管理");
        
        PlayerEquipment equipment = CardAccessorySystem.getInstance()
            .getEquipManager().getPlayerEquipment(player);
        
        // 填充已装备的卡牌
        for (int i = 0; i < 4; i++) {
            EquipmentSlot cardSlot = equipment.getCard(i);
            if (cardSlot != null && !cardSlot.isEmpty()) {
                String cardId = cardSlot.getId();
                ItemStack cardItem = CardAccessorySystem.getInstance()
                    .getItemManager().createCardItem(cardId);
                inventory.setItem(i, cardItem);
            } else {
                // 空槽位显示灰色玻璃板
                inventory.setItem(i, createEmptySlotItem());
            }
        }
        
        // 扫描玩家背包中的卡牌并显示在GUI中
        int slotIndex = 9; // 从第二行开始放置可装备的卡牌
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && !item.getType().equals(Material.AIR)) {
                if (CardAccessorySystem.getInstance().getItemManager().isCard(item)) {
                    // 检查这张卡牌是否已经装备
                    boolean isEquipped = false;
                    String cardId = CardAccessorySystem.getInstance().getItemManager().getCardId(item);
                    for (int i = 0; i < 4; i++) {
                        EquipmentSlot slot = equipment.getCard(i);
                        if (slot != null && cardId.equals(slot.getId())) {
                            isEquipped = true;
                            break;
                        }
                    }
                    
                    if (!isEquipped && slotIndex < CARD_GUI_SIZE) {
                        inventory.setItem(slotIndex++, item.clone());
                    }
                }
            }
        }
        
        player.openInventory(inventory);
    }
    
    public static void openAccessoryGUI(Player player) {
        Inventory inventory = Bukkit.createInventory(player, ACCESSORY_GUI_SIZE, "饰品管理");
        
        PlayerEquipment equipment = CardAccessorySystem.getInstance()
            .getEquipManager().getPlayerEquipment(player);
        
        // 填充已装备的饰品
        for (int i = 0; i < 2; i++) {
            EquipmentSlot accessorySlot = equipment.getAccessory(i);
            if (accessorySlot != null && !accessorySlot.isEmpty()) {
                String accessoryId = accessorySlot.getId();
                ItemStack accessoryItem = CardAccessorySystem.getInstance()
                    .getItemManager().createAccessoryItem(accessoryId);
                inventory.setItem(i, accessoryItem);
            } else {
                // 空槽位显示灰色玻璃板
                inventory.setItem(i, createEmptySlotItem());
            }
        }
        
        // 扫描玩家背包中的饰品并显示在GUI中
        int slotIndex = 9; // 从第二行开始放置可装备的饰品
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && !item.getType().equals(Material.AIR)) {
                if (CardAccessorySystem.getInstance().getItemManager().isAccessory(item)) {
                    // 检查这个饰品是否已经装备
                    boolean isEquipped = false;
                    String accessoryId = CardAccessorySystem.getInstance().getItemManager().getAccessoryId(item);
                    for (int i = 0; i < 2; i++) {
                        EquipmentSlot slot = equipment.getAccessory(i);
                        if (slot != null && accessoryId.equals(slot.getId())) {
                            isEquipped = true;
                            break;
                        }
                    }
                    
                    if (!isEquipped && slotIndex < ACCESSORY_GUI_SIZE) {
                        inventory.setItem(slotIndex++, item.clone());
                    }
                }
            }
        }
        
        player.openInventory(inventory);
    }
    
    private static ItemStack createEmptySlotItem() {
        ItemStack item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GRAY + "空槽位");
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.DARK_GRAY + "这里还没有装备物品");
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }
}