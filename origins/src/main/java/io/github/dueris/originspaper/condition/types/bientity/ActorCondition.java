package io.github.dueris.originspaper.condition.types.bientity;

import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class ActorCondition {

	public static @NotNull ConditionFactory<Tuple<Entity, Entity>> getFactory() {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("actor_condition"),
			InstanceDefiner.instanceDefiner()
				.add("condition", ApoliDataTypes.ENTITY_CONDITION),
			(data, actorAndTarget) -> {
				Entity actor = actorAndTarget.getA();
				return actor != null && data.<Predicate<Entity>>get("condition").test(actor);
			}
		);
	}
}
