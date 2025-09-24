package cn.popcraft.cardaccessory.listener;

import cn.popcraft.cardaccessory.CardAccessorySystem;
import cn.popcraft.cardaccessory.gui.upgrade.UpgradeAccessoryGUI;
import cn.popcraft.cardaccessory.gui.upgrade.UpgradeCardGUI;
import cn.popcraft.cardaccessory.hook.PlayerPointsHook;
import cn.popcraft.cardaccessory.hook.VaultHook;
import cn.popcraft.cardaccessory.model.UpgradeCost;
import cn.popcraft.cardaccessory.util.CustomItemLoader;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class UpgradeGUIListener implements Listener {
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) {
            return;
        }
        
        if (event.getInventory().getHolder() instanceof UpgradeCardGUI) {
            event.setCancelled(true);
            
            UpgradeCardGUI upgradeGUI = (UpgradeCardGUI) event.getInventory().getHolder();
            handleUpgradeCardGUIClick(upgradeGUI, event.getSlot(), event.getCurrentItem());
        } else if (event.getInventory().getHolder() instanceof UpgradeAccessoryGUI) {
            event.setCancelled(true);
            
            UpgradeAccessoryGUI upgradeGUI = (UpgradeAccessoryGUI) event.getInventory().getHolder();
            handleUpgradeAccessoryGUIClick(upgradeGUI, event.getSlot(), event.getCurrentItem());
        }
    }
    
    private void handleUpgradeCardGUIClick(UpgradeCardGUI upgradeGUI, int slot, ItemStack clickedItem) {
        // 检查是否点击了确认或取消按钮
        if (slot == 48) { // 确认按钮
            // 执行升级操作
            performCardUpgrade(upgradeGUI);
        } else if (slot == 50) { // 取消按钮
            // 关闭GUI
            upgradeGUI.getPlayer().closeInventory();
        }
    }
    
    private void handleUpgradeAccessoryGUIClick(UpgradeAccessoryGUI upgradeGUI, int slot, ItemStack clickedItem) {
        // 检查是否点击了确认或取消按钮
        if (slot == 48) { // 确认按钮
            // 执行升级操作
            performAccessoryUpgrade(upgradeGUI);
        } else if (slot == 50) { // 取消按钮
            // 关闭GUI
            upgradeGUI.getPlayer().closeInventory();
        }
    }
    
    private void performCardUpgrade(UpgradeCardGUI upgradeGUI) {
        int currentLevel = upgradeGUI.getCurrentLevel();
        int nextLevel = currentLevel + 1;
        
        // 检查是否已达到最高等级
        if (currentLevel >= upgradeGUI.getCard().getMaxLevel()) {
            upgradeGUI.getPlayer().sendMessage("该卡牌已达到最高等级！");
            return;
        }
        
        // 获取下一等级的升级信息
        var upgradeLevel = upgradeGUI.getCard().getUpgradeLevel(nextLevel);
        if (upgradeLevel == null) {
            upgradeGUI.getPlayer().sendMessage("没有下一等级的升级信息！");
            return;
        }
        
        // 检查并扣除消耗
        for (UpgradeCost cost : upgradeLevel.getCosts()) {
            if (!deductPlayerResource(upgradeGUI.getPlayer(), cost)) {
                upgradeGUI.getPlayer().sendMessage("资源不足，无法升级！");
                return;
            }
        }
        
        // 升级成功消息
        upgradeGUI.getPlayer().sendMessage("卡牌升级成功！等级: " + nextLevel);
        
        // 关闭GUI
        upgradeGUI.getPlayer().closeInventory();
        
        // TODO: 实际更新玩家的卡牌等级数据
    }
    
    private void performAccessoryUpgrade(UpgradeAccessoryGUI upgradeGUI) {
        int currentLevel = upgradeGUI.getCurrentLevel();
        int nextLevel = currentLevel + 1;
        
        // 检查是否已达到最高等级
        if (currentLevel >= upgradeGUI.getAccessory().getMaxLevel()) {
            upgradeGUI.getPlayer().sendMessage("该饰品已达到最高等级！");
            return;
        }
        
        // 获取下一等级的升级信息
        var upgradeLevel = upgradeGUI.getAccessory().getUpgradeLevel(nextLevel);
        if (upgradeLevel == null) {
            upgradeGUI.getPlayer().sendMessage("没有下一等级的升级信息！");
            return;
        }
        
        // 检查并扣除消耗
        for (UpgradeCost cost : upgradeLevel.getCosts()) {
            if (!deductPlayerResource(upgradeGUI.getPlayer(), cost)) {
                upgradeGUI.getPlayer().sendMessage("资源不足，无法升级！");
                return;
            }
        }
        
        // 升级成功消息
        upgradeGUI.getPlayer().sendMessage("饰品升级成功！等级: " + nextLevel);
        
        // 关闭GUI
        upgradeGUI.getPlayer().closeInventory();
        
        // TODO: 实际更新玩家的饰品等级数据
    }
    
    private boolean deductPlayerResource(org.bukkit.entity.Player player, UpgradeCost cost) {
        switch (cost.getType().toLowerCase()) {
            case "item":
                // 扣除玩家背包中的物品
                ItemStack item = CustomItemLoader.loadItem(cost.getId());
                if (item.getType() == Material.AIR) return false;
                
                // 查找并移除物品
                int remaining = cost.getAmount();
                ItemStack[] contents = player.getInventory().getContents();
                for (int i = 0; i < contents.length && remaining > 0; i++) {
                    ItemStack invItem = contents[i];
                    if (invItem != null && invItem.isSimilar(item)) {
                        int toRemove = Math.min(remaining, invItem.getAmount());
                        invItem.setAmount(invItem.getAmount() - toRemove);
                        if (invItem.getAmount() <= 0) {
                            player.getInventory().setItem(i, null);
                        }
                        remaining -= toRemove;
                    }
                }
                return remaining == 0;
                
            case "currency":
                // 扣除货币
                if ("playerpoints".equalsIgnoreCase(cost.getId())) {
                    return PlayerPointsHook.takePlayerPoints(player, cost.getAmount());
                } else if ("coins".equalsIgnoreCase(cost.getId())) {
                    return VaultHook.takePlayerMoney(player, cost.getAmount());
                }
                return false;
                
            default:
                return false;
        }
    }
}