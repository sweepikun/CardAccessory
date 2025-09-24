package cn.popcraft.cardaccessory.manager;

import cn.popcraft.cardaccessory.CardAccessorySystem;
import cn.popcraft.cardaccessory.model.PlayerEquipment;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class DataManager {
    
    public static void savePlayerData(UUID playerUUID, PlayerEquipment equipment) {
        File dataFolder = new File(CardAccessorySystem.getInstance().getDataFolder(), "player_data");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        
        File dataFile = new File(dataFolder, playerUUID.toString() + ".yml");
        YamlConfiguration config = new YamlConfiguration();
        
        // 保存卡牌数据
        for (int i = 0; i < 4; i++) {
            var cardSlot = equipment.getCard(i);
            config.set("player-equipment.cards." + i + ".id", cardSlot.getId());
            config.set("player-equipment.cards." + i + ".level", cardSlot.getLevel());
        }
        
        // 保存饰品数据
        for (int i = 0; i < 2; i++) {
            var accessorySlot = equipment.getAccessory(i);
            config.set("player-equipment.accessories." + i + ".id", accessorySlot.getId());
            config.set("player-equipment.accessories." + i + ".level", accessorySlot.getLevel());
        }
        
        try {
            config.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static PlayerEquipment loadPlayerData(UUID playerUUID) {
        File dataFile = new File(
            new File(CardAccessorySystem.getInstance().getDataFolder(), "player_data"),
            playerUUID.toString() + ".yml"
        );
        
        PlayerEquipment equipment = new PlayerEquipment();
        
        if (!dataFile.exists()) {
            return equipment;
        }
        
        YamlConfiguration config = YamlConfiguration.loadConfiguration(dataFile);
        
        // 加载卡牌数据
        for (int i = 0; i < 4; i++) {
            String cardId = config.getString("player-equipment.cards." + i + ".id", "");
            int level = config.getInt("player-equipment.cards." + i + ".level", 1);
            equipment.setCard(i, cardId, level);
        }
        
        // 加载饰品数据
        for (int i = 0; i < 2; i++) {
            String accessoryId = config.getString("player-equipment.accessories." + i + ".id", "");
            int level = config.getInt("player-equipment.accessories." + i + ".level", 1);
            equipment.setAccessory(i, accessoryId, level);
        }
        
        return equipment;
    }
}