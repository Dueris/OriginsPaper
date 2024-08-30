package io.github.dueris.originspaper.mixin;

import com.dragoncommissions.mixbukkit.api.shellcode.impl.api.CallbackInfo;
import io.github.dueris.originspaper.power.type.ActionOnLandPower;
import io.github.dueris.originspaper.storage.PowerHolderComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

@Mixin({Block.class})
public class BlockMixin {

	@Inject(method = "fallOn", locator = At.Value.HEAD)
	public static void apoli$actionOnLand(Block instance, Level world, BlockState state, BlockPos pos, @NotNull Entity entity, float fallDistance, CallbackInfo info) {
		PowerHolderComponent.getPowers(entity.getBukkitEntity(), ActionOnLandPower.class).forEach(p -> p.executeAction(entity));
	}
}
