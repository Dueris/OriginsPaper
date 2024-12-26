package io.github.dueris.originspaper.condition.type.bientity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.BiEntityConditionType;
import io.github.dueris.originspaper.condition.type.BiEntityConditionTypes;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.power.PowerReference;
import io.github.dueris.originspaper.power.type.EntitySetPowerType;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class InEntitySetBiEntityConditionType extends BiEntityConditionType {

	public static final TypedDataObjectFactory<InEntitySetBiEntityConditionType> DATA_FACTORY = TypedDataObjectFactory.simple(
		new SerializableData()
			.add("set", ApoliDataTypes.POWER_REFERENCE),
		data -> new InEntitySetBiEntityConditionType(
			data.get("set")
		),
		(conditionType, serializableData) -> serializableData.instance()
			.set("set", conditionType.set)
	);

	private final PowerReference set;

	public InEntitySetBiEntityConditionType(PowerReference set) {
		this.set = set;
	}

	@Override
	public @NotNull ConditionConfiguration<?> getConfig() {
		return BiEntityConditionTypes.IN_ENTITY_SET;
	}

	@Override
	public boolean test(Entity actor, Entity target) {
		return set.getNullablePowerType(actor) instanceof EntitySetPowerType entitySet
			&& entitySet.contains(target);
	}

}
