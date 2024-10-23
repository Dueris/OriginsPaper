package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerTypeFactory;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EffectImmunityPowerType extends PowerType {

	protected final Set<Holder<MobEffect>> effects = new HashSet<>();
	private final boolean inverted;

	public EffectImmunityPowerType(Power power, LivingEntity entity, boolean inverted) {
		super(power, entity);
		this.inverted = inverted;
	}

	public static PowerTypeFactory<?> getFactory() {
		return new PowerTypeFactory<>(OriginsPaper.apoliIdentifier("effect_immunity"),
			new SerializableData()
				.add("effect", SerializableDataTypes.STATUS_EFFECT_ENTRY, null)
				.add("effects", SerializableDataTypes.STATUS_EFFECT_ENTRIES, null)
				.add("inverted", SerializableDataTypes.BOOLEAN, false),
			data -> (power, entity) -> {

				EffectImmunityPowerType powerType = new EffectImmunityPowerType(power, entity, data.get("inverted"));

				data.ifPresent("effect", powerType::addEffect);
				data.<List<Holder<MobEffect>>>ifPresent("effects", effects -> effects.forEach(powerType::addEffect));

				return powerType;

			}
		).allowCondition();
	}

	public EffectImmunityPowerType addEffect(Holder<MobEffect> effect) {
		effects.add(effect);
		return this;
	}

	public boolean doesApply(@NotNull MobEffectInstance instance) {
		return doesApply(instance.getEffect());
	}

	public boolean doesApply(Holder<MobEffect> effect) {
		return inverted ^ effects.contains(effect);
	}
}
