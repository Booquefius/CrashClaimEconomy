package dev.whips.crashclaimeconomy;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicReference;

public class CrashClaimEconomyExpansion extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "crashclaimeconomy";
    }

    @Override
    public @NotNull String getAuthor() {
        return "person";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        if (params.equalsIgnoreCase("balance")){
            AtomicReference<Double> balance = new AtomicReference<>(0.0);
            CrashClaimEconomy.getInstance().getEconomyManager()
                    .getProvider()
                    .getBalance(player.getUniqueId(), balance::set);
            return ((int) balance.get())+"";
        }
        return null;
    }
}
