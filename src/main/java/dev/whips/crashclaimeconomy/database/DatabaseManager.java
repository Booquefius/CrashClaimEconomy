package dev.whips.crashclaimeconomy.database;

import co.aikar.idb.DB;
import co.aikar.idb.Database;
import co.aikar.idb.DatabaseOptions;
import co.aikar.idb.PooledDatabaseOptions;
import dev.whips.crashclaimeconomy.CrashClaimEconomy;
import dev.whips.crashclaimeconomy.database.versions.DataRev0;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Logger;

public class DatabaseManager {
    private final CrashClaimEconomy plugin;
    private final Logger logger;
    private final ArrayList<DataVersion> dataVersions;

    private int currentRevision;

    public DatabaseManager(CrashClaimEconomy plugin){
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.dataVersions = new ArrayList<>();
        this.currentRevision = -1; // represents no database configured yet

        DatabaseOptions options = DatabaseOptions.builder().sqlite(new File(plugin.getDataFolder(), "data.sqlite").getPath()).build();
        Database db = PooledDatabaseOptions.builder().options(options)
                .maxConnections(1)
                .createHikariDatabase();
        DB.setGlobalDatabase(db);

        setupDataVersions();
        validateDataVersions();

        try {
            String value = DB.getFirstColumn("SELECT value FROM properties WHERE `key` = ?", "dataVersion");
            currentRevision = Integer.parseInt(value);
        } catch (SQLException e){ // Table does not exist so we assume database is fresh
            logger.info("No data version found, creating database schema");
        }

        updateDatabase();
    }

    private void updateDatabase(){
        int latestVersion = (dataVersions.get(dataVersions.size() - 1).getVersion());
        if (currentRevision == latestVersion){
            return;
        }

        logger.info("Database is converting: [" + currentRevision + " -> " + latestVersion + "]");

        for (int x = currentRevision == -1 ? 0 : currentRevision + 1; x < dataVersions.size(); x++){
            DataVersion version = dataVersions.get(x);
            logger.info("Converting [" + currentRevision + " -> " + version.getVersion() + "]");
            try {
                version.executeUpgrade(currentRevision);
                currentRevision = version.getVersion();
            } catch (SQLException e){
                e.printStackTrace();
                plugin.disablePlugin("Failed conversion, disabling plugin, manual data fixing may be needed");
            }

            try {
                DB.executeInsert("REPLACE INTO properties(key, value) VALUES (?, ?)", "dataVersion", currentRevision);
            } catch (SQLException e){
                logger.severe("Failed to write property to database");
                e.printStackTrace();
            }
        }
    }

    private void setupDataVersions(){
        registerDataVersion(new DataRev0());
    }

    private void validateDataVersions(){
        ArrayList<Integer> failedDataVersions = new ArrayList<>();
        for (int x = 0; x < dataVersions.size(); x++){
            DataVersion version = dataVersions.get(x);

            if (version == null){
                failedDataVersions.add(x);
            }
        }

        if (failedDataVersions.size() > 0){
            StringBuilder builder = new StringBuilder("Data versions failed to initialize. [");
            for (int version : failedDataVersions){
                builder.append(version).append(", ");
            }
            builder.append("]");
            plugin.disablePlugin(builder.toString());
        }
    }

    private void registerDataVersion(DataVersion version){
        dataVersions.add(version.getVersion(), version);
    }
}
