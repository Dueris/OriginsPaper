package io.github.dueris.originspaper.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import io.github.dueris.originspaper.component.PowerHolderComponent;
import io.github.dueris.originspaper.power.type.EffectImmunityPowerType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(MobEffect.class)
public abstract class MobEffectMixin {

	@WrapMethod(method = "applyInstantenousEffect")
	private void apoli$instantEffectImmunity(Entity source, Entity attacker, LivingEntity target, int amplifier, double proximity, Operation<Void> original) {

		if (!PowerHolderComponent.hasPowerType(target, EffectImmunityPowerType.class, p -> p.doesApply(BuiltInRegistries.MOB_EFFECT.wrapAsHolder((MobEffect) (Object) this)))) {
			original.call(source, attacker, target, amplifier, proximity);
		}

	}
}
