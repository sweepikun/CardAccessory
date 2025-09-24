package cn.popcraft.cardaccessory.model;

public class PlayerEquipment {
    private final EquipmentSlot[] cardSlots = new EquipmentSlot[4]; // 4个卡牌槽位
    private final EquipmentSlot[] accessorySlots = new EquipmentSlot[2]; // 2个饰品槽位

    public PlayerEquipment() {
        // 初始化为空槽位
        for (int i = 0; i < cardSlots.length; i++) {
            cardSlots[i] = new EquipmentSlot("", 1); // 默认等级为1
        }
        for (int i = 0; i < accessorySlots.length; i++) {
            accessorySlots[i] = new EquipmentSlot("", 1); // 默认等级为1
        }
    }

    public EquipmentSlot[] getCardSlots() {
        EquipmentSlot[] slots = new EquipmentSlot[cardSlots.length];
        for (int i = 0; i < cardSlots.length; i++) {
            slots[i] = new EquipmentSlot(cardSlots[i].getId(), cardSlots[i].getLevel());
        }
        return slots;
    }

    public EquipmentSlot[] getAccessorySlots() {
        EquipmentSlot[] slots = new EquipmentSlot[accessorySlots.length];
        for (int i = 0; i < accessorySlots.length; i++) {
            slots[i] = new EquipmentSlot(accessorySlots[i].getId(), accessorySlots[i].getLevel());
        }
        return slots;
    }

    public EquipmentSlot getCard(int slot) {
        if (slot < 0 || slot >= cardSlots.length) {
            return null;
        }
        return cardSlots[slot];
    }

    public EquipmentSlot getAccessory(int slot) {
        if (slot < 0 || slot >= accessorySlots.length) {
            return null;
        }
        return accessorySlots[slot];
    }

    public boolean setCard(int slot, String cardId, int level) {
        if (slot < 0 || slot >= cardSlots.length) {
            return false;
        }
        cardSlots[slot] = new EquipmentSlot(cardId, level);
        return true;
    }

    public boolean setAccessory(int slot, String accessoryId, int level) {
        if (slot < 0 || slot >= accessorySlots.length) {
            return false;
        }
        accessorySlots[slot] = new EquipmentSlot(accessoryId, level);
        return true;
    }

    public boolean removeCard(int slot) {
        if (slot < 0 || slot >= cardSlots.length) {
            return false;
        }
        cardSlots[slot] = new EquipmentSlot("", 1);
        return true;
    }

    public boolean removeAccessory(int slot) {
        if (slot < 0 || slot >= accessorySlots.length) {
            return false;
        }
        accessorySlots[slot] = new EquipmentSlot("", 1);
        return true;
    }
    
    // 为了向后兼容，保留原来的方法
    public boolean setCard(int slot, String cardId) {
        return setCard(slot, cardId, 1);
    }
    
    public boolean setAccessory(int slot, String accessoryId) {
        return setAccessory(slot, accessoryId, 1);
    }
}