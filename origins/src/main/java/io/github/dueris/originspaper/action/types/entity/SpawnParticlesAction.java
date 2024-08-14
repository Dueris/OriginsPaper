package io.github.dueris.originspaper.action.types.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.parser.reader.DeserializedFactoryJson;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class SpawnParticlesAction {

	public static void action(DeserializedFactoryJson data, @NotNull Entity entity) {

		if (!(entity.level() instanceof ServerLevel serverWorld)) {
			return;
		}

		Vec3 delta = data
			.<Vec3>get("spread")
			.multiply(entity.getBbWidth(), entity.getEyeHeight(entity.getPose()), entity.getBbWidth());
		Vec3 pos = entity
			.position()
			.add(data.getDouble("offset_x"), data.getDouble("offset_y"), data.getDouble("offset_z"));

		Predicate<Tuple<Entity, Entity>> biEntityCondition = data.get("bientity_condition");
		ParticleOptions particleEffect = data.get("particle");

		boolean force = data.getBoolean("force");
		float speed = data.getFloat("speed");
		int count = Math.max(0, data.getInt("count"));

		for (ServerPlayer player : serverWorld.players()) {
			if (biEntityCondition == null || biEntityCondition.test(new Tuple<>(entity, player))) {
				serverWorld.sendParticles(player, particleEffect, force, pos.x(), pos.y(), pos.z(), count, delta.x(), delta.y(), delta.z(), speed);
			}
		}

	}

	public static @NotNull ActionFactory<Entity> getFactory() {
		return new ActionFactory<>(OriginsPaper.apoliIdentifier("spawn_particles"),
			InstanceDefiner.instanceDefiner()
				.add("particle", SerializableDataTypes.PARTICLE_EFFECT_OR_TYPE)
				.add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null)
				.add("count", SerializableDataTypes.INT, 1)
				.add("speed", SerializableDataTypes.FLOAT, 0.0F)
				.add("force", SerializableDataTypes.BOOLEAN, false)
				.add("spread", SerializableDataTypes.VECTOR, new Vec3(0.5, 0.5, 0.5))
				.add("offset_x", SerializableDataTypes.DOUBLE, 0.0D)
				.add("offset_y", SerializableDataTypes.DOUBLE, 0.5D)
				.add("offset_z", SerializableDataTypes.DOUBLE, 0.0D),
			SpawnParticlesAction::action
		);
	}
}
