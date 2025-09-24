package cn.popcraft.cardaccessory.model;

import java.util.List;
import java.util.Map;

public class Card {
    private final String id;
    private final String name;
    private final String[] lore;
    private final String itemIdentifier;
    private final Map<String, Double> attributes;
    private final String permission;
    private final String requiredClass;
    private final int maxLevel;
    private final Map<Integer, UpgradeLevel> upgradeLevels;

    public Card(String id, String name, String[] lore, String itemIdentifier, 
                Map<String, Double> attributes, String permission, String requiredClass,
                int maxLevel, Map<Integer, UpgradeLevel> upgradeLevels) {
        this.id = id;
        this.name = name;
        this.lore = lore;
        this.itemIdentifier = itemIdentifier;
        this.attributes = attributes;
        this.permission = permission;
        this.requiredClass = requiredClass;
        this.maxLevel = maxLevel;
        this.upgradeLevels = upgradeLevels;
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

    public Map<String, Double> getAttributes() {
        return attributes;
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
}