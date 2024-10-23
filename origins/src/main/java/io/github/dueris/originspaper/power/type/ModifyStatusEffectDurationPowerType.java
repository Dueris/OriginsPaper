package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerTypeFactory;
import io.github.dueris.originspaper.util.modifier.Modifier;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ModifyStatusEffectDurationPowerType extends ValueModifyingPowerType {

	private final Set<Holder<MobEffect>> statusEffects;

	public ModifyStatusEffectDurationPowerType(Power power, LivingEntity entity, Holder<MobEffect> statusEffect, List<Holder<MobEffect>> statusEffects, Modifier modifier, List<Modifier> modifiers) {
		super(power, entity);
		this.statusEffects = new HashSet<>();

		if (statusEffect != null) {
			this.statusEffects.add(statusEffect);
		}

		if (statusEffects != null) {
			this.statusEffects.addAll(statusEffects);
		}

		if (modifier != null) {
			this.addModifier(modifier);
		}

		if (modifiers != null) {
			modifiers.forEach(this::addModifier);
		}

	}

	public static PowerTypeFactory<?> getFactory() {
		return new PowerTypeFactory<>(
			OriginsPaper.apoliIdentifier("modify_status_effect_duration"),
			new SerializableData()
				.add("status_effect", SerializableDataTypes.STATUS_EFFECT_ENTRY, null)
				.add("status_effects", SerializableDataTypes.STATUS_EFFECT_ENTRIES, null)
				.add("modifier", Modifier.DATA_TYPE, null)
				.add("modifiers", Modifier.LIST_TYPE, null),
			data -> (power, entity) -> new ModifyStatusEffectDurationPowerType(power, entity,
				data.get("status_effect"),
				data.get("status_effects"),
				data.get("modifier"),
				data.get("modifiers")
			)
		).allowCondition();
	}

	public boolean doesApply(Holder<MobEffect> statusEffect) {
		return statusEffects.isEmpty()
			|| statusEffects.contains(statusEffect);
	}

}
