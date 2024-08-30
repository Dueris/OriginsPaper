package io.github.dueris.originspaper.power.type.simple;

import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.storage.PowerHolderComponent;
import io.github.dueris.originspaper.util.Util;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import org.bukkit.craftbukkit.CraftRegistry;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class WaterBreathingPower {
	public static final ResourceKey<DamageType> NO_WATER_FOR_GILLS = ResourceKey.create(Registries.DAMAGE_TYPE, OriginsPaper.originIdentifier("no_water_for_gills"));

	public static boolean shouldDrown(@NotNull LivingEntity entity) {
		return !entity.isEyeInFluid(FluidTags.WATER)
			&& !entity.hasEffect(MobEffects.WATER_BREATHING)
			&& !entity.hasEffect(MobEffects.CONDUIT_POWER);
	}

	private static int callGetNextAirOnLand(int a, LivingEntity entity) {
		try {
			Method toInvoke = LivingEntity.class.getDeclaredMethod("increaseAirSupply", int.class);
			toInvoke.setAccessible(true);
			return (int) toInvoke.invoke(entity, a);
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new RuntimeException("Unable to call 'getNextAirOnLand' for WaterBreathingPower", e);
		}
	}

	private static int callGetNextAirUnderwater(int a, LivingEntity entity) {
		try {
			Method toInvoke = LivingEntity.class.getDeclaredMethod("decreaseAirSupply", int.class);
			toInvoke.setAccessible(true);
			return (int) toInvoke.invoke(entity, a);
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	public static void tick(@NotNull LivingEntity entity) {

		if (!PowerHolderComponent.hasPower(entity.getBukkitEntity(), "origins:water_breathing")) {
			return;
		}

		if (shouldDrown(entity)) {

			int landGain = callGetNextAirOnLand(0, entity);
			int landLoss = callGetNextAirUnderwater(entity.getAirSupply(), entity);

			if (!entity.isInRain()) {

				entity.setAirSupply(landLoss - landGain);
				if (entity.getAirSupply() != -20) {
					return;
				}

				entity.setAirSupply(0);
				entity.hurt(Util.getDamageSource(CraftRegistry.getMinecraftRegistry().registryOrThrow(Registries.DAMAGE_TYPE).get(NO_WATER_FOR_GILLS)), 2.0F);

				for (int i = 0; i < 8; ++i) {

					double dx = entity.getRandom().nextDouble() - entity.getRandom().nextDouble();
					double dy = entity.getRandom().nextDouble() - entity.getRandom().nextDouble();
					double dz = entity.getRandom().nextDouble() - entity.getRandom().nextDouble();

					entity.level().addParticle(ParticleTypes.BUBBLE, entity.getRandomX(0.5), entity.getEyeHeight(entity.getPose()), entity.getRandomZ(0.5), dx * 0.5, dy * 0.5 + 0.25, dz * 0.5);

				}

			} else {
				entity.setAirSupply(entity.getAirSupply() - landGain);
			}

		} else if (entity.getAirSupply() < entity.getMaxAirSupply()) {
			entity.setAirSupply(callGetNextAirOnLand(entity.getAirSupply(), entity));
		}

	}
}
