package cn.popcraft.cardaccessory.model;

import java.util.List;
import java.util.Map;

public class UpgradeLevel {
    private final int level;
    private final Map<String, Double> attributes;
    private final List<UpgradeCost> costs;

    public UpgradeLevel(int level, Map<String, Double> attributes, List<UpgradeCost> costs) {
        this.level = level;
        this.attributes = attributes;
        this.costs = costs;
    }

    public int getLevel() {
        return level;
    }

    public Map<String, Double> getAttributes() {
        return attributes;
    }

    public List<UpgradeCost> getCosts() {
        return costs;
    }
}