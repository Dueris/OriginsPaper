package io.github.dueris.originspaper.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.dueris.originspaper.access.BlockStateCollisionShapeAccess;
import io.github.dueris.originspaper.component.PowerHolderComponent;
import io.github.dueris.originspaper.power.type.ModifyBreakSpeedPowerType;
import io.github.dueris.originspaper.power.type.ModifyHarvestPowerType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockBehaviour.class)
public abstract class BlockBehaviorMixin implements BlockStateCollisionShapeAccess {

	@Unique
	private boolean apoli$queryOriginal;

	@Shadow
	protected abstract VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context);

	@Shadow
	protected abstract Block asBlock();

	@Override
	public VoxelShape apoli$getOriginalCollisionShape(BlockGetter world, BlockPos pos, CollisionContext context) {

		this.apoli$queryOriginal = true;
		VoxelShape originalShape = this.getCollisionShape(asBlock().defaultBlockState(), world, pos, context);

		this.apoli$queryOriginal = false;
		return originalShape;

	}

	@ModifyExpressionValue(method = "getDestroyProgress", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getDestroySpeed(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)F"))
	private float apoli$modifyBlockHardness(float original, BlockState state, @NotNull Player player, BlockGetter world, BlockPos pos) {
		if (!PowerHolderComponent.KEY.isProvidedBy(player)) return original;

		for (ModifyBreakSpeedPowerType power : PowerHolderComponent.KEY.get(player).getPowerTypes(ModifyBreakSpeedPowerType.class, true)) {
			power.applyPower(pos, player, true);
		}

		return original;

	}

	@ModifyReturnValue(method = "getDestroyProgress", at = @At("RETURN"))
	private float apoli$modifyBlockSpeed(float original, BlockState state, @NotNull Player player, BlockGetter world, BlockPos pos) {
		if (!PowerHolderComponent.KEY.isProvidedBy(player)) return original;

		for (ModifyBreakSpeedPowerType power : PowerHolderComponent.KEY.get(player).getPowerTypes(ModifyBreakSpeedPowerType.class, true)) {
			power.applyPower(pos, player, false);
		}

		return original;

	}

	@ModifyExpressionValue(method = "getDestroyProgress", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;hasCorrectToolForDrops(Lnet/minecraft/world/level/block/state/BlockState;)Z"))
	private boolean apoli$modifyEffectiveTool(boolean original, BlockState state, Player player, BlockGetter world, BlockPos pos) {
		return PowerHolderComponent.getPowerTypes(player, ModifyHarvestPowerType.class)
			.stream()
			.filter(mhp -> mhp.doesApply(pos))
			.max(ModifyHarvestPowerType::compareTo)
			.map(ModifyHarvestPowerType::isHarvestAllowed)
			.orElse(original);
	}
}
