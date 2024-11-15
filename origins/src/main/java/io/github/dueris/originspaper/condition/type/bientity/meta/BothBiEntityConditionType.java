package io.github.dueris.originspaper.condition.type.bientity.meta;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.condition.type.BiEntityConditionType;
import io.github.dueris.originspaper.condition.type.BiEntityConditionTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class BothBiEntityConditionType extends BiEntityConditionType {

	public static final TypedDataObjectFactory<BothBiEntityConditionType> DATA_FACTORY = TypedDataObjectFactory.simple(
		new SerializableData()
			.add("condition", EntityCondition.DATA_TYPE),
		data -> new BothBiEntityConditionType(
			data.get("condition")
		),
		(conditionType, serializableData) -> serializableData.instance()
			.set("condition", conditionType.entityCondition)
	);

	private final EntityCondition entityCondition;

	public BothBiEntityConditionType(EntityCondition entityCondition) {
		this.entityCondition = entityCondition;
	}

	@Override
	public @NotNull ConditionConfiguration<?> getConfig() {
		return BiEntityConditionTypes.BOTH;
	}

	@Override
	public boolean test(Entity actor, Entity target) {
		return entityCondition.test(actor)
			&& entityCondition.test(target);
	}

}
