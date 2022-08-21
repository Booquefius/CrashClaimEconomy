package dev.whips.crashclaimeconomy.economy;

import co.aikar.idb.DB;
import dev.whips.crashclaimeconomy.CrashClaimEconomy;
import dev.whips.crashclaimeconomy.config.GlobalConfig;
import dev.whips.crashclaimeconomy.localization.Localization;
import net.crashcraft.crashpayment.payment.PaymentProvider;
import net.crashcraft.crashpayment.payment.TransactionType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.UUID;

public class RewardManager implements Listener {
    private final PaymentProvider provider;
    private final HashMap<UUID, Long> timeSinceReward;

    public RewardManager(CrashClaimEconomy plugin, PaymentProvider provider){
        this.provider = provider;
        timeSinceReward = new HashMap<>();

        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()){
                UUID uuid = player.getUniqueId();
                Long time = timeSinceReward.get(uuid);

                if (time == null) {
                    continue;
                }

                int balance = getBalance(uuid);

                if (balance + GlobalConfig.claimBlockReward >= GlobalConfig.maxClaimBlocks) {
                    if (GlobalConfig.maxClaimBlocks > balance) {
                        player.sendMessage(Localization.ALERT__MAX_BLOCKS.getMessage(player,
                            "max-balance", Integer.toString(GlobalConfig.maxClaimBlocks)));

                        giveReward(player, GlobalConfig.maxClaimBlocks - balance);
                        continue;
                    }

                    continue;
                }

                long delta = System.currentTimeMillis() - time;

                if (delta >= GlobalConfig.claimBlockRewardMillis){
                    giveReward(player);
                    timeSinceReward.put(uuid, System.currentTimeMillis());
                }
            }
        }, 20 * 60, 20 * 60); // Run once a minute
    }

    public void giveReward(Player player){
        giveReward(player, GlobalConfig.claimBlockReward);
    }

    public void giveReward(Player player, int amount){
        provider.makeTransaction(player.getUniqueId(), TransactionType.DEPOSIT, "Reward", amount, (transactionRecipe -> {
            if (transactionRecipe.transactionSuccess()){
                if (GlobalConfig.sendInChatInstead){
                    player.sendMessage(Localization.ALERT__CHAT.getMessage(player,
                        "reward", Integer.toString(amount)));
                } else {
                    CrashClaimEconomy.getInstance().getCompatibilityManager().getWrapper().sendActionBarTitle(
                        player,
                        Localization.ALERT__ACTIONBAR.getMessage(player,
                            "reward", Integer.toString(amount)),
                        GlobalConfig.alertFadeIn,
                        GlobalConfig.alertDuration,
                        GlobalConfig.alertFadeOut
                    );
                }
            } else {
                player.sendMessage(transactionRecipe.getTransactionError());
            }
        }));
    }

    public int getBalance(UUID uuid) {
        try {
            return DB.getFirstColumn("SELECT amount FROM claimblocks WHERE player_id = (SELECT id FROM players WHERE uuid = ?)", uuid.toString());
        } catch (Exception ex) {
            return 0;
        }
    }

    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent e){
        UUID uuid = e.getPlayer().getUniqueId();

        DB.getFirstColumnAsync("SELECT timeSinceReward FROM playtime WHERE player_id = (SELECT id FROM players WHERE uuid = ?)", uuid.toString()).thenAccept((time) -> {
            if (time == null){
                timeSinceReward.put(uuid, System.currentTimeMillis());
                provider.makeTransaction(uuid, TransactionType.DEPOSIT, "Join Reward", GlobalConfig.initialClaimBlocks, (transactionRecipe -> {
                    if (!transactionRecipe.transactionSuccess()){
                        e.getPlayer().sendMessage(transactionRecipe.getTransactionError());
                    }
                }));
            } else {
                timeSinceReward.put(uuid, System.currentTimeMillis() - (Integer) time); // Set to Current time minus already played
            }
        }).exceptionally((ex) -> {
            ex.printStackTrace();
            return null;
        });
    }

    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onLeave(PlayerQuitEvent e){
        UUID uuid = e.getPlayer().getUniqueId();

        if (timeSinceReward.containsKey(uuid)) {
            long delta = System.currentTimeMillis() - timeSinceReward.get(uuid);

            DB.executeUpdateAsync("INSERT OR REPLACE INTO playtime(player_id, timeSinceReward) VALUES ((SELECT id FROM players WHERE uuid = ?), ?)",
                    uuid.toString(),
                    delta)
            .exceptionally((ex) -> {
                ex.printStackTrace();
                return null;
            });

            timeSinceReward.remove(uuid);
        }
    }
}
