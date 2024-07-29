package tech.aurasoftware.aurautilitiesplus.message;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tech.aurasoftware.aurautilitiesplus.configuration.Configuration;
import tech.aurasoftware.aurautilitiesplus.util.Placeholder;
import tech.aurasoftware.aurautilitiesplus.util.Text;

import java.util.Arrays;
import java.util.List;

public interface Message extends Configuration {

    String getPrefix();

    Object getValue();

    default String getString() {
        return (String) getValue();
    }

    default List<String> getStringList() {
        return (List<String>) getValue();
    }

    default TitleMessage getTitleMessage() {
        return (TitleMessage) getValue();
    }

    default void send(CommandSender player, Placeholder... placeholders) {

        if (player == null) {
            return;
        }

        if(getValue() instanceof TitleMessage){
            if(player instanceof Player){
                sentTitle((Player) player, placeholders);
                return;
            }
            System.out.println("WARNING -> You tried to send a title message to the console, this is for players only!");
            return;
        }

        if(getValue() instanceof List){
            sendList(player, placeholders);
            return;
        }

        player.sendMessage(Text.c(Placeholder.apply(getString(), placeholders).replace("{prefix}", getPrefix())));
    }

    default void sendList(CommandSender player, Placeholder... placeholders) {

        if (player == null) {
            return;
        }

        StringBuilder text = new StringBuilder();
        for (String message : this.getStringList()) {
            text.append(Placeholder.apply(message, placeholders)).append("\n");
        }

        player.sendMessage(Text.c(text.toString().replace("{prefix}", (String) getPrefix())));
    }

    default void broadcast(Placeholder... placeholders) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            send(player, placeholders);
        }
    }

    default void sentTitle(Player player, Placeholder... placeholders) {
        TitleMessage titleMessage = getTitleMessage();

        List<Placeholder> placeholderList = Arrays.asList(placeholders);
        placeholderList.add(new Placeholder("{prefix}", getPrefix()));

        String header = Text.c(Placeholder.apply(titleMessage.getHeader(), placeholderList.toArray(new Placeholder[0])));
        String footer = Text.c(Placeholder.apply(titleMessage.getFooter(), placeholderList.toArray(new Placeholder[0])));

        player.sendTitle(header, footer, 1, titleMessage.getDuration(), 1);
    }

    default void broadcastTitle(Placeholder... placeholders) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            sentTitle(player, placeholders);
        }
    }


}
