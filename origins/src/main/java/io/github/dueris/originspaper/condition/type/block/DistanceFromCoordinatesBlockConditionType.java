package io.github.dueris.originspaper.condition.type.block;

import com.mojang.datafixers.util.Either;
import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.context.BlockConditionContext;
import io.github.dueris.originspaper.condition.type.BlockConditionType;
import io.github.dueris.originspaper.condition.type.BlockConditionTypes;
import io.github.dueris.originspaper.condition.type.meta.DistanceFromCoordinatesMetaConditionType;
import io.github.dueris.originspaper.util.Comparison;
import io.github.dueris.originspaper.util.Shape;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * @author Alluysl (refactored by eggohito)
 */
public class DistanceFromCoordinatesBlockConditionType extends BlockConditionType implements DistanceFromCoordinatesMetaConditionType {

	private final Reference reference;
	private final Shape shape;

	private final Optional<Integer> roundToDigit;
	private final Vec3 offset;

	private final Comparison comparison;
	private final double compareTo;

	private final boolean scaleReferenceToDimension;
	private final boolean scaleDistanceToDimension;

	private final boolean ignoreX;
	private final boolean ignoreY;
	private final boolean ignoreZ;

	public DistanceFromCoordinatesBlockConditionType(DistanceFromCoordinatesMetaConditionType.Reference reference, Shape shape, Optional<Integer> roundToDigit, Vec3 offset, Comparison comparison, double compareTo, boolean scaleReferenceToDimension, boolean scaleDistanceToDimension, boolean ignoreX, boolean ignoreY, boolean ignoreZ) {
		this.reference = reference;
		this.shape = shape;
		this.roundToDigit = roundToDigit;
		this.offset = offset;
		this.comparison = comparison;
		this.compareTo = compareTo;
		this.scaleReferenceToDimension = scaleReferenceToDimension;
		this.scaleDistanceToDimension = scaleDistanceToDimension;
		this.ignoreX = ignoreX;
		this.ignoreY = ignoreY;
		this.ignoreZ = ignoreZ;
	}

	@Override
	public boolean test(Level world, BlockPos pos, BlockState blockState, Optional<BlockEntity> blockEntity) {
		return testCondition(Either.left(new BlockConditionContext(world, pos, blockState, blockEntity)));
	}

	@Override
	public @NotNull ConditionConfiguration<?> getConfig() {
		return BlockConditionTypes.DISTANCE_FROM_COORDINATES;
	}

	@Override
	public Reference reference() {
		return reference;
	}

	@Override
	public Shape shape() {
		return shape;
	}

	@Override
	public Optional<Integer> roundToDigit() {
		return roundToDigit;
	}

	@Override
	public Vec3 offset() {
		return offset;
	}

	@Override
	public Comparison comparison() {
		return comparison;
	}

	@Override
	public double compareTo() {
		return compareTo;
	}

	@Override
	public boolean scaleReferenceToDimension() {
		return scaleReferenceToDimension;
	}

	@Override
	public boolean scaleDistanceToDimension() {
		return scaleDistanceToDimension;
	}

	@Override
	public boolean ignoreX() {
		return ignoreX;
	}

	@Override
	public boolean ignoreY() {
		return ignoreY;
	}

	@Override
	public boolean ignoreZ() {
		return ignoreZ;
	}

}
