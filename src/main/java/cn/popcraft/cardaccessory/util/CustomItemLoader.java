package cn.popcraft.cardaccessory.util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class CustomItemLoader {

    public static ItemStack loadItem(String identifier) {
        if (identifier == null || identifier.isEmpty()) {
            return new ItemStack(Material.AIR);
        }

        // 尝试 ItemsAdder（如果启用）
        if (identifier.contains(":")) {
            Plugin iaPlugin = Bukkit.getPluginManager().getPlugin("ItemsAdder");
            if (iaPlugin != null && iaPlugin.isEnabled()) {
                try {
                    // 注意：这里我们假设ItemsAdder API可用
                    // 实际使用时需要确保正确引入ItemsAdder API
                    Class<?> itemsAdderClass = Class.forName("dev.lone.itemsadder.api.ItemsAdder");
                    ItemStack iaItem = (ItemStack) itemsAdderClass.getMethod("getCustomItem", String.class)
                            .invoke(null, identifier);
                    if (iaItem != null && !iaItem.getType().isAir()) {
                        return iaItem;
                    }
                } catch (Exception e) {
                    // ItemsAdder API 调用失败
                    e.printStackTrace();
                }
            }
        }

        // 回退到原版 Material
        try {
            Material mat = Material.matchMaterial(identifier.toUpperCase());
            if (mat != null && mat.isItem()) {
                return new ItemStack(mat);
            }
        } catch (Exception ignored) {
        }

        // 无效物品
        return new ItemStack(Material.BARRIER);
    }
}