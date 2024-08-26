package io.github.dueris.originspaper.condition.types.bientity;

import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class InvertCondition {

	public static @NotNull ConditionTypeFactory<Tuple<Entity, Entity>> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("invert"),
			SerializableData.serializableData()
				.add("condition", ApoliDataTypes.BIENTITY_CONDITION),
			(data, actorAndTarget) -> {
				return data.<Predicate<Tuple<Entity, Entity>>>get("condition").test(new Tuple<>(actorAndTarget.getB(), actorAndTarget.getA()));
			}
		);
	}
}
