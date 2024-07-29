package me.dueris.originspaper.factory.conditions.types.bientity;

import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.util.holder.Pair;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.conditions.ConditionFactory;
import me.dueris.originspaper.factory.data.ApoliDataTypes;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class ActorCondition {

	@Contract(" -> new")
	public static @NotNull ConditionFactory<Pair<Entity, Entity>> getFactory() {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("actor_condition"),
			InstanceDefiner.instanceDefiner()
				.add("condition", ApoliDataTypes.ENTITY_CONDITION),
			(data, actorAndTarget) -> {
				Entity actor = actorAndTarget.first();
				return actor != null && data.<Predicate<Entity>>get("condition").test(actor);
			}
		);
	}
}
