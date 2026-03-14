package cn.popcraft.cardaccessory;

import cn.popcraft.cardaccessory.command.CardAccessoryCommand;
import cn.popcraft.cardaccessory.hook.MythicClassHook;
import cn.popcraft.cardaccessory.hook.MythicMobsHook;
import cn.popcraft.cardaccessory.hook.PlayerPointsHook;
import cn.popcraft.cardaccessory.hook.VaultHook;
import cn.popcraft.cardaccessory.listener.EquipListener;
import cn.popcraft.cardaccessory.listener.GUIListener;
import cn.popcraft.cardaccessory.listener.MythicDamageListener;
import cn.popcraft.cardaccessory.listener.PlayerDataListener;
import cn.popcraft.cardaccessory.listener.SkillDamageListener;
import cn.popcraft.cardaccessory.listener.UpgradeGUIListener;
import cn.popcraft.cardaccessory.manager.DataManager;
import cn.popcraft.cardaccessory.manager.EquipManager;
import cn.popcraft.cardaccessory.manager.ItemManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public final class CardAccessorySystem extends JavaPlugin {

    private static CardAccessorySystem instance;
    private ItemManager itemManager;
    private EquipManager equipManager;
    private boolean mythicMobsEnabled = false;
    private BukkitTask autoSaveTask;

    @Override
    public void onEnable() {
        instance = this;

        MythicMobsHook.init();
        mythicMobsEnabled = MythicMobsHook.isEnabled();

        PlayerPointsHook.init();
        VaultHook.init();
        MythicClassHook.init();

        saveDefaultConfig();
        saveResource("cards.yml", false);
        saveResource("accessories.yml", false);

        itemManager = new ItemManager();
        equipManager = new EquipManager();

        getServer().getPluginManager().registerEvents(new EquipListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerDataListener(), this);
        getServer().getPluginManager().registerEvents(new SkillDamageListener(), this);
        getServer().getPluginManager().registerEvents(new GUIListener(), this);
        getServer().getPluginManager().registerEvents(new UpgradeGUIListener(), this);

        if (mythicMobsEnabled) {
            getServer().getPluginManager().registerEvents(new MythicDamageListener(), this);
        }

        getCommand("cardaccessory").setExecutor(new CardAccessoryCommand());

        // 每5分钟自动保存所有在线玩家数据
        autoSaveTask = getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                var equipment = equipManager.getPlayerEquipment(player);
                DataManager.savePlayerData(player.getUniqueId(), equipment);
            }
        }, 6000L, 6000L); // 5分钟 = 6000 ticks

        getLogger().info("CardAccessorySystem 已启用！");
    }

    @Override
    public void onDisable() {
        // 取消自动保存任务
        if (autoSaveTask != null) {
            autoSaveTask.cancel();
        }

        // 保存所有在线玩家数据
        for (Player player : Bukkit.getOnlinePlayers()) {
            EffectManager.removeCardEffects(player);
            EffectManager.clearPlayerModifiers(player);

            var equipment = equipManager.getPlayerEquipment(player);
            DataManager.savePlayerData(player.getUniqueId(), equipment);
        }

        getLogger().info("CardAccessorySystem 已禁用！");
    }

    public static CardAccessorySystem getInstance() {
        return instance;
    }

    public ItemManager getItemManager() {
        return itemManager;
    }

    public EquipManager getEquipManager() {
        return equipManager;
    }

    public boolean isMythicMobsEnabled() {
        return mythicMobsEnabled;
    }
}
