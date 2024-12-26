package io.github.dueris.originspaper.mixin;

import io.github.dueris.originspaper.access.SectionBlocksOwner;
import net.minecraft.core.SectionPos;
import net.minecraft.network.protocol.game.ClientboundSectionBlocksUpdatePacket;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * Primarily used to maintain compatibility with {@link io.github.dueris.originspaper.power.type.ModifyBlockRenderPowerType} and plugins
 */
@Mixin(ClientboundSectionBlocksUpdatePacket.class)
public class ClientboundSectionBlocksUpdatePacketMixin implements SectionBlocksOwner {
	@Shadow
	@Final
	private BlockState[] states;

	@Shadow
	@Final
	private SectionPos sectionPos;

	@Shadow
	@Final
	private short[] positions;

	@Override
	public void apoli$setBlockStates(BlockState[] states) {
		System.arraycopy(states, 0, this.states, 0, states.length);
	}

	@Override
	public BlockState[] apoli$getBlockStates() {
		return this.states;
	}

	@Override
	public SectionPos apoli$sectionPos() {
		return this.sectionPos;
	}

	@Override
	public short[] apoli$positions() {
		return this.positions;
	}
}
