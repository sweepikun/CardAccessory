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
import cn.popcraft.cardaccessory.manager.EquipManager;
import cn.popcraft.cardaccessory.manager.ItemManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class CardAccessorySystem extends JavaPlugin {

    private static CardAccessorySystem instance;
    private ItemManager itemManager;
    private EquipManager equipManager;
    private boolean mythicMobsEnabled = false;

    @Override
    public void onEnable() {
        instance = this;
        
        // 初始化钩子
        MythicMobsHook.init();
        mythicMobsEnabled = MythicMobsHook.isEnabled();
        
        PlayerPointsHook.init();
        VaultHook.init();
        MythicClassHook.init();
        
        // 保存默认配置
        saveDefaultConfig();
        saveResource("cards.yml", false);
        saveResource("accessories.yml", false);
        
        // 初始化管理器
        itemManager = new ItemManager();
        equipManager = new EquipManager();
        
        // 注册监听器
        getServer().getPluginManager().registerEvents(new EquipListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerDataListener(), this);
        getServer().getPluginManager().registerEvents(new SkillDamageListener(), this);
        getServer().getPluginManager().registerEvents(new GUIListener(), this);
        getServer().getPluginManager().registerEvents(new UpgradeGUIListener(), this);
        
        // 如果MythicMobs启用，注册MythicDamageListener
        if (mythicMobsEnabled) {
            getServer().getPluginManager().registerEvents(new MythicDamageListener(), this);
        }
        
        // 注册命令
        getCommand("cardaccessory").setExecutor(new CardAccessoryCommand());

        getLogger().info("CardAccessorySystem 已启用！");
    }

    @Override
    public void onDisable() {
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