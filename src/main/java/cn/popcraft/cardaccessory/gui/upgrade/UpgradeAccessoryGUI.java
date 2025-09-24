package cn.popcraft.cardaccessory.gui.upgrade;

import cn.popcraft.cardaccessory.CardAccessorySystem;
import cn.popcraft.cardaccessory.model.Accessory;
import cn.popcraft.cardaccessory.model.UpgradeCost;
import cn.popcraft.cardaccessory.model.UpgradeLevel;
import cn.popcraft.cardaccessory.util.CustomItemLoader;
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

public class UpgradeAccessoryGUI implements InventoryHolder {
    private static final int GUI_SIZE = 54; // 6行
    
    private final Player player;
    private final Accessory accessory;
    private final int currentLevel;
    private final Inventory inventory;
    
    public UpgradeAccessoryGUI(Player player, Accessory accessory, int currentLevel) {
        this.player = player;
        this.accessory = accessory;
        this.currentLevel = currentLevel;
        this.inventory = Bukkit.createInventory(this, GUI_SIZE, "升级饰品: " + accessory.getName());
        update();
    }
    
    public void open() {
        player.openInventory(inventory);
    }
    
    public void update() {
        // 清空GUI
        inventory.clear();
        
        // 顶部：当前饰品（带等级信息）
        ItemStack accessoryItem = CardAccessorySystem.getInstance().getItemManager().createAccessoryItem(accessory.getId(), currentLevel);
        inventory.setItem(4, accessoryItem); // 第一行中间位置
        
        // 检查是否已达到最高等级
        if (currentLevel >= accessory.getMaxLevel()) {
            // 显示已达到最高等级的提示
            ItemStack maxLevelItem = new ItemStack(Material.BARRIER);
            ItemMeta maxLevelMeta = maxLevelItem.getItemMeta();
            if (maxLevelMeta != null) {
                maxLevelMeta.setDisplayName(ChatColor.RED + "已达最高等级");
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "该饰品已达到最高等级 " + accessory.getMaxLevel());
                maxLevelMeta.setLore(lore);
                maxLevelItem.setItemMeta(maxLevelMeta);
            }
            inventory.setItem(49, maxLevelItem); // 底部中间位置
            return;
        }
        
        // 获取下一等级的升级信息
        int nextLevel = currentLevel + 1;
        UpgradeLevel upgradeLevel = accessory.getUpgradeLevel(nextLevel);
        
