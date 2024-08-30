package io.github.dueris.originspaper.condition.type.bientity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class TargetConditionType {

	public static boolean condition(Entity target, @NotNull Predicate<Entity> condition) {
		return condition.test(target);
	}

	public static @NotNull ConditionTypeFactory<Tuple<Entity, Entity>> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("target_condition"),
			new SerializableData()
				.add("condition", ApoliDataTypes.ENTITY_CONDITION),
			(data, actorAndTarget) -> condition(actorAndTarget.getB(),
				data.get("condition")
			)
		);
	}

}
