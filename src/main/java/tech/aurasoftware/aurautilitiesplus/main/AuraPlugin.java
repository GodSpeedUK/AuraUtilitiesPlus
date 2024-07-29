package tech.aurasoftware.aurautilitiesplus.main;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import tech.aurasoftware.aurautilitiesplus.AuraUtilitiesPlus;
import tech.aurasoftware.aurautilitiesplus.command.AuraCommand;
import tech.aurasoftware.aurautilitiesplus.command.parameter.Parameter;
import tech.aurasoftware.aurautilitiesplus.configuration.serialization.Serializable;
import tech.aurasoftware.aurautilitiesplus.configuration.serialization.Serialization;

import java.lang.reflect.Field;

public abstract class AuraPlugin extends JavaPlugin {

    public void registerListener(Listener... listeners){
        for(Listener listener : listeners){
            getServer().getPluginManager().registerEvents(listener, this);
        }
    }

    public void registerParameters(Parameter<?>... parameters){
        for(Parameter<?> parameter : parameters){
            AuraUtilitiesPlus.getInstance().getParameterManager().insert(parameter);
        }
    }

    public void registerCommands(AuraCommand... commands){
        try {
            final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            bukkitCommandMap.setAccessible(true);
            CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());

            for (AuraCommand command : commands) {
                commandMap.register(command.getLabel(), command);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void registerSerializables(Class<? extends Serializable>... serializables){
        for(Class<? extends Serializable> serializable : serializables) {
            Serialization.register(serializable);
        }
    }


}
