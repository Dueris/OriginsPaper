package io.github.dueris.originspaper.action.type.bientity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import io.github.dueris.originspaper.util.Space;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.util.TriConsumer;
import org.joml.Vector3f;

import java.util.function.BiFunction;

public class AddVelocityActionType {

	public static void action(Entity actor, Entity target, Reference reference, Vector3f velocity, boolean set) {

		TriConsumer<Float, Float, Float> method = set
			? target::setDeltaMovement
			: target::push;

		Vec3 refVec = reference.apply(actor, target);
		Space.transformVectorToBase(refVec, velocity, actor.getYRot(), true); // vector normalized by method

		method.accept(velocity.x, velocity.y, velocity.z);
		target.hurtMarked = true;

	}

	public static ActionTypeFactory<Tuple<Entity, Entity>> getFactory() {
		return new ActionTypeFactory<>(
			OriginsPaper.apoliIdentifier("add_velocity"),
			new SerializableData()
				.add("reference", SerializableDataType.enumValue(Reference.class), Reference.POSITION)
				.add("x", SerializableDataTypes.FLOAT, 0F)
				.add("y", SerializableDataTypes.FLOAT, 0F)
				.add("z", SerializableDataTypes.FLOAT, 0F)
				.add("set", SerializableDataTypes.BOOLEAN, false),
			(data, actorAndTarget) -> action(actorAndTarget.getA(), actorAndTarget.getB(),
				data.get("reference"),
				new Vector3f(data.get("x"), data.get("y"), data.get("z")),
				data.get("set")
			)
		);
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

		Reference(BiFunction<Entity, Entity, Vec3> refFunction) {
			this.refFunction = refFunction;
		}

		public Vec3 apply(Entity actor, Entity target) {
			return refFunction.apply(actor, target);
		}

	}

}
