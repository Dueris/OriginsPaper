package io.github.dueris.originspaper.condition.type.bientity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;

import java.util.function.Predicate;

public class UndirectedConditionType {

	public static boolean condition(Entity actor, Entity target, Predicate<Tuple<Entity, Entity>> biEntityCondition) {
		return biEntityCondition.test(new Tuple<>(actor, target))
			|| biEntityCondition.test(new Tuple<>(target, actor));
	}

	public static ConditionTypeFactory<Tuple<Entity, Entity>> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("undirected"),
			new SerializableData()
				.add("condition", ApoliDataTypes.BIENTITY_CONDITION),
			(data, actorAndTarget) -> condition(actorAndTarget.getA(), actorAndTarget.getB(),
				data.get("condition")
			)
		);
	}

}
