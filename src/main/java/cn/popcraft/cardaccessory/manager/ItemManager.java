package cn.popcraft.cardaccessory.manager;

import cn.popcraft.cardaccessory.CardAccessorySystem;
import cn.popcraft.cardaccessory.model.*;
import cn.popcraft.cardaccessory.util.CustomItemLoader;
import cn.popcraft.cardaccessory.util.NBTKeys;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.util.*;

public class ItemManager {
    private final Map<String, Card> cards = new HashMap<>();
    private final Map<String, Accessory> accessories = new HashMap<>();

    public ItemManager() {
        loadCards();
        loadAccessories();
    }

    private void loadCards() {
        File file = new File(CardAccessorySystem.getInstance().getDataFolder(), "cards.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        if (!config.contains("cards")) {
            return;
        }

        for (String id : config.getConfigurationSection("cards").getKeys(false)) {
            String path = "cards." + id + ".";
            String name = config.getString(path + "name", "&c未知卡牌");
            List<String> loreList = config.getStringList(path + "lore");
            String[] lore = loreList.toArray(new String[0]);
            String item = config.getString(path + "item", "PAPER");
            String permission = config.getString(path + "permission", "");
            String requiredClass = config.getString(path + "required_class", "");
            
            Map<String, Double> baseAttributes = new HashMap<>();
            if (config.isConfigurationSection(path + "base_attributes")) {
                for (String attr : config.getConfigurationSection(path + "base_attributes").getKeys(false)) {
                    baseAttributes.put(attr, config.getDouble(path + "base_attributes." + attr));
                }
            }
            
            // 加载升级系统
            int maxLevel = 1;
            Map<Integer, UpgradeLevel> upgradeLevels = new HashMap<>();
            
            if (config.isConfigurationSection(path + "upgrade")) {
                maxLevel = config.getInt(path + "upgrade.max_level", 1);
                
                if (config.isConfigurationSection(path + "upgrade.levels")) {
                    for (String levelStr : config.getConfigurationSection(path + "upgrade.levels").getKeys(false)) {
                        try {
                            int level = Integer.parseInt(levelStr);
                            String levelPath = path + "upgrade.levels." + level + ".";
                            
                            Map<String, Double> levelAttributes = new HashMap<>();
                            if (config.isConfigurationSection(levelPath + "attributes")) {
                                for (String attr : config.getConfigurationSection(levelPath + "attributes").getKeys(false)) {
                                    levelAttributes.put(attr, config.getDouble(levelPath + "attributes." + attr));
                                }
                            }
                            
                            List<UpgradeCost> costs = new ArrayList<>();
                            if (config.isList(levelPath + "cost_to_next")) {
                                for (Map<?, ?> costMap : config.getMapList(levelPath + "cost_to_next")) {
                                    String type = (String) costMap.get("type");
                                    String costId = (String) costMap.get("id");
                                    int amount = (Integer) costMap.get("amount");
                                    costs.add(new UpgradeCost(type, costId, amount));
                                }
                            }
                            
                            upgradeLevels.put(level, new UpgradeLevel(level, levelAttributes, costs));
                        } catch (NumberFormatException e) {
                            // 无效的等级，跳过
                        }
                    }
                }
            }

            cards.put(id, new Card(id, name, lore, item, baseAttributes, permission, requiredClass, maxLevel, upgradeLevels));
        }
    }

    private void loadAccessories() {
        File file = new File(CardAccessorySystem.getInstance().getDataFolder(), "accessories.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        if (!config.contains("accessories")) {
            return;
        }

        for (String id : config.getConfigurationSection("accessories").getKeys(false)) {
            String path = "accessories." + id + ".";
            String name = config.getString(path + "name", "&c未知饰品");
            List<String> loreList = config.getStringList(path + "lore");
            String[] lore = loreList.toArray(new String[0]);
            String item = config.getString(path + "item", "PAPER");
            double multiplier = config.getDouble(path + "base_skill_damage_multiplier", 1.0);
            String permission = config.getString(path + "permission", "");
            String requiredClass = config.getString(path + "required_class", "");
            
            // 加载升级系统
            int maxLevel = 1;
            Map<Integer, UpgradeLevel> upgradeLevels = new HashMap<>();
            
            if (config.isConfigurationSection(path + "upgrade")) {
                maxLevel = config.getInt(path + "upgrade.max_level", 1);
                
                if (config.isConfigurationSection(path + "upgrade.levels")) {
                    for (String levelStr : config.getConfigurationSection(path + "upgrade.levels").getKeys(false)) {
                        try {
                            int level = Integer.parseInt(levelStr);
                            String levelPath = path + "upgrade.levels." + level + ".";
                            
                            Map<String, Double> levelAttributes = new HashMap<>();
                            // 饰品的升级属性与卡牌类似
                            if (config.isConfigurationSection(levelPath + "attributes")) {
                                for (String attr : config.getConfigurationSection(levelPath + "attributes").getKeys(false)) {
                                    levelAttributes.put(attr, config.getDouble(levelPath + "attributes." + attr));
                                }
                            }
                            
                            List<UpgradeCost> costs = new ArrayList<>();
                            if (config.isList(levelPath + "cost_to_next")) {
                                for (Map<?, ?> costMap : config.getMapList(levelPath + "cost_to_next")) {
                                    String type = (String) costMap.get("type");
                                    String costId = (String) costMap.get("id");
                                    int amount = (Integer) costMap.get("amount");
                                    costs.add(new UpgradeCost(type, costId, amount));
                                }
                            }
                            
                            upgradeLevels.put(level, new UpgradeLevel(level, levelAttributes, costs));
                        } catch (NumberFormatException e) {
                            // 无效的等级，跳过
                        }
                    }
                }
            }

            accessories.put(id, new Accessory(id, name, lore, item, multiplier, permission, requiredClass, maxLevel, upgradeLevels));
        }
    }

