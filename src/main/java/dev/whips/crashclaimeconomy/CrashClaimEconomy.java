package dev.whips.crashclaimeconomy;

import com.comphenix.protocol.ProtocolLibrary;
import dev.whips.crashclaimeconomy.commands.CommandManager;
import dev.whips.crashclaimeconomy.compatability.CompatibilityManager;
import dev.whips.crashclaimeconomy.config.ConfigManager;
import dev.whips.crashclaimeconomy.config.GlobalConfig;
import dev.whips.crashclaimeconomy.database.DatabaseManager;
import dev.whips.crashclaimeconomy.economy.EconomyManager;
import dev.whips.crashclaimeconomy.economy.RewardManager;
import dev.whips.crashclaimeconomy.localization.LocalizationLoader;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class CrashClaimEconomy extends JavaPlugin {
    private static CrashClaimEconomy crashClaimEconomy;

    private CompatibilityManager compatibilityManager;
    private DatabaseManager databaseManager;
    private CommandManager commandManager;
    private EconomyManager economyManager;
    private RewardManager rewardManager;

    @Override
    public void onLoad() {
        crashClaimEconomy = this;
        economyManager = new EconomyManager(this);
    }

    @Override
    public void onEnable() {
        loadConfigs();

        LocalizationLoader.initialize();

        compatibilityManager = new CompatibilityManager(ProtocolLibrary.getProtocolManager());
        databaseManager = new DatabaseManager(this);
        commandManager = new CommandManager(this);

        economyManager.enable();

        if (GlobalConfig.claimBlockRewardMillis > 0) {
            getLogger().info("Starting Reward Routine");
            rewardManager = new RewardManager(this, economyManager.getProvider());
        }

        Bukkit.getPluginManager().registerEvents(rewardManager, this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
    }

    @Override
    public void onDisable() {
        //Cleanup for possible reload.
        HandlerList.unregisterAll(this);

        commandManager = null;
        economyManager = null;
    }

    public void loadConfigs(){
        File dataFolder = getDataFolder();

        if (dataFolder.mkdirs()){
            getLogger().info("Created data directory");
        }

        try {
            ConfigManager.initConfig(new File(dataFolder, "config.yml"), GlobalConfig.class);
        } catch (Exception ex){
            ex.printStackTrace();
            getLogger().severe("Could not load configuration properly. Stopping server");
            getServer().shutdown();
        }
    }

    public void disablePlugin(String error){
        getLogger().severe(error);
        Bukkit.getPluginManager().disablePlugin(this);
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public static CrashClaimEconomy getInstance() {
        return crashClaimEconomy;
    }

    public CompatibilityManager getCompatibilityManager() {
        return compatibilityManager;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
}
