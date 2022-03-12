package dev.whips.crashclaimeconomy.compatability;

import com.comphenix.protocol.events.PacketContainer;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface CompatibilityWrapper {
    void sendActionBarTitle(Player player, BaseComponent[] message, int fade_in, int duration, int fade_out);
    boolean isInteractAndMainHand(PacketContainer packet);

    void spawnGlowingInvisibleMagmaSlime(Player player, double x, double z, double y, int id, UUID uuid,
                                         HashMap<Integer, String> fakeEntities, HashMap<Integer, Location> entityLocations);

    void removeEntity(Player player, Set<Integer> entity_ids);

    void setEntityTeam(Player player, String team, List<String> uuids);

    int getMinWorldHeight(World world);

    int getUniqueEntityID();
}
