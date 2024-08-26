package io.github.dueris.originspaper.condition.types.bientity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.types.Comparison;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.function.Function;

public class RelativeRotationCondition {

	public static boolean condition(SerializableData.Instance data, @NotNull Tuple<Entity, Entity> actorAndTarget) {

		Entity actor = actorAndTarget.getA();
		Entity target = actorAndTarget.getB();

		if (actor == null || target == null) {
			return false;
		}

		RotationType actorRotationType = data.get("actor_rotation");
		RotationType targetRotationType = data.get("target_rotation");

		Vec3 actorRotation = actorRotationType.getRotation(actor);
		Vec3 targetRotation = targetRotationType.getRotation(target);

		EnumSet<Direction.Axis> axes = data.get("axes");

		actorRotation = reduceAxes(actorRotation, axes);
		targetRotation = reduceAxes(targetRotation, axes);

		Comparison comparison = data.get("comparison");
		double compareTo = data.get("compare_to");
		double angle = getAngleBetween(actorRotation, targetRotation);

		return comparison.compare(angle, compareTo);

	}

	private static double getAngleBetween(@NotNull Vec3 a, Vec3 b) {
		double dot = a.dot(b);
		return dot / (a.length() * b.length());
	}

	private static @NotNull Vec3 reduceAxes(Vec3 vector, @NotNull EnumSet<Direction.Axis> axesToKeep) {
		return new Vec3(
			axesToKeep.contains(Direction.Axis.X) ? vector.x : 0,
			axesToKeep.contains(Direction.Axis.Y) ? vector.y : 0,
			axesToKeep.contains(Direction.Axis.Z) ? vector.z : 0
		);
	}

	public static @NotNull ConditionTypeFactory<Tuple<Entity, Entity>> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("relative_rotation"),
			SerializableData.serializableData()
				.add("axes", SerializableDataTypes.enumSet(Direction.Axis.class, SerializableDataTypes.AXIS), EnumSet.allOf(Direction.Axis.class))
				.add("actor_rotation", SerializableDataTypes.enumValue(RotationType.class), RotationType.HEAD)
				.add("target_rotation", SerializableDataTypes.enumValue(RotationType.class), RotationType.BODY)
				.add("comparison", ApoliDataTypes.COMPARISON)
				.add("compare_to", SerializableDataTypes.DOUBLE),
			RelativeRotationCondition::condition
		);
	}

	private static @NotNull Vec3 getBodyRotationVector(Entity entity) {

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
		BODY(RelativeRotationCondition::getBodyRotationVector);

		private final Function<Entity, Vec3> function;

		RotationType(Function<Entity, Vec3> function) {
			this.function = function;
		}

		public Vec3 getRotation(Entity entity) {
			return function.apply(entity);
		}

	}
}
