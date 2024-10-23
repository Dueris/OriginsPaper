package io.github.dueris.originspaper.condition.type.bientity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.util.Comparison;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.function.Function;

public class RelativeRotationConditionType {

	public static boolean condition(Entity actor, Entity target, RotationType actorRotationType, RotationType targetRotationType, EnumSet<Direction.Axis> axes, Comparison comparison, double compareTo) {

		if (actor == null || target == null) {
			return false;
		}

		Vec3 actorRotation = actorRotationType.getRotation(actor);
		Vec3 targetRotation = targetRotationType.getRotation(target);

		actorRotation = reduceAxes(actorRotation, axes);
		targetRotation = reduceAxes(targetRotation, axes);

		return comparison.compare(getAngleBetween(actorRotation, targetRotation), compareTo);

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

	public static ConditionTypeFactory<Tuple<Entity, Entity>> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("relative_rotation"),
			new SerializableData()
				.add("actor_rotation", SerializableDataType.enumValue(RotationType.class), RotationType.HEAD)
				.add("target_rotation", SerializableDataType.enumValue(RotationType.class), RotationType.BODY)
				.add("axes", SerializableDataTypes.AXIS_SET, EnumSet.allOf(Direction.Axis.class))
				.add("comparison", ApoliDataTypes.COMPARISON)
				.add("compare_to", SerializableDataTypes.DOUBLE),
			(data, actorAndTarget) -> condition(actorAndTarget.getA(), actorAndTarget.getB(),
				data.get("actor_rotation"),
				data.get("target_rotation"),
				data.get("axes"),
				data.get("comparison"),
				data.get("compare_to")
			)
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

	public enum RotationType {

		HEAD(e -> e.getViewVector(1.0F)),
		BODY(RelativeRotationConditionType::getBodyRotationVector);

		private final Function<Entity, Vec3> function;

		RotationType(Function<Entity, Vec3> function) {
			this.function = function;
		}

		public Vec3 getRotation(Entity entity) {
			return function.apply(entity);
		}

	}

}
