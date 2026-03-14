package cn.popcraft.cardaccessory.listener;

import cn.popcraft.cardaccessory.CardAccessorySystem;
import cn.popcraft.cardaccessory.gui.upgrade.UpgradeAccessoryGUI;
import cn.popcraft.cardaccessory.gui.upgrade.UpgradeCardGUI;
import cn.popcraft.cardaccessory.hook.PlayerPointsHook;
import cn.popcraft.cardaccessory.hook.VaultHook;
import cn.popcraft.cardaccessory.manager.EffectManager;
import cn.popcraft.cardaccessory.model.EquipmentSlot;
import cn.popcraft.cardaccessory.model.PlayerEquipment;
import cn.popcraft.cardaccessory.model.UpgradeCost;
import cn.popcraft.cardaccessory.util.CustomItemLoader;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
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
            handleUpgradeCardGUIClick(upgradeGUI, event.getSlot());
        } else if (event.getInventory().getHolder() instanceof UpgradeAccessoryGUI) {
            event.setCancelled(true);
            UpgradeAccessoryGUI upgradeGUI = (UpgradeAccessoryGUI) event.getInventory().getHolder();
            handleUpgradeAccessoryGUIClick(upgradeGUI, event.getSlot());
        }
    }

    private void handleUpgradeCardGUIClick(UpgradeCardGUI upgradeGUI, int slot) {
        if (slot == 48) {
            performCardUpgrade(upgradeGUI);
        } else if (slot == 50) {
            upgradeGUI.getPlayer().closeInventory();
        }
    }

    private void handleUpgradeAccessoryGUIClick(UpgradeAccessoryGUI upgradeGUI, int slot) {
        if (slot == 48) {
            performAccessoryUpgrade(upgradeGUI);
        } else if (slot == 50) {
            upgradeGUI.getPlayer().closeInventory();
        }
    }

    private void performCardUpgrade(UpgradeCardGUI upgradeGUI) {
        Player player = upgradeGUI.getPlayer();
        int currentLevel = upgradeGUI.getCurrentLevel();
        int nextLevel = currentLevel + 1;
        String cardId = upgradeGUI.getCard().getId();

        if (currentLevel >= upgradeGUI.getCard().getMaxLevel()) {
            player.sendMessage(ChatColor.RED + "该卡牌已达到最高等级！");
            return;
        }

        var upgradeLevel = upgradeGUI.getCard().getUpgradeLevel(nextLevel);
        if (upgradeLevel == null) {
            player.sendMessage(ChatColor.RED + "没有下一等级的升级信息！");
            return;
        }

        // 先检查所有资源是否充足
        for (UpgradeCost cost : upgradeLevel.getCosts()) {
            if (!checkPlayerHasResource(player, cost)) {
                player.sendMessage(ChatColor.RED + "资源不足，无法升级！");
                return;
            }
        }

        // 检查通过，扣除所有资源
        for (UpgradeCost cost : upgradeLevel.getCosts()) {
            deductPlayerResource(player, cost);
        }

        // 更新玩家装备数据中的卡牌等级
        PlayerEquipment equipment = CardAccessorySystem.getInstance()
            .getEquipManager().getPlayerEquipment(player);
        for (int i = 0; i < 4; i++) {
            EquipmentSlot slot = equipment.getCard(i);
            if (slot != null && cardId.equals(slot.getId())) {
                equipment.setCard(i, cardId, nextLevel);
                break;
            }
        }
        CardAccessorySystem.getInstance().getEquipManager().setPlayerEquipment(player, equipment);

        // 重新应用卡牌效果（属性可能会变化）
        EffectManager.removeCardEffects(player);
        EffectManager.applyCardEffects(player);

        player.sendMessage(ChatColor.GREEN + "卡牌升级成功！等级: " + nextLevel);
        player.closeInventory();
    }

    private void performAccessoryUpgrade(UpgradeAccessoryGUI upgradeGUI) {
        Player player = upgradeGUI.getPlayer();
        int currentLevel = upgradeGUI.getCurrentLevel();
        int nextLevel = currentLevel + 1;
        String accessoryId = upgradeGUI.getAccessory().getId();

        if (currentLevel >= upgradeGUI.getAccessory().getMaxLevel()) {
            player.sendMessage(ChatColor.RED + "该饰品已达到最高等级！");
            return;
        }

        var upgradeLevel = upgradeGUI.getAccessory().getUpgradeLevel(nextLevel);
        if (upgradeLevel == null) {
            player.sendMessage(ChatColor.RED + "没有下一等级的升级信息！");
            return;
        }

        // 先检查所有资源是否充足
        for (UpgradeCost cost : upgradeLevel.getCosts()) {
            if (!checkPlayerHasResource(player, cost)) {
                player.sendMessage(ChatColor.RED + "资源不足，无法升级！");
                return;
            }
        }

        // 检查通过，扣除所有资源
        for (UpgradeCost cost : upgradeLevel.getCosts()) {
            deductPlayerResource(player, cost);
        }

        // 更新玩家装备数据中的饰品等级
        PlayerEquipment equipment = CardAccessorySystem.getInstance()
            .getEquipManager().getPlayerEquipment(player);
        for (int i = 0; i < 2; i++) {
            EquipmentSlot slot = equipment.getAccessory(i);
            if (slot != null && accessoryId.equals(slot.getId())) {
                equipment.setAccessory(i, accessoryId, nextLevel);
                break;
            }
        }
        CardAccessorySystem.getInstance().getEquipManager().setPlayerEquipment(player, equipment);

        player.sendMessage(ChatColor.GREEN + "饰品升级成功！等级: " + nextLevel);
        player.closeInventory();
    }

    private boolean checkPlayerHasResource(Player player, UpgradeCost cost) {
        switch (cost.getType().toLowerCase()) {
            case "item":
                ItemStack item = CustomItemLoader.loadItem(cost.getId());
                if (item.getType() == Material.AIR) return false;

                int count = 0;
                for (ItemStack invItem : player.getInventory().getContents()) {
                    if (invItem != null && invItem.isSimilar(item)) {
                        count += invItem.getAmount();
                    }
                }
                return count >= cost.getAmount();

            case "currency":
                if ("playerpoints".equalsIgnoreCase(cost.getId())) {
                    int points = PlayerPointsHook.getPlayerPoints(player);
                    return points >= cost.getAmount();
                } else if ("coins".equalsIgnoreCase(cost.getId())) {
                    return VaultHook.hasPlayerMoney(player, cost.getAmount());
                }
                return false;

            default:
                return false;
        }
    }

    private boolean deductPlayerResource(Player player, UpgradeCost cost) {
        switch (cost.getType().toLowerCase()) {
            case "item":
                ItemStack item = CustomItemLoader.loadItem(cost.getId());
                if (item.getType() == Material.AIR) return false;

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
