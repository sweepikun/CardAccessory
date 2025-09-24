package cn.popcraft.cardaccessory.manager;

import cn.popcraft.cardaccessory.model.PlayerEquipment;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EquipManager {
    private final Map<UUID, PlayerEquipment> playerEquipmentMap = new HashMap<>();

    public PlayerEquipment getPlayerEquipment(UUID playerUUID) {
        return playerEquipmentMap.computeIfAbsent(playerUUID, uuid -> new PlayerEquipment());
    }

    public PlayerEquipment getPlayerEquipment(Player player) {
        return getPlayerEquipment(player.getUniqueId());
    }

    public void setPlayerEquipment(UUID playerUUID, PlayerEquipment equipment) {
        playerEquipmentMap.put(playerUUID, equipment);
    }

    public void setPlayerEquipment(Player player, PlayerEquipment equipment) {
        setPlayerEquipment(player.getUniqueId(), equipment);
    }

    public void removePlayerEquipment(UUID playerUUID) {
        playerEquipmentMap.remove(playerUUID);
    }

    public void removePlayerEquipment(Player player) {
        removePlayerEquipment(player.getUniqueId());
    }
}