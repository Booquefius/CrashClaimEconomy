package dev.whips.crashclaimeconomy.database.versions;

import co.aikar.idb.DB;
import dev.whips.crashclaimeconomy.database.DataVersion;

import java.sql.SQLException;

public class DataRev0 implements DataVersion {
    @Override
    public int getVersion() {
        return 0;
    }

    @Override
    public void executeUpgrade(int fromRevision) throws SQLException {
        DB.executeUpdate("CREATE TABLE \"properties\" (\n" +
                "\t\"key\"\tTEXT UNIQUE NOT NULL,\n" +
                "\t\"value\"\tTEXT NOT NULL,\n" +
                "\tUNIQUE(\"value\",\"key\")\n" +
                ")");
        DB.executeUpdate("CREATE TABLE \"players\" (\n" +
                "\t\"id\"\tINTEGER,\n" +
                "\t\"uuid\"\tTEXT NOT NULL,\n" +
                "\t\"username\"\tTEXT,\n" +
                "\tUNIQUE(\"id\",\"uuid\"),\n" +
                "\tPRIMARY KEY(\"id\" AUTOINCREMENT)\n" +
                ")");
        DB.executeUpdate("CREATE TABLE \"claimblocks\" (\n" +
                "\t\"player_id\"\tINTEGER NOT NULL UNIQUE,\n" +
                "\t\"amount\"\tINTEGER DEFAULT 0,\n" +
                "\tPRIMARY KEY(\"player_id\"),\n" +
                "\tFOREIGN KEY(\"player_id\") REFERENCES \"players\"(\"id\") ON DELETE CASCADE\n" +
                ");");
        DB.executeUpdate("CREATE TABLE \"playtime\" (\n" +
                "\t\"player_id\"\tINTEGER UNIQUE,\n" +
                "\t\"timeSinceReward\"\tINTEGER NOT NULL DEFAULT 0,\n" +
                "\tFOREIGN KEY(\"player_id\") REFERENCES \"players\"(\"id\") ON DELETE CASCADE,\n" +
                "\tPRIMARY KEY(\"player_id\")\n" +
                ")");
    }
}
