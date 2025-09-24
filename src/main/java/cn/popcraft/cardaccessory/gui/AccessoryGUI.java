package cn.popcraft.cardaccessory.gui;

import cn.popcraft.cardaccessory.CardAccessorySystem;
import cn.popcraft.cardaccessory.model.Accessory;
import cn.popcraft.cardaccessory.model.PlayerEquipment;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AccessoryGUI implements InventoryHolder {
    private static final int GUI_SIZE = 54; // 6行
    private static final int[] EQUIPMENT_SLOTS = {0, 1}; // 前2个槽位用于显示已装备的饰品
    
    private final Player player;
    private final Inventory inventory;
    
    public AccessoryGUI(Player player) {
        this.player = player;
        this.inventory = Bukkit.createInventory(this, GUI_SIZE, "饰品管理");
        update();
    }
    
    public void open() {
        player.openInventory(inventory);
    }
    
    public void update() {
        // 清空GUI
        inventory.clear();
        
        PlayerEquipment equipment = CardAccessorySystem.getInstance()
            .getEquipManager().getPlayerEquipment(player);
        
        // 填充已装备的饰品
        for (int i = 0; i < EQUIPMENT_SLOTS.length; i++) {
            String accessoryId = equipment.getAccessory(i);
            if (accessoryId != null && !accessoryId.isEmpty()) {
                ItemStack accessoryItem = CardAccessorySystem.getInstance()
                    .getItemManager().createAccessoryItem(accessoryId);
                inventory.setItem(EQUIPMENT_SLOTS[i], accessoryItem);
            } else {
                // 空槽位显示灰色玻璃板
                inventory.setItem(EQUIPMENT_SLOTS[i], createEmptySlotItem());
            }
        }
        
        // 在其他槽位放置玩家背包中的饰品（示例实现）
        // 实际应用中需要扫描玩家整个背包中的饰品
        int slotIndex = 9; // 从第二行开始放置可装备的饰品
        for (Accessory accessory : CardAccessorySystem.getInstance().getItemManager().getAllAccessories()) {
            if (slotIndex >= GUI_SIZE) break; // 防止超出GUI范围
            
            // 检查这个饰品是否已经装备
            boolean isEquipped = false;
            for (int i = 0; i < 2; i++) {
                if (accessory.getId().equals(equipment.getAccessory(i))) {
                    isEquipped = true;
                    break;
                }
            }
            
            if (!isEquipped) {
                ItemStack accessoryItem = CardAccessorySystem.getInstance()
                    .getItemManager().createAccessoryItem(accessory.getId());
                inventory.setItem(slotIndex++, accessoryItem);
            }
        }
    }
    
    public void handleItemClick(int slot, ItemStack clickedItem) {
        // 检查是否点击了装备槽位
        boolean isEquipmentSlot = false;
        int equipmentSlotIndex = -1;
        for (int i = 0; i < EQUIPMENT_SLOTS.length; i++) {
            if (EQUIPMENT_SLOTS[i] == slot) {
                isEquipmentSlot = true;
                equipmentSlotIndex = i;
                break;
            }
        }
        
        if (isEquipmentSlot) {
            // 点击了已装备的饰品，卸下它
            if (clickedItem != null && clickedItem.getType() != Material.GRAY_STAINED_GLASS_PANE) {
                unequipAccessory(equipmentSlotIndex);
            }
        } else {
            // 点击了可装备的饰品，装备它
            if (clickedItem != null && CardAccessorySystem.getInstance().getItemManager().isAccessory(clickedItem)) {
                equipAccessory(clickedItem);
            }
        }
    }
    
    private void equipAccessory(ItemStack accessoryItem) {
        String accessoryId = CardAccessorySystem.getInstance().getItemManager().getAccessoryId(accessoryItem);
        if (accessoryId == null) return;
        
        PlayerEquipment equipment = CardAccessorySystem.getInstance()
            .getEquipManager().getPlayerEquipment(player);
        
        // 查找第一个空槽位
        int emptySlot = -1;
        for (int i = 0; i < 2; i++) {
            if (equipment.getAccessory(i) == null || equipment.getAccessory(i).isEmpty()) {
                emptySlot = i;
                break;
            }
        }
        
        if (emptySlot != -1) {
            // 装备饰品
            equipment.setAccessory(emptySlot, accessoryId);
            CardAccessorySystem.getInstance().getEquipManager().setPlayerEquipment(player, equipment);
            
            // 更新GUI
            update();
            player.sendMessage(ChatColor.GREEN + "成功装备饰品！");
        } else {
            player.sendMessage(ChatColor.RED + "饰品槽已满！");
        }
    }
    
    private void unequipAccessory(int slot) {
        PlayerEquipment equipment = CardAccessorySystem.getInstance()
            .getEquipManager().getPlayerEquipment(player);
        
        String accessoryId = equipment.getAccessory(slot);
        if (accessoryId == null || accessoryId.isEmpty()) return;
        
        // 获取饰品物品并尝试返还给玩家
        ItemStack accessoryItem = CardAccessorySystem.getInstance().getItemManager().createAccessoryItem(accessoryId);
        boolean addItem = player.getInventory().addItem(accessoryItem).isEmpty();
        if (!addItem) {
            // 背包已满，掉落地上
            player.getWorld().dropItemNaturally(player.getLocation(), accessoryItem);
            player.sendMessage(ChatColor.YELLOW + "背包已满，饰品已掉落至地面。");
        }

        // 清空槽位
        equipment.removeAccessory(slot);
        CardAccessorySystem.getInstance().getEquipManager().setPlayerEquipment(player, equipment);
        
        // 更新GUI
        update();
        player.sendMessage(ChatColor.GREEN + "成功卸下饰品！");
    }
    
    private ItemStack createEmptySlotItem() {
        ItemStack item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GRAY + "空饰品槽位");
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.DARK_GRAY + "点击饰品进行装备");
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }
    
    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}