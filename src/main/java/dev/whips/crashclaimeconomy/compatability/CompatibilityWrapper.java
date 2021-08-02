package dev.whips.crashclaimeconomy.compatability;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Player;

public interface CompatibilityWrapper {
    void sendActionBarTitle(Player player, BaseComponent[] message, int fade_in, int duration, int fade_out);
}
