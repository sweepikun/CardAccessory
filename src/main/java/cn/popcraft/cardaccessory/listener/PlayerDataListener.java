package cn.popcraft.cardaccessory.listener;

import cn.popcraft.cardaccessory.CardAccessorySystem;
import cn.popcraft.cardaccessory.manager.DataManager;
import cn.popcraft.cardaccessory.manager.EffectManager;
import cn.popcraft.cardaccessory.model.PlayerEquipment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerDataListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        CardAccessorySystem.getInstance().getServer().getScheduler().runTaskAsynchronously(
            CardAccessorySystem.getInstance(),
            () -> {
                PlayerEquipment equipment = DataManager.loadPlayerData(player.getUniqueId());

                CardAccessorySystem.getInstance().getServer().getScheduler().runTask(
                    CardAccessorySystem.getInstance(),
                    () -> {
                        if (!player.isOnline()) return;

                        CardAccessorySystem.getInstance().getEquipManager()
                            .setPlayerEquipment(player, equipment);

                        EffectManager.removeCardEffects(player);
                        EffectManager.applyCardEffects(player);
                    }
                );
            }
        );
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        EffectManager.removeCardEffects(player);
        EffectManager.clearPlayerModifiers(player);

        PlayerEquipment equipment = CardAccessorySystem.getInstance().getEquipManager()
            .getPlayerEquipment(player);
        DataManager.savePlayerData(player.getUniqueId(), equipment);

        CardAccessorySystem.getInstance().getEquipManager()
            .removePlayerEquipment(player);
    }
}
