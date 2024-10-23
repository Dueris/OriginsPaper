package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerTypeFactory;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.function.Predicate;

public class ParticlePowerType extends PowerType {

	private final Predicate<Tuple<Entity, Entity>> biEntityCondition;
	private final ParticleOptions particleEffect;

	private final Vec3 spread;

	private final int frequency;
	private final int count;

	private final double offsetX;
	private final double offsetY;
	private final double offsetZ;

	private final float speed;

	private final boolean visibleInFirstPerson;
	private final boolean visibleWhileInvisible;
	private final boolean force;

	public ParticlePowerType(Power power, LivingEntity entity, ParticleOptions particleEffect, Predicate<Tuple<Entity, Entity>> biEntityCondition, int count, float speed, boolean force, Vec3 spread, double offsetX, double offsetY, double offsetZ, int frequency, boolean visibleInFirstPerson, boolean visibleWhileInvisible) {
		super(power, entity);
		this.particleEffect = particleEffect;
		this.biEntityCondition = biEntityCondition;
		this.count = Math.max(0, count);
		this.speed = speed;
		this.force = force;
		this.spread = spread;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.offsetZ = offsetZ;
		this.frequency = frequency;
		this.visibleInFirstPerson = visibleInFirstPerson;
		this.visibleWhileInvisible = visibleWhileInvisible;
		setTicking();
	}

	public static PowerTypeFactory<?> getFactory() {
		return new PowerTypeFactory<>(
			OriginsPaper.apoliIdentifier("particle"),
			new SerializableData()
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
				.add("visible_in_first_person", SerializableDataTypes.BOOLEAN, false)
				.add("visible_while_invisible", SerializableDataTypes.BOOLEAN, false),
			data -> (power, entity) -> new ParticlePowerType(power, entity,
				data.get("particle"),
				data.get("bientity_condition"),
				data.get("count"),
				data.get("speed"),
				data.get("force"),
				data.get("spread"),
				data.get("offset_x"),
				data.get("offset_y"),
				data.get("offset_z"),
				data.get("frequency"),
				data.get("visible_in_first_person"),
				data.get("visible_while_invisible")
			)
		).allowCondition();
	}

	public boolean doesApply(Player viewer, boolean inFirstPerson) {
		return (!entity.isInvisibleTo(viewer) || this.isVisibleWhileInvisible())
			&& (entity != viewer || (!inFirstPerson || this.isVisibleInFirstPerson()))
			&& (viewer.blockPosition().closerToCenterThan(entity.position(), this.shouldForce() ? 512 : 32))
			&& (entity.tickCount % this.getFrequency() == 0)
			&& (biEntityCondition == null || biEntityCondition.test(new Tuple<>(entity, viewer)));
	}

	public ParticleOptions getParticle() {
		return particleEffect;
	}

	public Vec3 getSpread() {
		return spread;
	}

	public int getFrequency() {
		return frequency;
	}

	public double getOffsetX() {
		return offsetX;
	}

	public double getOffsetY() {
		return offsetY;
	}

	public double getOffsetZ() {
		return offsetZ;
	}

	public int getCount() {
		return count;
	}

	public float getSpeed() {
		return speed;
	}

	public boolean shouldForce() {
		return force;
	}

	public boolean isVisibleInFirstPerson() {
		return visibleInFirstPerson;
	}

	public boolean isVisibleWhileInvisible() {
		return visibleWhileInvisible;
	}

	@Override
	public void tick() {
		double velocityX;
		double velocityY;
		double velocityZ;

		if (entity instanceof Player player) {
			if (!this.doesApply(player, false)) {
				return;
			}

			Vec3 spread = this
				.getSpread()
				.multiply(player.getBbWidth(), player.getEyeHeight(player.getPose()), player.getBbWidth());
			Vec3 particlePos = player
				.position()
				.add(this.getOffsetX(), this.getOffsetY(), this.getOffsetZ());

			if (this.getCount() == 0) {

				velocityX = spread.x() * this.getSpeed();
				velocityY = spread.y() * this.getSpeed();
				velocityZ = spread.z() * this.getSpeed();

				((ServerLevel) player.level()).sendParticles((ServerPlayer) player, this.getParticle(), particlePos.x, particlePos.y, particlePos.z, count, velocityX, velocityY, velocityZ, speed, force);
			} else {

				for (int i = 0; i < this.getCount(); i++) {

					Vec3 newSpread = spread.multiply(player.random.nextGaussian(), player.random.nextGaussian(), player.random.nextGaussian());
					Vec3 newParticlePos = particlePos.add(newSpread);

					velocityX = (2.0 * player.random.nextDouble() - 1.0) * this.getSpeed();
					velocityY = (2.0 * player.random.nextDouble() - 1.0) * this.getSpeed();
					velocityZ = (2.0 * player.random.nextDouble() - 1.0) * this.getSpeed();

					((ServerLevel) player.level()).sendParticles((ServerPlayer) player, this.getParticle(), newParticlePos.x, newParticlePos.y, newParticlePos.z, count, velocityX, velocityY, velocityZ, speed, force);

				}

			}
		}
	}

}
