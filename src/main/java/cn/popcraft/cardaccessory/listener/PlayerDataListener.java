package cn.popcraft.cardaccessory.listener;

import cn.popcraft.cardaccessory.CardAccessorySystem;
import cn.popcraft.cardaccessory.manager.DataManager;
import cn.popcraft.cardaccessory.manager.EffectManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerDataListener implements Listener {
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // 异步加载玩家数据
        CardAccessorySystem.getInstance().getServer().getScheduler().runTaskAsynchronously(
            CardAccessorySystem.getInstance(), 
            () -> {
                var equipment = DataManager.loadPlayerData(event.getPlayer().getUniqueId());
                CardAccessorySystem.getInstance().getEquipManager()
                    .setPlayerEquipment(event.getPlayer(), equipment);
                
                // 同步应用卡牌效果
                CardAccessorySystem.getInstance().getServer().getScheduler().runTask(
                    CardAccessorySystem.getInstance(),
                    () -> EffectManager.applyCardEffects(event.getPlayer())
                );
            }
        );
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // 移除卡牌效果
        EffectManager.removeCardEffects(event.getPlayer());
        
        // 清除属性修饰符记录
        EffectManager.clearPlayerModifiers(event.getPlayer());
        
        // 保存玩家数据
        var equipment = CardAccessorySystem.getInstance().getEquipManager()
            .getPlayerEquipment(event.getPlayer());
        DataManager.savePlayerData(event.getPlayer().getUniqueId(), equipment);
        
        // 清理内存中的数据
        CardAccessorySystem.getInstance().getEquipManager()
            .removePlayerEquipment(event.getPlayer());
    }
}