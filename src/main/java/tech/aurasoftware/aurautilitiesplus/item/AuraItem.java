package tech.aurasoftware.aurautilitiesplus.item;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import tech.aurasoftware.aurautilitiesplus.configuration.serialization.Serializable;
import tech.aurasoftware.aurautilitiesplus.util.Placeholder;
import tech.aurasoftware.aurautilitiesplus.util.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Getter
public class AuraItem implements Serializable {

    private String material;

    private String skullOwner;
    private String name;
    private List<String> lore = new ArrayList<>();
    private int amount = 1;
    private int data = 0;

    private boolean unbreakable = false;
    private boolean hideEnchants = false;
    private boolean hideAttributes = false;
    private List<String> enchantments = new ArrayList<>();
    private boolean hideUnbreakable = false;

    public AuraItem() {

    }

    public AuraItem material(String material) {
        this.material = material;
        return this;
    }

    public AuraItem name(String name) {
        this.name = name;
        return this;
    }

    public AuraItem lore(List<String> lore) {
        this.lore = lore;
        return this;
    }

    public AuraItem amount(int amount) {
        this.amount = amount;
        return this;
    }

    public AuraItem data(int data) {
        this.data = data;
        return this;
    }

    public AuraItem skullOwner(String skullOwner) {
        this.skullOwner = skullOwner;
        return this;
    }

    public AuraItem unbreakable(boolean unbreakable) {
        this.unbreakable = unbreakable;
        return this;
    }

    public AuraItem hideEnchants(boolean hideEnchants) {
        this.hideEnchants = hideEnchants;
        return this;
    }

    public AuraItem hideAttributes(boolean hideAttributes) {
        this.hideAttributes = hideAttributes;
        return this;
    }

    public AuraItem enchantments(List<String> enchantments) {
        this.enchantments = enchantments;
        return this;
    }

    public AuraItem hideUnbreakable(boolean hideUnbreakable) {
        this.hideUnbreakable = hideUnbreakable;
        return this;
    }


    public ItemStack toBukkitItem(Placeholder... placeholders) {

        if (material == null) {
            throw new IllegalArgumentException("Material cannot be null");
        }

        Material bukkitMaterial = Material.getMaterial(material.toUpperCase());

        if (bukkitMaterial == null) {
            throw new IllegalArgumentException("Material not found");
        }


        ItemStack itemStack;

        if (skullOwner != null && bukkitMaterial.equals(Material.PLAYER_HEAD)) {
            itemStack = new ItemStack(bukkitMaterial, amount);
            UUID playerUUID = Bukkit.getOfflinePlayer(Placeholder.apply(skullOwner, placeholders)).getUniqueId();
            String propertyValue = getPlayerTextureProperty(playerUUID);
            if(propertyValue != null) {
                Bukkit.getUnsafe().modifyItemStack(itemStack,
                        "{SkullOwner:{Id:\"" + playerUUID + "\",Properties:{textures:[{Value:\"" + propertyValue + "\"}]}}}");
            }else{
                System.out.println("Failed to get property for Player: " + playerUUID);
            }
        } else {
            itemStack = new ItemStack(bukkitMaterial, amount, (short) data);
        }

        ItemMeta itemMeta = itemStack.getItemMeta();

        if (name != null) {
            itemMeta.setDisplayName(Text.c(Placeholder.apply(name, placeholders)));
        }

        if (lore != null) {
            List<String> loreModified = new ArrayList<>();
            for (String loreLine : lore) {

                String placeholdersApplied = Placeholder.apply(loreLine, placeholders);
                // check for line break
                if(placeholdersApplied.contains(System.lineSeparator())){
                    String[] loreLines = placeholdersApplied.split(System.lineSeparator());
                    for(String line : loreLines){
                        loreModified.add(Text.c(line));
                    }
                    continue;
                }

                loreModified.add(Text.c(Placeholder.apply(loreLine, placeholders)));
            }
            itemMeta.setLore(loreModified);
        }

        itemMeta.setUnbreakable(unbreakable);

        if (enchantments != null) {
            for (String enchantment : enchantments) {
                String[] enchantmentSplit = enchantment.split(":");
                if (enchantmentSplit.length != 2) {
                    throw new IllegalArgumentException("Invalid enchantment format");
                }

                int level;

                try {
                    level = Integer.parseInt(enchantmentSplit[1]);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid enchantment level");
                }

                Enchantment bukkitEnchantment = Enchantment.getByName(enchantmentSplit[0].toUpperCase());

                if (bukkitEnchantment == null) {
                    throw new IllegalArgumentException("Enchantment not found");
                }

                itemMeta.addEnchant(bukkitEnchantment, level, true);

            }
        }

        if (hideEnchants) {
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        if (hideAttributes) {
            itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        }

        if(hideUnbreakable){
            itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        }


        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    private String getPlayerTextureProperty(UUID playerUUID) {
        try {
            URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + playerUUID.toString() + "?unsigned=false");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(response.toString());
            JsonObject rootObject = element.getAsJsonObject();

            JsonElement propertiesElement = rootObject.get("properties");
            if (propertiesElement != null) {
                for (JsonElement propertyElement : propertiesElement.getAsJsonArray()) {
                    JsonObject propertyObject = propertyElement.getAsJsonObject();
                    String name = propertyObject.get("name").getAsString();
                    String value = propertyObject.get("value").getAsString();
                    if (name.equals("textures")) {
                        return value;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
