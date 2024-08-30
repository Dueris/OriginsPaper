package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.factory.PowerType;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class ParticlePower extends PowerType {
	private final ParticleOptions particleEffect;
	private final Predicate<Tuple<Entity, Entity>> biEntityCondition;
	private final int count;
	private final float speed;
	private final boolean force;
	private final Vec3 spread;
	private final double offsetX;
	private final double offsetY;
	private final double offsetZ;
	private final int frequency;
	private final boolean visibleWhileInvisible;

	public ParticlePower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionTypeFactory<Entity> condition, int loadingPriority,
						 ParticleOptions particleEffect, ConditionTypeFactory<Tuple<Entity, Entity>> biEntityCondition, int count, float speed, boolean force, Vec3 spread, double offsetX, double offsetY,
						 double offsetZ, int frequency, boolean visibleWhileInvisible) {
		super(key, type, name, description, hidden, condition, loadingPriority);
		this.particleEffect = particleEffect;
		this.biEntityCondition = biEntityCondition;
		this.count = count;
		this.speed = speed;
		this.force = force;
		this.spread = spread;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.offsetZ = offsetZ;
		this.frequency = frequency;
		this.visibleWhileInvisible = visibleWhileInvisible;
	}

	public static SerializableData getFactory() {
		return PowerType.getFactory().typedRegistry(OriginsPaper.apoliIdentifier("particle"))
			.add("particle", SerializableDataTypes.PARTICLE_EFFECT_OR_TYPE)
			.add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null)
			.add("count", SerializableDataTypes.INT, 1)
			.add("speed", SerializableDataTypes.FLOAT, 0.0F)
			.add("force", SerializableDataTypes.BOOLEAN, false)
			.add("spread", SerializableDataTypes.VECTOR, new Vec3(0.5, 0.5, 0.5))
			.add("offset_x", SerializableDataTypes.DOUBLE, 0.0D)
			.add("offset_y", SerializableDataTypes.DOUBLE, 0.5D)
			.add("offset_z", SerializableDataTypes.DOUBLE, 0.0D)
			.add("frequency", SerializableDataTypes.POSITIVE_INT)
			.add("visible_while_invisible", SerializableDataTypes.BOOLEAN, false);
	}

	@Override
	public void tick(Player player) {
		double velocityX;
		double velocityY;
		double velocityZ;

		if (!doesApply(player, player)) {
			return;
		}

		Vec3 spread = this.spread
			.multiply(player.getBbWidth(), player.getEyeHeight(player.getPose()), player.getBbWidth());
		Vec3 particlePos = player
			.position()
			.add(offsetX, offsetY, offsetZ);

		if (count == 0) {

			velocityX = spread.x() * speed;
			velocityY = spread.y() * speed;
			velocityZ = spread.z() * speed;

			((ServerLevel) player.level()).sendParticles(particleEffect, particlePos.x(), particlePos.y(), particlePos.z(), count, velocityX, velocityY, velocityZ, speed);

		} else {

			for (int i = 0; i < count; i++) {

				Vec3 newSpread = spread.multiply(player.random.nextGaussian(), player.random.nextGaussian(), player.random.nextGaussian());
				Vec3 newParticlePos = particlePos.add(newSpread);

				velocityX = (2.0 * player.random.nextDouble() - 1.0) * speed;
				velocityY = (2.0 * player.random.nextDouble() - 1.0) * speed;
				velocityZ = (2.0 * player.random.nextDouble() - 1.0) * speed;

				((ServerLevel) player.level()).sendParticles(particleEffect, newParticlePos.x(), newParticlePos.y(), newParticlePos.z(), count, velocityX, velocityY, velocityZ, speed);

			}

		}
	}

	public boolean doesApply(Player viewer, @NotNull Entity entity) {
		return (!entity.isInvisibleTo(viewer) || visibleWhileInvisible)
			&& (viewer.blockPosition().closerToCenterThan(entity.position(), force ? 512 : 32))
			&& (entity.tickCount % frequency == 0)
			&& (biEntityCondition == null || biEntityCondition.test(new Tuple<>(entity, viewer)));
	}
}
