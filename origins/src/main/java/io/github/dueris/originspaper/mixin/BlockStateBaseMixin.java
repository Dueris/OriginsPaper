package io.github.dueris.originspaper.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.serialization.MapCodec;
import io.github.dueris.originspaper.access.BlockStateCollisionShapeAccess;
import io.github.dueris.originspaper.component.PowerHolderComponent;
import io.github.dueris.originspaper.power.type.PhasingPowerType;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockStateBaseMixin extends StateHolder<Block, BlockState> implements BlockStateCollisionShapeAccess {

	@Unique
	private boolean apoli$queryOriginal = false;

	protected BlockStateBaseMixin(Block owner, Reference2ObjectArrayMap<Property<?>, Comparable<?>> propertyMap, MapCodec<BlockState> codec) {
		super(owner, propertyMap, codec);
	}

	@Shadow
	public abstract VoxelShape getCollisionShape(BlockGetter world, BlockPos pos, CollisionContext context);

	@ModifyReturnValue(method = "getCollisionShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;", at = @At("RETURN"))
	private VoxelShape apoli$phaseThroughBlocks(VoxelShape original, BlockGetter blockView, BlockPos blockPos, CollisionContext context) {

		if (context == CollisionContext.empty()) {
			return original;
		}

		else {
			return !apoli$queryOriginal && PhasingPowerType.shouldPhase(context, original, blockPos)
				? Shapes.empty()
				: original;
		}

	}

	@WrapWithCondition(method = "entityInside", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;entityInside(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/Entity;)V"))
	private boolean apoli$preventOnEntityCollisionCallWhenPhasing(Block instance, BlockState state, Level world, BlockPos blockPos, Entity entity) {
		return !PowerHolderComponent.hasPowerType(entity, PhasingPowerType.class, p -> p.doesApply(blockPos));
	}

	@Override
	public VoxelShape apoli$getOriginalCollisionShape(BlockGetter world, BlockPos pos, CollisionContext context) {

		this.apoli$queryOriginal = true;
		VoxelShape originalShape = this.getCollisionShape(world, pos, context);

		this.apoli$queryOriginal = false;
		return originalShape;

	}
}
