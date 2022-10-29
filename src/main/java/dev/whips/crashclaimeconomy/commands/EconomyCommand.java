package dev.whips.crashclaimeconomy.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.whips.crashclaimeconomy.config.GlobalConfig;
import dev.whips.crashclaimeconomy.localization.Localization;
import net.crashcraft.crashpayment.payment.PaymentProcessor;
import net.crashcraft.crashpayment.payment.TransactionType;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("claimblocks")
public class EconomyCommand extends BaseCommand {
    private final PaymentProcessor provider;

    public EconomyCommand(PaymentProcessor provider) {
        this.provider = provider;
    }

    @Default
    @CommandPermission("crashclaimeconomy.user.checkclaimblocks")
    public void onBalance(Player player){
        provider.getBalance(player.getUniqueId(), (bal) -> {
            player.sendMessage(Localization.ECONOMY__CHECK_SELF_BALANCE.getMessage(player,
                        "balance", Integer.toString(bal.intValue()),
                        "max-balance", Integer.toString(GlobalConfig.maxClaimBlocks)));
        });
    }

    @Subcommand("check")
    @CommandCompletion("@players")
    @CommandPermission("crashclaimeconomy.admin.checkclaimblocks")
    public void onBalance(CommandSender sender, OfflinePlayer player){
        provider.getBalance(player.getUniqueId(), (bal) -> {
            sender.sendMessage(Localization.ECONOMY__CHECK_OTHER_BALANCE.getMessage(null,
                        "username", player.getName(),
                        "balance", Integer.toString(bal.intValue()),
                        "max-balance", Integer.toString(GlobalConfig.maxClaimBlocks)));
        });
    }

    @Subcommand("add")
    @CommandCompletion("@players @nothing")
    @CommandPermission("crashclaimeconomy.admin.addclaimblocks")
    public void onAdd(CommandSender sender, OfflinePlayer player, int amount){
        provider.makeTransaction(player.getUniqueId(), TransactionType.DEPOSIT, "ClaimBlock Admin Add", amount, (transactionRecipe) -> {
            if (transactionRecipe.transactionSuccess()){
                sender.sendMessage(Localization.ECONOMY__ADD_OTHER.getMessage(null,
                        "username", player.getName(),
                        "balance", Integer.toString((int) transactionRecipe.getAmount())));
            } else {
                sender.sendMessage(Localization.ECONOMY__ADD_OTHER_ERROR.getMessage(null,
                        "username", player.getName(),
                        "balance", Integer.toString((int) transactionRecipe.getAmount()),
                        "error", transactionRecipe.getTransactionError()));
            }
        });
    }

    @Subcommand("remove")
    @CommandCompletion("@players @nothing")
    @CommandPermission("crashclaimeconomy.admin.removeclaimblocks")
    public void onRemove(CommandSender sender, OfflinePlayer player, int amount){
        provider.makeTransaction(player.getUniqueId(), TransactionType.WITHDRAW, "ClaimBlock Admin Remove", amount, (transactionRecipe) -> {
            if (transactionRecipe.transactionSuccess()){
                sender.sendMessage(Localization.ECONOMY__REMOVE_OTHER.getMessage(null,
                        "username", player.getName(),
                        "balance", Integer.toString((int) transactionRecipe.getAmount())));
            } else {
                sender.sendMessage(Localization.ECONOMY__REMOVE_OTHER_ERROR.getMessage(null,
                        "username", player.getName(),
                        "balance", Integer.toString((int) transactionRecipe.getAmount()),
                        "error", transactionRecipe.getTransactionError()));
            }
        });
    }
}
