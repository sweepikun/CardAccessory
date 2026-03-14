package cn.popcraft.cardaccessory.listener;

import cn.popcraft.cardaccessory.CardAccessorySystem;
import cn.popcraft.cardaccessory.hook.MythicClassHook;
import cn.popcraft.cardaccessory.manager.EffectManager;
import cn.popcraft.cardaccessory.model.Card;
import cn.popcraft.cardaccessory.model.EquipmentSlot;
import cn.popcraft.cardaccessory.model.PlayerEquipment;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class EquipListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null) {
            return;
        }

        var itemManager = CardAccessorySystem.getInstance().getItemManager();
        var equipManager = CardAccessorySystem.getInstance().getEquipManager();

        if (itemManager.isCard(item)) {
            event.setCancelled(true);
            equipCard(player, item, itemManager, equipManager);
        } else if (itemManager.isAccessory(item)) {
            event.setCancelled(true);
            equipAccessory(player, item, itemManager, equipManager);
        }
    }

    private void equipCard(Player player, ItemStack item, var itemManager, var equipManager) {
        String cardId = itemManager.getCardId(item);
        if (cardId == null) return;

        Card card = itemManager.getCard(cardId);
        if (card == null) return;

        // 检查权限
        if (!card.getPermission().isEmpty() && !player.hasPermission(card.getPermission())) {
            player.sendMessage(ChatColor.RED + "你没有权限装备这张卡牌！");
            return;
        }

        // 检查职业
        if (!MythicClassHook.checkPlayerClass(player, card.getRequiredClass())) {
            player.sendMessage(ChatColor.RED + "你的职业不符合要求！");
            return;
        }

        PlayerEquipment equipment = equipManager.getPlayerEquipment(player);

        // 查找第一个空槽位
        int emptySlot = -1;
        for (int i = 0; i < 4; i++) {
            EquipmentSlot slot = equipment.getCard(i);
            if (slot == null || slot.isEmpty()) {
                emptySlot = i;
                break;
            }
        }

        if (emptySlot == -1) {
            player.sendMessage(ChatColor.RED + "卡牌槽已满！");
            return;
        }

        // 装备卡牌
        equipment.setCard(emptySlot, cardId);
        equipManager.setPlayerEquipment(player, equipment);

        // 从背包移除物品
        item.setAmount(item.getAmount() - 1);

        // 应用卡牌效果
        EffectManager.removeCardEffects(player);
        EffectManager.applyCardEffects(player);

        player.sendMessage(ChatColor.GREEN + "成功装备卡牌：" + card.getName());
    }

    private void equipAccessory(Player player, ItemStack item, var itemManager, var equipManager) {
        String accessoryId = itemManager.getAccessoryId(item);
        if (accessoryId == null) return;

        var accessory = itemManager.getAccessory(accessoryId);
        if (accessory == null) return;

        // 检查权限
        if (!accessory.getPermission().isEmpty() && !player.hasPermission(accessory.getPermission())) {
            player.sendMessage(ChatColor.RED + "你没有权限装备这个饰品！");
            return;
        }

        // 检查职业
        if (!MythicClassHook.checkPlayerClass(player, accessory.getRequiredClass())) {
            player.sendMessage(ChatColor.RED + "你的职业不符合要求！");
            return;
        }

        PlayerEquipment equipment = equipManager.getPlayerEquipment(player);

        // 查找第一个空槽位
        int emptySlot = -1;
        for (int i = 0; i < 2; i++) {
            EquipmentSlot slot = equipment.getAccessory(i);
            if (slot == null || slot.isEmpty()) {
                emptySlot = i;
                break;
            }
        }

        if (emptySlot == -1) {
            player.sendMessage(ChatColor.RED + "饰品槽已满！");
            return;
        }

        // 装备饰品
        equipment.setAccessory(emptySlot, accessoryId);
        equipManager.setPlayerEquipment(player, equipment);

        // 从背包移除物品
        item.setAmount(item.getAmount() - 1);

        player.sendMessage(ChatColor.GREEN + "成功装备饰品：" + accessory.getName());
    }
}
