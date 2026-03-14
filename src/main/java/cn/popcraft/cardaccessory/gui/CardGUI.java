package cn.popcraft.cardaccessory.gui;

import cn.popcraft.cardaccessory.CardAccessorySystem;
import cn.popcraft.cardaccessory.manager.EffectManager;
import cn.popcraft.cardaccessory.model.EquipmentSlot;
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

public class CardGUI implements InventoryHolder {
    private static final int GUI_SIZE = 54;
    private static final int[] EQUIPMENT_SLOTS = {0, 1, 2, 3};

    private final Player player;
    private final Inventory inventory;

    public CardGUI(Player player) {
        this.player = player;
        this.inventory = Bukkit.createInventory(this, GUI_SIZE, "卡牌管理");
        update();
    }

    public void open() {
        player.openInventory(inventory);
    }

    public void update() {
        inventory.clear();

        PlayerEquipment equipment = CardAccessorySystem.getInstance()
            .getEquipManager().getPlayerEquipment(player);

        // 填充已装备的卡牌
        for (int i = 0; i < EQUIPMENT_SLOTS.length; i++) {
            EquipmentSlot cardSlot = equipment.getCard(i);
            if (cardSlot != null && !cardSlot.isEmpty()) {
                String cardId = cardSlot.getId();
                ItemStack cardItem = CardAccessorySystem.getInstance()
                    .getItemManager().createCardItem(cardId, cardSlot.getLevel());
                inventory.setItem(EQUIPMENT_SLOTS[i], cardItem);
            } else {
                inventory.setItem(EQUIPMENT_SLOTS[i], createEmptySlotItem());
            }
        }

        // 扫描玩家背包中的卡牌并显示在GUI中
        int slotIndex = 9;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && !item.getType().equals(Material.AIR)) {
                if (CardAccessorySystem.getInstance().getItemManager().isCard(item)) {
                    if (slotIndex < GUI_SIZE) {
                        inventory.setItem(slotIndex++, item.clone());
                    }
                }
            }
        }
    }

    public void handleItemClick(int slot, ItemStack clickedItem) {
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
            if (clickedItem != null && clickedItem.getType() != Material.GRAY_STAINED_GLASS_PANE) {
                unequipCard(equipmentSlotIndex);
            }
        } else {
            if (clickedItem != null && CardAccessorySystem.getInstance().getItemManager().isCard(clickedItem)) {
                equipCard(clickedItem);
            }
        }
    }

    private void equipCard(ItemStack cardItem) {
        String cardId = CardAccessorySystem.getInstance().getItemManager().getCardId(cardItem);
        if (cardId == null) return;

        PlayerEquipment equipment = CardAccessorySystem.getInstance()
            .getEquipManager().getPlayerEquipment(player);

        // 查找第一个空槽位
        int emptySlot = -1;
        for (int i = 0; i < 4; i++) {
            EquipmentSlot slot = equipment.getCard(i);
            if (slot == null || slot.isEmpty()) {
                emptySlot = i;
                break;
            }
        }

        if (emptySlot != -1) {
            equipment.setCard(emptySlot, cardId);
            CardAccessorySystem.getInstance().getEquipManager().setPlayerEquipment(player, equipment);

            // 从背包移除物品
            removeFromInventory(cardItem);

            // 重新应用卡牌效果
            EffectManager.removeCardEffects(player);
            EffectManager.applyCardEffects(player);

            update();
            player.sendMessage(ChatColor.GREEN + "成功装备卡牌！");
        } else {
            player.sendMessage(ChatColor.RED + "卡牌槽已满！");
        }
    }

    private void unequipCard(int slot) {
        PlayerEquipment equipment = CardAccessorySystem.getInstance()
            .getEquipManager().getPlayerEquipment(player);

        EquipmentSlot cardSlot = equipment.getCard(slot);
        if (cardSlot == null || cardSlot.isEmpty()) return;

        // 创建物品返还给玩家
        ItemStack cardItem = CardAccessorySystem.getInstance().getItemManager()
            .createCardItem(cardSlot.getId(), cardSlot.getLevel());
        boolean added = player.getInventory().addItem(cardItem).isEmpty();
        if (!added) {
            player.getWorld().dropItemNaturally(player.getLocation(), cardItem);
            player.sendMessage(ChatColor.YELLOW + "背包已满，卡牌已掉落至地面。");
        }

        // 移除卡牌效果
        EffectManager.removeCardEffects(player);

        // 清空槽位
        equipment.removeCard(slot);
        CardAccessorySystem.getInstance().getEquipManager().setPlayerEquipment(player, equipment);

        // 重新应用剩余卡牌效果
        EffectManager.applyCardEffects(player);

        update();
        player.sendMessage(ChatColor.GREEN + "成功卸下卡牌！");
    }

    private void removeFromInventory(ItemStack targetItem) {
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item != null && item.isSimilar(targetItem)) {
                int newAmount = item.getAmount() - 1;
                if (newAmount <= 0) {
                    player.getInventory().setItem(i, null);
                } else {
                    item.setAmount(newAmount);
                }
                return;
            }
        }
    }

    private ItemStack createEmptySlotItem() {
        ItemStack item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GRAY + "空卡牌槽位");
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.DARK_GRAY + "点击卡牌进行装备");
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
