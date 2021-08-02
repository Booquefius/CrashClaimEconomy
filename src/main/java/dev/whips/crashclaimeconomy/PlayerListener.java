package dev.whips.crashclaimeconomy;

import co.aikar.idb.DB;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.SQLException;
import java.util.UUID;

public class PlayerListener implements Listener {
    @EventHandler (priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent e){
        UUID uuid = e.getPlayer().getUniqueId();
        try {
            Integer id = DB.getFirstColumn("SELECT id FROM players WHERE uuid = ?", uuid.toString());
            if (id == null){
                DB.executeInsert("INSERT INTO players(uuid, username) VALUES (?, ?)", uuid.toString(), e.getPlayer().getName());
            }
        } catch (SQLException ex){
            ex.printStackTrace();
        }
    }
}
