package io.github.dueris.originspaper.condition.types.bientity;

import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionTypeFactory;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class EqualCondition {

	public static boolean condition(SerializableData.Instance data, @NotNull Tuple<Entity, Entity> actorAndTarget) {
		return Objects.equals(actorAndTarget.getA(), actorAndTarget.getB());
	}

	public static @NotNull ConditionTypeFactory<Tuple<Entity, Entity>> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("equal"),
			SerializableData.serializableData(),
			EqualCondition::condition
		);
	}
}