        if (upgradeLevel == null) {
            // 没有下一等级的升级信息
            ItemStack noUpgradeItem = new ItemStack(Material.BARRIER);
            ItemMeta noUpgradeMeta = noUpgradeItem.getItemMeta();
            if (noUpgradeMeta != null) {
                noUpgradeMeta.setDisplayName(ChatColor.RED + "无法升级");
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "没有下一等级的升级信息");
                noUpgradeMeta.setLore(lore);
                noUpgradeItem.setItemMeta(noUpgradeMeta);
            }
            inventory.setItem(49, noUpgradeItem); // 底部中间位置
            return;
        }
        
        // 中部：消耗物品展示格（第2-4行）
        List<UpgradeCost> costs = upgradeLevel.getCosts();
        int slotIndex = 18; // 从第3行开始放置消耗物品
        boolean canAfford = true;
        
        for (UpgradeCost cost : costs) {
            if (slotIndex >= 36) break; // 最多显示18个消耗物品（第3-4行）
            
            ItemStack costItem;
            switch (cost.getType().toLowerCase()) {
                case "item":
                    costItem = CustomItemLoader.loadItem(cost.getId());
                    if (costItem.getType() == Material.AIR) {
                        costItem = new ItemStack(Material.BARRIER);
                    }
                    break;
                case "currency":
                    // 对于货币类型，使用命名物品表示
                    costItem = new ItemStack(Material.PAPER);
                    break;
                default:
                    costItem = new ItemStack(Material.BARRIER);
                    break;
            }
            
            ItemMeta costMeta = costItem.getItemMeta();
            if (costMeta != null) {
                if ("currency".equals(cost.getType().toLowerCase())) {
                    // 货币类型显示名称
                    String currencyName = cost.getId();
                    if ("playerpoints".equalsIgnoreCase(currencyName)) {
                        currencyName = "点券";
                    } else if ("coins".equalsIgnoreCase(currencyName)) {
                        currencyName = "金币";
                    }
                    costMeta.setDisplayName(ChatColor.GOLD + currencyName + ": " + ChatColor.WHITE + cost.getAmount());
                    
                    List<String> lore = new ArrayList<>();
                    lore.add(ChatColor.GRAY + "来自 " + currencyName);
                    costMeta.setLore(lore);
                } else {
                    // 物品类型显示数量
                    if (cost.getAmount() > 1) {
                        costMeta.setDisplayName(costItem.getItemMeta().getDisplayName() + " x" + cost.getAmount());
                    }
                }
                costItem.setItemMeta(costMeta);
            }
            
            // 检查玩家是否拥有足够的资源
            boolean hasEnough = checkPlayerHasResource(cost);
            if (!hasEnough) {
                canAfford = false;
                // 添加红色边框效果
                // 使用红色染色玻璃板作为边框
                ItemStack borderItem = new ItemStack(Material.RED_STAINED_GLASS_PANE);
                ItemMeta borderMeta = borderItem.getItemMeta();
                if (borderMeta != null) {
                    borderMeta.setDisplayName(ChatColor.RED + "资源不足");
                    List<String> borderLore = new ArrayList<>();
                    borderLore.add(ChatColor.GRAY + "需要: " + cost.getAmount() + " 个");
                    borderLore.add(ChatColor.GRAY + "你拥有: " + getPlayerResourceAmount(cost) + " 个");
                    borderMeta.setLore(borderLore);
                    borderItem.setItemMeta(borderMeta);
                }
                costItem = borderItem;
            }
            
            inventory.setItem(slotIndex++, costItem);
        }
        
        // 底部：确认和取消按钮（第5-6行）
        // 确认按钮（绿色玻璃板）
        ItemStack confirmButton = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        ItemMeta confirmMeta = confirmButton.getItemMeta();
        if (confirmMeta != null) {
            confirmMeta.setDisplayName(ChatColor.GREEN + "[ " + ChatColor.BOLD + "确认升级" + ChatColor.RESET + ChatColor.GREEN + " ]");
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "点击升级到等级 " + nextLevel);
            if (!canAfford) {
                lore.add("");
                lore.add(ChatColor.RED + "资源不足！");
            }
            confirmMeta.setLore(lore);
            confirmButton.setItemMeta(confirmMeta);
        }
        inventory.setItem(48, confirmButton); // 倒数第二行，靠左
        
        // 取消按钮（红色玻璃板）
        ItemStack cancelButton = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta cancelMeta = cancelButton.getItemMeta();
        if (cancelMeta != null) {
            cancelMeta.setDisplayName(ChatColor.RED + "[ " + ChatColor.BOLD + "取消" + ChatColor.RESET + ChatColor.RED + " ]");
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "关闭升级界面");
            cancelMeta.setLore(lore);
            cancelButton.setItemMeta(cancelMeta);
        }
        inventory.setItem(50, cancelButton); // 倒数第二行，靠右
    }
    
    private boolean checkPlayerHasResource(UpgradeCost cost) {
        switch (cost.getType().toLowerCase()) {
            case "item":
                // 检查玩家背包中的物品
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
                // 检查货币
                if ("playerpoints".equalsIgnoreCase(cost.getId())) {
                    int points = cn.popcraft.cardaccessory.hook.PlayerPointsHook.getPlayerPoints(player);
                    return points >= cost.getAmount();
                } else if ("coins".equalsIgnoreCase(cost.getId())) {
                    return cn.popcraft.cardaccessory.hook.VaultHook.hasPlayerMoney(player, cost.getAmount());
                }
                return false;
                
            default:
                return false;
        }
    }
    
    private int getPlayerResourceAmount(UpgradeCost cost) {
        switch (cost.getType().toLowerCase()) {
            case "item":
                // 检查玩家背包中的物品
                ItemStack item = CustomItemLoader.loadItem(cost.getId());
                if (item.getType() == Material.AIR) return 0;
                
                int count = 0;
                for (ItemStack invItem : player.getInventory().getContents()) {
                    if (invItem != null && invItem.isSimilar(item)) {
                        count += invItem.getAmount();
                    }
                }
                return count;
                
            case "currency":
                // 检查货币
                if ("playerpoints".equalsIgnoreCase(cost.getId())) {
                    return cn.popcraft.cardaccessory.hook.PlayerPointsHook.getPlayerPoints(player);
                } else if ("coins".equalsIgnoreCase(cost.getId())) {
                    return (int) cn.popcraft.cardaccessory.hook.VaultHook.getPlayerBalance(player);
                }
                return 0;
                
            default:
                return 0;
        }
    }
    
    public Player getPlayer() {
        return player;
    }
    
    public Accessory getAccessory() {
        return accessory;
    }
    
    public int getCurrentLevel() {
        return currentLevel;
    }
    
    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}