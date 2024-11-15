package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.power.PowerConfiguration;
import io.github.dueris.originspaper.util.Util;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class EffectImmunityPowerType extends PowerType {

	public static final TypedDataObjectFactory<EffectImmunityPowerType> DATA_FACTORY = PowerType.createConditionedDataFactory(
		new SerializableData()
			.add("effect", SerializableDataTypes.STATUS_EFFECT_ENTRY, null)
			.addFunctionedDefault("effects", SerializableDataTypes.STATUS_EFFECT_ENTRIES, data -> Util.singletonListOrEmpty(data.get("effect")))
			.add("inverted", SerializableDataTypes.BOOLEAN, false),
		(data, condition) -> new EffectImmunityPowerType(
			data.get("effects"),
			data.get("inverted"),
			condition
		),
		(powerType, serializableData) -> serializableData.instance()
			.set("effects", powerType.effects)
			.set("inverted", powerType.inverted)
	);

	protected final List<Holder<MobEffect>> effects;
	protected final boolean inverted;

	public EffectImmunityPowerType(List<Holder<MobEffect>> effects, boolean inverted, Optional<EntityCondition> condition) {
		super(condition);
		this.effects = effects;
		this.inverted = inverted;
	}

	@Override
	public @NotNull PowerConfiguration<?> getConfig() {
		return PowerTypes.EFFECT_IMMUNITY;
	}

	public boolean doesApply(MobEffectInstance instance) {
		return doesApply(instance.getEffect());
	}

	public boolean doesApply(Holder<MobEffect> effect) {
		return inverted ^ effects.contains(effect);
	}

}
