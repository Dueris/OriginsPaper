package io.github.dueris.originspaper.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import io.github.dueris.originspaper.access.ThrownEnderianPearlEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ThrownEnderpearl.class)
public abstract class ThrownEnderpearlMixin extends ThrowableItemProjectile implements ThrownEnderianPearlEntity {
	@Unique
	private boolean originspaper$isEnderian;

	public ThrownEnderpearlMixin(EntityType<? extends ThrowableItemProjectile> type, Level world) {
		super(type, world);
	}

	@Override
	public void originspaper$setEnderianPearl() {
		originspaper$isEnderian = true;
	}

	@WrapMethod(method = "onHit")
	public void originspaper$handleEnderian(HitResult hitResult, Operation<Void> original) {
		if (!originspaper$isEnderian) {
			original.call(hitResult);
		} else {
			super.onHit(hitResult);
			Entity entity = this.getOwner();

			for (int i = 0; i < 32; ++i) {
				this.level().addParticle(ParticleTypes.PORTAL, this.getX(), this.getY() + this.random.nextDouble() * 2.0D, this.getZ(), this.random.nextGaussian(), 0.0D, this.random.nextGaussian());
			}

			if (!this.level().isClientSide && !this.isRemoved()) {
				if (entity instanceof ServerPlayer serverPlayerEntity) {
					if (serverPlayerEntity.connection.isAcceptingMessages() && serverPlayerEntity.level() == this.level() && !serverPlayerEntity.isSleeping()) {

						if (entity.isPassenger()) {
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
	}
}
