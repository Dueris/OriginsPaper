package io.github.dueris.originspaper.power.type;

import io.github.dueris.originspaper.power.Power;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

import java.util.LinkedList;
import java.util.List;

public class StatusEffectPowerType extends PowerType {

	protected final List<MobEffectInstance> effects = new LinkedList<>();

	public StatusEffectPowerType(Power power, LivingEntity entity) {
		super(power, entity);
	}

	public StatusEffectPowerType(Power power, LivingEntity entity, MobEffectInstance effectInstance) {
		super(power, entity);
		addEffect(effectInstance);
	}

	public StatusEffectPowerType addEffect(Holder<MobEffect> effect) {
		return addEffect(effect, 80);
	}

	public StatusEffectPowerType addEffect(Holder<MobEffect> effect, int lingerDuration) {
		return addEffect(effect, lingerDuration, 0);
	}

	public StatusEffectPowerType addEffect(Holder<MobEffect> effect, int lingerDuration, int amplifier) {
		return addEffect(new MobEffectInstance(effect, lingerDuration, amplifier));
	}

	public StatusEffectPowerType addEffect(MobEffectInstance instance) {
		effects.add(instance);
		return this;
	}

	public void applyEffects() {
		effects.stream().map(MobEffectInstance::new).forEach(entity::addEffect);
	}
}
