package dev.whips.crashclaimeconomy;

import co.aikar.idb.DB;
import dev.whips.crashclaimeconomy.config.GlobalConfig;
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
            try {
                return DB.getFirstColumn("SELECT amount FROM claimblocks WHERE player_id = (SELECT id FROM players WHERE uuid = ?)", player.getUniqueId());
            } catch (Exception e) {
                return "0";
            }
        }

        if (params.equalsIgnoreCase("max_balance")) {
            return Integer.toString(GlobalConfig.maxClaimBlocks);
        }

        return null;
    }
}
