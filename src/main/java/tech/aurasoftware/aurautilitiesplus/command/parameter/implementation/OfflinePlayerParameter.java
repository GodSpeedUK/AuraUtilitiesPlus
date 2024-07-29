package tech.aurasoftware.aurautilitiesplus.command.parameter.implementation;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import tech.aurasoftware.aurautilitiesplus.command.parameter.Parameter;

import java.util.ArrayList;
import java.util.List;

public class OfflinePlayerParameter extends Parameter<OfflinePlayer> {

    public OfflinePlayerParameter() {
        super(OfflinePlayer.class);
    }


    @Override
    public OfflinePlayer parse(String input) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(input);
        return offlinePlayer;
    }

    @Override
    public boolean isParsable(String input){
        if(input == null){
            return false;
        }
        return parse(input).hasPlayedBefore() || Bukkit.getPlayer(input) != null;
    }

    @Override
    public List<String> tabComplete() {
        List<String> list = new ArrayList<>();
        for(Player offlinePlayer: Bukkit.getOnlinePlayers()){
            list.add(offlinePlayer.getName());
        }
        return list;
    }


}
