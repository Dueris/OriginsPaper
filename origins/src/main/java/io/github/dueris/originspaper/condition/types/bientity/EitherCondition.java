package io.github.dueris.originspaper.condition.types.bientity;

import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class EitherCondition {

	public static @NotNull ConditionFactory<Tuple<Entity, Entity>> getFactory() {
		return new ConditionFactory<>(
				OriginsPaper.apoliIdentifier("either"),
				SerializableData.serializableData()
						.add("condition", ApoliDataTypes.ENTITY_CONDITION),
				(data, actorAndTarget) -> {
					Entity actor = actorAndTarget.getA();
					Entity target = actorAndTarget.getB();

					Predicate<Entity> entityCondition = data.get("condition");
					return (actor != null && entityCondition.test(actor))
							|| (target != null && entityCondition.test(target));
				}
		);
	}
}
