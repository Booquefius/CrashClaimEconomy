package dev.whips.crashclaimeconomy.economy;

import dev.whips.crashclaimeconomy.CrashClaimEconomy;
import dev.whips.crashclaimeconomy.commands.EconomyCommand;
import net.crashcraft.crashpayment.CrashPayment;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;

import java.util.HashMap;
import java.util.UUID;

public class EconomyManager {
    private final CrashClaimEconomy crashClaimEconomy;
    private final CrashPayment paymentPlugin;
    private final ClaimBlockProvider claimBlockProvider;
    private final HashMap<UUID, Integer> claimBlockCache = new HashMap<>();

    public EconomyManager(CrashClaimEconomy crashClaimEconomy){
        this.crashClaimEconomy = crashClaimEconomy;
        this.paymentPlugin = (CrashPayment) Bukkit.getPluginManager().getPlugin("CrashPayment");
        this.claimBlockProvider = new ClaimBlockProvider();

        if (paymentPlugin == null){
            crashClaimEconomy.disablePlugin("[Payment] CrashPayment plugin not found, disabling plugin, download and install it here, https://www.spigotmc.org/resources/crashpayment.94069/");
            return;
        }

        paymentPlugin.register(crashClaimEconomy, ServicePriority.Low, claimBlockProvider); // Register ClaimBlock Provider
    }

    public void enable(){
        crashClaimEconomy.getCommandManager().getCommandManager().registerCommand(new EconomyCommand(
                paymentPlugin.setupPaymentProvider(crashClaimEconomy, claimBlockProvider.getProviderIdentifier()).getProcessor()
        ));
    }

    public ClaimBlockProvider getProvider() {
        return claimBlockProvider;
    }

    public HashMap<UUID, Integer> getClaimBlockCache() {
        return claimBlockCache;
    }
}
