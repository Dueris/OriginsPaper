package io.github.dueris.originspaper.entity;

import io.github.dueris.originspaper.OriginsPaper;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

public class EnderianPearlEntity extends ThrowableItemProjectile {
	public static final EntityType<EnderianPearlEntity> ENDERIAN_PEARL = register("enderian_pearl", EntityType.Builder.of(EnderianPearlEntity::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10));

	public EnderianPearlEntity(EntityType<? extends EnderianPearlEntity> type, Level world) {
		super(type, world);
	}

	public static void bootstrap() {
	}

	private static <T extends Entity> @NotNull EntityType<T> register(String id, EntityType.@NotNull Builder type) {
		return Registry.register(BuiltInRegistries.ENTITY_TYPE, OriginsPaper.originIdentifier(id), (EntityType<T>) type.build(id));
	}

	@Override
	protected @NotNull Item getDefaultItem() {
		return Items.ENDER_PEARL;
	}

	@Override
	protected void onHit(@NotNull HitResult hitResult) {
		Entity entity = this.getOwner();

		for (int i = 0; i < 32; ++i) {
			this.level().addParticle(ParticleTypes.PORTAL, this.getX(), this.getY() + this.random.nextDouble() * 2.0D, this.getZ(), this.random.nextGaussian(), 0.0D, this.random.nextGaussian());
		}

		if (!this.level().isClientSide && !this.isRemoved()) {
			if (entity instanceof ServerPlayer serverPlayer) {
				if (serverPlayer.connection.isAcceptingMessages() && serverPlayer.level() == this.level() && !serverPlayer.isSleeping()) {

					if (entity.getBukkitEntity().getVehicle() != null) {
						entity.stopRiding();
					}

					entity.teleportTo(this.getX(), this.getY(), this.getZ());
					entity.fallDistance = 0.0F;
				}
			} else if (entity != null) {
				entity.teleportTo(this.getX(), this.getY(), this.getZ());
				entity.fallDistance = 0.0F;
			}

			this.discard();
		}
	}

	@Override
	public void tick() {
		Entity entity = this.getOwner();
		if (entity instanceof Player && !entity.isAlive()) {
			this.discard();
		} else {
			super.tick();
		}
	}
}
