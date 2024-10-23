package io.github.dueris.originspaper.power.type;

import io.github.dueris.originspaper.power.Power;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class SimpleStatusEffectPowerType extends StatusEffectPowerType {

	public SimpleStatusEffectPowerType(Power power, LivingEntity entity) {
		super(power, entity);
	}

	public SimpleStatusEffectPowerType(Power power, LivingEntity entity, MobEffectInstance effectInstance) {
		super(power, entity, effectInstance);
	}

}
