package io.github.dueris.originspaper.mixin;

import io.github.dueris.originspaper.access.BlockStateOwner;
import net.minecraft.core.SectionPos;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * Need to update blocks for {@link io.github.dueris.originspaper.power.type.ModifyBlockRenderPowerType}...
 */
@Mixin(ClientboundBlockUpdatePacket.class)
public class ClientboundBlockUpdatePacketMixin implements BlockStateOwner {
	@Shadow
	public BlockState blockState;

	@Override
	public void apoli$setBlockState(BlockState state) {
		this.blockState = state;
	}

	@Override
	public BlockState apoli$getBlockState() {
		return this.blockState;
	}
}
