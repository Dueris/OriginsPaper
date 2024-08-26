package io.github.dueris.originspaper.condition.types.bientity;

import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class TargetCondition {

	public static @NotNull ConditionTypeFactory<Tuple<Entity, Entity>> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("target_condition"),
			SerializableData.serializableData()
				.add("condition", ApoliDataTypes.ENTITY_CONDITION),
			(data, actorAndTarget) -> {
				Entity target = actorAndTarget.getB();
				return target != null && data.<Predicate<Entity>>get("condition").test(target);
			}
		);
	}
}
