package io.github.dueris.originspaper.mixin;

import com.dragoncommissions.mixbukkit.api.shellcode.impl.api.CallbackInfo;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.phys.HitResult;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Mixin(ThrownEnderpearl.class)
public class ThrownEnderpearlMixin {
	public static List<ThrownEnderpearl> ENDERIAN_PEARLS = new CopyOnWriteArrayList<>();

	@Inject(method = "onHit", locator = At.Value.HEAD)
	public static void origins$enderianPearlHit(ThrownEnderpearl instance, HitResult result, CallbackInfo info) {
		if (ENDERIAN_PEARLS.contains(instance)) {
			ENDERIAN_PEARLS.remove(instance);
			info.setReturned(true);
			info.setReturnValue(false);
			Entity entity = instance.getOwner();

			for (int i = 0; i < 32; ++i) {
				instance.level().addParticle(ParticleTypes.PORTAL, instance.getX(), instance.getY() + instance.random.nextDouble() * 2.0D, instance.getZ(), instance.random.nextGaussian(), 0.0D, instance.random.nextGaussian());
			}

			if (!instance.level().isClientSide && !instance.isRemoved()) {
				if (entity instanceof ServerPlayer serverPlayerEntity) {
					if (serverPlayerEntity.connection.isAcceptingMessages() && serverPlayerEntity.level() == instance.level() && !serverPlayerEntity.isSleeping()) {

						if (entity.isPassenger()) {
							entity.stopRiding();
						}

						entity.teleportTo(instance.getX(), instance.getY(), instance.getZ());
						entity.fallDistance = 0.0F;
					}
				} else if (entity != null) {
					entity.teleportTo(instance.getX(), instance.getY(), instance.getZ());
					entity.fallDistance = 0.0F;
				}

				instance.discard();
			}
		}
	}
}
