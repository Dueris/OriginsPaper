package me.dueris.originspaper.factory.action.types.bientity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.parser.reader.DeserializedFactoryJson;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.data.types.Space;
import me.dueris.originspaper.factory.action.ActionFactory;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.function.BiFunction;

public class AddVelocityAction {

	public static void action(DeserializedFactoryJson data, @NotNull Tuple<Entity, Entity> entities) {

		Entity actor = entities.getA();
		Entity target = entities.getB();

		if ((actor == null || target == null) || (target instanceof Player && (target.level().isClientSide ? !data.getBoolean("client") : !data.getBoolean("server")))) {
			return;
		}

		Vector3f vec = new Vector3f(data.getFloat("x"), data.getFloat("y"), data.getFloat("z"));
		TriConsumer<Float, Float, Float> method = data.getBoolean("set") ? target::setDeltaMovement : target::push;

		Reference reference = data.get("reference");
		Vec3 refVec = reference.apply(actor, target);

		Space.transformVectorToBase(refVec, vec, actor.getYRot(), true); // vector normalized by method
		method.accept(vec.x, vec.y, vec.z);

		target.hurtMarked = true;

	}

	public static @NotNull ActionFactory<Tuple<Entity, Entity>> getFactory() {
		return new ActionFactory<>(
			OriginsPaper.apoliIdentifier("add_velocity"),
			InstanceDefiner.instanceDefiner()
				.add("x", SerializableDataTypes.FLOAT, 0F)
				.add("y", SerializableDataTypes.FLOAT, 0F)
				.add("z", SerializableDataTypes.FLOAT, 0F)
				.add("client", SerializableDataTypes.BOOLEAN, true)
				.add("server", SerializableDataTypes.BOOLEAN, true)
				.add("set", SerializableDataTypes.BOOLEAN, false)
				.add("reference", SerializableDataTypes.enumValue(Reference.class), Reference.POSITION),
			AddVelocityAction::action
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
