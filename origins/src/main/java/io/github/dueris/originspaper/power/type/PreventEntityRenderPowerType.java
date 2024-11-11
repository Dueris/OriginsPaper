package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.BiEntityCondition;
import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerConfiguration;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Predicate;

public class PreventEntityRenderPowerType extends PowerType {

	public static final TypedDataObjectFactory<PreventEntityRenderPowerType> DATA_FACTORY = PowerType.createConditionedDataFactory(
		new SerializableData()
			.add("entity_condition", EntityCondition.DATA_TYPE.optional(), Optional.empty())
			.add("bientity_condition", BiEntityCondition.DATA_TYPE.optional(), Optional.empty()),
		(data, condition) -> new PreventEntityRenderPowerType(
			data.get("entity_condition"),
			data.get("bientity_condition"),
			condition
		),
		(powerType, serializableData) -> serializableData.instance()
			.set("entity_condition", powerType.entityCondition)
			.set("bientity_condition", powerType.biEntityCondition)
	);

	private final Optional<EntityCondition> entityCondition;
	private final Optional<BiEntityCondition> biEntityCondition;

	public PreventEntityRenderPowerType(Optional<EntityCondition> entityCondition, Optional<BiEntityCondition> biEntityCondition, Optional<EntityCondition> condition) {
		super(condition);
		this.entityCondition = entityCondition;
		this.biEntityCondition = biEntityCondition;
	}

	@Override
	public @NotNull PowerConfiguration<?> getConfig() {
		return PowerTypes.PREVENT_ENTITY_RENDER;
	}

	public boolean doesApply(Entity entity) {
		return entityCondition.map(condition -> condition.test(entity)).orElse(true)
			&& biEntityCondition.map(condition -> condition.test(getHolder(), entity)).orElse(true);
	}

}
