package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.type.EntityActionType;
import io.github.dueris.originspaper.action.type.EntityActionTypes;
import io.github.dueris.originspaper.condition.BiEntityCondition;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class SpawnParticlesEntityActionType extends EntityActionType {

	public static final TypedDataObjectFactory<SpawnParticlesEntityActionType> DATA_FACTORY = TypedDataObjectFactory.simple(
		new SerializableData()
			.add("bientity_condition", BiEntityCondition.DATA_TYPE.optional(), Optional.empty())
			.add("particle", SerializableDataTypes.PARTICLE_EFFECT_OR_TYPE)
			.add("offset_x", SerializableDataTypes.DOUBLE, 0.0D)
			.add("offset_y", SerializableDataTypes.DOUBLE, 0.5D)
			.add("offset_z", SerializableDataTypes.DOUBLE, 0.0D)
			.addFunctionedDefault("offset", SerializableDataTypes.VECTOR, data -> new Vec3(data.getDouble("offset_x"), data.getDouble("offset_y"), data.getDouble("offset_z")))
			.add("spread", SerializableDataTypes.VECTOR, new Vec3(0.5D, 0.5D, 0.5D))
			.add("force", SerializableDataTypes.BOOLEAN, false)
			.add("speed", SerializableDataTypes.FLOAT, 0.0F)
			.add("count", SerializableDataTypes.INT, 1),
		data -> new SpawnParticlesEntityActionType(
			data.get("bientity_condition"),
			data.get("particle"),
			data.get("offset"),
			data.get("spread"),
			data.get("force"),
			data.get("speed"),
			data.get("count")
		),
		(actionType, serializableData) -> serializableData.instance()
			.set("bientity_condition", actionType.biEntityCondition)
			.set("particle", actionType.particle) // TODO - eggo response
			.set("offset", actionType.offset)
			.set("spread", actionType.spread)
			.set("force", actionType.force)
			.set("speed", actionType.speed)
			.set("count", actionType.count)
	);

	private final Optional<BiEntityCondition> biEntityCondition;
	private final ParticleOptions particle;

	private final Vec3 offset;
	private final Vec3 spread;

	private final boolean force;
	private final float speed;
	private final int count;

	public SpawnParticlesEntityActionType(Optional<BiEntityCondition> biEntityCondition, ParticleOptions particle, Vec3 offset, Vec3 spread, boolean force, float speed, int count) {
		this.biEntityCondition = biEntityCondition;
		this.particle = particle;
		this.offset = offset;
		this.spread = spread;
		this.force = force;
		this.speed = speed;
		this.count = count;
	}

	@Override
	protected void execute(Entity entity) {

		if (!(entity.level() instanceof ServerLevel serverWorld)) {
			return;
		}

		Vec3 delta = spread.multiply(entity.getBbWidth(), entity.getBbHeight(), entity.getBbWidth());
		Vec3 pos = entity.position().add(offset);

		for (ServerPlayer player : serverWorld.players()) {

			if (biEntityCondition.map(condition -> condition.test(entity, player)).orElse(true)) {
				serverWorld.sendParticles(player, particle, force, pos.x(), pos.y(), pos.z(), count, delta.x(), delta.y(), delta.z(), speed);
			}

		}

	}

	@Override
	public @NotNull ActionConfiguration<?> getConfig() {
		return EntityActionTypes.SPAWN_PARTICLES;
	}

}
