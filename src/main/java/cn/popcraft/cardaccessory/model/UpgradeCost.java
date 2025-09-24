package cn.popcraft.cardaccessory.model;

public class UpgradeCost {
    private final String type; // "item", "currency"
    private final String id;   // 物品ID或货币ID
    private final int amount;  // 数量

    public UpgradeCost(String type, String id, int amount) {
        this.type = type;
        this.id = id;
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public int getAmount() {
        return amount;
    }
}