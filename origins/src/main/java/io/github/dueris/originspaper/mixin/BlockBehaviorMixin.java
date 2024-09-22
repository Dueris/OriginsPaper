package io.github.dueris.originspaper.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.dueris.originspaper.power.type.ModifyBreakSpeedPower;
import io.github.dueris.originspaper.power.type.ModifyHarvestPower;
import io.github.dueris.originspaper.storage.PowerHolderComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockBehaviour.class)
public class BlockBehaviorMixin {

	@ModifyExpressionValue(method = "getDestroyProgress", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;hasCorrectToolForDrops(Lnet/minecraft/world/level/block/state/BlockState;)Z"))
	private boolean apoli$modifyEffectiveTool(boolean original, BlockState state, @NotNull Player player, BlockGetter world, BlockPos pos) {
		return PowerHolderComponent.getPowers(player.getBukkitEntity(), ModifyHarvestPower.class).stream()
			.filter(mhp -> mhp.doesApply(pos, player))
			.max(ModifyHarvestPower::compareTo)
			.map(ModifyHarvestPower::isHarvestAllowed)
			.orElse(original);
	}

	@ModifyExpressionValue(method = "getDestroyProgress", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getDestroySpeed(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)F"))
	private float apoli$modifyBlockHardness(float original, BlockState state, @NotNull Player player, BlockGetter world, BlockPos pos) {

		for (ModifyBreakSpeedPower power : PowerHolderComponent.getPowers(player.getBukkitEntity(), ModifyBreakSpeedPower.class)) {
			power.applyPower(pos, player, true);
		}

		return original;

	}

	@ModifyReturnValue(method = "getDestroyProgress", at = @At("RETURN"))
	private float apoli$modifyBlockSpeed(float original, BlockState state, @NotNull Player player, BlockGetter world, BlockPos pos) {

		for (ModifyBreakSpeedPower power : PowerHolderComponent.getPowers(player.getBukkitEntity(), ModifyBreakSpeedPower.class)) {
			power.applyPower(pos, player, false);
		}

		return original;

	}

}
