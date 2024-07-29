package tech.aurasoftware.aurautilitiesplus;

import lombok.Getter;
import tech.aurasoftware.aurautilitiesplus.command.parameter.ParameterManager;
import tech.aurasoftware.aurautilitiesplus.command.parameter.implementation.DoubleParameter;
import tech.aurasoftware.aurautilitiesplus.command.parameter.implementation.IntegerParameter;
import tech.aurasoftware.aurautilitiesplus.command.parameter.implementation.PlayerParameter;
import tech.aurasoftware.aurautilitiesplus.command.parameter.implementation.OfflinePlayerParameter;
import tech.aurasoftware.aurautilitiesplus.command.parameter.implementation.StringParameter;
import tech.aurasoftware.aurautilitiesplus.configuration.Configuration;
import tech.aurasoftware.aurautilitiesplus.file.YamlFile;
import tech.aurasoftware.aurautilitiesplus.gui.AuraGUI;
import tech.aurasoftware.aurautilitiesplus.gui.AuraGUIItem;
import tech.aurasoftware.aurautilitiesplus.gui.listener.InventoryClickListener;
import tech.aurasoftware.aurautilitiesplus.gui.listener.InventoryCloseListener;
import tech.aurasoftware.aurautilitiesplus.item.AuraItem;
import tech.aurasoftware.aurautilitiesplus.main.AuraPlugin;
import tech.aurasoftware.aurautilitiesplus.message.TitleMessage;
import tech.aurasoftware.aurautilitiesplus.message.UtilityMessages;

public final class AuraUtilitiesPlus extends AuraPlugin {

    @Getter
    private static AuraUtilitiesPlus instance;

    @Getter
    private ParameterManager parameterManager;

    @Override
    public void onEnable() {
        instance = this;
        this.parameterManager = new ParameterManager();
        this.registerParameters(
                new DoubleParameter(),
                new IntegerParameter(),
                new OfflinePlayerParameter(),
                new PlayerParameter(),
                new StringParameter()
        );
        registerSerializables(TitleMessage.class, AuraItem.class, AuraGUIItem.class, AuraGUI.class);
        registerListener(new InventoryClickListener(), new InventoryCloseListener());
        Configuration.loadConfig(new YamlFile("messages.yml", this.getDataFolder().getAbsolutePath(), null, this), UtilityMessages.values());

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
