package cn.popcraft.cardaccessory.model;

import java.util.Map;
import java.util.List;

public class Accessory {
    private final String id;
    private final String name;
    private final String[] lore;
    private final String itemIdentifier;
    private final double skillDamageMultiplier;
    private final String permission;
    private final String requiredClass;
    private final int maxLevel;
    private final Map<Integer, UpgradeLevel> upgradeLevels;
    private final List<Effect> effects; // 新增：饰品效果列表

    public Accessory(String id, String name, String[] lore, String itemIdentifier, 
                     double skillDamageMultiplier, String permission, String requiredClass,
                     int maxLevel, Map<Integer, UpgradeLevel> upgradeLevels,
                     List<Effect> effects) { // 新增：构造函数参数
        this.id = id;
        this.name = name;
        this.lore = lore;
        this.itemIdentifier = itemIdentifier;
        this.skillDamageMultiplier = skillDamageMultiplier;
        this.permission = permission;
        this.requiredClass = requiredClass;
        this.maxLevel = maxLevel;
        this.upgradeLevels = upgradeLevels;
        this.effects = effects; // 新增：初始化效果列表
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String[] getLore() {
        return lore;
    }

    public String getItemIdentifier() {
        return itemIdentifier;
    }

    public double getSkillDamageMultiplier() {
        return skillDamageMultiplier;
    }
    
    public String getPermission() {
        return permission;
    }
    
    public String getRequiredClass() {
        return requiredClass;
    }
    
    public int getMaxLevel() {
        return maxLevel;
    }
    
    public Map<Integer, UpgradeLevel> getUpgradeLevels() {
        return upgradeLevels;
    }
    
    public UpgradeLevel getUpgradeLevel(int level) {
        return upgradeLevels.get(level);
    }
    
    public boolean hasUpgradeLevel(int level) {
        return upgradeLevels.containsKey(level);
    }
    
    // 新增：获取饰品效果列表
    public List<Effect> getEffects() {
        return effects;
    }
}