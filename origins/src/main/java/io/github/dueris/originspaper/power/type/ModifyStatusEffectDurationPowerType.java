package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.power.PowerConfiguration;
import io.github.dueris.originspaper.util.Util;
import io.github.dueris.originspaper.util.modifier.Modifier;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ModifyStatusEffectDurationPowerType extends ValueModifyingPowerType {

	public static final TypedDataObjectFactory<ModifyStatusEffectDurationPowerType> DATA_FACTORY = createConditionedModifyingDataFactory(
		new SerializableData()
			.add("status_effect", SerializableDataTypes.STATUS_EFFECT_ENTRY, null)
			.addFunctionedDefault("status_effects", SerializableDataTypes.STATUS_EFFECT_ENTRIES, data -> Util.singletonListOrEmpty(data.get("status_effect"))),
		(data, modifiers, condition) -> new ModifyStatusEffectDurationPowerType(
			data.get("status_effects"),
			modifiers,
			condition
		),
		(powerType, serializableData) -> serializableData.instance()
			.set("status_effects", powerType.statusEffects)
	);

	private final List<Holder<MobEffect>> statusEffects;

	public ModifyStatusEffectDurationPowerType(List<Holder<MobEffect>> statusEffects, List<Modifier> modifiers, Optional<EntityCondition> condition) {
		super(modifiers, condition);
		this.statusEffects = statusEffects
			.stream()
			.distinct()
			.collect(Collectors.toCollection(ObjectArrayList::new));
	}

	@Override
	public @NotNull PowerConfiguration<?> getConfig() {
		return PowerTypes.MODIFY_STATUS_EFFECT_DURATION;
	}

	public boolean doesApply(Holder<MobEffect> statusEffect) {
		return statusEffects.isEmpty()
			|| statusEffects.contains(statusEffect);
	}

}
