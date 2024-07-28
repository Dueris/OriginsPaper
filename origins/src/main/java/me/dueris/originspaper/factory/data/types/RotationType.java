package me.dueris.originspaper.factory.data.types;

import net.minecraft.core.Direction.Axis;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.function.BiFunction;
import java.util.function.Function;

public enum RotationType {
	HEAD(e -> e.getViewVector(1.0F)),
	BODY(RotationType::getBodyRotationVector);

	private final Function<Entity, Vec3> function;

	RotationType(Function<Entity, Vec3> function) {
		this.function = function;
	}

	public static double getAngleBetween(@NotNull Vec3 a, Vec3 b) {
		double dot = a.dot(b);
		return dot / (a.length() * b.length());
	}

	@Contract("_, _ -> new")
	public static @NotNull Vec3 reduceAxes(Vec3 vector, @NotNull EnumSet<Axis> axesToKeep) {
		return new Vec3(axesToKeep.contains(Axis.X) ? vector.x : 0.0, axesToKeep.contains(Axis.Y) ? vector.y : 0.0, axesToKeep.contains(Axis.Z) ? vector.z : 0.0);
	}

	private static @NotNull Vec3 getBodyRotationVector(Entity entity) {
		if (entity instanceof LivingEntity livingEntity) {
			float f = livingEntity.getXRot() * (float) (Math.PI / 180.0);
			float g = -livingEntity.getYRot() * (float) (Math.PI / 180.0);
			float h = Mth.cos(g);
			float i = Mth.sin(g);
			float j = Mth.cos(f);
			float k = Mth.sin(f);
			return new Vec3(i * j, -k, h * j);
		} else {
			return entity.getViewVector(1.0F);
		}
	}

	public Vec3 getRotation(Entity entity) {
		return this.function.apply(entity);
	}

	public enum Reference {

		POSITION((actor, target) -> target.position().subtract(actor.position())),
		ROTATION((actor, target) -> {

			float pitch = actor.getXRot();
			float yaw = actor.getYRot();

			float i = 0.017453292F;

			float j = -Mth.sin(yaw * i) * Mth.cos(pitch * i);
			float k = -Mth.sin(pitch * i);
			float l = Mth.cos(yaw * i) * Mth.cos(pitch * i);

			return new Vec3(j, k, l);

		});

		final BiFunction<Entity, Entity, Vec3> refFunction;

		Reference(BiFunction<net.minecraft.world.entity.Entity, net.minecraft.world.entity.Entity, Vec3> refFunction) {
			this.refFunction = refFunction;
		}

		public Vec3 apply(net.minecraft.world.entity.Entity actor, net.minecraft.world.entity.Entity target) {
			return refFunction.apply(actor, target);
		}

	}
}
