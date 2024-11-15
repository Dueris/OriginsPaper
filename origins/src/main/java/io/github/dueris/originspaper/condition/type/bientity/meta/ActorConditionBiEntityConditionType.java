package io.github.dueris.originspaper.condition.type.bientity.meta;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.condition.type.BiEntityConditionType;
import io.github.dueris.originspaper.condition.type.BiEntityConditionTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class ActorConditionBiEntityConditionType extends BiEntityConditionType {

	public static final TypedDataObjectFactory<ActorConditionBiEntityConditionType> DATA_FACTORY = TypedDataObjectFactory.simple(
		new SerializableData()
			.add("condition", EntityCondition.DATA_TYPE),
		data -> new ActorConditionBiEntityConditionType(
			data.get("condition")
		),
		(conditionType, serializableData) -> serializableData.instance()
			.set("condition", conditionType.actorCondition)
	);

	private final EntityCondition actorCondition;

	public ActorConditionBiEntityConditionType(EntityCondition actorCondition) {
		this.actorCondition = actorCondition;
	}

	@Override
	public @NotNull ConditionConfiguration<?> getConfig() {
		return BiEntityConditionTypes.ACTOR_CONDITION;
	}

	@Override
	public boolean test(Entity actor, Entity target) {
		return actorCondition.test(actor);
	}

}
