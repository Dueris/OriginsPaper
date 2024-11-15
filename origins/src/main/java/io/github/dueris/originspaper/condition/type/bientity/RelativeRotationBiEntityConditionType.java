package io.github.dueris.originspaper.condition.type.bientity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.BiEntityConditionType;
import io.github.dueris.originspaper.condition.type.BiEntityConditionTypes;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.util.Comparison;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.function.Function;

public class RelativeRotationBiEntityConditionType extends BiEntityConditionType {

	public static final TypedDataObjectFactory<RelativeRotationBiEntityConditionType> DATA_FACTORY = TypedDataObjectFactory.simple(
		new SerializableData()
			.add("actor_rotation", SerializableDataType.enumValue(RotationType.class), RotationType.HEAD)
			.add("target_rotation", SerializableDataType.enumValue(RotationType.class), RotationType.BODY)
			.add("axes", SerializableDataTypes.AXIS_SET, EnumSet.allOf(Direction.Axis.class))
			.add("comparison", ApoliDataTypes.COMPARISON)
			.add("compare_to", SerializableDataTypes.DOUBLE),
		data -> new RelativeRotationBiEntityConditionType(
			data.get("actor_rotation"),
			data.get("target_rotation"),
			data.get("axes"),
			data.get("comparison"),
			data.get("compare_to")
		),
		(conditionType, serializableData) -> serializableData.instance()
			.set("actor_rotation", conditionType.actorRotationType)
			.set("target_rotation", conditionType.targetRotationType)
			.set("axes", conditionType.axes)
			.set("comparison", conditionType.comparison)
			.set("compare_to", conditionType.compareTo)
	);

	private final RotationType actorRotationType;
	private final RotationType targetRotationType;

	private final EnumSet<Direction.Axis> axes;

	private final Comparison comparison;
	private final double compareTo;

	public RelativeRotationBiEntityConditionType(RotationType actorRotationType, RotationType targetRotationType, EnumSet<Direction.Axis> axes, Comparison comparison, double compareTo) {
		this.actorRotationType = actorRotationType;
		this.targetRotationType = targetRotationType;
		this.axes = axes;
		this.comparison = comparison;
		this.compareTo = compareTo;
	}

	private static double getAngleBetween(Vec3 a, Vec3 b) {
		double dot = a.dot(b);
		return dot / (a.length() * b.length());
	}

	private static Vec3 reduceAxes(Vec3 vector, EnumSet<Direction.Axis> axesToKeep) {
		return new Vec3(
			axesToKeep.contains(Direction.Axis.X) ? vector.x : 0,
			axesToKeep.contains(Direction.Axis.Y) ? vector.y : 0,
			axesToKeep.contains(Direction.Axis.Z) ? vector.z : 0
		);
	}

	private static Vec3 getBodyRotationVector(Entity entity) {

		if (!(entity instanceof LivingEntity livingEntity)) {
			return entity.getViewVector(1.0f);
		}

		float f = livingEntity.getXRot() * ((float) Math.PI / 180);
		float g = -livingEntity.getYRot() * ((float) Math.PI / 180);

		float h = Mth.cos(g);
		float i = Mth.sin(g);
		float j = Mth.cos(f);
		float k = Mth.sin(f);

		return new Vec3(i * j, -k, h * j);

	}

	@Override
	public @NotNull ConditionConfiguration<?> getConfig() {
		return BiEntityConditionTypes.RELATIVE_ROTATION;
	}

	@Override
	public boolean test(Entity actor, Entity target) {

		if (actor == null || target == null) {
			return false;
		}

		Vec3 actorRotation = actorRotationType.getRotation(actor);
		Vec3 targetRotation = targetRotationType.getRotation(target);

		actorRotation = reduceAxes(actorRotation, axes);
		targetRotation = reduceAxes(targetRotation, axes);

		return comparison.compare(getAngleBetween(actorRotation, targetRotation), compareTo);

	}

	public enum RotationType {

		HEAD(e -> e.getViewVector(1.0F)),
		BODY(RelativeRotationBiEntityConditionType::getBodyRotationVector);

		private final Function<Entity, Vec3> function;

		RotationType(Function<Entity, Vec3> function) {
			this.function = function;
		}

		public Vec3 getRotation(Entity entity) {
			return function.apply(entity);
		}

	}

}
