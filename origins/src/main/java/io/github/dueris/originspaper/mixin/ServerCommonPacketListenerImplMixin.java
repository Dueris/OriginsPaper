package io.github.dueris.originspaper.mixin;

import io.github.dueris.originspaper.access.BlockStateOwner;
import io.github.dueris.originspaper.access.SectionBlocksOwner;
import io.github.dueris.originspaper.component.PowerHolderComponent;
import io.github.dueris.originspaper.power.type.ModifyBlockRenderPowerType;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundSectionBlocksUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerCommonPacketListenerImpl.class)
public class ServerCommonPacketListenerImplMixin {

	@Shadow @Final protected ServerPlayer player;

	@Inject(method = "send(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketSendListener;)V", at = @At("HEAD"))
	public void apoli$modifyBlockRender(Packet<?> packet, PacketSendListener callbacks, CallbackInfo ci) {
		if (packet instanceof ClientboundBlockUpdatePacket blockUpdatePacket) {
			for (ModifyBlockRenderPowerType powerType : PowerHolderComponent.getPowerTypes(player, ModifyBlockRenderPowerType.class)) {
				if (powerType.doesPrevent(player.level(), blockUpdatePacket.getPos())) {
					((BlockStateOwner) blockUpdatePacket).apoli$setBlockState(powerType.getBlockState());
				}
			}
		} else if (packet instanceof ClientboundSectionBlocksUpdatePacket sectionBlocksUpdatePacket) {
			SectionBlocksOwner sectionBlocksOwner = (SectionBlocksOwner) sectionBlocksUpdatePacket;
			BlockState[] states = sectionBlocksOwner.apoli$getBlockStates();
			short[] positions = sectionBlocksOwner.apoli$positions();
			for (ModifyBlockRenderPowerType powerType : PowerHolderComponent.getPowerTypes(player, ModifyBlockRenderPowerType.class)) {
				if (powerType.isSending()) continue;
				for (int i = 0; i < states.length; i++) {
					if (powerType.doesPrevent(player.level(), sectionBlocksOwner.apoli$sectionPos().relativeToBlockPos(positions[i]))) {
						states[i] = powerType.getBlockState();
					}
				}
			}
		}
	}
}
