package cn.popcraft.cardaccessory.gui;

import cn.popcraft.cardaccessory.CardAccessorySystem;
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
            String cardId = equipment.getCard(i);
            if (cardId != null && !cardId.isEmpty()) {
                ItemStack cardItem = CardAccessorySystem.getInstance()
                    .getItemManager().createCardItem(cardId);
                inventory.setItem(i, cardItem);
            } else {
                // 空槽位显示灰色玻璃板
                inventory.setItem(i, createEmptySlotItem());
            }
        }
        
        // 填充玩家背包中的其他卡牌（示例）
        // 实际实现中需要扫描玩家背包中的有效卡牌
        
        player.openInventory(inventory);
    }
    
    public static void openAccessoryGUI(Player player) {
        Inventory inventory = Bukkit.createInventory(player, ACCESSORY_GUI_SIZE, "饰品管理");
        
        PlayerEquipment equipment = CardAccessorySystem.getInstance()
            .getEquipManager().getPlayerEquipment(player);
        
        // 填充已装备的饰品
        for (int i = 0; i < 2; i++) {
            String accessoryId = equipment.getAccessory(i);
            if (accessoryId != null && !accessoryId.isEmpty()) {
                ItemStack accessoryItem = CardAccessorySystem.getInstance()
                    .getItemManager().createAccessoryItem(accessoryId);
                inventory.setItem(i, accessoryItem);
            } else {
                // 空槽位显示灰色玻璃板
                inventory.setItem(i, createEmptySlotItem());
            }
        }
        
        // 填充玩家背包中的其他饰品（示例）
        // 实际实现中需要扫描玩家背包中的有效饰品
        
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