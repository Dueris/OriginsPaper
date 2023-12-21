package me.dueris.genesismc.protocol;

import io.netty.buffer.Unpooled;
import me.dueris.genesismc.events.OriginPacketSendEvent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static org.bukkit.Bukkit.getServer;

public class SendStringPacketPayload {
    /**
     * Sends packet through the genesis packet channel, removed due to no longer requiring it
     * @param player
     * @param message
     */
    @Deprecated
    public static void sendCustomPacket(Player player, String message) {

//        OriginPacketSendEvent event = new OriginPacketSendEvent(player);
//        getServer().getPluginManager().callEvent(event);
//
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
//
//        try {
//            // Write packet data
//            dataOutputStream.writeUTF(message);
//
//            // Create the custom packet
//            byte[] packetData = outputStream.toByteArray();
//            ClientboundCustomPayloadPacket packet = new ClientboundCustomPayloadPacket(new FriendlyByteBuf(Unpooled.wrappedBuffer(packetData)));
//
//            // Send the packet to the player
//            ((CraftPlayer) player).getHandle().connection.send(packet);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            // Close streams
//            try {
//                dataOutputStream.close();
//                outputStream.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
    }
}
