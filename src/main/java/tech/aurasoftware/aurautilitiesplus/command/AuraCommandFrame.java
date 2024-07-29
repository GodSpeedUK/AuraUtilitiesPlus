package tech.aurasoftware.aurautilitiesplus.command;

import org.bukkit.command.CommandSender;


public interface AuraCommandFrame {

    Class<?>[] getParameters();

    String getPermission();

    String getUsage();

    boolean isRequiresPlayer();

    boolean[] getOptional();

    boolean run(CommandSender commandSender, String[] args);


}
