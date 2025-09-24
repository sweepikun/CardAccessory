package cn.popcraft.cardaccessory.command;

import cn.popcraft.cardaccessory.gui.AccessoryGUI;
import cn.popcraft.cardaccessory.gui.CardGUI;
import cn.popcraft.cardaccessory.gui.upgrade.UpgradeAccessoryGUI;
import cn.popcraft.cardaccessory.gui.upgrade.UpgradeCardGUI;
import cn.popcraft.cardaccessory.model.EquipmentSlot;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CardAccessoryCommand implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("该命令只能由玩家执行");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length == 0) {
            // 默认打开卡牌GUI
            CardGUI cardGUI = new CardGUI(player);
            cardGUI.open();
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "card":
            case "cards":
                CardGUI cardGUI = new CardGUI(player);
                cardGUI.open();
                break;
            case "accessory":
            case "accessories":
                AccessoryGUI accessoryGUI = new AccessoryGUI(player);
                accessoryGUI.open();
                break;
            case "upgrade":
                if (args.length < 3) {
                    player.sendMessage("用法: /cas upgrade <type> <id>");
                    return true;
                }
                
                String type = args[1].toLowerCase();
                String id = args[2];
                
                if ("card".equals(type) || "cards".equals(type)) {
                    // 升级卡牌
                    var card = cn.popcraft.cardaccessory.CardAccessorySystem.getInstance().getItemManager().getCard(id);
                    if (card == null) {
                        player.sendMessage("未找到ID为 " + id + " 的卡牌");
                        return true;
                    }
                    
                    // 查找玩家是否装备了这张卡牌
                    int level = 1;
                    boolean equipped = false;
                    var equipment = cn.popcraft.cardaccessory.CardAccessorySystem.getInstance().getEquipManager().getPlayerEquipment(player);
                    for (int i = 0; i < 4; i++) {
                        EquipmentSlot slot = equipment.getCard(i);
                        if (id.equals(slot.getId())) {
                            level = slot.getLevel();
                            equipped = true;
                            break;
                        }
                    }
                    
                    if (!equipped) {
                        player.sendMessage("你没有装备这张卡牌");
                        return true;
                    }
                    
                    // 检查权限
                    if (!card.getPermission().isEmpty() && !player.hasPermission(card.getPermission())) {
                        player.sendMessage("你没有权限升级这张卡牌");
                        return true;
                    }
                    
                    // 检查职业
                    if (!cn.popcraft.cardaccessory.hook.MythicClassHook.checkPlayerClass(player, card.getRequiredClass())) {
                        player.sendMessage("你的职业不符合要求");
                        return true;
                    }
                    
                    // 打开升级GUI
                    UpgradeCardGUI upgradeGUI = new UpgradeCardGUI(player, card, level);
                    upgradeGUI.open();
                } else if ("accessory".equals(type) || "accessories".equals(type)) {
                    // 升级饰品
                    var accessory = cn.popcraft.cardaccessory.CardAccessorySystem.getInstance().getItemManager().getAccessory(id);
                    if (accessory == null) {
                        player.sendMessage("未找到ID为 " + id + " 的饰品");
                        return true;
                    }
                    
                    // 查找玩家是否装备了这个饰品
                    int level = 1;
                    boolean equipped = false;
                    var equipment = cn.popcraft.cardaccessory.CardAccessorySystem.getInstance().getEquipManager().getPlayerEquipment(player);
                    for (int i = 0; i < 2; i++) {
                        EquipmentSlot slot = equipment.getAccessory(i);
                        if (id.equals(slot.getId())) {
                            level = slot.getLevel();
                            equipped = true;
                            break;
                        }
                    }
                    
                    if (!equipped) {
                        player.sendMessage("你没有装备这个饰品");
                        return true;
                    }
                    
                    // 检查权限
                    if (!accessory.getPermission().isEmpty() && !player.hasPermission(accessory.getPermission())) {
                        player.sendMessage("你没有权限升级这个饰品");
                        return true;
                    }
                    
                    // 检查职业
                    if (!cn.popcraft.cardaccessory.hook.MythicClassHook.checkPlayerClass(player, accessory.getRequiredClass())) {
                        player.sendMessage("你的职业不符合要求");
                        return true;
                    }
                    
                    // 打开升级GUI
                    UpgradeAccessoryGUI upgradeGUI = new UpgradeAccessoryGUI(player, accessory, level);
                    upgradeGUI.open();
                } else {
                    player.sendMessage("未知类型。用法: /cas upgrade <card|accessory> <id>");
                }
                break;
            default:
                player.sendMessage("未知子命令。用法: /cardaccessory [card|accessory|upgrade]");
                break;
        }
        
        return true;
    }
}