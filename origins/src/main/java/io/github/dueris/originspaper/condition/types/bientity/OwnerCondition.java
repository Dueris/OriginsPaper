package io.github.dueris.originspaper.condition.types.bientity;

import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.parser.reader.DeserializedFactoryJson;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionFactory;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.TraceableEntity;
import org.jetbrains.annotations.NotNull;

public class OwnerCondition {

	public static boolean condition(DeserializedFactoryJson data, @NotNull Tuple<Entity, Entity> actorAndTarget) {

		Entity actor = actorAndTarget.getA();
		Entity target = actorAndTarget.getB();

		if (actor == null || target == null) {
			return false;
		}

		return (target instanceof OwnableEntity tamable && actor.equals(tamable.getOwner()))
			|| (target instanceof TraceableEntity ownable && actor.equals(ownable.getOwner()));

	}

	public static @NotNull ConditionFactory<Tuple<Entity, Entity>> getFactory() {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("owner"),
			InstanceDefiner.instanceDefiner(),
			OwnerCondition::condition
		);
	}
}
