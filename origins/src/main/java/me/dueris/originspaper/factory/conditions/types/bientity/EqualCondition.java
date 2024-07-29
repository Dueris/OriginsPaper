package me.dueris.originspaper.factory.conditions.types.bientity;

import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.parser.reader.DeserializedFactoryJson;
import io.github.dueris.calio.util.holder.Pair;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.conditions.ConditionFactory;
import net.minecraft.world.entity.Entity;

import java.util.Objects;

public class EqualCondition {

	public static boolean condition(DeserializedFactoryJson data, Pair<Entity, Entity> actorAndTarget) {
		return Objects.equals(actorAndTarget.getA(), actorAndTarget.getB());
	}

	public static ConditionFactory<Pair<Entity, Entity>> getFactory() {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("equal"),
			InstanceDefiner.instanceDefiner(),
			EqualCondition::condition
		);
	}
}
