package tech.aurasoftware.aurautilitiesplus.util;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import tech.aurasoftware.aurautilitiesplus.AuraUtilitiesPlus;

@UtilityClass
public class Schedulers {

    public int async(Runnable runnable){
        return Bukkit.getScheduler().runTaskAsynchronously(AuraUtilitiesPlus.getInstance(), runnable).getTaskId();
    }

    public int sync(Runnable runnable){
        return Bukkit.getScheduler().runTask(AuraUtilitiesPlus.getInstance(), runnable).getTaskId();
    }

    public int later(Runnable runnable, long delay){
        return Bukkit.getScheduler().runTaskLater(AuraUtilitiesPlus.getInstance(), runnable, delay).getTaskId();
    }

    public int repeat(Runnable runnable, long delay){
        return Bukkit.getScheduler().scheduleSyncRepeatingTask(AuraUtilitiesPlus.getInstance(), runnable, delay, delay);
    }


}
