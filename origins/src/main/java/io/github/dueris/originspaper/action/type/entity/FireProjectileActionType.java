package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.util.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;

import java.util.function.Consumer;

public class FireProjectileActionType {

	public static void action(Entity entity, EntityType<?> entityType, Consumer<Entity> projectileAction, CompoundTag entityNbt, float divergence, float speed, int count) {

		if (!(entity.level() instanceof ServerLevel serverWorld)) {
			return;
		}

		RandomSource random = serverWorld.getRandom();

		Vec3 velocity = entity.getDeltaMovement();
		Vec3 verticalOffset = entity.position().add(0, entity.getEyeHeight(entity.getPose()), 0);

		float pitch = entity.getXRot();
		float yaw = entity.getYRot();

		for (int i = 0; i < count; i++) {

			Entity entityToSpawn = Util
				.getEntityWithPassengersSafe(serverWorld, entityType, entityNbt, verticalOffset, yaw, pitch)
				.orElse(null);

			if (entityToSpawn == null) {
				return;
			}

			if (entityToSpawn instanceof Projectile projectileToSpawn) {

				if (projectileToSpawn instanceof AbstractHurtingProjectile explosiveProjectileToSpawn) {
					explosiveProjectileToSpawn.accelerationPower = speed;
				}

				projectileToSpawn.setOwner(entity);
				projectileToSpawn.shootFromRotation(entity, pitch, yaw, 0F, speed, divergence);

			} else {

				float j = 0.017453292F;
				double k = 0.007499999832361937D;

				float l = -Mth.sin(yaw * j) * Mth.cos(pitch * j);
				float m = -Mth.sin(pitch * j);
				float n = Mth.cos(yaw * j) * Mth.cos(pitch * j);

				Vec3 velocityToApply = new Vec3(l, m, n)
					.normalize()
					.add(random.nextGaussian() * k * divergence, random.nextGaussian() * k * divergence, random.nextGaussian() * k * divergence)
					.scale(speed);

				entityToSpawn.setDeltaMovement(velocityToApply);
				entityToSpawn.push(velocity.x, entity.onGround() ? 0.0D : velocity.y, velocity.z);

			}

			if (!entityNbt.isEmpty()) {

				CompoundTag mergedNbt = entityToSpawn.saveWithoutId(new CompoundTag());
				mergedNbt.merge(entityNbt);

				entityToSpawn.load(mergedNbt);

			}

			serverWorld.tryAddFreshEntityWithPassengers(entityToSpawn);
			projectileAction.accept(entityToSpawn);

		}

	}

	public static ActionTypeFactory<Entity> getFactory() {
		return new ActionTypeFactory<>(
			OriginsPaper.apoliIdentifier("fire_projectile"),
			new SerializableData()
				.add("entity_type", SerializableDataTypes.ENTITY_TYPE)
				.add("projectile_action", ApoliDataTypes.ENTITY_ACTION, null)
				.add("tag", SerializableDataTypes.NBT_COMPOUND, new CompoundTag())
				.add("divergence", SerializableDataTypes.FLOAT, 1F)
				.add("speed", SerializableDataTypes.FLOAT, 1.5F)
				.add("count", SerializableDataTypes.INT, 1),
			(data, entity) -> action(entity,
				data.get("entity_type"),
				data.getOrElse("projectile_action", e -> {
				}),
				data.get("tag"),
				data.get("divergence"),
				data.get("speed"),
				data.get("count")
			)
		);
	}

}
