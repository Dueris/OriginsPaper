package io.github.dueris.originspaper.condition.type.bientity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;

import java.util.function.Predicate;

public class ActorConditionType {

	public static boolean condition(Entity actor, Predicate<Entity> condition) {
		return condition.test(actor);
	}

	public static ConditionTypeFactory<Tuple<Entity, Entity>> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("actor_condition"),
			new SerializableData()
				.add("condition", ApoliDataTypes.ENTITY_CONDITION),
			(data, actorAndTarget) -> condition(actorAndTarget.getA(),
				data.get("condition")
			)
		);
	}

}
