package tech.aurasoftware.aurautilitiesplus.gui.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import tech.aurasoftware.aurautilitiesplus.gui.AuraGUIItem;
import tech.aurasoftware.aurautilitiesplus.gui.AuraGUIUtility;
import tech.aurasoftware.aurautilitiesplus.gui.OpenedAuraGUI;
import tech.aurasoftware.aurautilitiesplus.gui.event.AuraGUIClickEvent;

public class InventoryClickListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){

        if(!(e.getWhoClicked() instanceof Player)){
            return;
        }

        Player player = (Player) e.getWhoClicked();

        OpenedAuraGUI openedAuraGUI = AuraGUIUtility.getOpenedGUI(player);

        if(openedAuraGUI == null){
            return;
        }

        AuraGUIItem auraGUIItem = openedAuraGUI.getAuraGUIItem(e.getSlot());

        if(auraGUIItem == null){
            return;
        }

        AuraGUIClickEvent auraGUIClickEvent = new AuraGUIClickEvent(openedAuraGUI.getAuraGUI().getId(), auraGUIItem.getKey(), player);

        Bukkit.getServer().getPluginManager().callEvent(auraGUIClickEvent);

        e.setCancelled(auraGUIClickEvent.isCancelled());
    }

}
