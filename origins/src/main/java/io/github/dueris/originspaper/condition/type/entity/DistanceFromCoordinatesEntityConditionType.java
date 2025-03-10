package io.github.dueris.originspaper.condition.type.entity;

import com.mojang.datafixers.util.Either;
import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.context.EntityConditionContext;
import io.github.dueris.originspaper.condition.type.EntityConditionType;
import io.github.dueris.originspaper.condition.type.EntityConditionTypes;
import io.github.dueris.originspaper.condition.type.meta.DistanceFromCoordinatesMetaConditionType;
import io.github.dueris.originspaper.util.Comparison;
import io.github.dueris.originspaper.util.Shape;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class DistanceFromCoordinatesEntityConditionType extends EntityConditionType implements DistanceFromCoordinatesMetaConditionType {

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

	public DistanceFromCoordinatesEntityConditionType(DistanceFromCoordinatesMetaConditionType.Reference reference, Shape shape, Optional<Integer> roundToDigit, Vec3 offset, Comparison comparison, double compareTo, boolean scaleReferenceToDimension, boolean scaleDistanceToDimension, boolean ignoreX, boolean ignoreY, boolean ignoreZ) {
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
	public boolean test(Entity entity) {
		return testCondition(Either.right(new EntityConditionContext(entity)));
	}

	@Override
	public @NotNull ConditionConfiguration<?> getConfig() {
		return EntityConditionTypes.DISTANCE_FROM_COORDINATES;
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
