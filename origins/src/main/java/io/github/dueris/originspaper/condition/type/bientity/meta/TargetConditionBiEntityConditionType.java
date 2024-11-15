package io.github.dueris.originspaper.condition.type.bientity.meta;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.condition.type.BiEntityConditionType;
import io.github.dueris.originspaper.condition.type.BiEntityConditionTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class TargetConditionBiEntityConditionType extends BiEntityConditionType {

	public static final TypedDataObjectFactory<TargetConditionBiEntityConditionType> DATA_FACTORY = TypedDataObjectFactory.simple(
		new SerializableData()
			.add("condition", EntityCondition.DATA_TYPE),
		data -> new TargetConditionBiEntityConditionType(
			data.get("condition")
		),
		(conditionType, serializableData) -> serializableData.instance()
			.set("condition", conditionType.targetCondition)
	);

	private final EntityCondition targetCondition;

	public TargetConditionBiEntityConditionType(EntityCondition targetCondition) {
		this.targetCondition = targetCondition;
	}

	@Override
	public @NotNull ConditionConfiguration<?> getConfig() {
		return BiEntityConditionTypes.TARGET_CONDITION;
	}

	@Override
	public boolean test(Entity actor, Entity target) {
		return targetCondition.test(target);
	}

}
