package tech.aurasoftware.aurautilitiesplus.gui;

import lombok.Getter;
import tech.aurasoftware.aurautilitiesplus.gui.listener.AuraGUIMetaData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class OpenedAuraGUI {

    private final AuraGUI auraGUI;
    private final AuraGUIMetaData auraGUIMetaData;

    public OpenedAuraGUI(AuraGUI auraGUI, AuraGUIMetaData auraGUIMetaData) {
        this.auraGUI = auraGUI;
        this.auraGUIMetaData = auraGUIMetaData;
        this.additionalItems = new ArrayList<>();
    }

    private final List<AuraGUIItem> additionalItems;

    public void addAdditionalItems(AuraGUIItem... auraItems){
        additionalItems.addAll(Arrays.asList(auraItems));
    }

    public AuraGUIItem getAuraGUIItem(int slot){

        for(AuraGUIItem auraGUIItem : additionalItems){
            for(int itemSlot: auraGUIItem.getSlots()){
                if(itemSlot == slot){
                    return auraGUIItem;
                }
            }
        }

        for(AuraGUIItem auraGUIItem : auraGUI.getItems()){
            for(int itemSlot: auraGUIItem.getSlots()){
                if(itemSlot == slot){
                    return auraGUIItem;
                }
            }
        }
        return null;
    }

}
