package tech.aurasoftware.aurautilitiesplus.gui;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import tech.aurasoftware.aurautilitiesplus.configuration.serialization.Serializable;
import tech.aurasoftware.aurautilitiesplus.util.Placeholder;
import tech.aurasoftware.aurautilitiesplus.util.Text;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class AuraGUI implements Serializable, Cloneable {

    private String id;
    private String name;
    private int size;
    private List<AuraGUIItem> items = new ArrayList<>();

    public AuraGUI id(String id){
        this.id = id;
        return this;
    }

    public AuraGUI name(String name){
        this.name = name;
        return this;
    }

    public AuraGUI size(int size){
        this.size = size;
        return this;
    }

    public AuraGUI items(List<AuraGUIItem> items){
        this.items = items;
        return this;
    }

    public List<Integer> getFreeSlots(){
        List<Integer> slots = new ArrayList<>();
        for(int i = 0; i < size; i++){
            AuraGUIItem guiItem = getDefaultAt(i);
            if(guiItem == null){
                slots.add(i);
            }
        }
        return slots;
    }

    public AuraGUIItem getDefaultAt(int slot){
        for(AuraGUIItem auraGUIItem : items){
            if(auraGUIItem.getSlots().contains(slot)){
                return auraGUIItem;
            }
        }
        return null;
    }

    @Override
    public AuraGUI clone() {
        return new AuraGUI()
                .id(id)
                .name(name)
                .size(size)
                .items(items);
    }

    public Inventory createInventory(List<Placeholder> placeholderList, AuraGUIItem... additionalItems){

        Placeholder[] placeholders = placeholderList.toArray(new Placeholder[0]);

        Inventory inventory = Bukkit.createInventory(null, size, Text.c(Placeholder.apply(name, placeholders)));

        for(AuraGUIItem auraGUIItem : items){
            for(int slot: auraGUIItem.getSlots()){
                inventory.setItem(slot, auraGUIItem.getAuraItem().toBukkitItem(placeholders));
            }

        }

        for(AuraGUIItem auraGUIItem : additionalItems){
            for (int slot : auraGUIItem.getSlots()) {
                inventory.setItem(slot, auraGUIItem.getAuraItem().toBukkitItem(placeholders));
            }
        }

        return inventory;
    }

}
