package cn.popcraft.cardaccessory.listener;

import cn.popcraft.cardaccessory.gui.AccessoryGUI;
import cn.popcraft.cardaccessory.gui.CardGUI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class GUIListener implements Listener {
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) {
            return;
        }
        
        if (event.getInventory().getHolder() instanceof CardGUI) {
            event.setCancelled(true);
            
            CardGUI cardGUI = (CardGUI) event.getInventory().getHolder();
            cardGUI.handleItemClick(event.getSlot(), event.getCurrentItem());
        } else if (event.getInventory().getHolder() instanceof AccessoryGUI) {
            event.setCancelled(true);
            
            AccessoryGUI accessoryGUI = (AccessoryGUI) event.getInventory().getHolder();
            accessoryGUI.handleItemClick(event.getSlot(), event.getCurrentItem());
        }
    }
}