package dev.whips.crashclaimeconomy.compatability.versions;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.utility.MinecraftVersion;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import dev.whips.crashclaimeconomy.compatability.CompatibilityManager;
import dev.whips.crashclaimeconomy.compatability.CompatibilityWrapper;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

@SuppressWarnings("Duplicates")
public class Wrapper1_17 implements CompatibilityWrapper {
    @Override
    public void sendActionBarTitle(Player player, BaseComponent[] message, int fade_in, int duration, int fade_out) {
        PacketContainer packet = CompatibilityManager.getProtocolManager().createPacket(
                new PacketType(PacketType.Protocol.PLAY, PacketType.Sender.SERVER, 0x41, MinecraftVersion.getCurrentVersion(), "ActionBar"));

        packet.getChatComponents().write(0, WrappedChatComponent.fromJson(ComponentSerializer.toString(message)));

        PacketContainer packetDelay = CompatibilityManager.getProtocolManager().createPacket(
                new PacketType(PacketType.Protocol.PLAY, PacketType.Sender.SERVER, 0x5A, MinecraftVersion.getCurrentVersion(), "SetTitleTime"));

        packetDelay.getIntegers().write(0, fade_in);
        packetDelay.getIntegers().write(1, duration);
        packetDelay.getIntegers().write(2, fade_out);

        try {
            CompatibilityManager.getProtocolManager().sendServerPacket(player, packet);
            CompatibilityManager.getProtocolManager().sendServerPacket(player, packetDelay);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}