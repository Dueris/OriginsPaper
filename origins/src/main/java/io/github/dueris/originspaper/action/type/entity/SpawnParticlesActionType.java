package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class SpawnParticlesActionType {

	public static void action(@NotNull Entity entity, ParticleOptions particle, Predicate<Tuple<Entity, Entity>> biEntityCondition, Vec3 offset, Vec3 spread, boolean force, float speed, int count) {

		if (!(entity.level() instanceof ServerLevel serverWorld)) {
			return;
		}

		Vec3 delta = spread.multiply(entity.getBbWidth(), entity.getBbHeight(), entity.getBbWidth());
		Vec3 pos = entity.position().add(offset);

		for (ServerPlayer player : serverWorld.players()) {

			if (biEntityCondition.test(new Tuple<>(entity, player))) {
				serverWorld.sendParticles(player, particle, force, pos.x(), pos.y(), pos.z(), count, delta.x(), delta.y(), delta.z(), speed);
			}

		}

	}

	public static @NotNull ActionTypeFactory<Entity> getFactory() {
		return new ActionTypeFactory<>(OriginsPaper.apoliIdentifier("spawn_particles"),
			new SerializableData()
				.add("particle", SerializableDataTypes.PARTICLE_EFFECT_OR_TYPE)
				.add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null)
				.add("offset_x", SerializableDataTypes.DOUBLE, 0.0D)
				.add("offset_y", SerializableDataTypes.DOUBLE, 0.5D)
				.add("offset_z", SerializableDataTypes.DOUBLE, 0.0D)
				.add("spread", SerializableDataTypes.VECTOR, new Vec3(0.5, 0.5, 0.5))
				.add("force", SerializableDataTypes.BOOLEAN, false)
				.add("speed", SerializableDataTypes.FLOAT, 0.0F)
				.add("count", SerializableDataTypes.INT, 1),
			(data, entity) -> action(entity,
				data.get("particle"),
				data.getOrElse("bientity_condition", actorAndTarget -> true),
				new Vec3(data.get("offset_x"), data.get("offset_y"), data.get("offset_z")),
				data.get("spread"),
				data.get("force"),
				data.get("speed"),
				data.get("count")
			)
		);
	}

}
