package cn.popcraft.cardaccessory.model;

public class EquipmentSlot {
    private final String id;
    private final int level;

    public EquipmentSlot(String id, int level) {
        this.id = id;
        this.level = level;
    }

    public String getId() {
        return id;
    }

    public int getLevel() {
        return level;
    }
    
    public boolean isEmpty() {
        return id == null || id.isEmpty();
    }
}