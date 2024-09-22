package io.github.dueris.originspaper.power.type;

import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.power.factory.PowerType;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

public class StatusEffectPower extends PowerType {
	protected final List<MobEffectInstance> effects = new LinkedList<>();

	public StatusEffectPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionTypeFactory<Entity> condition, int loadingPriority) {
		super(key, type, name, description, hidden, condition, loadingPriority);
	}

	public StatusEffectPower addEffect(Holder<MobEffect> effect) {
		return addEffect(effect, 80);
	}

	public StatusEffectPower addEffect(Holder<MobEffect> effect, int lingerDuration) {
		return addEffect(effect, lingerDuration, 0);
	}

	public StatusEffectPower addEffect(Holder<MobEffect> effect, int lingerDuration, int amplifier) {
		return addEffect(new MobEffectInstance(effect, lingerDuration, amplifier));
	}

	public StatusEffectPower addEffect(MobEffectInstance instance) {
		effects.add(instance);
		return this;
	}

	public void applyEffects(@NotNull LivingEntity entity, int currentStack) {
		effects.stream().map(MobEffectInstance::new).forEach(entity::addEffect);
	}
}
