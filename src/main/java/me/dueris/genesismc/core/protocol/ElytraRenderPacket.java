package me.dueris.genesismc.core.protocol;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ElytraRenderPacket {
    public static void sendCustomPacket(Player player, String message) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

        try {
            // Write packet data
            dataOutputStream.writeUTF(message);

            // Create the custom packet
            byte[] packetData = outputStream.toByteArray();
            ClientboundCustomPayloadPacket packet = new ClientboundCustomPayloadPacket(ResourceLocation.tryParse("genesis-packet-channel"), new FriendlyByteBuf(Unpooled.wrappedBuffer(packetData)));

            // Send the packet to the player
            ((CraftPlayer) player).getHandle().connection.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Close streams
            try {
                dataOutputStream.close();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
