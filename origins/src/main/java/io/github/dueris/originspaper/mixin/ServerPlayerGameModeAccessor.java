package io.github.dueris.originspaper.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.level.GameType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerPlayerGameMode.class)
public interface ServerPlayerGameModeAccessor {

	@Accessor
	BlockPos getDestroyPos();

	@Accessor
	boolean getIsDestroyingBlock();

	@Accessor
	GameType getGameModeForPlayer();
}