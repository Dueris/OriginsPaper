package io.github.dueris.originspaper.mixin;

import com.google.common.collect.AbstractIterator;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.dueris.originspaper.access.BlockCollisionSpliteratorAccess;
import io.github.dueris.originspaper.access.BlockStateCollisionShapeAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockCollisions;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockCollisions.class)
public abstract class BlockCollisionsMixin<T> extends AbstractIterator<T> implements BlockCollisionSpliteratorAccess {

	@Unique
	boolean apoli$getOriginalShapes;

	@Override
	public boolean apoli$shouldGetOriginalShapes() {
		return apoli$getOriginalShapes;
	}

	@Override
	public void apoli$setGetOriginalShapes(boolean getOriginalShapes) {
		this.apoli$getOriginalShapes = getOriginalShapes;
	}

	@WrapOperation(method = "computeNext", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getCollisionShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;"))
	private VoxelShape apoli$overrideCollisionShapeQuery(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context, Operation<VoxelShape> original) {
		return state instanceof BlockStateCollisionShapeAccess shapeAccess && this.apoli$shouldGetOriginalShapes()
			? shapeAccess.apoli$getOriginalCollisionShape(world, pos, context)
			: original.call(state, world, pos, context);
	}
}
