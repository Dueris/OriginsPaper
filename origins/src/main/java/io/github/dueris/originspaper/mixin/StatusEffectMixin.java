package io.github.dueris.originspaper.mixin;

import com.dragoncommissions.mixbukkit.api.shellcode.impl.api.CallbackInfo;
import io.github.dueris.originspaper.power.EffectImmunityPower;
import io.github.dueris.originspaper.storage.PowerHolderComponent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

@Mixin(MobEffect.class)
public class StatusEffectMixin {

	@Inject(method = "applyInstantenousEffect", locator = At.Value.HEAD)
	public static void wrappedEffectImmunity(MobEffect effect, @Nullable Entity source, @Nullable Entity attacker, @NotNull LivingEntity target, int amplifier, double proximity, CallbackInfo info) {
		boolean callOriginal = true;

		if (PowerHolderComponent.hasPowerType(target.getBukkitEntity(), EffectImmunityPower.class)) {
			for (EffectImmunityPower immunityPower : PowerHolderComponent.getPowers(target.getBukkitEntity(), EffectImmunityPower.class)) {
				if (immunityPower.doesApply(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(effect))) {
					callOriginal = false;
				}
			}
		}

		if (callOriginal) {
			effect.applyEffectTick(target, amplifier);
		}

	}
}
