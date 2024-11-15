package io.github.dueris.originspaper.mixin;

import io.github.dueris.originspaper.component.PowerHolderComponent;
import io.github.dueris.originspaper.power.type.ModifyBlockRenderPowerType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.PlayerChunkSender;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.chunk.LevelChunk;
import org.bukkit.craftbukkit.CraftChunk;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerChunkSender.class)
public class PlayerChunkSenderMixin {

	@Inject(method = "sendChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;send(Lnet/minecraft/network/protocol/Packet;)V", shift = At.Shift.AFTER))
	private static void apoli$modifyBlockRender(@NotNull ServerGamePacketListenerImpl handler, ServerLevel world, LevelChunk chunk, CallbackInfo ci) {
		Player p = handler.player.getBukkitEntity();
		ServerPlayer nms = ((CraftPlayer) p).getHandle();
		for (ModifyBlockRenderPowerType power : PowerHolderComponent.getPowerTypes(nms, ModifyBlockRenderPowerType.class, true)) {
			new ModifyBlockRenderPowerType.RenderUpdate(new CraftChunk(chunk), handler.player.serverLevel(), nms).accept(power);
		}
	}

}
