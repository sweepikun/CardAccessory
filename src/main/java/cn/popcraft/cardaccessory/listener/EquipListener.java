package cn.popcraft.cardaccessory.listener;

import cn.popcraft.cardaccessory.CardAccessorySystem;
import cn.popcraft.cardaccessory.manager.EffectManager;
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
        // 检查是否为右键点击
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        
        // 检查是否持有物品
        if (item == null) {
            return;
        }
        
        // 使用PDC标签系统判断是否为卡牌或饰品
        if (CardAccessorySystem.getInstance().getItemManager().isCard(item)) {
            // 装备卡牌逻辑
            player.sendMessage(ChatColor.GREEN + "你装备了一张卡牌！");
            // 应用卡牌效果
            EffectManager.applyCardEffects(player);
        } else if (CardAccessorySystem.getInstance().getItemManager().isAccessory(item)) {
            // 装备饰品逻辑
            player.sendMessage(ChatColor.GREEN + "你装备了一个饰品！");
            // 饰品效果将在技能伤害时计算
        }
    }
}