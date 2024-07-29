package tech.aurasoftware.aurautilitiesplus.configuration;


import org.bukkit.configuration.file.YamlConfiguration;
import tech.aurasoftware.aurautilitiesplus.configuration.serialization.Serializable;
import tech.aurasoftware.aurautilitiesplus.configuration.serialization.Serialization;
import tech.aurasoftware.aurautilitiesplus.file.YamlFile;
import tech.aurasoftware.aurautilitiesplus.gui.AuraGUI;
import tech.aurasoftware.aurautilitiesplus.item.AuraItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface Configuration {
    String getPath();

    Object getValue();

    void setValue(Object value);

    static void loadConfig(YamlFile iConfig, Configuration... values) {

        boolean saveConfig = false;

        YamlConfiguration config = iConfig.getConfig();

        for (Configuration configuration : values) {
            if (config.get(configuration.getPath()) == null) {
                saveConfig = true;
                if (configuration.getValue() instanceof Serializable) {
                    Serializable serializable = (Serializable) configuration.getValue();
                    Map<String, Object> serialized = serializable.serialize();
                    for (String key : serialized.keySet()) {
                        config.set(configuration.getPath() + "." + key, serialized.get(key));
                    }
                    continue;
                }
                if (configuration.getValue() instanceof List) {
                    List<?> list = (List<?>) configuration.getValue();
                    if (list.isEmpty()) {
                        continue;
                    }

                    if (list.get(0) instanceof Serializable) {
                        for (int i = 0; i < list.size(); i++) {
                            Serializable serializable = (Serializable) list.get(i);
                            Map<String, Object> serialized = serializable.serialize();
                            for (String key : serialized.keySet()) {
                                config.set(configuration.getPath() + "." + i + "." + key, serialized.get(key));
                            }
                        }
                        continue;
                    }
                    config.set(configuration.getPath(), list);
                    continue;
                }
                config.set(configuration.getPath(), configuration.getValue());
                continue;
            }

            // load values from config

            if (configuration.getValue() instanceof Serializable) {
                Serializable serializable = (Serializable) Serialization.deserialize(configuration.getValue().getClass(), config, configuration.getPath());
                configuration.setValue(serializable);
                continue;
            }

            if (configuration.getValue() instanceof List) {
                // get type of list
                List<?> list = (List<?>) configuration.getValue();

                if(list.isEmpty()){
                    throw new IllegalArgumentException("Default list is empty!, Path: " + configuration.getPath());
                }

                Class<?> type = list.get(0).getClass();

                // Check if type implements Serializable
                if(Serializable.class.isAssignableFrom(type)){
                    List<Object> newList = new ArrayList<>();
                    for(int i = 0; i < list.size(); i++){
                        Serializable serializable = (Serializable) Serialization.deserialize(type, config, configuration.getPath() + "." + i);
                        newList.add(serializable);
                    }

                    configuration.setValue(newList);
                }
                continue;
            }

            configuration.setValue(config.get(configuration.getPath()));
        }

        if(saveConfig){
            iConfig.saveConfig();
        }

    }



    default List<String> getStringList() {
        return (List<String>) getValue();
    }

    default String getString() {
        return (String) getValue();
    }

    default int getInt() {
        return (Integer) getValue();
    }

    default double getDouble() {
        return (Double) getValue();
    }

    default boolean getBoolean() {
        return (Boolean) getValue();
    }

    default AuraItem getAuraItem(){
        return (AuraItem) getValue();
    }

    default AuraGUI getAuraGUI(){
        return (AuraGUI) getValue();
    }
    default void saveValue(YamlFile yamlFile) {
        if (getValue() instanceof Serializable) {
            Serializable serializable = (Serializable) getValue();
            Map<String, Object> serialized = serializable.serialize();
            for (String key : serialized.keySet()) {
                yamlFile.getConfig().set(getPath() + "." + key, serialized.get(key));
            }
        } else {
            yamlFile.getConfig().set(getPath(), getValue());
        }
        yamlFile.saveConfig();
    }
}
