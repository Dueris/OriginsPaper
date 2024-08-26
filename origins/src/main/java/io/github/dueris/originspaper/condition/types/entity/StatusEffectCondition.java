package io.github.dueris.originspaper.condition.types.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionTypeFactory;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class StatusEffectCondition {
	public static boolean condition(Entity entity, Holder<MobEffect> effect, int minAmplifier, int maxAmplifier, int minDuration, int maxDuration) {

		if (!(entity instanceof LivingEntity living)) {
			return false;
		}

		MobEffectInstance effectInstance = living.getEffect(effect);
		if (effectInstance == null) {
			return false;
		}

		int duration = effectInstance.getDuration();
		int amplifier = effectInstance.getAmplifier();

		return (duration <= maxDuration && duration >= minDuration)
			&& (amplifier <= maxAmplifier && amplifier >= minAmplifier);

	}

	public static ConditionTypeFactory<Entity> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("status_effect"),
			SerializableData.serializableData()
				.add("effect", SerializableDataTypes.STATUS_EFFECT_ENTRY)
				.add("min_amplifier", SerializableDataTypes.INT, 0)
				.add("max_amplifier", SerializableDataTypes.INT, Integer.MAX_VALUE)
				.add("min_duration", SerializableDataTypes.INT, -1)
				.add("max_duration", SerializableDataTypes.INT, Integer.MAX_VALUE),
			(data, entity) -> condition(entity,
				data.get("effect"),
				data.get("min_amplifier"),
				data.get("max_amplifier"),
				data.get("min_duration"),
				data.get("max_duration")
			)
		);
	}
}
