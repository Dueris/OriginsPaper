package io.github.dueris.originspaper.power.type;

import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.power.Power;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public abstract class StatusEffectPowerType extends PowerType {

	protected final List<MobEffectInstance> effects = new LinkedList<>();

	public StatusEffectPowerType(List<MobEffectInstance> effectInstances, Optional<EntityCondition> condition) {
		super(condition);
		this.effects.addAll(effectInstances);
	}

	public StatusEffectPowerType(Optional<EntityCondition> condition) {
		super(condition);
	}

	public StatusEffectPowerType() {

	}

	public StatusEffectPowerType(MobEffectInstance effectInstance, Optional<EntityCondition> condition) {
		this(condition);
		this.addEffect(effectInstance);
	}

	public StatusEffectPowerType(MobEffectInstance effectInstance) {
		this(effectInstance, Optional.empty());
	}

	public void addEffect(Holder<MobEffect> effect, int duration) {
		addEffect(effect, duration, 0);
	}

	public void addEffect(Holder<MobEffect> effect, int duration, int amplifier) {
		addEffect(new MobEffectInstance(effect, duration, amplifier));
	}

	public void addEffect(MobEffectInstance instance) {
		effects.add(instance);
	}

	public void applyEffects() {
		effects.stream().map(MobEffectInstance::new).forEach(getHolder()::addEffect);
	}

}
