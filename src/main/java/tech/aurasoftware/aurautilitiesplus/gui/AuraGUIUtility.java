package tech.aurasoftware.aurautilitiesplus.gui;

import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;
import tech.aurasoftware.aurautilitiesplus.gui.listener.AuraGUIMetaData;
import tech.aurasoftware.aurautilitiesplus.util.Placeholder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@UtilityClass
public class AuraGUIUtility {

    private static final Map<UUID, OpenedAuraGUI> OPENED_GUI = new HashMap<>();

    public void openGUI(Player player, AuraGUI auraGUI, List<Placeholder> placeholderList, AuraGUIMetaData auraGUIMetaData, AuraGUIItem... additionalItems){
        player.openInventory(auraGUI.createInventory(placeholderList, additionalItems));

        OpenedAuraGUI openedAuraGUI = new OpenedAuraGUI(auraGUI, auraGUIMetaData);
        openedAuraGUI.addAdditionalItems(additionalItems);

        OPENED_GUI.put(player.getUniqueId(), openedAuraGUI);
    }

    public void removeGUI(Player player){
        OPENED_GUI.remove(player.getUniqueId());
    }

    public OpenedAuraGUI getOpenedGUI(Player player){
        return OPENED_GUI.get(player.getUniqueId());
    }


}