    public ItemStack createCardItem(String cardId, int level) {
        Card card = cards.get(cardId);
        if (card == null) return new ItemStack(org.bukkit.Material.BARRIER);

        ItemStack item = CustomItemLoader.loadItem(card.getItemIdentifier());
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', card.getName()));
            
            List<String> lore = new ArrayList<>();
            // 添加等级信息
            lore.add(ChatColor.GRAY + "等级: " + level + "/" + card.getMaxLevel());
            
            for (String line : card.getLore()) {
                lore.add(ChatColor.translateAlternateColorCodes('&', line));
            }
            
            // 如果有升级等级，显示升级后的属性
            if (level > 1 && card.hasUpgradeLevel(level)) {
                UpgradeLevel upgradeLevel = card.getUpgradeLevel(level);
                lore.add(""); // 空行
                lore.add(ChatColor.YELLOW + "升级属性:");
                for (Map.Entry<String, Double> entry : upgradeLevel.getAttributes().entrySet()) {
                    lore.add(ChatColor.GRAY + entry.getKey() + ": +" + entry.getValue());
                }
            }
            
            meta.setLore(lore);
            
            // 添加PDC标签
            meta.getPersistentDataContainer().set(NBTKeys.CARD_ID, PersistentDataType.STRING, cardId);
            
            item.setItemMeta(meta);
        }
        return item;
    }
    
    public ItemStack createCardItem(String cardId) {
        return createCardItem(cardId, 1);
    }

    public ItemStack createAccessoryItem(String accessoryId, int level) {
        Accessory accessory = accessories.get(accessoryId);
        if (accessory == null) return new ItemStack(org.bukkit.Material.BARRIER);

        ItemStack item = CustomItemLoader.loadItem(accessory.getItemIdentifier());
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', accessory.getName()));
            
            List<String> lore = new ArrayList<>();
            // 添加等级信息
            lore.add(ChatColor.GRAY + "等级: " + level + "/" + accessory.getMaxLevel());
            
            for (String line : accessory.getLore()) {
                lore.add(ChatColor.translateAlternateColorCodes('&', line));
            }
            
            // 如果有升级等级，显示升级后的属性
            if (level > 1 && accessory.hasUpgradeLevel(level)) {
                UpgradeLevel upgradeLevel = accessory.getUpgradeLevel(level);
                lore.add(""); // 空行
                lore.add(ChatColor.YELLOW + "升级属性:");
                for (Map.Entry<String, Double> entry : upgradeLevel.getAttributes().entrySet()) {
                    lore.add(ChatColor.GRAY + entry.getKey() + ": +" + entry.getValue());
                }
            }
            
            meta.setLore(lore);
            
            // 添加PDC标签
            meta.getPersistentDataContainer().set(NBTKeys.ACCESSORY_ID, PersistentDataType.STRING, accessoryId);
            
            item.setItemMeta(meta);
        }
        return item;
    }
    
    public ItemStack createAccessoryItem(String accessoryId) {
        return createAccessoryItem(accessoryId, 1);
    }

    public Card getCard(String id) {
        return cards.get(id);
    }

    public Accessory getAccessory(String id) {
        return accessories.get(id);
    }

    public Collection<Card> getAllCards() {
        return cards.values();
    }

    public Collection<Accessory> getAllAccessories() {
        return accessories.values();
    }
    
    // 通过物品获取卡牌ID
    public String getCardId(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return null;
        }
        
        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().get(NBTKeys.CARD_ID, PersistentDataType.STRING);
    }
    
    // 通过物品获取饰品ID
    public String getAccessoryId(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return null;
        }
        
        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().get(NBTKeys.ACCESSORY_ID, PersistentDataType.STRING);
    }
    
    // 检查物品是否为卡牌
    public boolean isCard(ItemStack item) {
        return getCardId(item) != null;
    }
    
    // 检查物品是否为饰品
    public boolean isAccessory(ItemStack item) {
        return getAccessoryId(item) != null;
    }
}