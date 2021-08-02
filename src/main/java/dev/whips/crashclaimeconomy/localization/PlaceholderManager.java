package dev.whips.crashclaimeconomy.localization;

import dev.whips.crashclaimeconomy.CrashClaimEconomy;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class PlaceholderManager {
    private final boolean isEnabled;

    public PlaceholderManager(){
        this.isEnabled = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;

        if (isEnabled){
            CrashClaimEconomy.getInstance().getLogger().info("PlaceholderAPI Support enabled.");
        }
    }

    public boolean hasPlaceholders(String message){
        if (!isEnabled){
            return false;
        }
        return PlaceholderAPI.containsPlaceholders(message);
    }

    public boolean hasPlaceholders(String... messageList){
        if (!isEnabled) {
            return false;
        }

        for (String s : messageList) {
            if (PlaceholderAPI.containsPlaceholders(s)){
                return true;
            }
        }
        return false;
    }

    public String usePlaceholders(Player player, String message){
        if (!isEnabled){
            return message;
        }

        return PlaceholderAPI.setPlaceholders(player, message);
    }

    public List<String> usePlaceholders(Player player, List<String> messageList){
        if (!isEnabled){
            return messageList;
        }

        return PlaceholderAPI.setPlaceholders(player, messageList);
    }
}
