package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.power.factory.PowerType;
import io.github.dueris.originspaper.power.factory.PowerTypeFactory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StackingStatusEffectPower extends StatusEffectPower {
	private final int minStack;
	private final int maxStack;
	private final int durationPerStack;
	private final int tickRate;

	private final Map<LivingEntity, Integer> stackMap = new HashMap<>();

	public StackingStatusEffectPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionTypeFactory<Entity> condition, int loadingPriority,
									 int minStacks, int maxStacks, int durationPerStack, int tickRate, MobEffectInstance effect, List<MobEffectInstance> effects) {
		super(key, type, name, description, hidden, condition, loadingPriority);
		this.minStack = minStacks;
		this.maxStack = maxStacks;
		this.durationPerStack = durationPerStack;
		this.tickRate = tickRate;

		if (effect != null) {
			this.effects.add(effect);
		}
		if (effects != null && !effects.isEmpty()) {
			this.effects.addAll(effects);
		}
	}

	public static @NotNull PowerTypeFactory getFactory() {
		return new PowerTypeFactory(OriginsPaper.apoliIdentifier("stacking_status_effect"), PowerType.getFactory().getSerializableData()
			.add("min_stacks", SerializableDataTypes.INT)
			.add("max_stacks", SerializableDataTypes.INT)
			.add("duration_per_stack", SerializableDataTypes.INT)
			.add("tick_rate", SerializableDataTypes.POSITIVE_INT, 10)
			.add("effect", SerializableDataTypes.STATUS_EFFECT_INSTANCE, null)
			.add("effects", SerializableDataType.of(SerializableDataTypes.STATUS_EFFECT_INSTANCE.listOf()), null));
	}

	@Override
	public void tick(@NotNull Player entity) {
		if (entity.tickCount % tickRate == 0) {
			int currentStack = stackMap.getOrDefault(entity, minStack);

			if (isActive(entity)) {
				currentStack += 1;
				if (currentStack > maxStack) {
					currentStack = maxStack;
				}
				stackMap.put(entity, currentStack);

				if (currentStack > 0) {
					applyEffects(entity, currentStack);
				}
			} else {
				currentStack -= 1;
				if (currentStack < minStack) {
					currentStack = minStack;
				}
				stackMap.put(entity, currentStack);
			}
		}
	}

	@Override
	public void applyEffects(@NotNull LivingEntity entity, int currentStack) {
		effects.forEach(sei -> {
			int duration = durationPerStack * currentStack;
			if (duration > 0) {
				MobEffectInstance applySei = new MobEffectInstance(sei.getEffect(), duration, sei.getAmplifier(), sei.isAmbient(), sei.isVisible(), sei.showIcon());
				entity.addEffect(applySei);
			}
		});
	}
}
